CREATE TABLE workers (
    worker_id               VARCHAR(100)   PRIMARY KEY,
    hostname                VARCHAR(255)   NOT NULL,
    status                  VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    max_concurrent_tasks    INTEGER        NOT NULL DEFAULT 10,
    current_task_count      INTEGER        NOT NULL DEFAULT 0,
    is_leader               BOOLEAN        NOT NULL DEFAULT FALSE,
    leader_lease_until      TIMESTAMPTZ,
    last_heartbeat_at       TIMESTAMPTZ,
    registered_at           TIMESTAMPTZ    NOT NULL DEFAULT now()
);

-- Powers WorkerRegistrationService.detectStaleWorkers() and load-based selection
CREATE INDEX idx_workers_status ON workers (status);

-- Ensures at most one row can hold leadership at a time at the DB level too,
-- as a defense-in-depth complement to the conditional UPDATE in
-- WorkerRepository.tryAcquireLeadership()
CREATE UNIQUE INDEX idx_workers_single_leader ON workers (is_leader) WHERE is_leader = TRUE;

COMMENT ON COLUMN workers.leader_lease_until IS 'Lease expiry for leader election — enables self-healing failover without manual intervention';
