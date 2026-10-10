package com.jobscheduler.scheduler.repository;

import com.jobscheduler.scheduler.model.Job;
import com.jobscheduler.scheduler.model.JobSpec;
import com.jobscheduler.scheduler.model.JobStatus;
import com.jobscheduler.scheduler.model.ScheduleType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JobRepository {

    private static final RowMapper<Job> MAPPER = (rs, i) -> new Job(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getString("handler_type"),
            rs.getString("payload"),
            ScheduleType.valueOf(rs.getString("schedule_type")),
            rs.getString("cron_expression"),
            Db.nullableLong(rs, "interval_seconds"),
            rs.getInt("priority"),
            JobStatus.valueOf(rs.getString("status")),
            Db.instant(rs, "next_run_at"),
            rs.getInt("max_retries"),
            rs.getInt("retry_count"),
            rs.getInt("backoff_seconds"),
            rs.getInt("timeout_seconds"),
            rs.getLong("run_count"),
            rs.getString("claimed_by"),
            rs.getString("assigned_worker"),
            Db.nullableLong(rs, "current_execution_id"),
            Db.instant(rs, "lease_expires_at"),
            Db.instant(rs, "last_run_at"),
            rs.getString("last_error"),
            Db.instant(rs, "created_at"),
            Db.instant(rs, "updated_at"));

    private final JdbcTemplate jdbc;

    public JobRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ------------------------------------------------------------------ CRUD

    public void insert(UUID id, JobSpec s) {
        jdbc.update("""
                INSERT INTO jobs (id, name, handler_type, payload, schedule_type, cron_expression, interval_seconds,
                                  priority, status, next_run_at, max_retries, backoff_seconds, timeout_seconds)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?, ?, ?)
                """,
                id, s.name(), s.handlerType(), s.payload(), s.scheduleType().name(), s.cronExpression(),
                s.intervalSeconds(), s.priority(), Db.ts(s.nextRunAt()), s.maxRetries(), s.backoffSeconds(),
                s.timeoutSeconds());
    }

    /** Only PENDING / PAUSED jobs may be edited. */
    public boolean update(UUID id, JobSpec s) {
        return jdbc.update("""
                UPDATE jobs SET name = ?, handler_type = ?, payload = ?, schedule_type = ?, cron_expression = ?,
                       interval_seconds = ?, priority = ?, next_run_at = ?, max_retries = ?, backoff_seconds = ?,
                       timeout_seconds = ?, retry_count = 0, last_error = NULL, updated_at = now()
                WHERE id = ? AND status IN ('PENDING', 'PAUSED')
                """,
                s.name(), s.handlerType(), s.payload(), s.scheduleType().name(), s.cronExpression(),
                s.intervalSeconds(), s.priority(), Db.ts(s.nextRunAt()), s.maxRetries(), s.backoffSeconds(),
                s.timeoutSeconds(), id) == 1;
    }

    public Optional<Job> findById(UUID id) {
        return jdbc.query("SELECT * FROM jobs WHERE id = ?", MAPPER, id).stream().findFirst();
    }

    public List<Job> list(JobStatus status, int limit, int offset) {
        if (status == null) {
            return jdbc.query("SELECT * FROM jobs ORDER BY created_at DESC LIMIT ? OFFSET ?", MAPPER, limit, offset);
        }
        return jdbc.query("SELECT * FROM jobs WHERE status = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                MAPPER, status.name(), limit, offset);
    }

    public boolean delete(UUID id) {
        return jdbc.update("DELETE FROM jobs WHERE id = ? AND status <> 'RUNNING'", id) == 1;
    }

    public Map<String, Long> countByStatus() {
        ResultSetExtractor<Map<String, Long>> ex = rs -> {
            Map<String, Long> m = new TreeMap<>();
            while (rs.next()) {
                m.put(rs.getString(1), rs.getLong(2));
            }
            return m;
        };
        return jdbc.query("SELECT status, COUNT(*) FROM jobs GROUP BY status", ex);
    }

    // ------------------------------------------------------ lifecycle control

    /** Conditional status change; also clears any claim data. Returns false if the job was not in a 'from' state. */
    public boolean transition(UUID id, JobStatus to, JobStatus... from) {
        String in = Arrays.stream(from).map(s -> "'" + s.name() + "'").collect(Collectors.joining(","));
        return jdbc.update("UPDATE jobs SET status = ?, claimed_by = NULL, assigned_worker = NULL, "
                        + "lease_expires_at = NULL, current_execution_id = NULL, updated_at = now() "
                        + "WHERE id = ? AND status IN (" + in + ")",
                to.name(), id) == 1;
    }

    public boolean resume(UUID id, Instant nextRunAt) {
        return jdbc.update("UPDATE jobs SET status = 'PENDING', next_run_at = ?, updated_at = now() "
                + "WHERE id = ? AND status = 'PAUSED'", Db.ts(nextRunAt), id) == 1;
    }

    // ------------------------------------------------------------ dispatching

    /** Due jobs, highest priority first, then oldest due time first. */
    public List<Job> findDueCandidates(int limit) {
        return jdbc.query("""
                SELECT * FROM jobs
                WHERE status = 'PENDING' AND next_run_at <= now()
                ORDER BY priority DESC, next_run_at ASC
                LIMIT ?
                """, MAPPER, limit);
    }

    /**
     * Optimistic claim: succeeds for exactly one caller even if several nodes race (e.g. during a leader hand-over),
     * because the WHERE clause only matches while the job is still PENDING and due.
     */
    public boolean tryClaim(UUID id, String nodeId, String workerId, int leaseSeconds) {
        return jdbc.update("""
                UPDATE jobs SET status = 'RUNNING', claimed_by = ?, assigned_worker = ?,
                       lease_expires_at = now() + (? * interval '1 second'),
                       last_run_at = now(), run_count = run_count + 1, updated_at = now()
                WHERE id = ? AND status = 'PENDING' AND next_run_at <= now()
                """, nodeId, workerId, leaseSeconds, id) == 1;
    }

    public void setCurrentExecution(UUID jobId, long executionId) {
        jdbc.update("UPDATE jobs SET current_execution_id = ? WHERE id = ?", executionId, jobId);
    }

    /** Dispatch failed (worker unreachable / full): put the job back without consuming a retry. */
    public void releaseAfterDispatchFailure(UUID jobId, long executionId, String error) {
        jdbc.update("""
                UPDATE jobs SET status = 'PENDING', next_run_at = now() + interval '2 seconds', claimed_by = NULL,
                       assigned_worker = NULL, lease_expires_at = NULL, current_execution_id = NULL,
                       last_error = ?, updated_at = now()
                WHERE id = ? AND status = 'RUNNING' AND current_execution_id = ?
                """, error, jobId, executionId);
    }

    /** Guarded by (status = RUNNING, current_execution_id) so a stale result can never overwrite newer state. */
    public boolean finishRun(UUID jobId, long executionId, JobStatus status, Instant nextRunAt,
                             int retryCount, String lastError) {
        return jdbc.update("""
                UPDATE jobs SET status = ?, next_run_at = ?, retry_count = ?, last_error = ?, claimed_by = NULL,
                       assigned_worker = NULL, lease_expires_at = NULL, current_execution_id = NULL, updated_at = now()
                WHERE id = ? AND status = 'RUNNING' AND current_execution_id = ?
                """, status.name(), Db.ts(nextRunAt), retryCount, lastError, jobId, executionId) == 1;
    }

    // --------------------------------------------------------- crash recovery

    /** Reset RUNNING jobs whose lease expired back to PENDING and mark their open executions LOST. */
    @Transactional
    public int recoverExpiredLeases() {
        String reason = "Lease expired without a result (worker crashed or hung)";
        jdbc.update("""
                UPDATE job_executions SET status = 'LOST', finished_at = now(), error = ?
                WHERE status = 'RUNNING' AND id IN (
                    SELECT current_execution_id FROM jobs WHERE status = 'RUNNING' AND lease_expires_at < now())
                """, reason);
        return jdbc.update("""
                UPDATE jobs SET status = 'PENDING', next_run_at = now(), claimed_by = NULL, assigned_worker = NULL,
                       lease_expires_at = NULL, current_execution_id = NULL, last_error = ?, updated_at = now()
                WHERE status = 'RUNNING' AND lease_expires_at < now()
                """, reason);
    }

    /** Same as above but triggered immediately when a worker is declared DEAD. */
    @Transactional
    public int recoverFromWorker(String workerId) {
        String reason = "Worker " + workerId + " was declared DEAD";
        jdbc.update("""
                UPDATE job_executions SET status = 'LOST', finished_at = now(), error = ?
                WHERE status = 'RUNNING' AND id IN (
                    SELECT current_execution_id FROM jobs WHERE status = 'RUNNING' AND assigned_worker = ?)
                """, reason, workerId);
        return jdbc.update("""
                UPDATE jobs SET status = 'PENDING', next_run_at = now(), claimed_by = NULL, assigned_worker = NULL,
                       lease_expires_at = NULL, current_execution_id = NULL, last_error = ?, updated_at = now()
                WHERE status = 'RUNNING' AND assigned_worker = ?
                """, reason, workerId);
    }
}
