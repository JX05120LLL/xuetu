# 学途 (XueTu) - 智能在线学习平台

> 基于 Spring Cloud 微服务架构的智能在线学习平台  
> 集成 RAG 知识库问答、Spring AI Function Calling 学习/运维智能体、OpenClaw MCP 技能暴露等现代化 AI 功能

> ⚠️ **项目说明**：本项目仅供个人参考与学习使用，非商业用途。

---

## 📋 项目简介

学途是一个功能完善的在线学习平台，采用前后端分离与微服务架构，涵盖用户认证、课程管理、学习跟踪、订单支付、AI 智能问答、权限管理等完整业务闭环。

项目使用 Spring Boot 3.x、Spring Cloud 2023、Spring Cloud Alibaba 等主流技术栈，引入 Redis 缓存、RocketMQ 消息队列、Redisson 分布式锁等中间件保障高并发与数据一致性。AI 模块基于 **Spring AI + Qdrant** 实现 RAG 知识库问答；并通过 **Spring AI Function Calling** 将平台业务能力封装为 Agent 工具，由 qwen-plus 大模型自动编排调用，同时通过 **MCP Server** 对 OpenClaw 等外部平台暴露。

**核心功能：**
- 🎓 **课程学习**：分类、章节、课时管理，评论系统
- 🤖 **AI 知识库问答**：RAG 问答（优先检索 Qdrant 知识库，未命中自动降级普通 AI）、学习路径规划、学习数据分析
- 🧠 **学习/运维智能体（Agent）**：Spring AI @Tool + DashScope qwen-plus，用户一句话即可查课程进度、生成学习计划、RAG 答疑；运维侧查服务健康、Redis/RocketMQ 状态；通过 MCP Server 暴露给 OpenClaw/Cursor
- 📊 **学习跟踪**：学习进度、笔记、学习记录
- 🛒 **订单支付**：课程购买、支付回调、RocketMQ 异步开课
- 👤 **用户与权限**：注册登录、角色权限、头像上传
- 🔐 **安全与限流**：JWT 认证、分布式限流（游客/登录用户分级）、防重复下单

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
| **向量数据库** | Qdrant 1.13.0（RAG 知识库） |
| **分布式锁** | Redisson 3.27.2 |
| **AI 框架** | Spring AI 1.1.2 / 通义千问 qwen-plus / text-embedding-v3 |
| **AI 协议** | Spring AI MCP Server（将 @Tool 暴露给 OpenClaw/Cursor） |
| **AI 网关** | OpenClaw（本地 AI Gateway，通过 MCP 协议调用平台技能） |
| **开发语言** | Java 17 |
| **构建工具** | Maven 3.8+ |

---

## 📁 项目模块结构

```
xuetu/
├── gateway/                 # API 网关（路由、JWT 鉴权、分布式限流）
├── user-service/            # 用户服务（认证、用户信息、头像）
├── course-service/          # 课程服务（课程、分类、章节、课时、评论、标签）
├── learning-service/        # 学习服务（学习记录、笔记、用户课程）
├── order-service/           # 订单服务（订单、支付、购买记录）
├── ai-service/              # AI 服务（RAG 问答、学习/运维 Agent、OpenClaw 技能）
├── admin-service/           # 管理服务（角色、权限）
├── common-service/          # 公共模块（DTO、工具类、MQ 消息体）
├── xuetu-web/               # 前端项目（Vue 3 + TypeScript + Element Plus）
├── deployment/              # Docker 编排与部署配置
│   ├── docker-compose-local.yml    # 本地开发：一键启动全量中间件
│   ├── init-db.sh                  # MySQL 初始化脚本
│   └── ...
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
| **ai-service** | RAG 知识库问答、学习/运维智能体（@Tool + Function Calling）、OpenClaw 技能 HTTP 入口、MCP Server |
| **admin-service** | 角色、权限、角色分配 |
| **common-service** | 通用 DTO、分页、Redis 工具、MQ 消息体（OrderPaidMessage） |

---

## 🐳 Docker Desktop 一键部署中间件

使用 **Docker Desktop** 可一键启动项目所需的全部中间件，无需单独安装 MySQL、Redis、RocketMQ、Nacos、Qdrant。

### 前提条件

- 已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows / Mac）
- 确保 Docker 已启动并正常运行

### 一键启动步骤

```bash
cd deployment
docker-compose -f docker-compose-local.yml up -d
```

等待约 1～2 分钟（首次启动需拉取镜像），验证状态：

```bash
docker-compose -f docker-compose-local.yml ps
```

所有容器状态为 `Up` 即表示启动成功。

### 中间件清单与访问地址

| 服务 | 端口 | 管理界面 |
|------|------|----------|
| **MySQL** | 3306 | 用户 root / root123456，库 xuetu_db |
| **Redis** | 6379 | - |
| **RocketMQ NameServer** | 9876 | - |
| **RocketMQ Broker** | 10909/10911 | - |
| **RocketMQ Dashboard** | 8180 | http://localhost:8180 |
| **Nacos** | 8848/9848 | http://localhost:8848/nacos（nacos/nacos） |
| **Qdrant** | 6333/6334 | http://localhost:6333/dashboard |

### 常用命令

```bash
# 停止所有中间件
docker-compose -f docker-compose-local.yml down

# 停止并删除数据卷（慎用，会清空 MySQL、Qdrant 数据）
docker-compose -f docker-compose-local.yml down -v

# 查看某服务日志
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

```bash
cd deployment
docker-compose -f docker-compose-local.yml up -d
```

### 二、配置环境变量

AI 相关功能需配置 **阿里云 DashScope API Key**，在 IDEA 的 Run/Debug Configurations → Environment variables 中添加：

```
ALIYUN_AI_KEY=你的DashScope_API_Key
```

前往 [阿里云百炼](https://bailian.console.aliyun.com/) 免费申请。

### 三、编译与启动后端

```bash
mvn clean package -DskipTests
```

按依赖顺序启动（先确保 Nacos、MySQL、Redis、RocketMQ 已就绪）：

```bash
# 推荐使用 IDE 依次启动各服务主类
# 或 java -jar 方式
java -jar gateway/target/gateway-*.jar
java -jar user-service/target/user-service-*.jar
java -jar course-service/target/course-service-*.jar
java -jar learning-service/target/learning-service-*.jar
java -jar order-service/target/order-service-*.jar
java -jar ai-service/target/ai-service-*.jar
java -jar admin-service/target/admin-service-*.jar
```

各服务 application.yml 默认连接 localhost，与 docker-compose 一致，无需修改。

### 四、启动前端

```bash
cd xuetu-web
npm install
npm run dev
```

访问 `http://localhost:5173`。

### 五、初始化 RAG 知识库（首次）

ai-service 启动后，调用以下接口将课程和 FAQ 数据写入 Qdrant 向量库（**只需执行一次**）：

```bash
POST http://localhost:8066/knowledge/ingest/all
```

或在 Knife4j（`http://localhost:8066/doc.html`）的「知识库管理」分组中点击「全量入库」。

---

## 🔄 核心工作流程

### 1. 请求整体流程

```
用户请求 → Gateway（路由 + JWT 鉴权 + 分布式限流）→ 具体微服务 → 数据库/Redis/MQ
```

- **JWT**：登录后携带 Token，Gateway 解析后通过 `X-User-Id` Header 传递到下游服务
- **限流**：游客按 IP，登录用户按 userId，基于 Redis 按分钟计数

### 2. Redis 使用场景

| 场景 | 模块 | 说明 |
|------|------|------|
| **布隆过滤器** | course-service | 拦截不存在的课程 ID，防缓存穿透 |
| **课程详情缓存** | course-service | `course:info:{id}`，随机 TTL，防雪崩 |
| **热门课程缓存** | course-service | 定时 30 分钟预热 |
| **分布式锁（缓存重建）** | course-service | 缓存未命中时加锁，只有一个请求查 DB |
| **分布式锁（下单）** | order-service | 同一用户并发下单时加锁，防重复 |
| **限流计数** | gateway | `rate_limit:ip:{ip}`、`rate_limit:user:{userId}` |

### 3. RocketMQ 消息流程：支付成功 → 开通课程

```
order-service 支付成功
    → 发送 MQ 消息（Topic: ORDER_PAID）→ 立即返回

learning-service 监听 ORDER_PAID
    → 幂等检查（user_course 是否已存在）
    → 写入 user_course 开通课程
    → 消费失败自动重试（最多 16 次），超出进入死信队列
```

### 4. 课程服务缓存与锁流程

```
请求课程详情
    → 布隆过滤器：ID 不存在直接返回 404
    → 查 Redis：命中则返回
    → 未命中：加 Redisson 分布式锁
        → 锁内二次查 Redis（双重检查）
        → 仍未命中则查 DB，写入 Redis（TTL + 随机偏移）
        → 释放锁
```

### 5. RAG 知识库问答流程

```
POST /chat/ask
    → 问题向量化（text-embedding-v3，1024 维）
    → Qdrant 检索 Top-5 相关文档（余弦相似度）
    → 有结果：注入 Prompt，调用 qwen-plus 生成带来源标注的回答
    → 无结果：降级为普通 AI 对话（带多轮上下文）
```

### 6. 学习/运维智能体（Agent）流程

```
POST /agent/learning/run 或 /agent/ops/run
    → AgentController 调用 ChatClient（Spring AI）
    → ChatClient 将用户消息 + @Tool 工具列表发送给 qwen-plus
    → 大模型决策：调用哪些工具、传什么参数
    → Spring AI 自动执行 @Tool 方法（通过 Feign 调 user/order/learning/course-service）
    → 工具结果返回大模型，继续推理，直到生成最终回答
    → 响应包含 answer（最终回答）+ toolCalls（工具调用链记录）
```

**学习智能体工具（6 个）：**

| 工具 | 功能 |
|------|------|
| `courseQuery` | 查询已购课程列表与章节结构 |
| `learningProgressTrack` | 查询学习进度、完成课时数、连续天数 |
| `learningPlanGenerate` | 根据课程章节生成按天学习计划 |
| `courseRagQa` | 基于 Qdrant 知识库回答课程疑问 |
| `learningRemind` | 设置每日学习提醒 |
| `afterClassTestGenerate` | 生成课后测验题目 |

**运维智能体工具（4 个）：**

| 工具 | 功能 |
|------|------|
| `serviceHealthCheck` | 查询 Nacos 服务实例健康状态 |
| `gatewayInterfaceMonitor` | 网关接口监控（占位，可对接 metrics） |
| `middlewareStatusCheck` | 查询 Redis / RocketMQ 运行状态 |
| `exceptionLogQuery` | 查询异常日志（占位，可对接 ELK） |

> 以上工具同时通过 **Spring AI MCP Server** 对外暴露，OpenClaw / Cursor 等客户端可通过 MCP 协议直接发现并调用。

---

## 📊 主要 API 说明

| 功能 | 接口 | 涉及服务 |
|------|------|----------|
| 登录注册 | `POST /auth/login`, `/auth/register` | user-service |
| 课程列表/详情 | `GET /course/**`, `/category/**` | course-service |
| 学习进度/笔记 | `/learning/**` | learning-service |
| 创建订单/支付 | `/api/orders/**`, `/api/payments/**` | order-service |
| AI 知识库问答 | `POST /chat/ask` | ai-service |
| AI 推荐/分析 | `/recommend/**`, `/analysis/**` | ai-service |
| 知识库管理 | `POST /knowledge/ingest/all` | ai-service |
| **学习智能体** | `POST /agent/learning/run` | ai-service |
| **运维智能体** | `POST /agent/ops/run` | ai-service |
| **OpenClaw 技能** | `POST /openclaw/skill/{skill_id}` | ai-service |
| 角色权限 | `/role/**`, `/permission/**` | admin-service |

---

## 🔧 配置说明

| 配置项 | 说明 |
|--------|------|
| **Nacos** | `http://localhost:8848/nacos`（默认 nacos/nacos） |
| **RocketMQ Dashboard** | `http://localhost:8180` |
| **Qdrant Dashboard** | `http://localhost:6333/dashboard` |
| **AI API Key** | 环境变量 `ALIYUN_AI_KEY`，在 IDEA Run Configuration 中配置 |
| **JWT** | Gateway 中配置 `jwt.secret`、`jwt.expiration` |
| **MySQL** | 默认 root/root123456，数据库 xuetu_db |

---

## 📝 API 文档

启动各微服务后访问：

- **Knife4j 聚合文档（网关）**：`http://localhost:8080/doc.html`
- **AI 服务文档（含 Agent、OpenClaw 技能）**：`http://localhost:8066/doc.html`

---

## 📌 版本历史

- **v1.0** - 核心功能上线（用户、课程、学习、订单）
- **v1.1** - 新增 AI 服务（通义千问对话、推荐、分析）
- **v1.2** - Redis 缓存防护（布隆过滤器、分布式锁、定时预热）
- **v1.3** - RocketMQ 支付成功异步开课
- **v2.0** - 升级 Spring Boot 3.x、Spring AI，接入 Qdrant 向量数据库
- **v2.1** - RAG 知识库问答上线（优先知识库，降级兜底）
- **v2.2** - 学习/运维智能体上线（Spring AI @Tool + Function Calling + MCP Server）

---

**最后更新**：2026 年 3 月

***最后我想说，本项目仍有许多不足，仅供参考学习使用，如果觉得对你有所帮助，点个star支持一下，谢谢！***
