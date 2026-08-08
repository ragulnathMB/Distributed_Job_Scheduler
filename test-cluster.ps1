# test-cluster.ps1
# Smoke-tests a running distributed-job-scheduler cluster.
# Usage: .\test-cluster.ps1
# Assumes nodes are running on 8080, 8081, 8082 (edit $ports below if different).

$ports = @(8080, 8081, 8082)

function Write-Section($title) {
    Write-Host ""
    Write-Host "==== $title ====" -ForegroundColor Cyan
}

# ---------------------------------------------------------------
# 1. Check each node is up and responding
# ---------------------------------------------------------------
Write-Section "1. Checking each node is reachable"
$liveNodes = @()
foreach ($port in $ports) {
    try {
        $resp = Invoke-RestMethod -Uri "http://localhost:$port/api/workers" -Method Get -TimeoutSec 3
        Write-Host "  Port $port : UP  ($($resp.Count) worker rows visible)" -ForegroundColor Green
        $liveNodes += $port
    } catch {
        Write-Host "  Port $port : DOWN or unreachable" -ForegroundColor DarkGray
    }
}

if ($liveNodes.Count -eq 0) {
    Write-Host "`nNo nodes reachable. Start the cluster first (scripts\start-cluster.bat)." -ForegroundColor Red
    exit 1
}

$anyPort = $liveNodes[0]

# ---------------------------------------------------------------
# 2. List all registered workers (from any node - they share the DB)
# ---------------------------------------------------------------
Write-Section "2. Worker registry (via port $anyPort)"
$workers = Invoke-RestMethod -Uri "http://localhost:$anyPort/api/workers" -Method Get
$workers | ForEach-Object {
    $leaderTag = if ($_.isLeader) { " <-- LEADER" } else { "" }
    Write-Host ("  {0,-10} status={1,-8} heartbeat={2}{3}" -f $_.workerId, $_.status, $_.lastHeartbeatAt, $leaderTag)
}

# ---------------------------------------------------------------
# 3. Confirm exactly one leader
# ---------------------------------------------------------------
Write-Section "3. Leader check"
$leader = Invoke-RestMethod -Uri "http://localhost:$anyPort/api/workers/leader" -Method Get
if ($null -eq $leader) {
    Write-Host "  No leader currently elected (mid-election gap, or all nodes just started)." -ForegroundColor Yellow
} else {
    Write-Host "  Current leader: $($leader.workerId)" -ForegroundColor Green
}

# ---------------------------------------------------------------
# 4. Create a test job (ONE_TIME, fires immediately)
# ---------------------------------------------------------------
Write-Section "4. Creating a test job"
$body = @{
    name        = "smoke-test-$(Get-Date -Format 'HHmmss')"
    jobType     = "ONE_TIME"
    taskHandler = "demo-handler"
    payload     = "hello from test-cluster.ps1"
} | ConvertTo-Json

$job = Invoke-RestMethod -Uri "http://localhost:$anyPort/api/jobs" -Method Post -Body $body -ContentType "application/json"
Write-Host "  Created job id=$($job.id) name=$($job.name) status=$($job.status) nextRunAt=$($job.nextRunAt)" -ForegroundColor Green

# ---------------------------------------------------------------
# 5. Poll until it completes (or timeout)
# ---------------------------------------------------------------
Write-Section "5. Polling job status until it completes"
$maxWaitSeconds = 30
$elapsed = 0
$pollIntervalSeconds = 2
$finalStatuses = @("COMPLETED", "FAILED", "CANCELLED")

do {
    Start-Sleep -Seconds $pollIntervalSeconds
    $elapsed += $pollIntervalSeconds
    $current = Invoke-RestMethod -Uri "http://localhost:$anyPort/api/jobs/$($job.id)" -Method Get
    Write-Host "  [$($elapsed)s] status=$($current.status) retryCount=$($current.retryCount) lockedBy=$($current.lockedBy)"
} while ($current.status -notin $finalStatuses -and $elapsed -lt $maxWaitSeconds)

if ($current.status -in $finalStatuses) {
    Write-Host "  Job reached terminal state: $($current.status)" -ForegroundColor Green
} else {
    Write-Host "  Timed out waiting for job to complete (still $($current.status) after ${maxWaitSeconds}s)." -ForegroundColor Yellow
}

# ---------------------------------------------------------------
# 6. Show execution history for this job
# ---------------------------------------------------------------
Write-Section "6. Execution history"
$history = Invoke-RestMethod -Uri "http://localhost:$anyPort/api/jobs/$($job.id)/executions" -Method Get
if ($history.Count -eq 0) {
    Write-Host "  No execution rows yet." -ForegroundColor Yellow
} else {
    $history | ForEach-Object {
        Write-Host ("  attempt={0} worker={1} status={2} durationMs={3} output={4} error={5}" -f `
            $_.attemptNumber, $_.workerId, $_.status, $_.durationMs, $_.output, $_.errorMessage)
    }
}

Write-Host ""
Write-Host "Done." -ForegroundColor Cyan
