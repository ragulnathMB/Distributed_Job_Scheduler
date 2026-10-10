package com.jobscheduler.scheduler.model;

import java.time.Instant;
import java.util.UUID;

/** One attempt at running a job. status: RUNNING, SUCCESS, FAILED, LOST. */
public record JobExecution(
        Long id,
        UUID jobId,
        int attempt,
        String workerId,
        String status,
        Instant startedAt,
        Instant finishedAt,
        String output,
        String error) {
}
