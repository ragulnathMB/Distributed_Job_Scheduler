package com.jobscheduler.worker;

import com.jobscheduler.entity.Worker;
import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Manages the worker registry: registration on startup, stale-worker
 * detection, and simple load-based selection for dispatch.
 *
 * Failure detection design: rather than workers actively announcing death
 * (impossible — a crashed process can't announce anything), we use a
 * timeout-based passive check: if `lastHeartbeatAt` hasn't been refreshed
 * within `heartbeat-timeout-seconds`, we *infer* the worker is dead. This is
 * the standard phi-accrual-lite approach used by most real systems (a fixed
 * timeout here; production systems like Cassandra use an adaptive phi-accrual
 * detector — worth knowing as an alternative design).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerRegistrationService {

    private final WorkerRepository workerRepository;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Value("${scheduler.heartbeat-timeout-seconds}")
    private long heartbeatTimeoutSeconds;

    public void registerSelf() {
        Worker self = Worker.builder()
                .workerId(nodeId)
                .hostname(resolveHostname())
                .status(Worker.WorkerStatus.ACTIVE)
                .maxConcurrentTasks(50)
                .currentTaskCount(0)
                .isLeader(false)
                .build();
        workerRepository.save(self);
        log.info("Worker {} registered", nodeId);
    }

    /**
     * Very simple least-loaded selection among ACTIVE workers.
     * A production system would also consider network locality, task
     * affinity, and resource requirements (CPU/memory) — noted here as
     * an extensibility point rather than implemented, to keep this
     * educational build focused on the core distributed-systems concepts.
     */
    public Optional<String> selectAvailableWorker() {
        List<Worker> activeWorkers = workerRepository.findByStatus(Worker.WorkerStatus.ACTIVE);
        return activeWorkers.stream()
                .filter(w -> w.getCurrentTaskCount() < w.getMaxConcurrentTasks())
                .min((a, b) -> Integer.compare(a.getCurrentTaskCount(), b.getCurrentTaskCount()))
                .map(Worker::getWorkerId);
    }

    /**
     * Runs periodically to mark workers DEAD if their heartbeat is stale.
     * Jobs that were leased to a now-dead worker get recovered separately
     * by SchedulerService.recoverExpiredLeases() based on the Job's own
     * lockedUntil lease, decoupling "worker health" from "job lease expiry".
     */
    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void detectStaleWorkers() {
        Instant threshold = Instant.now().minusSeconds(heartbeatTimeoutSeconds);
        List<Worker> stale = workerRepository.findStaleWorkers(threshold);
        for (Worker worker : stale) {
            worker.setStatus(Worker.WorkerStatus.DEAD);
            workerRepository.save(worker);
            log.warn("Worker {} marked DEAD — no heartbeat since {}", worker.getWorkerId(), worker.getLastHeartbeatAt());
        }
    }

    private String resolveHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-host";
        }
    }
}
