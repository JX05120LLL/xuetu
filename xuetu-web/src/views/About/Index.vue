<template>
  <div class="about-page">
    <Header />
    
    <!-- 页面头部横幅 -->
    <section class="about-banner">
      <div class="banner-overlay"></div>
      <div class="container">
        <h1 class="banner-title">关于我们</h1>
        <p class="banner-subtitle">致力于打造最优质的在线学习平台</p>
      </div>
    </section>

    <div class="page-content">
      <!-- 公司简介 -->
      <section class="intro-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">学途在线教育</h2>
            <div class="title-decoration"></div>
          </div>
          <div class="intro-content">
            <div class="intro-text">
              <p class="intro-paragraph">
                学途在线教育平台成立于2024年，是一家专注于提供高质量在线教育服务的互联网企业。
                我们致力于通过技术创新，为广大学习者提供便捷、高效、个性化的学习体验。
              </p>
              <p class="intro-paragraph">
                平台汇聚了前端、后端、数据库、人工智能等多个领域的优质课程，
                拥有200+专业讲师团队，服务超过50万在线学员，累计提供1000+精品课程。
              </p>
              <p class="intro-paragraph">
                我们坚持"以学员为中心"的理念，不断优化课程内容和学习体验，
                帮助每一位学习者在知识的海洋中找到属于自己的航道。
              </p>
            </div>
            <div class="intro-image">
              <div class="image-placeholder">
                <el-icon :size="120" color="#409eff"><School /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 核心价值观 -->
      <section class="values-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">我们的价值观</h2>
            <div class="title-decoration"></div>
          </div>
          <el-row :gutter="30">
            <el-col :xs="24" :sm="12" :md="6" v-for="value in values" :key="value.title">
              <div class="value-card">
                <div class="value-icon" :style="{ background: value.color }">
                  <el-icon :size="40">
                    <component :is="value.icon" />
                  </el-icon>
                </div>
                <h3 class="value-title">{{ value.title }}</h3>
                <p class="value-description">{{ value.description }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </section>

      <!-- 平台优势 -->
      <section class="advantages-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">平台优势</h2>
            <div class="title-decoration"></div>
          </div>
          <div class="advantages-grid">
            <div class="advantage-item" v-for="advantage in advantages" :key="advantage.title">
              <div class="advantage-number">{{ advantage.number }}</div>
              <h3 class="advantage-title">{{ advantage.title }}</h3>
              <p class="advantage-description">{{ advantage.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 团队介绍 -->
      <section class="team-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">核心团队</h2>
            <div class="title-decoration"></div>
          </div>
          <el-row :gutter="30">
            <el-col :xs="24" :sm="12" :md="6" v-for="member in teamMembers" :key="member.name">
              <div class="team-card">
                <div class="team-avatar">
                  <el-avatar :size="100" :src="member.avatar">
                    <el-icon :size="50"><User /></el-icon>
                  </el-avatar>
                </div>
                <h3 class="team-name">{{ member.name }}</h3>
                <p class="team-position">{{ member.position }}</p>
                <p class="team-description">{{ member.description }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </section>

      <!-- 联系我们 -->
      <section class="contact-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">联系我们</h2>
            <div class="title-decoration"></div>
          </div>
          <div class="contact-content">
            <el-row :gutter="30">
              <el-col :xs="24" :md="12">
                <div class="contact-info">
                  <div class="contact-item" v-for="info in contactInfo" :key="info.label">
                    <div class="contact-icon">
                      <el-icon :size="24" color="#409eff">
                        <component :is="info.icon" />
                      </el-icon>
                    </div>
                    <div class="contact-detail">
                      <div class="contact-label">{{ info.label }}</div>
                      <div class="contact-value">{{ info.value }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :md="12">
                <div class="contact-form">
                  <h3 class="form-title">在线留言</h3>
                  <el-form :model="messageForm" label-width="80px">
                    <el-form-item label="姓名">
                      <el-input v-model="messageForm.name" placeholder="请输入您的姓名" />
                    </el-form-item>
                    <el-form-item label="邮箱">
                      <el-input v-model="messageForm.email" placeholder="请输入您的邮箱" />
                    </el-form-item>
                    <el-form-item label="留言">
                      <el-input
                        v-model="messageForm.message"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入您的留言"
                      />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="submitMessage">提交留言</el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </section>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { School, User, Star, TrophyBase, Discount, Service } from '@element-plus/icons-vue'
import { Location, Phone, Message, Clock } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

// 核心价值观
const values = [
  {
    icon: Star,
    title: '专业',
    description: '汇聚行业顶尖讲师，提供专业的课程内容',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    icon: TrophyBase,
    title: '卓越',
    description: '追求卓越品质，打造精品课程',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    icon: Discount,
    title: '创新',
    description: '不断创新教学方式，提升学习体验',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    icon: Service,
    title: '服务',
    description: '用心服务每一位学员，共同成长进步',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
]

// 平台优势
const advantages = [
  {
    number: '01',
    title: '海量课程资源',
    description: '涵盖前端、后端、数据库、AI等多个技术领域，1000+精品课程任你选择'
  },
  {
    number: '02',
    title: '专业讲师团队',
    description: '200+行业大咖讲师，平均10年以上从业经验，手把手教你实战技能'
  },
  {
    number: '03',
    title: '灵活学习方式',
    description: '支持PC、手机、平板多端学习，随时随地想学就学'
  },
  {
    number: '04',
    title: 'AI智能辅助',
    description: '智能推荐课程、个性化学习路径、AI答疑助手，让学习更高效'
  }
]

// 团队成员
const teamMembers = [
  {
    name: '张明',
    position: 'CEO & 创始人',
    description: '10年互联网教育经验',
    avatar: ''
  },
  {
    name: '李华',
    position: 'CTO & 技术总监',
    description: '资深全栈工程师',
    avatar: ''
  },
  {
    name: '王芳',
    position: '教学总监',
    description: '教育学硕士，8年教学经验',
    avatar: ''
  },
  {
    name: '刘强',
    position: '产品总监',
    description: '5年在线教育产品经验',
    avatar: ''
  }
]

// 联系信息
const contactInfo = [
  {
    icon: Location,
    label: '公司地址',
    value: '北京市海淀区中关村科技园'
  },
  {
    icon: Phone,
    label: '联系电话',
    value: '400-888-8888'
  },
  {
    icon: Message,
    label: '邮箱地址',
    value: 'contact@xuetu.com'
  },
  {
    icon: Clock,
    label: '工作时间',
    value: '周一至周日 9:00-21:00'
  }
]

// 留言表单
const messageForm = reactive({
  name: '',
  email: '',
  message: ''
})

// 提交留言
const submitMessage = () => {
  if (!messageForm.name || !messageForm.email || !messageForm.message) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  // TODO: 调用后端API提交留言
  ElMessage.success('留言提交成功，我们会尽快与您联系！')
  
  // 重置表单
  messageForm.name = ''
  messageForm.email = ''
  messageForm.message = ''
}
</script>

<style scoped lang="scss">
.about-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

// 顶部横幅
.about-banner {
  position: relative;
  height: 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  
  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.1)" d="M0,96L48,112C96,128,192,160,288,186.7C384,213,480,235,576,213.3C672,192,768,128,864,128C960,128,1056,192,1152,197.3C1248,203,1344,149,1392,122.7L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
    background-size: cover;
    opacity: 0.3;
  }
  
  .container {
    position: relative;
    z-index: 1;
    text-align: center;
  }
  
  .banner-title {
    font-size: 48px;
    font-weight: bold;
    color: white;
    margin-bottom: 20px;
    animation: fadeInUp 0.6s ease-out;
  }
  
  .banner-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.9);
    animation: fadeInUp 0.6s ease-out 0.2s both;
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

.page-content {
  padding: 80px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

// 章节标题
.section-header {
  text-align: center;
  margin-bottom: 60px;
  
  .section-title {
    font-size: 36px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
  }
  
  .title-decoration {
    width: 60px;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    margin: 0 auto;
    border-radius: 2px;
  }
}

// 公司简介
.intro-section {
  margin-bottom: 100px;
  
  .intro-content {
    display: flex;
    gap: 60px;
    align-items: center;
    
    .intro-text {
      flex: 1;
      
      .intro-paragraph {
        font-size: 16px;
        line-height: 1.8;
        color: #666;
        margin-bottom: 20px;
        text-indent: 2em;
      }
    }
    
    .intro-image {
      flex: 0 0 400px;
      
      .image-placeholder {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }
    }
  }
}

// 核心价值观
.values-section {
  margin-bottom: 100px;
  
  .value-card {
    background: white;
    border-radius: 12px;
    padding: 40px 30px;
    text-align: center;
    transition: all 0.3s;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    height: 100%;
    
    &:hover {
      transform: translateY(-10px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .value-icon {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 25px;
      color: white;
    }
    
    .value-title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin-bottom: 15px;
    }
    
    .value-description {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
    }
  }
}

// 平台优势
.advantages-section {
  background: white;
  padding: 80px 0;
  margin-bottom: 100px;
  
  .advantages-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
    
    .advantage-item {
      position: relative;
      padding-left: 80px;
      
      .advantage-number {
        position: absolute;
        left: 0;
        top: 0;
        font-size: 48px;
        font-weight: bold;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        opacity: 0.3;
      }
      
      .advantage-title {
        font-size: 20px;
        font-weight: bold;
        color: #333;
        margin-bottom: 10px;
      }
      
      .advantage-description {
        font-size: 14px;
        color: #666;
        line-height: 1.6;
      }
    }
  }
}

// 团队介绍
.team-section {
  margin-bottom: 100px;
  
  .team-card {
    background: white;
    border-radius: 12px;
    padding: 30px;
    text-align: center;
    transition: all 0.3s;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .team-avatar {
      margin-bottom: 20px;
    }
    
    .team-name {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin-bottom: 8px;
    }
    
    .team-position {
      font-size: 14px;
      color: #409eff;
      margin-bottom: 12px;
    }
    
    .team-description {
      font-size: 13px;
      color: #999;
    }
  }
}

// 联系我们
.contact-section {
  .contact-content {
    background: white;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
  
  .contact-info {
    .contact-item {
      display: flex;
      align-items: flex-start;
      margin-bottom: 30px;
      
      .contact-icon {
        flex: 0 0 50px;
        height: 50px;
        background: #ecf5ff;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;
      }
      
      .contact-detail {
        flex: 1;
        
        .contact-label {
          font-size: 14px;
          color: #999;
          margin-bottom: 5px;
        }
        
        .contact-value {
          font-size: 16px;
          color: #333;
          font-weight: 500;
        }
      }
    }
  }
  
  .contact-form {
    .form-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      margin-bottom: 25px;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .about-banner {
    height: 300px;
    
    .banner-title {
      font-size: 32px;
    }
    
    .banner-subtitle {
      font-size: 16px;
    }
  }
  
  .intro-content {
    flex-direction: column;
    
    .intro-image {
      flex: 0 0 auto !important;
      width: 100%;
    }
  }
  
  .advantages-grid {
    grid-template-columns: 1fr !important;
  }
}
</style>
