<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h2>欢迎登录学途</h2>
          <p>开启您的学习之旅</p>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名或邮箱"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-button type="primary" link>忘记密码？</el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              native-type="submit"
              style="width: 100%"
            >
              登录
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="register-link">
              还没有账号？
              <router-link to="/register">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import type { LoginRequest } from '@/types/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const success = await userStore.login(loginForm)
      if (success) {
        // 跳转到原来的页面或首页
        const redirect = (route.query.redirect as string) || '/'
        router.push(redirect)
      }
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -20%;
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    animation: float 8s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: -10%;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
    animation: float 6s ease-in-out infinite reverse;
  }
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

.login-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.6s ease-out;

  .login-header {
    text-align: center;
    margin-bottom: 36px;

    h2 {
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 12px;
    }

    p {
      font-size: 15px;
      color: #718096;
    }
  }

  .login-form {
    :deep(.el-form-item) {
      margin-bottom: 24px;

      .el-input__wrapper {
        border-radius: 12px;
        padding: 12px 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border: 2px solid transparent;
        transition: all 0.3s;

        &:hover,
        &.is-focus {
          border-color: #667eea;
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
        }
      }
    }
  }

  .form-options {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;

    :deep(.el-checkbox) {
      .el-checkbox__label {
        color: #718096;
        font-size: 14px;
      }
    }

    .el-button {
      color: #667eea;
      font-size: 14px;

      &:hover {
        color: #764ba2;
      }
    }
  }

  .el-button[type="submit"] {
    border-radius: 12px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      transform: translateY(-2px);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .register-link {
    width: 100%;
    text-align: center;
    font-size: 14px;
    color: #718096;
    margin-top: 8px;

    a {
      color: #667eea;
      font-weight: 600;
      transition: color 0.3s;

      &:hover {
        color: #764ba2;
        text-decoration: underline;
      }
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}
</style>