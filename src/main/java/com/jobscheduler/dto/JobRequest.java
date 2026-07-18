package com.jobscheduler.dto;

import com.jobscheduler.entity.JobType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Inbound payload for POST /api/jobs.
 * Kept deliberately flat rather than mirroring the Job entity 1:1 — clients
 * shouldn't need to know about internal fields like lockedBy/version/status.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "jobType is required")
    private JobType jobType;

    @NotBlank(message = "taskHandler is required")
    private String taskHandler;

    private String payload;

    /** Required if jobType == CRON */
    private String cronExpression;

    /** Required if jobType == RECURRING */
    private Long fixedIntervalSeconds;

    /** Required if jobType == ONE_TIME or DELAYED */
    private Instant scheduledAt;

    /** Only relevant for DELAYED: seconds from now until first execution */
    private Long delaySeconds;

    @Builder.Default
    private Integer priority = 0;

    @Min(value = 0, message = "maxRetries cannot be negative")
    @Builder.Default
    private Integer maxRetries = 5;

    @Min(value = 1, message = "timeoutSeconds must be at least 1")
    @Builder.Default
    private Integer timeoutSeconds = 60;
}
