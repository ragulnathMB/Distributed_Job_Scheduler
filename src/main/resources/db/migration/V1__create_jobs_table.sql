CREATE TABLE jobs (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255)    NOT NULL,
    job_type                VARCHAR(20)     NOT NULL,
    task_handler            VARCHAR(255)    NOT NULL,
    payload                 TEXT,
    cron_expression         VARCHAR(100),
    fixed_interval_seconds  BIGINT,
    scheduled_at            TIMESTAMPTZ,
    next_run_at             TIMESTAMPTZ,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    priority                INTEGER         NOT NULL DEFAULT 0,
    max_retries             INTEGER         NOT NULL DEFAULT 5,
    retry_count             INTEGER         NOT NULL DEFAULT 0,
    timeout_seconds         INTEGER         NOT NULL DEFAULT 60,
    locked_by               VARCHAR(100),
    locked_until            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT now(),
    version                 BIGINT          NOT NULL DEFAULT 0
);

-- Backs the core polling query in JobRepository.findDueJobs():
-- WHERE status = 'PENDING' AND next_run_at <= now() ORDER BY priority DESC
CREATE INDEX idx_jobs_next_run_status ON jobs (next_run_at, status);

-- Speeds up filtering the dashboard/API by status (e.g. list all FAILED jobs)
CREATE INDEX idx_jobs_status ON jobs (status);

COMMENT ON COLUMN jobs.version IS 'Optimistic lock to prevent double-claiming across scheduler nodes';
COMMENT ON COLUMN jobs.locked_until IS 'Lease expiry — used for crash recovery of stuck RUNNING jobs';
