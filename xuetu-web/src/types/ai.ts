/**
 * AI相关类型定义
 */

// 聊天请求
export interface ChatRequest {
  question: string
  conversationId?: string
  context?: string
}

// 聊天响应
export interface ChatResponse {
  answer: string
  conversationId: string
  timestamp: string
}

// 聊天历史
export interface ChatHistory {
  id: number
  userId: number
  conversationId: string
  question: string
  answer: string
  createdTime: string
}

// 课程推荐
export interface CourseRecommendation {
  courseId?: number | null
  courseTitle: string
  courseCover?: string | null
  reason: string
  matchScore: number
  category: string
  level: string
}

// 学习路径
export interface LearningPath {
  goal: string
  totalDuration: number
  stages: LearningStage[]
  advice: string
}

// 学习阶段
export interface LearningStage {
  stageName: string
  duration: number
  courses: CourseRecommendation[]
  description: string
  keyPoints?: string[]
}

// 学习报告
export interface LearningReport {
  userId: number
  totalCourses: number
  totalStudyTime: number
  completedCourses: number
  averageProgress: number
  strongPoints: string[]
  weakPoints: string[]
  suggestions: string[]
  weeklyTrend: WeeklyTrend[]
  categoryDistribution: CategoryDistribution[]
}

// 每周趋势
export interface WeeklyTrend {
  date: string
  studyTime: number
}

// 分类分布
export interface CategoryDistribution {
  category: string
  count: number
  percentage: number
}

// 学习建议
export interface LearningAdvice {
  advice: string
  priority: number
  category: string
}