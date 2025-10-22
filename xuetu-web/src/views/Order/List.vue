<template>
  <div class="order-list-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">我的订单</h2>

        <!-- 状态筛选标签 -->
        <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
          <el-tab-pane label="全部订单" name="all" />
          <el-tab-pane label="待支付" name="0" />
          <el-tab-pane label="已支付" name="1" />
          <el-tab-pane label="已取消" name="2" />
          <el-tab-pane label="已完成" name="4" />
        </el-tabs>

        <!-- 订单列表 -->
        <div class="order-list">
          <!-- 骨架屏 -->
          <template v-if="loading">
            <div v-for="i in 3" :key="'skeleton-' + i" class="order-skeleton">
              <el-skeleton animated>
                <template #template>
                  <div style="padding: 20px">
                    <div style="display: flex; justify-content: space-between; align-items: center">
                      <el-skeleton-item variant="text" style="width: 200px" />
                      <el-skeleton-item variant="text" style="width: 100px" />
                    </div>
                    <el-divider />
                    <div style="display: flex; gap: 20px">
                      <el-skeleton-item variant="image" style="width: 120px; height: 80px" />
                      <div style="flex: 1">
                        <el-skeleton-item variant="h3" style="width: 60%" />
                        <el-skeleton-item variant="text" style="margin-top: 12px; width: 40%" />
                      </div>
                      <div style="text-align: right">
                        <el-skeleton-item variant="text" style="width: 100px" />
                        <el-skeleton-item variant="button" style="margin-top: 12px; width: 80px" />
                      </div>
                    </div>
                  </div>
                </template>
              </el-skeleton>
            </div>
          </template>
          
          <!-- 有订单数据 -->
          <template v-else-if="orders.length > 0">
            <OrderItem
              v-for="order in orders"
              :key="order.id"
              :order="order"
              @refresh="loadOrders"
            />

            <!-- 分页 -->
            <div class="pagination-container" v-if="total > 0">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="total"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handlePageChange"
              />
            </div>
          </template>

          <!-- 空状态 -->
          <el-empty
            v-else-if="!loading"
            description=""
            class="custom-empty"
          >
            <template #image>
              <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" width="200">
                <rect x="30" y="60" width="140" height="100" rx="8" fill="#F3F4F6"/>
                <rect x="50" y="80" width="80" height="50" rx="4" fill="#E5E7EB"/>
                <circle cx="90" cy="105" r="8" fill="#9CA3AF"/>
                <circle cx="150" cy="140" r="20" fill="#DBEAFE"/>
                <text x="150" y="148" text-anchor="middle" font-size="24" fill="#3B82F6">¥</text>
              </svg>
            </template>
            <template #description>
              <h3 style="color: #606266; font-size: 18px; margin-bottom: 8px;">{{ getEmptyText() }}</h3>
              <p style="color: #909399; font-size: 14px;">发现心仪的课程就下单吧！</p>
            </template>
            <el-button type="primary" size="large" @click="$router.push('/course/list')">
              <el-icon style="margin-right: 5px"><ShoppingBag /></el-icon>
              去选课
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ShoppingBag } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import OrderItem from '@/components/OrderItem.vue'
import { getMyOrders } from '@/api/order'
import type { Order } from '@/types/order'

const route = useRoute()
const router = useRouter()

// 状态
const activeStatus = ref<string>('all')
const loading = ref(false)
const orders = ref<Order[]>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

/**
 * 加载订单列表
 */
const loadOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      current: currentPage.value,
      size: pageSize.value
    }

    // 如果不是"全部"，添加状态筛选（转换为数字）
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }

    console.log('🔍 加载订单列表，参数:', params)
    
    const result = await getMyOrders(params)
    orders.value = result.records || []
    total.value = result.total || 0
    
    console.log('✅ 订单加载成功:', orders.value.length, '条')
  } catch (error: any) {
    console.error('加载订单列表失败:', error)
    ElMessage.error(error.message || '加载订单列表失败')
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

/**
 * 状态切换
 */
const handleStatusChange = () => {
  currentPage.value = 1 // 切换状态时重置页码
  loadOrders()
}

/**
 * 每页大小改变
 */
const handleSizeChange = () => {
  currentPage.value = 1
  loadOrders()
}

/**
 * 页码改变
 */
const handlePageChange = () => {
  loadOrders()
}

/**
 * 从URL参数初始化状态
 */
const initStatusFromQuery = () => {
  const status = route.query.status
  if (status !== undefined && status !== '') {
    activeStatus.value = String(status)
  }
}

// 组件挂载
onMounted(() => {
  initStatusFromQuery()
  loadOrders()
})

// 监听路由查询参数变化
watch(() => route.query.status, (newStatus) => {
  if (newStatus !== undefined) {
    activeStatus.value = String(newStatus)
    currentPage.value = 1
    loadOrders()
  }
})

// 获取空状态文案
const getEmptyText = () => {
  const statusTexts: Record<string, string> = {
    all: '还没有订单',
    '0': '没有待支付的订单',
    '1': '没有已支付的订单',
    '2': '没有已取消的订单',
    '4': '没有已完成的订单'
  }
  return statusTexts[activeStatus.value] || '还没有订单'
}
</script>

<style scoped lang="scss">
.order-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.custom-empty {
  padding: 60px 0;
  
  :deep(.el-empty__image) {
    margin-bottom: 24px;
  }
  
  :deep(.el-empty__description) {
    margin-top: 16px;
  }
  
  :deep(.el-button) {
    margin-top: 24px;
  }
  
  svg {
    filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
  }
}

.page-content {
  padding: 20px 0 40px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  padding: 20px 0;
  margin: 0;
}

.order-list {
  min-height: 400px;
  margin-top: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 20px 0;
}

// 响应式设计
@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }

  .page-title {
    font-size: 20px;
    padding: 16px 0;
  }

  .pagination-container {
    :deep(.el-pagination) {
      .el-pagination__sizes,
      .el-pagination__jump {
        display: none;
      }
    }
  }
}
</style>