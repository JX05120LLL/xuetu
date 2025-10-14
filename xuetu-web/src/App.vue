<template>
  <router-view />
  
  <!-- 全局AI助手 - 悬浮按钮 -->
  <AIAssistant v-if="showAIAssistant" />
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AIAssistant from '@/components/AIAssistant.vue'

const route = useRoute()
const userStore = useUserStore()

// 决定是否显示AI助手
// 登录页和注册页不显示
const showAIAssistant = computed(() => {
  const hiddenRoutes = ['/login', '/register']
  return !hiddenRoutes.includes(route.path)
})

onMounted(() => {
  // 从本地存储恢复用户信息
  const token = localStorage.getItem('token')
  if (token) {
    userStore.token = token
    userStore.getUserInfo()
  }
})
</script>

<style scoped>
</style>