package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a worker node registered with the cluster.
 *
 * `lastHeartbeatAt` is the crux of failure detection: HeartbeatService updates
 * it periodically, and a background check (WorkerRegistrationService) marks
 * workers DEAD if lastHeartbeatAt is older than heartbeat-timeout-seconds.
 *
 * `isLeader` supports the simple lease-based leader election described in
 * Phase 6: exactly one worker/scheduler row should have isLeader=true at a time,
 * enforced via a conditional UPDATE (see SchedulerService).
 */
@Entity
@Table(name = "workers", indexes = {
        @Index(name = "idx_workers_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @Column(name = "worker_id")
    private String workerId;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkerStatus status;

    @Column(name = "max_concurrent_tasks", nullable = false)
    @Builder.Default
    private Integer maxConcurrentTasks = 10;

    @Column(name = "current_task_count", nullable = false)
    @Builder.Default
    private Integer currentTaskCount = 0;

    @Column(name = "is_leader", nullable = false)
    @Builder.Default
    private Boolean isLeader = false;

    @Column(name = "leader_lease_until")
    private Instant leaderLeaseUntil;

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @PrePersist
    void onCreate() {
        this.registeredAt = Instant.now();
        this.lastHeartbeatAt = Instant.now();
    }

    public enum WorkerStatus {
        ACTIVE,
        INACTIVE,
        DEAD
    }
}
