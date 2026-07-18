package com.jobscheduler.util;

import com.jobscheduler.exception.InvalidCronExpressionException;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Thin wrapper around Spring's CronExpression so the rest of the codebase
 * doesn't need to know about java.time <-> Instant conversions.
 *
 * Why not write our own cron parser?
 * - Cron parsing is a solved, well-tested problem. Spring's CronExpression
 *   already supports standard 6-field (with seconds) cron syntax.
 * - Writing our own here would be reinventing a wheel that doesn't teach
 *   any new distributed-systems concept — the interesting part is *how we use*
 *   the next-fire-time, not how we parse "* * * * * *".
 */
public final class CronUtil {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private CronUtil() {
    }

    /** Validates a cron expression eagerly, e.g. at job-creation time. */
    public static void validate(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
        } catch (IllegalArgumentException ex) {
            throw new InvalidCronExpressionException(
                    "Invalid cron expression: " + cronExpression, ex);
        }
    }

    /** Computes the next fire time strictly after `from`. */
    public static Instant nextExecutionTime(String cronExpression, Instant from) {
        CronExpression expression = CronExpression.parse(cronExpression);
        LocalDateTime fromLocal = LocalDateTime.ofInstant(from, ZONE);
        LocalDateTime next = expression.next(fromLocal);
        if (next == null) {
            throw new InvalidCronExpressionException(
                    "Cron expression has no future execution time: " + cronExpression);
        }
        return next.atZone(ZONE).toInstant();
    }
}
