CREATE TABLE job_executions (
    id              BIGSERIAL PRIMARY KEY,
    job_id          BIGINT          NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    worker_id       VARCHAR(100),
    status          VARCHAR(20)     NOT NULL,
    attempt_number  INTEGER         NOT NULL,
    leased_at       TIMESTAMPTZ,
    started_at      TIMESTAMPTZ,
    completed_at    TIMESTAMPTZ,
    duration_ms     BIGINT,
    error_message   TEXT,
    stack_trace     TEXT,
    output          TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now()
);

-- Powers GET /api/jobs/{id}/executions (execution history / job logs)
CREATE INDEX idx_executions_job_id ON job_executions (job_id);

-- Powers dashboard queries like "show all FAILED executions cluster-wide"
CREATE INDEX idx_executions_status ON job_executions (status);

COMMENT ON TABLE job_executions IS
    'Append-only audit log: one row per execution attempt. Natural candidate for partitioning/archival at scale (Phase 8/9).';
