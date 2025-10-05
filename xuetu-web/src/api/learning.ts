import request from './request'
import type { PageParam, PageResult } from '@/types/common'

/**
 * 学习记录相关类型
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

export interface UserCourse {
  id: number
  userId: number
  courseId: number
  progress: number
  totalDuration: number
  studyDuration: number
  lastStudyTime: string
}

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
 * 获取我的课程
 */
export function getMyCourses(params: PageParam): Promise<PageResult<UserCourse>> {
  return request({
    url: '/api/user-courses/my',
    method: 'get',
    params
  })
}

/**
 * 获取课程学习记录
 */
export function getCourseRecords(courseId: number): Promise<LearningRecord[]> {
  return request({
    url: `/learning/record/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 获取学习统计数据
 */
export function getLearningStats(): Promise<any> {
  return request({
    url: '/learning/record/stats',
    method: 'get'
  })
}

/**
 * 更新学习进度
 */
export function updateLearningProgress(data: {
  courseId: number
  lessonId: number
  progress: number
  lastPosition: number
}): Promise<boolean> {
  return request({
    url: '/learning/record/progress',
    method: 'post',
    data
  })
}

/**
 * 获取我的笔记列表
 */
export function getMyNotes(params: PageParam & { courseId?: number }): Promise<PageResult<Note>> {
  return request({
    url: '/learning/note/my',
    method: 'get',
    params
  })
}

/**
 * 创建笔记
 */
export function createNote(data: {
  courseId: number
  lessonId: number
  content: string
  videoTime: number
}): Promise<number> {
  return request({
    url: '/learning/note',
    method: 'post',
    data
  })
}

/**
 * 更新笔记
 */
export function updateNote(noteId: number, data: { content: string }): Promise<boolean> {
  return request({
    url: `/learning/note/${noteId}`,
    method: 'put',
    data
  })
}

/**
 * 删除笔记
 */
export function deleteNote(noteId: number): Promise<boolean> {
  return request({
    url: `/learning/note/${noteId}`,
    method: 'delete'
  })
}