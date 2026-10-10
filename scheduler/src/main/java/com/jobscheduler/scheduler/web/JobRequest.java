package com.jobscheduler.scheduler.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobscheduler.scheduler.model.ScheduleType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Body for creating / updating a job.
 * <ul>
 *   <li>ONE_TIME: runs at {@code runAt} (or immediately).</li>
 *   <li>DELAYED: runs after {@code delaySeconds}.</li>
 *   <li>FIXED_INTERVAL: runs every {@code intervalSeconds} (first run after optional {@code delaySeconds}).</li>
 *   <li>CRON: runs per {@code cronExpression} (6 fields, UTC).</li>
 * </ul>
 * priority: 1 (lowest) .. 10 (highest), default 5.
 */
public record JobRequest(
        @NotBlank String name,
        @NotBlank String handlerType,
        JsonNode payload,
        @NotNull ScheduleType scheduleType,
        Long delaySeconds,
        Instant runAt,
        Long intervalSeconds,
        String cronExpression,
        @Min(1) @Max(10) Integer priority,
        @Min(0) @Max(20) Integer maxRetries,
        @Min(1) Integer backoffSeconds,
        @Min(1) Integer timeoutSeconds) {
}
