import request from './request'
import type { 
  ChatRequest, 
  ChatResponse, 
  ChatHistory,
  CourseRecommendation,
  LearningPath,
  LearningReport
} from '@/types/ai'

/**
 * AI聊天接口
 */

// AI问答
export function askAI(data: ChatRequest): Promise<ChatResponse> {
  return request({
    url: '/chat/ask',
    method: 'post',
    data
  })
}

// 获取对话历史
export function getChatHistory(params: {
  conversationId?: string
  limit?: number
}): Promise<ChatHistory[]> {
  return request({
    url: '/chat/history',
    method: 'get',
    params
  })
}

// 清空对话
export function clearConversation(conversationId: string): Promise<boolean> {
  return request({
    url: '/chat/clear',
    method: 'delete',
    params: { conversationId }
  })
}

// 获取对话列表
export function getConversations(): Promise<string[]> {
  return request({
    url: '/chat/conversations',
    method: 'get'
  })
}

// AI服务健康检查
export function checkAIHealth(): Promise<string> {
  return request({
    url: '/chat/health',
    method: 'get'
  })
}

/**
 * 课程推荐接口
 */

// 获取推荐课程
export function getRecommendedCourses(limit: number = 5): Promise<CourseRecommendation[]> {
  return request({
    url: '/recommend/courses',
    method: 'get',
    params: { limit }
  })
}

// 生成学习路径
export function generateLearningPath(goal: string): Promise<LearningPath> {
  return request({
    url: '/recommend/path',
    method: 'post',
    params: { goal }
  })
}

// 课程推荐服务健康检查
export function checkRecommendHealth(): Promise<string> {
  return request({
    url: '/recommend/health',
    method: 'get'
  })
}

/**
 * 学习分析接口
 */

// 生成学习报告
export function generateLearningReport(): Promise<LearningReport> {
  return request({
    url: '/analysis/report',
    method: 'get'
  })
}

// 获取学习建议
export function getLearningAdvice(): Promise<string> {
  return request({
    url: '/analysis/advice',
    method: 'get'
  })
}

// 学习分析服务健康检查
export function checkAnalysisHealth(): Promise<string> {
  return request({
    url: '/analysis/health',
    method: 'get'
  })
}