<template>
  <div class="home-page">
    <Header />
    
    <main class="main-content">
      <!-- Banner轮播图 -->
      <section class="banner-section">
        <el-carousel height="560px" indicator-position="outside">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item" :class="`banner-${item.id}`">
              <!-- 背景图案层 -->
              <div class="banner-bg-pattern"></div>
              
              <!-- 代码装饰层 -->
              <div class="banner-code-decoration">
                <div class="code-line" v-for="i in 8" :key="i" :style="{ animationDelay: `${i * 0.3}s` }">
                  <span class="code-symbol">{{ item.codeSymbols[i % item.codeSymbols.length] }}</span>
                </div>
              </div>
              
              <!-- 几何图形层 -->
              <div class="banner-shapes">
                <div class="shape shape-1"></div>
                <div class="shape shape-2"></div>
                <div class="shape shape-3"></div>
                <div class="shape shape-4"></div>
              </div>
              
              <!-- 粒子效果 -->
              <div class="particles">
                <div class="particle" v-for="i in 15" :key="i" :style="{
                  left: `${Math.random() * 100}%`,
                  top: `${Math.random() * 100}%`,
                  animationDelay: `${Math.random() * 5}s`,
                  animationDuration: `${3 + Math.random() * 4}s`
                }"></div>
              </div>
              
              <div class="container">
                <div class="banner-content">
                  <div class="badge-container">
                    <div class="badge">
                      <span class="badge-icon">{{ item.icon }}</span>
                      <span class="badge-text">{{ item.badge }}</span>
                    </div>
                  </div>
                  
                  <h2 class="banner-title animate-fade-in">
                    <span class="title-main">{{ item.titleMain }}</span>
                    <span class="title-highlight">{{ item.titleHighlight }}</span>
                  </h2>
                  
                  <p class="banner-description animate-fade-in-delay">
                    {{ item.description }}
                  </p>
                  
                  <div class="banner-stats animate-fade-in-delay">
                    <div class="stat-item" v-for="stat in item.stats" :key="stat.label">
                      <span class="stat-number">{{ stat.value }}</span>
                      <span class="stat-label">{{ stat.label }}</span>
                    </div>
                  </div>
                  
                  <div class="banner-actions animate-fade-in-delay-2">
                  <el-button 
                    type="primary" 
                    size="large" 
                      class="banner-btn primary-btn"
                    @click="router.push('/course/list')"
                  >
                      {{ item.buttonText }}
                      <el-icon class="btn-icon"><ArrowRight /></el-icon>
                    </el-button>
                    <el-button 
                      size="large" 
                      class="banner-btn secondary-btn"
                      @click="router.push('/about')"
                    >
                      了解更多
                  </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 快速入口 -->
      <section class="quick-access-section">
        <div class="container">
          <div class="quick-access-grid">
            <div 
              v-for="item in quickAccess" 
              :key="item.id"
              class="access-card"
              @click="handleAccessClick(item.path)"
            >
              <div class="access-icon" :style="{ background: item.gradient }">
                <el-icon :size="40">
                  <component :is="item.icon" />
                </el-icon>
              </div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 精选课程 -->
      <section class="featured-section">
        <div class="container">
          <div class="section-header">
            <div class="header-left">
              <h2>精选好课</h2>
              <p class="subtitle">严选优质课程，助力高效学习</p>
            </div>
            <router-link to="/course/list" class="more-link">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          
          <div class="course-grid-small" v-loading="loading">
            <CourseCard
              v-for="course in hotCourses"
              :key="course.id"
              :course="course"
            />
          </div>
        </div>
      </section>

      <!-- 平台数据统计 -->
      <section class="stats-section">
        <div class="container">
          <div class="stats-grid">
            <div v-for="stat in platformStats" :key="stat.label" class="stat-item">
              <div class="stat-icon">
                <el-icon :size="36">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowRight,
  Compass,
  Document,
  ChatDotRound,
  UserFilled,
  User,
  Timer,
  Trophy
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import CourseCard from '@/components/CourseCard.vue'
import { getCourseList } from '@/api/course'
import type { Course } from '@/types/course'

const router = useRouter()

const loading = ref(false)
const hotCourses = ref<Course[]>([])

// Banner数据
const banners = ref([
  {
    id: 1,
    icon: '🎓',
    badge: '在线教育',
    titleMain: '开启你的',
    titleHighlight: '学习之旅',
    description: '海量优质课程，涵盖前端、后端、数据库、人工智能等多个领域',
    buttonText: '立即探索',
    stats: [
      { value: '1000+', label: '精品课程' },
      { value: '50W+', label: '在线学员' },
      { value: '200+', label: '名师团队' }
    ],
    codeSymbols: ['{ }', '< />', '[ ]', '( )', '=>', '===', '++', '&&']
  },
  {
    id: 2,
    icon: '💡',
    badge: '实战导向',
    titleMain: '从理论到',
    titleHighlight: '实践',
    description: '专业讲师团队倾力打造，企业级项目实战经验全面分享',
    buttonText: '开始学习',
    stats: [
      { value: '95%', label: '就业率' },
      { value: '500+', label: '合作企业' },
      { value: '4.9', label: '课程评分' }
    ],
    codeSymbols: ['function', 'class', 'const', 'import', 'export', 'async', 'await', 'return']
  },
  {
    id: 3,
    icon: '🚀',
    badge: '高效学习',
    titleMain: '随时随地',
    titleHighlight: '想学就学',
    description: '支持多平台学习，AI智能辅助，让你的学习更高效',
    buttonText: '加入我们',
    stats: [
      { value: '24/7', label: '在线答疑' },
      { value: 'AI', label: '智能助手' },
      { value: '∞', label: '学习时长' }
    ],
    codeSymbols: ['console.log', 'useState', 'useEffect', 'map', 'filter', 'forEach', 'reduce', 'promise']
  }
])

// 快速入口配置
const quickAccess = ref([
  {
    id: 1,
    title: '浏览课程',
    description: '海量优质课程等你来学',
    icon: 'Compass',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    path: '/course/list'
  },
  {
    id: 2,
    title: '我的学习',
    description: '查看学习进度和记录',
    icon: 'Document',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    path: '/user/courses'
  },
  {
    id: 3,
    title: 'AI助手',
    description: '智能推荐个性化学习',
    icon: 'ChatDotRound',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    path: '/ai'
  },
  {
    id: 4,
    title: '成为讲师',
    description: '分享知识，实现价值',
    icon: 'UserFilled',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    path: '/teacher/apply'
  }
])

// 平台数据统计
const platformStats = ref([
  { icon: 'User', value: '50000+', label: '注册学员' },
  { icon: 'Reading', value: '1000+', label: '优质课程' },
  { icon: 'Timer', value: '100万+', label: '学习时长' },
  { icon: 'Trophy', value: '98%', label: '好评率' }
])

// 快速入口点击
const handleAccessClick = (path: string) => {
  router.push(path)
}

// 获取精选课程（减少到4门）
const fetchHotCourses = async () => {
  loading.value = true
  try {
    const res = await getCourseList({ 
      current: 1, 
      size: 4,  // 改为4门精选课程
      status: 1
    })
    hotCourses.value = res.records
  } catch (error) {
    console.error('获取精选课程失败:', error)
    hotCourses.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchHotCourses()
})
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: linear-gradient(to bottom, #f5f7fa 0%, #e8eef5 100%);
}

.banner-section {
  :deep(.el-carousel__indicator) {
    .el-carousel__button {
      background: rgba(255, 255, 255, 0.5);
    }
    
    &.is-active .el-carousel__button {
      background: #fff;
    }
  }

  .banner-item {
    height: 560px;
    display: flex;
    align-items: center;
    position: relative;
    overflow: hidden;

    // Banner 1 - 紫蓝科技风
    &.banner-1 {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      .banner-bg-pattern {
        background-image: 
          radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
          radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.08) 0%, transparent 50%),
          repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(255, 255, 255, 0.03) 2px, rgba(255, 255, 255, 0.03) 4px);
      }

      .shape-1 {
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
        border-radius: 50%;
        top: -250px;
        right: -150px;
        animation: float 8s ease-in-out infinite;
      }

      .shape-2 {
        width: 350px;
        height: 350px;
        background: rgba(255, 255, 255, 0.06);
        border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
        bottom: -150px;
        left: -100px;
        animation: morph 12s ease-in-out infinite;
      }

      .shape-3 {
        width: 250px;
        height: 250px;
        border: 2px solid rgba(255, 255, 255, 0.15);
        border-radius: 50%;
        top: 45%;
        right: 12%;
        animation: pulse 5s ease-in-out infinite;
      }

      .shape-4 {
        width: 150px;
        height: 150px;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), transparent);
        border-radius: 20px;
        top: 15%;
        right: 35%;
        animation: rotate 20s linear infinite;
      }
    }

    // Banner 2 - 粉红活力风
    &.banner-2 {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);

      .banner-bg-pattern {
        background-image: 
          repeating-linear-gradient(45deg, transparent, transparent 40px, rgba(255, 255, 255, 0.02) 40px, rgba(255, 255, 255, 0.02) 80px),
          repeating-linear-gradient(-45deg, transparent, transparent 40px, rgba(255, 255, 255, 0.02) 40px, rgba(255, 255, 255, 0.02) 80px);
      }

      .shape-1 {
        width: 400px;
        height: 400px;
        background: linear-gradient(45deg, rgba(255, 255, 255, 0.12), transparent);
        transform: rotate(45deg);
        top: -150px;
        right: 8%;
        animation: rotate 25s linear infinite;
      }

      .shape-2 {
        width: 300px;
        height: 300px;
        background: rgba(255, 255, 255, 0.08);
        border-radius: 40% 60% 70% 30% / 40% 50% 60% 50%;
        bottom: -100px;
        left: 8%;
        animation: morph 10s ease-in-out infinite;
      }

      .shape-3 {
        width: 180px;
        height: 180px;
        border: 3px solid rgba(255, 255, 255, 0.2);
        border-radius: 50%;
        top: 28%;
        right: 18%;
        animation: pulse 4s ease-in-out infinite;
      }

      .shape-4 {
        width: 120px;
        height: 120px;
        background: rgba(255, 255, 255, 0.1);
        clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
        top: 60%;
        right: 30%;
        animation: float 6s ease-in-out infinite;
      }
    }

    // Banner 3 - 青蓝清新风
    &.banner-3 {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);

      .banner-bg-pattern {
        background-image: 
          linear-gradient(30deg, rgba(255, 255, 255, 0.04) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.04) 87.5%),
          linear-gradient(150deg, rgba(255, 255, 255, 0.04) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.04) 87.5%),
          linear-gradient(30deg, rgba(255, 255, 255, 0.04) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.04) 87.5%),
          linear-gradient(150deg, rgba(255, 255, 255, 0.04) 12%, transparent 12.5%, transparent 87%, rgba(255, 255, 255, 0.04) 87.5%);
        background-size: 80px 140px;
        background-position: 0 0, 0 0, 40px 70px, 40px 70px;
      }

      .shape-1 {
        width: 380px;
        height: 380px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
        border-radius: 50%;
        top: -80px;
        right: 3%;
        animation: pulse 6s ease-in-out infinite;
      }

      .shape-2 {
        width: 220px;
        height: 220px;
        background: rgba(255, 255, 255, 0.1);
        clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
        bottom: 40px;
        left: 12%;
        animation: rotate 18s linear infinite;
      }

      .shape-3 {
        width: 200px;
        height: 200px;
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.12), transparent);
        border-radius: 25px;
        top: 38%;
        right: 22%;
        animation: float 8s ease-in-out infinite;
      }

      .shape-4 {
        width: 140px;
        height: 140px;
        border: 2px solid rgba(255, 255, 255, 0.2);
        border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
        top: 18%;
        left: 20%;
        animation: morph 9s ease-in-out infinite;
      }
    }

    // 背景图案层
    .banner-bg-pattern {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 0;
      opacity: 0.6;
    }

    // 代码装饰层
    .banner-code-decoration {
      position: absolute;
      top: 0;
      right: 0;
      width: 50%;
      height: 100%;
      overflow: hidden;
      z-index: 0;
      opacity: 0.15;
      pointer-events: none;

      .code-line {
        position: absolute;
        right: -20%;
        white-space: nowrap;
        font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
        font-size: 18px;
        color: #fff;
        animation: slideCode 15s linear infinite;
        
        @for $i from 1 through 8 {
          &:nth-child(#{$i}) {
            top: 10% + ($i - 1) * 12%;
          }
        }

        .code-symbol {
          display: inline-block;
          padding: 0 8px;
          opacity: 0.8;
        }
      }
    }

    // 几何图形层
    .banner-shapes {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 0;

      .shape {
        position: absolute;
        pointer-events: none;
      }
    }

    // 粒子效果
    .particles {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 0;
      pointer-events: none;

      .particle {
        position: absolute;
        width: 4px;
        height: 4px;
        background: rgba(255, 255, 255, 0.6);
        border-radius: 50%;
        animation: particleFloat 5s ease-in-out infinite;

        &:nth-child(2n) {
          width: 3px;
          height: 3px;
          opacity: 0.5;
        }

        &:nth-child(3n) {
          width: 5px;
          height: 5px;
          opacity: 0.7;
        }
      }
    }

    .container {
      position: relative;
      z-index: 1;
    }

    .banner-content {
      position: relative;
      z-index: 1;
      color: #fff;
      max-width: 700px;

      .badge-container {
        margin-bottom: 28px;
        animation: fadeIn 0.8s ease-out;
      }

      .badge {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        padding: 10px 24px;
        background: rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(20px);
        border-radius: 50px;
        font-size: 15px;
        font-weight: 600;
        border: 1px solid rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        transition: all 0.3s;

        .badge-icon {
          font-size: 18px;
          line-height: 1;
        }

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }
      }

      .banner-title {
        font-size: 64px;
        font-weight: 900;
        margin-bottom: 20px;
        line-height: 1.1;
        letter-spacing: -1px;
        text-shadow: 0 6px 25px rgba(0, 0, 0, 0.2);
        
        .title-main {
          display: block;
          font-weight: 700;
          opacity: 0.95;
        }

        .title-highlight {
          display: block;
          font-weight: 900;
          background: linear-gradient(to right, #fff, rgba(255, 255, 255, 0.85));
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          margin-top: 8px;
          position: relative;

          &::after {
            content: '';
            position: absolute;
            bottom: -8px;
            left: 0;
            width: 120px;
            height: 4px;
            background: linear-gradient(to right, #fff, transparent);
            border-radius: 2px;
          }
        }
      }

      .banner-description {
        font-size: 19px;
        margin-bottom: 32px;
        opacity: 0.92;
        line-height: 1.7;
        text-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
        max-width: 600px;
      }

      .banner-stats {
        display: flex;
        gap: 40px;
        margin-bottom: 40px;
        padding: 24px 0;

        .stat-item {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          gap: 4px;

          .stat-number {
            font-size: 32px;
            font-weight: 800;
            line-height: 1;
            text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
          }

          .stat-label {
            font-size: 13px;
            opacity: 0.85;
            font-weight: 500;
            letter-spacing: 0.5px;
          }
        }
      }

      .banner-actions {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;

      .banner-btn {
          padding: 15px 40px;
          font-size: 17px;
          font-weight: 600;
        border-radius: 50px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          letter-spacing: 0.5px;

          .btn-icon {
            margin-left: 8px;
            transition: transform 0.3s;
          }

          &.primary-btn {
            background: rgba(255, 255, 255, 1);
            color: #667eea;
            border: 2px solid rgba(255, 255, 255, 1);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);

            &:hover {
              transform: translateY(-3px);
              box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
              
              .btn-icon {
                transform: translateX(4px);
              }
            }
          }

          &.secondary-btn {
            background: transparent;
        color: #fff;
            border: 2px solid rgba(255, 255, 255, 0.6);
        backdrop-filter: blur(10px);

        &:hover {
              background: rgba(255, 255, 255, 0.15);
              border-color: rgba(255, 255, 255, 1);
              transform: translateY(-3px);
              box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            }
          }
        }
      }
    }
  }
}

.course-section,
.category-section {
  padding: 80px 0;
  background: transparent;
}

// 快速入口
.quick-access-section {
  padding: 60px 0;
  background: linear-gradient(to bottom, #f8f9ff 0%, #fff 100%);
}

.quick-access-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.access-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.6s;
  }

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);

    &::before {
      left: 100%;
    }

    .access-icon {
      transform: scale(1.1) rotate(5deg);
    }
  }

  .access-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    transition: all 0.3s;
  }

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 12px;
  }

  p {
    font-size: 14px;
    color: #999;
    line-height: 1.6;
  }
}

// 精选课程
.featured-section {
  padding: 80px 0;
  background: #fff;
}

// 平台数据统计
.stats-section {
  padding: 60px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;

  @media (max-width: 968px) {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }
}

.stat-item {
  text-align: center;
  padding: 20px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 1px;
    height: 60%;
    background: rgba(255, 255, 255, 0.2);
  }

  &:last-child::after {
    display: none;
  }

  .stat-icon {
    margin-bottom: 16px;
    color: rgba(255, 255, 255, 0.9);
  }

  .stat-number {
    font-size: 36px;
    font-weight: 700;
    margin-bottom: 8px;
    background: linear-gradient(to right, #fff, rgba(255, 255, 255, 0.8));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-label {
    font-size: 15px;
    color: rgba(255, 255, 255, 0.85);
  }
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;

  .header-left {
    h2 {
      font-size: 32px;
      font-weight: 700;
      color: #2c3e50;
      position: relative;
      padding-left: 20px;
      margin-bottom: 8px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 5px;
        height: 30px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 3px;
      }
    }

    .subtitle {
      font-size: 14px;
      color: #999;
      padding-left: 20px;
    }
  }

  h2 {
    font-size: 32px;
    font-weight: 700;
    color: #2c3e50;
    position: relative;
    padding-left: 20px;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 5px;
      height: 30px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 3px;
    }
  }

  .more-link {
    display: flex;
    align-items: center;
    gap: 5px;
    color: #667eea;
    font-size: 16px;
    font-weight: 500;
    padding: 10px 20px;
    border-radius: 25px;
    transition: all 0.3s;
    border: 2px solid transparent;

    &:hover {
      color: #fff;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      transform: translateX(3px);
    }
  }
}

// 精选课程网格（2x2）
.course-grid-small {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
  }
}

// 左右布局
.category-layout {
  display: flex;
  gap: 30px;
  min-height: 600px;

  @media (max-width: 968px) {
    flex-direction: column;
    gap: 20px;
  }
}

// 左侧分类栏
.category-sidebar {
  flex: 0 0 280px;
  
  @media (max-width: 968px) {
    flex: none;
  }
}

.category-list {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;

  /* 美化滚动条 */
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f5f5f5;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 3px;
    
    &:hover {
      background: #bbb;
    }
  }
}

.category-item {
  display: flex;
  align-items: center;
  padding: 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: #fff;
  border: 2px solid transparent;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 0 2px 2px 0;
    transition: height 0.3s;
  }

  &:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    transform: translateX(4px);
    border-color: rgba(102, 126, 234, 0.2);

    &::before {
      height: 60%;
    }

    .arrow-icon {
      opacity: 1;
      transform: translateX(0);
    }
  }

  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);

    &::before {
      height: 100%;
      background: #fff;
    }

    .category-icon {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
    }

    .category-count {
      color: rgba(255, 255, 255, 0.9);
    }

    .arrow-icon {
      opacity: 1;
      color: #fff;
    }
  }

  .category-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #667eea;
    transition: all 0.3s;
    flex-shrink: 0;
  }

  .category-info {
    flex: 1;
    margin-left: 12px;
    min-width: 0;
  }

  .category-name {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 4px;
    color: inherit;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .category-count {
    font-size: 12px;
    color: #999;
    transition: color 0.3s;
  }

  .arrow-icon {
    opacity: 0;
    transform: translateX(-5px);
    transition: all 0.3s;
    color: #667eea;
    flex-shrink: 0;
  }
}

// 右侧内容区
.category-content {
  flex: 1;
  min-width: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 500px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .empty-text {
    margin-top: 24px;
    font-size: 18px;
    font-weight: 600;
    color: #666;
  }

  .empty-desc {
    margin-top: 8px;
    font-size: 14px;
    color: #999;
  }
}

.category-courses {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  min-height: 500px;

  .category-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;
  }

  .category-title {
    display: flex;
    align-items: baseline;
    gap: 12px;

    h3 {
      font-size: 24px;
      font-weight: 700;
      color: #2c3e50;
      position: relative;
      padding-left: 16px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
      }
    }

    .course-total {
      font-size: 14px;
      color: #999;
      font-weight: 400;
    }
  }

  .more-link {
    display: flex;
    align-items: center;
    gap: 5px;
    color: #667eea;
    font-size: 15px;
    font-weight: 500;
    padding: 8px 16px;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      color: #fff;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      transform: translateX(3px);
    }
  }

  .course-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    @media (max-width: 1400px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }
}

// 动画效果
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes morph {
  0%, 100% {
    border-radius: 40% 60% 70% 30% / 40% 50% 60% 50%;
  }
  25% {
    border-radius: 60% 40% 30% 70% / 50% 60% 40% 50%;
  }
  50% {
    border-radius: 30% 70% 50% 50% / 60% 40% 60% 40%;
  }
  75% {
    border-radius: 70% 30% 60% 40% / 50% 40% 50% 60%;
  }
}

@keyframes slideCode {
  0% {
    transform: translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateX(-100%);
    opacity: 0;
  }
}

@keyframes particleFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  50% {
    transform: translateY(-20px) scale(1.2);
    opacity: 0.8;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-40px) scale(0.8);
    opacity: 0;
  }
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

.animate-fade-in-delay {
  animation: fadeIn 0.8s ease-out 0.2s both;
}

.animate-fade-in-delay-2 {
  animation: fadeIn 0.8s ease-out 0.4s both;
}
</style>