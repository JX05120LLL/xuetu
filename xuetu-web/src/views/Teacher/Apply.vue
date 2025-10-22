<template>
  <div class="teacher-apply-page">
    <Header />
    
    <div class="page-content">
      <!-- 头部Banner -->
      <section class="apply-banner">
        <div class="container">
          <div class="banner-content">
            <h1 class="banner-title">成为讲师，分享你的知识</h1>
            <p class="banner-subtitle">在学途平台开启你的教学之旅，影响更多学习者</p>
            <div class="banner-stats">
              <div class="stat-item">
                <div class="stat-number">10000+</div>
                <div class="stat-label">活跃学员</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">500+</div>
                <div class="stat-label">认证讲师</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">98%</div>
                <div class="stat-label">学员满意度</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 讲师权益 -->
      <section class="benefits-section">
        <div class="container">
          <h2 class="section-title">成为讲师的优势</h2>
          <div class="benefits-grid">
            <div class="benefit-card" v-for="benefit in benefits" :key="benefit.id">
              <div class="benefit-icon" :style="{ background: benefit.gradient }">
                <el-icon :size="32">
                  <component :is="benefit.icon" />
                </el-icon>
              </div>
              <h3>{{ benefit.title }}</h3>
              <p>{{ benefit.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 申请流程 -->
      <section class="process-section">
        <div class="container">
          <h2 class="section-title">申请流程</h2>
          <div class="process-steps">
            <div class="step-item" v-for="(step, index) in steps" :key="step.id">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <h3>{{ step.title }}</h3>
                <p>{{ step.description }}</p>
              </div>
              <div class="step-arrow" v-if="index < steps.length - 1">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 申请表单 -->
      <section class="form-section">
        <div class="container">
          <div class="form-container">
            <h2 class="form-title">讲师申请表</h2>
            <p class="form-subtitle">请填写以下信息，我们会在3个工作日内审核并与您联系</p>
            
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="120px"
              class="apply-form"
            >
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入您的真实姓名" />
              </el-form-item>

              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入手机号码" />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱地址" />
              </el-form-item>

              <el-form-item label="擅长领域" prop="expertise">
                <el-select v-model="form.expertise" placeholder="请选择您的擅长领域" multiple>
                  <el-option label="前端开发" value="frontend" />
                  <el-option label="后端开发" value="backend" />
                  <el-option label="移动开发" value="mobile" />
                  <el-option label="数据库" value="database" />
                  <el-option label="人工智能" value="ai" />
                  <el-option label="大数据" value="bigdata" />
                  <el-option label="云计算" value="cloud" />
                  <el-option label="网络安全" value="security" />
                  <el-option label="UI/UX设计" value="design" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>

              <el-form-item label="工作年限" prop="experience">
                <el-select v-model="form.experience" placeholder="请选择您的工作年限">
                  <el-option label="1年以下" value="0-1" />
                  <el-option label="1-3年" value="1-3" />
                  <el-option label="3-5年" value="3-5" />
                  <el-option label="5-10年" value="5-10" />
                  <el-option label="10年以上" value="10+" />
                </el-select>
              </el-form-item>

              <el-form-item label="教学经验" prop="teachingExperience">
                <el-radio-group v-model="form.teachingExperience">
                  <el-radio label="yes">有教学经验</el-radio>
                  <el-radio label="no">无教学经验</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="个人简介" prop="bio">
                <el-input
                  v-model="form.bio"
                  type="textarea"
                  :rows="6"
                  placeholder="请简单介绍您的工作经历、技术能力和教学理念（200字以内）"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="个人作品" prop="portfolio">
                <el-input
                  v-model="form.portfolio"
                  type="textarea"
                  :rows="4"
                  placeholder="请提供您的个人作品链接、GitHub地址、技术博客等（选填）"
                />
              </el-form-item>

              <el-form-item>
                <div class="form-footer">
                  <el-checkbox v-model="form.agree">
                    我已阅读并同意
                    <el-link type="primary" @click="showAgreement">《讲师入驻协议》</el-link>
                  </el-checkbox>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="submitting"
                  :disabled="!form.agree"
                  @click="handleSubmit"
                  class="submit-btn"
                >
                  提交申请
                </el-button>
                <el-button size="large" @click="handleReset">
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </section>

      <!-- FAQ -->
      <section class="faq-section">
        <div class="container">
          <h2 class="section-title">常见问题</h2>
          <el-collapse v-model="activeFaq" class="faq-collapse">
            <el-collapse-item
              v-for="faq in faqs"
              :key="faq.id"
              :title="faq.question"
              :name="faq.id"
            >
              <div class="faq-answer">{{ faq.answer }}</div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </section>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { ArrowRight, Money, TrendCharts, Timer, Star } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

// 讲师权益
const benefits = ref([
  {
    id: 1,
    title: '知识变现',
    description: '通过课程销售获得收益，实现知识价值的转化',
    icon: 'Money',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 2,
    title: '个人品牌',
    description: '提升个人影响力，建立行业专家形象',
    icon: 'TrendCharts',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 3,
    title: '灵活时间',
    description: '自由安排教学时间，工作学习两不误',
    icon: 'Timer',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    id: 4,
    title: '平台支持',
    description: '提供课程制作指导和技术支持',
    icon: 'Star',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 申请流程
const steps = ref([
  {
    id: 1,
    title: '提交申请',
    description: '填写申请表单，提交个人信息和教学意向'
  },
  {
    id: 2,
    title: '资质审核',
    description: '平台审核您的资质和教学能力'
  },
  {
    id: 3,
    title: '签约入驻',
    description: '审核通过后签署协议，正式成为讲师'
  },
  {
    id: 4,
    title: '开始授课',
    description: '创建课程，开启您的教学之旅'
  }
])

// 常见问题
const faqs = ref([
  {
    id: 1,
    question: '成为讲师需要什么条件？',
    answer: '需要具备相关领域的专业知识和工作经验，有教学热情和良好的沟通能力。有教学经验者优先，但不是必须条件。'
  },
  {
    id: 2,
    question: '申请审核需要多长时间？',
    answer: '我们会在收到申请后的3个工作日内完成初步审核，并通过邮件或电话与您联系。'
  },
  {
    id: 3,
    question: '讲师收益如何计算？',
    answer: '讲师可获得课程销售收入的70%作为收益，平台会在每月15日结算上月收益。'
  },
  {
    id: 4,
    question: '需要自己制作课程视频吗？',
    answer: '是的，讲师需要自行录制课程视频。我们会提供课程制作指南和技术支持，帮助您制作高质量的课程内容。'
  },
  {
    id: 5,
    question: '可以同时在其他平台授课吗？',
    answer: '可以，我们不限制讲师在其他平台授课，但希望您在学途平台上提供独家或优质的课程内容。'
  }
])

const activeFaq = ref([1])

// 表单数据
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  name: '',
  phone: '',
  email: '',
  expertise: [] as string[],
  experience: '',
  teachingExperience: '',
  bio: '',
  portfolio: '',
  agree: false
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  expertise: [
    { required: true, message: '请至少选择一个擅长领域', trigger: 'change' }
  ],
  experience: [
    { required: true, message: '请选择工作年限', trigger: 'change' }
  ],
  teachingExperience: [
    { required: true, message: '请选择是否有教学经验', trigger: 'change' }
  ],
  bio: [
    { required: true, message: '请填写个人简介', trigger: 'blur' },
    { min: 20, max: 200, message: '个人简介长度在20-200字之间', trigger: 'blur' }
  ]
}

// 提交申请
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // TODO: 调用后端API提交申请
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        ElMessageBox.alert(
          '您的申请已成功提交！我们会在3个工作日内完成审核，并通过邮件或电话与您联系。请保持手机和邮箱畅通。',
          '提交成功',
          {
            confirmButtonText: '确定',
            type: 'success',
            callback: () => {
              handleReset()
            }
          }
        )
      } catch (error) {
        console.error('提交申请失败:', error)
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  form.agree = false
}

// 显示协议
const showAgreement = () => {
  ElMessageBox.alert(
    `
    1. 讲师应保证所提供的个人信息真实有效。
    2. 讲师承诺课程内容为原创或已获得合法授权。
    3. 讲师同意平台对课程内容进行审核。
    4. 课程收益按照平台规则进行分成和结算。
    5. 讲师应遵守平台的教学规范和服务标准。
    6. 双方可根据协议约定解除合作关系。
    `,
    '讲师入驻协议',
    {
      confirmButtonText: '我已阅读',
      dangerouslyUseHTMLString: false
    }
  )
}
</script>

<style scoped lang="scss">
.teacher-apply-page {
  min-height: 100vh;
  background: #f5f7fa;
}

// Banner区域
.apply-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><circle cx="50" cy="50" r="2" fill="rgba(255,255,255,0.1)"/></svg>');
    opacity: 0.3;
  }
  
  .banner-content {
    position: relative;
    z-index: 1;
    text-align: center;
  }
  
  .banner-title {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 20px;
  }
  
  .banner-subtitle {
    font-size: 20px;
    opacity: 0.95;
    margin-bottom: 60px;
  }
  
  .banner-stats {
    display: flex;
    justify-content: center;
    gap: 80px;
    
    .stat-item {
      .stat-number {
        font-size: 36px;
        font-weight: 700;
        display: block;
        margin-bottom: 8px;
      }
      
      .stat-label {
        font-size: 16px;
        opacity: 0.9;
      }
    }
  }
}

// 通用section样式
.page-content {
  section {
    padding: 80px 0;
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .section-title {
    text-align: center;
    font-size: 36px;
    font-weight: 700;
    margin-bottom: 60px;
    color: #333;
  }
}

// 权益卡片
.benefits-section {
  background: white;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.benefit-card {
  text-align: center;
  padding: 40px 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .benefit-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 24px;
    color: white;
  }
  
  h3 {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 12px;
    color: #333;
  }
  
  p {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
  }
}

// 流程步骤
.process-section {
  background: #f9fafb;
}

.process-steps {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1000px;
  margin: 0 auto;
}

.step-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  
  .step-number {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: 700;
    flex-shrink: 0;
  }
  
  .step-content {
    flex: 1;
    
    h3 {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #333;
    }
    
    p {
      font-size: 14px;
      color: #666;
      line-height: 1.5;
    }
  }
  
  .step-arrow {
    color: #d1d5db;
    font-size: 24px;
    margin: 0 20px;
  }
}

// 表单区域
.form-section {
  background: white;
}

.form-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 60px;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 12px;
  color: #333;
}

.form-subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 40px;
}

.apply-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .form-footer {
    margin-bottom: 24px;
  }
  
  .submit-btn {
    width: 200px;
    height: 48px;
    font-size: 16px;
  }
}

// FAQ区域
.faq-section {
  background: #f9fafb;
}

.faq-collapse {
  max-width: 900px;
  margin: 0 auto;
  
  :deep(.el-collapse-item__header) {
    font-size: 16px;
    font-weight: 600;
    padding: 20px;
  }
  
  .faq-answer {
    padding: 20px;
    color: #666;
    line-height: 1.8;
  }
}

// 响应式
@media (max-width: 768px) {
  .apply-banner {
    padding: 60px 0;
    
    .banner-title {
      font-size: 32px;
    }
    
    .banner-subtitle {
      font-size: 16px;
    }
    
    .banner-stats {
      flex-direction: column;
      gap: 30px;
    }
  }
  
  .benefits-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .process-steps {
    flex-direction: column;
    
    .step-item {
      width: 100%;
    }
    
    .step-arrow {
      transform: rotate(90deg);
      margin: 20px 0;
    }
  }
  
  .form-container {
    padding: 30px 20px;
  }
}
</style>
