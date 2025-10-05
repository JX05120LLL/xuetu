import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Course, Category, Chapter } from '@/types/course'
import { getCourseDetail, getCategoryList, getCourseChapters } from '@/api/course'

export const useCourseStore = defineStore('course', () => {
  // 状态
  const currentCourse = ref<Course | null>(null)
  const categories = ref<Category[]>([])
  const chapters = ref<Chapter[]>([])

  // 获取课程详情
  async function fetchCourseDetail(id: number) {
    try {
      currentCourse.value = await getCourseDetail(id)
      return currentCourse.value
    } catch (error) {
      console.error('获取课程详情失败:', error)
      return null
    }
  }

  // 获取分类列表
  async function fetchCategories() {
    try {
      categories.value = await getCategoryList()
      return categories.value
    } catch (error) {
      console.error('获取分类列表失败:', error)
      return []
    }
  }

  // 获取课程章节
  async function fetchChapters(courseId: number) {
    try {
      chapters.value = await getCourseChapters(courseId)
      return chapters.value
    } catch (error) {
      console.error('获取课程章节失败:', error)
      return []
    }
  }

  return {
    currentCourse,
    categories,
    chapters,
    fetchCourseDetail,
    fetchCategories,
    fetchChapters
  }
})