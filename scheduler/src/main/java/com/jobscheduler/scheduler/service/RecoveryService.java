package com.jobscheduler.scheduler.service;

import com.jobscheduler.scheduler.config.SchedulerProperties;
import com.jobscheduler.scheduler.repository.JobRepository;
import com.jobscheduler.scheduler.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** Leader-only housekeeping: dead worker detection and crash recovery of stuck jobs. */
@Service
public class RecoveryService {
    private static final Logger log = LoggerFactory.getLogger(RecoveryService.class);

    private final WorkerRepository workers;
    private final JobRepository jobs;
    private final SchedulerProperties props;

    public RecoveryService(WorkerRepository workers, JobRepository jobs, SchedulerProperties props) {
        this.workers = workers;
        this.jobs = jobs;
        this.props = props;
    }

    public void run() {
        List<String> dead = workers.markDead(props.getWorkerDeadAfterSeconds());
        for (String id : dead) {
            int recovered = jobs.recoverFromWorker(id);
            log.warn("Worker {} marked DEAD (no heartbeat for {}s); {} running job(s) reset to PENDING",
                    id, props.getWorkerDeadAfterSeconds(), recovered);
        }
        int expired = jobs.recoverExpiredLeases();
        if (expired > 0) {
            log.warn("Recovered {} job(s) whose lease expired -> PENDING", expired);
        }
    }
}
