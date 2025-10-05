# API接口文档

## 后端服务端口

| 服务名称 | 端口 | 路径前缀 | 说明 |
|---------|------|---------|------|
| 网关服务 | 8080 | - | 前端统一访问入口 |
| 用户服务 | 8088 | /auth, /user | 认证和用户管理 |
| 课程服务 | 8077 | /course, /category, /chapter, /lesson, /comment | 课程相关 |
| 订单服务 | 8033 | /api/orders, /api/payments, /api/user-courses | 订单和支付 |
| 学习服务 | 8044 | /learning/record, /learning/note | 学习记录和笔记 |
| AI服务 | 8066 | /chat, /analysis, /recommend | AI功能 |
| 管理服务 | 8055 | /role, /permission | 权限管理 |

## 统一响应格式

```typescript
{
  code: number,      // 200成功，其他失败
  message: string,   // 响应消息
  data: any         // 响应数据
}
```

## 状态码说明

- `200` - 成功
- `400` - 操作失败/参数错误
- `401` - 未认证/Token无效
- `403` - 未授权
- `404` - 资源不存在
- `500` - 服务器内部错误

## 分页格式

### 请求参数
```typescript
{
  current: number,   // 当前页码，从1开始
  size: number       // 每页大小
}
```

### 响应数据 (IPage)
```typescript
{
  records: T[],      // 数据列表
  total: number,     // 总记录数
  current: number,   // 当前页
  size: number,      // 每页大小
  pages: number      // 总页数
}
```

## API模块说明

### 1. 认证模块 (auth)
- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录
- `GET /auth/check-username` - 检查用户名
- `GET /auth/check-email` - 检查邮箱

### 2. 用户模块 (user)
- `GET /user/{id}` - 获取用户信息
- `POST /user/{id}/change-password` - 修改密码

### 3. 课程模块 (course)
- `GET /course/list` - 课程列表（分页）
- `GET /course/{id}` - 课程详情
- `GET /course/hot` - 热门课程
- `GET /course/search` - 搜索课程
- `POST /course/{id}/student-count` - 增加学习人数

### 4. 分类模块 (category)
- `GET /category/list` - 分类列表
- `GET /category/tree` - 分类树
- `GET /category/{id}` - 分类详情

### 5. 章节模块 (chapter)
- `GET /chapter/course/{courseId}` - 获取课程章节

### 6. 课时模块 (lesson)
- `GET /lesson/chapter/{chapterId}` - 获取章节课时

### 7. 评论模块 (comment)
- `GET /comment/list` - 评论列表

### 8. 订单模块 (orders)
- `POST /api/orders` - 创建订单
- `GET /api/orders/my` - 我的订单
- `GET /api/orders/{id}` - 订单详情
- `PUT /api/orders/{id}/cancel` - 取消订单

### 9. 支付模块 (payments)
- `POST /api/payments` - 创建支付
- `POST /api/payments/{paymentNo}/simulate` - 模拟支付
- `GET /api/payments/{paymentNo}` - 查询支付记录

### 10. 用户课程模块 (user-courses)
- `GET /api/user-courses/my` - 我的课程

### 11. 学习记录模块 (learning/record)
- `POST /learning/record/progress` - 更新学习进度
- `GET /learning/record/course/{courseId}` - 获取课程学习记录
- `GET /learning/record/stats` - 获取学习统计

### 12. 笔记模块 (learning/note)
- `GET /learning/note/my` - 我的笔记
- `POST /learning/note` - 创建笔记
- `PUT /learning/note/{id}` - 更新笔记
- `DELETE /learning/note/{id}` - 删除笔记

## 注意事项

1. **所有请求通过网关(8080)访问**
2. **需要认证的接口要携带Token**: `Authorization: Bearer {token}`
3. **参数名称严格遵循后端定义**，如分页参数必须是 `current` 和 `size`
4. **时间字段使用驼峰命名**，如 `createdTime`, `updatedTime`
5. **状态值使用数字类型**，不是字符串
6. **ID类型统一使用 number (Long)**

## 开发建议

- 使用TypeScript编写，享受类型检查
- 所有API调用都要有错误处理
- 响应拦截器已统一处理401跳转登录
- 请求超时时间设置为10秒