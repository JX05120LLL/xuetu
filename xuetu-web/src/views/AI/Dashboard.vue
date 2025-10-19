<template>
  <div class="ai-dashboard-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <el-icon :size="32" color="#409eff">
              <MagicStick />
            </el-icon>
            <div class="header-text">
              <h2 class="page-title">AI智能助手</h2>
              <p class="page-subtitle">让AI帮助您更高效地学习</p>
            </div>
          </div>
          <el-button type="primary" @click="openChat">
            <el-icon><ChatDotRound /></el-icon>
            <span>开始对话</span>
          </el-button>
        </div>

        <!-- 功能卡片 -->
        <el-row :gutter="20" class="feature-cards">
          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card" @click="activeTab = 'recommend'">
              <div class="card-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <el-icon :size="40">
                  <Reading />
                </el-icon>
              </div>
              <h3>智能推荐</h3>
              <p>基于学习历史，为您推荐最适合的课程</p>
            </div>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card" @click="activeTab = 'analysis'">
              <div class="card-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                <el-icon :size="40">
                  <TrendCharts />
                </el-icon>
              </div>
              <h3>学习分析</h3>
              <p>深度分析您的学习数据，提供专业建议</p>
            </div>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8">
            <div class="feature-card" @click="activeTab = 'path'">
              <div class="card-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                <el-icon :size="40">
                  <Guide />
                </el-icon>
              </div>
              <h3>学习路径</h3>
              <p>规划完整学习路径，助您达成学习目标</p>
            </div>
          </el-col>
        </el-row>

        <!-- Tab切换区域 -->
        <el-tabs v-model="activeTab" class="ai-tabs">
          <!-- 课程推荐 -->
          <el-tab-pane label="智能推荐" name="recommend">
            <div class="tab-content" v-loading="loading.recommend">
              <div class="section-header">
                <h3>为您推荐的课程</h3>
                <el-button @click="loadRecommendations" :loading="loading.recommend">
                  <el-icon><Refresh /></el-icon>
                  刷新推荐
                </el-button>
              </div>

              <div v-if="recommendations.length > 0">
                <el-row :gutter="20">
                  <el-col 
                    :xs="24" 
                    :sm="12" 
                    :md="8" 
                    v-for="item in recommendations" 
                    :key="item.courseId"
                  >
                    <div class="recommend-card">
                      <div class="course-cover">
                        <img 
                          :src="item.courseCover || '/images/default-course.jpg'" 
                          :alt="item.courseTitle"
                          @error="handleImageError"
                        />
                        <div class="match-badge">
                          匹配度 {{ Math.round(item.matchScore * 100) }}%
                        </div>
                      </div>
                      <div class="course-info">
                        <h4>{{ item.courseTitle }}</h4>
                        <div class="course-meta">
                          <el-tag size="small">{{ item.category }}</el-tag>
                          <el-tag size="small" type="success">{{ item.level }}</el-tag>
                        </div>
                        <p class="reason">
                          <el-icon><Memo /></el-icon>
                          {{ item.reason }}
                        </p>
                        <el-button type="primary" @click="item.courseId && goToCourse(item.courseId)">
                          查看课程
                        </el-button>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <el-empty v-else description="暂无推荐课程，去完成一些课程后再来看看吧">
                <el-button type="primary" @click="router.push('/course/list')">
                  浏览课程
                </el-button>
              </el-empty>
            </div>
          </el-tab-pane>

          <!-- 学习分析 -->
          <el-tab-pane label="学习分析" name="analysis">
            <div class="tab-content" v-loading="loading.analysis">
              <div class="section-header">
                <h3>学习数据分析</h3>
                <el-button @click="loadAnalysisReport" :loading="loading.analysis">
                  <el-icon><Refresh /></el-icon>
                  刷新报告
                </el-button>
              </div>

              <div v-if="report" class="analysis-content">
                <!-- 总览卡片 -->
                <el-row :gutter="20" class="stats-cards">
                  <el-col :xs="12" :sm="6">
                    <div class="stat-card">
                      <div class="stat-icon" style="background: #409eff">
                        <el-icon><Reading /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ report.totalCourses }}</div>
                        <div class="stat-label">学习课程</div>
                      </div>
                    </div>
                  </el-col>

                  <el-col :xs="12" :sm="6">
                    <div class="stat-card">
                      <div class="stat-icon" style="background: #67c23a">
                        <el-icon><CircleCheck /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ report.completedCourses }}</div>
                        <div class="stat-label">已完成</div>
                      </div>
                    </div>
                  </el-col>

                  <el-col :xs="12" :sm="6">
                    <div class="stat-card">
                      <div class="stat-icon" style="background: #e6a23c">
                        <el-icon><Clock /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ report.totalStudyTime }}分钟</div>
                        <div class="stat-label">学习时长</div>
                      </div>
                    </div>
                  </el-col>

                  <el-col :xs="12" :sm="6">
                    <div class="stat-card">
                      <div class="stat-icon" style="background: #f56c6c">
                        <el-icon><TrendCharts /></el-icon>
                      </div>
                      <div class="stat-info">
                        <div class="stat-value">{{ report.averageProgress }}%</div>
                        <div class="stat-label">平均进度</div>
                      </div>
                    </div>
                  </el-col>
                </el-row>

                <!-- 优势与不足 -->
                <el-row :gutter="20" class="strength-weakness">
                  <el-col :xs="24" :md="12">
                    <div class="info-card">
                      <h4>
                        <el-icon color="#67c23a"><CircleCheckFilled /></el-icon>
                        您的优势
                      </h4>
                      <ul v-if="report.strongPoints && report.strongPoints.length > 0">
                        <li v-for="(point, index) in report.strongPoints" :key="index">
                          {{ point }}
                        </li>
                      </ul>
                      <p v-else class="empty-tip">继续努力学习，发掘您的优势！</p>
                    </div>
                  </el-col>

                  <el-col :xs="24" :md="12">
                    <div class="info-card">
                      <h4>
                        <el-icon color="#e6a23c"><Warning /></el-icon>
                        改进建议
                      </h4>
                      <ul v-if="report.weakPoints && report.weakPoints.length > 0">
                        <li v-for="(point, index) in report.weakPoints" :key="index">
                          {{ point }}
                        </li>
                      </ul>
                      <p v-else class="empty-tip">您做得很好！</p>
                    </div>
                  </el-col>
                </el-row>

                <!-- AI建议 -->
                <div class="suggestions-card">
                  <h4>
                    <el-icon color="#409eff"><MagicStick /></el-icon>
                    AI学习建议
                  </h4>
                  <div v-if="report.suggestions && report.suggestions.length > 0" class="suggestions-list">
                    <el-alert
                      v-for="(suggestion, index) in report.suggestions"
                      :key="index"
                      :title="suggestion"
                      type="info"
                      :closable="false"
                      class="suggestion-item"
                    />
                  </div>
                  <p v-else class="empty-tip">暂无建议</p>
                </div>
              </div>

              <el-empty v-else description="暂无学习数据，开始学习后即可查看分析报告">
                <el-button type="primary" @click="router.push('/course/list')">
                  开始学习
                </el-button>
              </el-empty>
            </div>
          </el-tab-pane>

          <!-- 学习路径 -->
          <el-tab-pane label="学习路径" name="path">
            <div class="tab-content">
              <div class="section-header">
                <h3>规划学习路径</h3>
              </div>

              <!-- 目标输入 -->
              <div class="path-input-section">
                <el-form :model="pathForm" label-width="100px">
                  <el-form-item label="学习目标">
                    <el-input
                      v-model="pathForm.goal"
                      placeholder="例如：成为Java后端工程师、掌握前端开发技能"
                      clearable
                    >
                      <template #append>
                        <el-button 
                          type="primary" 
                          @click="generatePath"
                          :loading="loading.path"
                        >
                          生成路径
                        </el-button>
                      </template>
                    </el-input>
                  </el-form-item>
                </el-form>

                <!-- 快捷目标 -->
                <div class="quick-goals">
                  <span class="label">快捷目标：</span>
                  <el-tag
                    v-for="goal in quickGoals"
                    :key="goal"
                    @click="pathForm.goal = goal"
                    class="goal-tag"
                    effect="plain"
                  >
                    {{ goal }}
                  </el-tag>
                </div>
              </div>

              <!-- 学习路径结果 -->
              <div v-if="learningPath" class="path-result" v-loading="loading.path">
                <div class="path-header">
                  <h4>{{ learningPath.goal }}</h4>
                  <el-tag type="success">预计 {{ learningPath.totalDuration }} 小时</el-tag>
                </div>

                <!-- AI完整回复 -->
                <div v-if="learningPath.advice" class="ai-full-advice">
                  <div class="advice-header">
                    <el-icon><MagicStick /></el-icon>
                    <span>AI学习规划建议</span>
                  </div>
                  <div class="advice-content" v-html="formatAdviceText(learningPath.advice)"></div>
                </div>

                <!-- 学习阶段 -->
                <div class="stages-title">
                  <el-icon><Guide /></el-icon>
                  <span>详细学习路径</span>
                </div>
                <el-timeline class="path-timeline">
                  <el-timeline-item
                    v-for="(stage, index) in learningPath.stages"
                    :key="index"
                    :timestamp="`阶段 ${index + 1}`"
                    placement="top"
                    :color="index === 0 ? '#409eff' : index === 1 ? '#67c23a' : '#e6a23c'"
                  >
                    <el-card class="stage-card">
                      <div class="stage-header">
                        <h5>{{ stage.stageName }}</h5>
                        <div class="stage-meta">
                          <el-tag size="small" effect="plain">
                            <el-icon><Clock /></el-icon>
                            {{ stage.duration }} 小时
                          </el-tag>
                          <el-tag size="small" type="success" effect="plain">
                            <el-icon><Reading /></el-icon>
                            {{ stage.courses.length }} 门课程
                          </el-tag>
                        </div>
                      </div>
                      
                      <p class="stage-desc">{{ stage.description }}</p>
                      
                      <!-- 学习要点 -->
                      <div v-if="stage.keyPoints && stage.keyPoints.length > 0" class="key-points">
                        <h6><el-icon><Star /></el-icon> 学习要点</h6>
                        <ul>
                          <li v-for="(point, pIndex) in stage.keyPoints" :key="pIndex">
                            {{ point }}
                          </li>
                        </ul>
                      </div>
                      
                      <!-- 阶段课程 -->
                      <div v-if="stage.courses && stage.courses.length > 0" class="courses-section">
                        <h6><el-icon><Reading /></el-icon> 推荐课程</h6>
                        <div class="stage-courses">
                        <div 
                          v-for="(course, index) in stage.courses" 
                          :key="course.courseId || index"
                          class="mini-course-card"
                          :class="{ 'virtual-course': !course.courseCover }"
                          @click="course.courseId ? goToCourse(course.courseId) : null"
                        >
                          <!-- 真实课程：显示图片 -->
                          <img 
                            v-if="course.courseCover"
                            :src="course.courseCover" 
                            :alt="course.courseTitle"
                            @error="handleImageError"
                          />
                          <!-- 虚拟课程：显示图标 -->
                          <div v-else class="course-icon">
                            <el-icon :size="24"><Reading /></el-icon>
                          </div>
                          <div class="mini-course-info">
                            <div class="course-title">{{ course.courseTitle }}</div>
                            <el-tag v-if="course.level" size="small">{{ course.level }}</el-tag>
                            <el-tag v-else size="small" type="info">推荐学习</el-tag>
                          </div>
                        </div>
                        </div>
                      </div>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import {
  MagicStick,
  ChatDotRound,
  Reading,
  TrendCharts,
  Guide,
  Refresh,
  Memo,
  CircleCheck,
  Clock,
  CircleCheckFilled,
  Warning,
  Star
} from '@element-plus/icons-vue'
import {
  getRecommendedCourses,
  generateLearningReport,
  generateLearningPath
} from '@/api/ai'
import type {
  CourseRecommendation,
  LearningReport,
  LearningPath
} from '@/types/ai'

const router = useRouter()

// 页面状态
const activeTab = ref('recommend')
const loading = reactive({
  recommend: false,
  analysis: false,
  path: false
})

// 数据
const recommendations = ref<CourseRecommendation[]>([])
const report = ref<LearningReport | null>(null)
const learningPath = ref<LearningPath | null>(null)

// 学习路径表单
const pathForm = reactive({
  goal: ''
})

// 快捷目标
const quickGoals = [
  '成为Java后端工程师',
  '掌握前端开发',
  '学习Python数据分析',
  '入门机器学习',
  '掌握移动端开发'
]

// 加载课程推荐
const loadRecommendations = async () => {
  loading.recommend = true
  try {
    recommendations.value = await getRecommendedCourses(6)
  } catch (error) {
    console.error('加载推荐失败:', error)
    ElMessage.error('加载推荐失败')
  } finally {
    loading.recommend = false
  }
}

// 加载学习分析报告
const loadAnalysisReport = async () => {
  loading.analysis = true
  try {
    report.value = await generateLearningReport()
    console.log('✅ AI学习分析报告数据:', report.value)
  } catch (error) {
    console.error('加载分析报告失败:', error)
    ElMessage.error('加载分析报告失败')
  } finally {
    loading.analysis = false
  }
}

// 生成学习路径
const generatePath = async () => {
  if (!pathForm.goal.trim()) {
    ElMessage.warning('请输入学习目标')
    return
  }
  
  loading.path = true
  try {
    learningPath.value = await generateLearningPath(pathForm.goal)
    ElMessage.success('学习路径生成成功')
  } catch (error) {
    console.error('生成学习路径失败:', error)
    ElMessage.error('生成学习路径失败')
  } finally {
    loading.path = false
  }
}

// 跳转到课程详情
const goToCourse = (courseId: number) => {
  router.push(`/course/${courseId}`)
}

// 打开聊天（这里假设有一个全局的AI助手组件）
const openChat = () => {
  // 通过事件或全局状态打开AI助手
  ElMessage.info('请点击右下角的AI助手按钮开始对话')
}

// 处理图片加载错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = '/images/default-course.jpg'
}

// 格式化AI建议文本，使其更易读
const formatAdviceText = (text: string): string => {
  if (!text) return ''
  
  return text
    // 替换【】为加粗标题
    .replace(/【([^】]+)】/g, '<div class="section-title">$1</div>')
    // 识别并格式化列表项（以"- "或"• "开头）
    .replace(/^[-•]\s+(.+)$/gm, '<div class="list-item">• $1</div>')
    // 识别并格式化数字标题（如"第一阶段："）
    .replace(/^(第[一二三四五六七八九十]+阶段[：:].+)$/gm, '<div class="stage-title">$1</div>')
    // 识别键值对（如"阶段描述: xxx"）
    .replace(/^([^：:]+[：:])(.+)$/gm, '<div class="key-value"><span class="key">$1</span><span class="value">$2</span></div>')
    // 添加段落间距
    .replace(/\n\n/g, '<div class="paragraph-break"></div>')
    // 普通换行
    .replace(/\n/g, '<br>')
}

// 页面加载
onMounted(() => {
  loadRecommendations()
  loadAnalysisReport()
})
</script>

<style scoped lang="scss">
.ai-dashboard-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #fff 100%);
}

.page-content {
  padding: 30px 0 60px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 15px;
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 30px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .header-text {
      .page-title {
        margin: 0 0 5px;
        font-size: 28px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
      
      .page-subtitle {
        margin: 0;
        color: #909399;
      }
    }
  }
}

// 功能卡片
.feature-cards {
  margin-bottom: 30px;
  
  .feature-card {
    padding: 30px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;
    margin-bottom: 20px;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }
    
    .card-icon {
      width: 80px;
      height: 80px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-bottom: 20px;
    }
    
    h3 {
      margin: 0 0 10px;
      font-size: 20px;
    }
    
    p {
      margin: 0;
      color: #909399;
      line-height: 1.6;
    }
  }
}

// Tab内容
.ai-tabs {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  
  :deep(.el-tabs__header) {
    margin-bottom: 30px;
  }
}

.tab-content {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 20px;
    }
  }
}

// 推荐卡片
.recommend-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  margin-bottom: 20px;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
  }
  
  .course-cover {
    position: relative;
    height: 200px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .match-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      padding: 5px 10px;
      background: rgba(103, 194, 58, 0.9);
      color: #fff;
      border-radius: 4px;
      font-size: 12px;
    }
  }
  
  .course-info {
    padding: 15px;
    
    h4 {
      margin: 0 0 10px;
      font-size: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .course-meta {
      margin-bottom: 10px;
      display: flex;
      gap: 8px;
    }
    
    .reason {
      margin: 10px 0;
      color: #606266;
      font-size: 14px;
      display: flex;
      align-items: flex-start;
      gap: 5px;
      line-height: 1.6;
    }
  }
}

// 统计卡片
.stats-cards {
  margin-bottom: 20px;
  
  .stat-card {
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    gap: 15px;
    
    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    .stat-info {
      flex: 1;
      
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 5px;
      }
      
      .stat-label {
        color: #909399;
        font-size: 14px;
      }
    }
  }
}

// 优势与不足
.strength-weakness {
  margin-bottom: 20px;
  
  .info-card {
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    
    h4 {
      margin: 0 0 15px;
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
    }
    
    ul {
      margin: 0;
      padding-left: 20px;
      
      li {
        margin-bottom: 8px;
        line-height: 1.6;
      }
    }
    
    .empty-tip {
      color: #909399;
      margin: 0;
    }
  }
}

// 建议卡片
.suggestions-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  h4 {
    margin: 0 0 15px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
  }
  
  .suggestions-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .empty-tip {
    color: #909399;
    margin: 0;
  }
}

// 学习路径
.path-input-section {
  margin-bottom: 30px;
  
  .quick-goals {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
    
    .label {
      color: #606266;
      font-size: 14px;
    }
    
    .goal-tag {
      cursor: pointer;
      
      &:hover {
        background: #409eff;
        color: #fff;
        border-color: #409eff;
      }
    }
  }
}

.path-result {
  .path-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      font-size: 20px;
    }
  }
  
  .ai-full-advice {
    margin: 20px 0;
    padding: 20px;
    background: #f8f9fa;
    border: 1px solid #e1e8ed;
    border-radius: 8px;
    
    .advice-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 15px;
      padding-bottom: 12px;
      border-bottom: 2px solid #409eff;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      
      .el-icon {
        font-size: 20px;
        color: #409eff;
      }
    }
    
    .advice-content {
      background: #fff;
      padding: 20px;
      border-radius: 6px;
      line-height: 1.8;
      color: #303133;
      font-size: 14px;
      
      :deep(.section-title) {
        font-size: 15px;
        font-weight: 700;
        color: #fff;
        margin: 16px 0 10px 0;
        padding: 8px 12px;
        background: #409eff;
        border-radius: 4px;
        
        &:first-child {
          margin-top: 0;
        }
      }
      
      :deep(.stage-title) {
        font-size: 14px;
        font-weight: 600;
        color: #409eff;
        margin: 14px 0 8px 0;
        padding-left: 10px;
        border-left: 3px solid #409eff;
      }
      
      :deep(.key-value) {
        margin: 6px 0;
        padding: 4px 0;
        
        .key {
          font-weight: 600;
          color: #303133;
          margin-right: 6px;
        }
        
        .value {
          color: #606266;
        }
      }
      
      :deep(.list-item) {
        margin: 5px 0;
        padding-left: 20px;
        color: #606266;
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          left: 8px;
          top: 10px;
          width: 4px;
          height: 4px;
          background: #409eff;
          border-radius: 50%;
        }
      }
      
      :deep(.paragraph-break) {
        height: 10px;
      }
    }
  }
  
  .stages-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 25px 0 15px 0;
    padding: 10px 15px;
    background: #409eff;
    color: #fff;
    border-radius: 6px;
    font-size: 16px;
    font-weight: 600;
    
    .el-icon {
      font-size: 18px;
    }
  }
  
  .path-timeline {
    padding: 15px 0;
    
    .stage-card {
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
      }
    }
    
    .stage-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      padding-bottom: 12px;
      border-bottom: 2px solid #f0f0f0;
      
      h5 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
      
      .stage-meta {
        display: flex;
        gap: 6px;
        
        .el-tag {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
    
    .stage-desc {
      margin: 0 0 15px;
      padding: 10px;
      background: #f8f9fa;
      border-radius: 6px;
      color: #606266;
      line-height: 1.6;
      font-size: 14px;
    }
    
    .key-points {
      margin: 15px 0;
      padding: 12px;
      background: #fff9e6;
      border-left: 3px solid #e6a23c;
      border-radius: 4px;
      
      h6 {
        margin: 0 0 8px;
        font-size: 14px;
        color: #e6a23c;
        display: flex;
        align-items: center;
        gap: 5px;
      }
      
      ul {
        margin: 0;
        padding-left: 20px;
        
        li {
          margin-bottom: 6px;
          line-height: 1.6;
          color: #606266;
          font-size: 14px;
          
          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }
    
    .courses-section {
      margin-top: 15px;
      
      h6 {
        margin: 0 0 12px;
        font-size: 14px;
        color: #409eff;
        display: flex;
        align-items: center;
        gap: 5px;
      }
    }
    
    .stage-courses {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .mini-course-card {
        display: flex;
        gap: 10px;
        padding: 8px;
        border: 1px solid #e4e7ed;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          border-color: #409eff;
          background: #ecf5ff;
        }
        
        // 虚拟课程样式（纯文字，不可点击）
        &.virtual-course {
          cursor: default;
          background: #f5f7fa;
          
          &:hover {
            border-color: #e4e7ed;
            background: #f5f7fa;
          }
          
          .course-icon {
            width: 70px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #e4e7ed;
            border-radius: 4px;
            color: #909399;
          }
        }
        
        img {
          width: 70px;
          height: 50px;
          object-fit: cover;
          border-radius: 4px;
        }
        
        .mini-course-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          
          .course-title {
            font-size: 13px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: #303133;
            font-weight: 500;
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .feature-cards {
    .el-col {
      margin-bottom: 0;
    }
  }
}
</style>