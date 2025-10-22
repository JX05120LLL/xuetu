<template>
  <div class="notes-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">我的笔记</h2>

        <div class="notes-container">
          <!-- 工具栏 -->
          <div class="notes-toolbar">
            <div class="search-area">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索笔记内容"
                class="search-input"
                clearable
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>

              <el-select
                v-model="filterCourse"
                placeholder="选择课程"
                clearable
                class="filter-select"
                @change="handleSearch"
              >
                <el-option
                  v-for="course in courseOptions"
                  :key="course.value"
                  :label="course.label"
                  :value="course.value"
                />
              </el-select>

              <el-select
                v-model="sortBy"
                placeholder="排序方式"
                class="filter-select"
                @change="handleSearch"
              >
                <el-option label="最新创建" value="latest" />
                <el-option label="最早创建" value="oldest" />
              </el-select>
            </div>

            <div class="action-area">
              <el-button 
                type="danger" 
                icon="Delete" 
                :disabled="selectedNotes.length === 0"
                @click="handleBatchDelete"
              >
                批量删除
              </el-button>
            </div>
          </div>

          <!-- 笔记列表 -->
          <div class="notes-list">
            <!-- 骨架屏 -->
            <template v-if="loading">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="8" v-for="i in 6" :key="'skeleton-' + i">
                  <div class="note-skeleton-card">
                    <el-skeleton animated>
                      <template #template>
                        <div style="padding: 20px">
                          <div style="display: flex; justify-content: space-between">
                            <el-skeleton-item variant="text" style="width: 40%" />
                            <el-skeleton-item variant="circle" style="width: 30px; height: 30px" />
                          </div>
                          <el-skeleton-item variant="h3" style="margin-top: 16px; width: 70%" />
                          <el-skeleton-item variant="text" style="margin-top: 12px" />
                          <el-skeleton-item variant="text" style="margin-top: 8px" />
                          <el-skeleton-item variant="text" style="margin-top: 8px; width: 60%" />
                        </div>
                      </template>
                    </el-skeleton>
                  </div>
                </el-col>
              </el-row>
            </template>
            
            <!-- 实际笔记列表 -->
            <div v-else-if="notes.length > 0">
              <el-checkbox 
                v-model="selectAll"
                @change="handleSelectAllChange"
                class="select-all-checkbox"
              >
                全选
              </el-checkbox>
              
              <el-row :gutter="20">
                <el-col 
                  :xs="24" 
                  :sm="12" 
                  :md="8" 
                  v-for="note in notes" 
                  :key="note.id" 
                  class="note-column"
                >
                  <div class="note-card">
                    <div class="note-header">
                      <el-checkbox 
                        v-model="selectedNotes"
                        :value="note.id"
                      />
                      <div class="course-info">
                        <el-tag size="small" type="success">{{ getCourseNameById(note.courseId) }}</el-tag>
                      </div>
                      <div class="note-actions">
                        <el-button 
                          type="primary" 
                          size="small" 
                          icon="Edit"
                          circle
                          @click="handleEditNote(note)"
                        />
                        <el-button 
                          type="danger" 
                          size="small" 
                          icon="Delete"
                          circle
                          @click="handleDeleteNote(note.id)"
                        />
                      </div>
                    </div>
                    
                    <div class="note-title-display" @click="handleViewNote(note)">
                      <h4>{{ note.title }}</h4>
                    </div>
                    
                    <div class="note-content" @click="handleViewNote(note)">
                      <div class="content-text">{{ note.content }}</div>
                    </div>
                    
                    <div class="note-footer">
                      <div class="time-info">
                        <el-icon><Clock /></el-icon>
                        <span>{{ formatDate(note.createdTime || note.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>

              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[6, 12, 24]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="total"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </div>

            <!-- 空状态 -->
            <el-empty 
              v-else 
              description="" 
              :image-size="200"
              class="custom-empty"
            >
              <template #image>
                <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" width="200">
                  <rect x="40" y="40" width="120" height="120" rx="8" fill="#F3F4F6"/>
                  <line x1="60" y1="70" x2="140" y2="70" stroke="#D1D5DB" stroke-width="4"/>
                  <line x1="60" y1="90" x2="130" y2="90" stroke="#D1D5DB" stroke-width="4"/>
                  <line x1="60" y1="110" x2="120" y2="110" stroke="#D1D5DB" stroke-width="4"/>
                  <circle cx="100" cy="140" r="15" fill="#FEF3C7"/>
                  <path d="M 90 140 L 95 145 L 110 130" stroke="#F59E0B" stroke-width="3" fill="none"/>
                </svg>
              </template>
              <template #description>
                <h3 style="color: #606266; font-size: 18px; margin-bottom: 8px;">
                  {{ isFiltered ? '没有符合条件的笔记' : '还没有创建笔记' }}
                </h3>
                <p style="color: #909399; font-size: 14px;">学习时记录笔记，方便日后复习</p>
              </template>
              <el-button type="primary" size="large" @click="goToCourses">
                <el-icon style="margin-right: 5px"><Reading /></el-icon>
                去学习并记笔记
              </el-button>
            </el-empty>
          </div>
        </div>
      </div>
    </div>

    <!-- 笔记详情对话框 -->
    <el-dialog
      v-model="noteDialogVisible"
      :title="dialogMode === 'view' ? '笔记详情' : (dialogMode === 'edit' ? '编辑笔记' : '创建笔记')"
      width="50%"
    >
      <div class="note-dialog-content">
        <div class="course-lesson-info" v-if="currentNote">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="课程名称">
              {{ getCourseNameById(currentNote.courseId) }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDate(currentNote.createdTime || currentNote.createTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="note-edit-area">
          <el-form :model="noteForm" label-width="80px">
            <el-form-item label="笔记标题">
              <el-input
                v-model="noteForm.title"
                placeholder="请输入笔记标题"
                maxlength="100"
                show-word-limit
                :disabled="dialogMode === 'view'"
              />
            </el-form-item>
            <el-form-item label="笔记内容">
              <el-input
                v-model="noteForm.content"
                type="textarea"
                :rows="8"
                placeholder="请输入笔记内容"
                maxlength="5000"
                show-word-limit
                :disabled="dialogMode === 'view'"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="noteDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="dialogMode === 'edit'" 
            type="primary" 
            @click="submitEditNote" 
            :loading="submitting"
          >
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="30%"
    >
      <span>{{ deleteDialogMessage }}</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete" :loading="submitting">确认删除</el-button>
        </span>
      </template>
    </el-dialog>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { Search, Clock, Reading } from '@element-plus/icons-vue'
import { getMyNotes, updateNote, deleteNote } from '@/api/learning'
import { getCourseList } from '@/api/course'
import { formatDate } from '@/utils/format'
import type { Note } from '@/api/learning'
import type { Course } from '@/types/course'

const router = useRouter()

// 笔记列表数据
const notes = ref<Note[]>([])
const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

// 搜索和过滤
const searchKeyword = ref('')
const filterCourse = ref<number | undefined>(undefined)
const sortBy = ref('latest')
const courseOptions = ref<{ label: string, value: number }[]>([])
const isFiltered = computed(() => searchKeyword.value || filterCourse.value)

// 批量操作
const selectedNotes = ref<number[]>([])
const selectAll = ref(false)

// 笔记详情对话框
const noteDialogVisible = ref(false)
const dialogMode = ref<'view' | 'edit' | 'create'>('view')
const currentNote = ref<Note | null>(null)
const noteForm = reactive({
  title: '',
  content: ''
})

// 删除确认对话框
const deleteDialogVisible = ref(false)
const deleteDialogMessage = ref('')
const noteToDelete = ref<number | number[]>(0)

// 课程列表缓存
const coursesCache = ref<Record<number, string>>({})

// 获取课程名称
const getCourseNameById = (courseId: number) => {
  return coursesCache.value[courseId] || `课程 ${courseId}`
}

// 加载笔记列表
const loadNotes = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...(filterCourse.value ? { courseId: filterCourse.value } : {}),
      ...(searchKeyword.value ? { keyword: searchKeyword.value } : {}),
      ...(sortBy.value === 'oldest' ? { sortOrder: 'asc' } : { sortOrder: 'desc' })
    }
    
    const res = await getMyNotes(params)
    notes.value = res.records
    total.value = res.total
    
    // 重置选中状态
    selectedNotes.value = []
    selectAll.value = false
  } catch (error) {
    console.error('加载笔记失败:', error)
    ElMessage.error('加载笔记失败')
  } finally {
    loading.value = false
  }
}

// 加载课程列表
const loadCourses = async () => {
  try {
    const params = { current: 1, size: 100 }
    const res = await getCourseList(params)
    
    // 构建下拉选项
    courseOptions.value = res.records.map((course: Course) => ({
      label: course.title,
      value: course.id
    }))
    
    // 构建缓存
    res.records.forEach((course: Course) => {
      coursesCache.value[course.id] = course.title
    })
  } catch (error) {
    console.error('加载课程列表失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadNotes()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadNotes()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadNotes()
}

// 查看笔记详情
const handleViewNote = (note: Note) => {
  currentNote.value = note
  noteForm.title = note.title
  noteForm.content = note.content
  dialogMode.value = 'view'
  noteDialogVisible.value = true
}

// 编辑笔记
const handleEditNote = (note: Note) => {
  currentNote.value = note
  noteForm.title = note.title
  noteForm.content = note.content
  dialogMode.value = 'edit'
  noteDialogVisible.value = true
}

// 提交编辑笔记
const submitEditNote = async () => {
  if (!currentNote.value) return
  
  if (!noteForm.title.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }
  
  if (!noteForm.content.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }
  
  submitting.value = true
  try {
    await updateNote(currentNote.value.id, {
      courseId: currentNote.value.courseId,
      lessonId: currentNote.value.lessonId,
      title: noteForm.title,
      content: noteForm.content
    })
    
    // 更新本地数据
    const noteIndex = notes.value.findIndex(item => item.id === currentNote.value?.id)
    if (noteIndex !== -1) {
      notes.value[noteIndex].title = noteForm.title
      notes.value[noteIndex].content = noteForm.content
    }
    
    ElMessage.success('笔记已更新')
    noteDialogVisible.value = false
  } catch (error) {
    console.error('更新笔记失败:', error)
    ElMessage.error('更新笔记失败')
  } finally {
    submitting.value = false
  }
}

// 删除笔记
const handleDeleteNote = (noteId: number) => {
  noteToDelete.value = noteId
  deleteDialogMessage.value = '确定要删除这条笔记吗？'
  deleteDialogVisible.value = true
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedNotes.value.length === 0) return
  
  noteToDelete.value = [...selectedNotes.value]
  deleteDialogMessage.value = `确定要删除选中的 ${selectedNotes.value.length} 条笔记吗？`
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  submitting.value = true
  try {
    if (Array.isArray(noteToDelete.value)) {
      // 批量删除
      const promises = noteToDelete.value.map(id => deleteNote(id))
      await Promise.all(promises)
      ElMessage.success(`成功删除${noteToDelete.value.length}条笔记`)
    } else {
      // 单条删除
      await deleteNote(noteToDelete.value)
      ElMessage.success('笔记已删除')
    }
    
    // 重新加载数据
    loadNotes()
    deleteDialogVisible.value = false
  } catch (error) {
    console.error('删除笔记失败:', error)
    ElMessage.error('删除笔记失败')
  } finally {
    submitting.value = false
  }
}

// 全选/取消全选
const handleSelectAllChange = (val: boolean) => {
  if (val) {
    selectedNotes.value = notes.value.map(note => note.id)
  } else {
    selectedNotes.value = []
  }
}

// 去学习课程
const goToCourses = () => {
  router.push('/user/courses')
}

// 页面加载
onMounted(() => {
  loadCourses()
  loadNotes()
})
</script>

<style scoped lang="scss">
.notes-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.custom-empty {
  padding: 60px 0;
  
  :deep(.el-empty__image) {
    margin-bottom: 24px;
  }
  
  :deep(.el-empty__description) {
    margin-top: 16px;
  }
}

.page-content {
  padding: 30px 0 60px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 15px;
  }
  
  .page-title {
    margin-bottom: 30px;
    font-size: 24px;
    font-weight: 500;
    color: #333;
  }
}

.notes-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.notes-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
  
  .search-area {
    display: flex;
    gap: 10px;
    flex-grow: 1;
    flex-wrap: wrap;
    
    .search-input {
      width: 200px;
    }
    
    .filter-select {
      width: 150px;
    }
  }
  
  .action-area {
    display: flex;
    gap: 10px;
    align-items: center;
  }
}

.select-all-checkbox {
  margin-bottom: 15px;
}

.note-column {
  margin-bottom: 20px;
}

.note-card {
  height: 100%;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  
  &:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  }
  
  .note-header {
    padding: 12px 15px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #ebeef5;
    background-color: #f9fafc;
    
    .course-info {
      margin-left: 10px;
      flex-grow: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .note-actions {
      display: flex;
      gap: 5px;
    }
  }
  
  .note-title-display {
    padding: 12px 15px 0;
    cursor: pointer;
    
    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
  
  .note-content {
    padding: 15px;
    min-height: 80px;
    cursor: pointer;
    
    .content-text {
      display: -webkit-box;
      -webkit-line-clamp: 4;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.6;
      color: #303133;
    }
  }
  
  .note-footer {
    padding: 10px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #ebeef5;
    background-color: #f9fafc;
    color: #909399;
    font-size: 12px;
    
    .time-info, .video-time-info {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.note-dialog-content {
  .course-lesson-info {
    margin-bottom: 20px;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .notes-toolbar {
    flex-direction: column;
    
    .search-area {
      width: 100%;
      
      .search-input, .filter-select {
        width: 100%;
      }
    }
    
    .action-area {
      width: 100%;
      justify-content: flex-end;
    }
  }
}
</style>