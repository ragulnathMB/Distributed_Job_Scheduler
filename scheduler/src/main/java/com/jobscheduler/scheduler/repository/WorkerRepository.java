package com.jobscheduler.scheduler.repository;

import com.jobscheduler.common.WorkerRegistration;
import com.jobscheduler.scheduler.model.WorkerNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkerRepository {

    private static final String SELECT = """
            SELECT w.*, (SELECT COUNT(*) FROM jobs j WHERE j.status = 'RUNNING' AND j.assigned_worker = w.id) AS active_jobs
            FROM workers w
            """;

    private static final RowMapper<WorkerNode> MAPPER = (rs, i) -> new WorkerNode(
            rs.getString("id"),
            rs.getString("host"),
            rs.getInt("port"),
            rs.getInt("capacity"),
            rs.getString("status"),
            Db.instant(rs, "last_heartbeat"),
            Db.instant(rs, "registered_at"),
            rs.getInt("active_jobs"));

    private final JdbcTemplate jdbc;

    public WorkerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void upsert(WorkerRegistration r) {
        jdbc.update("""
                INSERT INTO workers (id, host, port, capacity, status, last_heartbeat, registered_at)
                VALUES (?, ?, ?, ?, 'ALIVE', now(), now())
                ON CONFLICT (id) DO UPDATE SET host = EXCLUDED.host, port = EXCLUDED.port,
                       capacity = EXCLUDED.capacity, status = 'ALIVE', last_heartbeat = now()
                """, r.workerId(), r.host(), r.port(), r.capacity());
    }

    public boolean heartbeat(String workerId) {
        return jdbc.update("UPDATE workers SET last_heartbeat = now(), status = 'ALIVE' WHERE id = ?", workerId) == 1;
    }

    /** Flip silent workers to DEAD and return their ids. */
    public List<String> markDead(int silentForSeconds) {
        return jdbc.queryForList("""
                UPDATE workers SET status = 'DEAD'
                WHERE status = 'ALIVE' AND last_heartbeat < now() - (? * interval '1 second')
                RETURNING id
                """, String.class, silentForSeconds);
    }

    public List<WorkerNode> findAll() {
        return jdbc.query(SELECT + " ORDER BY w.id", MAPPER);
    }

    public List<WorkerNode> findAlive() {
        return jdbc.query(SELECT + " WHERE w.status = 'ALIVE' ORDER BY w.id", MAPPER);
    }
}
