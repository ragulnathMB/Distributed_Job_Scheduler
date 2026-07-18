package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a schedulable unit of work.
 *
 * Design notes:
 * - `nextRunAt` is the single most important column for the scheduler:
 *   JobPollingScheduler queries "WHERE nextRunAt <= now AND status = PENDING"
 *   to find due jobs. It is indexed (see V1 migration).
 * - `version` enables optimistic locking so two scheduler nodes racing to
 *   claim the same job fail gracefully instead of double-dispatching it.
 * - `lockedBy` / `lockedUntil` implement a lease: once a node claims a job,
 *   other nodes must not touch it until the lease expires.
 */
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_jobs_next_run_status", columnList = "next_run_at,status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    /** Fully-qualified task identifier the worker uses to look up executable logic. */
    @Column(name = "task_handler", nullable = false)
    private String taskHandler;

    /** Arbitrary JSON payload passed to the task handler at execution time. */
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "fixed_interval_seconds")
    private Long fixedIntervalSeconds;

    /** Used for ONE_TIME and DELAYED jobs. */
    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    /** Higher number = higher priority. Used by PriorityJobQueue ordering. */
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 5;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "timeout_seconds", nullable = false)
    @Builder.Default
    private Integer timeoutSeconds = 60;

    /** Node ID of the scheduler that currently holds the lease on this job. */
    @Column(name = "locked_by")
    private String lockedBy;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Optimistic lock — prevents two scheduler nodes from double-claiming this row. */
    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = JobStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
