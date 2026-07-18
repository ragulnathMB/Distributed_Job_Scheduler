package com.jobscheduler.service;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.scheduler.CronEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implements retry-with-exponential-backoff and dead-letter routing.
 *
 * Backoff formula: base * 2^attempt, capped, so repeated failures don't
 * hammer a downstream dependency that's already struggling (thundering-herd
 * avoidance). Once retryCount exceeds maxRetries, the job is moved to
 * FAILED — conceptually our "dead letter queue" is simply
 * status=FAILED rows, queryable via JobController for inspection/replay,
 * rather than a separate physical queue. This keeps the DB as the single
 * source of truth, matching the rest of this project's design.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final JobRepository jobRepository;
    private final CronEvaluator cronEvaluator;

    @Value("${scheduler.retry-base-delay-seconds}")
    private long baseDelaySeconds;

    private static final long MAX_BACKOFF_SECONDS = 300; // 5 minute cap

    public void handleSuccess(Job job) {
        job.setRetryCount(0);

        if (cronEvaluator.isRecurring(job)) {
            Instant nextRun = cronEvaluator.computeNextRunAt(job, Instant.now());
            job.setNextRunAt(nextRun);
            job.setStatus(JobStatus.PENDING);
        } else {
            job.setStatus(JobStatus.COMPLETED);
            job.setNextRunAt(null);
        }
        clearLease(job);
        jobRepository.save(job);
    }

    public void handleFailure(Job job, String errorMessage) {
        int nextAttempt = job.getRetryCount() + 1;

        if (nextAttempt > job.getMaxRetries()) {
            log.error("Job {} exhausted {} retries — moving to FAILED (dead letter). Last error: {}",
                    job.getId(), job.getMaxRetries(), errorMessage);
            job.setStatus(JobStatus.FAILED);
            job.setNextRunAt(null);
            clearLease(job);
            jobRepository.save(job);
            return;
        }

        long backoffSeconds = Math.min(
                baseDelaySeconds * (1L << nextAttempt),
                MAX_BACKOFF_SECONDS
        );

        job.setRetryCount(nextAttempt);
        job.setStatus(JobStatus.PENDING);
        job.setNextRunAt(Instant.now().plusSeconds(backoffSeconds));
        clearLease(job);
        jobRepository.save(job);

        log.warn("Job {} failed (attempt {}/{}) — retrying in {}s. Error: {}",
                job.getId(), nextAttempt, job.getMaxRetries(), backoffSeconds, errorMessage);
    }

    private void clearLease(Job job) {
        job.setLockedBy(null);
        job.setLockedUntil(null);
    }
}
