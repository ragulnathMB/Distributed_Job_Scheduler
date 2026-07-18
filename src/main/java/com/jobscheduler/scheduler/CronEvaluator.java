package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobType;
import com.jobscheduler.util.CronUtil;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Computes the next `nextRunAt` for a Job *after* it has just executed,
 * based on its JobType. Called by SchedulerService once an execution
 * finishes successfully.
 *
 * ONE_TIME and DELAYED jobs return null — there's no next run, so
 * SchedulerService will transition them to COMPLETED instead of
 * rescheduling them.
 */
@Component
public class CronEvaluator {

    public Instant computeNextRunAt(Job job, Instant completedAt) {
        return switch (job.getJobType()) {
            case ONE_TIME, DELAYED -> null;
            case RECURRING -> completedAt.plusSeconds(job.getFixedIntervalSeconds());
            case CRON -> CronUtil.nextExecutionTime(job.getCronExpression(), completedAt);
        };
    }

    public boolean isRecurring(Job job) {
        return job.getJobType() == JobType.RECURRING || job.getJobType() == JobType.CRON;
    }
}
