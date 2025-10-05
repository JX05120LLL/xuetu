<template>
  <div class="order-confirm-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">确认订单</h2>

        <div class="order-content" v-loading="loading">
          <!-- 课程列表 -->
          <div class="order-courses">
            <h3>订单商品</h3>
            <div class="course-list">
              <div v-for="course in selectedCourses" :key="course.id" class="course-item">
                <img :src="course.coverImage" :alt="course.title" />
                <div class="course-info">
                  <h4>{{ course.title }}</h4>
                  <p>讲师: {{ course.teacherName }}</p>
                </div>
                <div class="course-price">{{ formatPrice(course.price) }}</div>
              </div>
            </div>
          </div>

          <!-- 订单信息 -->
          <div class="order-summary">
            <h3>订单信息</h3>
            <div class="summary-item">
              <span>商品总价</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="summary-item total">
              <span>应付金额</span>
              <span class="total-price">{{ formatPrice(totalAmount) }}</span>
            </div>

            <el-button
              type="primary"
              size="large"
              block
              :loading="submitting"
              @click="handleSubmitOrder"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getCourseDetail } from '@/api/course'
import { createOrder } from '@/api/order'
import { formatPrice } from '@/utils/format'
import type { Course } from '@/types/course'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const selectedCourses = ref<Course[]>([])

const totalAmount = computed(() => {
  return selectedCourses.value.reduce((sum, course) => sum + course.price, 0)
})

// 加载选中的课程
const loadSelectedCourses = async () => {
  loading.value = true
  try {
    const courseIds = (route.query.courseIds as string).split(',').map(Number)
    const promises = courseIds.map(id => getCourseDetail(id))
    selectedCourses.value = await Promise.all(promises)
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

// 提交订单
const handleSubmitOrder = async () => {
  submitting.value = true
  try {
    const courseIds = selectedCourses.value.map(course => course.id)
    const order = await createOrder({ courseIds })
    
    ElMessage.success('订单创建成功')
    router.push(`/order/pay/${order.orderNo}`)
  } catch (error) {
    console.error('创建订单失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (route.query.courseIds) {
    loadSelectedCourses()
  } else {
    ElMessage.warning('请先选择课程')
    router.push('/course/list')
  }
})
</script>

<style scoped lang="scss">
.order-confirm-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  padding: 20px 0;
}

.order-content {
  display: flex;
  gap: 20px;
}

.order-courses {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 8px;

  h3 {
    font-size: 18px;
    margin-bottom: 20px;
  }

  .course-item {
    display: flex;
    gap: 15px;
    padding: 15px;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    margin-bottom: 15px;

    img {
      width: 120px;
      height: 80px;
      border-radius: 4px;
      object-fit: cover;
    }

    .course-info {
      flex: 1;

      h4 {
        font-size: 16px;
        margin-bottom: 8px;
      }

      p {
        font-size: 14px;
        color: #666;
      }
    }

    .course-price {
      font-size: 20px;
      color: #f56c6c;
      font-weight: bold;
    }
  }
}

.order-summary {
  width: 350px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: fit-content;

  h3 {
    font-size: 18px;
    margin-bottom: 20px;
  }

  .summary-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    font-size: 14px;

    &.total {
      padding-top: 15px;
      border-top: 1px solid #f0f0f0;
      margin-bottom: 20px;

      .total-price {
        font-size: 24px;
        color: #f56c6c;
        font-weight: bold;
      }
    }
  }
}
</style>