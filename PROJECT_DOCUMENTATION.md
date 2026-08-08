# Distributed_Job_Scheduler - Project Documentation

## 📁 Project Structure

```text
Distributed_Job_Scheduler
├── .github
│   └── modernize
│       └── java-upgrade
│           ├── hooks
│           │   └── scripts
│           │       ├── recordToolUse.ps1
│           │       └── recordToolUse.sh
│           └── .gitignore
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── jobscheduler
│   │   │           ├── config
│   │   │           │   ├── AsyncConfig.java
│   │   │           │   ├── SchedulerConfig.java
│   │   │           │   └── ThreadPoolConfig.java
│   │   │           ├── controller
│   │   │           │   ├── JobController.java
│   │   │           │   └── WorkerController.java
│   │   │           ├── dispatcher
│   │   │           │   ├── JobDispatcher.java
│   │   │           │   └── TaskExecutor.java
│   │   │           ├── dto
│   │   │           │   ├── JobExecutionResponse.java
│   │   │           │   ├── JobRequest.java
│   │   │           │   └── JobResponse.java
│   │   │           ├── entity
│   │   │           │   ├── ExecutionStatus.java
│   │   │           │   ├── Job.java
│   │   │           │   ├── JobExecution.java
│   │   │           │   ├── JobStatus.java
│   │   │           │   ├── JobType.java
│   │   │           │   └── Worker.java
│   │   │           ├── exception
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── InvalidCronExpressionException.java
│   │   │           │   └── JobNotFoundException.java
│   │   │           ├── mapper
│   │   │           │   └── JobMapper.java
│   │   │           ├── repository
│   │   │           │   ├── JobExecutionRepository.java
│   │   │           │   ├── JobRepository.java
│   │   │           │   └── WorkerRepository.java
│   │   │           ├── scheduler
│   │   │           │   ├── CronEvaluator.java
│   │   │           │   ├── JobPollingScheduler.java
│   │   │           │   └── PriorityJobQueue.java
│   │   │           ├── service
│   │   │           │   ├── JobService.java
│   │   │           │   ├── RetryService.java
│   │   │           │   └── SchedulerService.java
│   │   │           ├── util
│   │   │           │   └── CronUtil.java
│   │   │           ├── worker
│   │   │           │   ├── HeartbeatService.java
│   │   │           │   └── WorkerRegistrationService.java
│   │   │           └── SchedulerApplication.java
│   │   └── resources
│   │       ├── db
│   │       │   └── migration
│   │       │       ├── V1__create_jobs_table.sql
│   │       │       ├── V2__create_job_executions_table.sql
│   │       │       └── V3__create_workers_table.sql
│   │       └── application.yml
│   └── test
│       └── java
│           └── com
│               └── jobscheduler
│                   └── SchedulerApplicationTests.java
├── target
│   ├── classes
│   │   ├── com
│   │   │   └── jobscheduler
│   │   │       ├── config
│   │   │       │   ├── AsyncConfig.class
│   │   │       │   ├── SchedulerConfig.class
│   │   │       │   └── ThreadPoolConfig.class
│   │   │       ├── controller
│   │   │       │   ├── JobController.class
│   │   │       │   └── WorkerController.class
│   │   │       ├── dispatcher
│   │   │       │   ├── JobDispatcher.class
│   │   │       │   ├── TaskExecutor$TaskResult.class
│   │   │       │   └── TaskExecutor.class
│   │   │       ├── dto
│   │   │       │   ├── JobExecutionResponse$JobExecutionResponseBuilder.class
│   │   │       │   ├── JobExecutionResponse.class
│   │   │       │   ├── JobRequest$JobRequestBuilder.class
│   │   │       │   ├── JobRequest.class
│   │   │       │   ├── JobResponse$JobResponseBuilder.class
│   │   │       │   └── JobResponse.class
│   │   │       ├── entity
│   │   │       │   ├── ExecutionStatus.class
│   │   │       │   ├── Job$JobBuilder.class
│   │   │       │   ├── Job.class
│   │   │       │   ├── JobExecution$JobExecutionBuilder.class
│   │   │       │   ├── JobExecution.class
│   │   │       │   ├── JobStatus.class
│   │   │       │   ├── JobType.class
│   │   │       │   ├── Worker$WorkerBuilder.class
│   │   │       │   ├── Worker$WorkerStatus.class
│   │   │       │   └── Worker.class
│   │   │       ├── exception
│   │   │       │   ├── GlobalExceptionHandler.class
│   │   │       │   ├── InvalidCronExpressionException.class
│   │   │       │   └── JobNotFoundException.class
│   │   │       ├── mapper
│   │   │       │   └── JobMapper.class
│   │   │       ├── repository
│   │   │       │   ├── JobExecutionRepository.class
│   │   │       │   ├── JobRepository.class
│   │   │       │   └── WorkerRepository.class
│   │   │       ├── scheduler
│   │   │       │   ├── CronEvaluator.class
│   │   │       │   ├── JobPollingScheduler.class
│   │   │       │   └── PriorityJobQueue.class
│   │   │       ├── service
│   │   │       │   ├── JobService.class
│   │   │       │   ├── RetryService.class
│   │   │       │   └── SchedulerService.class
│   │   │       ├── util
│   │   │       │   └── CronUtil.class
│   │   │       ├── worker
│   │   │       │   ├── HeartbeatService.class
│   │   │       │   └── WorkerRegistrationService.class
│   │   │       └── SchedulerApplication.class
│   │   ├── db
│   │   │   └── migration
│   │   │       ├── V1__create_jobs_table.sql
│   │   │       ├── V2__create_job_executions_table.sql
│   │   │       └── V3__create_workers_table.sql
│   │   └── application.yml
│   ├── generated-sources
│   │   └── annotations
│   ├── generated-test-sources
│   │   └── test-annotations
│   └── test-classes
│       └── com
│           └── jobscheduler
│               └── SchedulerApplicationTests.class
├── .gitignore
├── CodebaseExtractor.py
├── pom.xml
└── README.md
```

## 📄 Source Code

### `.github\modernize\java-upgrade\.gitignore`

```text

**/*

```

### `.github\modernize\java-upgrade\hooks\scripts\recordToolUse.sh`

```bash
#!/usr/bin/env bash
# Records run_in_terminal and appmod-* tool calls as JSONL for the extension to process.

INPUT=$(cat)

TOOL_NAME="${INPUT#*\"tool_name\":\"}"
TOOL_NAME="${TOOL_NAME%%\"*}"

case "$TOOL_NAME" in
  run_in_terminal|appmod-*) ;;
  *) exit 0 ;;
esac

case "$INPUT" in
  *'"session_id":"'*) ;;
  *) exit 0 ;;
esac

SESSION_ID="${INPUT#*\"session_id\":\"}"
SESSION_ID="${SESSION_ID%%\"*}"
[ -z "$SESSION_ID" ] && exit 0

HOOKS_DIR=".github/modernize/java-upgrade/hooks"
mkdir -p "$HOOKS_DIR"

LINE=$(printf '%s' "$INPUT" | tr -d '\r\n')
printf '%s\n' "$LINE" >> "$HOOKS_DIR/${SESSION_ID}.json"

```

### `.gitignore`

```text
# Maven / Java
/target/
!.mvn/wrapper/maven-wrapper.jar

# IDE files
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db

# Logs
*.log

# Environment files
.env
*.env

# Temporary files
*.tmp
*.temp

```

### `CodebaseExtractor.py`

```python
from pathlib import Path
import os

# Files and folders to ignore
IGNORE_DIRS = {
    "node_modules",
    ".git",
    ".venv",
    "venv",
    "__pycache__",
    "dist",
    "build",
    ".idea",
    ".vscode",
}

IGNORE_FILES = {
    "generate_project_doc.py",
    "package-lock.json",
    "yarn.lock",
    "pnpm-lock.yaml",
}

# File extensions that are usually safe to include as code
CODE_EXTENSIONS = {
    ".py",
    ".js",
    ".jsx",
    ".ts",
    ".tsx",
    ".java",
    ".cpp",
    ".c",
    ".h",
    ".hpp",
    ".cs",
    ".go",
    ".rs",
    ".php",
    ".rb",
    ".swift",
    ".kt",
    ".html",
    ".css",
    ".scss",
    ".sass",
    ".json",
    ".xml",
    ".yaml",
    ".yml",
    ".md",
    ".txt",
    ".sql",
    ".sh",
    ".bat",
    ".env",
    ".gitignore",
    ".dockerfile",
}

OUTPUT_FILE = "PROJECT_DOCUMENTATION.md"


def get_language(file_path):
    extension = file_path.suffix.lower()

    languages = {
        ".py": "python",
        ".js": "javascript",
        ".jsx": "jsx",
        ".ts": "typescript",
        ".tsx": "tsx",
        ".java": "java",
        ".cpp": "cpp",
        ".c": "c",
        ".h": "c",
        ".hpp": "cpp",
        ".cs": "csharp",
        ".go": "go",
        ".rs": "rust",
        ".php": "php",
        ".rb": "ruby",
        ".swift": "swift",
        ".kt": "kotlin",
        ".html": "html",
        ".css": "css",
        ".scss": "scss",
        ".json": "json",
        ".xml": "xml",
        ".yaml": "yaml",
        ".yml": "yaml",
        ".sql": "sql",
        ".sh": "bash",
        ".bat": "bat",
        ".md": "markdown",
        ".txt": "text",
    }

    return languages.get(extension, "text")


def create_project_tree(root_path):
    tree_lines = []

    def walk(directory, prefix=""):
        try:
            items = sorted(
                [
                    item
                    for item in directory.iterdir()
                    if item.name not in IGNORE_DIRS
                    and item.name not in IGNORE_FILES
                ],
                key=lambda x: (x.is_file(), x.name.lower()),
            )
        except PermissionError:
            return

        for index, item in enumerate(items):
            is_last = index == len(items) - 1

            connector = "└── " if is_last else "├── "
            tree_lines.append(f"{prefix}{connector}{item.name}")

            if item.is_dir():
                new_prefix = prefix + ("    " if is_last else "│   ")
                walk(item, new_prefix)

    tree_lines.append(root_path.name)
    walk(root_path)

    return "\n".join(tree_lines)


def collect_files(root_path):
    files = []

    for path in root_path.rglob("*"):
        if not path.is_file():
            continue

        # Skip ignored directories
        if any(part in IGNORE_DIRS for part in path.parts):
            continue

        # Skip ignored files
        if path.name in IGNORE_FILES:
            continue

        # Skip generated documentation file
        if path.name == OUTPUT_FILE:
            continue

        # Only include recognized code/text files
        if path.suffix.lower() in CODE_EXTENSIONS or path.name in {
            "Dockerfile",
            ".gitignore",
            ".env",
        }:
            files.append(path)

    return sorted(files)


def read_file(file_path):
    try:
        return file_path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return "[Binary or non-UTF-8 file skipped]"
    except Exception as error:
        return f"[Could not read file: {error}]"


def generate_documentation():
    root_path = Path(__file__).parent.resolve()

    print("Scanning project...")

    project_tree = create_project_tree(root_path)
    project_files = collect_files(root_path)

    markdown = []

    # Title
    markdown.append(f"# {root_path.name} - Project Documentation\n")

    # Project tree
    markdown.append("## 📁 Project Structure\n")
    markdown.append("```text")
    markdown.append(project_tree)
    markdown.append("```\n")

    # File contents
    markdown.append("## 📄 Source Code\n")

    for file_path in project_files:
        relative_path = file_path.relative_to(root_path)
        code = read_file(file_path)
        language = get_language(file_path)

        markdown.append(f"### `{relative_path}`\n")
        markdown.append(f"```{language}")
        markdown.append(code)
        markdown.append("```\n")

    output_path = root_path / OUTPUT_FILE
    output_path.write_text("\n".join(markdown), encoding="utf-8")

    print(f"\n✅ Documentation generated successfully!")
    print(f"📄 File: {OUTPUT_FILE}")
    print(f"📊 Files included: {len(project_files)}")


if __name__ == "__main__":
    generate_documentation()
```

### `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.jobscheduler</groupId>
    <artifactId>distributed-job-scheduler</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>

        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Flyway -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Tests -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>1.19.7</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>

            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

        </plugins>
    </build>

</project>
```

### `README.md`

```markdown
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
```

### `src\main\java\com\jobscheduler\config\AsyncConfig.java`

```java
package com.jobscheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;

/**
 * @Async methods returning void (none currently, but future-proofing) or
 * whose CompletableFuture chain throws before a handler is attached would
 * otherwise silently swallow exceptions. This ensures they're at least logged.
 */
@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) ->
                log.error("Uncaught async exception in {}: {}", method.getName(), ex.getMessage(), ex);
    }
}

```

### `src\main\java\com\jobscheduler\config\SchedulerConfig.java`

```java
package com.jobscheduler.config;

import com.jobscheduler.worker.WorkerRegistrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Runs one-time startup wiring: registers this node in the `workers` table
 * as soon as the application context is ready, so HeartbeatService and
 * leader election have a row to operate on from the very first scheduled tick.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final WorkerRegistrationService workerRegistrationService;

    @PostConstruct
    public void init() {
        log.info("Registering this node in the worker registry on startup");
        workerRegistrationService.registerSelf();
    }
}

```

### `src\main\java\com\jobscheduler\config\ThreadPoolConfig.java`

```java
package com.jobscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Two distinct pools, deliberately separated:
 *
 *  - dispatcherExecutor: small pool that only does the *lightweight* work of
 *    claiming a job (DB update) and handing it to a worker thread. Never runs
 *    user task code, so it can't be starved by a slow/hanging job.
 *
 *  - workerExecutor: larger pool that actually executes task handler logic.
 *    Isolating this means a flood of slow jobs saturates only this pool,
 *    not the dispatch loop — the scheduler keeps claiming/scheduling even
 *    under worker back-pressure. This is the concrete implementation of the
 *    "Backpressure" concept from Phase 9.
 *
 * CallerRunsPolicy is used as the rejection policy: when both pool and queue
 * are full, the calling thread (the poller) executes the task itself, which
 * naturally slows down job intake instead of throwing away work or OOMing.
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "dispatcherExecutor")
    public ThreadPoolTaskExecutor dispatcherExecutor(
            @Value("${thread-pool.dispatcher-core-size}") int coreSize,
            @Value("${thread-pool.dispatcher-max-size}") int maxSize,
            @Value("${thread-pool.dispatcher-queue-capacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("dispatcher-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "workerExecutor")
    public ThreadPoolTaskExecutor workerExecutor(
            @Value("${thread-pool.worker-core-size}") int coreSize,
            @Value("${thread-pool.worker-max-size}") int maxSize,
            @Value("${thread-pool.worker-queue-capacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("worker-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

```

### `src\main\java\com\jobscheduler\controller\JobController.java`

```java
package com.jobscheduler.controller;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST surface for job management: creation, inspection, lifecycle
 * transitions, and execution history — the "REST API" and "Monitoring
 * Dashboard" backing endpoints from the functional requirements.
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> listJobs(
            @RequestParam(required = false) JobStatus status) {
        return ResponseEntity.ok(jobService.listJobs(status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<JobResponse> cancelJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.cancelJob(id));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<JobResponse> pauseJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.pauseJob(id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<JobResponse> resumeJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.resumeJob(id));
    }

    @GetMapping("/{id}/executions")
    public ResponseEntity<List<JobExecutionResponse>> getExecutionHistory(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getExecutionHistory(id));
    }
}

```

### `src\main\java\com\jobscheduler\controller\WorkerController.java`

```java
package com.jobscheduler.controller;

import com.jobscheduler.entity.Worker;
import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only visibility into the worker registry — powers the "Monitoring
 * Dashboard" requirement's cluster/worker health view. No mutation endpoints
 * are exposed here deliberately: worker lifecycle (register/heartbeat/leader
 * election) is internal, driven by WorkerRegistrationService, HeartbeatService,
 * and SchedulerService — not by external API calls.
 */
@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerRepository workerRepository;

    @GetMapping
    public List<Worker> listWorkers() {
        return workerRepository.findAll();
    }

    @GetMapping("/leader")
    public Worker getLeader() {
        return workerRepository.findByIsLeaderTrue().orElse(null);
    }
}

```

### `src\main\java\com\jobscheduler\dispatcher\JobDispatcher.java`

```java
package com.jobscheduler.dispatcher;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.service.RetryService;
import com.jobscheduler.worker.WorkerRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Bridges "a job was claimed" to "a job is actually running", and records
 * the full execution lifecycle into JobExecution rows.
 *
 * Runs on the dispatcherExecutor pool (@Async("dispatcherExecutor")) so that
 * a burst of claimed jobs doesn't block the polling thread itself — the
 * claim already happened in JobPollingScheduler/SchedulerService; this class
 * only handles running + recording the result.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobDispatcher {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final TaskExecutor taskExecutor;
    private final RetryService retryService;
    private final WorkerRegistrationService workerRegistrationService;

    @Async("dispatcherExecutor")
    public void dispatch(Job job) {
        String workerId = workerRegistrationService.selectAvailableWorker()
                .orElse("in-process-worker");

        int attemptNumber = job.getRetryCount() + 1;
        Instant leasedAt = Instant.now();

        JobExecution execution = JobExecution.builder()
                .jobId(job.getId())
                .workerId(workerId)
                .status(ExecutionStatus.RUNNING)
                .attemptNumber(attemptNumber)
                .leasedAt(leasedAt)
                .startedAt(Instant.now())
                .build();
        execution = executionRepository.save(execution);
        final Long executionId = execution.getId();

        Callable<String> taskLogic = () -> {
            // Placeholder for real task handler resolution/execution.
            // In a production system this would look up job.getTaskHandler()
            // in a registry (Spring bean map, plugin loader, RPC client, etc).
            log.info("Executing job {} ({}) on {}", job.getId(), job.getTaskHandler(), workerId);
            Thread.sleep(100); // simulate work
            return "OK";
        };

        taskExecutor.execute(job, taskLogic).thenAccept(result -> {
            Instant completedAt = Instant.now();
            long durationMs = completedAt.toEpochMilli() - leasedAt.toEpochMilli();

            JobExecution finished = executionRepository.findById(executionId).orElseThrow();
            finished.setCompletedAt(completedAt);
            finished.setDurationMs(durationMs);

            if (result.success()) {
                finished.setStatus(ExecutionStatus.SUCCEEDED);
                finished.setOutput(result.output());
                executionRepository.save(finished);
                retryService.handleSuccess(job);
            } else {
                boolean timedOut = "Execution timed out".equals(result.errorMessage());
                finished.setStatus(timedOut ? ExecutionStatus.TIMED_OUT : ExecutionStatus.FAILED);
                finished.setErrorMessage(result.errorMessage());
                finished.setStackTrace(result.stackTrace());
                executionRepository.save(finished);
                retryService.handleFailure(job, result.errorMessage());
            }
        });
    }
}

```

### `src\main\java\com\jobscheduler\dispatcher\TaskExecutor.java`

```java
package com.jobscheduler.dispatcher;

import com.jobscheduler.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executes the actual task handler logic for a Job on the workerExecutor pool,
 * wrapped with a hard timeout via CompletableFuture.orTimeout.
 *
 * In this educational implementation, "workers" run in-process as thread-pool
 * tasks rather than as separate physical worker nodes reachable over the
 * network — this keeps the concurrency model teachable without requiring
 * a message broker. WorkerRegistrationService + HeartbeatService still model
 * the *distributed* worker-node bookkeeping so the concepts transfer directly
 * to a real multi-node deployment (swap this class's internals for an RPC call).
 */
@Slf4j
@Component
public class TaskExecutor {

    private final Executor workerExecutor;

    public TaskExecutor(Executor workerExecutor) {
        this.workerExecutor = workerExecutor;
    }

    public record TaskResult(boolean success, String output, String errorMessage, String stackTrace) {
        static TaskResult ok(String output) {
            return new TaskResult(true, output, null, null);
        }

        static TaskResult failure(String message, String stackTrace) {
            return new TaskResult(false, null, message, stackTrace);
        }
    }

    /**
     * Runs the given job's task handler with a timeout. The actual handler
     * resolution (mapping taskHandler string -> executable logic) is
     * intentionally left as a simple registry lookup here — a real system
     * might resolve this to a Spring bean, a script, or an RPC target.
     */
    public CompletableFuture<TaskResult> execute(Job job, Callable<String> taskHandlerLogic) {
        CompletableFuture<TaskResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                String output = taskHandlerLogic.call();
                return TaskResult.ok(output);
            } catch (Exception ex) {
                log.error("Job {} execution failed: {}", job.getId(), ex.getMessage());
                return TaskResult.failure(ex.getMessage(), stackTraceOf(ex));
            }
        }, workerExecutor);

        return future
                .orTimeout(job.getTimeoutSeconds(), TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof TimeoutException || ex instanceof TimeoutException) {
                        log.warn("Job {} timed out after {}s", job.getId(), job.getTimeoutSeconds());
                        return TaskResult.failure("Execution timed out", null);
                    }
                    return TaskResult.failure(ex.getMessage(), stackTraceOf(ex));
                });
    }

    private String stackTraceOf(Throwable t) {
        java.io.StringWriter sw = new java.io.StringWriter();
        t.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}

```

### `src\main\java\com\jobscheduler\dto\JobExecutionResponse.java`

```java
package com.jobscheduler.dto;

import com.jobscheduler.entity.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Outbound representation of a single JobExecution row — powers the
 * "Execution History" / "Job Logs" endpoints and dashboard.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobExecutionResponse {

    private Long id;
    private Long jobId;
    private String workerId;
    private ExecutionStatus status;
    private Integer attemptNumber;
    private Instant leasedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Long durationMs;
    private String errorMessage;
    private String output;
    private Instant createdAt;
}

```

### `src\main\java\com\jobscheduler\dto\JobRequest.java`

```java
package com.jobscheduler.dto;

import com.jobscheduler.entity.JobType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Inbound payload for POST /api/jobs.
 * Kept deliberately flat rather than mirroring the Job entity 1:1 — clients
 * shouldn't need to know about internal fields like lockedBy/version/status.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "jobType is required")
    private JobType jobType;

    @NotBlank(message = "taskHandler is required")
    private String taskHandler;

    private String payload;

    /** Required if jobType == CRON */
    private String cronExpression;

    /** Required if jobType == RECURRING */
    private Long fixedIntervalSeconds;

    /** Required if jobType == ONE_TIME or DELAYED */
    private Instant scheduledAt;

    /** Only relevant for DELAYED: seconds from now until first execution */
    private Long delaySeconds;

    @Builder.Default
    private Integer priority = 0;

    @Min(value = 0, message = "maxRetries cannot be negative")
    @Builder.Default
    private Integer maxRetries = 5;

    @Min(value = 1, message = "timeoutSeconds must be at least 1")
    @Builder.Default
    private Integer timeoutSeconds = 60;
}

```

### `src\main\java\com\jobscheduler\dto\JobResponse.java`

```java
package com.jobscheduler.dto;

import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Outbound representation of a Job. Deliberately excludes internal
 * concurrency-control fields (version, lockedBy) from the public API surface,
 * but does expose lockedBy for admin/debug visibility on the dashboard.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String name;
    private JobType jobType;
    private String taskHandler;
    private String payload;
    private String cronExpression;
    private Long fixedIntervalSeconds;
    private Instant scheduledAt;
    private Instant nextRunAt;
    private JobStatus status;
    private Integer priority;
    private Integer maxRetries;
    private Integer retryCount;
    private Integer timeoutSeconds;
    private String lockedBy;
    private Instant lockedUntil;
    private Instant createdAt;
    private Instant updatedAt;
}

```

### `src\main\java\com\jobscheduler\entity\ExecutionStatus.java`

```java
package com.jobscheduler.entity;

/**
 * Status of a single execution attempt of a Job (JobExecution row).
 * A Job can have many JobExecution rows over its lifetime — one per attempt.
 *
 *   LEASED     -> claimed by a worker but not yet started
 *   RUNNING    -> actively executing on a worker
 *   SUCCEEDED  -> completed without error
 *   FAILED     -> threw an error / returned non-zero
 *   TIMED_OUT  -> exceeded max allowed execution duration
 *   ABANDONED  -> lease expired without heartbeat (worker likely crashed)
 */
public enum ExecutionStatus {
    LEASED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    TIMED_OUT,
    ABANDONED
}

```

### `src\main\java\com\jobscheduler\entity\Job.java`

```java
package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a schedulable unit of work.
 *
 * Design notes:
 * - `nextRunAt` is the single most important column for the scheduler:
 *   JobPollingScheduler queries "WHERE nextRunAt <= now AND status = PENDING"
 *   to find due jobs. It is indexed (see V1 migration).
 * - `version` enables optimistic locking so two scheduler nodes racing to
 *   claim the same job fail gracefully instead of double-dispatching it.
 * - `lockedBy` / `lockedUntil` implement a lease: once a node claims a job,
 *   other nodes must not touch it until the lease expires.
 */
@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_jobs_next_run_status", columnList = "next_run_at,status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    /** Fully-qualified task identifier the worker uses to look up executable logic. */
    @Column(name = "task_handler", nullable = false)
    private String taskHandler;

    /** Arbitrary JSON payload passed to the task handler at execution time. */
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "fixed_interval_seconds")
    private Long fixedIntervalSeconds;

    /** Used for ONE_TIME and DELAYED jobs. */
    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    /** Higher number = higher priority. Used by PriorityJobQueue ordering. */
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "max_retries", nullable = false)
    @Builder.Default
    private Integer maxRetries = 5;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "timeout_seconds", nullable = false)
    @Builder.Default
    private Integer timeoutSeconds = 60;

    /** Node ID of the scheduler that currently holds the lease on this job. */
    @Column(name = "locked_by")
    private String lockedBy;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Optimistic lock — prevents two scheduler nodes from double-claiming this row. */
    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = JobStatus.PENDING;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

```

### `src\main\java\com\jobscheduler\entity\JobExecution.java`

```java
package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * An immutable-ish audit trail row: one JobExecution per attempt of a Job.
 * This is what "Execution History" / "Job Logs" in the requirements refers to.
 *
 * Why separate from Job?
 * - Job holds *current* scheduling state (mutable, small).
 * - JobExecution holds *historical* attempt data (append-only, grows unbounded,
 *   so it's a natural candidate for partitioning/archival later — Phase 8/9).
 */
@Entity
@Table(name = "job_executions", indexes = {
        @Index(name = "idx_executions_job_id", columnList = "job_id"),
        @Index(name = "idx_executions_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "worker_id")
    private String workerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "leased_at")
    private Instant leasedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    /** Wall-clock duration of this attempt, filled in when it terminates. */
    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}

```

### `src\main\java\com\jobscheduler\entity\JobStatus.java`

```java
package com.jobscheduler.entity;

/**
 * Lifecycle states of a Job (not a single execution — the Job's overall state).
 *
 *   PENDING   -> job created, waiting for its next trigger time
 *   RUNNING   -> currently leased/dispatched to a worker
 *   COMPLETED -> terminal state for one-time jobs that finished successfully
 *   FAILED    -> exhausted all retry attempts
 *   CANCELLED -> user explicitly cancelled the job
 *   RETRY     -> execution failed, waiting for next backoff-delayed attempt
 *   TIMEOUT   -> execution exceeded its allowed duration
 *   PAUSED    -> user paused; scheduler skips it until resumed
 */
public enum JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    RETRY,
    TIMEOUT,
    PAUSED
}

```

### `src\main\java\com\jobscheduler\entity\JobType.java`

```java
package com.jobscheduler.entity;

/**
 * Determines how the scheduler computes a Job's next execution time.
 *
 *   ONE_TIME  -> executes once at a specific scheduledAt timestamp
 *   RECURRING -> executes every fixedIntervalSeconds after the previous run
 *   CRON      -> next run computed from a cron expression
 *   DELAYED   -> one-time execution after an initial delay from creation time
 */
public enum JobType {
    ONE_TIME,
    RECURRING,
    CRON,
    DELAYED
}

```

### `src\main\java\com\jobscheduler\entity\Worker.java`

```java
package com.jobscheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a worker node registered with the cluster.
 *
 * `lastHeartbeatAt` is the crux of failure detection: HeartbeatService updates
 * it periodically, and a background check (WorkerRegistrationService) marks
 * workers DEAD if lastHeartbeatAt is older than heartbeat-timeout-seconds.
 *
 * `isLeader` supports the simple lease-based leader election described in
 * Phase 6: exactly one worker/scheduler row should have isLeader=true at a time,
 * enforced via a conditional UPDATE (see SchedulerService).
 */
@Entity
@Table(name = "workers", indexes = {
        @Index(name = "idx_workers_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @Column(name = "worker_id")
    private String workerId;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkerStatus status;

    @Column(name = "max_concurrent_tasks", nullable = false)
    @Builder.Default
    private Integer maxConcurrentTasks = 10;

    @Column(name = "current_task_count", nullable = false)
    @Builder.Default
    private Integer currentTaskCount = 0;

    @Column(name = "is_leader", nullable = false)
    @Builder.Default
    private Boolean isLeader = false;

    @Column(name = "leader_lease_until")
    private Instant leaderLeaseUntil;

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @PrePersist
    void onCreate() {
        this.registeredAt = Instant.now();
        this.lastHeartbeatAt = Instant.now();
    }

    public enum WorkerStatus {
        ACTIVE,
        INACTIVE,
        DEAD
    }
}

```

### `src\main\java\com\jobscheduler\exception\GlobalExceptionHandler.java`

```java
package com.jobscheduler.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralizes error -> HTTP response mapping so controllers stay free of
 * try/catch noise. Response shape is a flat, consistent error envelope
 * that's easy to consume from the dashboard frontend.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(JobNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCronExpressionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCron(InvalidCronExpressionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

```

### `src\main\java\com\jobscheduler\exception\InvalidCronExpressionException.java`

```java
package com.jobscheduler.exception;

public class InvalidCronExpressionException extends RuntimeException {
    public InvalidCronExpressionException(String message) {
        super(message);
    }

    public InvalidCronExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

### `src\main\java\com\jobscheduler\exception\JobNotFoundException.java`

```java
package com.jobscheduler.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String message) {
        super(message);
    }
}

```

### `src\main\java\com\jobscheduler\mapper\JobMapper.java`

```java
package com.jobscheduler.mapper;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.exception.InvalidCronExpressionException;
import com.jobscheduler.util.CronUtil;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Converts between entities and DTOs, and centralizes the "compute initial
 * nextRunAt" logic for each JobType — kept here (not in the entity) to keep
 * Job a plain persistence model without scheduling-decision logic baked in.
 */
@Component
public class JobMapper {

    public Job toEntity(JobRequest request) {
        Instant now = Instant.now();
        Instant nextRunAt = computeInitialNextRunAt(request, now);

        return Job.builder()
                .name(request.getName())
                .jobType(request.getJobType())
                .taskHandler(request.getTaskHandler())
                .payload(request.getPayload())
                .cronExpression(request.getCronExpression())
                .fixedIntervalSeconds(request.getFixedIntervalSeconds())
                .scheduledAt(request.getScheduledAt())
                .nextRunAt(nextRunAt)
                .status(JobStatus.PENDING)
                .priority(request.getPriority())
                .maxRetries(request.getMaxRetries())
                .retryCount(0)
                .timeoutSeconds(request.getTimeoutSeconds())
                .build();
    }

    private Instant computeInitialNextRunAt(JobRequest request, Instant now) {
        return switch (request.getJobType()) {
            case ONE_TIME -> request.getScheduledAt() != null ? request.getScheduledAt() : now;
            case DELAYED -> {
                long delay = request.getDelaySeconds() != null ? request.getDelaySeconds() : 0;
                yield now.plusSeconds(delay);
            }
            case RECURRING -> {
                if (request.getFixedIntervalSeconds() == null) {
                    throw new IllegalArgumentException("fixedIntervalSeconds is required for RECURRING jobs");
                }
                yield now.plusSeconds(request.getFixedIntervalSeconds());
            }
            case CRON -> {
                if (request.getCronExpression() == null || request.getCronExpression().isBlank()) {
                    throw new InvalidCronExpressionException("cronExpression is required for CRON jobs");
                }
                CronUtil.validate(request.getCronExpression());
                yield CronUtil.nextExecutionTime(request.getCronExpression(), now);
            }
        };
    }

    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .jobType(job.getJobType())
                .taskHandler(job.getTaskHandler())
                .payload(job.getPayload())
                .cronExpression(job.getCronExpression())
                .fixedIntervalSeconds(job.getFixedIntervalSeconds())
                .scheduledAt(job.getScheduledAt())
                .nextRunAt(job.getNextRunAt())
                .status(job.getStatus())
                .priority(job.getPriority())
                .maxRetries(job.getMaxRetries())
                .retryCount(job.getRetryCount())
                .timeoutSeconds(job.getTimeoutSeconds())
                .lockedBy(job.getLockedBy())
                .lockedUntil(job.getLockedUntil())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public JobExecutionResponse toExecutionResponse(JobExecution execution) {
        return JobExecutionResponse.builder()
                .id(execution.getId())
                .jobId(execution.getJobId())
                .workerId(execution.getWorkerId())
                .status(execution.getStatus())
                .attemptNumber(execution.getAttemptNumber())
                .leasedAt(execution.getLeasedAt())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .durationMs(execution.getDurationMs())
                .errorMessage(execution.getErrorMessage())
                .output(execution.getOutput())
                .createdAt(execution.getCreatedAt())
                .build();
    }
}

```

### `src\main\java\com\jobscheduler\repository\JobExecutionRepository.java`

```java
package com.jobscheduler.repository;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findByJobIdOrderByCreatedAtDesc(Long jobId);

    Optional<JobExecution> findTopByJobIdOrderByAttemptNumberDesc(Long jobId);

    List<JobExecution> findByStatus(ExecutionStatus status);

    /**
     * Used by crash recovery: any execution still marked RUNNING/LEASED whose
     * parent job's lease has expired is stale and gets flipped to ABANDONED
     * so it doesn't pollute metrics or dashboards as "still running".
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE JobExecution e
           SET e.status = 'ABANDONED',
               e.completedAt = :now
           WHERE e.jobId = :jobId
             AND e.status IN ('LEASED', 'RUNNING')
           """)
    int markAbandoned(@Param("jobId") Long jobId, @Param("now") Instant now);

    long countByJobIdAndStatus(Long jobId, ExecutionStatus status);
}

```

### `src\main\java\com\jobscheduler\repository\JobRepository.java`

```java
package com.jobscheduler.repository;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Core polling query: finds jobs that are due to run and not currently leased.
     * Ordered by priority DESC so higher-priority jobs are claimed first within
     * a single poll batch — this is the DB-level half of PriorityJobQueue.
     *
     * PESSIMISTIC_WRITE here would serialize claims across nodes; instead we rely
     * on optimistic locking (Job.version) at the claim step (see SchedulerService)
     * so multiple nodes can read this query concurrently without blocking each other,
     * and only collide (rarely) at the actual claim UPDATE.
     */
    @Query("""
           SELECT j FROM Job j
           WHERE j.status = 'PENDING'
             AND j.nextRunAt <= :now
             AND (j.lockedUntil IS NULL OR j.lockedUntil <= :now)
           ORDER BY j.priority DESC, j.nextRunAt ASC
           """)
    List<Job> findDueJobs(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);

    /**
     * Atomic claim: only succeeds if the job is still unlocked/expired-lock at
     * write time. Returns number of rows updated — 0 means another node won the race.
     * This is the "lease" acquisition step referenced in Phase 4 (Task Leasing).
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Job j
           SET j.status = 'RUNNING',
               j.lockedBy = :nodeId,
               j.lockedUntil = :leaseUntil
           WHERE j.id = :jobId
             AND j.status = 'PENDING'
             AND (j.lockedUntil IS NULL OR j.lockedUntil <= :now)
           """)
    int claimJob(@Param("jobId") Long jobId,
                 @Param("nodeId") String nodeId,
                 @Param("now") Instant now,
                 @Param("leaseUntil") Instant leaseUntil);

    /** Finds jobs whose lease expired without the worker finishing — crash recovery (Phase 5). */
    @Query("""
           SELECT j FROM Job j
           WHERE j.status = 'RUNNING'
             AND j.lockedUntil < :now
           """)
    List<Job> findExpiredLeases(@Param("now") Instant now);

    List<Job> findByStatus(JobStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Job j WHERE j.id = :id")
    Job findByIdForUpdate(@Param("id") Long id);
}

```

### `src\main\java\com\jobscheduler\repository\WorkerRepository.java`

```java
package com.jobscheduler.repository;

import com.jobscheduler.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    List<Worker> findByStatus(Worker.WorkerStatus status);

    Optional<Worker> findByIsLeaderTrue();

    /**
     * Heartbeat update — cheap, frequent write. Kept as a single UPDATE
     * rather than load-modify-save to avoid optimistic lock contention
     * and unnecessary round trips (Phase 4 - Heartbeats).
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Worker w
           SET w.lastHeartbeatAt = :now, w.status = 'ACTIVE'
           WHERE w.workerId = :workerId
           """)
    int updateHeartbeat(@Param("workerId") String workerId, @Param("now") Instant now);

    @Query("""
           SELECT w FROM Worker w
           WHERE w.status = 'ACTIVE'
             AND w.lastHeartbeatAt < :threshold
           """)
    List<Worker> findStaleWorkers(@Param("threshold") Instant threshold);

    /**
     * Lease-based leader election (Phase 6): a node becomes leader only if
     * no one currently holds a valid lease, OR the existing lease has expired.
     * The WHERE clause makes this atomic and race-safe across nodes —
     * exactly one concurrent UPDATE can match a given expired/empty leader row.
     */
    @Modifying
    @Transactional
    @Query("""
           UPDATE Worker w
           SET w.isLeader = true, w.leaderLeaseUntil = :leaseUntil
           WHERE w.workerId = :candidateId
             AND NOT EXISTS (
                 SELECT 1 FROM Worker w2
                 WHERE w2.isLeader = true
                   AND w2.leaderLeaseUntil > :now
                   AND w2.workerId <> :candidateId
             )
           """)
    int tryAcquireLeadership(@Param("candidateId") String candidateId,
                              @Param("now") Instant now,
                              @Param("leaseUntil") Instant leaseUntil);

    @Modifying
    @Transactional
    @Query("UPDATE Worker w SET w.isLeader = false WHERE w.workerId = :workerId")
    void relinquishLeadership(@Param("workerId") String workerId);
}

```

### `src\main\java\com\jobscheduler\scheduler\CronEvaluator.java`

```java
package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobType;
import com.jobscheduler.util.CronUtil;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Computes the next `nextRunAt` for a Job *after* it has just executed,
 * based on its JobType. Called by SchedulerService once an execution
 * finishes successfully.
 *
 * ONE_TIME and DELAYED jobs return null — there's no next run, so
 * SchedulerService will transition them to COMPLETED instead of
 * rescheduling them.
 */
@Component
public class CronEvaluator {

    public Instant computeNextRunAt(Job job, Instant completedAt) {
        return switch (job.getJobType()) {
            case ONE_TIME, DELAYED -> null;
            case RECURRING -> completedAt.plusSeconds(job.getFixedIntervalSeconds());
            case CRON -> CronUtil.nextExecutionTime(job.getCronExpression(), completedAt);
        };
    }

    public boolean isRecurring(Job job) {
        return job.getJobType() == JobType.RECURRING || job.getJobType() == JobType.CRON;
    }
}

```

### `src\main\java\com\jobscheduler\scheduler\JobPollingScheduler.java`

```java
package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * The heartbeat of the whole scheduler: a fixed-delay polling loop that
 * scans the DB for due jobs and hands them off for dispatch.
 *
 * Design choice — Polling vs Push:
 * We poll rather than push because:
 *  1. It's simple and requires no extra infrastructure (no message broker).
 *  2. It naturally self-heals: if a poll cycle is missed (GC pause, node
 *     restart), the next cycle just picks up all due jobs — nothing is lost
 *     as long as nextRunAt was persisted.
 *  3. Trade-off: polling adds up to `poll-interval-ms` latency and constant
 *     DB load even when idle. Push-based systems (e.g. Kafka-triggered)
 *     avoid this but add operational complexity — a natural Phase 8+ upgrade.
 *
 * Only the elected leader should actually dispatch (see SchedulerService.isLeader())
 * to avoid every node in the cluster double-dispatching the same due jobs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobPollingScheduler {

    private final JobRepository jobRepository;
    private final SchedulerService schedulerService;
    private final PriorityJobQueue priorityJobQueue;

    @Scheduled(fixedDelayString = "${scheduler.poll-interval-ms}")
    public void pollAndDispatch() {
        if (!schedulerService.isLeader()) {
            log.trace("Not leader — skipping poll cycle");
            return;
        }

        Instant now = Instant.now();
        int batchSize = schedulerService.getBatchSize();

        List<Job> dueJobs = jobRepository.findDueJobs(now, PageRequest.of(0, batchSize));
        if (dueJobs.isEmpty()) {
            return;
        }

        log.info("Poll cycle found {} due job(s)", dueJobs.size());
        dueJobs.forEach(priorityJobQueue::offer);

        schedulerService.drainQueueAndDispatch(priorityJobQueue);
    }

    /**
     * Separate, less frequent cycle that recovers jobs whose worker lease
     * expired without completion — see Phase 5 (Crash Recovery).
     */
    @Scheduled(fixedDelayString = "${scheduler.poll-interval-ms}")
    public void recoverExpiredLeases() {
        if (!schedulerService.isLeader()) {
            return;
        }
        schedulerService.recoverExpiredLeases();
    }
}

```

### `src\main\java\com\jobscheduler\scheduler\PriorityJobQueue.java`

```java
package com.jobscheduler.scheduler;

import com.jobscheduler.entity.Job;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory priority queue used *within a single poll cycle* to order jobs
 * fetched from the DB before handing them to the dispatcher's thread pool.
 *
 * Why have this at all if the SQL query already ORDER BY priority DESC?
 * - The SQL ordering guarantees order *within one fetched page*. This queue
 *   is a teaching vehicle for how in-memory priority scheduling works, and
 *   gives us a single place to re-prioritize jobs if, e.g., a higher-priority
 *   job is pushed in in-process between poll cycles (see SchedulerService).
 * - It's thread-safe via an explicit lock since JobDispatcher's async workers
 *   pull from it concurrently.
 *
 * Complexity: offer/poll are O(log n); peek is O(1).
 */
@Component
public class PriorityJobQueue {

    private final PriorityQueue<Job> queue = new PriorityQueue<>(
            Comparator.comparing(Job::getPriority).reversed()
                    .thenComparing(Job::getNextRunAt)
    );

    private final ReentrantLock lock = new ReentrantLock();

    public void offer(Job job) {
        lock.lock();
        try {
            queue.offer(job);
        } finally {
            lock.unlock();
        }
    }

    public Job poll() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}

```

### `src\main\java\com\jobscheduler\SchedulerApplication.java`

```java
package com.jobscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // powers JobPollingScheduler's fixed-delay polling loop
@EnableAsync        // powers async job dispatch via TaskExecutor
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}

```

### `src\main\java\com\jobscheduler\service\JobService.java`

```java
package com.jobscheduler.service;

import com.jobscheduler.dto.JobExecutionResponse;
import com.jobscheduler.dto.JobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.exception.JobNotFoundException;
import com.jobscheduler.mapper.JobMapper;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application-level CRUD + lifecycle operations on Job (creation, cancel,
 * pause, resume, history lookup). Deliberately separate from
 * SchedulerService, which owns the *distributed coordination* concerns
 * (leader election, claiming, recovery) — this keeps a clean boundary
 * between "managing jobs as a resource" and "scheduling jobs across nodes".
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final JobMapper jobMapper;

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Job job = jobMapper.toEntity(request);
        Job saved = jobRepository.save(job);
        log.info("Created job {} (type={}, nextRunAt={})", saved.getId(), saved.getJobType(), saved.getNextRunAt());
        return jobMapper.toResponse(saved);
    }

    public JobResponse getJob(Long id) {
        Job job = findOrThrow(id);
        return jobMapper.toResponse(job);
    }

    public List<JobResponse> listJobs(JobStatus status) {
        List<Job> jobs = status != null ? jobRepository.findByStatus(status) : jobRepository.findAll();
        return jobs.stream().map(jobMapper::toResponse).toList();
    }

    @Transactional
    public JobResponse cancelJob(Long id) {
        Job job = findOrThrow(id);
        job.setStatus(JobStatus.CANCELLED);
        job.setNextRunAt(null);
        jobRepository.save(job);
        log.info("Job {} cancelled", id);
        return jobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse pauseJob(Long id) {
        Job job = findOrThrow(id);
        if (job.getStatus() != JobStatus.PENDING) {
            log.warn("Pausing job {} while in status {} (expected PENDING)", id, job.getStatus());
        }
        job.setStatus(JobStatus.PAUSED);
        jobRepository.save(job);
        return jobMapper.toResponse(job);
    }

    @Transactional
    public JobResponse resumeJob(Long id) {
        Job job = findOrThrow(id);
        if (job.getStatus() != JobStatus.PAUSED) {
            throw new IllegalStateException("Cannot resume a job that is not paused, id=" + id);
        }
        job.setStatus(JobStatus.PENDING);
        jobRepository.save(job);
        log.info("Job {} resumed", id);
        return jobMapper.toResponse(job);
    }

    public List<JobExecutionResponse> getExecutionHistory(Long jobId) {
        findOrThrow(jobId); // ensures 404 if job doesn't exist
        return executionRepository.findByJobIdOrderByCreatedAtDesc(jobId).stream()
                .map(jobMapper::toExecutionResponse)
                .toList();
    }

    private Job findOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + id));
    }
}

```

### `src\main\java\com\jobscheduler\service\RetryService.java`

```java
package com.jobscheduler.service;

import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.scheduler.CronEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implements retry-with-exponential-backoff and dead-letter routing.
 *
 * Backoff formula: base * 2^attempt, capped, so repeated failures don't
 * hammer a downstream dependency that's already struggling (thundering-herd
 * avoidance). Once retryCount exceeds maxRetries, the job is moved to
 * FAILED — conceptually our "dead letter queue" is simply
 * status=FAILED rows, queryable via JobController for inspection/replay,
 * rather than a separate physical queue. This keeps the DB as the single
 * source of truth, matching the rest of this project's design.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final JobRepository jobRepository;
    private final CronEvaluator cronEvaluator;

    @Value("${scheduler.retry-base-delay-seconds}")
    private long baseDelaySeconds;

    private static final long MAX_BACKOFF_SECONDS = 300; // 5 minute cap

    public void handleSuccess(Job job) {
        job.setRetryCount(0);

        if (cronEvaluator.isRecurring(job)) {
            Instant nextRun = cronEvaluator.computeNextRunAt(job, Instant.now());
            job.setNextRunAt(nextRun);
            job.setStatus(JobStatus.PENDING);
        } else {
            job.setStatus(JobStatus.COMPLETED);
            job.setNextRunAt(null);
        }
        clearLease(job);
        jobRepository.save(job);
    }

    public void handleFailure(Job job, String errorMessage) {
        int nextAttempt = job.getRetryCount() + 1;

        if (nextAttempt > job.getMaxRetries()) {
            log.error("Job {} exhausted {} retries — moving to FAILED (dead letter). Last error: {}",
                    job.getId(), job.getMaxRetries(), errorMessage);
            job.setStatus(JobStatus.FAILED);
            job.setNextRunAt(null);
            clearLease(job);
            jobRepository.save(job);
            return;
        }

        long backoffSeconds = Math.min(
                baseDelaySeconds * (1L << nextAttempt),
                MAX_BACKOFF_SECONDS
        );

        job.setRetryCount(nextAttempt);
        job.setStatus(JobStatus.PENDING);
        job.setNextRunAt(Instant.now().plusSeconds(backoffSeconds));
        clearLease(job);
        jobRepository.save(job);

        log.warn("Job {} failed (attempt {}/{}) — retrying in {}s. Error: {}",
                job.getId(), nextAttempt, job.getMaxRetries(), backoffSeconds, errorMessage);
    }

    private void clearLease(Job job) {
        job.setLockedBy(null);
        job.setLockedUntil(null);
    }
}

```

### `src\main\java\com\jobscheduler\service\SchedulerService.java`

```java
package com.jobscheduler.service;

import com.jobscheduler.entity.ExecutionStatus;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobStatus;
import com.jobscheduler.dispatcher.JobDispatcher;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import com.jobscheduler.repository.WorkerRepository;
import com.jobscheduler.scheduler.PriorityJobQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Owns three closely related distributed-systems responsibilities:
 *
 *  1. Leader election — only the elected node polls/dispatches (see
 *     JobPollingScheduler), preventing every node in the cluster from
 *     double-dispatching the same due jobs.
 *  2. Claiming due jobs (optimistic-lease acquisition) and draining the
 *     in-memory PriorityJobQueue to the dispatcher.
 *  3. Crash recovery — reclaiming jobs whose lease expired without
 *     completion (e.g. the node that claimed them crashed mid-execution).
 *
 * Leader election design (lease-based, DB-backed):
 * We don't use ZooKeeper/etcd/Consul here — that would be the production
 * choice, but implementing leader election via a conditional UPDATE against
 * the same PostgreSQL instance we already depend on teaches the *concept*
 * (lease + fencing via expiry) without adding a new piece of infrastructure.
 * Trade-off: our "lease" is not linearizable across network partitions the
 * way a proper consensus system (Raft/Paxos-based, e.g. etcd) would
 * guarantee — worth calling out explicitly as a limitation in interviews.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository executionRepository;
    private final WorkerRepository workerRepository;
    private final JobDispatcher jobDispatcher;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Value("${scheduler.lease-duration-seconds}")
    private long leaseDurationSeconds;

    private static final long LEADER_LEASE_SECONDS = 15;

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isLeader() {
        return workerRepository.findByIsLeaderTrue()
                .map(w -> w.getWorkerId().equals(nodeId))
                .orElse(false);
    }

    /**
     * Attempts to acquire (or renew) leadership via a lease. Called
     * frequently so a live leader keeps renewing before its lease expires;
     * if the leader crashes, its lease naturally expires and another node's
     * tryAcquireLeadership UPDATE will succeed (self-healing, no manual
     * failover step needed).
     */
    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void participateInLeaderElection() {
        Instant now = Instant.now();
        Instant leaseUntil = now.plusSeconds(LEADER_LEASE_SECONDS);
        int acquired = workerRepository.tryAcquireLeadership(nodeId, now, leaseUntil);
        if (acquired > 0) {
            log.debug("Node {} holds/renewed leadership until {}", nodeId, leaseUntil);
        }
    }

    /**
     * Attempts to claim each job in the queue using optimistic-lease UPDATE.
     * If claimJob() affects 0 rows, another node beat us to it — that job is
     * simply dropped from our local queue (it's still being processed
     * elsewhere; no data loss).
     */
    public void drainQueueAndDispatch(PriorityJobQueue queue) {
        Instant now = Instant.now();
        Instant leaseUntil = now.plusSeconds(leaseDurationSeconds);

        Job job;
        while ((job = queue.poll()) != null) {
            int claimed = jobRepository.claimJob(job.getId(), nodeId, now, leaseUntil);
            if (claimed == 0) {
                log.debug("Job {} already claimed by another node — skipping", job.getId());
                continue;
            }
            log.info("Node {} claimed job {} (priority={})", nodeId, job.getId(), job.getPriority());
            jobDispatcher.dispatch(job);
        }
    }

    /**
     * Crash recovery (Phase 5): jobs stuck in RUNNING whose lease has
     * expired are assumed abandoned (the node holding them likely crashed
     * or hung). We reset them to PENDING so the next poll cycle picks them
     * up again — at-least-once semantics, not exactly-once (see
     * RetryService/idempotency discussion for how duplicate execution risk
     * is mitigated).
     */
    public void recoverExpiredLeases() {
        Instant now = Instant.now();
        List<Job> expired = jobRepository.findExpiredLeases(now);
        for (Job job : expired) {
            log.warn("Recovering job {} — lease expired (was locked by {})", job.getId(), job.getLockedBy());
            executionRepository.markAbandoned(job.getId(), now);
            job.setStatus(JobStatus.PENDING);
            job.setLockedBy(null);
            job.setLockedUntil(null);
            jobRepository.save(job);
        }
    }
}

```

### `src\main\java\com\jobscheduler\util\CronUtil.java`

```java
package com.jobscheduler.util;

import com.jobscheduler.exception.InvalidCronExpressionException;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Thin wrapper around Spring's CronExpression so the rest of the codebase
 * doesn't need to know about java.time <-> Instant conversions.
 *
 * Why not write our own cron parser?
 * - Cron parsing is a solved, well-tested problem. Spring's CronExpression
 *   already supports standard 6-field (with seconds) cron syntax.
 * - Writing our own here would be reinventing a wheel that doesn't teach
 *   any new distributed-systems concept — the interesting part is *how we use*
 *   the next-fire-time, not how we parse "* * * * * *".
 */
public final class CronUtil {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private CronUtil() {
    }

    /** Validates a cron expression eagerly, e.g. at job-creation time. */
    public static void validate(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
        } catch (IllegalArgumentException ex) {
            throw new InvalidCronExpressionException(
                    "Invalid cron expression: " + cronExpression, ex);
        }
    }

    /** Computes the next fire time strictly after `from`. */
    public static Instant nextExecutionTime(String cronExpression, Instant from) {
        CronExpression expression = CronExpression.parse(cronExpression);
        LocalDateTime fromLocal = LocalDateTime.ofInstant(from, ZONE);
        LocalDateTime next = expression.next(fromLocal);
        if (next == null) {
            throw new InvalidCronExpressionException(
                    "Cron expression has no future execution time: " + cronExpression);
        }
        return next.atZone(ZONE).toInstant();
    }
}

```

### `src\main\java\com\jobscheduler\worker\HeartbeatService.java`

```java
package com.jobscheduler.worker;

import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Periodically refreshes this node's `lastHeartbeatAt` row so
 * WorkerRegistrationService's stale-check on other nodes doesn't mark it dead.
 *
 * Why a separate class from WorkerRegistrationService?
 * - Single Responsibility: registration/detection is a *reader* of heartbeat
 *   data across all workers; this is a *writer* of this node's own heartbeat.
 *   In a real multi-process deployment these run in different JVMs entirely.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatService {

    private final WorkerRepository workerRepository;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void sendHeartbeat() {
        int updated = workerRepository.updateHeartbeat(nodeId, Instant.now());
        if (updated == 0) {
            log.warn("Heartbeat update affected 0 rows — worker {} may not be registered", nodeId);
        } else {
            log.debug("Heartbeat sent for worker {}", nodeId);
        }
    }
}

```

### `src\main\java\com\jobscheduler\worker\WorkerRegistrationService.java`

```java
package com.jobscheduler.worker;

import com.jobscheduler.entity.Worker;
import com.jobscheduler.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Manages the worker registry: registration on startup, stale-worker
 * detection, and simple load-based selection for dispatch.
 *
 * Failure detection design: rather than workers actively announcing death
 * (impossible — a crashed process can't announce anything), we use a
 * timeout-based passive check: if `lastHeartbeatAt` hasn't been refreshed
 * within `heartbeat-timeout-seconds`, we *infer* the worker is dead. This is
 * the standard phi-accrual-lite approach used by most real systems (a fixed
 * timeout here; production systems like Cassandra use an adaptive phi-accrual
 * detector — worth knowing as an alternative design).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerRegistrationService {

    private final WorkerRepository workerRepository;

    @Value("${scheduler.node-id}")
    private String nodeId;

    @Value("${scheduler.heartbeat-timeout-seconds}")
    private long heartbeatTimeoutSeconds;

    public void registerSelf() {
        Worker self = Worker.builder()
                .workerId(nodeId)
                .hostname(resolveHostname())
                .status(Worker.WorkerStatus.ACTIVE)
                .maxConcurrentTasks(50)
                .currentTaskCount(0)
                .isLeader(false)
                .build();
        workerRepository.save(self);
        log.info("Worker {} registered", nodeId);
    }

    /**
     * Very simple least-loaded selection among ACTIVE workers.
     * A production system would also consider network locality, task
     * affinity, and resource requirements (CPU/memory) — noted here as
     * an extensibility point rather than implemented, to keep this
     * educational build focused on the core distributed-systems concepts.
     */
    public Optional<String> selectAvailableWorker() {
        List<Worker> activeWorkers = workerRepository.findByStatus(Worker.WorkerStatus.ACTIVE);
        return activeWorkers.stream()
                .filter(w -> w.getCurrentTaskCount() < w.getMaxConcurrentTasks())
                .min((a, b) -> Integer.compare(a.getCurrentTaskCount(), b.getCurrentTaskCount()))
                .map(Worker::getWorkerId);
    }

    /**
     * Runs periodically to mark workers DEAD if their heartbeat is stale.
     * Jobs that were leased to a now-dead worker get recovered separately
     * by SchedulerService.recoverExpiredLeases() based on the Job's own
     * lockedUntil lease, decoupling "worker health" from "job lease expiry".
     */
    @Scheduled(fixedDelayString = "${scheduler.heartbeat-interval-ms}")
    public void detectStaleWorkers() {
        Instant threshold = Instant.now().minusSeconds(heartbeatTimeoutSeconds);
        List<Worker> stale = workerRepository.findStaleWorkers(threshold);
        for (Worker worker : stale) {
            worker.setStatus(Worker.WorkerStatus.DEAD);
            workerRepository.save(worker);
            log.warn("Worker {} marked DEAD — no heartbeat since {}", worker.getWorkerId(), worker.getLastHeartbeatAt());
        }
    }

    private String resolveHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown-host";
        }
    }
}

```

### `src\main\resources\application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: distributed-job-scheduler

  datasource:
    url: jdbc:postgresql://localhost:5432/jobscheduler
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      pool-name: SchedulerHikariPool

  jpa:
    hibernate:
      ddl-auto: validate   # schema is owned by Flyway, not Hibernate
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 20
    show-sql: false
    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always

scheduler:
  node-id: ${SCHEDULER_NODE_ID:node-1}
  poll-interval-ms: 5000        # how often JobPollingScheduler scans for due jobs
  batch-size: 50                # max jobs fetched per poll cycle
  lease-duration-seconds: 30    # how long a claimed job lease is valid
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

logging:
  level:
    com.jobscheduler: DEBUG
    org.springframework.web: INFO

```

### `src\main\resources\db\migration\V1__create_jobs_table.sql`

```sql
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

```

### `src\main\resources\db\migration\V2__create_job_executions_table.sql`

```sql
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

```

### `src\main\resources\db\migration\V3__create_workers_table.sql`

```sql
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

```

### `src\test\java\com\jobscheduler\SchedulerApplicationTests.java`

```java
package com.jobscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke test: boots the full Spring context against a real Postgres
 * container (not H2) so Flyway migrations, JPA mappings, and scheduled
 * beans are all validated against the actual target database engine.
 */
@Testcontainers
@SpringBootTest
class SchedulerApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("jobscheduler_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // If the context fails to start (bad config, migration error,
        // missing bean), this test fails — the cheapest possible regression check.
    }
}

```

### `target\classes\application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: distributed-job-scheduler

  datasource:
    url: jdbc:postgresql://localhost:5432/jobscheduler
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      pool-name: SchedulerHikariPool

  jpa:
    hibernate:
      ddl-auto: validate   # schema is owned by Flyway, not Hibernate
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 20
    show-sql: false
    open-in-view: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always

scheduler:
  node-id: ${SCHEDULER_NODE_ID:node-1}
  poll-interval-ms: 5000        # how often JobPollingScheduler scans for due jobs
  batch-size: 50                # max jobs fetched per poll cycle
  lease-duration-seconds: 30    # how long a claimed job lease is valid
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

logging:
  level:
    com.jobscheduler: DEBUG
    org.springframework.web: INFO

```

### `target\classes\db\migration\V1__create_jobs_table.sql`

```sql
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

```

### `target\classes\db\migration\V2__create_job_executions_table.sql`

```sql
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

```

### `target\classes\db\migration\V3__create_workers_table.sql`

```sql
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

```
