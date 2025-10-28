# 学途 (XueTu) - 在线学习平台

> 基于 Spring Cloud 微服务架构的智能在线学习平台  
> 个人开发项目 · 用于学习和演示

---

## 📋 项目简介

学途是一个功能完善的在线学习平台，采用前后端分离架构，集成了AI智能助手、学习路径规划、课程推荐等现代化功能。项目采用微服务架构，部署在3台云服务器上，实现了服务的分布式部署。

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
- **容器化**: Docker
- **Web服务器**: Nginx
- **服务器**: 阿里云 ECS x 3

---

## 🎯 系统架构

### 微服务架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户/浏览器                            │
└────────────────────┬────────────────────────────────────────┘
                     │ HTTP
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  Nginx (8.141.106.92:80)  - 前端静态文件 + 反向代理          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│  Spring Cloud Gateway (112.126.85.23:8080)  - API网关        │
└────────┬────────────────────────────────────────────────────┘
         │
         ├─────────────────────────────────────────────────────┐
         │                                                     │
    ┌────▼────┐  ┌────────┐  ┌────────┐  ┌────────┐  ┌───────▼──┐
    │  User   │  │ Course │  │Learning│  │ Order  │  │   AI     │
    │ Service │  │Service │  │Service │  │Service │  │ Service  │
    └────┬────┘  └───┬────┘  └───┬────┘  └───┬────┘  └────┬─────┘
         │           │            │           │            │
         └───────────┴────────────┴───────────┴────────────┘
                                  │
                     ┌────────────┴────────────┐
                     ↓                         ↓
              ┌──────────┐              ┌──────────┐
              │  MySQL   │              │  Redis   │
              │   8.0    │              │   7.x    │
              └──────────┘              └──────────┘
```

### 服务注册与发现

所有微服务通过 **Nacos** 进行服务注册与发现，实现动态路由和负载均衡。

---

## 🖥️ 服务器部署架构

项目部署在 **3台阿里云服务器** 上，实现服务的分布式部署：

### 🖥️ 服务器 1 - 基础设施 (8.141.106.92)

| 容器名称 | 服务 | 端口 | 说明 |
|---------|------|------|------|
| `xuetu-web` | Nginx | 80 | 前端静态文件服务 |
| `xuetu-nacos` | Nacos Server | 8848, 9848 | 服务注册与配置中心 |
| `xuetu-mysql` | MySQL 8.0 | 3306 | 主数据库 |
| `xuetu-redis` | Redis 7 | 6379 | 缓存服务 |

**挂载目录：**
- `/www/wwwroot/xuetu-web/dist` → 前端静态文件
- `/www/wwwroot/xuetu-web/nginx.conf` → Nginx配置
- `/www/wwwroot/media` → 媒体文件存储

---

### 🖥️ 服务器 2 - 核心业务服务 (112.126.85.23)

| 容器名称 | 服务 | 内部端口 | 说明 |
|---------|------|---------|------|
| `xuetu-gateway` | API网关 | 8080 | Spring Cloud Gateway，所有API的统一入口 |
| `xuetu-user-service` | 用户服务 | 8066 | 用户管理、认证授权 |
| `xuetu-course-service` | 课程服务 | 8055 | 课程、分类、课时管理 |
| `xuetu-learning-service` | 学习服务 | 8033 | 学习记录、进度、笔记 |

**服务职责：**
- **Gateway**: 路由转发、鉴权、限流、跨域处理
- **User Service**: 用户注册/登录、个人信息、权限管理
- **Course Service**: 课程CRUD、分类管理、课时管理
- **Learning Service**: 学习进度追踪、笔记管理、学习统计

---

### 🖥️ 服务器 3 - 扩展服务 (8.140.224.117)

| 容器名称 | 服务 | 内部端口 | 说明 |
|---------|------|---------|------|
| `xuetu-ai-service` | AI服务 | 8066 | AI助手、学习路径规划、智能推荐 |
| `xuetu-admin-service` | 管理服务 | 8055 | 后台管理、数据统计 |
| `xuetu-order-service` | 订单服务 | 8033 | 课程购买、订单管理 |

**服务职责：**
- **AI Service**: 集成通义千问AI，提供智能问答、学习路径生成、课程推荐
- **Admin Service**: 平台管理功能、数据报表
- **Order Service**: 订单处理、支付集成（预留）

---

## 📁 项目结构

```
xuetu/
├── admin-service/           # 管理服务
│   ├── src/
│   └── pom.xml
├── ai-service/              # AI智能服务 ⭐
│   ├── src/
│   │   └── main/
│   │       ├── java/com/star/ai/
│   │       │   ├── controller/      # AI接口控制器
│   │       │   ├── service/         # AI业务逻辑
│   │       │   ├── client/          # 通义千问客户端
│   │       │   └── dto/             # 数据传输对象
│   │       └── resources/
│   └── pom.xml
├── common-service/          # 公共服务模块
│   ├── src/
│   └── pom.xml
├── course-service/          # 课程服务 📚
│   ├── src/
│   └── pom.xml
├── gateway/                 # API网关 🚪
│   ├── src/
│   └── pom.xml
├── learning-service/        # 学习服务 📖
│   ├── src/
│   └── pom.xml
├── order-service/           # 订单服务 🛒
│   ├── src/
│   └── pom.xml
├── user-service/            # 用户服务 👤
│   ├── src/
│   └── pom.xml
├── xuetu-web/               # 前端项目 💻
│   ├── src/
│   │   ├── api/            # API接口封装
│   │   ├── components/     # Vue组件
│   │   ├── views/          # 页面视图
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── utils/          # 工具函数
│   ├── dist/               # 构建输出
│   ├── package.json
│   └── vite.config.ts
├── deployment/              # 部署相关 🚀
│   ├── docker-compose-server1.yml
│   ├── docker-compose-server2.yml
│   ├── docker-compose-server3.yml
│   ├── jars/               # 服务JAR包
│   └── DEPLOYMENT-GUIDE.md
├── sql/                     # 数据库脚本
│   └── xuetu_db.sql
├── logs/                    # 日志目录
└── pom.xml                  # 父级POM
```

---

## 🔌 服务端口汇总

### 外部访问端口

| 服务 | 服务器 | 端口 | 访问地址 | 说明 |
|------|--------|------|----------|------|
| Web前端 | Server 1 | 80 | http://8.141.106.92 | 用户访问入口 |
| API网关 | Server 2 | 8080 | http://112.126.85.23:8080 | 后端API入口 |
| Nacos控制台 | Server 1 | 8848 | http://8.141.106.92:8848/nacos | 服务注册中心 |
| MySQL | Server 1 | 3306 | 8.141.106.92:3306 | 数据库 |
| Redis | Server 1 | 6379 | 8.141.106.92:6379 | 缓存 |

### 内部服务端口

| 服务名称 | 服务器 | 端口 | 用途 |
|---------|--------|------|------|
| user-service | Server 2 | 8066 | 用户服务 |
| course-service | Server 2 | 8055 | 课程服务 |
| learning-service | Server 2 | 8033 | 学习服务 |
| ai-service | Server 3 | 8066 | AI服务 |
| admin-service | Server 3 | 8055 | 管理服务 |
| order-service | Server 3 | 8033 | 订单服务 |
| gateway | Server 2 | 8080 | API网关 |

---

## 🚀 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Node.js 16+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.x

### 本地开发

#### 1. 克隆项目

```bash
git clone <repository-url>
cd xuetu
```

#### 2. 启动基础设施

```bash
# 启动MySQL
docker run -d --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=your_password \
  mysql:8.0

# 启动Redis
docker run -d --name redis \
  -p 6379:6379 \
  redis:7-alpine

# 启动Nacos
docker run -d --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.0
```

#### 3. 导入数据库

```bash
mysql -u root -p < sql/xuetu_db.sql
```

#### 4. 配置AI服务

在 `ai-service/src/main/resources/application.yml` 中配置通义千问API密钥：

```yaml
qwen:
  api:
    key: your_api_key_here
```

#### 5. 启动后端服务

```bash
# 编译整个项目
mvn clean package -DskipTests

# 启动各个服务（按顺序）
cd gateway && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd course-service && mvn spring-boot:run &
cd learning-service && mvn spring-boot:run &
cd order-service && mvn spring-boot:run &
cd ai-service && mvn spring-boot:run &
cd admin-service && mvn spring-boot:run &
```

#### 6. 启动前端

```bash
cd xuetu-web
npm install
npm run dev
```

访问 http://localhost:5173

---

## 🌟 核心功能特性

### 1. AI智能助手 🤖

- **智能问答**: 基于通义千问AI，24小时在线答疑
- **学习路径规划**: 根据学习目标生成个性化学习路线
- **智能推荐**: 基于学习数据推荐适合的课程
- **学习分析**: AI分析学习数据，生成可视化报告
- **前端缓存优化**: 
  - 学习路径缓存24小时
  - 课程推荐缓存5分钟
  - 学习报告缓存10分钟

### 2. 课程管理 📚

- 课程分类与检索
- 视频课时播放
- 课程详情展示
- 学习进度跟踪
- 课程评论与评分

### 3. 学习功能 📖

- 学习记录追踪
- 学习进度统计
- 学习笔记管理
- 学习数据分析
- 学习成就系统（开发中）

### 4. 订单系统 🛒

- 课程购买
- 订单管理
- 支付集成（预留）
- 购物车功能

### 5. 用户系统 👤

- 用户注册/登录
- 个人信息管理
- 角色权限管理
- 学习中心

---

## 📊 数据库设计

主要数据表：

- **用户相关**: `user`, `user_role`, `role`, `permission`
- **课程相关**: `course`, `category`, `lesson`, `course_comment`
- **学习相关**: `learning_record`, `learning_note`, `learning_progress`
- **订单相关**: `order`, `order_item`
- **AI相关**: `ai_chat_history`, `ai_recommendation`

---

## 🔧 开发说明

### 后端开发规范

1. **包结构**
   ```
   com.star.{service-name}/
   ├── controller/    # 控制器层
   ├── service/       # 服务层
   ├── mapper/        # 数据访问层
   ├── entity/        # 实体类
   ├── dto/           # 数据传输对象
   ├── vo/            # 视图对象
   └── config/        # 配置类
   ```

2. **统一返回格式**
   ```java
   R.ok(data)      // 成功
   R.error(msg)    // 失败
   ```

3. **异常处理**: 全局异常处理器统一处理

### 前端开发规范

1. **组件规范**: 使用 Vue 3 Composition API
2. **状态管理**: Pinia stores
3. **API调用**: 封装在 `/src/api` 目录
4. **类型定义**: TypeScript接口定义在 `/src/types`

---

## 🎨 前端缓存优化

为提升用户体验，前端实现了智能缓存机制：

| 功能 | 缓存时间 | 说明 |
|------|---------|------|
| 学习路径生成 | 24小时 | 相同目标直接使用缓存 |
| 课程推荐 | 5分钟 | 快速响应，减少AI调用 |
| 学习报告 | 10分钟 | 数据分析结果缓存 |

**特点：**
- 自动过期管理
- 用户可选择使用缓存或重新生成
- 明确提示数据来源（缓存/实时）

---

## 🌐 部署架构

### 生产环境部署

当前项目已部署在3台阿里云服务器上，采用Docker容器化部署：

**优势：**
- ✅ 服务隔离，互不影响
- ✅ 资源分配合理
- ✅ 易于扩展和维护
- ✅ 支持独立重启

**部署流程：**
1. 打包各服务JAR文件
2. 上传到对应服务器的 `/root/xuetu/jars/` 目录
3. 使用 docker-compose 启动服务
4. 通过Nacos查看服务注册状态

详见：`deployment/DEPLOYMENT-GUIDE.md`

---

## 📝 API文档

### 主要API端点

#### 用户服务
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `GET /user/info` - 获取用户信息

#### 课程服务
- `GET /course/list` - 课程列表
- `GET /course/{id}` - 课程详情
- `GET /course/category` - 课程分类

#### AI服务
- `POST /ai/chat/ask` - AI问答
- `POST /ai/recommend/path` - 学习路径生成
- `GET /ai/recommend/courses` - 课程推荐
- `GET /ai/analysis/report` - 学习分析报告

所有API通过 Gateway (8080端口) 统一访问。

---

## 🔐 安全说明

- JWT令牌认证
- CORS跨域配置
- SQL注入防护
- XSS攻击防护
- 敏感信息加密存储

---

## 📞 联系方式

- **项目性质**: 个人学习项目
- **用途**: 技术学习与演示
- **状态**: 开发中

---

## 📜 许可证

本项目仅供学习和研究使用。

---

## 🙏 致谢

- Spring Cloud 生态
- Vue.js 社区
- Element Plus UI
- 阿里云通义千问
- ECharts 可视化

---

## 📌 版本历史

- **v1.0** (2025-10) - 初始版本，实现核心功能
- **v1.1** (2025-10) - 新增AI服务和前端缓存优化

---

**最后更新**: 2025年10月28日

