package com.jobscheduler.scheduler.service;

import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/** Pure scheduling maths (no I/O) so it is trivially unit-testable. All cron evaluation is in UTC. */
public final class ScheduleCalculator {
    private ScheduleCalculator() {}

    /** Validates a Spring 6-field cron expression: "second minute hour day-of-month month day-of-week". */
    public static void validateCron(String expression) {
        try {
            CronExpression.parse(expression);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid cron expression '" + expression + "': " + e.getMessage());
        }
    }

    /** Next fire time strictly after {@code from}, or null if the expression never fires again. */
    public static Instant nextCron(String expression, Instant from) {
        ZonedDateTime next = CronExpression.parse(expression).next(from.atZone(ZoneOffset.UTC));
        return next == null ? null : next.toInstant();
    }

    /** Fixed-rate style: previous due time + interval; if we are already behind, run as soon as possible. */
    public static Instant nextFixedInterval(Instant previousDue, long intervalSeconds, Instant now) {
        Instant next = (previousDue == null ? now : previousDue).plusSeconds(intervalSeconds);
        return next.isBefore(now) ? now : next;
    }

    /** base * 2^(retryNumber-1), capped. retryNumber starts at 1. */
    public static long backoffSeconds(int baseSeconds, int retryNumber, int maxSeconds) {
        int exp = Math.min(Math.max(retryNumber - 1, 0), 20);
        long delay = (long) baseSeconds * (1L << exp);
        return Math.min(delay, maxSeconds);
    }
}
