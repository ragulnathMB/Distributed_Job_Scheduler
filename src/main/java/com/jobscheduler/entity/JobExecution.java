package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * An immutable-ish audit trail row: one JobExecution per attempt of a Job.
 * This is what "Execution History" / "Job Logs" in the requirements refers to.
 *
 * Why separate from Job?
 * - Job holds *current* scheduling state (mutable, small).
 * - JobExecution holds *historical* attempt data (append-only, grows unbounded,
 *   so it's a natural candidate for partitioning/archival later — Phase 8/9).
 */
@Entity
@Table(name = "job_executions", indexes = {
        @Index(name = "idx_executions_job_id", columnList = "job_id"),
        @Index(name = "idx_executions_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "worker_id")
    private String workerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "leased_at")
    private Instant leasedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    /** Wall-clock duration of this attempt, filled in when it terminates. */
    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
