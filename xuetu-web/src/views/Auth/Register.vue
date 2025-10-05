<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <h2>注册学途账号</h2>
          <p>创建您的学习账户</p>
        </div>

        <el-form
          ref="formRef"
          :model="registerForm"
          :rules="rules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              :prefix-icon="Message"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号（可选）"
              :prefix-icon="Phone"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="agree">
            <el-checkbox v-model="registerForm.agree">
              我已阅读并同意
              <el-button type="primary" link>《用户协议》</el-button>
              和
              <el-button type="primary" link>《隐私政策》</el-button>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              native-type="submit"
              style="width: 100%"
            >
              注册
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="login-link">
              已有账号？
              <router-link to="/login">立即登录</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import type { RegisterRequest } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive<RegisterRequest & { confirmPassword: string; agree: boolean }>({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgree = (rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agree: [
    { validator: validateAgree, trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const success = await userStore.register({
        username: registerForm.username,
        email: registerForm.email,
        phone: registerForm.phone,
        password: registerForm.password
      })
      
      if (success) {
        router.push('/login')
      }
    } catch (error) {
      console.error('注册失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -30%;
    right: -15%;
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    animation: float 8s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -20%;
    left: -10%;
    width: 350px;
    height: 350px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
    animation: float 6s ease-in-out infinite reverse;
  }
}

.register-container {
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.register-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.6s ease-out;

  .register-header {
    text-align: center;
    margin-bottom: 36px;

    h2 {
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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

  .register-form {
    :deep(.el-form-item) {
      margin-bottom: 20px;

      .el-input__wrapper {
        border-radius: 12px;
        padding: 12px 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border: 2px solid transparent;
        transition: all 0.3s;

        &:hover,
        &.is-focus {
          border-color: #f093fb;
          box-shadow: 0 4px 12px rgba(240, 147, 251, 0.2);
        }
      }

      .el-checkbox__label {
        color: #718096;
        font-size: 13px;
        line-height: 1.6;
      }
    }
  }

  .el-button[type="submit"] {
    border-radius: 12px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    border: none;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 8px 20px rgba(240, 147, 251, 0.4);
      transform: translateY(-2px);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-link {
    width: 100%;
    text-align: center;
    font-size: 14px;
    color: #718096;
    margin-top: 8px;

    a {
      color: #f093fb;
      font-weight: 600;
      transition: color 0.3s;

      &:hover {
        color: #f5576c;
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