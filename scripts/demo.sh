#!/usr/bin/env bash
# Distributed Job Scheduler demo
# Run from Git Bash, WSL, or another Bash shell with Docker Compose, curl, and Python 3.
# Usage: bash scripts/demo.sh
#
# This demo:
#   1. Starts the cluster
#   2. Waits for scheduler-1 and scheduler-2 to become healthy
#   3. Shows cluster status and workers
#   4. Creates a one-time LOG job and waits for completion
#   5. Stops scheduler-1 and demonstrates scheduler-2 taking leadership
#   6. Creates another job after failover
#   7. Restarts scheduler-1 and prints final cluster/job status

set -Eeuo pipefail

SCHEDULER1="http://localhost:8081"
SCHEDULER2="http://localhost:8082"
COMPOSE_FILE="docker-compose.yml"

# Run this script from the repository root, or pass the repository path as an argument.
if [[ -n "${1:-}" ]]; then
  cd "$1"
fi

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "ERROR: $COMPOSE_FILE not found."
  echo "Run this script from the project root, or pass the project path:"
  echo "  bash scripts/demo.sh /path/to/distributed-job-scheduler"
  exit 1
fi

section() {
  printf '\n\n============================================================\n'
  printf ' %s\n' "$1"
  printf '============================================================\n'
}

wait_for_health() {
  local url="$1"
  local label="$2"
  local attempts=40

  printf 'Waiting for %s' "$label"
  for ((i=1; i<=attempts; i++)); do
    if curl -fsS "$url/actuator/health" >/dev/null 2>&1; then
      echo " ready"
      return 0
    fi
    printf '.'
    sleep 2
  done

  echo
  echo "ERROR: $label did not become healthy."
  echo "Check logs with: docker compose logs --tail=100"
  return 1
}

get_json_field() {
  # Reads JSON from stdin and prints a top-level field using Python 3.
  local field="$1"
  python -c 'import json,sys; print(json.load(sys.stdin).get(sys.argv[1], ""))' "$field"
}

wait_for_job_completion() {
  local base_url="$1"
  local job_id="$2"
  local attempts=30
  local response status

  for ((i=1; i<=attempts; i++)); do
    response="$(curl -fsS "$base_url/api/jobs/$job_id")"
    status="$(printf '%s' "$response" | get_json_field status)"
    printf '  Job status: %s\n' "${status:-unknown}"

    if [[ "$status" == "COMPLETED" ]]; then
      return 0
    fi
    if [[ "$status" == "FAILED" ]]; then
      echo "Job failed. Response:"
      printf '%s\n' "$response"
      return 1
    fi
    sleep 2
  done

  echo "Timed out waiting for job $job_id."
  curl -sS "$base_url/api/jobs/$job_id" || true
  return 1
}

create_log_job() {
  local base_url="$1"
  local name="$2"
  local message="$3"
  local response job_id

  response="$(curl -fsS -X POST "$base_url/api/jobs" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"$name\",\"handlerType\":\"LOG\",\"payload\":{\"message\":\"$message\"},\"scheduleType\":\"ONE_TIME\",\"priority\":5,\"maxRetries\":2,\"backoffSeconds\":2,\"timeoutSeconds\":30}")"

  echo "Create-job response:"
  printf '%s\n' "$response"

  job_id="$(printf '%s' "$response" | get_json_field id)"
  if [[ -z "$job_id" ]]; then
    echo "ERROR: Could not read the job id from the API response."
    return 1
  fi

  echo "$job_id"
}

section "1. Start the cluster"
docker compose up -d --build
docker compose ps

section "2. Wait for both schedulers to be healthy"
wait_for_health "$SCHEDULER1" "scheduler-1"
wait_for_health "$SCHEDULER2" "scheduler-2"

section "3. Show cluster status and registered workers"
echo "--- Cluster status (scheduler-1) ---"
curl -fsS "$SCHEDULER1/api/cluster/status"
echo
echo "--- Workers (scheduler-2) ---"
curl -fsS "$SCHEDULER2/api/cluster/workers"
echo

section "4. Create a job and verify execution"
job_response="$(create_log_job "$SCHEDULER1" "demo-before-failover" "Hello from the scheduler demo")"
job_id="$(printf '%s\n' "$job_response" | tail -n 1)"
echo "Created job ID: $job_id"
wait_for_job_completion "$SCHEDULER1" "$job_id"

section "5. Stop scheduler-1 to demonstrate leader failover"
echo "Stopping scheduler-1..."
docker compose stop scheduler-1

echo "Waiting for scheduler-2 to acquire an active leader lease..."
failover_ok=0
for ((i=1; i<=30; i++)); do
  leader_json="$(curl -fsS "$SCHEDULER2/api/cluster/leader" 2>/dev/null || true)"
  leader_id="$(printf '%s' "$leader_json" | python -c 'import json,sys; print(json.load(sys.stdin).get("nodeId", ""))' 2>/dev/null || true)"
  lease_active="$(printf '%s' "$leader_json" | python -c 'import json,sys; print(str(json.load(sys.stdin).get("leaseActive", "")).lower())' 2>/dev/null || true)"
  term="$(printf '%s' "$leader_json" | python -c 'import json,sys; print(json.load(sys.stdin).get("term", ""))' 2>/dev/null || true)"

  printf '  Leader: %s | term: %s | lease active: %s\n' "${leader_id:-unknown}" "${term:-unknown}" "${lease_active:-unknown}"
  if [[ "$leader_id" == "scheduler-2" && "$lease_active" == "true" ]]; then
    failover_ok=1
    break
  fi
  sleep 2
done

if [[ "$failover_ok" -ne 1 ]]; then
  echo "ERROR: scheduler-2 did not become active leader in time."
  echo "Inspect logs: docker compose logs --tail=150 scheduler-2"
  exit 1
fi

section "6. Create a job after failover"
job_response="$(create_log_job "$SCHEDULER2" "demo-after-failover" "Job executed after scheduler failover")"
job_id="$(printf '%s\n' "$job_response" | tail -n 1)"
echo "Created job ID: $job_id"
wait_for_job_completion "$SCHEDULER2" "$job_id"

section "7. Show all jobs and current cluster status"
echo "--- All jobs ---"
curl -fsS "$SCHEDULER2/api/jobs"
echo
echo
echo "--- Leader ---"
curl -fsS "$SCHEDULER2/api/cluster/leader"
echo
echo
echo "--- Cluster status ---"
curl -fsS "$SCHEDULER2/api/cluster/status"
echo

section "8. Restart scheduler-1"
docker compose start scheduler-1
wait_for_health "$SCHEDULER1" "scheduler-1"
echo "--- Final containers ---"
docker compose ps

section "Demo complete"
echo "The demo verified basic job execution and scheduler leader failover."
echo "Containers are still running. To stop the project later, run:"
echo "  docker compose down"
echo "To also delete stored database volume data, use docker compose down -v (destructive)."
