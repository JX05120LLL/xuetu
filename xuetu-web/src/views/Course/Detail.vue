<template>
  <div class="course-detail-page">
    <Header />
    
    <div class="page-content" v-loading="loading">
      <div class="container" v-if="course">
        <!-- 课程基本信息 -->
        <div class="course-header">
          <div class="course-info-left">
            <h1>{{ course.title }}</h1>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span>讲师: {{ course.teacherName }}</span>
              <span>学员: {{ formatNumber(course.studentCount) }}人</span>
              <span>难度: {{ course.levelName }}</span>
            </div>
          </div>
          
          <div class="course-info-right">
            <img :src="course.coverImage" :alt="course.title" />
            <div class="price-section">
              <div class="price">{{ formatPrice(course.price) }}</div>
              <el-button type="primary" size="large" block @click="handleBuyNow">
                立即购买
              </el-button>
              <el-button size="large" block @click="handleAddToCart">
                加入购物车
              </el-button>
            </div>
          </div>
        </div>

        <!-- 课程详情Tab -->
        <el-tabs v-model="activeTab" class="course-tabs">
          <el-tab-pane label="课程目录" name="catalog">
            <div class="chapter-list">
              <div v-for="chapter in chapters" :key="chapter.id" class="chapter-item">
                <h3>{{ chapter.title }}</h3>
                <!-- 课时列表（待实现） -->
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="课程介绍" name="intro">
            <div class="course-intro">
              {{ course.description }}
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="学员评价" name="comments">
            <div class="comments-section">
              <!-- 评论列表（待实现） -->
              <p>暂无评价</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useCourseStore } from '@/stores/course'
import { useCartStore } from '@/stores/cart'
import { formatPrice, formatNumber } from '@/utils/format'
import type { Course, Chapter } from '@/types/course'

const route = useRoute()
const router = useRouter()
const courseStore = useCourseStore()
const cartStore = useCartStore()

const loading = ref(false)
const course = ref<Course | null>(null)
const chapters = ref<Chapter[]>([])
const activeTab = ref('catalog')

const fetchCourseDetail = async () => {
  loading.value = true
  try {
    const courseId = Number(route.params.id)
    course.value = await courseStore.fetchCourseDetail(courseId)
    chapters.value = await courseStore.fetchChapters(courseId)
  } catch (error) {
    console.error('获取课程详情失败:', error)
  } finally {
    loading.value = false
  }
}

const handleBuyNow = () => {
  if (course.value) {
    router.push({
      path: '/order/confirm',
      query: { courseIds: String(course.value.id) }
    })
  }
}

const handleAddToCart = () => {
  if (course.value) {
    cartStore.addToCart(course.value)
  }
}

onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped lang="scss">
.course-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.course-header {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;

  .course-info-left {
    flex: 1;

    h1 {
      font-size: 28px;
      margin-bottom: 15px;
    }

    .course-desc {
      font-size: 16px;
      color: #666;
      margin-bottom: 20px;
    }

    .course-meta {
      display: flex;
      gap: 20px;
      font-size: 14px;
      color: #999;
    }
  }

  .course-info-right {
    width: 350px;

    img {
      width: 100%;
      border-radius: 8px;
      margin-bottom: 20px;
    }

    .price-section {
      .price {
        font-size: 32px;
        color: #f56c6c;
        font-weight: bold;
        margin-bottom: 20px;
      }

      .el-button {
        margin-bottom: 10px;
      }
    }
  }
}

.course-tabs {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: 400px;
}

.chapter-list {
  .chapter-item {
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    h3 {
      font-size: 18px;
      margin-bottom: 10px;
    }
  }
}
</style>