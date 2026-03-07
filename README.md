# 学途 (XueTu) - 智能在线学习平台

> 基于 Spring Cloud 微服务架构的智能在线学习平台  
> 集成 AI 助手、课程推荐、学习路径规划、RAG 知识库问答（规划中）等现代化功能

> ⚠️ **项目说明**：本项目仅供个人参考与学习使用，非商业用途。

---

## 📋 项目简介

学途是一个功能完善的在线学习平台，采用前后端分离与微服务架构，涵盖用户认证、课程管理、学习跟踪、订单支付、AI 智能问答、权限管理等完整业务闭环。项目使用 Spring Boot 3.x、Spring Cloud 2023、Spring Cloud Alibaba 等主流技术栈，并引入 Redis 缓存、RocketMQ 消息队列、Redisson 分布式锁等中间件，保障高并发与数据一致性。后续计划基于 Spring AI 实现 RAG 知识库问答，将课程、章节、笔记等知识向量化，提供「带引用」的智能回答。

**核心功能：**
- 🎓 **课程学习**：分类、章节、课时管理，视频播放，评论系统
- 🤖 **AI 智能助手**：学习问答、学习路径规划、学习数据分析
- 📚 **RAG 知识库问答**（规划中）：课程/章节/课时/笔记/FAQ → 向量检索 → LLM 生成带引用回答
- 📊 **学习跟踪**：学习进度、笔记、学习记录
- 🛒 **订单支付**：课程购买、支付回调、异步开课
- 👤 **用户与权限**：注册登录、角色权限、头像上传
- 🔐 **安全与限流**：JWT 认证、分布式限流、防重复下单

---

## 🏗️ 技术栈

| 类别 | 技术 |
|------|------|
| **框架** | Spring Boot 3.3.6, Spring Cloud 2023.0.3, Spring Cloud Alibaba 2023.0.3.2 |
| **注册中心** | Nacos 2.2.0 |
| **网关** | Spring Cloud Gateway |
| **数据库** | MySQL 8.0 |
| **缓存** | Redis 7.x |
| **消息队列** | RocketMQ 4.9.7 |
| **向量数据库** | Qdrant 1.7.4（RAG 知识库） |
| **分布式锁** | Redisson 3.27.2 |
| **AI** | 通义千问 (Qwen) / Spring AI 1.1.2 |
| **开发语言** | Java 17 |
| **构建工具** | Maven 3.8+ |

---

## 📁 项目模块结构

```
xuetu/
├── gateway/                 # API 网关
├── user-service/            # 用户服务（认证、用户信息、头像）
├── course-service/          # 课程服务（课程、分类、章节、课时、评论、标签）
├── learning-service/        # 学习服务（学习记录、笔记、用户课程）
├── order-service/           # 订单服务（订单、支付、购买记录）
├── ai-service/              # AI 服务（智能问答、推荐、分析、RAG 规划中）
├── admin-service/           # 管理服务（角色、权限）
├── common-service/          # 公共模块（DTO、工具类、MQ 消息体）
├── xuetu-web/               # 前端项目（Vue 3 + TypeScript + Element Plus）
├── deployment/              # Docker 编排与部署配置
│   ├── docker-compose-local.yml    # 本地开发：一键启动全量中间件
│   ├── init-db.sh                  # MySQL 初始化脚本
│   ├── docker-compose-server1.yml  # 生产：基础设施层
│   ├── docker-compose-server2.yml  # 生产：核心业务层
│   └── docker-compose-server3.yml  # 生产：扩展业务层
├── sql/                     # 数据库脚本
└── 文档/
```

| 模块 | 说明 |
|------|------|
| **gateway** | 统一入口、路由转发、JWT 鉴权、分布式限流（Redis） |
| **user-service** | 登录注册、用户信息、头像上传 |
| **course-service** | 课程 CRUD、分类树、章节课时、评论、布隆过滤器 + Redis 缓存 + 分布式锁 |
| **learning-service** | 学习进度、笔记、用户课程、消费支付成功 MQ 消息开通课程 |
| **order-service** | 订单创建、支付、发送支付成功 MQ 消息、分布式锁防重复下单 |
| **ai-service** | 通义千问对话、学习路径推荐、学习分析报告；规划中：RAG 知识库问答 |
| **admin-service** | 角色、权限、角色分配 |
| **common-service** | 通用 DTO、分页、Redis 工具、MQ 消息体（OrderPaidMessage） |

---

## 🐳 Docker Desktop 一键部署中间件

使用 **Docker Desktop** 可一键启动项目所需的全部中间件，无需单独安装 MySQL、Redis、RocketMQ、Nacos、Qdrant。

### 前提条件

- 已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows / Mac）
- 确保 Docker 已启动并正常运行

### 一键启动步骤

1. **打开终端**，进入项目 `deployment` 目录：

```bash
cd deployment
```

2. **执行以下命令**启动所有中间件：

```bash
docker-compose -f docker-compose-local.yml up -d
```

3. **等待服务就绪**（约 1～2 分钟，首次启动需拉取镜像）：

- MySQL 会自动创建 `xuetu_db` 并导入表结构
- RocketMQ Broker 依赖 NameServer，会有短暂启动延迟
- Nacos 使用内嵌存储，启动较快
- Qdrant 首次启动会初始化存储目录

4. **验证服务状态**：

```bash
docker-compose -f docker-compose-local.yml ps
```

所有容器状态为 `Up` 即表示启动成功。

### 中间件清单与访问地址

| 服务 | 容器名 | 端口 | 说明 | 管理界面/用途 |
|------|--------|------|------|---------------|
| **MySQL** | xuetu-mysql | 3306 | 业务数据库 | 用户 root / root123456，库 xuetu_db |
| **Redis** | xuetu-redis | 6379 | 缓存、限流、分布式锁 | - |
| **RocketMQ NameServer** | xuetu-namesrv | 9876 | 消息注册中心 | - |
| **RocketMQ Broker** | xuetu-broker | 10909/10911/10912 | 消息存储与转发 | - |
| **RocketMQ Dashboard** | xuetu-rocketmq-dashboard | 8180 | 消息可视化管理 | http://localhost:8180 |
| **Nacos** | xuetu-nacos | 8848/9848 | 服务注册与发现 | http://localhost:8848/nacos（nacos/nacos） |
| **Qdrant** | xuetu-qdrant | 6333/6334 | 向量数据库（RAG 用） | http://localhost:6333/dashboard |

### 常用命令

```bash
# 停止所有中间件
docker-compose -f docker-compose-local.yml down

# 停止并删除数据卷（慎用，会清空 MySQL、Qdrant 数据）
docker-compose -f docker-compose-local.yml down -v

# 查看某服务日志
docker-compose -f docker-compose-local.yml logs -f broker
docker-compose -f docker-compose-local.yml logs -f mysql
```

---

## 🚀 快速运行项目

### 环境要求

- **JDK 17+**
- **Maven 3.8+**
- **Docker Desktop**（用于一键启动中间件）
- **Node.js 18+**（前端开发时）

### 一、启动中间件（Docker）

按上文「Docker Desktop 一键部署中间件」执行：

```bash
cd deployment
docker-compose -f docker-compose-local.yml up -d
```

### 二、配置与启动后端

1. **确认各服务 `application.yml` 中的连接配置**（默认连接 localhost）：
   - MySQL：`localhost:3306`，用户 `root`，密码 `root123456`（与 docker-compose 一致）
   - Redis：`localhost:6379`
   - Nacos：`localhost:8848`
   - RocketMQ：`localhost:9876`（NameServer）
   - Qdrant：`localhost:6334`（gRPC）

2. **编译项目**：

```bash
mvn clean package -DskipTests
```

3. **按依赖顺序启动微服务**（先确保 Nacos、MySQL、Redis、RocketMQ 已就绪）：

```bash
# 方式一：使用 IDE 依次启动各服务主类
# 方式二：使用 java -jar 启动
java -jar gateway/target/gateway-*.jar
java -jar user-service/target/user-service-*.jar
java -jar course-service/target/course-service-*.jar
java -jar learning-service/target/learning-service-*.jar
java -jar order-service/target/order-service-*.jar
java -jar ai-service/target/ai-service-*.jar
java -jar admin-service/target/admin-service-*.jar
```

### 三、启动前端

```bash
cd xuetu-web
npm install
npm run dev
```

访问 `http://localhost:5173`（或 Vite 配置的端口）。

### 四、AI 服务配置

AI 相关功能（智能问答、学习路径、RAG）需配置 **阿里云 DashScope API Key**：

- 在 `ai-service/src/main/resources/application.yml` 中配置 `aliyun.ai.api-key` 或环境变量 `ALIYUN_AI_KEY`
- 可前往 [阿里云百炼](https://bailian.console.aliyun.com/) 申请通义千问 API Key

---

## 🔄 核心工作流程

### 1. 请求整体流程

```
用户请求 → Nginx(可选) → Gateway(路由+鉴权+限流) → 具体微服务 → 数据库/Redis/MQ
```

- **Gateway**：根据 `Path` 将请求转发到对应服务（如 `/auth/**` → user-service，`/course/**` → course-service）
- **JWT**：登录后携带 Token，Gateway 解析并传递用户信息到下游
- **限流**：基于 Redis 的分布式限流（游客按 IP，登录用户按 userId）

### 2. Redis 使用场景

| 场景 | 模块 | 说明 |
|------|------|------|
| **布隆过滤器** | course-service | Redis BitMap 实现，拦截不存在的课程 ID，防止缓存穿透 |
| **课程详情缓存** | course-service | `course:info:{id}`，带随机 TTL，防止雪崩 |
| **热门课程缓存** | course-service | `course:hot:{limit}`，定时 30 分钟预热 |
| **分布式锁（课程详情）** | course-service | 缓存未命中时加锁，只有一个请求查 DB，防击穿 |
| **分布式锁（创建订单）** | order-service | 同一用户并发下单时加锁，防重复下单 |
| **限流计数** | gateway | `rate_limit:ip:{ip}`、`rate_limit:user:{userId}`，按分钟计数 |

### 3. RocketMQ 消息流程：支付成功 → 开通课程

```
order-service 支付成功
    → 发送 MQ 消息（Topic: ORDER_PAID）
    → 立即返回

learning-service 监听 ORDER_PAID
    → 消费消息
    → 幂等检查（user_course 是否已存在）
    → 写入 user_course 开通课程
```

- **消息体**：`OrderPaidMessage`（订单号、用户 ID、课程列表等）
- **幂等**：消费前查询 `user_course`，已开通则跳过
- **重试**：消费失败由 RocketMQ 自动重试（最多 16 次），超过后进入死信队列 `%DLQ%learning-consumer-group`

### 4. 课程服务缓存与锁流程

```
请求课程详情
    → 布隆过滤器：不存在则直接返回 404
    → 查 Redis：命中则返回
    → 未命中：加 Redisson 分布式锁
        → 锁内二次查 Redis（双重检查）
        → 仍未命中则查 DB，写入 Redis（TTL + 随机偏移）
        → 释放锁
```

### 5. 订单创建与防重复流程

```
用户发起购买
    → 加 Redisson 锁（key: lock:create:order:{userId}）
    → 拿不到锁：返回「请勿重复提交」
    → 拿到锁：创建订单、发起支付
    → 支付成功回调：发送 ORDER_PAID 消息
```

---

## 📚 RAG 知识库问答（规划中）

基于 **Spring AI** 与 **Qdrant** 向量数据库，计划实现 RAG（Retrieval-Augmented Generation）知识库问答功能。

### 设计思路

1. **知识源**：课程简介、章节标题与内容、课时内容、用户笔记、FAQ、项目文档等
2. **流程**：
   - 将知识切分、向量化（DashScope `text-embedding-v3`，1024 维）
   - 存入 Qdrant 向量库（collection：`xuetu_knowledge`）
   - 用户提问时：先做向量检索 Top-K，再结合检索结果 + 问题，调用 LLM 生成「带引用」的回答
3. **后续规划**：
   - RAG 评测与观测（recall@k、延迟、token 成本）
   - 增量更新索引、缓存、限流
   - 权限隔离（用户笔记仅本人可检索）

### 当前准备情况

- `ai-service` 已引入 Spring AI、Qdrant 相关依赖
- `application.yml` 已配置 DashScope、Qdrant 连接
- `docker-compose-local.yml` 已包含 Qdrant 容器，启动即可使用

---

## 📊 主要功能说明

| 功能 | 入口/接口 | 涉及服务 |
|------|-----------|----------|
| 登录注册 | `/auth/login`, `/auth/register` | user-service |
| 课程列表/详情 | `/course/**`, `/category/**` | course-service |
| 学习进度/笔记 | `/learning/**` | learning-service |
| 创建订单/支付 | `/api/orders/**`, `/api/payments/**` | order-service |
| AI 问答/推荐/分析 | `/chat/**`, `/recommend/**`, `/analysis/**` | ai-service |
| 角色权限 | `/role/**`, `/permission/**` | admin-service |
| API 文档 | `/doc.html`（经 Gateway 聚合） | 各服务 Knife4j |

---

## 🔧 配置说明

| 配置项 | 说明 |
|--------|------|
| **Nacos** | `http://localhost:8848/nacos`（默认 nacos/nacos） |
| **RocketMQ Dashboard** | `http://localhost:8180` |
| **Qdrant Dashboard** | `http://localhost:6333/dashboard` |
| **AI 通义千问** | 在 `ai-service` 中配置 DashScope API Key 或环境变量 `ALIYUN_AI_KEY` |
| **JWT** | Gateway 中配置 `jwt.secret`、`jwt.expiration` |
| **MySQL** | 默认 root/root123456，数据库 xuetu_db（与 docker-compose 一致） |

---

## 📝 API 文档

启动 Gateway 及各微服务后，访问：

- **Knife4j 聚合文档**：`http://localhost:8080/doc.html`

可在此查看并调试各服务的接口。

---

## 📌 版本历史

- **v1.0** - 核心功能上线
- **v1.1** - 新增 AI 服务与缓存优化
- **v1.2** - Redis 缓存防护（布隆过滤器、分布式锁、定时预热）
- **v1.3** - RocketMQ 支付成功异步开课
- **v2.0** - 升级 Spring Boot 3.x、Spring AI，数据库索引优化，接入 Qdrant 为 RAG 做准备

---

**最后更新**：2026 年 3 月
