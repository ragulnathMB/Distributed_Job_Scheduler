package com.jobscheduler.dto;

import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Outbound representation of a Job. Deliberately excludes internal
 * concurrency-control fields (version, lockedBy) from the public API surface,
 * but does expose lockedBy for admin/debug visibility on the dashboard.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String name;
    private JobType jobType;
    private String taskHandler;
    private String payload;
    private String cronExpression;
    private Long fixedIntervalSeconds;
    private Instant scheduledAt;
    private Instant nextRunAt;
    private JobStatus status;
    private Integer priority;
    private Integer maxRetries;
    private Integer retryCount;
    private Integer timeoutSeconds;
    private String lockedBy;
    private Instant lockedUntil;
    private Instant createdAt;
    private Instant updatedAt;
}
