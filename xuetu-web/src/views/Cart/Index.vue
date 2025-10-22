<template>
  <div class="cart-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">购物车</h2>

        <div v-if="cartStore.cartCount === 0" class="empty-cart">
          <el-empty description="购物车为空">
            <el-button type="primary" @click="router.push('/course/list')">
              去选课
            </el-button>
          </el-empty>
        </div>

        <div v-else class="cart-content">
          <!-- 购物车列表 -->
          <div class="cart-list">
            <div
              v-for="course in cartStore.cartItems"
              :key="course.id"
              class="cart-item"
            >
              <el-checkbox v-model="selectedCourses" :label="course.id" />
              
              <img
                :src="course.coverImage || '/images/default-course.jpg'"
                :alt="course.title"
                class="course-cover"
              />

              <div class="course-info">
                <h3>{{ course.title }}</h3>
                <p>讲师: {{ course.teacherName }}</p>
              </div>

              <div class="course-price">
                {{ formatPrice(course.price) }}
              </div>

              <el-button
                type="danger"
                text
                @click="cartStore.removeFromCart(course.id)"
              >
                删除
              </el-button>
            </div>
          </div>

          <!-- 结算栏 -->
          <div class="cart-summary">
            <div class="summary-content">
              <div class="select-all">
                <el-checkbox
                  v-model="selectAll"
                  @change="handleSelectAll"
                >
                  全选
                </el-checkbox>
              </div>

              <div class="summary-info">
                <span>已选 {{ selectedCount }} 件</span>
                <span class="total-price">
                  总计: {{ formatPrice(totalPrice) }}
                </span>
              </div>

              <el-button
                type="primary"
                size="large"
                :disabled="selectedCount === 0"
                @click="handleCheckout"
              >
                去结算
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'

const router = useRouter()
const cartStore = useCartStore()

const selectedCourses = ref<number[]>([])
const selectAll = ref(false)

const selectedCount = computed(() => selectedCourses.value.length)

const totalPrice = computed(() => {
  return cartStore.cartItems
    .filter(course => selectedCourses.value.includes(course.id))
    .reduce((sum, course) => sum + course.price, 0)
})

const handleSelectAll = (value: boolean) => {
  if (value) {
    selectedCourses.value = cartStore.cartItems.map(course => course.id)
  } else {
    selectedCourses.value = []
  }
}

const handleCheckout = () => {
  if (selectedCourses.value.length === 0) {
    ElMessage.warning('请至少选择一门课程')
    return
  }
  
  // 携带选中的课程ID去订单确认页，标记来自购物车
  router.push({
    path: '/order/confirm',
    query: {
      courseIds: selectedCourses.value.join(','),
      from: 'cart' // 标记来自购物车
    }
  })
}
</script>

<style scoped lang="scss">
.cart-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding: 20px 0;
}

.empty-cart {
  background: #fff;
  border-radius: 8px;
  padding: 60px 20px;
}

.cart-content {
  padding-bottom: 100px;
}

.cart-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }

  .course-cover {
    width: 150px;
    height: 100px;
    border-radius: 4px;
    object-fit: cover;
  }

  .course-info {
    flex: 1;

    h3 {
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
    margin-right: 20px;
  }
}

.cart-summary {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #e0e0e0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 99;

  .summary-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .summary-info {
    display: flex;
    align-items: center;
    gap: 30px;
    font-size: 16px;

    .total-price {
      font-size: 24px;
      color: #f56c6c;
      font-weight: bold;
    }
  }
}
</style>