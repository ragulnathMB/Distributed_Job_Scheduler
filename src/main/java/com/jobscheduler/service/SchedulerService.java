package com.jobscheduler.service;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.dispatcher.JobDispatcher;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.repository.WorkerRepository;
import com.jobscheduler.scheduler.PriorityJobQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Owns three closely related distributed-systems responsibilities:
 *
 *  1. Leader election — only the elected node polls/dispatches (see
 *     JobPollingScheduler), preventing every node in the cluster from
 *     double-dispatching the same due jobs.
 *  2. Claiming due jobs (optimistic-lease acquisition) and draining the
 *     in-memory PriorityJobQueue to the dispatcher.
 *  3. Crash recovery — reclaiming jobs whose lease expired without
 *     completion (e.g. the node that claimed them crashed mid-execution).
 *
 * Leader election design (lease-based, DB-backed):
 * We don't use ZooKeeper/etcd/Consul here — that would be the production
 * choice, but implementing leader election via a conditional UPDATE against
 * the same PostgreSQL instance we already depend on teaches the *concept*
 * (lease + fencing via expiry) without adding a new piece of infrastructure.
 * Trade-off: our "lease" is not linearizable across network partitions the
 * way a proper consensus system (Raft/Paxos-based, e.g. etcd) would
 * guarantee — worth calling out explicitly as a limitation in interviews.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final WorkerRepository workerRepository;
    private final JobDispatcher jobDispatcher;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Value("${scheduler.lease-duration-seconds}")
    private long leaseDurationSeconds;

    private static final long LEADER_LEASE_SECONDS = 15;

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isLeader() {
        return workerRepository.findByIsLeaderTrue()
                .map(w -> w.getWorkerId().equals(nodeId))
                .orElse(false);
    }

    /**
     * Attempts to acquire (or renew) leadership via a lease. Called
     * frequently so a live leader keeps renewing before its lease expires;
     * if the leader crashes, its lease naturally expires and another node's
     * tryAcquireLeadership UPDATE will succeed (self-healing, no manual
     * failover step needed).
     */
    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void participateInLeaderElection() {
        Instant now = Instant.now();
        Instant leaseUntil = now.plusSeconds(LEADER_LEASE_SECONDS);
        int acquired = workerRepository.tryAcquireLeadership(nodeId, now, leaseUntil);
        if (acquired > 0) {
            log.debug("Node {} holds/renewed leadership until {}", nodeId, leaseUntil);
        }
    }

    /**
     * Attempts to claim each job in the queue using optimistic-lease UPDATE.
     * If claimJob() affects 0 rows, another node beat us to it — that job is
     * simply dropped from our local queue (it's still being processed
     * elsewhere; no data loss).
     */
    public void drainQueueAndDispatch(PriorityJobQueue queue) {
        Instant now = Instant.now();
        Instant leaseUntil = now.plusSeconds(leaseDurationSeconds);

        Job job;
        while ((job = queue.poll()) != null) {
            int claimed = jobRepository.claimJob(job.getId(), nodeId, now, leaseUntil);
            if (claimed == 0) {
                log.debug("Job {} already claimed by another node — skipping", job.getId());
                continue;
            }
            log.info("Node {} claimed job {} (priority={})", nodeId, job.getId(), job.getPriority());
            jobDispatcher.dispatch(job);
        }
    }

    /**
     * Crash recovery (Phase 5): jobs stuck in RUNNING whose lease has
     * expired are assumed abandoned (the node holding them likely crashed
     * or hung). We reset them to PENDING so the next poll cycle picks them
     * up again — at-least-once semantics, not exactly-once (see
     * RetryService/idempotency discussion for how duplicate execution risk
     * is mitigated).
     */
    public void recoverExpiredLeases() {
        Instant now = Instant.now();
        List<Job> expired = jobRepository.findExpiredLeases(now);
        for (Job job : expired) {
            log.warn("Recovering job {} — lease expired (was locked by {})", job.getId(), job.getLockedBy());
            executionRepository.markAbandoned(job.getId(), now);
            job.setStatus(JobStatus.PENDING);
            job.setLockedBy(null);
            job.setLockedUntil(null);
            jobRepository.save(job);
        }
    }
}
