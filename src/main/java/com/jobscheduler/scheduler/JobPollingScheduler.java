package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * The heartbeat of the whole scheduler: a fixed-delay polling loop that
 * scans the DB for due jobs and hands them off for dispatch.
 *
 * Design choice — Polling vs Push:
 * We poll rather than push because:
 *  1. It's simple and requires no extra infrastructure (no message broker).
 *  2. It naturally self-heals: if a poll cycle is missed (GC pause, node
 *     restart), the next cycle just picks up all due jobs — nothing is lost
 *     as long as nextRunAt was persisted.
 *  3. Trade-off: polling adds up to `poll-interval-ms` latency and constant
 *     DB load even when idle. Push-based systems (e.g. Kafka-triggered)
 *     avoid this but add operational complexity — a natural Phase 8+ upgrade.
 *
 * Only the elected leader should actually dispatch (see SchedulerService.isLeader())
 * to avoid every node in the cluster double-dispatching the same due jobs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobPollingScheduler {

    private final JobRepository jobRepository;
    private final SchedulerService schedulerService;
    private final PriorityJobQueue priorityJobQueue;

    @Scheduled(fixedDelayString = "${scheduler.poll-interval-ms}")
    public void pollAndDispatch() {
        if (!schedulerService.isLeader()) {
            log.trace("Not leader — skipping poll cycle");
            return;
        }

        Instant now = Instant.now();
        int batchSize = schedulerService.getBatchSize();

        List<Job> dueJobs = jobRepository.findDueJobs(now, PageRequest.of(0, batchSize));
        if (dueJobs.isEmpty()) {
            return;
        }

        log.info("Poll cycle found {} due job(s)", dueJobs.size());
        dueJobs.forEach(priorityJobQueue::offer);

        schedulerService.drainQueueAndDispatch(priorityJobQueue);
    }

    /**
     * Separate, less frequent cycle that recovers jobs whose worker lease
     * expired without completion — see Phase 5 (Crash Recovery).
     */
    @Scheduled(fixedDelayString = "${scheduler.poll-interval-ms}")
    public void recoverExpiredLeases() {
        if (!schedulerService.isLeader()) {
            return;
        }
        schedulerService.recoverExpiredLeases();
    }
}
