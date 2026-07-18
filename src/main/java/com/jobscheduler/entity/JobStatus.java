package com.jobscheduler.entity;

/**
 * Lifecycle states of a Job (not a single execution — the Job's overall state).
 *
 *   PENDING   -> job created, waiting for its next trigger time
 *   RUNNING   -> currently leased/dispatched to a worker
 *   COMPLETED -> terminal state for one-time jobs that finished successfully
 *   FAILED    -> exhausted all retry attempts
 *   CANCELLED -> user explicitly cancelled the job
 *   RETRY     -> execution failed, waiting for next backoff-delayed attempt
 *   TIMEOUT   -> execution exceeded its allowed duration
 *   PAUSED    -> user paused; scheduler skips it until resumed
 */
public enum JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    RETRY,
    TIMEOUT,
    PAUSED
}
