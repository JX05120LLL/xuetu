<template>
  <div class="course-card" @click="goToCourseDetail">
    <div class="course-cover">
      <img :src="course.coverImage || 'https://via.placeholder.com/300x180'" :alt="course.title" />
      <div class="course-level">{{ course.levelName }}</div>
    </div>

    <div class="course-info">
      <h3 class="course-title">{{ course.title }}</h3>
      <p class="course-teacher">讲师: {{ course.teacherName }}</p>
      
      <div class="course-stats">
        <span><el-icon><User /></el-icon> {{ formatNumber(course.studentCount) }}</span>
      </div>

      <div class="course-footer">
        <div class="course-price">
          <span class="price">{{ formatPrice(course.price) }}</span>
          <span v-if="course.originalPrice > course.price" class="original-price">
            {{ formatPrice(course.originalPrice) }}
          </span>
        </div>
        
        <el-button type="primary" size="small" @click.stop="handleAddToCart">
          加入购物车
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { formatPrice, formatNumber } from '@/utils/format'
import type { Course } from '@/types/course'

interface Props {
  course: Course
}

const props = defineProps<Props>()
const router = useRouter()
const cartStore = useCartStore()

const goToCourseDetail = () => {
  router.push(`/course/${props.course.id}`)
}

const handleAddToCart = () => {
  cartStore.addToCart(props.course)
}
</script>

<style scoped lang="scss">
.course-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);

  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: 0 12px 28px rgba(102, 126, 234, 0.25);
    border-color: rgba(102, 126, 234, 0.3);

    .course-cover {
      img {
        transform: scale(1.1);
      }

      .course-level {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    .course-info {
      .course-footer {
        .el-button {
          opacity: 1;
          transform: translateY(0);
        }
      }
    }
  }

  .course-cover {
    position: relative;
    width: 100%;
    height: 200px;
    overflow: hidden;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    }

    .course-level {
      position: absolute;
      top: 12px;
      right: 12px;
      background: rgba(0, 0, 0, 0.75);
      backdrop-filter: blur(10px);
      color: #fff;
      padding: 6px 14px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      transition: all 0.3s;
      z-index: 1;
    }
  }

  .course-info {
    padding: 18px;

    .course-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #2d3748;
      transition: color 0.3s;

      &:hover {
        color: #667eea;
      }
    }

    .course-teacher {
      font-size: 14px;
      color: #718096;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      gap: 4px;

      &::before {
        content: '👨‍🏫';
        font-size: 14px;
      }
    }

    .course-stats {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 13px;
      color: #a0aec0;
      margin-bottom: 16px;
      padding: 8px 0;
      border-top: 1px solid #f0f0f0;
      border-bottom: 1px solid #f0f0f0;

      span {
        display: flex;
        align-items: center;
        gap: 5px;
        transition: color 0.3s;

        &:hover {
          color: #667eea;
        }
      }
    }

    .course-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;

      .course-price {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .price {
          font-size: 24px;
          color: #f56c6c;
          font-weight: 700;
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .original-price {
          font-size: 13px;
          color: #cbd5e0;
          text-decoration: line-through;
        }
      }

      .el-button {
        flex-shrink: 0;
        border-radius: 20px;
        font-weight: 500;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        opacity: 0.9;
        transform: translateY(5px);
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .course-card {
    .course-info {
      .course-footer {
        flex-direction: column;
        align-items: flex-start;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>