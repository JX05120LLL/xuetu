<template>
  <div class="course-list-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <!-- 面包屑导航 -->
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>课程列表</el-breadcrumb-item>
          <el-breadcrumb-item v-if="currentCategoryName">{{ currentCategoryName }}</el-breadcrumb-item>
        </el-breadcrumb>

        <div class="course-list-container">
          <!-- 侧边栏筛选 -->
          <aside class="filter-sidebar">
            <div class="filter-card">
              <div class="filter-section">
                <div class="filter-header">
                  <h3>课程分类</h3>
                  <el-button 
                    v-if="filters.categoryId" 
                    link 
                    type="primary" 
                    size="small"
                    @click="clearCategory"
                  >
                    清空
                  </el-button>
                </div>
                <div class="category-list">
                  <div 
                    class="category-item" 
                    :class="{ active: !filters.categoryId }"
                    @click="selectCategory(undefined)"
                  >
                    <span>全部分类</span>
                    <el-icon v-if="!filters.categoryId"><Check /></el-icon>
                  </div>
                  <div
                    v-for="category in categories"
                    :key="category.id"
                    class="category-item"
                    :class="{ active: filters.categoryId === category.id }"
                    @click="selectCategory(category.id)"
                  >
                    <span>{{ category.name }}</span>
                    <el-badge 
                      v-if="category.courseCount" 
                      :value="category.courseCount" 
                      :max="999"
                      class="count-badge"
                    />
                    <el-icon v-if="filters.categoryId === category.id"><Check /></el-icon>
                  </div>
                </div>
              </div>

              <el-divider />

              <div class="filter-section">
                <div class="filter-header">
                  <h3>难度级别</h3>
                  <el-button 
                    v-if="filters.level" 
                    link 
                    type="primary" 
                    size="small"
                    @click="clearLevel"
                  >
                    清空
                  </el-button>
                </div>
                <div class="level-grid">
                  <div 
                    v-for="item in levelOptions" 
                    :key="item.value"
                    class="level-item"
                    :class="{ active: filters.level === item.value }"
                    @click="selectLevel(item.value)"
                  >
                    <el-icon :size="20" :color="item.color">
                      <component :is="item.icon" />
                    </el-icon>
                    <span>{{ item.label }}</span>
                  </div>
                </div>
              </div>
            </div>
          </aside>

          <!-- 课程列表主区域 -->
          <main class="course-list-main">
            <!-- 排序和搜索 -->
            <div class="list-header">
              <div class="total-count">共 {{ total }} 门课程</div>
              <div class="sort-options">
                <el-select v-model="sortType" placeholder="排序方式" @change="handleSortChange">
                  <el-option label="最新" value="latest" />
                  <el-option label="最热" value="hot" />
                  <el-option label="价格从低到高" value="price_asc" />
                  <el-option label="价格从高到低" value="price_desc" />
                </el-select>
              </div>
            </div>

            <!-- 课程网格 -->
            <div class="course-grid" v-loading="loading">
              <CourseCard
                v-for="course in courseList"
                :key="course.id"
                :course="course"
              />
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="pagination.current"
                v-model:page-size="pagination.size"
                :total="total"
                :page-sizes="[12, 24, 36, 48]"
                layout="total, sizes, prev, pager, next, jumper"
                @current-change="fetchCourseList"
                @size-change="fetchCourseList"
              />
            </div>
          </main>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Check, Star, TrendCharts, Trophy } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import CourseCard from '@/components/CourseCard.vue'
import { getCourseList } from '@/api/course'
import { useCourseStore } from '@/stores/course'
import type { Course } from '@/types/course'

const route = useRoute()
const courseStore = useCourseStore()

const loading = ref(false)
const courseList = ref<Course[]>([])
const total = ref(0)
const categories = ref<any[]>([])
const sortType = ref('latest')

const pagination = reactive({
  current: 1,
  size: 12
})

const filters = reactive<{
  categoryId?: number
  level?: number
  title?: string
  status?: number
}>({
  categoryId: undefined,
  level: undefined,
  title: '',
  status: 1  // 只显示已发布的课程
})

// 难度级别选项
const levelOptions = [
  { value: undefined, label: '全部', icon: 'Star', color: '#999' },
  { value: 1, label: '初级', icon: 'Star', color: '#67C23A' },
  { value: 2, label: '中级', icon: 'TrendCharts', color: '#E6A23C' },
  { value: 3, label: '高级', icon: 'Trophy', color: '#F56C6C' }
]

// 当前分类名称
const currentCategoryName = computed(() => {
  if (!filters.categoryId) return ''
  const category = categories.value.find(c => c.id === filters.categoryId)
  return category?.name || ''
})

// 选择分类
const selectCategory = (categoryId: number | undefined) => {
  filters.categoryId = categoryId
  handleFilterChange()
}

// 选择难度
const selectLevel = (level: number | undefined) => {
  filters.level = level
  handleFilterChange()
}

// 清空分类
const clearCategory = () => {
  filters.categoryId = undefined
  handleFilterChange()
}

// 清空难度
const clearLevel = () => {
  filters.level = undefined
  handleFilterChange()
}

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      ...pagination,
      ...filters
    })
    courseList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类
const fetchCategories = async () => {
  const result = await courseStore.fetchCategories()
  categories.value = result
}

// 筛选变化
const handleFilterChange = () => {
  pagination.current = 1
  fetchCourseList()
}

// 排序变化
const handleSortChange = () => {
  // 根据排序类型处理
  fetchCourseList()
}

// 监听路由参数
watch(
  () => route.query.keyword,
  (newKeyword) => {
    if (newKeyword) {
      filters.title = newKeyword as string
      fetchCourseList()
    }
  },
  { immediate: true }
)

onMounted(() => {
  fetchCategories()
  fetchCourseList()
})
</script>

<style scoped lang="scss">
.course-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.course-list-container {
  display: flex;
  gap: 20px;
  padding: 20px 0;
}

.breadcrumb {
  padding: 20px 0;
}

.filter-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.filter-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 80px;
}

.filter-section {
  margin-bottom: 0;

  .filter-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0;
    }
  }
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8f9fa;
  border: 2px solid transparent;

  span {
    font-size: 14px;
    color: #666;
    transition: color 0.3s;
  }

  .count-badge {
    margin-left: auto;
    margin-right: 8px;
  }

  .el-icon {
    color: #667eea;
    font-weight: bold;
  }

  &:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    transform: translateX(4px);

    span {
      color: #667eea;
    }
  }

  &.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

    span {
      color: #fff;
      font-weight: 600;
    }

    .el-icon {
      color: #fff;
    }
  }
}

.level-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.level-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8f9fa;
  border: 2px solid transparent;
  gap: 8px;

  span {
    font-size: 13px;
    color: #666;
    font-weight: 500;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.active {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);

    span {
      color: #667eea;
      font-weight: 600;
    }
  }
}

.el-divider {
  margin: 24px 0;
}

.course-list-main {
  flex: 1;
}

.list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 15px;
  background: #fff;
  border-radius: 8px;

  .total-count {
    font-size: 14px;
    color: #666;
  }
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}
</style>