-- Single-row table used for lease-based leader election.
CREATE TABLE IF NOT EXISTS scheduler_leader (
    id               INT PRIMARY KEY,
    node_id          VARCHAR(100),
    term             BIGINT      NOT NULL DEFAULT 0,
    lease_expires_at TIMESTAMPTZ NOT NULL
);

INSERT INTO scheduler_leader (id, node_id, term, lease_expires_at)
VALUES (1, NULL, 0, '1970-01-01T00:00:00Z')
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS jobs (
    id                   UUID PRIMARY KEY,
    name                 VARCHAR(200) NOT NULL,
    handler_type         VARCHAR(50)  NOT NULL,
    payload              TEXT,
    schedule_type        VARCHAR(20)  NOT NULL,
    cron_expression      VARCHAR(100),
    interval_seconds     BIGINT,
    priority             INT          NOT NULL DEFAULT 5,
    status               VARCHAR(20)  NOT NULL,
    next_run_at          TIMESTAMPTZ,
    max_retries          INT          NOT NULL DEFAULT 3,
    retry_count          INT          NOT NULL DEFAULT 0,
    backoff_seconds      INT          NOT NULL DEFAULT 5,
    timeout_seconds      INT          NOT NULL DEFAULT 60,
    run_count            BIGINT       NOT NULL DEFAULT 0,
    claimed_by           VARCHAR(100),
    assigned_worker      VARCHAR(100),
    current_execution_id BIGINT,
    lease_expires_at     TIMESTAMPTZ,
    last_run_at          TIMESTAMPTZ,
    last_error           TEXT,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_jobs_due ON jobs (status, priority DESC, next_run_at);
CREATE INDEX IF NOT EXISTS idx_jobs_lease ON jobs (status, lease_expires_at);

-- Append-only history: one row per attempt.
CREATE TABLE IF NOT EXISTS job_executions (
    id          BIGSERIAL PRIMARY KEY,
    job_id      UUID         NOT NULL REFERENCES jobs (id) ON DELETE CASCADE,
    attempt     INT          NOT NULL,
    worker_id   VARCHAR(100),
    status      VARCHAR(20)  NOT NULL,
    started_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    finished_at TIMESTAMPTZ,
    output      TEXT,
    error       TEXT
);

CREATE INDEX IF NOT EXISTS idx_exec_job ON job_executions (job_id, id);

CREATE TABLE IF NOT EXISTS workers (
    id             VARCHAR(100) PRIMARY KEY,
    host           VARCHAR(200) NOT NULL,
    port           INT          NOT NULL,
    capacity       INT          NOT NULL DEFAULT 4,
    status         VARCHAR(20)  NOT NULL,
    last_heartbeat TIMESTAMPTZ  NOT NULL,
    registered_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
