/**
 * 课程相关类型定义
 */

export interface Course {
  id: number
  title: string
  description: string
  coverImage: string
  price: number
  originalPrice: number
  teacherId: number
  teacherName: string
  categoryId: number
  categoryName: string
  level: number
  levelName: string
  status: number
  statusName: string
  totalDuration: number
  studentCount: number
  createdTime: string
  updatedTime: string
}

export interface Category {
  id: number
  name: string
  parentId: number
  sortOrder: number
  createdTime: string
  updatedTime: string
}

export interface Chapter {
  id: number
  courseId: number
  title: string
  sortOrder: number
  lessons?: Lesson[]
}

export interface Lesson {
  id: number
  chapterId: number
  title: string
  videoUrl: string
  duration: number
  isFree: number
  sortOrder: number
}

export interface Comment {
  id: number
  userId: number
  courseId: number
  content: string
  rating: number
  likeCount: number
  createdTime: string
  updatedTime: string
}