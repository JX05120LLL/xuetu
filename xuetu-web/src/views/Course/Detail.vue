<template>
  <div class="course-detail-page">
    <Header />
    
    <div class="page-content" v-loading="loading">
      <div class="container" v-if="course">
        <!-- 课程基本信息 -->
        <div class="course-header">
          <div class="course-info-left">
            <div class="title-row">
              <h1>{{ course.title }}</h1>
              <el-tag v-if="hasAccess" type="success" size="large">
                <el-icon><Check /></el-icon> 已购买
              </el-tag>
            </div>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span><el-icon><User /></el-icon> 讲师: {{ course.teacherName }}</span>
              <span><el-icon><UserFilled /></el-icon> 学员: {{ formatNumber(course.studentCount) }}人</span>
              <span><el-icon><TrendCharts /></el-icon> 难度: {{ course.levelName }}</span>
            </div>
          </div>
          
          <div class="course-info-right">
            <img :src="course.coverImage" :alt="course.title" />
            <div class="price-section">
              <!-- 未购买状态 -->
              <template v-if="!hasAccess">
                <div class="price">{{ formatPrice(course.price) }}</div>
                <el-button type="primary" size="large" block @click="handleBuyNow">
                  <el-icon><ShoppingCart /></el-icon> 立即购买
                </el-button>
                <el-button size="large" block @click="handleAddToCart">
                  <el-icon><Plus /></el-icon> 加入购物车
                </el-button>
              </template>
              <!-- 已购买状态 -->
              <template v-else>
                <div class="purchased-info">
                  <el-icon class="check-icon" color="#67c23a" :size="48"><CircleCheck /></el-icon>
                  <p>您已购买此课程</p>
                </div>
                <el-button type="success" size="large" block @click="handleContinueLearning">
                  <el-icon><VideoPlay /></el-icon> 开始学习
                </el-button>
              </template>
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
                        class="lesson-link"
                      >
                        <div class="lesson-info">
                          <span class="lesson-title">
                            <el-icon><VideoPlay /></el-icon>
                            {{ lesson.title }}
                          </span>
                          <div class="lesson-meta">
                            <span class="lesson-duration">
                              <el-icon><Clock /></el-icon>
                              {{ formatDuration(lesson.duration) }}
                            </span>
                            <span class="lesson-tag" :class="{ 'free': lesson.isFree === 1 }">
                              {{ lesson.isFree === 1 ? '免费试看' : hasAccess ? '已购买' : '付费' }}
                            </span>
                          </div>
                        </div>
                      </router-link>
                      <div 
                        v-else
                        class="lesson-info locked"
                        @click="handleBuyNowPrompt"
                      >
                        <span class="lesson-title">
                          <el-icon><Lock /></el-icon>
                          {{ lesson.title }}
                        </span>
                        <div class="lesson-meta">
                          <span class="lesson-duration">
                            <el-icon><Clock /></el-icon>
                            {{ formatDuration(lesson.duration) }}
                          </span>
                          <span class="lesson-tag locked-tag">
                            <el-icon><Lock /></el-icon>
                            需购买
                          </span>
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
              <!-- 发表评论按钮 -->
              <div class="comment-write-section" v-if="hasAccess && !showCommentForm">
                <el-button type="primary" @click="handleShowCommentForm">
                  <el-icon><Plus /></el-icon> 发表评价
                </el-button>
              </div>
              
              <!-- 发表评论表单 -->
              <div class="comment-form" v-if="showCommentForm">
                <div class="form-header">
                  <h3>发表评价</h3>
                  <el-button text @click="showCommentForm = false">取消</el-button>
                </div>
                <div class="rating-input">
                  <label>课程评分：</label>
                  <el-rate v-model="commentForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
                </div>
                <el-input
                  v-model="commentForm.content"
                  type="textarea"
                  :rows="4"
                  placeholder="分享你的学习体验..."
                  maxlength="500"
                  show-word-limit
                />
                <div class="form-actions">
                  <el-button @click="showCommentForm = false">取消</el-button>
                  <el-button type="primary" @click="handleSubmitComment" :loading="commentSubmitting">
                    发表评价
                  </el-button>
                </div>
              </div>
              
              <!-- 评论列表 -->
              <div v-if="commentsLoading" class="loading-comments">
                <el-skeleton :rows="5" animated />
              </div>
              <template v-else>
                <div v-if="comments.length > 0" class="comment-list">
                  <div v-for="comment in comments" :key="comment.id" class="comment-item">
                    <div class="comment-main">
                      <el-avatar 
                        :size="45" 
                        :src="comment.avatar || '/images/default-avatar.svg'"
                        class="user-avatar"
                      >
                        {{ comment.username?.[0] || 'U' }}
                      </el-avatar>
                      <div class="comment-body">
                        <div class="comment-header">
                          <div class="user-info">
                            <span class="username">{{ comment.username || `用户${comment.userId}` }}</span>
                            <el-rate 
                              v-model="comment.rating" 
                              disabled 
                              size="small"
                              class="rating"
                            />
                          </div>
                          <span class="time">{{ formatDate(comment.createdTime) }}</span>
                        </div>
                        <div class="comment-content">
                          {{ comment.content }}
                        </div>
                        <div class="comment-footer">
                          <div class="actions">
                            <el-button 
                              text 
                              size="small" 
                              @click="handleLikeComment(comment.id)"
                              :type="comment.isLiked ? 'primary' : 'default'"
                            >
                              <el-icon><Star /></el-icon> 
                              {{ comment.likeCount > 0 ? comment.likeCount : '点赞' }}
                            </el-button>
                            <el-button 
                              text 
                              size="small"
                              @click="handleShowReply(comment.id)"
                            >
                              回复
                            </el-button>
                          </div>
                        </div>
                        
                        <!-- 回复输入框 -->
                        <div class="reply-input-box" v-if="replyingCommentId === comment.id">
                          <el-input
                            v-model="replyContent"
                            type="textarea"
                            :rows="2"
                            placeholder="写下你的回复..."
                            maxlength="200"
                            show-word-limit
                          />
                          <div class="reply-actions">
                            <el-button size="small" @click="handleCancelReply">取消</el-button>
                            <el-button 
                              size="small" 
                              type="primary" 
                              @click="handleSubmitReply(comment.id)"
                              :loading="replySubmitting"
                            >
                              发送
                            </el-button>
                          </div>
                        </div>
                        
                        <!-- 回复列表 -->
                        <div class="replies-list" v-if="comment.replies && comment.replies.length > 0">
                          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                            <el-avatar 
                              :size="32" 
                              :src="reply.avatar || '/images/default-avatar.svg'"
                            >
                              {{ reply.username?.[0] || 'U' }}
                            </el-avatar>
                            <div class="reply-content">
                              <span class="reply-user">{{ reply.username || `用户${reply.userId}` }}</span>
                              <span v-if="reply.replyToUsername" class="reply-to">
                                回复 <span class="reply-to-user">@{{ reply.replyToUsername }}</span>
                              </span>
                              <span class="reply-text">: {{ reply.content }}</span>
                              <div class="reply-meta">
                                <span class="reply-time">{{ formatDate(reply.createdTime) }}</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="no-comments">
                  <el-empty description="暂无评价，快来抢沙发吧~" />
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
import { 
  ArrowDown, 
  ArrowUp, 
  Star, 
  Lock, 
  Check, 
  User, 
  UserFilled, 
  TrendCharts,
  ShoppingCart,
  Plus,
  CircleCheck,
  VideoPlay,
  Clock
} from '@element-plus/icons-vue'
import { getChapterLessons } from '@/api/course'
import { checkUserHasCourse } from '@/api/learning'
import { getCourseComments, createComment, likeComment, createReply } from '@/api/comment'
import type { Course, Chapter, Lesson } from '@/types/course'
import type { CommentDTO, CommentRequest } from '@/types/comment'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const courseStore = useCourseStore()
const cartStore = useCartStore()
const userStore = useUserStore()

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
const comments = ref<CommentDTO[]>([])
const commentsLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)

// 发表评论
const commentForm = ref<CommentRequest>({
  courseId: 0,
  content: '',
  rating: 5
})
const commentSubmitting = ref(false)
const showCommentForm = ref(false)

// 回复
const replyingCommentId = ref<number | null>(null)
const replyContent = ref('')
const replySubmitting = ref(false)

const fetchCourseDetail = async () => {
  loading.value = true
  try {
    const courseId = Number(route.params.id)
    course.value = await courseStore.fetchCourseDetail(courseId)
    chapters.value = await courseStore.fetchChapters(courseId)
    
    // 检查用户是否购买了课程
    try {
      hasAccess.value = await checkUserHasCourse(courseId)
      console.log('📖 课程访问权限:', hasAccess.value ? '已购买' : '未购买')
    } catch (error) {
      console.error('检查课程购买状态失败:', error)
      hasAccess.value = false // 默认未购买
    }
    
    // 默认打开第一个章节
    if (chapters.value.length > 0) {
      openChapters[chapters.value[0].id] = true
      // 加载第一个章节的课时
      fetchLessons(chapters.value[0].id)
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
    const res = await getCourseComments(course.value.id, {
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

// 发表评论
const handleSubmitComment = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  if (!commentForm.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  
  commentSubmitting.value = true
  try {
    commentForm.value.courseId = course.value!.id
    await createComment(commentForm.value)
    ElMessage.success('评论成功')
    
    // 重置表单
    commentForm.value = {
      courseId: 0,
      content: '',
      rating: 5
    }
    showCommentForm.value = false
    
    // 重新加载评论列表
    currentPage.value = 1
    await fetchComments()
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 点赞评论
const handleLikeComment = async (commentId: number) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  try {
    await likeComment(commentId)
    // 刷新评论列表
    await fetchComments()
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}

// 显示回复框
const handleShowReply = (commentId: number) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  replyingCommentId.value = commentId
  replyContent.value = ''
}

// 取消回复
const handleCancelReply = () => {
  replyingCommentId.value = null
  replyContent.value = ''
}

// 提交回复
const handleSubmitReply = async (commentId: number) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  replySubmitting.value = true
  try {
    await createReply({
      commentId,
      content: replyContent.value
    })
    ElMessage.success('回复成功')
    
    // 重置状态
    replyingCommentId.value = null
    replyContent.value = ''
    
    // 刷新评论列表
    await fetchComments()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  } finally {
    replySubmitting.value = false
  }
}

// 显示评论表单
const handleShowCommentForm = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  if (!hasAccess.value) {
    ElMessage.warning('请先购买课程后再评价')
    return
  }
  
  showCommentForm.value = true
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

    .title-row {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 15px;

      h1 {
        font-size: 28px;
        margin: 0;
      }

      .el-tag {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }

    .course-desc {
      font-size: 16px;
      color: #666;
      margin-bottom: 20px;
      line-height: 1.6;
    }

    .course-meta {
      display: flex;
      gap: 24px;
      font-size: 14px;
      color: #606266;

      span {
        display: flex;
        align-items: center;
        gap: 6px;

        .el-icon {
          font-size: 16px;
          color: #909399;
        }
      }
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

      .purchased-info {
        text-align: center;
        padding: 24px 0;
        margin-bottom: 16px;
        background: #f0f9ff;
        border-radius: 8px;
        border: 2px dashed #67c23a;

        .check-icon {
          margin-bottom: 8px;
        }

        p {
          margin: 0;
          font-size: 16px;
          color: #67c23a;
          font-weight: 500;
        }
      }

      .el-button {
        margin-bottom: 10px;
        
        .el-icon {
          margin-right: 4px;
        }
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
        transition: background-color 0.3s;
        
        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: #f9fafc;
        }
        
        .lesson-link {
          text-decoration: none;
          color: #333;
          display: block;
        }
        
        a, .lesson-link {
          text-decoration: none;
          color: #333;
          display: block;
          
          &:hover {
            color: #409eff;
          }
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
          }
          
          .lesson-title {
            flex: 1;
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 15px;

            .el-icon {
              font-size: 16px;
              flex-shrink: 0;
            }
          }
          
          .lesson-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .lesson-duration {
              color: #909399;
              font-size: 14px;
              display: flex;
              align-items: center;
              gap: 4px;

              .el-icon {
                font-size: 14px;
              }
            }
            
            .lesson-tag {
              padding: 3px 10px;
              border-radius: 12px;
              font-size: 12px;
              color: white;
              background-color: #409eff;
              display: flex;
              align-items: center;
              gap: 4px;
              
              &.free {
                background-color: #67c23a;
              }

              &.locked-tag {
                background-color: #909399;
              }

              .el-icon {
                font-size: 12px;
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
  
  // 评论区样式
  .comments-section {
    padding: 20px 0;
    
    .comment-write-section {
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 1px solid #ebeef5;
    }
    
    .comment-form {
      background: #f5f7fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 30px;
      
      .form-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        
        h3 {
          margin: 0;
          font-size: 16px;
          color: #303133;
        }
      }
      
      .rating-input {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        gap: 10px;
        
        label {
          font-size: 14px;
          color: #606266;
        }
      }
      
      .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        margin-top: 15px;
      }
    }
    
    .loading-comments {
      padding: 20px 0;
    }
    
    .comment-list {
      .comment-item {
        padding: 20px 0;
        border-bottom: 1px solid #ebeef5;
        
        &:last-child {
          border-bottom: none;
        }
        
        .comment-main {
          display: flex;
          gap: 15px;
          
          .user-avatar {
            flex-shrink: 0;
          }
          
          .comment-body {
            flex: 1;
            
            .comment-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 10px;
              
              .user-info {
                display: flex;
                align-items: center;
                gap: 12px;
                
                .username {
                  font-size: 14px;
                  font-weight: 500;
                  color: #303133;
                }
                
                .rating {
                  :deep(.el-rate__icon) {
                    font-size: 14px;
                  }
                }
              }
              
              .time {
                font-size: 12px;
                color: #909399;
              }
            }
            
            .comment-content {
              margin-bottom: 10px;
              line-height: 1.6;
              color: #606266;
              font-size: 14px;
            }
            
            .comment-footer {
              .actions {
                display: flex;
                gap: 10px;
                
                .el-button--text {
                  color: #909399;
                  
                  &:hover {
                    color: #409eff;
                  }
                  
                  &.el-button--primary {
                    color: #409eff;
                  }
                  
                  .el-icon {
                    margin-right: 4px;
                  }
                }
              }
            }
            
            .reply-input-box {
              margin-top: 15px;
              padding: 15px;
              background: #f5f7fa;
              border-radius: 6px;
              
              .reply-actions {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 10px;
              }
            }
            
            .replies-list {
              margin-top: 15px;
              padding-left: 20px;
              border-left: 2px solid #ebeef5;
              
              .reply-item {
                display: flex;
                gap: 10px;
                padding: 10px 0;
                
                &:first-child {
                  padding-top: 0;
                }
                
                .reply-content {
                  flex: 1;
                  font-size: 13px;
                  line-height: 1.5;
                  
                  .reply-user {
                    font-weight: 500;
                    color: #303133;
                  }
                  
                  .reply-to {
                    color: #909399;
                    
                    .reply-to-user {
                      color: #409eff;
                    }
                  }
                  
                  .reply-text {
                    color: #606266;
                  }
                  
                  .reply-meta {
                    margin-top: 5px;
                    
                    .reply-time {
                      font-size: 12px;
                      color: #c0c4cc;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    
    .no-comments {
      padding: 40px 0;
    }
    
    .pagination {
      display: flex;
      justify-content: center;
      margin-top: 30px;
    }
  }
}
</style>