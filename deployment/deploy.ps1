# Xuetu Project - Multi-Server Deployment Script
# Server IPs Configuration
$Server1IP = "8.141.106.92"
$Server2IP = "112.126.85.23"
$Server3IP = "8.140.224.117"

# Set UTF-8 Encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

$ErrorActionPreference = "Stop"

# Auto detect project root
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir

# Change to project root
if ((Get-Location).Path -ne $projectRoot) {
    Write-Host "Switching to project root: $projectRoot" -ForegroundColor Gray
    Set-Location $projectRoot
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Xuetu Deployment Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Project Root: $projectRoot" -ForegroundColor Gray
Write-Host ""
Write-Host "Server 1: $Server1IP" -ForegroundColor Yellow
Write-Host "  MySQL, Redis, Nacos, Gateway, Nginx, Portainer" -ForegroundColor Gray
Write-Host ""
Write-Host "Server 2: $Server2IP" -ForegroundColor Yellow
Write-Host "  User, Course, Learning Services" -ForegroundColor Gray
Write-Host ""
Write-Host "Server 3: $Server3IP" -ForegroundColor Yellow
Write-Host "  Order, AI, Admin Services" -ForegroundColor Gray
Write-Host ""

# Step 1: Compile
Write-Host "[Step 1/7] Compiling..." -ForegroundColor Green
Write-Host "Cleaning..." -ForegroundColor Gray
mvn clean -q

Write-Host "Building (skip tests)..." -ForegroundColor Gray
mvn package -DskipTests -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

# Step 2: Prepare files
Write-Host "`n[Step 2/7] Preparing files..." -ForegroundColor Green

$deployDir = "deployment"
$jarsDir = "$deployDir/jars"
$logsDir = "$deployDir/logs"

if (Test-Path $jarsDir) { Remove-Item $jarsDir -Recurse -Force }
if (Test-Path $logsDir) { Remove-Item $logsDir -Recurse -Force }

New-Item -ItemType Directory -Path $jarsDir -Force | Out-Null
New-Item -ItemType Directory -Path $logsDir -Force | Out-Null

Write-Host "Copying JAR files..." -ForegroundColor Gray
Copy-Item "gateway/target/gateway-1.0-SNAPSHOT.jar" "$jarsDir/gateway.jar" -Force
Copy-Item "user-service/target/user-service-1.0-SNAPSHOT.jar" "$jarsDir/user-service.jar" -Force
Copy-Item "course-service/target/course-service-1.0-SNAPSHOT.jar" "$jarsDir/course-service.jar" -Force
Copy-Item "learning-service/target/learning-service-1.0-SNAPSHOT.jar" "$jarsDir/learning-service.jar" -Force
Copy-Item "order-service/target/order-service-1.0-SNAPSHOT.jar" "$jarsDir/order-service.jar" -Force
Copy-Item "ai-service/target/ai-service-1.0-SNAPSHOT.jar" "$jarsDir/ai-service.jar" -Force
Copy-Item "admin-service/target/admin-service-1.0-SNAPSHOT.jar" "$jarsDir/admin-service.jar" -Force

Write-Host "JAR files ready!" -ForegroundColor Green

# Step 3: Upload to Server 1
Write-Host "`n[Step 3/7] Uploading to Server 1 ($Server1IP)..." -ForegroundColor Green
Write-Host "Uploading JAR..." -ForegroundColor Gray
scp $jarsDir/gateway.jar root@${Server1IP}:/root/xuetu/jars/

Write-Host "Uploading docker-compose..." -ForegroundColor Gray
scp deployment/docker-compose-server1.yml root@${Server1IP}:/root/xuetu/docker-compose.yml

# Step 4: Upload to Server 2
Write-Host "`n[Step 4/7] Uploading to Server 2 ($Server2IP)..." -ForegroundColor Green
ssh root@$Server2IP "mkdir -p /root/xuetu/jars /root/xuetu/logs"

Write-Host "Uploading JARs..." -ForegroundColor Gray
scp $jarsDir/user-service.jar root@${Server2IP}:/root/xuetu/jars/
scp $jarsDir/course-service.jar root@${Server2IP}:/root/xuetu/jars/
scp $jarsDir/learning-service.jar root@${Server2IP}:/root/xuetu/jars/

Write-Host "Uploading docker-compose..." -ForegroundColor Gray
scp deployment/docker-compose-server2.yml root@${Server2IP}:/root/xuetu/docker-compose.yml

# Step 5: Upload to Server 3
Write-Host "`n[Step 5/7] Uploading to Server 3 ($Server3IP)..." -ForegroundColor Green
ssh root@$Server3IP "mkdir -p /root/xuetu/jars /root/xuetu/logs"

Write-Host "Uploading JARs..." -ForegroundColor Gray
scp $jarsDir/order-service.jar root@${Server3IP}:/root/xuetu/jars/
scp $jarsDir/ai-service.jar root@${Server3IP}:/root/xuetu/jars/
scp $jarsDir/admin-service.jar root@${Server3IP}:/root/xuetu/jars/

Write-Host "Uploading docker-compose..." -ForegroundColor Gray
scp deployment/docker-compose-server3.yml root@${Server3IP}:/root/xuetu/docker-compose.yml

# Step 6: Start services
Write-Host "`n[Step 6/7] Starting services..." -ForegroundColor Green

Write-Host "Server 1: Starting infrastructure..." -ForegroundColor Yellow
ssh root@$Server1IP "cd /root/xuetu && docker compose up -d"

Write-Host "`nWaiting for Nacos (30s)..." -ForegroundColor Gray
Start-Sleep -Seconds 30

Write-Host "`nServer 2: Starting services..." -ForegroundColor Yellow
ssh root@$Server2IP "cd /root/xuetu && docker compose up -d"

Write-Host "`nServer 3: Starting services..." -ForegroundColor Yellow
ssh root@$Server3IP "cd /root/xuetu && docker compose up -d"

Write-Host "`nWaiting for services (20s)..." -ForegroundColor Gray
Start-Sleep -Seconds 20

# Step 7: Check status
Write-Host "`n[Step 7/7] Checking status..." -ForegroundColor Green

Write-Host "`nServer 1 Status:" -ForegroundColor Yellow
ssh root@$Server1IP "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

Write-Host "`nServer 2 Status:" -ForegroundColor Yellow
ssh root@$Server2IP "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

Write-Host "`nServer 3 Status:" -ForegroundColor Yellow
ssh root@$Server3IP "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Deployment Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Access URLs:" -ForegroundColor Green
Write-Host "  Frontend:        http://$Server1IP" -ForegroundColor White
Write-Host "  API Gateway:     http://$Server1IP:8080" -ForegroundColor White
Write-Host "  Nacos Console:   http://$Server1IP:8848/nacos (nacos/nacos)" -ForegroundColor White
Write-Host "  Portainer:       http://$Server1IP:9000" -ForegroundColor White
Write-Host ""
Write-Host "View Logs:" -ForegroundColor Yellow
Write-Host "  Server 1: ssh root@$Server1IP 'docker logs -f xuetu-gateway'" -ForegroundColor Gray
Write-Host "  Server 2: ssh root@$Server2IP 'docker logs -f xuetu-user-service'" -ForegroundColor Gray
Write-Host "  Server 3: ssh root@$Server3IP 'docker logs -f xuetu-order-service'" -ForegroundColor Gray
Write-Host ""
