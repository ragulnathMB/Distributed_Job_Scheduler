package com.jobscheduler.scheduler.model;

import java.time.Instant;

/** Validated, defaulted definition of a job used for inserts and updates. */
public record JobSpec(
        String name,
        String handlerType,
        String payload,
        ScheduleType scheduleType,
        String cronExpression,
        Long intervalSeconds,
        int priority,
        Instant nextRunAt,
        int maxRetries,
        int backoffSeconds,
        int timeoutSeconds) {
}
