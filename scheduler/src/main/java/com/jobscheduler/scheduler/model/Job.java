package com.jobscheduler.scheduler.model;

import java.time.Instant;
import java.util.UUID;

public record Job(
        UUID id,
        String name,
        String handlerType,
        String payload,
        ScheduleType scheduleType,
        String cronExpression,
        Long intervalSeconds,
        int priority,
        JobStatus status,
        Instant nextRunAt,
        int maxRetries,
        int retryCount,
        int backoffSeconds,
        int timeoutSeconds,
        long runCount,
        String claimedBy,
        String assignedWorker,
        Long currentExecutionId,
        Instant leaseExpiresAt,
        Instant lastRunAt,
        String lastError,
        Instant createdAt,
        Instant updatedAt) {
}
