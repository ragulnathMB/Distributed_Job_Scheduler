# Distributed Job Scheduler

A production-inspired, distributed job scheduler built with Spring Boot and PostgreSQL. It supports one-time, delayed, recurring, and cron-based jobs, with priority-based dispatch, automatic retries with exponential backoff, crash recovery, and lease-based leader election across multiple scheduler nodes.

This project is designed as an educational but realistic reference implementation of the core mechanics behind systems like Quartz, Airflow, or a lightweight Temporal — the kind of distributed-systems concepts that come up in backend design interviews.

## Features

- **Multiple job types** — one-time, delayed, fixed-interval recurring, and cron-scheduled jobs
- **Priority-based dispatch** — jobs are ordered by priority (and then by due time) before being handed to workers
- **Distributed leader election** — only one scheduler node polls and dispatches at a time, using a lease-based conditional update against PostgreSQL (no ZooKeeper/etcd required)
- **Optimistic job claiming** — nodes claim due jobs via a conditional UPDATE, preventing double-dispatch across the cluster
- **Crash recovery** — jobs whose lease expires without completing are automatically reset to `PENDING` and picked back up
- **Retry with exponential backoff** — failed jobs are retried with a capped, doubling backoff; jobs that exhaust their retries move to a terminal `FAILED` ("dead letter") state
- **Execution history** — every attempt is recorded as an append-only `job_executions` row, queryable per job
- **Worker registry and heartbeats** — worker nodes register, send heartbeats, and are marked `DEAD` if they go silent
- **REST API** — full CRUD and lifecycle control (create, cancel, pause, resume) plus monitoring endpoints
- **Database-backed state** — Postgres is the single source of truth for job state, leases, and leadership; schema is version-controlled with Flyway

## Architecture

```
                    ┌─────────────────────┐
                    │   JobController      │  REST API (create/cancel/pause/resume/history)
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │     JobService        │  Job CRUD + lifecycle
                    └──────────┬───────────┘
                               │
        ┌──────────────────────▼───────────────────────┐
        │              PostgreSQL (jobs table)          │
        └──────────────────────┬───────────────────────┘
                               │  polled every poll-interval-ms
                    ┌──────────▼───────────┐
                    │ JobPollingScheduler   │  only runs on the elected leader
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  PriorityJobQueue     │  in-memory, priority + due-time ordered
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  SchedulerService     │  leader election, job claiming, crash recovery
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │   JobDispatcher       │  async dispatch, records execution lifecycle
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    TaskExecutor       │  runs the task with a timeout
                    └──────────┬───────────┘
                               │
                 success ──────┴────── failure
                    │                      │
          ┌─────────▼────────┐   ┌─────────▼─────────┐
          │  RetryService     │   │   RetryService      │
          │ (reschedule/      │   │ (backoff + retry,   │
          │  complete)        │   │  or mark FAILED)    │
          └───────────────────┘   └────────────────────┘
```

Two supporting concerns run alongside this pipeline:

- **`WorkerRegistrationService` / `HeartbeatService`** — track worker nodes, detect stale/dead ones, and feed worker selection.
- **`SchedulerService.participateInLeaderElection()`** — a recurring, lease-based conditional update that elects and renews a single leader node; if the leader crashes, its lease expires and another node takes over automatically.

## Job lifecycle

**Job status** (overall state of a job):

| Status | Meaning |
|---|---|
| `PENDING` | Waiting for its next trigger time |
| `RUNNING` | Currently leased and dispatched to a worker |
| `COMPLETED` | Terminal state for one-time/delayed jobs that finished successfully |
| `RETRY` | Failed; waiting for the next backoff-delayed attempt |
| `FAILED` | Exhausted all retry attempts (dead letter) |
| `CANCELLED` | Explicitly cancelled by the user |
| `TIMEOUT` | Execution exceeded its allowed duration |
| `PAUSED` | Paused by the user; skipped by the scheduler until resumed |

**Job type** (how the next run time is computed):

| Type | Behavior |
|---|---|
| `ONE_TIME` | Executes once at a specific `scheduledAt` timestamp |
| `DELAYED` | Executes once, after an initial delay from creation time |
| `RECURRING` | Executes every `fixedIntervalSeconds` after the previous run |
| `CRON` | Next run computed from a cron expression |

## REST API

### Jobs — `/api/jobs`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/jobs` | Create a new job |
| `GET` | `/api/jobs/{id}` | Get a job by ID |
| `GET` | `/api/jobs?status={status}` | List jobs, optionally filtered by status |
| `POST` | `/api/jobs/{id}/cancel` | Cancel a job |
| `POST` | `/api/jobs/{id}/pause` | Pause a job |
| `POST` | `/api/jobs/{id}/resume` | Resume a paused job |
| `GET` | `/api/jobs/{id}/executions` | Get execution history for a job |

Example request body for creating a job:

```json
{
  "name": "nightly-report",
  "jobType": "CRON",
  "taskHandler": "reportGenerator",
  "cronExpression": "0 0 2 * * *",
  "priority": 5,
  "maxRetries": 3,
  "timeoutSeconds": 120
}
```

### Workers — `/api/workers`

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/workers` | List all registered workers |
| `GET` | `/api/workers/leader` | Get the currently elected leader node |

Worker lifecycle (registration, heartbeats, leader election) is internal and driven by background services — it is not exposed as a mutable resource over the API.

## Tech stack

- **Java 17**, **Spring Boot 3.2.5**
- **Spring Data JPA** + **PostgreSQL**
- **Flyway** for schema migrations
- **Spring Validation** for request validation
- **Spring Actuator** for health/metrics
- **Lombok** for boilerplate reduction
- **Testcontainers** + **JUnit 5** for integration testing against a real Postgres instance

## Project structure

```
Distributed_Job_Scheduler
├── src
│   ├── main
│   │   ├── java/com/jobscheduler
│   │   │   ├── config          # Async, scheduler, and thread-pool configuration
│   │   │   ├── controller      # REST controllers
│   │   │   ├── dispatcher      # Job dispatch + task execution
│   │   │   ├── dto             # Request/response payloads
│   │   │   ├── entity          # JPA entities and enums
│   │   │   ├── exception       # Custom exceptions + global handler
│   │   │   ├── mapper          # Entity <-> DTO mapping
│   │   │   ├── repository      # Spring Data repositories
│   │   │   ├── scheduler       # Polling loop, cron evaluation, priority queue
│   │   │   ├── service         # Job, retry, and scheduler coordination services
│   │   │   ├── util            # Cron parsing helpers
│   │   │   ├── worker          # Worker registration + heartbeat services
│   │   │   └── SchedulerApplication.java
│   │   └── resources
│   │       ├── db/migration    # Flyway SQL migrations
│   │       └── application.yml
│   └── test
├── pom.xml
└── README.md
```

## Configuration

Key settings in `application.yml` (all overridable via environment variables):

```yaml
scheduler:
  node-id: ${SCHEDULER_NODE_ID:node-1}
  poll-interval-ms: 5000        # how often the scheduler scans for due jobs
  batch-size: 50                # max jobs fetched per poll cycle
  lease-duration-seconds: 30    # how long a claimed job's lease is valid
  heartbeat-interval-ms: 10000
  heartbeat-timeout-seconds: 30
  max-retry-attempts: 5
  retry-base-delay-seconds: 2   # base for exponential backoff: base * 2^attempt

thread-pool:
  dispatcher-core-size: 10
  dispatcher-max-size: 50
  dispatcher-queue-capacity: 200
  worker-core-size: 20
  worker-max-size: 100
  worker-queue-capacity: 500
```

Database connection defaults to a local PostgreSQL instance:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jobscheduler
    username: postgres
    password: postgres
```

## Getting started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (running locally or reachable via the configured URL)

### Setup

1. Create the database:

   ```bash
   createdb jobscheduler
   ```

2. Build and run:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Flyway will automatically apply the migrations in `src/main/resources/db/migration` on startup.

3. The API is available at `http://localhost:8080`.

### Running tests

```bash
mvn test
```

Integration tests spin up a real PostgreSQL container via Testcontainers rather than an in-memory database, so Flyway migrations, JPA mappings, and scheduled beans are validated against the actual target engine.

## Design notes and trade-offs

- **Polling vs. push** — the scheduler polls the database on a fixed delay rather than reacting to a push/event source. This keeps the system simple and self-healing (a missed cycle just gets picked up on the next poll) at the cost of some latency and constant idle DB load. A message-broker-driven push model is a natural upgrade path.
- **Leader election without a consensus system** — leadership is decided with a lease-based conditional UPDATE against PostgreSQL rather than ZooKeeper, etcd, or Consul. This teaches the core concept (lease + fencing via expiry) without extra infrastructure, but it is not linearizable across network partitions the way a proper Raft/Paxos-based system would be.
- **In-process workers** — "workers" currently execute as thread-pool tasks within the same JVM rather than as separate networked nodes. The worker registry, heartbeat, and selection logic are still fully modeled, so swapping the execution layer for an RPC call to real remote workers is a contained change.
- **At-least-once execution** — recovered leases are reset to `PENDING` and re-run rather than guaranteed exactly-once. Task handlers are expected to be idempotent.
- **Dead letter queue as a status, not a queue** — jobs that exhaust their retries simply move to `FAILED`. This keeps PostgreSQL as the single source of truth instead of introducing a separate physical dead-letter queue.