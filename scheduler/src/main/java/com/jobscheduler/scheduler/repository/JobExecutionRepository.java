package com.jobscheduler.scheduler.repository;

import com.jobscheduler.scheduler.model.JobExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Append-only execution history: rows are inserted once and only ever finalised, never deleted or rewritten. */
@Repository
public class JobExecutionRepository {

    private static final RowMapper<JobExecution> MAPPER = (rs, i) -> new JobExecution(
            rs.getLong("id"),
            rs.getObject("job_id", UUID.class),
            rs.getInt("attempt"),
            rs.getString("worker_id"),
            rs.getString("status"),
            Db.instant(rs, "started_at"),
            Db.instant(rs, "finished_at"),
            rs.getString("output"),
            rs.getString("error"));

    private final JdbcTemplate jdbc;

    public JobExecutionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long create(UUID jobId, int attempt, String workerId) {
        Long id = jdbc.queryForObject("""
                INSERT INTO job_executions (job_id, attempt, worker_id, status, started_at)
                VALUES (?, ?, ?, 'RUNNING', now()) RETURNING id
                """, Long.class, jobId, attempt, workerId);
        return id == null ? -1 : id;
    }

    /** Finalise a RUNNING execution. Returns false if it was already finalised (e.g. marked LOST). */
    public boolean finish(long id, String status, String output, String error) {
        return jdbc.update("UPDATE job_executions SET status = ?, finished_at = now(), output = ?, error = ? "
                + "WHERE id = ? AND status = 'RUNNING'", status, output, error, id) == 1;
    }

    public Optional<JobExecution> findById(long id) {
        return jdbc.query("SELECT * FROM job_executions WHERE id = ?", MAPPER, id).stream().findFirst();
    }

    public List<JobExecution> findByJob(UUID jobId) {
        return jdbc.query("SELECT * FROM job_executions WHERE job_id = ? ORDER BY id", MAPPER, jobId);
    }
}
