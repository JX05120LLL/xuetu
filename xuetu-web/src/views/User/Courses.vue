<template>
  <div class="user-courses-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="page-header">
          <h2 class="page-title">我的课程</h2>
          <div class="header-right">
            <div class="filter-tabs">
              <el-radio-group v-model="filterStatus" @change="handleFilterChange">
                <el-radio-button label="all">全部课程</el-radio-button>
                <el-radio-button label="not-started">未学习</el-radio-button>
                <el-radio-button label="learning">学习中</el-radio-button>
                <el-radio-button label="completed">已完成</el-radio-button>
              </el-radio-group>
            </div>
            <el-button 
              type="primary" 
              :icon="Refresh" 
              @click="handleRefresh"
              :loading="loading"
              circle
              title="刷新课程数据"
            />
          </div>
        </div>

        <div class="user-courses" v-loading="loading">
          <div v-if="courses.length > 0">
            <el-row :gutter="20">
              <el-col :span="8" v-for="course in courses" :key="course.id">
                <div class="course-card">
                  <div class="course-cover" @click="goToPlay(course)">
                    <img 
                      :src="course.coverImage || 'https://via.placeholder.com/400x250'" 
                      :alt="course.courseName"
                      @error="handleImageError"
                    />
                    <div class="play-overlay">
                      <el-icon :size="50" color="#fff"><VideoPlay /></el-icon>
                    </div>
                    <!-- 未开始学习 -->
                    <div class="not-started-badge" v-if="course.progress === 0">
                      未开始学习
                    </div>
                    <!-- 学习中 -->
                    <div class="progress-badge" v-else-if="course.progress > 0 && course.progress < 100">
                      学习中 {{ course.progress }}%
                    </div>
                    <!-- 已完成 -->
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
import { ref, onMounted, onActivated, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { VideoPlay, Clock, Calendar, Check, Refresh } from '@element-plus/icons-vue'
import { getMyCourses, getCourseRecords } from '@/api/learning'
import { getCourseDetail, getChaptersByCourseId } from '@/api/course'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(false)
const filterStatus = ref('all')
const courses = ref<any[]>([])
const allCourses = ref<any[]>([]) // 存储所有课程数据用于筛选
const currentPage = ref(1)
const pageSize = ref(6)
const total = ref(0)

// 计算课程进度
const calculateCourseProgress = async (courseId: number, chapters: any[]): Promise<number> => {
  try {
    console.log(`🔍 开始计算课程${courseId}的进度...`)
    
    // 获取该课程的学习记录
    const records = await getCourseRecords(courseId)
    console.log(`📚 课程${courseId}的学习记录:`, records)
    
    if (!records || records.length === 0) {
      console.log(`📊 课程${courseId}暂无学习记录 - 进度0%`)
      return 0
    }
    
    console.log(`📖 课程${courseId}的章节数据:`, chapters)
    
    // 统计总课时数和已学课时数
    let totalLessons = 0
    let studiedLessons = 0 // 只要有学习记录就算已学（包括快进、跳看等场景）
    const studiedLessonIds: number[] = [] // 记录已学课时ID
    
    // 遍历所有章节和课时
    if (chapters && chapters.length > 0) {
      chapters.forEach(chapter => {
        console.log(`  📑 章节: ${chapter.title}, 课时数: ${chapter.lessons?.length || 0}`)
        if (chapter.lessons && chapter.lessons.length > 0) {
          chapter.lessons.forEach((lesson: any) => {
            totalLessons++
            // 检查该课时是否有学习记录，只要有记录就算已学（支持快进、跳看等）
            const record = records.find((r: any) => r.lessonId === lesson.id)
            if (record) {
              studiedLessons++
              studiedLessonIds.push(lesson.id)
              console.log(`    ✅ 课时${lesson.id}(${lesson.title}): 已学 - 进度${record.progress}%`)
            } else {
              console.log(`    ⭕ 课时${lesson.id}(${lesson.title}): 未学`)
            }
          })
        }
      })
    } else {
      console.warn(`⚠️ 课程${courseId}没有章节数据！`)
    }
    
    // 如果没有课时，返回0
    if (totalLessons === 0) {
      console.log(`📊 课程${courseId}暂无课时`)
      return 0
    }
    
    // 计算进度百分比：只要有学习记录就算已学
    const progress = Math.floor((studiedLessons / totalLessons) * 100)
    console.log(`📊 课程${courseId}进度总结:`)
    console.log(`   - 已学课时: ${studiedLessons}节 [${studiedLessonIds.join(', ')}]`)
    console.log(`   - 总课时数: ${totalLessons}节`)
    console.log(`   - 学习进度: ${progress}%`)
    return progress
  } catch (error) {
    console.error(`❌ 计算课程${courseId}进度失败:`, error)
    // 如果接口调用失败，返回0而不是让整个页面失败
    return 0
  }
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    // 1. 获取用户课程列表（包含courseId和progress）
    const res = await getMyCourses({
      current: currentPage.value,
      size: pageSize.value
    })
    
    const userCourses = res.records || []
    
    // 2. 获取每个课程的详细信息
    if (userCourses.length > 0) {
      const courseDetailsPromises = userCourses.map(async (uc: any) => {
        try {
          // 获取课程基本信息
          const courseDetail = await getCourseDetail(uc.courseId)
          
          // 获取课程章节和课时信息（用于计算进度）
          const chapters = await getChaptersByCourseId(uc.courseId)
          console.log(`📚 课程${uc.courseId}(${courseDetail.title})的章节数据:`, chapters)
          
          // 实时计算课程进度（根据学习记录和章节数据）
          const actualProgress = await calculateCourseProgress(uc.courseId, chapters || [])
          
          // 注意：不在这里同步进度，避免页面加载时发送大量请求
          // 进度同步会在视频播放时自动完成
          
          // 合并用户课程信息和课程详情
          return {
            ...uc,
            id: uc.id, // 用户课程记录ID
            courseId: uc.courseId, // 课程ID（重要！）
            courseName: courseDetail.title,
            coverImage: courseDetail.coverImage,
            courseDescription: courseDetail.description,
            teacherId: courseDetail.teacherId,
            teacherName: courseDetail.teacherName,
            totalDuration: courseDetail.duration || 0,
            progress: actualProgress, // 使用实时计算的进度
            studyDuration: 0, // 后续可以从学习记录获取
            lastStudyTime: uc.lastLearnTime || uc.purchaseTime,
            purchaseTime: uc.purchaseTime,
            status: uc.status
          }
        } catch (error) {
          console.error(`获取课程${uc.courseId}详情失败:`, error)
          // 如果获取失败，返回基础信息
          return {
            ...uc,
            id: uc.id,
            courseId: uc.courseId, // 确保courseId存在
            courseName: '课程加载失败',
            coverImage: '',
            progress: 0
          }
        }
      })
      
      const enrichedCourses = await Promise.all(courseDetailsPromises)
      allCourses.value = enrichedCourses // 保存所有课程
      
      // 应用筛选
      applyFilter()
    } else {
      courses.value = []
      allCourses.value = []
    }
    
    total.value = res.total || 0
    
    console.log('📚 课程列表加载完成，共', allCourses.value.length, '门课程')
    if (allCourses.value.length > 0) {
      console.log('📋 第一门课程数据示例:', {
        id: allCourses.value[0].id,
        courseId: allCourses.value[0].courseId,
        courseName: allCourses.value[0].courseName
      })
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    courses.value = []
    allCourses.value = []
  } finally {
    loading.value = false
  }
}

// 应用筛选
const applyFilter = () => {
  if (filterStatus.value === 'not-started') {
    // 未开始学习：进度 = 0
    courses.value = allCourses.value.filter(course => 
      course.progress === 0 || !course.progress
    )
  } else if (filterStatus.value === 'learning') {
    // 学习中：进度 > 0 且 < 100
    courses.value = allCourses.value.filter(course => 
      course.progress > 0 && course.progress < 100
    )
  } else if (filterStatus.value === 'completed') {
    // 已完成：进度 >= 100
    courses.value = allCourses.value.filter(course => 
      course.progress >= 100
    )
  } else {
    // 全部课程
    courses.value = [...allCourses.value]
  }
  
  console.log(`筛选结果 [${filterStatus.value}]:`, courses.value.length, '门课程')
}

// 手动刷新课程数据
const handleRefresh = () => {
  console.log('🔄 手动刷新课程数据...')
  ElMessage.info('正在刷新课程数据...')
  fetchCourses()
}

// 筛选切换
const handleFilterChange = () => {
  currentPage.value = 1 // 重置页码
  applyFilter()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  fetchCourses()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchCourses()
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/400x250?text=课程封面'
}

// 跳转到播放页
const goToPlay = (course: any) => {
  router.push(`/course/${course.courseId}/play`)
}

// 跳转到课程详情
const goToCourseDetail = (courseId: number) => {
  console.log('🔍 点击课程详情，courseId:', courseId)
  console.log('🔍 courseId 类型:', typeof courseId)
  
  if (!courseId) {
    console.error('❌ courseId 为空或未定义')
    ElMessage.error('课程信息有误，无法跳转')
    return
  }
  
  const targetPath = `/course/${courseId}`
  console.log('➡️ 正在跳转到课程详情页:', targetPath)
  console.log('📍 当前路由:', router.currentRoute.value.fullPath)
  
  router.push(targetPath)
    .then(() => {
      console.log('✅ 路由跳转成功！')
      console.log('📍 跳转后路由:', router.currentRoute.value.fullPath)
    })
    .catch((error) => {
      console.error('❌ 路由跳转失败:', error)
      ElMessage.error('页面跳转失败，请重试')
    })
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

// 页面首次挂载时加载
onMounted(() => {
  fetchCourses()
  
  // 监听页面可见性变化，当用户从其他标签页返回时刷新数据
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 使用keep-alive时，页面激活时重新加载（从播放页返回时）
onActivated(() => {
  console.log('🔄 页面激活，刷新课程数据...')
  fetchCourses()
})

// 页面卸载时移除监听器
onBeforeUnmount(() => {
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

// 页面可见性变化处理
const handleVisibilityChange = () => {
  if (!document.hidden) {
    console.log('📱 页面可见，刷新课程数据...')
    fetchCourses()
  }
}
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

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
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

    .not-started-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(144, 147, 153, 0.9);
      color: white;
      padding: 5px 12px;
      border-radius: 20px;
      font-size: 12px;
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