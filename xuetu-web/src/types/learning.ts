/**
 * 学习记录相关类型定义
 */

/**
 * 学习记录
 */
export interface LearningRecord {
  id: number
  userId: number
  courseId: number
  lessonId: number
  progress: number
  lastPosition: number
  createdTime: string
  updatedTime: string
}

/**
 * 用户课程
 */
export interface UserCourse {
  id: number
  userId: number
  courseId: number
  courseName: string
  coverImage: string
  courseDescription: string
  teacherId: number
  teacherName: string
  totalDuration: number
  progress: number
  studyDuration: number
  lastStudyTime: string
  purchaseTime: string
  orderId: number
  status: number
}

/**
 * 笔记
 */
export interface Note {
  id: number
  userId: number
  courseId: number
  lessonId: number
  content: string
  videoTime: number
  createdTime: string
  updatedTime: string
}

/**
 * 笔记DTO
 */
export interface NoteDTO {
  id: number
  lessonId: number
  lessonTitle?: string
  content: string
  timestamp: number
  createdTime: string
  updatedTime: string
}

/**
 * 创建笔记请求
 */
export interface CreateNoteRequest {
  lessonId: number
  content: string
  timestamp: number
}

/**
 * 更新笔记请求
 */
export interface UpdateNoteRequest {
  id?: number
  content: string
  timestamp?: number
  lessonId?: number
}

/**
 * 学习统计
 */
export interface LearningStats {
  totalCourses: number
  totalLessons: number
  totalDuration: number
  todayDuration: number
  weekDuration: number
  monthDuration: number
}
