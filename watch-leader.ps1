# watch-leader.ps1
# Continuously polls /api/workers/leader every 2 seconds so you can watch
# leadership move when you kill a node's terminal window.
# Usage: .\watch-leader.ps1
# Then, in another window, Ctrl+C the current leader's terminal and watch this output.

$ports = @(8080, 8081, 8082)

function Get-FirstLiveNode {
    foreach ($port in $ports) {
        try {
            Invoke-RestMethod -Uri "http://localhost:$port/api/workers" -Method Get -TimeoutSec 2 | Out-Null
            return $port
        } catch { continue }
    }
    return $null
}

Write-Host "Watching leader election. Press Ctrl+C to stop." -ForegroundColor Cyan
Write-Host ""

$lastLeader = $null

while ($true) {
    $port = Get-FirstLiveNode
    if ($null -eq $port) {
        Write-Host "$(Get-Date -Format 'HH:mm:ss') - no nodes reachable" -ForegroundColor Red
        Start-Sleep -Seconds 2
        continue
    }

    try {
        $leader = Invoke-RestMethod -Uri "http://localhost:$port/api/workers/leader" -Method Get -TimeoutSec 2
    } catch {
        $leader = $null
    }

    $currentLeader = if ($null -eq $leader) { "(none)" } else { $leader.workerId }

    if ($currentLeader -ne $lastLeader) {
        Write-Host "$(Get-Date -Format 'HH:mm:ss') - LEADER CHANGED -> $currentLeader" -ForegroundColor Green
        $lastLeader = $currentLeader
    } else {
        Write-Host "$(Get-Date -Format 'HH:mm:ss') - leader: $currentLeader" -ForegroundColor DarkGray
    }

    Start-Sleep -Seconds 2
}
