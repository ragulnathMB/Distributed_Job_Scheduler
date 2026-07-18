package com.jobscheduler.repository;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Core polling query: finds jobs that are due to run and not currently leased.
     * Ordered by priority DESC so higher-priority jobs are claimed first within
     * a single poll batch — this is the DB-level half of PriorityJobQueue.
     *
     * PESSIMISTIC_WRITE here would serialize claims across nodes; instead we rely
     * on optimistic locking (Job.version) at the claim step (see SchedulerService)
     * so multiple nodes can read this query concurrently without blocking each other,
     * and only collide (rarely) at the actual claim UPDATE.
     */
    @Query("""
           SELECT j FROM Job j
           WHERE j.status = 'PENDING'
             AND j.nextRunAt <= :now
             AND (j.lockedUntil IS NULL OR j.lockedUntil <= :now)
           ORDER BY j.priority DESC, j.nextRunAt ASC
           """)
    List<Job> findDueJobs(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);

    /**
     * Atomic claim: only succeeds if the job is still unlocked/expired-lock at
     * write time. Returns number of rows updated — 0 means another node won the race.
     * This is the "lease" acquisition step referenced in Phase 4 (Task Leasing).
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Job j
           SET j.status = 'RUNNING',
               j.lockedBy = :nodeId,
               j.lockedUntil = :leaseUntil
           WHERE j.id = :jobId
             AND j.status = 'PENDING'
             AND (j.lockedUntil IS NULL OR j.lockedUntil <= :now)
           """)
    int claimJob(@Param("jobId") Long jobId,
                 @Param("nodeId") String nodeId,
                 @Param("now") Instant now,
                 @Param("leaseUntil") Instant leaseUntil);

    /** Finds jobs whose lease expired without the worker finishing — crash recovery (Phase 5). */
    @Query("""
           SELECT j FROM Job j
           WHERE j.status = 'RUNNING'
             AND j.lockedUntil < :now
           """)
    List<Job> findExpiredLeases(@Param("now") Instant now);

    List<Job> findByStatus(JobStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Job j WHERE j.id = :id")
    Job findByIdForUpdate(@Param("id") Long id);
}
