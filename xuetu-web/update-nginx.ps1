# Update Nginx Configuration on Server 1
# Author: AI Assistant
# Date: 2025-10-26

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Update Nginx Configuration" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$SERVER1_IP = "8.141.106.92"
$SERVER1_USER = "root"

# Step 1: Upload nginx.conf
Write-Host "[1/2] Uploading nginx.conf to Server 1..." -ForegroundColor Yellow
scp .\nginx.conf ${SERVER1_USER}@${SERVER1_IP}:/root/xuetu-web/nginx.conf

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Upload successful" -ForegroundColor Green
} else {
    Write-Host "  ✗ Upload failed" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Step 2: Restart Nginx container
Write-Host "[2/2] Restarting Nginx container..." -ForegroundColor Yellow
ssh ${SERVER1_USER}@${SERVER1_IP} "docker restart xuetu-web"

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Nginx restarted successfully" -ForegroundColor Green
} else {
    Write-Host "  ✗ Restart failed" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Nginx configuration updated!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Test image access:" -ForegroundColor Cyan
Write-Host "  http://${SERVER1_IP}/media/covers/Java零基础.jpg" -ForegroundColor White
Write-Host ""
