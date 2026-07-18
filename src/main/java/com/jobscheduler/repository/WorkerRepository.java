package com.jobscheduler.repository;

import com.jobscheduler.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    List<Worker> findByStatus(Worker.WorkerStatus status);

    Optional<Worker> findByIsLeaderTrue();

    /**
     * Heartbeat update — cheap, frequent write. Kept as a single UPDATE
     * rather than load-modify-save to avoid optimistic lock contention
     * and unnecessary round trips (Phase 4 - Heartbeats).
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Worker w
           SET w.lastHeartbeatAt = :now, w.status = 'ACTIVE'
           WHERE w.workerId = :workerId
           """)
    int updateHeartbeat(@Param("workerId") String workerId, @Param("now") Instant now);

    @Query("""
           SELECT w FROM Worker w
           WHERE w.status = 'ACTIVE'
             AND w.lastHeartbeatAt < :threshold
           """)
    List<Worker> findStaleWorkers(@Param("threshold") Instant threshold);

    /**
     * Lease-based leader election (Phase 6): a node becomes leader only if
     * no one currently holds a valid lease, OR the existing lease has expired.
     * The WHERE clause makes this atomic and race-safe across nodes —
     * exactly one concurrent UPDATE can match a given expired/empty leader row.
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Worker w
           SET w.isLeader = true, w.leaderLeaseUntil = :leaseUntil
           WHERE w.workerId = :candidateId
             AND NOT EXISTS (
                 SELECT 1 FROM Worker w2
                 WHERE w2.isLeader = true
                   AND w2.leaderLeaseUntil > :now
                   AND w2.workerId <> :candidateId
             )
           """)
    int tryAcquireLeadership(@Param("candidateId") String candidateId,
                              @Param("now") Instant now,
                              @Param("leaseUntil") Instant leaseUntil);

    @Modifying
    @Transactional
    @Query("UPDATE Worker w SET w.isLeader = false WHERE w.workerId = :workerId")
    void relinquishLeadership(@Param("workerId") String workerId);
}
