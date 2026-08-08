@echo off
echo Starting 3-node cluster...

start "node-1 (8080)" cmd /k scripts\start-node.bat 8080 node-1
timeout /t 2 >nul
start "node-2 (8081)" cmd /k scripts\start-node.bat 8081 node-2
timeout /t 2 >nul
start "node-3 (8082)" cmd /k scripts\start-node.bat 8082 node-3

echo All 3 nodes launching in separate windows.
echo Check leader with: curl http://localhost:8080/api/workers/leader