package com.jobscheduler.scheduler.service;

import com.jobscheduler.scheduler.config.SchedulerProperties;
import com.jobscheduler.scheduler.model.LeaderInfo;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Lease-based leader election on a single PostgreSQL row.
 * <p>
 * A node becomes (or stays) leader only if its conditional UPDATE matches: the row is already ours OR the previous
 * lease has expired. Postgres serialises concurrent updates on the row, so at most one node can win. The lease
 * comparison uses the database clock ({@code now()}), so clock skew between scheduler nodes is irrelevant.
 * The {@code term} column is a fencing counter that increases on every change of leadership.
 */
@Service
public class LeaderElectionService {
    private static final Logger log = LoggerFactory.getLogger(LeaderElectionService.class);

    private final JdbcTemplate jdbc;
    private final SchedulerProperties props;
    private volatile boolean leader = false;

    public LeaderElectionService(JdbcTemplate jdbc, SchedulerProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    /** Try to acquire or renew the lease. Returns true if this node is the leader after the call. */
    public boolean tryAcquireOrRenew() {
        String node = props.getNodeId();
        int rows = jdbc.update("""
                UPDATE scheduler_leader
                SET term = CASE WHEN node_id = ? THEN term ELSE term + 1 END,
                    node_id = ?,
                    lease_expires_at = now() + (? * interval '1 second')
                WHERE id = 1 AND (node_id = ? OR lease_expires_at < now())
                """, node, node, props.getLeaderLeaseSeconds(), node);
        boolean nowLeader = rows == 1;
        if (nowLeader != leader) {
            log.warn("*** {} {} leadership ***", node, nowLeader ? "ACQUIRED" : "LOST");
        }
        leader = nowLeader;
        return nowLeader;
    }

    public boolean isLeader() {
        return leader;
    }

    public LeaderInfo currentLeader() {
        return jdbc.queryForObject("""
                SELECT node_id, term, lease_expires_at, (lease_expires_at > now()) AS active
                FROM scheduler_leader WHERE id = 1
                """, (rs, i) -> new LeaderInfo(
                rs.getString("node_id"),
                rs.getLong("term"),
                rs.getTimestamp("lease_expires_at").toInstant(),
                rs.getBoolean("active")));
    }

    /** On graceful shutdown hand the lease over immediately instead of making the others wait for expiry. */
    @PreDestroy
    public void release() {
        try {
            int rows = jdbc.update("UPDATE scheduler_leader SET lease_expires_at = now() - interval '1 second' "
                    + "WHERE id = 1 AND node_id = ?", props.getNodeId());
            if (rows == 1) {
                log.info("Released leadership on shutdown");
            }
        } catch (Exception e) {
            log.debug("Could not release leadership: {}", e.getMessage());
        }
        leader = false;
    }
}
