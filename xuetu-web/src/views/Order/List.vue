<template>
  <div class="order-list-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">我的订单</h2>

        <!-- 状态筛选标签 -->
        <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
          <el-tab-pane label="全部订单" name="all" />
          <el-tab-pane label="待支付" :name="0" />
          <el-tab-pane label="已支付" :name="1" />
          <el-tab-pane label="已取消" :name="2" />
          <el-tab-pane label="已完成" :name="4" />
        </el-tabs>

        <!-- 订单列表 -->
        <div class="order-list" v-loading="loading">
          <!-- 有订单数据 -->
          <template v-if="orders.length > 0">
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
            description="暂无订单"
          >
            <el-button type="primary" @click="$router.push('/course/list')">
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
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import OrderItem from '@/components/OrderItem.vue'
import { getMyOrders } from '@/api/order'
import type { Order } from '@/types/order'

const route = useRoute()
const router = useRouter()

// 状态
const activeStatus = ref<string | number>('all')
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

    // 如果不是"全部"，添加状态筛选
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }

    const result = await getMyOrders(params)
    orders.value = result.records || []
    total.value = result.total || 0
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
    activeStatus.value = status === 'all' ? 'all' : Number(status)
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
    activeStatus.value = newStatus === 'all' ? 'all' : Number(newStatus)
    loadOrders()
  }
})
</script>

<style scoped lang="scss">
.order-list-page {
  min-height: 100vh;
  background: #f5f7fa;
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