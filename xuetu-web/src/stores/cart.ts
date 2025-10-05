import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Course } from '@/types/course'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartItems = ref<Course[]>([])

  // 计算属性
  const cartCount = computed(() => cartItems.value.length)
  const totalPrice = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.price, 0)
  })

  // 从本地存储加载购物车
  function loadCart() {
    const localCart = localStorage.getItem('cart')
    if (localCart) {
      try {
        cartItems.value = JSON.parse(localCart)
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    }
  }

  // 保存到本地存储
  function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  // 添加到购物车
  function addToCart(course: Course) {
    // 检查是否已存在
    const exists = cartItems.value.some(item => item.id === course.id)
    if (exists) {
      ElMessage.warning('该课程已在购物车中')
      return false
    }

    cartItems.value.push(course)
    saveCart()
    ElMessage.success('已加入购物车')
    return true
  }

  // 从购物车移除
  function removeFromCart(courseId: number) {
    const index = cartItems.value.findIndex(item => item.id === courseId)
    if (index > -1) {
      cartItems.value.splice(index, 1)
      saveCart()
      ElMessage.success('已移出购物车')
    }
  }

  // 清空购物车
  function clearCart() {
    cartItems.value = []
    saveCart()
  }

  // 检查课程是否在购物车中
  function isInCart(courseId: number) {
    return cartItems.value.some(item => item.id === courseId)
  }

  // 初始化时加载购物车
  loadCart()

  return {
    cartItems,
    cartCount,
    totalPrice,
    addToCart,
    removeFromCart,
    clearCart,
    isInCart
  }
})