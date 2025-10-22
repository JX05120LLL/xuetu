# 学途系统后端部署脚本（简化版）
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "学途系统后端部署" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$SERVER = "root@8.141.106.92"

Write-Host "`n[1/3] 在服务器创建目录..." -ForegroundColor Yellow
ssh $SERVER "mkdir -p /root/xuetu/jars /root/xuetu/logs"

Write-Host "`n[2/3] 上传 JAR 包..." -ForegroundColor Yellow
Write-Host "  上传 gateway..." -ForegroundColor Gray
scp gateway/target/*.jar ${SERVER}:/root/xuetu/jars/gateway.jar

Write-Host "  上传 user-service..." -ForegroundColor Gray
scp user-service/target/*.jar ${SERVER}:/root/xuetu/jars/user-service.jar

Write-Host "  上传 course-service..." -ForegroundColor Gray
scp course-service/target/*.jar ${SERVER}:/root/xuetu/jars/course-service.jar

Write-Host "  上传 learning-service..." -ForegroundColor Gray
scp learning-service/target/*.jar ${SERVER}:/root/xuetu/jars/learning-service.jar

Write-Host "  上传 order-service..." -ForegroundColor Gray
scp order-service/target/*.jar ${SERVER}:/root/xuetu/jars/order-service.jar

Write-Host "  上传 ai-service..." -ForegroundColor Gray
scp ai-service/target/*.jar ${SERVER}:/root/xuetu/jars/ai-service.jar

Write-Host "  上传 admin-service..." -ForegroundColor Gray
scp admin-service/target/*.jar ${SERVER}:/root/xuetu/jars/admin-service.jar

Write-Host "`n[3/3] 创建启动脚本..." -ForegroundColor Yellow

# 创建启动脚本
$startScript = @'
#!/bin/bash
# 学途系统启动脚本

cd /root/xuetu
LOG_DIR="logs"

echo "======================================"
echo "学途系统启动脚本"
echo "======================================"

# 停止所有服务
echo "停止旧服务..."
pkill -f "xuetu/jars" 2>/dev/null
sleep 3

# 启动服务（按顺序）
echo ""
echo "启动服务..."

echo "  [1/7] 启动 gateway (端口: 8080)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/gateway.jar > $LOG_DIR/gateway.log 2>&1 &
sleep 5

echo "  [2/7] 启动 user-service (端口: 8088)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/user-service.jar > $LOG_DIR/user-service.log 2>&1 &
sleep 3

echo "  [3/7] 启动 course-service (端口: 8077)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/course-service.jar > $LOG_DIR/course-service.log 2>&1 &
sleep 3

echo "  [4/7] 启动 learning-service (端口: 8044)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/learning-service.jar > $LOG_DIR/learning-service.log 2>&1 &
sleep 3

echo "  [5/7] 启动 order-service (端口: 8033)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/order-service.jar > $LOG_DIR/order-service.log 2>&1 &
sleep 3

echo "  [6/7] 启动 ai-service (端口: 8066)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/ai-service.jar > $LOG_DIR/ai-service.log 2>&1 &
sleep 3

echo "  [7/7] 启动 admin-service (端口: 8055)"
nohup java -jar -Xms256m -Xmx512m -Dspring.profiles.active=prod jars/admin-service.jar > $LOG_DIR/admin-service.log 2>&1 &

echo ""
echo "======================================"
echo "所有服务已启动！"
echo "======================================"
echo ""
echo "查看服务进程："
echo "  ps aux | grep java"
echo ""
echo "查看日志："
echo "  tail -f logs/gateway.log"
echo ""
'@

$startScript | Out-File -FilePath "start-all.sh" -Encoding UTF8 -NoNewline
scp start-all.sh ${SERVER}:/root/xuetu/
ssh $SERVER "chmod +x /root/xuetu/start-all.sh"

# 创建停止脚本
$stopScript = @'
#!/bin/bash
echo "停止所有学途系统服务..."
pkill -f "xuetu/jars"
echo "✓ 所有服务已停止"
'@

$stopScript | Out-File -FilePath "stop-all.sh" -Encoding UTF8 -NoNewline
scp stop-all.sh ${SERVER}:/root/xuetu/
ssh $SERVER "chmod +x /root/xuetu/stop-all.sh"

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "部署完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host "`n现在在 FinalShell 中执行：`n"
Write-Host "cd /root/xuetu && ./start-all.sh" -ForegroundColor Yellow
Write-Host "`n等待约 30 秒后，访问：http://8.141.106.92`n" -ForegroundColor Green
