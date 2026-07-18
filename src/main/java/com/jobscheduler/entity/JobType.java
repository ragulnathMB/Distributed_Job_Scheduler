package com.jobscheduler.entity;

/**
 * Determines how the scheduler computes a Job's next execution time.
 *
 *   ONE_TIME  -> executes once at a specific scheduledAt timestamp
 *   RECURRING -> executes every fixedIntervalSeconds after the previous run
 *   CRON      -> next run computed from a cron expression
 *   DELAYED   -> one-time execution after an initial delay from creation time
 */
public enum JobType {
    ONE_TIME,
    RECURRING,
    CRON,
    DELAYED
}
