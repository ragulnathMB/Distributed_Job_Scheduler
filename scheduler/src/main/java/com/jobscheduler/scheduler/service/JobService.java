package com.jobscheduler.scheduler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobscheduler.common.ExecutionResult;
import com.jobscheduler.scheduler.config.SchedulerProperties;
import com.jobscheduler.scheduler.model.Job;
import com.jobscheduler.scheduler.model.JobExecution;
import com.jobscheduler.scheduler.model.JobSpec;
import com.jobscheduler.scheduler.model.JobStatus;
import com.jobscheduler.scheduler.model.ScheduleType;
import com.jobscheduler.scheduler.repository.JobExecutionRepository;
import com.jobscheduler.scheduler.repository.JobRepository;
import com.jobscheduler.scheduler.service.Exceptions.ConflictException;
import com.jobscheduler.scheduler.service.Exceptions.NotFoundException;
import com.jobscheduler.scheduler.web.JobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class JobService {
    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobs;
    private final JobExecutionRepository executions;
    private final SchedulerProperties props;

    public JobService(JobRepository jobs, JobExecutionRepository executions, SchedulerProperties props) {
        this.jobs = jobs;
        this.executions = executions;
        this.props = props;
    }

    // ------------------------------------------------------------------ CRUD

    @Transactional
    public Job create(JobRequest request) {
        UUID id = UUID.randomUUID();
        jobs.insert(id, toSpec(request));
        log.info("Created job {} ({}, {})", id, request.name(), request.scheduleType());
        return get(id);
    }

    @Transactional
    public Job update(UUID id, JobRequest request) {
        Job existing = get(id);
        if (!jobs.update(id, toSpec(request))) {
            throw new ConflictException("Job is " + existing.status() + "; only PENDING or PAUSED jobs can be edited");
        }
        return get(id);
    }

    public Job get(UUID id) {
        return jobs.findById(id).orElseThrow(() -> new NotFoundException("Job not found: " + id));
    }

    public List<Job> list(JobStatus status, int limit, int offset) {
        return jobs.list(status, Math.min(Math.max(limit, 1), 500), Math.max(offset, 0));
    }

    @Transactional
    public void delete(UUID id) {
        Job job = get(id);
        if (!jobs.delete(id)) {
            throw new ConflictException("Job is " + job.status() + "; cancel it first / wait for it to finish");
        }
    }

    public List<JobExecution> executions(UUID id) {
        get(id);
        return executions.findByJob(id);
    }

    // ------------------------------------------------------ lifecycle control

    @Transactional
    public Job cancel(UUID id) {
        Job job = get(id);
        if (!jobs.transition(id, JobStatus.CANCELLED, JobStatus.PENDING, JobStatus.PAUSED, JobStatus.RUNNING)) {
            throw new ConflictException("Job is " + job.status() + " and cannot be cancelled");
        }
        return get(id);
    }

    @Transactional
    public Job pause(UUID id) {
        Job job = get(id);
        if (!jobs.transition(id, JobStatus.PAUSED, JobStatus.PENDING)) {
            throw new ConflictException("Job is " + job.status() + "; only PENDING jobs can be paused");
        }
        return get(id);
    }

    @Transactional
    public Job resume(UUID id) {
        Job job = get(id);
        Instant now = Instant.now();
        Instant next = job.nextRunAt();
        if (job.scheduleType() == ScheduleType.CRON) {
            next = ScheduleCalculator.nextCron(job.cronExpression(), now);   // skip missed fires
        } else if (next == null || next.isBefore(now)) {
            next = now;
        }
        if (job.scheduleType() == ScheduleType.CRON && next == null) {
            next = now;
        }
        if (!jobs.resume(id, next)) {
            throw new ConflictException("Job is " + job.status() + "; only PAUSED jobs can be resumed");
        }
        return get(id);
    }

    // ------------------------------------------------ result of one attempt

    /**
     * Called (on any scheduler node) when a worker reports the outcome of an attempt. Decides what happens next:
     * complete, reschedule (recurring) or retry with exponential backoff / move to terminal FAILED.
     */
    @Transactional
    public void completeExecution(long executionId, ExecutionResult result) {
        JobExecution ex = executions.findById(executionId)
                .orElseThrow(() -> new NotFoundException("Execution not found: " + executionId));

        boolean accepted = executions.finish(executionId, result.success() ? "SUCCESS" : "FAILED",
                result.output(), result.error());
        if (!accepted) {
            log.warn("Ignoring late result for execution {} (already {})", executionId, ex.status());
            return;
        }

        Job job = jobs.findById(ex.jobId()).orElse(null);
        if (job == null || job.status() != JobStatus.RUNNING || !Objects.equals(job.currentExecutionId(), executionId)) {
            log.info("Result for execution {} recorded, but job {} is no longer awaiting it", executionId, ex.jobId());
            return;
        }

        Instant now = Instant.now();
        if (result.success()) {
            switch (job.scheduleType()) {
                case FIXED_INTERVAL -> jobs.finishRun(job.id(), executionId, JobStatus.PENDING,
                        ScheduleCalculator.nextFixedInterval(job.nextRunAt(), job.intervalSeconds(), now), 0, null);
                case CRON -> {
                    Instant next = ScheduleCalculator.nextCron(job.cronExpression(), now);
                    if (next == null) {
                        jobs.finishRun(job.id(), executionId, JobStatus.COMPLETED, job.nextRunAt(), 0, null);
                    } else {
                        jobs.finishRun(job.id(), executionId, JobStatus.PENDING, next, 0, null);
                    }
                }
                default -> jobs.finishRun(job.id(), executionId, JobStatus.COMPLETED, job.nextRunAt(), 0, null);
            }
            log.info("Job {} attempt {} succeeded on {}", job.id(), ex.attempt(), result.workerId());
            return;
        }

        int retries = job.retryCount() + 1;
        if (retries <= job.maxRetries()) {
            long delay = ScheduleCalculator.backoffSeconds(job.backoffSeconds(), retries, props.getMaxBackoffSeconds());
            jobs.finishRun(job.id(), executionId, JobStatus.PENDING, now.plusSeconds(delay), retries, result.error());
            log.warn("Job {} failed (retry {}/{}), next attempt in {}s: {}",
                    job.id(), retries, job.maxRetries(), delay, result.error());
        } else {
            jobs.finishRun(job.id(), executionId, JobStatus.FAILED, job.nextRunAt(), retries, result.error());
            log.error("Job {} exhausted {} retries -> FAILED (dead letter): {}", job.id(), job.maxRetries(), result.error());
        }
    }

    // ---------------------------------------------------------------- helpers

    private JobSpec toSpec(JobRequest r) {
        Instant now = Instant.now();
        Instant next;
        String cron = null;
        Long interval = null;

        switch (r.scheduleType()) {
            case ONE_TIME -> next = r.runAt() != null ? r.runAt() : now;
            case DELAYED -> {
                if (r.delaySeconds() == null || r.delaySeconds() <= 0) {
                    throw new IllegalArgumentException("DELAYED jobs require delaySeconds > 0");
                }
                next = now.plusSeconds(r.delaySeconds());
            }
            case FIXED_INTERVAL -> {
                if (r.intervalSeconds() == null || r.intervalSeconds() <= 0) {
                    throw new IllegalArgumentException("FIXED_INTERVAL jobs require intervalSeconds > 0");
                }
                interval = r.intervalSeconds();
                next = now.plusSeconds(r.delaySeconds() == null ? 0 : Math.max(r.delaySeconds(), 0));
            }
            case CRON -> {
                if (r.cronExpression() == null || r.cronExpression().isBlank()) {
                    throw new IllegalArgumentException("CRON jobs require cronExpression");
                }
                ScheduleCalculator.validateCron(r.cronExpression());
                cron = r.cronExpression().trim();
                next = ScheduleCalculator.nextCron(cron, now);
                if (next == null) {
                    throw new IllegalArgumentException("Cron expression never fires");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported schedule type");
        }

        return new JobSpec(
                r.name().trim(),
                r.handlerType().trim().toUpperCase(),
                payloadToString(r.payload()),
                r.scheduleType(),
                cron,
                interval,
                r.priority() == null ? 5 : r.priority(),
                next,
                r.maxRetries() == null ? 3 : r.maxRetries(),
                r.backoffSeconds() == null ? 5 : r.backoffSeconds(),
                r.timeoutSeconds() == null ? 60 : r.timeoutSeconds());
    }

    private static String payloadToString(JsonNode payload) {
        if (payload == null || payload.isNull()) {
            return null;
        }
        return payload.isTextual() ? payload.asText() : payload.toString();
    }

    /** Exposed for the cluster endpoint. */
    public Map<String, Long> stats() {
        return jobs.countByStatus();
    }
}
