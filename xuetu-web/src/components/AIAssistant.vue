<template>
  <div class="ai-assistant">
    <!-- 悬浮按钮 -->
    <transition name="bounce">
      <div 
        v-if="!isExpanded"
        class="ai-float-button"
        @click="toggleAssistant"
        :class="{ 'has-notification': hasNewMessage }"
      >
        <el-icon :size="28">
          <ChatDotRound />
        </el-icon>
        <div class="button-label">AI助手</div>
        <span v-if="hasNewMessage" class="notification-dot"></span>
      </div>
    </transition>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div 
        v-if="isExpanded" 
        class="ai-chat-panel"
        :style="panelStyle"
        ref="chatPanel"
      >
        <!-- 头部 -->
        <div class="chat-header" @mousedown="handleMouseDown">
          <div class="header-left">
            <el-icon :size="24" color="#fff">
              <ChatDotRound />
            </el-icon>
            <span class="header-title">AI学习助手</span>
            <el-tag size="small" type="success" v-if="isOnline">在线</el-tag>
          </div>
          <div class="header-actions">
            <el-button 
              circle 
              size="small" 
              @click.stop="handleClearChat"
              title="清空对话"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
            <el-button 
              circle 
              size="small" 
              @click.stop="toggleAssistant"
              title="最小化"
            >
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </div>
        
        <!-- 缩放手柄 -->
        <div class="resize-handle" @mousedown.stop="handleResizeStart"></div>

        <!-- 快捷提问 -->
        <div class="quick-questions" v-if="messages.length === 0">
          <div class="welcome-message">
            <el-icon :size="40" color="#409eff"><ChatLineRound /></el-icon>
            <h3>您好！我是AI学习助手</h3>
            <p>我可以帮您：</p>
          </div>
          <div class="question-grid">
            <div 
              v-for="(question, index) in quickQuestions"
              :key="index"
              class="question-card"
              @click="handleQuickQuestion(question.text)"
            >
              <el-icon :size="20" :color="question.color">
                <component :is="question.icon" />
              </el-icon>
              <span>{{ question.text }}</span>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesContainer" v-else>
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            class="message-item"
            :class="message.role"
          >
            <div class="message-avatar">
              <el-avatar :size="32" v-if="message.role === 'user'">
                <el-icon><User /></el-icon>
              </el-avatar>
              <el-avatar :size="32" v-else :style="{ background: '#409eff' }">
                <el-icon><Cpu /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div 
                  class="message-text"
                  v-if="message.role === 'assistant'"
                  v-html="renderMarkdown(message.content)"
                ></div>
                <div class="message-text" v-else>{{ message.content }}</div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
              </div>
            </div>
          </div>

          <!-- 加载中提示 -->
          <div v-if="isLoading" class="message-item assistant">
            <div class="message-avatar">
              <el-avatar :size="32" :style="{ background: '#409eff' }">
                <el-icon><Cpu /></el-icon>
              </el-avatar>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="输入您的问题..."
            @keydown.enter.ctrl="handleSend"
            :disabled="isLoading"
          />
          <el-button 
            type="primary"
            :loading="isLoading"
            @click="handleSend"
            :disabled="!inputMessage.trim()"
            class="send-button"
          >
            <el-icon v-if="!isLoading"><Promotion /></el-icon>
            <span>发送</span>
          </el-button>
          <div class="input-tip">按 Ctrl + Enter 快速发送</div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ChatDotRound, 
  ChatLineRound,
  Delete, 
  Minus, 
  User, 
  Cpu, 
  Promotion,
  QuestionFilled,
  Reading,
  TrendCharts,
  Search
} from '@element-plus/icons-vue'
import { askAI, clearConversation, checkAIHealth, generateLearningReport } from '@/api/ai'
import type { ChatRequest } from '@/types/ai'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 定义消息类型
interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

// 生成唯一ID的辅助函数
const generateUUID = (): string => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// 组件状态
const isExpanded = ref(false)
const isLoading = ref(false)
const isOnline = ref(true)
const hasNewMessage = ref(false)
const inputMessage = ref('')
const messages = ref<Message[]>([])
const messagesContainer = ref<HTMLElement | null>(null)
const chatPanel = ref<HTMLElement | null>(null)
const conversationId = ref<string>(generateUUID())

// 拖动和缩放相关状态
const isDragging = ref(false)
const isResizing = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)
const panelX = ref(0)
const panelY = ref(0)
const panelWidth = ref(400)
const panelHeight = ref(600)
const MIN_WIDTH = 300
const MIN_HEIGHT = 400
const MAX_WIDTH = 800
const MAX_HEIGHT = 900

// 计算面板样式 - 初始居中显示
const panelStyle = ref({
  width: `${panelWidth.value}px`,
  height: `${panelHeight.value}px`,
  left: '50%',
  top: '50%',
  transform: `translate(calc(-50% + ${panelX.value}px), calc(-50% + ${panelY.value}px))`
})

// 快捷提问列表
const quickQuestions = [
  { 
    text: '推荐适合我的课程', 
    icon: 'Reading',
    color: '#409eff' 
  },
  { 
    text: '如何高效学习编程', 
    icon: 'QuestionFilled',
    color: '#67c23a' 
  },
  { 
    text: '分析我的学习情况', 
    icon: 'TrendCharts',
    color: '#e6a23c' 
  },
  { 
    text: '这个知识点不理解', 
    icon: 'Search',
    color: '#f56c6c' 
  }
]

// 切换助手展开/收起
const toggleAssistant = () => {
  isExpanded.value = !isExpanded.value
  hasNewMessage.value = false
  
  if (isExpanded.value) {
    nextTick(() => {
      scrollToBottom()
    })
  } else {
    // 关闭时重置位置和大小
    resetPanel()
  }
}

// 处理快捷提问
const handleQuickQuestion = async (question: string) => {
  // 如果是"分析我的学习情况"，先获取学习数据
  if (question === '分析我的学习情况') {
    inputMessage.value = question
    await handleAnalysisQuestion()
  } else {
    inputMessage.value = question
    handleSend()
  }
}

// 处理学习分析问题（带缓存）
const handleAnalysisQuestion = async () => {
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: '分析我的学习情况',
    timestamp: new Date()
  })
  
  inputMessage.value = ''
  isLoading.value = true
  
  nextTick(() => scrollToBottom())
  
  try {
    const { storage } = await import('@/utils/storage')
    const cacheKey = 'learning_report'
    
    // 检查缓存（10分钟有效）
    let report = storage.getCache(cacheKey)
    let fromCache = false
    
    if (!report) {
      // 无缓存，从服务器获取
      report = await generateLearningReport()
      // 存入缓存（10分钟有效）
      storage.setCache(cacheKey, report, 10 * 60 * 1000)
    } else {
      fromCache = true
    }
    
    // 构建AI回复内容
    const cacheIndicator = fromCache ? '\n💾 *使用缓存数据，响应更快*\n\n' : '\n'
    const analysisText = `您好！我已为您分析了学习情况：${cacheIndicator}📚 **学习概况**
• 学习课程：${report.totalCourses || 0} 门
• 已完成：${report.completedCourses || 0} 门
• 学习时长：${Math.floor((report.totalStudyTime || 0) / 60)} 小时
• 平均进度：${report.averageProgress || 0}%

✨ **您的优势**
${report.strongPoints && report.strongPoints.length > 0 ? report.strongPoints.map(p => `• ${p}`).join('\n') : '• 继续努力学习，发掘您的优势！'}

💡 **改进建议**
${report.weakPoints && report.weakPoints.length > 0 ? report.weakPoints.map(p => `• ${p}`).join('\n') : '• 您做得很好！'}

🎯 **AI学习建议**
${report.suggestions && report.suggestions.length > 0 ? report.suggestions.map(p => `• ${p}`).join('\n') : '• 继续保持学习热情！'}

想要查看更详细的分析报告，可以访问AI智能助手页面哦！`
    
    // 添加AI回复
    messages.value.push({
      role: 'assistant',
      content: analysisText,
      timestamp: new Date()
    })
    
    nextTick(() => scrollToBottom())
  } catch (error) {
    console.error('获取学习分析失败:', error)
    
    // 添加错误提示消息
    messages.value.push({
      role: 'assistant',
      content: '抱歉，暂时无法获取您的学习数据。请确保您已经开始学习课程，或稍后再试。',
      timestamp: new Date()
    })
  } finally {
    isLoading.value = false
  }
}

// 拖动开始
const handleMouseDown = (e: MouseEvent) => {
  isDragging.value = true
  dragStartX.value = e.clientX - panelX.value
  dragStartY.value = e.clientY - panelY.value
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

// 拖动中
const handleMouseMove = (e: MouseEvent) => {
  if (isDragging.value) {
    panelX.value = e.clientX - dragStartX.value
    panelY.value = e.clientY - dragStartY.value
    updatePanelStyle()
  } else if (isResizing.value) {
    const newWidth = e.clientX - (chatPanel.value?.getBoundingClientRect().left || 0)
    const newHeight = e.clientY - (chatPanel.value?.getBoundingClientRect().top || 0)
    
    panelWidth.value = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, newWidth))
    panelHeight.value = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, newHeight))
    updatePanelStyle()
  }
}

// 拖动结束
const handleMouseUp = () => {
  isDragging.value = false
  isResizing.value = false
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
}

// 缩放开始
const handleResizeStart = (e: MouseEvent) => {
  isResizing.value = true
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

// 更新面板样式
const updatePanelStyle = () => {
  panelStyle.value = {
    width: `${panelWidth.value}px`,
    height: `${panelHeight.value}px`,
    left: '50%',
    top: '50%',
    transform: `translate(calc(-50% + ${panelX.value}px), calc(-50% + ${panelY.value}px))`
  }
}

// 重置面板位置和大小
const resetPanel = () => {
  panelX.value = 0
  panelY.value = 0
  panelWidth.value = 400
  panelHeight.value = 600
  updatePanelStyle()
}

// 发送消息
const handleSend = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    timestamp: new Date()
  })
  
  inputMessage.value = ''
  isLoading.value = true
  
  // 滚动到底部
  nextTick(() => scrollToBottom())
  
  try {
    // 调用AI接口
    const request: ChatRequest = {
      question: message,
      conversationId: conversationId.value
    }
    
    const response = await askAI(request)
    
    // 添加AI回复
    messages.value.push({
      role: 'assistant',
      content: response.answer,
      timestamp: new Date()
    })
    
    // 更新会话ID
    conversationId.value = response.conversationId
    
    // 滚动到底部
    nextTick(() => scrollToBottom())
  } catch (error) {
    console.error('AI对话失败:', error)
    ElMessage.error('AI助手暂时无法回复，请稍后再试')
    
    // 添加错误提示消息
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我暂时无法回复您的问题，请稍后再试。',
      timestamp: new Date()
    })
  } finally {
    isLoading.value = false
  }
}

// 清空对话
const handleClearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空当前对话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 清空本地消息
    messages.value = []
    
    // 调用后端清空接口
    try {
      await clearConversation(conversationId.value)
    } catch (error) {
      console.error('清空对话失败:', error)
    }
    
    // 生成新的会话ID
    conversationId.value = generateUUID()
    
    ElMessage.success('对话已清空')
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('清空对话失败:', error)
    }
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (date: Date) => {
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 渲染Markdown格式
const renderMarkdown = (text: string): string => {
  if (!text) return ''
  
  let html = text
  
  // 转义HTML标签（在处理其他标记之前）
  html = html.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
  
  // 处理代码块 ```code```
  html = html.replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
    return `<pre><code class="language-${lang || 'text'}">${code.trim()}</code></pre>`
  })
  
  // 处理表格（必须在处理行内代码之前）
  const tableRegex = /\|(.+)\|[\r\n]+\|([-:\s|]+)\|[\r\n]+((?:\|.+\|[\r\n]*)+)/g
  html = html.replace(tableRegex, (match, header, separator, rows) => {
    const headerCells = header.split('|')
      .map((cell: string) => cell.trim())
      .filter((cell: string) => cell)
      .map((cell: string) => `<th>${cell}</th>`)
      .join('')
    
    const rowsHtml = rows.trim().split('\n')
      .map((row: string) => {
        const cells = row.split('|')
          .map((cell: string) => cell.trim())
          .filter((cell: string) => cell)
          .map((cell: string) => `<td>${cell}</td>`)
          .join('')
        return cells ? `<tr>${cells}</tr>` : ''
      })
      .filter((row: string) => row)
      .join('')
    
    return `<table><thead><tr>${headerCells}</tr></thead><tbody>${rowsHtml}</tbody></table>`
  })
  
  // 处理行内代码 `code`
  html = html.replace(/`([^`]+)`/g, '<code>$1</code>')
  
  // 处理标题 #### 到 #
  html = html.replace(/^####\s+(.+)$/gm, '<h4>$1</h4>')
  html = html.replace(/^###\s+(.+)$/gm, '<h3>$1</h3>')
  html = html.replace(/^##\s+(.+)$/gm, '<h2>$1</h2>')
  html = html.replace(/^#\s+(.+)$/gm, '<h1>$1</h1>')
  
  // 处理粗体 **text** （必须在斜体之前）
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  
  // 处理斜体 *text*
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>')
  
  // 处理列表项 - item 或 • item
  html = html.replace(/^[-•]\s+(.+)$/gm, '___LI___$1___ENDLI___')
  
  // 包装连续的列表项
  html = html.replace(/(___LI___[\s\S]+?___ENDLI___\n?)+/g, (match) => {
    const items = match.split('___ENDLI___')
      .filter(item => item.includes('___LI___'))
      .map(item => `<li>${item.replace('___LI___', '').trim()}</li>`)
      .join('')
    return `<ul>${items}</ul>`
  })
  
  // 处理链接 [text](url)
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>')
  
  // 处理分隔线 ---
  html = html.replace(/^---+$/gm, '<hr>')
  
  // 处理段落：双换行分隔段落
  const lines = html.split('\n')
  let result = ''
  let inParagraph = false
  let paragraphContent = ''
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trim()
    
    // 跳过已经处理的块级元素
    if (line.match(/^<(h[1-6]|ul|table|pre|hr)/)) {
      if (inParagraph && paragraphContent) {
        result += `<p>${paragraphContent}</p>\n`
        paragraphContent = ''
        inParagraph = false
      }
      result += line + '\n'
    } else if (line.match(/<\/(h[1-6]|ul|table|pre)>/)) {
      result += line + '\n'
    } else if (line === '') {
      if (inParagraph && paragraphContent) {
        result += `<p>${paragraphContent}</p>\n`
        paragraphContent = ''
        inParagraph = false
      }
    } else {
      if (!inParagraph) {
        inParagraph = true
        paragraphContent = line
      } else {
        paragraphContent += '<br>' + line
      }
    }
  }
  
  // 处理最后一个段落
  if (inParagraph && paragraphContent) {
    result += `<p>${paragraphContent}</p>`
  }
  
  return result
}

// 检查AI服务状态
const checkHealth = async () => {
  try {
    await checkAIHealth()
    isOnline.value = true
  } catch (error) {
    console.error('AI服务离线:', error)
    isOnline.value = false
  }
}

// 组件挂载时检查健康状态
onMounted(() => {
  checkHealth()
  
  // 监听全局打开AI助手的事件
  window.addEventListener('open-ai-assistant', () => {
    isExpanded.value = true
    nextTick(() => scrollToBottom())
  })
})

// 组件卸载时移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('open-ai-assistant', () => {})
})

// 暴露方法供外部调用
defineExpose({
  open: () => { isExpanded.value = true },
  close: () => { isExpanded.value = false },
  toggle: toggleAssistant
})
</script>

<style scoped lang="scss">
.ai-assistant {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

// 悬浮按钮
.ai-float-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #fff;
  position: relative;
  
  &:hover {
    transform: scale(1.1);
    box-shadow: 0 12px 30px rgba(102, 126, 234, 0.6);
  }
  
  &:active {
    transform: scale(0.95);
  }
  
  .button-label {
    font-size: 10px;
    margin-top: 2px;
  }
  
  .notification-dot {
    position: absolute;
    top: 5px;
    right: 5px;
    width: 12px;
    height: 12px;
    background: #f56c6c;
    border-radius: 50%;
    border: 2px solid #fff;
    animation: pulse 2s infinite;
  }
}

// 聊天面板
.ai-chat-panel {
  position: fixed;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: none;
  user-select: none;
  
  // 缩放手柄
  .resize-handle {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 20px;
    height: 20px;
    cursor: nwse-resize;
    background: linear-gradient(135deg, transparent 0%, transparent 50%, #667eea 50%, #667eea 100%);
    border-bottom-right-radius: 16px;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 4px;
      right: 4px;
      width: 8px;
      height: 8px;
      border-right: 2px solid rgba(255, 255, 255, 0.6);
      border-bottom: 2px solid rgba(255, 255, 255, 0.6);
    }
  }
  
  // 头部
  .chat-header {
    padding: 16px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: move;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;
      
      .header-title {
        font-size: 16px;
        font-weight: 500;
      }
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
      
      .el-button {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        color: #fff;
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }
      }
    }
  }
  
  // 快捷提问
  .quick-questions {
    flex: 1;
    padding: 30px 20px;
    overflow-y: auto;
    
    .welcome-message {
      text-align: center;
      margin-bottom: 30px;
      
      h3 {
        margin: 15px 0 10px;
        color: #303133;
      }
      
      p {
        color: #909399;
        margin: 0;
      }
    }
    
    .question-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 12px;
      
      .question-card {
        padding: 15px;
        border: 1px solid #ebeef5;
        border-radius: 8px;
        display: flex;
        align-items: center;
        gap: 10px;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          border-color: #409eff;
          background: #ecf5ff;
          transform: translateX(5px);
        }
        
        span {
          color: #606266;
          font-size: 14px;
        }
      }
    }
  }
  
  // 消息列表
  .chat-messages {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: #f5f7fa;
    
    .message-item {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
      
      &.user {
        flex-direction: row-reverse;
        
        .message-bubble {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;
          border-radius: 12px 12px 0 12px;
        }
        
        .message-time {
          text-align: right;
        }
        
        // 用户消息中的Markdown样式（白色系）
        .message-text {
          :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
            color: #fff;
            border-color: rgba(255, 255, 255, 0.3);
          }
          
          :deep(strong) {
            color: #fff;
          }
          
          :deep(code) {
            background: rgba(255, 255, 255, 0.2);
            color: #fff;
          }
          
          :deep(pre) {
            background: rgba(0, 0, 0, 0.2);
            
            code {
              color: #fff;
            }
          }
          
          :deep(table) {
            th, td {
              border-color: rgba(255, 255, 255, 0.3);
            }
            
            th {
              background: rgba(255, 255, 255, 0.2);
            }
            
            tr:hover {
              background: rgba(255, 255, 255, 0.1);
            }
          }
          
          :deep(hr) {
            border-color: rgba(255, 255, 255, 0.3);
          }
          
          :deep(a) {
            color: #fff;
            text-decoration: underline;
          }
        }
      }
      
      &.assistant {
        .message-bubble {
          background: #fff;
          border-radius: 12px 12px 12px 0;
        }
      }
      
      .message-avatar {
        flex-shrink: 0;
      }
      
      .message-content {
        max-width: 85%;
        width: fit-content;
        
        .message-bubble {
          padding: 12px 16px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          
          .message-text {
            line-height: 1.6;
            word-wrap: break-word;
            
            // Markdown样式
            :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
              margin: 16px 0 8px 0;
              font-weight: 600;
              line-height: 1.4;
            }
            
            :deep(h1) {
              font-size: 20px;
              border-bottom: 2px solid #ebeef5;
              padding-bottom: 8px;
            }
            
            :deep(h2) {
              font-size: 18px;
              border-bottom: 1px solid #ebeef5;
              padding-bottom: 6px;
            }
            
            :deep(h3) {
              font-size: 16px;
            }
            
            :deep(h4) {
              font-size: 14px;
            }
            
            :deep(p) {
              margin: 8px 0;
            }
            
            :deep(strong) {
              font-weight: 700;
              color: #303133;
            }
            
            :deep(em) {
              font-style: italic;
            }
            
            :deep(code) {
              padding: 2px 6px;
              background: #f5f7fa;
              border-radius: 3px;
              font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
              font-size: 13px;
              color: #e6a23c;
            }
            
            :deep(pre) {
              background: #f5f7fa;
              border-radius: 6px;
              padding: 12px;
              margin: 12px 0;
              overflow-x: auto;
              
              code {
                padding: 0;
                background: none;
                color: #303133;
                font-size: 13px;
                line-height: 1.5;
              }
            }
            
            :deep(ul), :deep(ol) {
              margin: 8px 0;
              padding-left: 24px;
              
              li {
                margin: 4px 0;
                line-height: 1.6;
              }
            }
            
            :deep(table) {
              border-collapse: collapse;
              width: 100%;
              margin: 12px 0;
              font-size: 13px;
              
              th, td {
                border: 1px solid #ebeef5;
                padding: 8px 12px;
                text-align: left;
              }
              
              th {
                background: #f5f7fa;
                font-weight: 600;
              }
              
              tr:hover {
                background: #fafafa;
              }
            }
            
            :deep(hr) {
              border: none;
              border-top: 1px solid #ebeef5;
              margin: 16px 0;
            }
            
            :deep(a) {
              color: #409eff;
              text-decoration: none;
              
              &:hover {
                text-decoration: underline;
              }
            }
          }
          
          .message-time {
            margin-top: 8px;
            font-size: 12px;
            opacity: 0.7;
          }
        }
      }
    }
  }
  
  // 输入区域
  .chat-input-area {
    padding: 15px 20px;
    border-top: 1px solid #ebeef5;
    background: #fff;
    
    .el-textarea {
      margin-bottom: 10px;
    }
    
    .send-button {
      width: 100%;
    }
    
    .input-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #909399;
      text-align: center;
    }
  }
}

// 加载动画
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 5px 0;
  
  span {
    width: 8px;
    height: 8px;
    background: #909399;
    border-radius: 50%;
    animation: typing 1.4s infinite;
    
    &:nth-child(2) {
      animation-delay: 0.2s;
    }
    
    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

// 过渡动画
.bounce-enter-active {
  animation: bounce-in 0.5s;
}

.bounce-leave-active {
  animation: bounce-in 0.3s reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.slide-up-enter-active {
  animation: slide-up 0.3s;
}

.slide-up-leave-active {
  animation: slide-up 0.3s reverse;
}

@keyframes slide-up {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .ai-assistant {
    bottom: 20px;
    right: 20px;
  }
  
  .ai-chat-panel {
    width: calc(100vw - 40px);
    height: calc(100vh - 100px);
    max-width: 400px;
  }
  
  .ai-float-button {
    width: 56px;
    height: 56px;
  }
}
</style>