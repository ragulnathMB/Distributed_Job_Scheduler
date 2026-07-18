package com.jobscheduler.entity;

/**
 * Status of a single execution attempt of a Job (JobExecution row).
 * A Job can have many JobExecution rows over its lifetime — one per attempt.
 *
 *   LEASED     -> claimed by a worker but not yet started
 *   RUNNING    -> actively executing on a worker
 *   SUCCEEDED  -> completed without error
 *   FAILED     -> threw an error / returned non-zero
 *   TIMED_OUT  -> exceeded max allowed execution duration
 *   ABANDONED  -> lease expired without heartbeat (worker likely crashed)
 */
public enum ExecutionStatus {
    LEASED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    TIMED_OUT,
    ABANDONED
}
