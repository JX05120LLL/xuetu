import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home/Index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/course',
    children: [
      {
        path: 'list',
        name: 'CourseList',
        component: () => import('@/views/Course/List.vue'),
        meta: { title: '课程列表' }
      },
      {
        path: ':id',
        name: 'CourseDetail',
        component: () => import('@/views/Course/Detail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: ':id/play',
        name: 'CoursePlay',
        component: () => import('@/views/Course/Play.vue'),
        meta: { title: '课程播放', requiresAuth: true }
      }
    ]
  },
  {
    path: '/order',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/Order/Confirm.vue'),
        meta: { title: '确认订单' }
      },
      {
        path: 'pay/:orderNo',
        name: 'OrderPay',
        component: () => import('@/views/Order/Pay.vue'),
        meta: { title: '订单支付' }
      },
      {
        path: 'result/:orderNo',
        name: 'OrderResult',
        component: () => import('@/views/Order/Result.vue'),
        meta: { title: '支付结果' }
      },
      {
        path: 'list',
        name: 'OrderList',
        component: () => import('@/views/Order/List.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: ':orderId',
        name: 'OrderDetail',
        component: () => import('@/views/Order/Detail.vue'),
        meta: { title: '订单详情' }
      }
    ]
  },
  {
    path: '/user',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'center',
        name: 'UserCenter',
        component: () => import('@/views/User/Center.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'courses',
        name: 'UserCourses',
        component: () => import('@/views/User/Courses.vue'),
        meta: { title: '我的课程' }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/Order/List.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'notes',
        name: 'UserNotes',
        component: () => import('@/views/User/Notes.vue'),
        meta: { title: '我的笔记' }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/views/User/Settings.vue'),
        meta: { title: '个人设置' }
      }
    ]
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart/Index.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/ai',
    name: 'AIDashboard',
    component: () => import('@/views/AI/Dashboard.vue'),
    meta: { title: 'AI助手', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学途在线教育平台` : '学途在线教育平台'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLogin) {
    ElMessage.warning('请先登录')
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  next()
})

export default router