<template>
  <div class="course-play-page">
    <div class="play-container">
      <div class="video-section">
        <div class="video-player">
          <video controls :src="currentLessonUrl" width="100%" height="100%">
            您的浏览器不支持视频播放
          </video>
        </div>
        <div class="video-info">
          <h2>{{ currentLessonTitle }}</h2>
        </div>
      </div>

      <div class="sidebar">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="目录" name="catalog">
            <div class="chapter-list">
              <p>课程目录加载中...</p>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="笔记" name="notes">
            <div class="notes-section">
              <p>笔记功能开发中...</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const currentLessonUrl = ref('')
const currentLessonTitle = ref('课程播放')
const activeTab = ref('catalog')
</script>

<style scoped lang="scss">
.course-play-page {
  min-height: 100vh;
  background: #000;
}

.play-container {
  display: flex;
  height: 100vh;
}

.video-section {
  flex: 1;
  display: flex;
  flex-direction: column;

  .video-player {
    flex: 1;
    background: #000;
  }

  .video-info {
    background: #1a1a1a;
    color: #fff;
    padding: 20px;

    h2 {
      font-size: 20px;
    }
  }
}

.sidebar {
  width: 400px;
  background: #fff;
  overflow-y: auto;
}
</style>