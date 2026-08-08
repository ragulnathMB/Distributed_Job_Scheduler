@echo off
REM Usage: start-node.bat <port> <node-id>
REM Example: start-node.bat 8080 node-1

set PORT=%1
set NODEID=%2

if "%PORT%"=="" (
    echo Usage: start-node.bat ^<port^> ^<node-id^>
    exit /b 1
)

echo Starting %NODEID% on port %PORT% ...
java -jar target\distributed-job-scheduler-1.0.0.jar --server.port=%PORT% --scheduler.node-id=%NODEID%