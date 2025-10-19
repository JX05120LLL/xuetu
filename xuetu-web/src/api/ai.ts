import request from './request'
import type { 
  ChatRequest, 
  ChatResponse, 
  ChatHistory,
  CourseRecommendation,
  LearningPath,
  LearningReport
} from '@/types/ai'

// 辅助函数：转换难度级别
function getLevelName(level: number): string {
  switch (level) {
    case 0: return '初级'
    case 1: return '中级'
    case 2: return '高级'
    default: return '未知'
  }
}

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
  }).then((response: any) => {
    // 转换后端字段到前端期望的字段
    return response.map((item: any) => ({
      courseId: item.courseId,
      courseTitle: item.title,
      courseCover: item.coverImage,
      reason: item.reason,
      matchScore: item.score ? item.score / 100 : 0, // 后端返回0-100，前端期望0-1
      category: item.categoryName || '未分类',
      level: getLevelName(item.level)
    }))
  })
}

// 生成学习路径
export function generateLearningPath(goal: string): Promise<LearningPath> {
  return request({
    url: '/recommend/path',
    method: 'post',
    params: { goal }
  }).then((response: any) => {
    // 转换后端字段到前端期望的字段
    return {
      goal: response.goal || goal,
      totalDuration: response.totalDuration || 0,
      stages: response.stages ? response.stages.map((stage: any) => ({
        stageName: stage.stageName,
        duration: stage.duration || 0,
        description: stage.description || '',
        keyPoints: stage.keyPoints || [],
        courses: stage.courses ? stage.courses.map((course: any) => ({
          courseId: course.courseId,
          courseTitle: course.title,
          courseCover: course.coverImage,
          reason: course.reason || '',
          matchScore: course.score ? course.score / 100 : 0,
          category: course.categoryName || '未分类',
          level: getLevelName(course.level)
        })) : []
      })) : [],
      advice: response.advice || ''
    }
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
  }).then((response: any) => {
    console.log('🔍 AI后端返回的原始数据:', response)
    // 转换后端字段到前端期望的字段
    const result = {
      userId: response.userId,
      totalCourses: response.learningCourses || 0, // 正在学习的课程数
      totalStudyTime: response.learningTime || 0, // 学习时长（分钟）
      completedCourses: response.completedCourses || 0, // 完成的课程数
      averageProgress: response.averageProgress || 0, // 平均进度（后端已计算）
      strongPoints: response.strengths ? [response.strengths] : [],
      weakPoints: response.advices || [],
      suggestions: response.advices || [],
      weeklyTrend: [],
      categoryDistribution: []
    }
    console.log('🔍 转换后的前端数据:', result)
    return result
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