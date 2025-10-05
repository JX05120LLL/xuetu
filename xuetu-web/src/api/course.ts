import request from './request'
import type { Course, Category, Chapter, Lesson, Comment } from '@/types/course'
import type { PageParam, PageResult } from '@/types/common'

/**
 * 获取课程列表
 */
export function getCourseList(params: PageParam & {
  title?: string
  categoryId?: number
  status?: number
  level?: number
}): Promise<PageResult<Course>> {
  return request({
    url: '/course/list',
    method: 'get',
    params
  })
}

/**
 * 搜索课程
 */
export function searchCourses(params: PageParam & { keyword: string }): Promise<PageResult<Course>> {
  return request({
    url: '/course/search',
    method: 'get',
    params
  })
}

/**
 * 获取热门课程
 */
export function getHotCourses(limit: number = 10): Promise<Course[]> {
  return request({
    url: '/course/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取课程详情
 */
export function getCourseDetail(id: number): Promise<Course> {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

/**
 * 获取分类列表
 */
export function getCategoryList(): Promise<Category[]> {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

/**
 * 获取课程章节
 */
export function getCourseChapters(courseId: number): Promise<Chapter[]> {
  return request({
    url: `/chapter/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 获取章节课时
 */
export function getChapterLessons(chapterId: number): Promise<Lesson[]> {
  return request({
    url: `/lesson/chapter/${chapterId}`,
    method: 'get'
  })
}

/**
 * 获取课程评论
 */
export function getCourseComments(params: PageParam & { courseId: number }): Promise<PageResult<Comment>> {
  return request({
    url: '/comment/list',
    method: 'get',
    params
  })
}