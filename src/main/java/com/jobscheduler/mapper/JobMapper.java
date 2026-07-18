package com.jobscheduler.mapper;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.exception.InvalidCronExpressionException;
import com.jobscheduler.util.CronUtil;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Converts between entities and DTOs, and centralizes the "compute initial
 * nextRunAt" logic for each JobType — kept here (not in the entity) to keep
 * Job a plain persistence model without scheduling-decision logic baked in.
 */
@Component
public class JobMapper {

    public Job toEntity(JobRequest request) {
        Instant now = Instant.now();
        Instant nextRunAt = computeInitialNextRunAt(request, now);

        return Job.builder()
                .name(request.getName())
                .jobType(request.getJobType())
                .taskHandler(request.getTaskHandler())
                .payload(request.getPayload())
                .cronExpression(request.getCronExpression())
                .fixedIntervalSeconds(request.getFixedIntervalSeconds())
                .scheduledAt(request.getScheduledAt())
                .nextRunAt(nextRunAt)
                .status(JobStatus.PENDING)
                .priority(request.getPriority())
                .maxRetries(request.getMaxRetries())
                .retryCount(0)
                .timeoutSeconds(request.getTimeoutSeconds())
                .build();
    }

    private Instant computeInitialNextRunAt(JobRequest request, Instant now) {
        return switch (request.getJobType()) {
            case ONE_TIME -> request.getScheduledAt() != null ? request.getScheduledAt() : now;
            case DELAYED -> {
                long delay = request.getDelaySeconds() != null ? request.getDelaySeconds() : 0;
                yield now.plusSeconds(delay);
            }
            case RECURRING -> {
                if (request.getFixedIntervalSeconds() == null) {
                    throw new IllegalArgumentException("fixedIntervalSeconds is required for RECURRING jobs");
                }
                yield now.plusSeconds(request.getFixedIntervalSeconds());
            }
            case CRON -> {
                if (request.getCronExpression() == null || request.getCronExpression().isBlank()) {
                    throw new InvalidCronExpressionException("cronExpression is required for CRON jobs");
                }
                CronUtil.validate(request.getCronExpression());
                yield CronUtil.nextExecutionTime(request.getCronExpression(), now);
            }
        };
    }

    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .jobType(job.getJobType())
                .taskHandler(job.getTaskHandler())
                .payload(job.getPayload())
                .cronExpression(job.getCronExpression())
                .fixedIntervalSeconds(job.getFixedIntervalSeconds())
                .scheduledAt(job.getScheduledAt())
                .nextRunAt(job.getNextRunAt())
                .status(job.getStatus())
                .priority(job.getPriority())
                .maxRetries(job.getMaxRetries())
                .retryCount(job.getRetryCount())
                .timeoutSeconds(job.getTimeoutSeconds())
                .lockedBy(job.getLockedBy())
                .lockedUntil(job.getLockedUntil())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public JobExecutionResponse toExecutionResponse(JobExecution execution) {
        return JobExecutionResponse.builder()
                .id(execution.getId())
                .jobId(execution.getJobId())
                .workerId(execution.getWorkerId())
                .status(execution.getStatus())
                .attemptNumber(execution.getAttemptNumber())
                .leasedAt(execution.getLeasedAt())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .durationMs(execution.getDurationMs())
                .errorMessage(execution.getErrorMessage())
                .output(execution.getOutput())
                .createdAt(execution.getCreatedAt())
                .build();
    }
}
