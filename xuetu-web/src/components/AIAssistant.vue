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
      <div v-if="isExpanded" class="ai-chat-panel">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <el-icon :size="24" color="#409eff">
              <ChatDotRound />
            </el-icon>
            <span class="header-title">AI学习助手</span>
            <el-tag size="small" type="success" v-if="isOnline">在线</el-tag>
          </div>
          <div class="header-actions">
            <el-button 
              circle 
              size="small" 
              @click="handleClearChat"
              title="清空对话"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
            <el-button 
              circle 
              size="small" 
              @click="toggleAssistant"
              title="最小化"
            >
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </div>

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
                <div class="message-text">{{ message.content }}</div>
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
import { ref, nextTick, onMounted } from 'vue'
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
import { askAI, clearConversation, checkAIHealth } from '@/api/ai'
import type { ChatRequest } from '@/types/ai'

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
const conversationId = ref<string>(generateUUID())

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
  }
}

// 处理快捷提问
const handleQuickQuestion = (question: string) => {
  inputMessage.value = question
  handleSend()
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
  width: 400px;
  height: 600px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  
  // 头部
  .chat-header {
    padding: 16px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
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
        max-width: 70%;
        
        .message-bubble {
          padding: 12px 16px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
          
          .message-text {
            line-height: 1.6;
            word-wrap: break-word;
            white-space: pre-wrap;
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