<template>
  <div class="user-notes-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">我的笔记</h2>
        <div class="user-notes">
          <el-empty description="暂无笔记" />
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
</script>

<style scoped lang="scss">
.user-notes-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  padding: 20px 0;
}

.user-notes {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  min-height: 400px;
}
</style>