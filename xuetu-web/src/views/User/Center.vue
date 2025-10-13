<template>
  <div class="user-center-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="user-center">
          <!-- 用户信息卡片 -->
          <div class="user-info-card" v-loading="loading">
            <div class="user-avatar">
              <el-avatar :size="100" :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
            </div>
            <div class="user-details">
              <h2>{{ userStore.userInfo?.username || '游客' }}</h2>
              <p class="user-email">{{ userStore.userInfo?.email || '未设置邮箱' }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <div class="stat-value">{{ stats.totalCourses }}</div>
                  <div class="stat-label">已购课程</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ stats.completedCourses }}</div>
                  <div class="stat-label">已完成</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ formatDuration(stats.totalDuration) }}</div>
                  <div class="stat-label">学习时长</div>
                </div>
              </div>
            </div>
            <div class="user-actions">
              <el-button type="primary" @click="router.push('/user/settings')">
                编辑资料
              </el-button>
            </div>
          </div>

          <!-- 快捷入口 -->
          <div class="quick-links">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="link-card" @click="router.push('/user/courses')">
                  <el-icon :size="32" color="#409eff"><Reading /></el-icon>
                  <h3>我的课程</h3>
                  <p>{{ stats.totalCourses }} 门课程</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="link-card" @click="router.push('/order/list')">
                  <el-icon :size="32" color="#67c23a"><ShoppingBag /></el-icon>
                  <h3>我的订单</h3>
                  <p>查看订单记录</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="link-card" @click="router.push('/user/notes')">
                  <el-icon :size="32" color="#e6a23c"><Edit /></el-icon>
                  <h3>我的笔记</h3>
                  <p>学习笔记管理</p>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="link-card" @click="router.push('/cart')">
                  <el-icon :size="32" color="#f56c6c"><ShoppingCart /></el-icon>
                  <h3>购物车</h3>
                  <p>{{ cartStore.cartCount }} 件商品</p>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 学习统计 -->
          <div class="learning-stats">
            <h3 class="section-title">学习统计</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="stats-card">
                  <h4>学习进度</h4>
                  <div class="progress-info">
                    <div class="progress-item">
                      <span>总课程</span>
                      <el-progress :percentage="stats.totalCourses > 0 ? 100 : 0" :show-text="false" />
                      <span>{{ stats.totalCourses }}</span>
                    </div>
                    <div class="progress-item">
                      <span>已完成</span>
                      <el-progress 
                        :percentage="stats.totalCourses > 0 ? Math.round((stats.completedCourses / stats.totalCourses) * 100) : 0" 
                        :show-text="false" 
                        color="#67c23a"
                      />
                      <span>{{ stats.completedCourses }}</span>
                    </div>
                    <div class="progress-item">
                      <span>学习中</span>
                      <el-progress 
                        :percentage="stats.totalCourses > 0 ? Math.round(((stats.totalCourses - stats.completedCourses) / stats.totalCourses) * 100) : 0" 
                        :show-text="false"
                        color="#e6a23c"
                      />
                      <span>{{ stats.totalCourses - stats.completedCourses }}</span>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stats-card">
                  <h4>本周学习时长</h4>
                  <div class="weekly-duration">
                    <div class="duration-chart">
                      <div v-for="(duration, index) in stats.weeklyDuration" :key="index" class="duration-bar">
                        <div class="bar" :style="{ height: (duration / Math.max(...stats.weeklyDuration, 1)) * 100 + '%' }"></div>
                        <span class="day">{{ ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][index] }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 最近学习 -->
          <div class="recent-learning">
            <h3 class="section-title">
              <span>最近学习</span>
              <el-button type="primary" text @click="router.push('/user/courses')">
                查看全部 →
              </el-button>
            </h3>
            <div v-loading="loadingCourses">
              <el-row :gutter="20">
                <el-col :span="6" v-for="course in recentCourses" :key="course.id">
                  <div class="course-card" @click="router.push(`/course/${course.courseId}`)">
                    <img :src="course.coverImage || 'https://via.placeholder.com/300x200'" :alt="course.courseName" />
                    <div class="course-info">
                      <h4>{{ course.courseName }}</h4>
                      <el-progress :percentage="course.progress" />
                      <p class="last-study">上次学习: {{ formatDate(course.lastStudyTime) }}</p>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <el-empty v-if="recentCourses.length === 0" description="暂无学习记录" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { Reading, ShoppingBag, Edit, ShoppingCart } from '@element-plus/icons-vue'
import { getMyCourses, getLearningStats } from '@/api/learning'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const loadingCourses = ref(false)

// 统计数据
const stats = ref({
  totalCourses: 0,
  completedCourses: 0,
  totalDuration: 0,
  totalLessons: 0,
  completedLessons: 0,
  weeklyDuration: [0, 0, 0, 0, 0, 0, 0]
})

// 最近学习的课程
const recentCourses = ref<any[]>([])

// 获取学习统计
const fetchStats = async () => {
  loading.value = true
  try {
    const data = await getLearningStats()
    stats.value = {
      totalCourses: data.totalCourses || 0,
      completedCourses: data.completedCourses || 0,
      totalDuration: data.totalDuration || 0,
      totalLessons: data.totalLessons || 0,
      completedLessons: data.completedLessons || 0,
      weeklyDuration: data.weeklyDuration || [0, 0, 0, 0, 0, 0, 0]
    }
  } catch (error) {
    console.error('获取学习统计失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取最近学习的课程
const fetchRecentCourses = async () => {
  loadingCourses.value = true
  try {
    const res = await getMyCourses({ current: 1, size: 4 })
    recentCourses.value = res.records || []
  } catch (error) {
    console.error('获取最近学习课程失败:', error)
  } finally {
    loadingCourses.value = false
  }
}

// 格式化时长（秒转小时）
const formatDuration = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  return hours > 0 ? `${hours}小时` : `${Math.floor(seconds / 60)}分钟`
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}

onMounted(() => {
  if (userStore.isLogin) {
    fetchStats()
    fetchRecentCourses()
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})
</script>

<style scoped lang="scss">
.user-center-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.user-center {
  padding: 20px 0;
}

.user-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 40px;
  display: flex;
  align-items: center;
  margin-bottom: 30px;

  .user-avatar {
    margin-right: 30px;
  }

  .user-details {
    flex: 1;

    h2 {
      font-size: 28px;
      margin-bottom: 10px;
    }

    .user-email {
      margin-bottom: 20px;
      opacity: 0.9;
    }

    .user-stats {
      display: flex;
      gap: 40px;

      .stat-item {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          margin-bottom: 5px;
        }

        .stat-label {
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }
  }
}

.quick-links {
  margin-bottom: 30px;

  .link-card {
    background: white;
    border-radius: 8px;
    padding: 30px 20px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
      border-color: #409eff;
    }

    h3 {
      margin: 15px 0 10px;
      font-size: 18px;
    }

    p {
      color: #909399;
      font-size: 14px;
    }
  }
}

.learning-stats {
  margin-bottom: 30px;

  .section-title {
    font-size: 20px;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f0f0f0;
  }

  .stats-card {
    background: white;
    border-radius: 8px;
    padding: 20px;

    h4 {
      font-size: 16px;
      margin-bottom: 20px;
    }

    .progress-info {
      .progress-item {
        display: flex;
        align-items: center;
        margin-bottom: 15px;

        span:first-child {
          width: 70px;
          font-size: 14px;
        }

        .el-progress {
          flex: 1;
          margin: 0 15px;
        }

        span:last-child {
          width: 40px;
          text-align: right;
          font-weight: bold;
        }
      }
    }

    .weekly-duration {
      .duration-chart {
        display: flex;
        align-items: flex-end;
        justify-content: space-around;
        height: 150px;
        padding: 10px 0;

        .duration-bar {
          flex: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: flex-end;

          .bar {
            width: 100%;
            max-width: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 4px 4px 0 0;
            min-height: 5px;
            transition: all 0.3s;

            &:hover {
              opacity: 0.8;
            }
          }

          .day {
            font-size: 12px;
            color: #909399;
            margin-top: 8px;
          }
        }
      }
    }
  }
}

.recent-learning {
  .section-title {
    font-size: 20px;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .course-card {
    background: white;
  border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }

    img {
      width: 100%;
      height: 150px;
      object-fit: cover;
    }

    .course-info {
      padding: 15px;

      h4 {
        font-size: 16px;
        margin-bottom: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .el-progress {
        margin-bottom: 10px;
      }

      .last-study {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>