<template>
  <div class="course-play-page">
    <div class="play-container">
      <!-- 视频播放区域 -->
      <div class="video-section">
        <div id="dplayer" class="video-player"></div>
        <div class="video-info">
          <h2>{{ currentLesson?.title || '请选择课时' }}</h2>
          <div class="lesson-meta">
            <span v-if="currentLesson">{{ currentChapter?.title }}</span>
            <span v-if="currentLesson">时长: {{ formatDuration(currentLesson.duration) }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="sidebar">
        <el-tabs v-model="activeTab">
          <!-- 课程目录 -->
          <el-tab-pane label="目录" name="catalog">
            <div class="chapter-list" v-loading="loading">
              <div
                v-for="chapter in chapters"
                :key="chapter.id"
                class="chapter-item"
              >
                <div class="chapter-title">
                  <i class="el-icon-folder-opened"></i>
                  <span>{{ chapter.title }}</span>
                  <span class="chapter-count">({{ chapter.lessons?.length || 0 }})</span>
                </div>
                <div class="lesson-list">
                  <div
                    v-for="lesson in chapter.lessons"
                    :key="lesson.id"
                    class="lesson-item"
                    :class="{ active: currentLesson?.id === lesson.id }"
                    @click="playLesson(chapter, lesson)"
                  >
                    <i class="el-icon-video-play"></i>
                    <span class="lesson-title">{{ lesson.title }}</span>
                    <span class="lesson-duration">{{ formatDuration(lesson.duration * 60) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 笔记 -->
          <el-tab-pane label="笔记" name="notes">
            <div class="notes-section">
              <!-- 笔记列表 -->
              <div class="notes-list" v-loading="notesLoading">
                <div v-if="notes.length === 0" class="empty-notes">
                  <el-empty description="还没有笔记，快来记录学习心得吧~"></el-empty>
                </div>
                <div
                  v-for="note in notes"
                  :key="note.id"
                  class="note-item"
                >
                  <div class="note-header">
                    <span class="note-time">{{ formatTime(note.timestamp) }}</span>
                    <div class="note-actions">
                      <el-button link type="primary" size="small" @click="editNote(note)">编辑</el-button>
                      <el-button link type="danger" size="small" @click="deleteNote(note.id)">删除</el-button>
                    </div>
                  </div>
                  <div class="note-content">{{ note.content }}</div>
                  <div class="note-footer">
                    <span>{{ note.lessonTitle }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 新建/编辑笔记 -->
              <div class="note-editor">
                <el-input
                  v-model="noteForm.content"
                  type="textarea"
                  :rows="4"
                  placeholder="记录学习心得..."
                  maxlength="500"
                  show-word-limit
                ></el-input>
                <div class="editor-actions">
                  <el-button v-if="noteForm.id" @click="cancelEdit">取消</el-button>
                  <el-button type="primary" @click="saveNote" :loading="noteSaving">
                    {{ noteForm.id ? '更新' : '保存'}}笔记
                  </el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import DPlayer from 'dplayer'
import type { ChapterDTO, LessonDTO } from '@/types/course'
import type { NoteDTO, CreateNoteRequest, UpdateNoteRequest } from '@/types/learning'
import { getChaptersByCourseId } from '@/api/course'
import { getNotesByLesson, createNote, updateNote, deleteNote as deleteNoteApi, updateLearningProgress, checkUserHasCourse, syncCourseProgress } from '@/api/learning'
import { formatDuration as formatDurationUtil, formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const courseId = Number(route.params.id)

// 播放器
let dp: DPlayer | null = null

// 课程章节数据
const loading = ref(false)
const chapters = ref<ChapterDTO[]>([])
const currentChapter = ref<ChapterDTO>()
const currentLesson = ref<LessonDTO>()
const hasAccess = ref(false)

// 笔记相关
const activeTab = ref('catalog')
const notesLoading = ref(false)
const notes = ref<NoteDTO[]>([])
const noteForm = ref<CreateNoteRequest | UpdateNoteRequest>({
  content: '',
  lessonId: 0,
  timestamp: 0
})
const noteSaving = ref(false)

// 格式化时长
const formatDuration = (seconds: number) => {
  return formatDurationUtil(seconds)
}

// 格式化时间
const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 加载课程章节
const loadChapters = async () => {
  loading.value = true
  try {
    // 检查用户是否有权限访问该课程
    hasAccess.value = await checkUserHasCourse(courseId)
    
    // 获取课程章节
    chapters.value = await getChaptersByCourseId(courseId)
    
    // 获取要播放的课时ID（如果URL中有指定）
    const lessonIdParam = route.query.lessonId ? Number(route.query.lessonId) : undefined
    
    // 自动播放指定课时或第一课时
    if (chapters.value.length > 0) {
      if (lessonIdParam) {
        // 查找指定课时
        for (const chapter of chapters.value) {
          if (chapter.lessons) {
            const lesson = chapter.lessons.find(l => l.id === lessonIdParam)
            if (lesson) {
              // 检查是否有权限播放
              if (hasAccess.value || lesson.isFree === 1) {
                playLesson(chapter, lesson)
                break
              } else {
                ElMessage.warning('您还未购买该课程，无法播放付费课时')
                // 尝试播放免费课时
                playFreeLesson()
              }
            }
          }
        }
      } else {
        // 播放第一课时
        playFreeLesson()
      }
    }
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程目录失败')
    
    // 跳回课程详情页
    setTimeout(() => {
      router.push(`/course/${courseId}`)
    }, 2000)
  } finally {
    loading.value = false
  }
}

// 播放免费课时或第一课时（如果已购买）
const playFreeLesson = () => {
  for (const chapter of chapters.value) {
    if (chapter.lessons && chapter.lessons.length > 0) {
      // 如果已购买或有免费课时，则播放
      const freeLesson = chapter.lessons.find(l => l.isFree === 1)
      if (hasAccess.value) {
        // 已购买，播放第一课时
        playLesson(chapter, chapter.lessons[0])
        break
      } else if (freeLesson) {
        // 未购买但有免费课时，播放免费课时
        playLesson(chapter, freeLesson)
        break
      }
    }
  }
}

// 播放课时
const playLesson = async (chapter: ChapterDTO, lesson: LessonDTO) => {
  // 检查是否有权限播放
  if (!hasAccess.value && lesson.isFree !== 1) {
    ElMessage.warning('您还未购买该课程，无法播放付费课时')
    return
  }
  
  // 切换课时前，先保存并同步当前课程进度
  if (dp && currentLesson.value) {
    const currentTime = Math.floor(dp.video.currentTime)
    const totalTime = Math.floor(dp.video.duration)
    if (currentTime > 0 && totalTime > 0) {
      await savePlayProgress(currentTime, totalTime, true) // 同步进度
    }
  }
  
  currentChapter.value = chapter
  currentLesson.value = lesson
  
  // 初始化播放器
  initPlayer(lesson)
  
  // 加载笔记
  loadNotes()
}

// 初始化DPlayer
const initPlayer = (lesson: LessonDTO) => {
  // 销毁旧的播放器
  if (dp) {
    dp.destroy()
  }
  
  const container = document.getElementById('dplayer')
  if (!container) return
  
  dp = new DPlayer({
    container: container,
    video: {
      url: lesson.videoUrl || 'https://example.com/demo.mp4',
      type: 'auto'
    },
    autoplay: true,
    theme: '#1989fa',
    loop: false,
    lang: 'zh-cn',
    screenshot: true,
    hotkey: true,
    preload: 'auto',
    volume: 0.7,
    mutex: true
  })
  
  // 监听播放进度，定时保存学习进度
  let lastSaveTime = 0
  dp.on('timeupdate', () => {
    const currentTime = Math.floor(dp!.video.currentTime)
    if (currentTime - lastSaveTime >= 10) { // 每10秒保存一次
      lastSaveTime = currentTime
      savePlayProgress(currentTime, Math.floor(dp!.video.duration), false) // 不同步
    }
  })
  
  // 播放结束时保存进度并同步
  dp.on('ended', () => {
    savePlayProgress(Math.floor(dp!.video.duration), Math.floor(dp!.video.duration), true) // 同步
  })
}

// 保存播放进度
const savePlayProgress = async (currentTime: number, totalTime: number, shouldSync: boolean = false) => {
  if (!currentLesson.value) return
  
  try {
    await updateLearningProgress({
      courseId: courseId,
      lessonId: currentLesson.value.id,
      progress: Math.floor((currentTime / totalTime) * 100),
      lastPosition: currentTime
    })
    
    // 只在特定时机同步进度（避免请求过于频繁）
    if (shouldSync) {
      try {
        await syncCourseProgress(courseId)
        console.log('✅ 课程进度已同步到数据库')
      } catch (syncError) {
        console.warn('⚠️ 同步课程进度失败:', syncError)
      }
    }
  } catch (error) {
    console.error('保存学习进度失败:', error)
  }
}

// 加载笔记列表
const loadNotes = async () => {
  if (!currentLesson.value) return
  
  notesLoading.value = true
  try {
    notes.value = await getNotesByLesson(currentLesson.value.id)
  } catch (error) {
    console.error('加载笔记失败:', error)
  } finally {
    notesLoading.value = false
  }
}

// 保存笔记
const saveNote = async () => {
  if (!noteForm.value.content.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }
  
  if (!currentLesson.value) {
    ElMessage.warning('请先选择课时')
    return
  }
  
  noteSaving.value = true
  try {
    const currentTime = dp ? Math.floor(dp.video.currentTime) : 0
    
    if ('id' in noteForm.value && noteForm.value.id) {
      // 更新笔记
      await updateNote(noteForm.value.id, {
        content: noteForm.value.content,
        timestamp: currentTime
      })
      ElMessage.success('笔记更新成功')
    } else {
      // 创建笔记
      await createNote({
        lessonId: currentLesson.value.id,
        content: noteForm.value.content,
        timestamp: currentTime
      })
      ElMessage.success('笔记保存成功')
    }
    
    // 重置表单
    noteForm.value = {
      content: '',
      lessonId: 0,
      timestamp: 0
    }
    
    // 重新加载笔记列表
    loadNotes()
  } catch (error) {
    ElMessage.error('保存笔记失败')
  } finally {
    noteSaving.value = false
  }
}

// 编辑笔记
const editNote = (note: NoteDTO) => {
  noteForm.value = {
    id: note.id,
    content: note.content,
    timestamp: note.timestamp
  }
  activeTab.value = 'notes'
}

// 取消编辑
const cancelEdit = () => {
  noteForm.value = {
    content: '',
    lessonId: 0,
    timestamp: 0
  }
}

// 删除笔记
const deleteNote = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNoteApi(id)
    ElMessage.success('笔记删除成功')
    loadNotes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除笔记失败')
    }
  }
}

// 组件挂载
onMounted(() => {
  loadChapters()
})

// 组件卸载
onBeforeUnmount(async () => {
  // 离开页面前，先保存并同步当前课程进度
  if (dp && currentLesson.value) {
    const currentTime = Math.floor(dp.video.currentTime)
    const totalTime = Math.floor(dp.video.duration)
    if (currentTime > 0 && totalTime > 0) {
      await savePlayProgress(currentTime, totalTime, true) // 同步进度
      console.log('📤 离开页面，已同步课程进度')
    }
  }
  
  if (dp) {
    dp.destroy()
    dp = null
  }
})

// 监听activeTab变化，加载笔记
watch(activeTab, (newTab) => {
  if (newTab === 'notes' && currentLesson.value) {
    loadNotes()
  }
})
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
    min-height: 0;
  }

  .video-info {
    background: #1a1a1a;
    color: #fff;
    padding: 20px;

    h2 {
      font-size: 20px;
      margin: 0 0 10px 0;
    }

    .lesson-meta {
      display: flex;
      gap: 20px;
      font-size: 14px;
      color: #999;
    }
  }
}

.sidebar {
  width: 400px;
  background: #fff;
  overflow-y: auto;
  display: flex;
  flex-direction: column;

  :deep(.el-tabs) {
    display: flex;
    flex-direction: column;
    height: 100%;

    .el-tabs__header {
      margin: 0;
    }

    .el-tabs__content {
      flex: 1;
      overflow-y: auto;
    }
  }
}

// 章节列表
.chapter-list {
  padding: 10px;

  .chapter-item {
    margin-bottom: 15px;

    .chapter-title {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
      font-weight: 600;
      font-size: 15px;

      .chapter-count {
        margin-left: auto;
        color: #909399;
        font-weight: normal;
        font-size: 13px;
      }
    }

    .lesson-list {
      margin-top: 8px;

      .lesson-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px;
        cursor: pointer;
        transition: all 0.3s;
        border-radius: 4px;

        &:hover {
          background: #f5f7fa;
        }

        &.active {
          background: #ecf5ff;
          color: #409eff;

          .lesson-title {
            font-weight: 600;
          }
        }

        .lesson-title {
          flex: 1;
          font-size: 14px;
        }

        .lesson-duration {
          color: #909399;
          font-size: 12px;
        }

        i {
          font-size: 16px;
        }
      }
    }
  }
}

// 笔记区域
.notes-section {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 10px;

  .notes-list {
    flex: 1;
    overflow-y: auto;
    margin-bottom: 15px;

    .empty-notes {
      padding: 40px 20px;
    }

    .note-item {
      background: #f5f7fa;
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 12px;

      .note-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

        .note-time {
          color: #409eff;
          font-weight: 600;
          font-size: 14px;
        }

        .note-actions {
          display: flex;
          gap: 5px;
        }
      }

      .note-content {
        color: #333;
        line-height: 1.6;
        margin-bottom: 10px;
        white-space: pre-wrap;
        word-break: break-word;
      }

      .note-footer {
        color: #909399;
        font-size: 12px;
        padding-top: 8px;
        border-top: 1px solid #e4e7ed;
      }
    }
  }

  .note-editor {
    border-top: 1px solid #e4e7ed;
    padding-top: 15px;

    .editor-actions {
      margin-top: 10px;
      display: flex;
      justify-content: flex-end;
      gap: 10px;
    }
  }
}
</style>