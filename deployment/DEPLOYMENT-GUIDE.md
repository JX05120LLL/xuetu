# 学途项目 - 多服务器部署指南

## 🎯 部署架构

```
服务器1 (8.141.106.92) - 主服务器
├── MySQL (3306) - 数据库
├── Redis (6379) - 缓存
├── Nacos (8848) - 服务注册中心
├── Gateway (8080) - API网关
├── Nginx (80) - 前端
└── Portainer (9000) - Docker可视化管理

服务器2 (新IP)
├── User Service (8088)
├── Course Service (8077)
├── Learning Service (8044)
└── Portainer Agent (9001)

服务器3 (新IP)
├── Order Service (8033)
├── AI Service (8066)
├── Admin Service (8055)
└── Portainer Agent (9001)
```

## 📋 启动顺序

1. **MySQL + Redis** (基础设施)
2. **Nacos** (服务注册中心) - 等待30秒启动完成
3. **业务微服务** (User/Course/Learning/Order/AI/Admin) - 自动注册到Nacos
4. **Gateway** (API网关) - 从Nacos获取服务列表
5. **Nginx** (前端)

Docker Compose会自动处理依赖关系和启动顺序！

---

## 🚀 快速部署

### 前提条件

1. **3台服务器**，每台至少 2核2G
2. **已安装**: Docker, Docker Compose
3. **本地已安装**: Maven, OpenJDK 17, PowerShell (Windows) 或 bash (Linux/Mac)
4. **网络配置**: 配置好服务器间的安全组，开放必要端口

### 一键部署命令

```powershell
# Windows PowerShell
cd deployment
.\deploy-all.ps1 -Server2IP "你的服务器2IP" -Server3IP "你的服务器3IP"
```

```bash
# Linux/Mac (TODO: 创建bash版本)
cd deployment
./deploy-all.sh 服务器2IP 服务器3IP
```

---

## 🔧 手动部署步骤

### 步骤1: 配置安全组（阿里云）

**服务器1需要开放的端口:**
- 80 (Nginx)
- 8080 (Gateway)
- 8848, 9848 (Nacos)
- 9000, 9443 (Portainer)
- 3306 (MySQL - 仅内网)
- 6379 (Redis - 仅内网)

**服务器2需要开放的端口:**
- 8088 (User Service - 仅内网)
- 8077 (Course Service - 仅内网)
- 8044 (Learning Service - 仅内网)
- 9001 (Portainer Agent - 仅内网)

**服务器3需要开放的端口:**
- 8033 (Order Service - 仅内网)
- 8066 (AI Service - 仅内网)
- 8055 (Admin Service - 仅内网)
- 9001 (Portainer Agent - 仅内网)

### 步骤2: 初始化MySQL和Nacos数据库

**SSH到服务器1**

```bash
# 1. 启动MySQL
cd /root/xuetu
docker-compose up -d mysql

# 2. 等待MySQL启动
sleep 10

# 3. 创建Nacos数据库
docker exec -i xuetu-mysql mysql -uroot -proot123456 <<EOF
CREATE DATABASE IF NOT EXISTS nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EOF

# 4. 导入Nacos表结构（可选，Nacos会自动创建）
# 下载: https://github.com/alibaba/nacos/blob/master/distribution/conf/mysql-schema.sql
```

### 步骤3: 修改服务器IP配置

**编辑所有配置文件，将 `8.141.106.92` 替换为实际的服务器1 IP:**

- `deployment/docker-compose-server1.yml`
- `deployment/docker-compose-server2.yml`
- `deployment/docker-compose-server3.yml`
- 所有服务的 `application-prod.yml` 文件

### 步骤4: 编译项目

```powershell
# 在项目根目录
mvn clean package -DskipTests
```

### 步骤5: 部署服务器1

```bash
# 1. 创建目录
ssh root@8.141.106.92 "mkdir -p /root/xuetu/jars /root/xuetu/logs"

# 2. 上传文件
scp gateway/target/gateway-1.0-SNAPSHOT.jar root@8.141.106.92:/root/xuetu/jars/gateway.jar
scp deployment/docker-compose-server1.yml root@8.141.106.92:/root/xuetu/docker-compose.yml

# 3. 启动服务
ssh root@8.141.106.92 "cd /root/xuetu && docker-compose up -d"

# 4. 查看Nacos日志，确保启动成功
ssh root@8.141.106.92 "docker logs -f xuetu-nacos"
```

**等待Nacos启动完成（约30秒），访问: http://8.141.106.92:8848/nacos**
- 默认用户名/密码: `nacos/nacos`

### 步骤6: 部署服务器2

```bash
# 上传JAR和配置
ssh root@服务器2IP "mkdir -p /root/xuetu/jars /root/xuetu/logs"
scp user-service/target/user-service-1.0-SNAPSHOT.jar root@服务器2IP:/root/xuetu/jars/user-service.jar
scp course-service/target/course-service-1.0-SNAPSHOT.jar root@服务器2IP:/root/xuetu/jars/course-service.jar
scp learning-service/target/learning-service-1.0-SNAPSHOT.jar root@服务器2IP:/root/xuetu/jars/learning-service.jar
scp deployment/docker-compose-server2.yml root@服务器2IP:/root/xuetu/docker-compose.yml

# 启动服务
ssh root@服务器2IP "cd /root/xuetu && docker-compose up -d"

# 查看日志
ssh root@服务器2IP "docker logs -f xuetu-user-service"
```

### 步骤7: 部署服务器3

```bash
# 上传JAR和配置
ssh root@服务器3IP "mkdir -p /root/xuetu/jars /root/xuetu/logs"
scp order-service/target/order-service-1.0-SNAPSHOT.jar root@服务器3IP:/root/xuetu/jars/order-service.jar
scp ai-service/target/ai-service-1.0-SNAPSHOT.jar root@服务器3IP:/root/xuetu/jars/ai-service.jar
scp admin-service/target/admin-service-1.0-SNAPSHOT.jar root@服务器3IP:/root/xuetu/jars/admin-service.jar
scp deployment/docker-compose-server3.yml root@服务器3IP:/root/xuetu/docker-compose.yml

# 启动服务
ssh root@服务器3IP "cd /root/xuetu && docker-compose up -d"
```

---

## 📊 可视化管理

### 1. Nacos控制台

访问: `http://8.141.106.92:8848/nacos`

**功能:**
- 查看所有已注册的服务
- 查看服务健康状态
- 查看服务实例列表
- 动态配置管理

### 2. Portainer

访问: `http://8.141.106.92:9000`

**首次访问需要创建管理员账号**

**功能:**
- 可视化管理所有3台服务器的Docker容器
- 查看容器日志
- 启动/停止/重启容器
- 查看容器资源使用情况
- 管理镜像和网络

**添加其他服务器:**
1. 在Portainer中点击 "Environments" → "Add environment"
2. 选择 "Agent"
3. 输入服务器2的IP和端口9001
4. 重复操作添加服务器3

---

## 🔍 服务检查

### 查看Nacos服务列表

```bash
# 访问Nacos API
curl "http://8.141.106.92:8848/nacos/v1/ns/instance/list?serviceName=user-service&namespaceId=xuetu-prod"
```

### 查看所有服务状态

```bash
# 服务器1
ssh root@8.141.106.92 "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

# 服务器2
ssh root@服务器2IP "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

# 服务器3
ssh root@服务器3IP "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"
```

### 查看服务日志

```bash
# Gateway日志
ssh root@8.141.106.92 "docker logs -f xuetu-gateway"

# User Service日志
ssh root@服务器2IP "docker logs -f xuetu-user-service"

# 查看最近100行日志
ssh root@8.141.106.92 "docker logs --tail 100 xuetu-gateway"
```

---

## 🛠️ 常用命令

### 重启单个服务

```bash
# 重启Gateway
ssh root@8.141.106.92 "docker restart xuetu-gateway"

# 重启User Service
ssh root@服务器2IP "docker restart xuetu-user-service"
```

### 更新服务

```bash
# 1. 本地编译新版本
mvn clean package -DskipTests

# 2. 上传新JAR
scp user-service/target/user-service-1.0-SNAPSHOT.jar root@服务器2IP:/root/xuetu/jars/user-service.jar

# 3. 重启容器
ssh root@服务器2IP "docker restart xuetu-user-service"
```

### 停止所有服务

```bash
# 服务器1
ssh root@8.141.106.92 "cd /root/xuetu && docker-compose down"

# 服务器2
ssh root@服务器2IP "cd /root/xuetu && docker-compose down"

# 服务器3
ssh root@服务器3IP "cd /root/xuetu && docker-compose down"
```

### 查看服务器资源使用

```bash
# 查看Docker容器资源占用
ssh root@8.141.106.92 "docker stats --no-stream"
```

---

## ❓ 常见问题

### Q1: 服务启动失败，提示连接不上Nacos？

**A:** 检查Nacos是否启动完成，并确保防火墙/安全组已开放8848端口。

```bash
# 查看Nacos日志
docker logs xuetu-nacos

# 测试Nacos连接
curl http://8.141.106.92:8848/nacos/v1/console/health/liveness
```

### Q2: Gateway路由失败，提示找不到服务？

**A:** 检查业务服务是否成功注册到Nacos。

访问Nacos控制台 → 服务管理 → 服务列表，确认所有服务都已注册。

### Q3: 服务之间调用超时？

**A:** 检查服务器间网络连通性和安全组配置。

```bash
# 从服务器1测试连接服务器2的服务
ssh root@8.141.106.92 "curl -I http://服务器2IP:8088/user/health"
```

### Q4: 内存不足，服务被OOM杀死？

**A:** 调整JVM内存参数（docker-compose.yml中的`JAVA_OPTS`）。

建议配置:
- Gateway: `-Xms256m -Xmx512m`
- 业务服务: `-Xms256m -Xmx512m`
- Nacos: `JVM_XMS=256m JVM_XMX=256m`

### Q5: 如何查看某个服务的完整日志？

**A:**

```bash
# 查看实时日志
docker logs -f xuetu-user-service

# 查看所有日志
docker logs xuetu-user-service

# 查看最近500行
docker logs --tail 500 xuetu-user-service

# 导出日志到文件
docker logs xuetu-user-service > user-service.log 2>&1
```

---

## 📚 参考资料

- [Nacos官方文档](https://nacos.io/zh-cn/docs/quick-start.html)
- [Docker Compose文档](https://docs.docker.com/compose/)
- [Portainer文档](https://docs.portainer.io/)
- [Spring Cloud Gateway文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)

---

## 🎉 部署完成后

**访问地址:**
- 前端: http://8.141.106.92
- API网关: http://8.141.106.92:8080
- Nacos控制台: http://8.141.106.92:8848/nacos
- Portainer: http://8.141.106.92:9000

**测试API:**
```bash
# 测试登录
curl -X POST http://8.141.106.92:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 测试课程列表
curl http://8.141.106.92:8080/course/list
```

**祝您部署顺利！** 🚀
