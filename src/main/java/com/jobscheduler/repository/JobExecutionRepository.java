package com.jobscheduler.repository;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findByJobIdOrderByCreatedAtDesc(Long jobId);

    Optional<JobExecution> findTopByJobIdOrderByAttemptNumberDesc(Long jobId);

    List<JobExecution> findByStatus(ExecutionStatus status);

    /**
     * Used by crash recovery: any execution still marked RUNNING/LEASED whose
     * parent job's lease has expired is stale and gets flipped to ABANDONED
     * so it doesn't pollute metrics or dashboards as "still running".
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE JobExecution e
           SET e.status = 'ABANDONED',
               e.completedAt = :now
           WHERE e.jobId = :jobId
             AND e.status IN ('LEASED', 'RUNNING')
           """)
    int markAbandoned(@Param("jobId") Long jobId, @Param("now") Instant now);

    long countByJobIdAndStatus(Long jobId, ExecutionStatus status);
}
