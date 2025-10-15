<template>
  <div class="user-courses-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">我的课程</h2>
          <div class="filter-tabs">
            <el-radio-group v-model="filterStatus" @change="handleFilterChange">
              <el-radio-button label="all">全部课程</el-radio-button>
              <el-radio-button label="learning">学习中</el-radio-button>
              <el-radio-button label="completed">已完成</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <div class="user-courses" v-loading="loading">
          <div v-if="courses.length > 0">
            <el-row :gutter="20">
              <el-col :span="8" v-for="course in courses" :key="course.id">
                <div class="course-card">
                  <div class="course-cover" @click="goToPlay(course)">
                    <img :src="course.coverImage || 'https://via.placeholder.com/400x250'" :alt="course.courseName" />
                    <div class="play-overlay">
                      <el-icon :size="50" color="#fff"><VideoPlay /></el-icon>
                    </div>
                    <div class="progress-badge" v-if="course.progress < 100">
                      学习中 {{ course.progress }}%
                    </div>
                    <div class="completed-badge" v-else>
                      <el-icon><Check /></el-icon> 已完成
                    </div>
                  </div>
                  
                  <div class="course-info">
                    <h3 @click="goToCourseDetail(course.courseId)">{{ course.courseName }}</h3>
                    
                    <div class="progress-section">
                      <el-progress :percentage="course.progress" :color="progressColor(course.progress)" />
                    </div>
                    
                    <div class="course-meta">
                      <div class="meta-item">
                        <el-icon><Clock /></el-icon>
                        <span>学习时长: {{ formatDuration(course.studyDuration) }}</span>
                      </div>
                      <div class="meta-item">
                        <el-icon><Calendar /></el-icon>
                        <span>{{ formatDate(course.lastStudyTime) }}</span>
                      </div>
                    </div>
                    
                    <div class="course-actions">
                      <el-button type="primary" @click="goToPlay(course)">
                        {{ course.progress < 100 ? '继续学习' : '复习课程' }}
                      </el-button>
                      <el-button @click="goToCourseDetail(course.courseId)">
                        课程详情
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
            
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[6, 12, 24]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
          
          <el-empty v-else description="暂无课程">
            <el-button type="primary" @click="router.push('/course/list')">
              去选课
            </el-button>
          </el-empty>
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
import { VideoPlay, Clock, Calendar, Check } from '@element-plus/icons-vue'
import { getMyCourses, checkUserHasCourse } from '@/api/learning'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const filterStatus = ref('all')
const courses = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await getMyCourses({
      current: currentPage.value,
      size: pageSize.value
    })
    courses.value = res.records || []
    total.value = res.total || 0
    
    // 调试日志：检查返回的数据
    console.log('📚 我的课程数据:', courses.value)
    if (courses.value.length > 0) {
      console.log('第一个课程的封面图:', courses.value[0].coverImage)
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选切换
const handleFilterChange = (value: string) => {
  if (value === 'learning') {
    courses.value = courses.value.filter(course => course.progress < 100 && course.progress > 0)
  } else if (value === 'completed') {
    courses.value = courses.value.filter(course => course.progress === 100)
  } else {
    fetchCourses()
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchCourses()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchCourses()
}

// 跳转到播放页
const goToPlay = (course: any) => {
  // 检查是否有课程章节信息
  router.push(`/course/${course.courseId}/play`)
}

// 跳转到课程详情
const goToCourseDetail = (courseId: number) => {
  // 课程详情页是公开的，直接跳转即可
  router.push(`/course/${courseId}`)
}

// 进度条颜色
const progressColor = (percentage: number) => {
  if (percentage < 30) return '#f56c6c'
  if (percentage < 70) return '#e6a23c'
  return '#67c23a'
}

// 格式化时长
const formatDuration = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return '未开始学习'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天学习'
  if (days === 1) return '昨天学习'
  if (days < 7) return `${days}天前学习`
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.user-courses-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.filter-tabs {
  .el-radio-group {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 4px;
  }
}

.user-courses {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  min-height: 400px;
}

.course-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  margin-bottom: 20px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }

  .course-cover {
    position: relative;
    height: 200px;
    overflow: hidden;
    cursor: pointer;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }

    &:hover img {
      transform: scale(1.1);
    }

    .play-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;
    }

    &:hover .play-overlay {
      opacity: 1;
    }

    .progress-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(64, 158, 255, 0.9);
      color: white;
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 12px;
    }

    .completed-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(103, 194, 58, 0.9);
      color: white;
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .course-info {
    padding: 20px;

    h3 {
      font-size: 18px;
      margin-bottom: 15px;
      cursor: pointer;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      &:hover {
        color: #409eff;
      }
    }

    .progress-section {
      margin-bottom: 15px;
    }

    .course-meta {
      margin-bottom: 15px;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;

        .el-icon {
          font-size: 16px;
        }
      }
    }

    .course-actions {
      display: flex;
      gap: 10px;

      .el-button {
        flex: 1;
      }
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>