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
import { ref, h } from 'vue'
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
    let courses: any[] | null = storage.getCache(cacheKey)
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
      const courseList = courses.map((c: any, i: number) => 
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
  let loading: any = null
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
          console.log('🎯 使用缓存的学习路径:', cachedPath)
          displayLearningPath(cachedPath, true)
          return
        } catch {
          // 用户选择重新生成，继续执行下面的代码
          console.log('用户选择重新生成学习路径')
        }
      }
      
      // 没有缓存或用户选择重新生成
      // 显示加载提示
      loading = ElMessage({
        message: 'AI正在为您量身定制学习路径，请稍候...（预计需要30-60秒）',
        type: 'info',
        duration: 0,
        icon: 'Loading'
      })
      
      console.log('🚀 开始调用AI生成学习路径:', goal)
      
      try {
        const { generateLearningPath } = await import('@/api/ai')
        const path = await generateLearningPath(goal)
        
        // 关闭loading
        if (loading) {
          loading.close()
          loading = null
        }
        
        console.log('✅ 学习路径生成成功:', path)
        
        if (path && path.advice) {
          // 存入缓存（24小时有效）
          storage.setCache(cacheKey, path, 24 * 60 * 60 * 1000)
          ElMessage.success('✅ 学习路径已生成并缓存')
          
          // 延迟一下再显示，确保loading已关闭
          setTimeout(() => {
            displayLearningPath(path, false)
          }, 100)
        } else {
          console.warn('⚠️ AI返回的数据格式不正确:', path)
          ElMessage.warning('AI返回的内容格式不正确，请重试')
        }
      } catch (error: any) {
        // 确保关闭loading
        if (loading) {
          loading.close()
          loading = null
        }
        
        console.error('❌ 生成学习路径失败:', error)
        
        // 更友好的错误提示
        let errorMsg = '生成学习路径失败'
        if (error.message?.includes('timeout')) {
          errorMsg = 'AI生成超时，请稍后重试'
        } else if (error.message?.includes('Network')) {
          errorMsg = '网络连接失败，请检查后端服务是否启动'
        } else if (error.response?.status === 500) {
          errorMsg = '后端服务错误，请检查MySQL和Redis是否启动'
        }
        
        ElMessage.error(errorMsg)
      }
    }
  } catch (error) {
    // 确保loading被关闭
    if (loading) {
      loading.close()
      loading = null
    }
    
    if (error !== 'cancel' && error !== 'close') {
      console.error('输入失败:', error)
    }
  }
}

// 显示学习路径（直接显示完整内容）
const displayLearningPath = (path: any, fromCache: boolean) => {
  console.log('📖 准备显示学习路径:', path)
  
  try {
    const cacheIndicator = fromCache ? '💾 [缓存数据] ' : ''
    const content = path.advice || ''
    
    // 数据验证
    if (!content || content.trim() === '') {
      console.error('❌ 学习路径内容为空')
      ElMessage.error('学习路径内容为空，请重新生成')
      return
    }
    
    // 构建显示内容 - 直接显示完整内容
    const displayContent = `${cacheIndicator}
🎯 学习目标：${path.goal || '未指定'}
⏱ 预计时长：${path.totalDuration || 120}小时

📋 完整学习路径：
${content}

📚 温馨提示：每天坚持2-3小时学习，循序渐进！
    `.trim()
    
    console.log('✅ 准备显示对话框，内容长度:', content.length)
    
    // 使用MessageBox显示完整内容
    ElMessageBox({
      title: '🎓 AI学习路径',
      message: displayContent,
      confirmButtonText: '好的，开始学习',
      showCancelButton: false,
      dangerouslyUseHTMLString: false,
      customClass: 'learning-path-simple-dialog',
      showClose: true,
      closeOnClickModal: true
    }).then(() => {
      console.log('✅ 用户确认开始学习')
      ElMessage.success('💪 加油学习！')
    }).catch(() => {
      // 用户关闭对话框
    })
  } catch (error) {
    console.error('❌ 显示学习路径失败:', error)
    ElMessage.error('显示失败，请重试')
  }
}

// 显示完整学习路径（使用Dialog代替MessageBox，性能更好）
const showFullLearningPath = (path: any, fromCache: boolean) => {
  const cacheIndicator = fromCache ? '💾 [缓存数据] ' : ''
  const content = path.advice || '暂无详细内容'
  
  // 使用 h 函数创建虚拟节点，分段渲染，避免一次性渲染大量文本
  ElMessageBox({
    title: `${cacheIndicator}🎓 完整学习路径`,
    message: h('div', {
      style: {
        maxHeight: '500px',
        overflowY: 'auto',
        padding: '10px',
        fontSize: '13px',
        lineHeight: '1.8',
        whiteSpace: 'pre-wrap',
        wordBreak: 'break-word'
      }
    }, [
      h('div', { style: { fontWeight: 'bold', marginBottom: '10px', color: '#333' } }, 
        `🎯 ${path.goal}`
      ),
      h('div', { style: { color: '#666', marginBottom: '15px', fontSize: '12px' } }, 
        `⏱ 预计时长：${path.totalDuration || 120}小时`
      ),
      h('div', { style: { color: '#333' } }, content)
    ]),
    confirmButtonText: '关闭',
    customClass: 'learning-path-full-dialog',
    showClose: true,
    closeOnClickModal: true
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

// 学习路径对话框样式（完整内容版 - 优化配色）
:global(.learning-path-simple-dialog) {
  width: 90vw !important;
  max-width: 800px !important;
  min-width: 500px !important;

  .el-message-box__header {
    background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
    color: white;
    padding: 20px 28px;
    border-radius: 12px 12px 0 0;
  }

  .el-message-box__title {
    color: white;
    font-size: 18px;
    font-weight: 600;
  }

  .el-message-box__content {
    max-height: 70vh !important;
    overflow-y: auto;
    padding: 24px 28px;
    background: #ffffff !important;
  }

  .el-message-box__message {
    font-size: 14px !important;
    line-height: 2 !important;
    color: #2c3e50 !important;
    white-space: pre-wrap;
    word-wrap: break-word;
    text-align: left;
    background: #ffffff !important;
  }

  .el-message-box__btns {
    padding: 16px 28px 24px;
    background: #f8f9fa;
    border-top: 1px solid #e9ecef;
    
    button {
      padding: 10px 24px;
      font-size: 14px;
    }
  }

  // 美化滚动条
  ::-webkit-scrollbar {
    width: 8px;
  }

  ::-webkit-scrollbar-track {
    background: #f1f3f5;
    border-radius: 4px;
  }

  ::-webkit-scrollbar-thumb {
    background: #4a90e2;
    border-radius: 4px;
    
    &:hover {
      background: #357abd;
    }
  }
}

// 完整学习路径对话框样式
:global(.learning-path-full-dialog) {
  width: 90vw !important;
  max-width: 750px !important;
  min-width: 500px !important;

  .el-message-box__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 18px 24px;
    border-radius: 8px 8px 0 0;
  }

  .el-message-box__title {
    color: white;
    font-size: 18px;
    font-weight: 600;
  }

  .el-message-box__content {
    padding: 0;
  }

  .el-message-box__message {
    margin: 0;
  }

  // 优化滚动条
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
      background: #5568d3;
    }
  }
}
</style>
