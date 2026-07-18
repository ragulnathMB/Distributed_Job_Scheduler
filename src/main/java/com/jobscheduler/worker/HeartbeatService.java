package com.jobscheduler.worker;

import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Periodically refreshes this node's `lastHeartbeatAt` row so
 * WorkerRegistrationService's stale-check on other nodes doesn't mark it dead.
 *
 * Why a separate class from WorkerRegistrationService?
 * - Single Responsibility: registration/detection is a *reader* of heartbeat
 *   data across all workers; this is a *writer* of this node's own heartbeat.
 *   In a real multi-process deployment these run in different JVMs entirely.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatService {

    private final WorkerRepository workerRepository;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void sendHeartbeat() {
        int updated = workerRepository.updateHeartbeat(nodeId, Instant.now());
        if (updated == 0) {
            log.warn("Heartbeat update affected 0 rows — worker {} may not be registered", nodeId);
        } else {
            log.debug("Heartbeat sent for worker {}", nodeId);
        }
    }
}
