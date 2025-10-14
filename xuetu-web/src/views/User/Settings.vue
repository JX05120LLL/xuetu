<template>
  <div class="settings-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">个人设置</h2>

        <div class="settings-container">
          <!-- 设置选项卡 -->
          <el-tabs v-model="activeTab" class="settings-tabs">
            <el-tab-pane label="基本资料" name="profile">
              <div class="profile-form" v-loading="loading.profile">
                <el-form
                  ref="profileFormRef"
                  :model="profileForm"
                  :rules="profileRules"
                  label-width="100px"
                >
                  <el-form-item label="用户名">
                    <el-input v-model="userStore.userInfo.username" disabled />
                    <div class="form-tip">用户名不可修改</div>
                  </el-form-item>

                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                  </el-form-item>

                  <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="profileForm.gender">
                      <el-radio :label="0">保密</el-radio>
                      <el-radio :label="1">男</el-radio>
                      <el-radio :label="2">女</el-radio>
                    </el-radio-group>
                  </el-form-item>

                  <el-form-item label="生日" prop="birthday">
                    <el-date-picker
                      v-model="profileForm.birthday"
                      type="date"
                      placeholder="选择生日"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>

                  <el-form-item label="个人简介" prop="bio">
                    <el-input
                      v-model="profileForm.bio"
                      type="textarea"
                      :rows="4"
                      placeholder="介绍一下自己吧"
                    />
                  </el-form-item>

                  <el-form-item>
                    <el-button type="primary" @click="updateProfile" :loading="submitting.profile">
                      保存基本资料
                    </el-button>
                    <el-button @click="resetProfile">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>

            <el-tab-pane label="联系方式" name="contact">
              <div class="contact-form" v-loading="loading.contact">
                <el-form
                  ref="contactFormRef"
                  :model="contactForm"
                  :rules="contactRules"
                  label-width="100px"
                >
                  <el-form-item label="邮箱" prop="email">
                    <el-input v-model="contactForm.email" placeholder="请输入邮箱" />
                  </el-form-item>

                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="contactForm.phone" placeholder="请输入手机号" />
                  </el-form-item>

                  <el-form-item>
                    <el-button type="primary" @click="updateContact" :loading="submitting.contact">
                      保存联系方式
                    </el-button>
                    <el-button @click="resetContact">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>

            <el-tab-pane label="密码修改" name="password">
              <div class="password-form" v-loading="loading.password">
                <el-form
                  ref="passwordFormRef"
                  :model="passwordForm"
                  :rules="passwordRules"
                  label-width="100px"
                >
                  <el-form-item label="当前密码" prop="oldPassword">
                    <el-input
                      v-model="passwordForm.oldPassword"
                      type="password"
                      placeholder="请输入当前密码"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="请输入新密码"
                      show-password
                    />
                    <div class="password-strength" v-if="passwordForm.newPassword">
                      <div class="strength-label">密码强度:</div>
                      <div class="strength-bar">
                        <div
                          class="strength-value"
                          :style="{ width: `${passwordStrength.percent}%` }"
                          :class="passwordStrength.level"
                        ></div>
                      </div>
                      <div class="strength-text" :class="passwordStrength.level">
                        {{ passwordStrength.text }}
                      </div>
                    </div>
                  </el-form-item>

                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="请再次输入新密码"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item>
                    <el-button type="primary" @click="updatePassword" :loading="submitting.password">
                      修改密码
                    </el-button>
                    <el-button @click="resetPassword">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>

            <el-tab-pane label="通知设置" name="notification">
              <div class="notification-settings" v-loading="loading.notification">
                <el-form
                  ref="notificationFormRef"
                  :model="notificationForm"
                  label-width="100px"
                >
                  <el-form-item label="系统通知">
                    <el-switch
                      v-model="notificationForm.systemNotify"
                      active-text="接收系统通知"
                      inactive-text="关闭系统通知"
                    />
                  </el-form-item>

                  <el-form-item label="邮件通知">
                    <el-switch
                      v-model="notificationForm.emailNotify"
                      active-text="接收邮件通知"
                      inactive-text="关闭邮件通知"
                    />
                    <div class="form-tip" v-if="!contactForm.email">
                      请先设置邮箱才能接收邮件通知
                    </div>
                  </el-form-item>

                  <el-form-item label="站内消息">
                    <el-switch
                      v-model="notificationForm.messageNotify"
                      active-text="接收站内消息"
                      inactive-text="关闭站内消息"
                    />
                  </el-form-item>

                  <el-form-item>
                    <el-button 
                      type="primary" 
                      @click="updateNotification" 
                      :loading="submitting.notification"
                    >
                      保存通知设置
                    </el-button>
                    <el-button @click="resetNotification">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useUserStore } from '@/stores/user'
import { changePassword, updateNotificationSettings } from '@/api/user'
import type { ChangePasswordRequest, UpdateProfileRequest } from '@/types/user'

const userStore = useUserStore()
const activeTab = ref('profile')

// 表单引用
const profileFormRef = ref<FormInstance>()
const contactFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const notificationFormRef = ref<FormInstance>()

// 加载状态
const loading = reactive({
  profile: false,
  contact: false,
  password: false,
  notification: false
})

// 提交状态
const submitting = reactive({
  profile: false,
  contact: false,
  password: false,
  notification: false
})

// 表单数据
const profileForm = reactive({
  nickname: userStore.userInfo?.nickname || '',
  gender: userStore.userInfo?.gender || 0,
  birthday: userStore.userInfo?.birthday || '',
  bio: userStore.userInfo?.bio || ''
})

const contactForm = reactive({
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const notificationForm = reactive({
  systemNotify: true,
  emailNotify: false,
  messageNotify: true
})

// 表单验证规则
const profileRules = {
  nickname: [
    { max: 20, message: '昵称最多20个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 200, message: '个人简介最多200个字符', trigger: 'blur' }
  ]
}

const contactRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 计算密码强度
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) {
    return { level: 'weak', text: '弱', percent: 0 }
  }

  let strength = 0
  // 长度检查
  if (password.length >= 8) strength += 1
  // 包含数字
  if (/\d/.test(password)) strength += 1
  // 包含小写字母
  if (/[a-z]/.test(password)) strength += 1
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength += 1
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) strength += 1

  if (strength <= 2) {
    return { level: 'weak', text: '弱', percent: 33 }
  } else if (strength <= 4) {
    return { level: 'medium', text: '中', percent: 66 }
  } else {
    return { level: 'strong', text: '强', percent: 100 }
  }
})

// 页面加载
onMounted(() => {
  // 加载用户信息
  if (!userStore.userInfo) {
    userStore.getUserInfo()
  }

  // 更新表单数据
  resetProfile()
  resetContact()
})

// 更新基本资料
const updateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.profile = true
      try {
        // 构建更新数据
        const profileData: UpdateProfileRequest = {
          nickname: profileForm.nickname,
          gender: profileForm.gender,
          birthday: profileForm.birthday,
          bio: profileForm.bio
        }

        // 调用store更新方法
        const success = await userStore.updateProfile(profileData)
        if (success) {
          ElMessage.success('基本资料已更新')
        }
      } catch (error) {
        console.error('更新资料失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        submitting.profile = false
      }
    }
  })
}

// 更新联系方式
const updateContact = async () => {
  if (!contactFormRef.value) return

  await contactFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.contact = true
      try {
        // 构建更新数据
        const contactData: UpdateProfileRequest = {
          email: contactForm.email,
          phone: contactForm.phone
        }

        // 调用store更新方法
        const success = await userStore.updateProfile(contactData)
        if (success) {
          ElMessage.success('联系方式已更新')
        }
      } catch (error) {
        console.error('更新联系方式失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        submitting.contact = false
      }
    }
  })
}

// 修改密码
const updatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.password = true
      try {
        const passwordData: ChangePasswordRequest = {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        }

        // 调用API修改密码
        if (!userStore.userId) {
          throw new Error('用户未登录')
        }
        
        await changePassword(userStore.userId, passwordData)
        ElMessage.success('密码修改成功，请重新登录')
        resetPassword()
        
        // 密码修改成功后退出登录
        setTimeout(() => {
          userStore.logout()
        }, 1500)
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败，请检查当前密码是否正确')
      } finally {
        submitting.password = false
      }
    }
  })
}

// 更新通知设置
const updateNotification = async () => {
  submitting.notification = true
  try {
    if (!userStore.userId) {
      throw new Error('用户未登录')
    }
    
    await updateNotificationSettings(userStore.userId, notificationForm)
    ElMessage.success('通知设置已更新')
  } catch (error) {
    console.error('更新通知设置失败:', error)
    ElMessage.error('更新通知设置失败，请重试')
  } finally {
    submitting.notification = false
  }
}

// 重置表单
const resetProfile = () => {
  profileForm.nickname = userStore.userInfo?.nickname || ''
  profileForm.gender = userStore.userInfo?.gender || 0
  profileForm.birthday = userStore.userInfo?.birthday || ''
  profileForm.bio = userStore.userInfo?.bio || ''
}

const resetContact = () => {
  contactForm.email = userStore.userInfo?.email || ''
  contactForm.phone = userStore.userInfo?.phone || ''
}

const resetPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

const resetNotification = () => {
  notificationForm.systemNotify = true
  notificationForm.emailNotify = false
  notificationForm.messageNotify = true
}
</script>

<style scoped lang="scss">
.settings-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.page-content {
  padding: 30px 0 60px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 15px;
  }
  
  .page-title {
    margin-bottom: 30px;
    font-size: 24px;
    font-weight: 500;
    color: #333;
  }
}

.settings-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.settings-tabs {
  padding: 20px;
  
  :deep(.el-tabs__nav) {
    padding-left: 20px;
  }
  
  :deep(.el-tabs__content) {
    padding: 20px;
  }
}

.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

// 密码强度样式
.password-strength {
  margin-top: 10px;
  display: flex;
  align-items: center;
  
  .strength-label {
    font-size: 12px;
    color: #606266;
    margin-right: 8px;
  }
  
  .strength-bar {
    width: 100px;
    height: 6px;
    background-color: #ebeef5;
    border-radius: 3px;
    overflow: hidden;
    
    .strength-value {
      height: 100%;
      transition: all 0.3s;
      
      &.weak {
        background-color: #f56c6c;
      }
      
      &.medium {
        background-color: #e6a23c;
      }
      
      &.strong {
        background-color: #67c23a;
      }
    }
  }
  
  .strength-text {
    margin-left: 8px;
    font-size: 12px;
    
    &.weak {
      color: #f56c6c;
    }
    
    &.medium {
      color: #e6a23c;
    }
    
    &.strong {
      color: #67c23a;
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .settings-tabs {
    :deep(.el-tabs__content) {
      padding: 15px 0;
    }
  }
  
  .el-form {
    max-width: 100%;
  }
}
</style>