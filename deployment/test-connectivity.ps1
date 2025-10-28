# -*- coding: utf-8 -*-
key义# 测试各服务器连通性脚本

Write-Host "================================" -ForegroundColor Cyan
Write-Host "开始测试各服务器连通性" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 服务器配置
$server1 = "8.141.106.92"
$server2 = "112.126.85.23"
$server3 = "8.140.224.117"

# 测试函数
function Test-ServiceHealth {
    param(
        [string]$ServerName,
        [string]$ServerIP,
        [string]$ServiceName,
        [int]$Port,
        [string]$HealthPath = "/actuator/health"
    )
    
    $url = "http://${ServerIP}:${Port}${HealthPath}"
    Write-Host "测试 [$ServerName] $ServiceName (端口 $Port)..." -NoNewline
    
    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host " OK 正常" -ForegroundColor Green
            $content = $response.Content
            if ($content.Length -gt 100) {
                $content = $content.Substring(0, 100) + "..."
            }
            Write-Host "  响应: $content" -ForegroundColor Gray
            return $true
        }
        else {
            Write-Host " FAIL 异常 (状态码: $($response.StatusCode))" -ForegroundColor Red
            return $false
        }
    }
    catch {
        Write-Host " FAIL 失败" -ForegroundColor Red
        Write-Host "  错误: $($_.Exception.Message)" -ForegroundColor Yellow
        return $false
    }
}

# 测试结果统计
$server1Results = @{}
$server2Results = @{}
$server3Results = @{}

Write-Host ""
Write-Host "======== 服务器1 ($server1) ========" -ForegroundColor Cyan
Write-Host "基础设施服务测试:" -ForegroundColor Yellow
Write-Host ""

# Nacos
$server1Results["Nacos"] = Test-ServiceHealth "服务器1" $server1 "Nacos" 8848 "/nacos"
Start-Sleep -Milliseconds 500

# Gateway
$server1Results["Gateway"] = Test-ServiceHealth "服务器1" $server1 "Gateway" 80 "/actuator/health"
Start-Sleep -Milliseconds 500

Write-Host ""
Write-Host "======== 服务器2 ($server2) ========" -ForegroundColor Cyan
Write-Host "业务服务测试:" -ForegroundColor Yellow
Write-Host ""

# User Service
$server2Results["User"] = Test-ServiceHealth "服务器2" $server2 "User Service" 8022 "/actuator/health"
Start-Sleep -Milliseconds 500

# Course Service
$server2Results["Course"] = Test-ServiceHealth "服务器2" $server2 "Course Service" 8077 "/actuator/health"
Start-Sleep -Milliseconds 500

# Learning Service
$server2Results["Learning"] = Test-ServiceHealth "服务器2" $server2 "Learning Service" 8044 "/actuator/health"
Start-Sleep -Milliseconds 500

Write-Host ""
Write-Host "======== 服务器3 ($server3) ========" -ForegroundColor Cyan
Write-Host "业务服务测试:" -ForegroundColor Yellow
Write-Host ""

# Order Service
$server3Results["Order"] = Test-ServiceHealth "服务器3" $server3 "Order Service" 8033 "/actuator/health"
Start-Sleep -Milliseconds 500

# AI Service
$server3Results["AI"] = Test-ServiceHealth "服务器3" $server3 "AI Service" 8066 "/actuator/health"
Start-Sleep -Milliseconds 500

# Admin Service
$server3Results["Admin"] = Test-ServiceHealth "服务器3" $server3 "Admin Service" 8055 "/actuator/health"
Start-Sleep -Milliseconds 500

Write-Host ""
Write-Host "======== 测试总结 ========" -ForegroundColor Cyan

$totalTests = 0
$passedTests = 0

Write-Host ""
Write-Host "服务器1 测试结果:" -ForegroundColor Yellow
foreach ($key in $server1Results.Keys) {
    $totalTests++
    if ($server1Results[$key]) {
        $passedTests++
        Write-Host "  $key : OK 通过" -ForegroundColor Green
    }
    else {
        Write-Host "  $key : FAIL 失败" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "服务器2 测试结果:" -ForegroundColor Yellow
foreach ($key in $server2Results.Keys) {
    $totalTests++
    if ($server2Results[$key]) {
        $passedTests++
        Write-Host "  $key : OK 通过" -ForegroundColor Green
    }
    else {
        Write-Host "  $key : FAIL 失败" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "服务器3 测试结果:" -ForegroundColor Yellow
$server3FailCount = 0
foreach ($key in $server3Results.Keys) {
    $totalTests++
    if ($server3Results[$key]) {
        $passedTests++
        Write-Host "  $key : OK 通过" -ForegroundColor Green
    }
    else {
        $server3FailCount++
        Write-Host "  $key : FAIL 失败" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "总计: $passedTests / $totalTests 个服务正常" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Yellow" })

# 分析问题
Write-Host ""
Write-Host "======== 问题分析 ========" -ForegroundColor Cyan
$server3Total = $server3Results.Count

if ($server3FailCount -eq $server3Total) {
    Write-Host "[!] 服务器3上所有服务都失败 - 可能是网络/安全组问题" -ForegroundColor Red
    Write-Host "    建议检查:" -ForegroundColor Yellow
    Write-Host "    1. 服务器3的安全组配置 (端口 8033, 8055, 8066)" -ForegroundColor Yellow
    Write-Host "    2. 从服务器1 SSH到服务器3，在内部测试 curl localhost:8033/actuator/health" -ForegroundColor Yellow
    Write-Host "    3. 检查服务器3的防火墙设置" -ForegroundColor Yellow
}
elseif ($server3FailCount -gt 0) {
    Write-Host "[!] 服务器3上部分服务失败 - 可能是单个服务配置问题" -ForegroundColor Yellow
    Write-Host "    失败的服务:" -ForegroundColor Yellow
    foreach ($key in $server3Results.Keys) {
        if (-not $server3Results[$key]) {
            Write-Host "    - $key" -ForegroundColor Red
        }
    }
}
else {
    Write-Host "[OK] 服务器3上所有服务正常" -ForegroundColor Green
}

Write-Host ""
Write-Host "测试完成!" -ForegroundColor Cyan
