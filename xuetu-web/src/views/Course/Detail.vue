<template>
  <div class="course-detail-page">
    <Header />
    
    <div class="page-content" v-loading="loading">
      <div class="container" v-if="course">
        <!-- 课程基本信息 -->
        <div class="course-header">
          <div class="course-info-left">
            <h1>{{ course.title }}</h1>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span>讲师: {{ course.teacherName }}</span>
              <span>学员: {{ formatNumber(course.studentCount) }}人</span>
              <span>难度: {{ course.levelName }}</span>
            </div>
          </div>
          
          <div class="course-info-right">
            <img :src="course.coverImage" :alt="course.title" />
            <div class="price-section">
              <div class="price">{{ formatPrice(course.price) }}</div>
              <el-button v-if="!hasAccess" type="primary" size="large" block @click="handleBuyNow">
                立即购买
              </el-button>
              <el-button v-else type="success" size="large" block @click="handleContinueLearning">
                继续学习
              </el-button>
              <el-button v-if="!hasAccess" size="large" block @click="handleAddToCart">
                加入购物车
              </el-button>
            </div>
          </div>
        </div>

        <!-- 课程详情Tab -->
        <el-tabs v-model="activeTab" class="course-tabs">
          <el-tab-pane label="课程目录" name="catalog">
            <div class="chapter-list">
              <div v-for="chapter in chapters" :key="chapter.id" class="chapter-item">
                <div class="chapter-header" @click="toggleChapter(chapter.id)">
                  <h3>{{ chapter.title }}</h3>
                  <el-icon>
                    <ArrowDown v-if="!openChapters[chapter.id]" />
                    <ArrowUp v-else />
                  </el-icon>
                </div>
                
                <div v-show="openChapters[chapter.id]" class="lesson-list">
                  <div v-if="loadingLessons[chapter.id]" class="loading-lessons">
                    <el-skeleton :rows="3" animated />
                  </div>
                  <template v-else>
                    <div v-for="lesson in chapterLessons[chapter.id]" :key="lesson.id" class="lesson-item">
                      <router-link 
                        v-if="hasAccess || lesson.isFree === 1"
                        :to="`/course/${course.id}/play?lessonId=${lesson.id}`"
                      >
                        <div class="lesson-info">
                          <span class="lesson-title">{{ lesson.title }}</span>
                          <div class="lesson-meta">
                            <span class="lesson-duration">{{ formatDuration(lesson.duration) }}</span>
                            <span class="lesson-tag" :class="{ 'free': lesson.isFree === 1 }">
                              {{ lesson.isFree === 1 ? '免费' : '付费' }}
                            </span>
                          </div>
                        </div>
                      </router-link>
                      <div 
                        v-else
                        class="lesson-info locked"
                        @click="handleBuyNowPrompt"
                      >
                        <span class="lesson-title">{{ lesson.title }} <el-icon><Lock /></el-icon></span>
                        <div class="lesson-meta">
                          <span class="lesson-duration">{{ formatDuration(lesson.duration) }}</span>
                          <span class="lesson-tag">付费</span>
                        </div>
                      </div>
                    </div>
                    <div v-if="chapterLessons[chapter.id]?.length === 0" class="no-lessons">
                      暂无课时
                    </div>
                  </template>
                </div>
              </div>
              
              <div v-if="chapters.length === 0" class="no-chapters">
                暂无章节
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="课程介绍" name="intro">
            <div class="course-intro">
              {{ course.description }}
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="学员评价" name="comments">
            <div class="comments-section">
              <div v-if="commentsLoading" class="loading-comments">
                <el-skeleton :rows="5" animated />
              </div>
              <template v-else>
                <div v-if="comments.length > 0" class="comment-list">
                  <div v-for="comment in comments" :key="comment.id" class="comment-item">
                    <div class="comment-header">
                      <div class="user-info">
                        <el-avatar :size="40" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"></el-avatar>
                        <span class="username">{{ comment.userId }}</span>
                      </div>
                      <div class="rating">
                        <el-rate v-model="comment.rating" disabled></el-rate>
                      </div>
                    </div>
                    <div class="comment-content">
                      {{ comment.content }}
                    </div>
                    <div class="comment-footer">
                      <span class="time">{{ formatDate(comment.createdTime) }}</span>
                      <div class="actions">
                        <el-button type="text" size="small">
                          <el-icon><ThumbUp /></el-icon> {{ comment.likeCount }}
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="no-comments">
                  <el-empty description="暂无评价" />
                </div>
                
                <div class="pagination" v-if="total > 0">
                  <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[5, 10, 20]"
                    layout="total, sizes, prev, pager, next"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                  />
                </div>
              </template>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useCourseStore } from '@/stores/course'
import { useCartStore } from '@/stores/cart'
import { formatPrice, formatNumber } from '@/utils/format'
import { ArrowDown, ArrowUp, ThumbUp, Lock } from '@element-plus/icons-vue'
import { getChapterLessons, getCourseComments } from '@/api/course'
import { checkUserHasCourse } from '@/api/learning'
import type { Course, Chapter, Lesson, Comment } from '@/types/course'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const courseStore = useCourseStore()
const cartStore = useCartStore()

const loading = ref(false)
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeTab = ref('catalog')
const hasAccess = ref(false)

// 章节和课时相关
const openChapters = reactive<Record<number, boolean>>({})
const loadingLessons = reactive<Record<number, boolean>>({})
const chapterLessons = reactive<Record<number, Lesson[]>>({})

// 评论相关
const comments = ref<Comment[]>([])
const commentsLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchCourseDetail = async () => {
  loading.value = true
  try {
    const courseId = Number(route.params.id)
    course.value = await courseStore.fetchCourseDetail(courseId)
    chapters.value = await courseStore.fetchChapters(courseId)
    
    // 检查用户是否购买了课程
    hasAccess.value = await checkUserHasCourse(courseId)
    
    // 默认打开第一个章节
    if (chapters.value.length > 0) {
      openChapters[chapters.value[0].id] = true
      if (hasAccess.value || chapters.value[0].isFree === 1) {
        fetchLessons(chapters.value[0].id)
      }
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
  } finally {
    loading.value = false
  }
}

// 获取章节课时
const fetchLessons = async (chapterId: number) => {
  loadingLessons[chapterId] = true
  try {
    const lessons = await getChapterLessons(chapterId)
    chapterLessons[chapterId] = lessons
  } catch (error) {
    console.error('获取课时列表失败:', error)
    ElMessage.error('获取课时列表失败')
  } finally {
    loadingLessons[chapterId] = false
  }
}

// 切换章节展开/折叠
const toggleChapter = (chapterId: number) => {
  openChapters[chapterId] = !openChapters[chapterId]
  if (openChapters[chapterId] && !chapterLessons[chapterId]) {
    fetchLessons(chapterId)
  }
}

// 格式化时长
const formatDuration = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const handleBuyNow = () => {
  if (course.value) {
    router.push({
      path: '/order/confirm',
      query: { courseIds: String(course.value.id) }
    })
  }
}

const handleAddToCart = () => {
  if (course.value) {
    cartStore.addToCart(course.value)
  }
}

const handleContinueLearning = () => {
  if (course.value) {
    router.push(`/course/${course.value.id}/play`)
  }
}

const handleBuyNowPrompt = () => {
  ElMessage({
    message: '请先购买课程再学习',
    type: 'warning',
    duration: 2000,
    showClose: true,
    onClose: () => {
      // 滚动到页面顶部购买区域
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  })
}

// 获取评论列表
const fetchComments = async () => {
  if (!course.value) return
  
  commentsLoading.value = true
  try {
    const res = await getCourseComments({
      courseId: course.value.id,
      current: currentPage.value,
      size: pageSize.value
    })
    comments.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败')
  } finally {
    commentsLoading.value = false
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchComments()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchComments()
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}

// 监听标签切换
watch(() => activeTab.value, (newVal) => {
  if (newVal === 'comments' && course.value && comments.value.length === 0) {
    fetchComments()
  }
})

onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped lang="scss">
.course-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.course-header {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;

  .course-info-left {
    flex: 1;

    h1 {
      font-size: 28px;
      margin-bottom: 15px;
    }

    .course-desc {
      font-size: 16px;
      color: #666;
      margin-bottom: 20px;
    }

    .course-meta {
      display: flex;
      gap: 20px;
      font-size: 14px;
      color: #999;
    }
  }

  .course-info-right {
    width: 350px;

    img {
      width: 100%;
      border-radius: 8px;
      margin-bottom: 20px;
    }

    .price-section {
      .price {
        font-size: 32px;
        color: #f56c6c;
        font-weight: bold;
        margin-bottom: 20px;
      }

      .el-button {
        margin-bottom: 10px;
      }
    }
  }
}

.course-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: 400px;
  
  .comments-section {
    .loading-comments {
      padding: 20px 0;
    }
    
    .comment-list {
      .comment-item {
        padding: 20px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .comment-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
          
          .user-info {
            display: flex;
            align-items: center;
            
            .username {
              margin-left: 10px;
              font-weight: 500;
            }
          }
        }
        
        .comment-content {
          margin-bottom: 15px;
          line-height: 1.6;
        }
        
        .comment-footer {
          display: flex;
          justify-content: space-between;
          color: #909399;
          font-size: 14px;
        }
      }
    }
    
    .no-comments {
      padding: 30px 0;
    }
    
    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

.chapter-list {
  .chapter-item {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;

    .chapter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px;
      background-color: #f5f7fa;
      border-radius: 4px;
      cursor: pointer;
      
      &:hover {
        background-color: #ecf5ff;
      }

      h3 {
        font-size: 18px;
        margin: 0;
      }
    }
    
    .lesson-list {
      padding: 0 10px;
      
      .loading-lessons {
        padding: 10px 0;
      }
      
      .lesson-item {
        padding: 12px 10px;
        border-bottom: 1px dashed #ebeef5;
        
        &:last-child {
          border-bottom: none;
        }
        
        a {
          text-decoration: none;
          color: #333;
          display: block;
          
          &:hover {
            color: #409eff;
          }
          
          .lesson-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            &.locked {
              cursor: pointer;
              color: #909399;
              
              &:hover {
                color: #f56c6c;
              }
              
              .el-icon {
                margin-left: 5px;
                font-size: 14px;
              }
            }
            
            .lesson-title {
              flex: 1;
            }
            
            .lesson-meta {
              display: flex;
              align-items: center;
              
              .lesson-duration {
                color: #909399;
                margin-right: 10px;
                font-size: 14px;
              }
              
              .lesson-tag {
                padding: 2px 6px;
                border-radius: 4px;
                font-size: 12px;
                color: white;
                background-color: #f56c6c;
                
                &.free {
                  background-color: #67c23a;
                }
              }
            }
          }
        }
      }
      
      .no-lessons {
        padding: 20px 0;
        text-align: center;
        color: #909399;
      }
    }
  }
  
  .no-chapters {
    padding: 40px 0;
    text-align: center;
    color: #909399;
  }
}
</style>