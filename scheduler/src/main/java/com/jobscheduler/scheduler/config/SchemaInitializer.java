package com.jobscheduler.scheduler.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Creates the schema on startup. A Postgres advisory lock serialises the DDL so that several
 * scheduler nodes booting at the same moment don't race each other.
 */
@Component
public class SchemaInitializer {
    private static final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);
    private static final long LOCK_KEY = 727274L;

    private final DataSource dataSource;

    public SchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            try (Statement s = c.createStatement()) {
                s.execute("SELECT pg_advisory_lock(" + LOCK_KEY + ")");
            }
            try {
                ScriptUtils.executeSqlScript(c, new ClassPathResource("schema.sql"));
                log.info("Database schema is ready");
            } finally {
                try (Statement s = c.createStatement()) {
                    s.execute("SELECT pg_advisory_unlock(" + LOCK_KEY + ")");
                }
            }
        }
    }
}
