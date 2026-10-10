# Distributed Job Scheduler (Spring Boot + PostgreSQL + Docker)

A small but realistic distributed job scheduler. Several **scheduler nodes** share one PostgreSQL database and elect
a single **leader** with a lease; the leader polls for due jobs and pushes them to **worker nodes**, each running
on its own port. No ZooKeeper / etcd / Redis / Kafka needed.

```
                       ┌──────────────────────── REST API (any node) ───────────────────────┐
   curl / client ────► │ scheduler-1 :8081  (leader)        scheduler-2 :8082  (standby)    │
                       └───────┬──────────────────────────────────────┬─────────────────────┘
                               │ lease election, claim jobs,          │ same code, same DB;
                               │ crash recovery, dispatch             │ takes over if leader dies
                               ▼                                      ▼
                       ┌──────────────────────── PostgreSQL ───────────────────────────────┐
                       │ scheduler_leader │ jobs │ job_executions │ workers                │
                       └────────────────────────────────────────────────────────────────────┘
                               │ POST /internal/execute (push)           ▲ register / heartbeat /
                               ▼                                         │ result  (any scheduler node)
                 worker-1 :8091     worker-2 :8092     worker-3 :8093
```

## Run it

Requires Docker + Docker Compose v2.

```bash
docker compose up --build
```

| Service      | Port | Role                                   |
|--------------|------|----------------------------------------|
| scheduler-1  | 8081 | API + leader-eligible                  |
| scheduler-2  | 8082 | API + leader-eligible                  |
| worker-1/2/3 | 8091 / 8092 / 8093 | execute jobs             |
| postgres     | 5432 | state (db/user/password: `scheduler`)  |

```bash
./scripts/demo.sh                      # creates one job of every type
curl -s localhost:8081/api/cluster/status | jq
```

Without Docker (needs Java 17, Maven, a local Postgres): `mvn package -DskipTests`, then start
`scheduler/target/app.jar` (env `NODE_ID`, `SERVER_PORT`, `DB_URL`...) and `worker/target/app.jar`
(env `WORKER_ID`, `SERVER_PORT`, `SCHEDULER_URLS`...).

## Features and how they work

| Feature | Implementation |
|---|---|
| **Job types** | `ONE_TIME` (optionally `runAt`), `DELAYED` (`delaySeconds`), `FIXED_INTERVAL` (`intervalSeconds`), `CRON` (6-field Spring cron, UTC) |
| **Priority dispatch** | `ORDER BY priority DESC, next_run_at ASC` (priority 1–10, 10 = highest, default 5) |
| **Leader election** | Single-row `scheduler_leader` table. `UPDATE ... WHERE node_id = me OR lease_expires_at < now()` — exactly one node can win; leader renews every poll tick (1s) with a 10s lease. Uses the **DB clock**, so node clock skew is irrelevant. `term` is a fencing counter. Graceful shutdown releases the lease instantly. |
| **Optimistic claiming** | `UPDATE jobs SET status='RUNNING' ... WHERE id=? AND status='PENDING' AND next_run_at<=now()` — 1 row updated = you own it. Protects against double-dispatch even during a leader hand-over. |
| **Crash recovery** | Every RUNNING job has `lease_expires_at = now + timeout + 15s`. The leader resets expired jobs to `PENDING` (and marks the execution `LOST`). Jobs of a worker declared `DEAD` are reset immediately. |
| **Retry + backoff** | On failure: `backoff = min(base * 2^(n-1), 300s)`. After `maxRetries` retries the job becomes terminal `FAILED` (dead letter) with `lastError`. |
| **Execution history** | Every attempt is an append-only row in `job_executions` (`RUNNING → SUCCESS / FAILED / LOST`), `GET /api/jobs/{id}/executions`. |
| **Workers** | Register on startup, heartbeat every 3s, marked `DEAD` after 15s silence; revive automatically on the next heartbeat. Least-loaded worker gets the next job; each worker has a concurrency `capacity` (default 4) and returns HTTP 429 when full. |
| **Failover** | Workers know all scheduler URLs and fail over between them for register / heartbeat / result calls. |

Job lifecycle: `PENDING → RUNNING → COMPLETED | FAILED`, recurring jobs go back to `PENDING`;
`PAUSED` and `CANCELLED` are set by the user.

## REST API

| Method | Path | Description |
|---|---|---|
| POST | `/api/jobs` | create a job |
| GET | `/api/jobs?status=&limit=&offset=` | list |
| GET | `/api/jobs/{id}` | get |
| PUT | `/api/jobs/{id}` | update (only `PENDING` / `PAUSED`) |
| DELETE | `/api/jobs/{id}` | delete (not while `RUNNING`) |
| POST | `/api/jobs/{id}/cancel` / `pause` / `resume` | lifecycle control |
| GET | `/api/jobs/{id}/executions` | attempt history |
| GET | `/api/cluster/status` | this node, leader, workers, job counts |
| GET | `/api/cluster/leader` / `workers` | monitoring |
| GET | `/actuator/health` | health (both node types) |

Create-job body:

```json
{
  "name": "nightly-cleanup",
  "handlerType": "LOG",
  "payload": { "message": "hi" },
  "scheduleType": "CRON",
  "cronExpression": "0 0 2 * * *",
  "priority": 7,
  "maxRetries": 3,
  "backoffSeconds": 5,
  "timeoutSeconds": 60
}
```
`delaySeconds` for `DELAYED`, `intervalSeconds` for `FIXED_INTERVAL`, `runAt` (ISO-8601) for `ONE_TIME`.

### Built-in job handlers (`handlerType`)

| Type | Payload | Behaviour |
|---|---|---|
| `LOG` | `{"message": "..."}` | logs a message |
| `SLEEP` | `{"seconds": 5}` | simulates long work |
| `HTTP` | `{"url": "https://..."}` | GET; non-2xx = failure |
| `FAIL` | `{"message": "..."}` | always fails (retry / dead-letter demo) |
| `FLAKY` | `{"failureRate": 0.5}` | random failures |
| `CRASH` | – | kills the worker JVM mid-job (crash-recovery demo) |

Add your own: implement `JobHandler` in `worker/.../handler/` and annotate it with `@Component`.

## Try the failure scenarios

```bash
# 1. Leader failover: find the leader, stop it, watch the other take over (~10s, or instantly on graceful stop)
curl -s localhost:8082/api/cluster/leader
docker compose stop scheduler-1
curl -s localhost:8082/api/cluster/leader        # scheduler-2, term incremented
# the API keeps working on :8082 and workers fail over automatically

# 2. Crash recovery: this job kills the worker that runs it
curl -s -X POST localhost:8082/api/jobs -H 'Content-Type: application/json' \
  -d '{"name":"crasher","handlerType":"CRASH","scheduleType":"ONE_TIME"}'
# after ~15s the worker is DEAD, the job goes back to PENDING and a surviving worker picks it up
# (it will crash that one too - it's a poison pill; cancel it with POST /api/jobs/{id}/cancel)
curl -s localhost:8082/api/cluster/workers

# 3. Retry with backoff -> dead letter
curl -s -X POST localhost:8082/api/jobs -H 'Content-Type: application/json' \
  -d '{"name":"bad","handlerType":"FAIL","scheduleType":"ONE_TIME","maxRetries":3,"backoffSeconds":2}'
curl -s localhost:8082/api/jobs/<id>/executions | jq    # 4 attempts, spaced ~2s, 4s, 8s; job ends FAILED

# 4. Bring a worker back
docker compose start worker-1
```

## Configuration (env vars)

Scheduler: `NODE_ID`, `SERVER_PORT`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `LEADER_LEASE_SECONDS` (10),
`POLL_INTERVAL_MS` (1000), `WORKER_DEAD_AFTER_SECONDS` (15).
Worker: `WORKER_ID`, `WORKER_HOST` (address the scheduler uses to reach it), `SERVER_PORT`, `WORKER_CAPACITY` (4),
`SCHEDULER_URLS` (comma-separated), `HEARTBEAT_INTERVAL_MS` (3000).

To add a 4th worker, copy a `worker-*` block in `docker-compose.yml` with a new `WORKER_ID`, `WORKER_HOST`
and `SERVER_PORT`.

## Project layout

```
common/      shared DTOs (DispatchRequest, ExecutionResult, WorkerRegistration)
scheduler/   REST API, LeaderElectionService, SchedulerLoop, JobDispatcher, RecoveryService, JDBC repositories, schema.sql
worker/      registration/heartbeat, /internal/execute, JobRunner (pool + timeout), pluggable JobHandlers
```

## Design notes and limits

* **At-least-once delivery.** If a worker finishes a job but dies before reporting, or a lease expires on a slow job,
  the job can run again. Make handlers idempotent.
* A cancelled `RUNNING` job is not interrupted on the worker; its late result is recorded but ignored.
* Cron expressions are evaluated in UTC. A recurring job whose schedule was missed (e.g. while paused) skips the
  missed fires and runs at the next one.
* Leader election is only as available as PostgreSQL. For production you would add a Postgres HA setup,
  authentication on the `/internal` endpoints, and metrics/alerting.
