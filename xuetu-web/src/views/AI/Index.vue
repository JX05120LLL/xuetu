<template>
  <div class="ai-home">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1><el-icon><ChatDotRound /></el-icon> AI智能学习助手</h1>
        <p class="subtitle">基于人工智能的个性化学习辅导系统</p>
      </div>
    </div>

    <!-- 功能卡片区域 -->
    <div class="features-section">
      <el-row :gutter="24">
        <!-- AI对话助手 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="openChat">
            <div class="feature-icon chat">
              <el-icon :size="48"><ChatLineRound /></el-icon>
            </div>
            <h3>AI智能问答</h3>
            <p>24小时在线答疑，即时解答学习疑问</p>
            <div class="feature-stats">
              <span><el-icon><Message /></el-icon> 已解答 {{ chatStats.totalQuestions }} 个问题</span>
            </div>
          </el-card>
        </el-col>

        <!-- 学习分析 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="goToAnalytics">
            <div class="feature-icon analytics">
              <el-icon :size="48"><TrendCharts /></el-icon>
            </div>
            <h3>学习数据分析</h3>
            <p>AI分析学习数据，生成可视化报告</p>
            <div class="feature-stats">
              <span><el-icon><DataAnalysis /></el-icon> {{ analyticsStats.reports }} 份分析报告</span>
            </div>
          </el-card>
        </el-col>

        <!-- 课程推荐 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="showRecommendations">
            <div class="feature-icon recommend">
              <el-icon :size="48"><MagicStick /></el-icon>
            </div>
            <h3>智能推荐</h3>
            <p>根据学习轨迹，推荐最适合的课程</p>
            <div class="feature-stats">
              <span><el-icon><Star /></el-icon> {{ recommendStats.courses }} 门推荐课程</span>
            </div>
          </el-card>
        </el-col>

        <!-- 学习路径 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="showLearningPath">
            <div class="feature-icon path">
              <el-icon :size="48"><Guide /></el-icon>
            </div>
            <h3>学习路径规划</h3>
            <p>AI生成个性化学习路线图</p>
            <div class="feature-stats">
              <span><el-icon><Operation /></el-icon> {{ pathStats.steps }} 个学习阶段</span>
            </div>
          </el-card>
        </el-col>

        <!-- 智能笔记 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="showSmartNotes">
            <div class="feature-icon notes">
              <el-icon :size="48"><Edit /></el-icon>
            </div>
            <h3>智能笔记助手</h3>
            <p>AI辅助整理笔记，自动生成摘要</p>
            <div class="feature-stats">
              <span><el-icon><Document /></el-icon> {{ notesStats.total }} 篇笔记</span>
            </div>
          </el-card>
        </el-col>

        <!-- 学习建议 -->
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="feature-card" shadow="hover" @click="showSuggestions">
            <div class="feature-icon suggestions">
              <el-icon :size="48"><Promotion /></el-icon>
            </div>
            <h3>学习建议</h3>
            <p>AI分析薄弱环节，提供改进建议</p>
            <div class="feature-stats">
              <span><el-icon><Warning /></el-icon> {{ suggestionsStats.items }} 条建议</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- AI助手聊天窗口（使用已有的AIAssistant组件） -->
    <div v-if="showChatWindow" class="chat-container">
      <div class="chat-overlay" @click="showChatWindow = false"></div>
      <div class="chat-window">
        <div class="chat-header">
          <h3><el-icon><ChatDotRound /></el-icon> AI智能问答</h3>
          <el-button circle size="small" @click="showChatWindow = false">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="chat-content">
          <p class="chat-tip">💡 提示：您可以通过右下角的悬浮按钮随时打开AI助手</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatDotRound,
  ChatLineRound,
  TrendCharts,
  MagicStick,
  Guide,
  Edit,
  Promotion,
  Message,
  DataAnalysis,
  Star,
  Operation,
  Document,
  Warning,
  Close
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const showChatWindow = ref(false)

// 模拟统计数据
const chatStats = ref({
  totalQuestions: 1287
})

const analyticsStats = ref({
  reports: 15
})

const recommendStats = ref({
  courses: 23
})

const pathStats = ref({
  steps: 6
})

const notesStats = ref({
  total: 45
})

const suggestionsStats = ref({
  items: 8
})

// 打开聊天
const openChat = () => {
  ElMessage.info('AI助手已在右下角准备就绪！点击悬浮按钮开始对话')
  // 可以触发全局AIAssistant组件展开
  window.dispatchEvent(new CustomEvent('open-ai-assistant'))
}

// 跳转到分析页面
const goToAnalytics = () => {
  router.push('/ai/analytics')
}

// 显示推荐课程
const showRecommendations = async () => {
  try {
    const { storage } = await import('@/utils/storage')
    const cacheKey = 'recommended_courses'
    
    // 检查缓存（5分钟有效）
    let courses = storage.getCache(cacheKey)
    let fromCache = false
    
    if (!courses) {
      // 无缓存，显示加载提示
      const loading = ElMessage({
        message: '正在获取推荐课程...',
        type: 'info',
        duration: 0,
        icon: 'Loading'
      })
      
      try {
        const { getRecommendedCourses } = await import('@/api/ai')
        courses = await getRecommendedCourses(10)
        loading.close()
        
        // 存入缓存（5分钟有效）
        storage.setCache(cacheKey, courses, 5 * 60 * 1000)
      } catch (err) {
        loading.close()
        throw err
      }
    } else {
      fromCache = true
      ElMessage.success('💾 使用缓存数据，加载更快！')
    }
    
    if (courses && courses.length > 0) {
      const cacheIndicator = fromCache ? '💾 [缓存数据] ' : ''
      const courseList = courses.map((c, i) => 
        `${i + 1}. ${c.courseTitle} (推荐度: ${Math.round(c.matchScore * 100)}%)<br/>   ${c.reason}`
      ).join('<br/><br/>')
      
      ElMessageBox.alert(cacheIndicator + courseList, 'AI智能推荐课程', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '查看全部课程',
        callback: () => {
          router.push('/course/list')
        }
      })
    }
  } catch (error) {
    console.error('获取课程推荐失败:', error)
    ElMessage.warning('暂时无法获取推荐，请稍后再试')
  }
}

// 生成学习路径的缓存键
const getLearningPathCacheKey = (goal: string) => {
  return `learning_path_${goal.trim().toLowerCase().replace(/\s+/g, '_')}`
}

// 格式化剩余时间
const formatRemainingTime = (ms: number): string => {
  const hours = Math.floor(ms / (1000 * 60 * 60))
  if (hours >= 24) {
    const days = Math.floor(hours / 24)
    return `${days}天`
  }
  return `${hours}小时`
}

// 显示学习路径
const showLearningPath = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入您的学习目标', 'AI学习路径规划', {
      confirmButtonText: '生成路径',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：成为Java后端工程师、掌握Vue3前端开发',
      inputType: 'textarea'
    })
    
    if (value && value.trim()) {
      const goal = value.trim()
      const { storage } = await import('@/utils/storage')
      const cacheKey = getLearningPathCacheKey(goal)
      
      // 检查缓存
      const cachedPath = storage.getCache(cacheKey)
      
      if (cachedPath) {
        // 有缓存，询问用户是否使用
        const remainingTime = storage.getCacheRemainingTime(cacheKey)
        const timeStr = formatRemainingTime(remainingTime)
        
        try {
          await ElMessageBox.confirm(
            `检测到您之前生成过类似的学习路径（缓存有效期：还剩${timeStr}）\n\n是否直接使用缓存的结果？`,
            '💾 发现缓存',
            {
              confirmButtonText: '使用缓存（快速）',
              cancelButtonText: '重新生成',
              type: 'info'
            }
          )
          
          // 用户选择使用缓存
          displayLearningPath(cachedPath, true)
          return
        } catch {
          // 用户选择重新生成，继续执行下面的代码
          console.log('用户选择重新生成学习路径')
        }
      }
      
      // 没有缓存或用户选择重新生成
      // 显示加载提示
      const loading = ElMessage({
        message: 'AI正在为您量身定制学习路径，请稍候...',
        type: 'info',
        duration: 0,
        icon: 'Loading'
      })
      
      try {
        const { generateLearningPath } = await import('@/api/ai')
        const path = await generateLearningPath(goal)
        
        loading.close()
        
        if (path && path.advice) {
          // 存入缓存（24小时有效）
          storage.setCache(cacheKey, path, 24 * 60 * 60 * 1000)
          ElMessage.success('✅ 学习路径已生成并缓存')
          
          // 显示学习路径
          displayLearningPath(path, false)
        } else {
          ElMessage.warning('AI返回的内容格式不正确')
        }
      } catch (error) {
        loading.close()
        console.error('生成学习路径失败:', error)
        ElMessage.error('生成学习路径失败，请检查网络连接或稍后再试')
      }
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      console.error('输入失败:', error)
    }
  }
}

// 显示学习路径（统一的显示函数）
const displayLearningPath = (path: any, fromCache: boolean) => {
  // 限制内容长度，避免过长导致渲染问题
  let content = path.advice
  if (content.length > 1500) {
    content = content.substring(0, 1500) + '\n\n...(内容较长，已截断部分内容)'
  }
  
  // 使用纯文本方式显示，更安全稳定
  const cacheIndicator = fromCache ? '💾 [来自缓存] ' : ''
  const displayContent = `
${cacheIndicator}🎯 学习目标: ${path.goal}
⏱ 预计时长: ${path.totalDuration || 120}小时

${content}

💡 建议每天学习2-3小时，循序渐进完成各阶段目标
  `.trim()
  
  ElMessageBox.alert(displayContent, '🎓 AI专属学习路径', {
    confirmButtonText: '开始学习',
    dangerouslyUseHTMLString: false,
    customClass: 'learning-path-text-dialog'
  }).then(() => {
    ElMessage.success('加油学习！')
  }).catch(() => {
    // 用户点击关闭
  })
}

// 显示智能笔记
const showSmartNotes = () => {
  router.push('/user/notes')
}

// 显示学习建议
const showSuggestions = async () => {
  try {
    const { getLearningAdvice } = await import('@/api/ai')
    const advice = await getLearningAdvice()
    
    if (advice) {
      ElMessageBox.alert(advice, 'AI学习建议', {
        dangerouslyUseHTMLString: false,
        confirmButtonText: '知道了'
      })
    }
  } catch (error) {
    console.error('获取学习建议失败:', error)
    ElMessage.warning('暂时无法获取学习建议，请稍后再试')
  }
}
</script>

<style scoped lang="scss">
.ai-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 60px;

  .page-header {
    padding: 80px 24px 60px;
    text-align: center;
    color: white;

    .header-content {
      max-width: 800px;
      margin: 0 auto;

      h1 {
        font-size: 48px;
        font-weight: 700;
        margin: 0 0 16px 0;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);

        .el-icon {
          font-size: 56px;
        }
      }

      .subtitle {
        font-size: 20px;
        opacity: 0.95;
        margin: 0;
        text-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
      }
    }
  }

  .features-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;

    .feature-card {
      border-radius: 16px;
      border: none;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      height: 100%;
      background: white;
      margin-bottom: 24px;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);

        .feature-icon {
          transform: scale(1.1) rotate(5deg);
        }
      }

      :deep(.el-card__body) {
        padding: 32px 24px;
        text-align: center;
      }

      .feature-icon {
        width: 100px;
        height: 100px;
        margin: 0 auto 20px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        transition: all 0.3s;

        &.chat {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.analytics {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.recommend {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.path {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        &.notes {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }

        &.suggestions {
          background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
        }
      }

      h3 {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 12px 0;
      }

      p {
        font-size: 14px;
        color: #606266;
        margin: 0 0 20px 0;
        line-height: 1.6;
      }

      .feature-stats {
        padding-top: 16px;
        border-top: 1px solid #f0f0f0;

        span {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #909399;

          .el-icon {
            font-size: 16px;
            color: #667eea;
          }
        }
      }
    }
  }

  .chat-container {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 2000;

    .chat-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
    }

    .chat-window {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 90%;
      max-width: 600px;
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);

      .chat-header {
        padding: 20px 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 16px 16px 0 0;
        display: flex;
        align-items: center;
        justify-content: space-between;
        color: white;

        h3 {
          font-size: 18px;
          font-weight: 600;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 10px;
        }

        .el-button {
          background: rgba(255, 255, 255, 0.2);
          border: none;
          color: white;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }
        }
      }

      .chat-content {
        padding: 40px 24px;
        text-align: center;

        .chat-tip {
          font-size: 16px;
          color: #606266;
          line-height: 1.8;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .ai-home {
    .page-header {
      padding: 60px 16px 40px;

      h1 {
        font-size: 32px;

        .el-icon {
          font-size: 40px;
        }
      }

      .subtitle {
        font-size: 16px;
      }
    }

    .features-section {
      padding: 0 16px;
    }
  }
}

// 学习路径对话框样式（纯文本模式）
:global(.learning-path-text-dialog) {
  max-width: 700px;

  .el-message-box__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 20px;
    border-radius: 8px 8px 0 0;
  }

  .el-message-box__title {
    color: white;
    font-size: 18px;
    font-weight: 600;
  }

  .el-message-box__content {
    max-height: 500px;
    overflow-y: auto;
    padding: 20px;
  }

  .el-message-box__message {
    font-size: 14px;
    line-height: 1.8;
    color: #333;
    white-space: pre-wrap;
    word-wrap: break-word;
    text-align: left;
  }

  // 自定义滚动条
  ::-webkit-scrollbar {
    width: 6px;
  }

  ::-webkit-scrollbar-track {
    background: #f5f7fa;
    border-radius: 3px;
  }

  ::-webkit-scrollbar-thumb {
    background: #667eea;
    border-radius: 3px;

    &:hover {
      background: #764ba2;
    }
  }
}
</style>
