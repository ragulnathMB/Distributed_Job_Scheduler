package com.jobscheduler.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Every node runs this tick, but only the node that currently holds the leader lease does any work.
 */
@Component
public class SchedulerLoop {
    private static final Logger log = LoggerFactory.getLogger(SchedulerLoop.class);

    private final LeaderElectionService election;
    private final RecoveryService recovery;
    private final JobDispatcher dispatcher;

    public SchedulerLoop(LeaderElectionService election, RecoveryService recovery, JobDispatcher dispatcher) {
        this.election = election;
        this.recovery = recovery;
        this.dispatcher = dispatcher;
    }

    @Scheduled(fixedDelayString = "${scheduler.poll-interval-ms:1000}")
    public void tick() {
        try {
            if (!election.tryAcquireOrRenew()) {
                return;
            }
            recovery.run();
            dispatcher.dispatchDue();
        } catch (Exception e) {
            log.error("Scheduler tick failed: {}", e.toString());
        }
    }
}
