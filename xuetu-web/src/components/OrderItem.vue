<template>
  <div class="order-item">
    <div class="order-header">
      <div class="order-info">
        <span class="order-no">订单号: {{ order.orderNo }}</span>
        <span class="order-time">{{ formatDate(order.createTime) }}</span>
      </div>
      <div class="order-status">
        <el-tag :type="getStatusType(order.status)">
          {{ order.statusName }}
        </el-tag>
      </div>
    </div>

    <div class="order-content">
      <!-- 课程列表 -->
      <div class="course-list">
        <div
          v-for="item in order.orderItems"
          :key="item.id"
          class="course-item"
        >
          <img
            :src="item.courseCover || 'https://via.placeholder.com/80x60'"
            :alt="item.courseTitle"
            class="course-cover"
          />
          <div class="course-info">
            <h4>{{ item.courseTitle }}</h4>
            <p class="course-price">¥{{ item.price.toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <!-- 金额信息 -->
      <div class="amount-info">
        <div class="amount-item">
          <span class="label">订单总额:</span>
          <span class="value">¥{{ order.totalAmount.toFixed(2) }}</span>
        </div>
        <div class="amount-item" v-if="order.discountAmount > 0">
          <span class="label">优惠金额:</span>
          <span class="value discount">-¥{{ order.discountAmount.toFixed(2) }}</span>
        </div>
        <div class="amount-item total">
          <span class="label">实付金额:</span>
          <span class="value">¥{{ order.actualAmount.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="order-actions">
        <!-- 待支付状态 -->
        <template v-if="order.status === 0">
          <el-button
            type="primary"
            size="small"
            @click="handlePay"
          >
            立即支付
          </el-button>
          <el-button
            size="small"
            @click="handleCancel"
          >
            取消订单
          </el-button>
        </template>

        <!-- 已支付状态 -->
        <template v-else-if="order.status === 1">
          <el-button
            type="primary"
            size="small"
            @click="handleGoToCourse"
          >
            去学习
          </el-button>
        </template>

        <!-- 所有状态都显示查看详情 -->
        <el-button
          size="small"
          @click="handleViewDetail"
        >
          查看详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import type { Order } from '@/types/order'
import { cancelOrder } from '@/api/order'

interface Props {
  order: Order
}

interface Emits {
  (e: 'refresh'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const router = useRouter()

/**
 * 格式化日期
 */
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 获取状态标签类型
 */
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: 'warning',  // 待支付
    1: 'success',  // 已支付
    2: 'info',     // 已取消
    3: 'danger',   // 已超时
    4: 'success'   // 已完成
  }
  return typeMap[status] || 'info'
}

/**
 * 立即支付
 */
const handlePay = () => {
  router.push(`/order/pay/${props.order.orderNo}`)
}

/**
 * 取消订单
 */
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该订单吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await cancelOrder(props.order.id)
    ElMessage.success('订单已取消')
    emit('refresh')
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

/**
 * 查看详情
 */
const handleViewDetail = () => {
  router.push(`/order/${props.order.id}`)
}

/**
 * 去学习
 */
const handleGoToCourse = () => {
  router.push('/user/courses')
}
</script>

<style scoped lang="scss">
.order-item {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;

  .order-info {
    display: flex;
    align-items: center;
    gap: 20px;

    .order-no {
      font-weight: 500;
      color: #303133;
    }

    .order-time {
      color: #909399;
      font-size: 14px;
    }
  }
}

.order-content {
  padding: 20px;
}

.course-list {
  margin-bottom: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px dashed #e4e7ed;

  &:last-child {
    border-bottom: none;
  }

  .course-cover {
    width: 80px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 16px;
  }

  .course-info {
    flex: 1;

    h4 {
      font-size: 15px;
      color: #303133;
      margin: 0 0 8px 0;
      line-height: 1.4;
    }

    .course-price {
      color: #f56c6c;
      font-size: 16px;
      font-weight: 500;
      margin: 0;
    }
  }
}

.amount-info {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 16px;

  .amount-item {
    display: flex;
    justify-content: space-between;
    padding: 4px 0;
    font-size: 14px;

    .label {
      color: #606266;
    }

    .value {
      color: #303133;
      font-weight: 500;

      &.discount {
        color: #67c23a;
      }
    }

    &.total {
      border-top: 1px dashed #dcdfe6;
      margin-top: 8px;
      padding-top: 12px;

      .label {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
      }

      .value {
        font-size: 20px;
        color: #f56c6c;
      }
    }
  }
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .course-item {
    .course-cover {
      width: 60px;
      height: 45px;
    }

    .course-info h4 {
      font-size: 14px;
    }
  }

  .order-actions {
    flex-wrap: wrap;
    justify-content: flex-start;

    .el-button {
      flex: 1;
      min-width: 100px;
    }
  }
}
</style>
