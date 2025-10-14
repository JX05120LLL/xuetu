<template>
  <div class="home-page">
    <Header />
    
    <main class="main-content">
      <!-- Banner轮播图 -->
      <section class="banner-section">
        <el-carousel height="500px" indicator-position="outside">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item" :style="{ background: item.gradient }">
              <div class="container">
                <div class="banner-content">
                  <h2 class="animate-fade-in">{{ item.title }}</h2>
                  <p class="animate-fade-in-delay">{{ item.description }}</p>
                  <el-button 
                    type="primary" 
                    size="large" 
                    class="banner-btn animate-fade-in-delay-2"
                    @click="router.push('/course/list')"
                  >
                    立即学习
                  </el-button>
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

      <!-- 分类课程 -->
      <section class="category-section">
        <div class="container">
          <div class="section-header">
            <h2>课程分类</h2>
          </div>
          
          <!-- 左右布局 -->
          <div class="category-layout">
            <!-- 左侧分类列表 -->
            <div class="category-sidebar">
              <div class="category-list">
                <div
                  v-for="category in categories"
                  :key="category.id"
                  class="category-item"
                  :class="{ active: activeCategory === String(category.id) }"
                  @click="handleCategoryChange(String(category.id))"
                >
                  <div class="category-icon">
                    <el-icon :size="24">
                      <Reading v-if="category.name.includes('编程')" />
                      <Monitor v-else-if="category.name.includes('前端')" />
                      <Box v-else-if="category.name.includes('后端')" />
                      <DataLine v-else-if="category.name.includes('数据')" />
                      <Notebook v-else-if="category.name.includes('设计')" />
                      <TrendCharts v-else-if="category.name.includes('算法')" />
                      <Platform v-else />
                    </el-icon>
                  </div>
                  <div class="category-info">
                    <div class="category-name">{{ category.name }}</div>
                    <div class="category-count">{{ category.courseCount || 0 }} 门</div>
                  </div>
                  <el-icon class="arrow-icon">
                    <ArrowRight />
                  </el-icon>
                </div>
              </div>
            </div>

            <!-- 右侧课程内容 -->
            <div class="category-content">
              <!-- 未选择分类时的提示 -->
              <div v-if="!activeCategory" class="empty-state">
                <el-icon :size="80" color="#ddd">
                  <Reading />
                </el-icon>
                <p class="empty-text">请从左侧选择课程分类</p>
                <p class="empty-desc">探索海量优质课程，开启学习之旅</p>
              </div>

              <!-- 选中分类的课程 -->
              <div v-else class="category-courses">
                <div class="category-header">
                  <div class="category-title">
                    <h3>{{ getCurrentCategoryName() }}</h3>
                    <span class="course-total">共 {{ categoryCourses.length }} 门课程</span>
                  </div>
                  <router-link 
                    :to="{ path: '/course/list', query: { categoryId: activeCategory } }"
                    class="more-link"
                  >
                    查看全部 <el-icon><ArrowRight /></el-icon>
                  </router-link>
                </div>
                
                <div class="course-grid" v-loading="loadingCategoryCourses">
                  <CourseCard
                    v-for="course in categoryCourses"
                    :key="course.id"
                    :course="course"
                  />
                </div>

                <el-empty 
                  v-if="!loadingCategoryCourses && categoryCourses.length === 0"
                  description="该分类暂无课程"
                  :image-size="120"
                />
              </div>
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
  Reading, 
  Monitor, 
  Box, 
  DataLine, 
  Notebook, 
  TrendCharts, 
  Platform,
  Compass,
  Document,
  ChatDotRound,
  UserFilled,
  User,
  Timer,
  Trophy,
  Star
} from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import CourseCard from '@/components/CourseCard.vue'
import { getCourseList } from '@/api/course'
import { useCourseStore } from '@/stores/course'
import type { Course } from '@/types/course'

const router = useRouter()

const courseStore = useCourseStore()
const loading = ref(false)
const loadingCategoryCourses = ref(false)
const hotCourses = ref<Course[]>([])
const categoryCourses = ref<Course[]>([])
const activeCategory = ref('')

// Banner数据
const banners = ref([
  {
    id: 1,
    title: '欢迎来到学途在线教育平台',
    description: '海量优质课程，助你学习成长',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    id: 2,
    title: '专业讲师团队',
    description: '行业大咖亲自授课，传授实战经验',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    id: 3,
    title: '随时随地学习',
    description: '支持多平台学习，想学就学',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
])

const categories = ref<any[]>([])

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
    path: '/my/courses'
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

// 获取分类
const fetchCategories = async () => {
  const result = await courseStore.fetchCategories()
  categories.value = result
  // 默认不选中任何分类，让用户主动选择
}

// 分类切换
const handleCategoryChange = async (categoryId: string) => {
  activeCategory.value = categoryId
  loadingCategoryCourses.value = true
  
  try {
    const res = await getCourseList({
      current: 1,
      size: 8,
      categoryId: Number(categoryId),
      status: 1  // 只获取已发布的课程
    })
    categoryCourses.value = res.records
  } catch (error) {
    console.error('获取分类课程失败:', error)
    categoryCourses.value = []
  } finally {
    loadingCategoryCourses.value = false
  }
}

// 获取当前分类名称
const getCurrentCategoryName = () => {
  const category = categories.value.find(c => String(c.id) === activeCategory.value)
  return category?.name || '全部课程'
}

onMounted(() => {
  fetchHotCourses()
  fetchCategories()
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
    height: 500px;
    display: flex;
    align-items: center;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: radial-gradient(circle at 30% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
      animation: pulse 4s ease-in-out infinite;
    }

    .banner-content {
      position: relative;
      z-index: 1;
      color: #fff;
      max-width: 600px;

      h2 {
        font-size: 52px;
        font-weight: 700;
        margin-bottom: 20px;
        line-height: 1.2;
        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      }

      p {
        font-size: 22px;
        margin-bottom: 40px;
        opacity: 0.95;
        line-height: 1.6;
      }

      .banner-btn {
        padding: 16px 48px;
        font-size: 18px;
        border-radius: 50px;
        background: rgba(255, 255, 255, 0.25);
        border: 2px solid rgba(255, 255, 255, 0.8);
        color: #fff;
        font-weight: 600;
        transition: all 0.3s;
        backdrop-filter: blur(10px);

        &:hover {
          background: rgba(255, 255, 255, 0.35);
          border-color: #fff;
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
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