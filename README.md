# 学途 (XueTu) - 在线学习平台

> 基于 Spring Cloud 微服务架构的智能在线学习平台  
> 个人开发项目 · 用于学习和演示

---

## 📋 项目简介

学途是一个功能完善的在线学习平台，采用前后端分离架构，集成了AI智能助手、学习路径规划、课程推荐等现代化功能。项目采用微服务架构，部署在3台阿里云服务器上，实现了服务的分布式部署。

**核心功能：**
- 🎓 在线课程学习（视频、课时管理）
- 🤖 AI智能助手（学习问答、路径规划、数据分析）
- 📊 学习数据分析与可视化
- 🛒 课程购买与订单管理
- 👤 用户权限与角色管理
- 📝 学习笔记与评论系统

---

## 🏗️ 技术栈

### 后端技术
- **框架**: Spring Boot 2.7.x, Spring Cloud 2021.x
- **注册中心**: Nacos 2.2.0
- **网关**: Spring Cloud Gateway
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **AI集成**: 通义千问 (Qwen AI)
- **构建工具**: Maven 3.8+
- **开发语言**: Java 17

### 前端技术
- **框架**: Vue 3 + TypeScript
- **UI组件**: Element Plus
- **构建工具**: Vite
- **图表**: ECharts 5
- **状态管理**: Pinia
- **路由**: Vue Router 4

### 基础设施
- **容器化**: Docker + Docker Compose
- **Web服务器**: Nginx
- **服务器**: 阿里云 ECS x 3

---

## 🎯 系统架构详解

### 三台服务器分布式架构

```
┌──────────────────────────────────────────────────────────────────┐
│                        用户浏览器                                  │
└────────────────────────┬─────────────────────────────────────────┘
                         │ HTTP :80
                         ↓
┌────────────────────────────────────────────────────────────────────┐
│                  服务器1 (8.141.106.92)                             │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  Nginx 容器 (xuetu-web)                                       │ │
│  │  - 前端静态文件托管 (/usr/share/nginx/html)                   │ │
│  │  - 媒体文件托管 (/usr/share/nginx/media)                      │ │
│  │  - API反向代理 → 服务器2:8080                                 │ │
│  └──────────────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  MySQL 容器 (xuetu-mysql) :3306                               │ │
│  │  Redis 容器 (xuetu-redis) :6379                               │ │
│  │  Nacos 容器 (xuetu-nacos) :8848                               │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘
                         │
                         │ API请求转发
                         ↓
┌────────────────────────────────────────────────────────────────────┐
│                  服务器2 (112.126.85.23)                            │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  Gateway 容器 (xuetu-gateway) :8080                           │ │
│  │  - 统一API网关入口                                            │ │
│  │  - 从Nacos获取服务列表                                        │ │
│  │  - 路由转发到各个微服务                                       │ │
│  └──────────────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  User Service (xuetu-user-service) :8088                      │ │
│  │  - 用户管理、认证、头像上传                                   │ │
│  │  - 文件挂载: /www/wwwroot/media                               │ │
│  └──────────────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  Course Service (xuetu-course-service) :8077                  │ │
│  │  Learning Service (xuetu-learning-service) :8044              │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘
                         │
                         │ 服务调用
                         ↓
┌────────────────────────────────────────────────────────────────────┐
│                  服务器3 (8.140.224.117)                            │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  Order Service (xuetu-order-service) :8033                    │ │
│  │  AI Service (xuetu-ai-service) :8066                          │ │
│  │  Admin Service (xuetu-admin-service) :8055                    │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘

         所有微服务通过Nacos进行服务注册与发现
              ↑ 注册 / 心跳 / 配置拉取 ↓
```

---

## 🔗 核心组件关系详解

### 1. 用户请求流程

#### 静态资源请求（前端页面、CSS、JS、图片）
```
浏览器 
  → http://8.141.106.92/ 
  → Nginx容器 (服务器1) 
  → /usr/share/nginx/html/index.html
  → 返回前端页面
```

#### 媒体文件请求（课程封面、视频、用户头像）
```
浏览器 
  → http://8.141.106.92/media/covers/course1.jpg 
  → Nginx容器 (服务器1) 
  → /usr/share/nginx/media/covers/course1.jpg
  → 返回图片文件
```

#### API请求（登录、获取数据等）
```
浏览器 
  → http://8.141.106.92/api/auth/login 
  → Nginx容器 (服务器1) 
  → 反向代理到 http://112.126.85.23:8080/auth/login 
  → Gateway容器 (服务器2) 
  → 查询Nacos获取 user-service 的地址
  → 转发到 user-service:8088/auth/login
  → 返回响应
```

---

### 2. Nginx配置详解

**Nginx容器位置**：服务器1 (8.141.106.92)  
**配置文件路径**：`/etc/nginx/conf.d/default.conf`

```nginx
server {
    listen 80;
    server_name localhost;
    client_max_body_size 50m;
    charset utf-8;

    # 【1】前端静态文件托管
    root /usr/share/nginx/html;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;  # Vue Router 的 History 模式支持
    }

    # 【2】媒体文件托管（课程封面、视频、用户头像）
    location ^~ /media/ {
        alias /usr/share/nginx/media/;  # 映射到容器内的media目录
        add_header Cache-Control "public, max-age=31536000, immutable";
        add_header Access-Control-Allow-Origin *;
    }

    # 【3】API反向代理到服务器2的Gateway
    location /api/ {
        proxy_pass http://112.126.85.23:8080/;  # 转发到Gateway
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 120s;
        proxy_read_timeout 120s;
    }

    # 【4】直接访问微服务（用于调试）
    location ~ ^/(user-service|course-service|learning-service|order-service|ai-service|admin-service)/ {
        proxy_pass http://112.126.85.23:8080;
    }
}
```

---

### 3. Docker容器与文件挂载关系

#### 服务器1 - 基础设施层

**xuetu-web (Nginx容器)**
```yaml
volumes:
  # 前端打包文件
  - /www/wwwroot/xuetu-web/dist:/usr/share/nginx/html
  # Nginx配置文件
  - /www/wwwroot/xuetu-web/nginx.conf:/etc/nginx/conf.d/default.conf
  # 媒体文件目录（重要！）
  - /www/wwwroot/media:/usr/share/nginx/media
```

**xuetu-mysql (MySQL容器)**
```yaml
volumes:
  - mysql_data:/var/lib/mysql  # 数据持久化
ports:
  - "3306:3306"  # 暴露给其他服务器访问
```

**xuetu-redis (Redis容器)**
```yaml
ports:
  - "6379:6379"  # 暴露给其他服务器访问
```

**xuetu-nacos (Nacos容器)**
```yaml
ports:
  - "8848:8848"  # HTTP端口（Web控制台 + 服务注册）
  - "9848:9848"  # gRPC端口（服务间通信）
```

---

#### 服务器2 - 核心业务层

**xuetu-gateway (网关容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"  # 连接到服务器1的Nacos
  MYSQL_HOST: "8.141.106.92"         # 连接到服务器1的MySQL
  REDIS_HOST: "8.141.106.92"         # 连接到服务器1的Redis
volumes:
  - ./jars/gateway.jar:/app/gateway.jar
  - ./logs:/var/log/xuetu
ports:
  - "8080:8080"  # 网关端口（Nginx会代理到这里）
```

**xuetu-user-service (用户服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/user-service.jar:/app/user-service.jar
  - ./logs:/var/log/xuetu
  # ⚠️ 关键：文件上传目录（与服务器1的Nginx共享同一路径）
  - /www/wwwroot/media:/www/wwwroot/media
ports:
  - "8088:8088"
```

**xuetu-course-service (课程服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/course-service.jar:/app/course-service.jar
  - ./logs:/var/log/xuetu
ports:
  - "8077:8077"
```

**xuetu-learning-service (学习服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/learning-service.jar:/app/learning-service.jar
  - ./logs:/var/log/xuetu
ports:
  - "8044:8044"
```

---

#### 服务器3 - 扩展业务层

**xuetu-order-service (订单服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/order-service.jar:/app/order-service.jar
  - ./logs:/var/log/xuetu
ports:
  - "8033:8033"
```

**xuetu-ai-service (AI服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/ai-service.jar:/app/ai-service.jar
  - ./logs:/var/log/xuetu
ports:
  - "8066:8066"
```

**xuetu-admin-service (管理服务容器)**
```yaml
environment:
  NACOS_SERVER: "8.141.106.92:8848"
  MYSQL_HOST: "8.141.106.92"
  REDIS_HOST: "8.141.106.92"
volumes:
  - ./jars/admin-service.jar:/app/admin-service.jar
  - ./logs:/var/log/xuetu
ports:
  - "8055:8055"
```

---

## 📁 文件存储机制详解

### ⚠️ 重要：跨服务器文件存储问题

在分布式架构中，文件存储是一个关键问题。我们的项目存在以下情况：

#### 问题场景：用户头像上传

```
用户上传头像 
  → 前端发送到 /api/user/avatar/upload
  → Nginx (服务器1) 代理到 Gateway (服务器2)
  → Gateway 路由到 user-service (服务器2)
  → user-service 保存文件到 /www/wwwroot/media/UserAvatar/ (服务器2的磁盘)
  → 返回URL: http://8.141.106.92/media/UserAvatar/2025/10/31/xxx.png

用户访问头像
  → 浏览器请求 /media/UserAvatar/2025/10/31/xxx.png
  → Nginx (服务器1) 从 /usr/share/nginx/media/UserAvatar/ 读取
  → ❌ 文件不存在！因为文件在服务器2上
```

#### 解决方案

**方案1：手动文件同步（当前使用）**

当发现头像404时，手动从服务器2同步到服务器1：

```bash
# 在服务器2打包
cd /www/wwwroot/media
tar -czvf /tmp/UserAvatar.tar.gz UserAvatar/

# 传输到服务器1
scp /tmp/UserAvatar.tar.gz root@8.141.106.92:/tmp/

# 在服务器1解压
cd /www/wwwroot/media
tar -xzvf /tmp/UserAvatar.tar.gz
```

**方案2：定期自动同步（推荐）**

在服务器2上创建定时任务：

```bash
# 编辑crontab
crontab -e

# 每小时同步一次
0 * * * * rsync -avz --delete /www/wwwroot/media/UserAvatar/ root@8.141.106.92:/www/wwwroot/media/UserAvatar/
```

**方案3：统一对象存储（生产推荐）**

使用阿里云OSS或MinIO统一存储：
- 所有服务上传到OSS
- 用户直接从OSS访问
- 无需跨服务器同步

---

### 媒体文件目录结构

```
/www/wwwroot/media/          # 媒体根目录
├── covers/                  # 课程封面
│   ├── course1.jpg
│   └── course2.png
├── videos/                  # 课程视频
│   ├── lesson1.mp4
│   └── lesson2.mp4
└── UserAvatar/              # 用户头像
    └── 2025/
        └── 10/
            └── 31/
                └── d56b7c7069a64c90b13eaee8be2a2952.png
```

**关键点：**
- `covers/` 和 `videos/` 通常由管理员上传，只在服务器1上
- `UserAvatar/` 由用户动态上传，在服务器2生成，需要同步到服务器1

---

## 🌐 网络通信详解

### 端口映射表

| 服务器 | 容器名称 | 容器内端口 | 宿主机端口 | 外部访问 |
|--------|---------|-----------|-----------|---------|
| 服务器1 | xuetu-web | 80 | 80 | ✅ 所有用户 |
| 服务器1 | xuetu-mysql | 3306 | 3306 | ⚠️ 仅内部服务 |
| 服务器1 | xuetu-redis | 6379 | 6379 | ⚠️ 仅内部服务 |
| 服务器1 | xuetu-nacos | 8848 | 8848 | ✅ Web控制台 |
| 服务器1 | xuetu-nacos | 9848 | 9848 | ⚠️ gRPC通信 |
| 服务器2 | xuetu-gateway | 8080 | 8080 | ⚠️ 仅Nginx代理 |
| 服务器2 | xuetu-user-service | 8088 | 8088 | ❌ 不对外 |
| 服务器2 | xuetu-course-service | 8077 | 8077 | ❌ 不对外 |
| 服务器2 | xuetu-learning-service | 8044 | 8044 | ❌ 不对外 |
| 服务器3 | xuetu-order-service | 8033 | 8033 | ❌ 不对外 |
| 服务器3 | xuetu-ai-service | 8066 | 8066 | ❌ 不对外 |
| 服务器3 | xuetu-admin-service | 8055 | 8055 | ❌ 不对外 |

### 服务间通信机制

**1. Nacos服务注册与发现**

```
启动流程：
1. 各微服务启动时连接 8.141.106.92:8848
2. 向Nacos注册自己的服务名和IP:端口
3. 定期发送心跳保持在线状态

示例：
- user-service 注册为 "user-service" @ 112.126.85.23:8088
- course-service 注册为 "course-service" @ 112.126.85.23:8077
- ai-service 注册为 "ai-service" @ 8.140.224.117:8066
```

**2. Gateway路由转发**

```
Gateway从Nacos获取服务列表，动态生成路由：

路由规则：
- /user/** → user-service
- /course/** → course-service
- /learning/** → learning-service
- /api/orders/** → order-service
- /chat/** → ai-service
- /role/** → admin-service
```

**3. 微服务互相调用**

```java
// 使用OpenFeign进行服务调用（通过Nacos服务发现）

@FeignClient(name = "user-service")  // 服务名称，不是IP
public interface UserServiceClient {
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id);
}

// Feign会自动从Nacos查询 user-service 的实际地址
```

---

## 🚀 部署流程

### 前置条件

1. **三台服务器**已安装 Docker 和 Docker Compose
2. **服务器间网络**互通（阿里云安全组已配置）
3. **本地开发环境**已安装 JDK 17 和 Maven
4. **前端环境**已安装 Node.js 18+

### 步骤1：打包后端服务

```bash
# 在项目根目录执行
mvn clean package -DskipTests

# 打包结果在各服务的 target 目录：
# - gateway/target/gateway.jar
# - user-service/target/user-service.jar
# - course-service/target/course-service.jar
# - learning-service/target/learning-service.jar
# - order-service/target/order-service.jar
# - ai-service/target/ai-service.jar
# - admin-service/target/admin-service.jar
```

### 步骤2：打包前端项目

```bash
cd xuetu-web
npm install
npm run build

# 打包结果在 dist 目录
```

### 步骤3：上传文件到服务器

**服务器1 (8.141.106.92)**
```bash
# 上传前端
scp -r xuetu-web/dist root@8.141.106.92:/www/wwwroot/xuetu-web/
scp xuetu-web/nginx.conf root@8.141.106.92:/www/wwwroot/xuetu-web/

# 上传docker-compose文件
scp deployment/docker-compose-server1.yml root@8.141.106.92:/root/xuetu/docker-compose.yml
```

**服务器2 (112.126.85.23)**
```bash
# 创建目录
ssh root@112.126.85.23 "mkdir -p /root/xuetu/jars"

# 上传JAR文件
scp gateway/target/gateway.jar root@112.126.85.23:/root/xuetu/jars/
scp user-service/target/user-service.jar root@112.126.85.23:/root/xuetu/jars/
scp course-service/target/course-service.jar root@112.126.85.23:/root/xuetu/jars/
scp learning-service/target/learning-service.jar root@112.126.85.23:/root/xuetu/jars/

# 上传docker-compose文件
scp deployment/docker-compose-server2.yml root@112.126.85.23:/root/xuetu/docker-compose.yml
```

**服务器3 (8.140.224.117)**
```bash
# 创建目录
ssh root@8.140.224.117 "mkdir -p /root/xuetu/jars"

# 上传JAR文件
scp order-service/target/order-service.jar root@8.140.224.117:/root/xuetu/jars/
scp ai-service/target/ai-service.jar root@8.140.224.117:/root/xuetu/jars/
scp admin-service/target/admin-service.jar root@8.140.224.117:/root/xuetu/jars/

# 上传docker-compose文件
scp deployment/docker-compose-server3.yml root@8.140.224.117:/root/xuetu/docker-compose.yml
```

### 步骤4：启动服务

**按顺序启动（重要！）**

```bash
# 1. 先启动服务器1的基础设施
ssh root@8.141.106.92
cd /root/xuetu
docker-compose up -d

# 等待30秒，确保MySQL、Redis、Nacos完全启动
sleep 30

# 2. 启动服务器2的Gateway和业务服务
ssh root@112.126.85.23
cd /root/xuetu
docker-compose up -d

# 3. 启动服务器3的业务服务
ssh root@8.140.224.117
cd /root/xuetu
docker-compose up -d
```

### 步骤5：验证部署

```bash
# 检查Nacos服务注册
浏览器访问：http://8.141.106.92:8848/nacos
账号：nacos / nacos
查看"服务管理" → "服务列表"，确保7个服务都已注册

# 检查容器状态
docker ps -a  # 所有容器STATUS应该是 Up

# 测试前端访问
浏览器访问：http://8.141.106.92/

# 测试API
curl http://8.141.106.92/api/chat/health
```

---

## 🔧 运维管理

### 查看日志

```bash
# 查看容器日志
docker logs -f xuetu-gateway
docker logs --tail 100 xuetu-user-service

# 查看应用日志（如果挂载了日志目录）
tail -f /root/xuetu/logs/gateway/app.log
```

### 重启服务

```bash
# 重启单个容器
docker restart xuetu-gateway

# 重启整个服务器的所有服务
cd /root/xuetu
docker-compose restart

# 完全重建（删除旧容器，重新创建）
docker-compose down
docker-compose up -d
```

### 更新服务

```bash
# 1. 上传新的JAR文件到 /root/xuetu/jars/
# 2. 重启对应的服务
docker-compose restart xuetu-user-service
```

### 定期维护

   可以设置每周重启脚本防止OOM：

```bash
# 创建重启脚本
cat > /root/restart_xuetu.sh << 'EOF'
#!/bin/bash
cd /root/xuetu
docker-compose restart
echo "$(date): 学途服务已重启" >> /var/log/xuetu-restart.log
EOF

chmod +x /root/restart_xuetu.sh

# 设置定时任务（每周日凌晨3点）
crontab -e
# 添加：0 3 * * 0 /root/restart_xuetu.sh
```

---

## 🐛 常见问题排查

### 1. 服务无法访问 / 502 Bad Gateway

**排查步骤：**

```bash
# 检查容器状态
docker ps -a

# 检查Nacos服务注册
curl http://8.141.106.92:8848/nacos/v1/ns/instance/list?serviceName=gateway

# 检查网关连通性
curl http://112.126.85.23:8080/actuator/health

# 查看容器日志
docker logs xuetu-gateway
```

### 2. 用户头像/媒体文件404

**原因**：文件在服务器2，但Nginx在服务器1

**解决**：
```bash
# 从服务器2同步到服务器1
ssh root@112.126.85.23 "cd /www/wwwroot/media && tar -czf /tmp/media.tar.gz UserAvatar/"
scp root@112.126.85.23:/tmp/media.tar.gz /tmp/
cd /www/wwwroot/media
tar -xzf /tmp/media.tar.gz
```

**永久方案**：配置自动同步或使用OSS

### 3. 内存不足 / 容器频繁重启

**检查内存使用：**
```bash
free -h
docker stats --no-stream
```

**优化JVM内存：**
```yaml
# docker-compose.yml 中调整
command: java -Xms64m -Xmx128m -jar /app/gateway.jar
```

### 4. 数据库连接失败

**检查：**
```bash
# 测试MySQL连接
docker exec xuetu-mysql mysql -uroot -p密码 -e "SELECT 1"

# 检查防火墙
telnet 8.141.106.92 3306
```

### 5. Nacos服务注册失败

**检查：**
```bash
# 查看Nacos日志
docker logs xuetu-nacos

# 检查服务配置
docker exec xuetu-user-service env | grep NACOS

# 手动测试注册
curl -X POST 'http://8.141.106.92:8848/nacos/v1/ns/instance?serviceName=test&ip=127.0.0.1&port=8080'
```

---

## 📊 监控与性能

### 资源使用建议

| 服务器 | 推荐配置 | 当前使用 |
|--------|---------|---------|
| 服务器1 | 2核4G | 1.7G / 1.8G |
| 服务器2 | 2核4G | - |
| 服务器3 | 2核2G | - |

### 性能优化建议

1. **启用Redis缓存**：热点数据缓存，减少数据库压力
2. **Nginx静态资源缓存**：CSS/JS设置长期缓存
3. **数据库连接池**：HikariCP优化
4. **JVM参数调优**：根据实际内存调整Xms/Xmx
5. **使用CDN**：静态资源和媒体文件使用CDN加速

---

## 🔐 安全说明

- **JWT令牌认证**：Gateway前置鉴权
- **CORS跨域配置**：Nginx统一配置
- **SQL注入防护**：MyBatis Plus参数化查询
- **XSS攻击防护**：前端Element Plus过滤
- **敏感信息加密**：密码使用bcrypt加密存储
- **端口安全**：仅开放必要端口（80, 8848），其他端口仅内网访问
- **定期备份**：MySQL数据、用户上传文件

---

## 📝 API文档

### 主要API端点

**用户服务**
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/user/info` - 获取用户信息
- `POST /api/user/avatar/upload` - 上传头像

**课程服务**
- `GET /api/course/list` - 课程列表
- `GET /api/course/{id}` - 课程详情
- `GET /api/category/tree` - 课程分类树

**学习服务**
- `GET /api/learning/progress/{courseId}` - 学习进度
- `POST /api/learning/record` - 记录学习

**AI服务**
- `POST /api/chat/ask` - AI问答
- `POST /api/recommend/path` - 学习路径生成
- `GET /api/analysis/report` - 学习分析报告

---

## 📞 联系方式

- **项目性质**：个人学习项目
- **用途**：技术学习与演示
- **状态**：生产环境运行中

---

## 🙏 致谢

- Spring Cloud 生态
- Vue.js 社区
- Element Plus UI
- 阿里云通义千问
- ECharts 可视化
- 阿里云 ECS

---

## 📌 版本历史

- **v1.0** (2025-10) - 初始版本，实现核心功能
- **v1.1** (2025-10) - 新增AI服务和前端缓存优化
- **v1.2** (2025-11) - 优化生产部署配置，修复媒体路径
- **v1.3** (2026-01) - 修正架构文档，完善跨服务器文件存储说明

---

**最后更新**：2026年1月14日
