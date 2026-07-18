package com.jobscheduler.dispatcher;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.service.RetryService;
import com.jobscheduler.worker.WorkerRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Bridges "a job was claimed" to "a job is actually running", and records
 * the full execution lifecycle into JobExecution rows.
 *
 * Runs on the dispatcherExecutor pool (@Async("dispatcherExecutor")) so that
 * a burst of claimed jobs doesn't block the polling thread itself — the
 * claim already happened in JobPollingScheduler/SchedulerService; this class
 * only handles running + recording the result.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobDispatcher {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final TaskExecutor taskExecutor;
    private final RetryService retryService;
    private final WorkerRegistrationService workerRegistrationService;

    @Async("dispatcherExecutor")
    public void dispatch(Job job) {
        String workerId = workerRegistrationService.selectAvailableWorker()
                .orElse("in-process-worker");

        int attemptNumber = job.getRetryCount() + 1;
        Instant leasedAt = Instant.now();

        JobExecution execution = JobExecution.builder()
                .jobId(job.getId())
                .workerId(workerId)
                .status(ExecutionStatus.RUNNING)
                .attemptNumber(attemptNumber)
                .leasedAt(leasedAt)
                .startedAt(Instant.now())
                .build();
        execution = executionRepository.save(execution);
        final Long executionId = execution.getId();

        Callable<String> taskLogic = () -> {
            // Placeholder for real task handler resolution/execution.
            // In a production system this would look up job.getTaskHandler()
            // in a registry (Spring bean map, plugin loader, RPC client, etc).
            log.info("Executing job {} ({}) on {}", job.getId(), job.getTaskHandler(), workerId);
            Thread.sleep(100); // simulate work
            return "OK";
        };

        taskExecutor.execute(job, taskLogic).thenAccept(result -> {
            Instant completedAt = Instant.now();
            long durationMs = completedAt.toEpochMilli() - leasedAt.toEpochMilli();

            JobExecution finished = executionRepository.findById(executionId).orElseThrow();
            finished.setCompletedAt(completedAt);
            finished.setDurationMs(durationMs);

            if (result.success()) {
                finished.setStatus(ExecutionStatus.SUCCEEDED);
                finished.setOutput(result.output());
                executionRepository.save(finished);
                retryService.handleSuccess(job);
            } else {
                boolean timedOut = "Execution timed out".equals(result.errorMessage());
                finished.setStatus(timedOut ? ExecutionStatus.TIMED_OUT : ExecutionStatus.FAILED);
                finished.setErrorMessage(result.errorMessage());
                finished.setStackTrace(result.stackTrace());
                executionRepository.save(finished);
                retryService.handleFailure(job, result.errorMessage());
            }
        });
    }
}
