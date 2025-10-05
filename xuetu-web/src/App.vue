<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

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