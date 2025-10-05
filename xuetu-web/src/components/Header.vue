<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo">
          <router-link to="/">
            <h1>学途在线教育</h1>
          </router-link>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/course/list" class="nav-item">课程</router-link>
          <router-link v-if="userStore.isLogin" to="/user/courses" class="nav-item">我的学习</router-link>
        </nav>

        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
        </div>

        <!-- 用户信息 -->
        <div class="user-actions">
          <!-- 购物车 -->
          <router-link v-if="userStore.isLogin" to="/cart" class="cart-icon">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>

          <!-- 用户菜单 -->
          <div v-if="userStore.isLogin" class="user-info">
            <el-dropdown @command="handleCommand">
              <div class="user-avatar">
                <el-avatar :src="userStore.userInfo?.avatar || ''" :size="32">
                  {{ userStore.userInfo?.nickname?.[0] || 'U' }}
                </el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="center">个人中心</el-dropdown-item>
                  <el-dropdown-item command="courses">我的课程</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="settings">个人设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <!-- 登录/注册按钮 -->
          <div v-else class="auth-buttons">
            <el-button type="primary" link @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

// 搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/course/list',
      query: { keyword: searchKeyword.value }
    })
  }
}

// 用户菜单操作
const handleCommand = (command: string) => {
  switch (command) {
    case 'center':
      router.push('/user/center')
      break
    case 'courses':
      router.push('/user/courses')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'settings':
      router.push('/user/settings')
      break
    case 'logout':
      userStore.logout()
      break
  }
}
</script>

<style scoped lang="scss">
.app-header {
  height: 70px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  }

  .header-content {
    height: 100%;
    display: flex;
    align-items: center;
    gap: 40px;
  }

  .logo {
    h1 {
      font-size: 24px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      cursor: pointer;
      letter-spacing: -0.5px;
      transition: all 0.3s;

      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .nav-menu {
    display: flex;
    gap: 30px;

    .nav-item {
      font-size: 16px;
      font-weight: 500;
      color: #4a5568;
      transition: all 0.3s;
      padding: 8px 16px;
      border-radius: 8px;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%) scaleX(0);
        width: 80%;
        height: 2px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        transition: transform 0.3s;
      }

      &:hover {
        color: #667eea;
        background: rgba(102, 126, 234, 0.05);
        
        &::after {
          transform: translateX(-50%) scaleX(1);
        }
      }

      &.router-link-active {
        color: #667eea;
        background: rgba(102, 126, 234, 0.1);

        &::after {
          transform: translateX(-50%) scaleX(1);
        }
      }
    }
  }

  .search-box {
    flex: 1;
    max-width: 450px;

    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 25px;
        background: #f5f7fa;
        border: 2px solid transparent;
        transition: all 0.3s;
        box-shadow: none;

        &:hover,
        &.is-focus {
          background: #fff;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }
  }

  .user-actions {
    display: flex;
    align-items: center;
    gap: 24px;

    .cart-icon {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #4a5568;
      padding: 8px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        color: #667eea;
        background: rgba(102, 126, 234, 0.1);
        transform: scale(1.1);
      }

      :deep(.el-badge__content) {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        border: 2px solid #fff;
      }
    }

    .user-avatar {
      cursor: pointer;
      transition: transform 0.3s;

      &:hover {
        transform: scale(1.1);
      }
    }

    .auth-buttons {
      display: flex;
      gap: 12px;

      .el-button {
        border-radius: 20px;
        font-weight: 500;
        padding: 10px 24px;

        &:first-child {
          color: #667eea;
          
          &:hover {
            color: #764ba2;
            background: rgba(102, 126, 234, 0.05);
          }
        }

        &:last-child {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;

          &:hover {
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            transform: translateY(-2px);
          }
        }
      }
    }
  }
}
</style>