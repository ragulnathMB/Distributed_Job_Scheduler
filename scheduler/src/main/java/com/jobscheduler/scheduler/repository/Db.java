package com.jobscheduler.scheduler.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/** Small JDBC helpers shared by the repositories. */
final class Db {
    private Db() {}

    static Instant instant(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toInstant();
    }

    static Long nullableLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    static Timestamp ts(Instant i) {
        return i == null ? null : Timestamp.from(i);
    }
}
