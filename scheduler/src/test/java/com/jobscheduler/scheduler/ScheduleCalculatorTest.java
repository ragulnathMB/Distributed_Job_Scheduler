package com.jobscheduler.scheduler;

import com.jobscheduler.scheduler.service.ScheduleCalculator;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleCalculatorTest {

    @Test
    void backoffDoublesAndIsCapped() {
        assertEquals(5, ScheduleCalculator.backoffSeconds(5, 1, 300));
        assertEquals(10, ScheduleCalculator.backoffSeconds(5, 2, 300));
        assertEquals(20, ScheduleCalculator.backoffSeconds(5, 3, 300));
        assertEquals(300, ScheduleCalculator.backoffSeconds(5, 15, 300));
    }

    @Test
    void cronNextFireIsInTheFuture() {
        Instant from = Instant.parse("2026-01-01T10:00:30Z");
        Instant next = ScheduleCalculator.nextCron("0 * * * * *", from);
        assertEquals(Instant.parse("2026-01-01T10:01:00Z"), next);
    }

    @Test
    void invalidCronIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScheduleCalculator.validateCron("not a cron"));
    }

    @Test
    void fixedIntervalCatchesUpWhenBehind() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        assertEquals(Instant.parse("2026-01-01T10:00:30Z"),
                ScheduleCalculator.nextFixedInterval(Instant.parse("2026-01-01T10:00:00Z"), 30, now));
        assertEquals(now, ScheduleCalculator.nextFixedInterval(Instant.parse("2026-01-01T09:00:00Z"), 30, now));
    }
}
