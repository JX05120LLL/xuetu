# 学途在线教育平台 - 用户端

## 项目简介

这是学途在线教育平台的用户端前端项目，基于 Vue 3 + TypeScript + Vite + Element Plus 开发。

## 技术栈

- **框架**: Vue 3.3
- **语言**: TypeScript 5.2
- **构建工具**: Vite 5.0
- **UI组件库**: Element Plus 2.4
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP请求**: Axios 1.6
- **CSS预处理器**: SCSS

## 项目结构

```
xuetu-web/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   ├── assets/            # 资源文件
│   │   └── styles/       # 样式文件
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   ├── stores/            # 状态管理
│   ├── types/             # TypeScript类型
│   ├── utils/             # 工具函数
│   ├── views/             # 页面视图
│   ├── App.vue
│   └── main.ts
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

项目将运行在 `http://localhost:5173`

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 环境变量

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=学途在线教育平台
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=https://api.xuetu.com
VITE_APP_TITLE=学途在线教育平台
```

## 后端服务配置

确保以下后端服务已启动：

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| 网关服务 | 8080 | API网关（前端统一访问入口） |
| 用户服务 | 8088 | 用户认证、用户管理 |
| 课程服务 | 8077 | 课程、分类、章节、评论 |
| 订单服务 | 8033 | 订单、支付、用户课程 |
| 学习服务 | 8044 | 学习记录、笔记 |
| AI服务 | 8065 | AI对话、推荐、分析 |

**注意**: 前端通过网关 (8080端口) 访问所有后端服务

## 核心功能模块

### 已实现

- ✅ 项目初始化和配置
- ✅ 路由配置
- ✅ API封装和请求拦截
- ✅ 用户登录/注册
- ✅ 首页布局
- ✅ 课程卡片组件
- ✅ 购物车状态管理
- ✅ 用户状态管理

### 待实现

- ⏳ 课程列表页
- ⏳ 课程详情页
- ⏳ 课程播放页
- ⏳ 购物车页面
- ⏳ 订单确认和支付
- ⏳ 个人中心
- ⏳ 我的课程
- ⏳ 学习记录
- ⏳ 笔记管理
- ⏳ AI助手

## 开发规范

### 命名规范

- **文件命名**: PascalCase (如: `UserProfile.vue`)
- **组件命名**: PascalCase (如: `CourseCard`)
- **路由命名**: kebab-case (如: `/user-center`)
- **API函数**: camelCase (如: `getUserInfo`)

### 代码规范

- 使用 TypeScript 严格模式
- 组件必须有注释说明
- API调用必须有错误处理
- 使用 Composition API

### Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
```

## API接口说明

### 统一响应格式

```typescript
{
  code: 200,        // 200成功，其他失败
  message: "成功",  // 响应消息
  data: {}          // 响应数据
}
```

### 分页格式

```typescript
{
  current: 1,       // 当前页码
  size: 10,         // 每页大小
  total: 100,       // 总记录数
  records: []       // 数据列表
}
```

## 常见问题

### 1. 启动失败

确保 Node.js 版本 >= 16.0

### 2. API请求失败

检查后端服务是否启动，网关端口是否正确(8080)

### 3. 跨域问题

已在 `vite.config.ts` 中配置代理，如有问题请检查配置

## 参考文档

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/zh-CN/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)
- [Vite 文档](https://cn.vitejs.dev/)

## License

MIT

---

**开发团队**: 学途开发组
**最后更新**: 2025-10-05