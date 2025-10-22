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
                  
                  <!-- 一级分类及其子分类 -->
                  <div v-for="parentCategory in parentCategories" :key="parentCategory.id" class="parent-category">
                    <div
                      class="category-item parent"
                      :class="{ active: filters.categoryId === parentCategory.id, expanded: expandedCategories[parentCategory.id] }"
                      @click="toggleCategory(parentCategory.id)"
                    >
                      <span>{{ parentCategory.name }}</span>
                      <el-icon v-if="getChildCategories(parentCategory.id).length > 0" class="expand-icon">
                        <ArrowDown v-if="!expandedCategories[parentCategory.id]" />
                        <ArrowUp v-else />
                      </el-icon>
                      <el-icon v-if="filters.categoryId === parentCategory.id && getChildCategories(parentCategory.id).length === 0"><Check /></el-icon>
                    </div>
                    
                    <!-- 二级分类 -->
                    <transition name="slide-fade">
                      <div v-show="expandedCategories[parentCategory.id]" class="child-categories">
                        <div
                          v-for="childCategory in getChildCategories(parentCategory.id)"
                          :key="childCategory.id"
                          class="category-item child"
                          :class="{ active: filters.categoryId === childCategory.id }"
                          @click.stop="selectCategory(childCategory.id)"
                        >
                          <span>{{ childCategory.name }}</span>
                          <el-icon v-if="filters.categoryId === childCategory.id"><Check /></el-icon>
                        </div>
                      </div>
                    </transition>
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
            <!-- 搜索关键词显示 -->
            <div v-if="searchKeyword" class="search-info">
              <div class="search-tag">
                <el-icon><Search /></el-icon>
                <span>搜索：{{ searchKeyword }}</span>
                <el-icon class="close-icon" @click="clearSearch"><Close /></el-icon>
              </div>
              <div class="search-tip">
                找到 <span class="highlight">{{ total }}</span> 门相关课程
              </div>
            </div>

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
            <div class="course-grid">
              <!-- 骨架屏 -->
              <template v-if="loading">
                <div v-for="i in 12" :key="'skeleton-' + i" class="course-skeleton">
                  <el-skeleton :rows="5" animated>
                    <template #template>
                      <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
                      <div style="padding: 14px">
                        <el-skeleton-item variant="h3" style="width: 80%" />
                        <div style="display: flex; align-items: center; justify-content: space-between; margin-top: 16px">
                          <el-skeleton-item variant="text" style="width: 30%" />
                          <el-skeleton-item variant="text" style="width: 30%" />
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </div>
              </template>
              
              <!-- 实际课程列表 -->
              <template v-else>
                <CourseCard
                  v-for="course in courseList"
                  :key="course.id"
                  :course="course"
                />
              </template>
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
import { Check, Star, TrendCharts, Trophy, ArrowDown, ArrowUp, Search, Close } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import CourseCard from '@/components/CourseCard.vue'
import { getCourseList, searchCourses } from '@/api/course'
import { useCourseStore } from '@/stores/course'
import type { Course } from '@/types/course'

const route = useRoute()
const courseStore = useCourseStore()

const loading = ref(false)
const courseList = ref<Course[]>([])
const total = ref(0)
const categories = ref<any[]>([])
const sortType = ref('latest')
const expandedCategories = reactive<Record<number, boolean>>({})
const searchKeyword = ref('') // 搜索关键词

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

// 难度级别选项（根据数据库实际的level值：0=初级, 1=中级, 2=高级）
const levelOptions = [
  { value: undefined, label: '全部', icon: 'Star', color: '#999' },
  { value: 0, label: '初级', icon: 'Star', color: '#67C23A' },
  { value: 1, label: '中级', icon: 'TrendCharts', color: '#E6A23C' },
  { value: 2, label: '高级', icon: 'Trophy', color: '#F56C6C' }
]

// 父级分类（一级分类）- 显示所有分类，不区分层级
const parentCategories = computed(() => {
  // 如果有parent_id为0的分类，只显示一级分类
  const topLevel = categories.value.filter(c => !c.parentId || c.parentId === 0)
  // 如果没有，显示所有分类
  return topLevel.length > 0 ? topLevel : categories.value
})

// 获取子分类
const getChildCategories = (parentId: number) => {
  return categories.value.filter(c => c.parentId === parentId)
}

// 切换分类展开状态
const toggleCategory = (categoryId: number) => {
  const hasChildren = getChildCategories(categoryId).length > 0
  
  if (hasChildren) {
    // 如果有子分类，只切换展开状态
    expandedCategories[categoryId] = !expandedCategories[categoryId]
  } else {
    // 如果没有子分类，选择该分类
    selectCategory(categoryId)
  }
}

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
    let res
    
    // 判断是否是搜索模式
    if (searchKeyword.value) {
      // 使用搜索接口（支持标题+描述搜索）
      console.log('🔍 搜索课程，关键词:', searchKeyword.value)
      res = await searchCourses({
        ...pagination,
        keyword: searchKeyword.value
      })
      console.log('🔍 搜索结果:', res.records.length, '门课程')
    } else {
      // 使用普通列表接口（支持筛选）
      const params = {
        ...pagination,
        ...filters
      }
      console.log('📚 请求课程列表，参数:', params)
      res = await getCourseList(params)
      console.log('📚 获取到课程:', res.records.length, '门，总数:', res.total)
    }
    
    courseList.value = res.records
    total.value = res.total
    
    if (res.records.length > 0) {
      console.log('📚 第一门课程:', res.records[0])
    }
  } catch (error) {
    console.error('获取课程失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类
const fetchCategories = async () => {
  const result = await courseStore.fetchCategories()
  categories.value = result
  
  console.log('📁 获取到的分类数据:', categories.value)
  console.log('📁 一级分类:', parentCategories.value)
  
  // 自动展开第一个有子分类的一级分类
  const firstParentWithChildren = parentCategories.value.find(p => getChildCategories(p.id).length > 0)
  if (firstParentWithChildren) {
    expandedCategories[firstParentWithChildren.id] = true
  }
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

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  // 清空URL中的keyword参数
  window.history.replaceState({}, '', '/course/list')
  handleFilterChange()
}

// 监听路由参数（支持从Header搜索跳转过来）
watch(
  () => route.query.keyword,
  (newKeyword) => {
    if (newKeyword) {
      searchKeyword.value = newKeyword as string
      // 搜索模式下清空筛选条件
      filters.categoryId = undefined
      filters.level = undefined
      filters.title = ''
      pagination.current = 1
      fetchCourseList()
    } else {
      searchKeyword.value = ''
    }
  },
  { immediate: true }
)

onMounted(() => {
  fetchCategories()
  // 如果没有搜索关键词才加载列表，有关键词的话watch会触发
  if (!route.query.keyword) {
    fetchCourseList()
  }
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

.parent-category {
  margin-bottom: 4px;
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

  .el-icon {
    color: #667eea;
    font-weight: bold;
  }

  .expand-icon {
    margin-left: auto;
    color: #999;
    transition: transform 0.3s;
  }

  &.parent {
    font-weight: 600;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
    
    &.expanded {
      .expand-icon {
        transform: rotate(180deg);
      }
    }
  }

  &.child {
    padding-left: 32px;
    font-size: 13px;
    background: #fff;
    border: 1px solid #f0f0f0;
  }

  &:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    transform: translateX(4px);

    span {
      color: #667eea;
    }
    
    &.parent {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
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

.child-categories {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 6px;
  padding-left: 0;
}

// 动画
.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
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

.search-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);

  .search-tag {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    backdrop-filter: blur(10px);

    .el-icon {
      font-size: 16px;
    }

    .close-icon {
      cursor: pointer;
      margin-left: 4px;
      transition: all 0.3s;

      &:hover {
        transform: scale(1.2);
        color: #ffd700;
      }
    }
  }

  .search-tip {
    color: #fff;
    font-size: 14px;
    opacity: 0.95;

    .highlight {
      font-size: 18px;
      font-weight: 700;
      margin: 0 4px;
      color: #ffd700;
    }
  }
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