<template>
  <div class="ai-analytics">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1><el-icon><TrendCharts /></el-icon> AI学习分析仪表板</h1>
      <p class="subtitle">基于AI分析的个性化学习数据洞察</p>
    </div>

    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-overview">
      <el-col :xs="12" :sm="6" v-for="stat in statsData" :key="stat.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: stat.color }">
            <component :is="stat.icon" />
          </div>
          <div class="stat-content">
            <h3>{{ stat.value }}</h3>
            <p>{{ stat.label }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 可视化图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <!-- 学习能力雷达图 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><Odometer /></el-icon> 学习能力分析</h3>
              <el-tag type="success">AI评估</el-tag>
            </div>
          </template>
          <div ref="radarChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 学习趋势图 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><TrendCharts /></el-icon> 学习趋势分析</h3>
              <el-tag type="primary">最近30天</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 学习热力图 -->
      <el-col :xs="24">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><Calendar /></el-icon> 学习活跃度热力图</h3>
              <el-tag type="warning">近90天</el-tag>
            </div>
          </template>
          <div ref="heatmapChartRef" class="chart-container-large"></div>
        </el-card>
      </el-col>

      <!-- 课程完成度 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><PieChart /></el-icon> 课程完成度</h3>
              <el-tag type="info">全部课程</el-tag>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- AI洞察建议 -->
      <el-col :xs="24" :md="12">
        <el-card class="insight-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3><el-icon><MagicStick /></el-icon> AI学习洞察</h3>
              <el-tag type="danger">智能推荐</el-tag>
            </div>
          </template>
          <div class="insights-list">
            <div class="insight-item" v-for="(insight, index) in aiInsights" :key="index">
              <div class="insight-icon">
                <el-icon><component :is="insight.icon" /></el-icon>
              </div>
              <div class="insight-content">
                <h4>{{ insight.title }}</h4>
                <p>{{ insight.description }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { ElMessage } from 'element-plus'
import { generateLearningReport } from '@/api/ai'
import type { LearningReport } from '@/types/ai'
import {
  TrendCharts,
  Odometer,
  Calendar,
  PieChart,
  MagicStick,
  Timer,
  Reading,
  Medal,
  TrophyBase,
  StarFilled,
  Warning,
  SuccessFilled
} from '@element-plus/icons-vue'

// 统计数据
const statsData = ref([
  {
    label: '累计学习时长',
    value: '0h',
    icon: Timer,
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    label: '完成课程',
    value: '0',
    icon: Reading,
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    label: '学习天数',
    value: '0',
    icon: Calendar,
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    label: '获得成就',
    value: '0',
    icon: Medal,
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 加载真实数据（带缓存）
const loadLearningData = async () => {
  try {
    const { storage } = await import('@/utils/storage')
    const cacheKey = 'learning_report'
    
    // 检查缓存（10分钟有效）
    let report: LearningReport | null = storage.getCache<LearningReport>(cacheKey)
    let fromCache = false
    
    if (!report) {
      // 无缓存，从服务器获取
      report = await generateLearningReport()
      // 存入缓存（10分钟有效）
      storage.setCache(cacheKey, report, 10 * 60 * 1000)
    } else {
      fromCache = true
    }
    
    // 更新统计数据
    statsData.value[0].value = `${Math.round(report.totalStudyTime / 60)}h`
    statsData.value[1].value = String(report.completedCourses || 0)
    statsData.value[2].value = String(report.totalCourses || 0)
    statsData.value[3].value = '0' // 成就系统待实现
    
    // 更新AI洞察
    if (report.strongPoints && report.strongPoints.length > 0) {
      aiInsights.value[1].description = report.strongPoints[0]
    }
    if (report.weakPoints && report.weakPoints.length > 0) {
      aiInsights.value[2].description = report.weakPoints[0]
    }
    if (report.suggestions && report.suggestions.length > 0) {
      aiInsights.value[3].description = report.suggestions[0]
    }
    
    if (fromCache) {
      ElMessage.success('💾 学习数据加载成功（来自缓存）')
    } else {
      ElMessage.success('✅ 学习数据加载成功')
    }
  } catch (error) {
    console.error('加载学习数据失败:', error)
    ElMessage.warning('部分数据加载失败，显示为默认值')
  }
}

// AI洞察数据
const aiInsights = ref([
  {
    icon: TrophyBase,
    title: '学习状态良好',
    description: '最近7天保持稳定学习，建议继续保持当前节奏。'
  },
  {
    icon: StarFilled,
    title: '数据结构掌握优秀',
    description: '在算法与数据结构方面表现突出，可以尝试更高难度的实战项目。'
  },
  {
    icon: Warning,
    title: '前端框架需加强',
    description: 'Vue.js相关课程进度较慢，建议增加学习时间或调整学习方法。'
  },
  {
    icon: SuccessFilled,
    title: '推荐学习路径',
    description: '基于您的学习轨迹，AI推荐：微服务架构 → Docker容器化 → Kubernetes'
  }
])

// 图表引用
const radarChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const heatmapChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()

let radarChart: ECharts | null = null
let trendChart: ECharts | null = null
let heatmapChart: ECharts | null = null
let pieChart: ECharts | null = null

// 初始化雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  
  radarChart = echarts.init(radarChartRef.value)
  
  const option = {
    tooltip: {},
    radar: {
      indicator: [
        { name: '编程能力', max: 100 },
        { name: '理论基础', max: 100 },
        { name: '实践经验', max: 100 },
        { name: '问题解决', max: 100 },
        { name: '创新思维', max: 100 },
        { name: '团队协作', max: 100 }
      ],
      radius: '65%'
    },
    series: [
      {
        name: '学习能力',
        type: 'radar',
        data: [
          {
            value: [85, 75, 80, 78, 70, 82],
            name: '当前能力',
            areaStyle: {
              color: new echarts.graphic.RadialGradient(0.1, 0.6, 1, [
                { color: 'rgba(102, 126, 234, 0.1)', offset: 0 },
                { color: 'rgba(102, 126, 234, 0.9)', offset: 1 }
              ])
            },
            lineStyle: {
              color: '#667eea',
              width: 2
            },
            itemStyle: {
              color: '#667eea'
            }
          }
        ]
      }
    ]
  }
  
  radarChart.setOption(option)
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  
  // 生成最近30天的数据
  const dates = []
  const values = []
  for (let i = 29; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    values.push(Math.floor(Math.random() * 4 + 2)) // 2-6小时随机
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>学习时长: {c}小时'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        interval: 4
      }
    },
    yAxis: {
      type: 'value',
      name: '学习时长(h)',
      min: 0
    },
    series: [
      {
        data: values,
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#667eea',
          width: 3
        },
        itemStyle: {
          color: '#667eea'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 初始化热力图
const initHeatmapChart = () => {
  if (!heatmapChartRef.value) return
  
  heatmapChart = echarts.init(heatmapChartRef.value)
  
  // 生成热力图数据
  const data: [string, number, number][] = []
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 90)
  
  for (let i = 0; i < 90; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + i)
    const dateStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    const value = Math.floor(Math.random() * 6)
    data.push([dateStr, date.getDay(), value])
  }
  
  const option = {
    tooltip: {
      formatter: (params: any) => {
        return `${params.data[0]}<br/>学习${params.data[2]}小时`
      }
    },
    visualMap: {
      min: 0,
      max: 5,
      type: 'piecewise',
      orient: 'horizontal',
      left: 'center',
      bottom: 0,
      pieces: [
        { min: 0, max: 0, label: '无', color: '#ebedf0' },
        { min: 1, max: 1, label: '1h', color: '#c6e48b' },
        { min: 2, max: 2, label: '2h', color: '#7bc96f' },
        { min: 3, max: 3, label: '3h', color: '#239a3b' },
        { min: 4, max: 5, label: '4h+', color: '#196127' }
      ]
    },
    calendar: {
      top: 50,
      left: 50,
      right: 50,
      cellSize: ['auto', 15],
      range: [data[0][0], data[data.length - 1][0]],
      itemStyle: {
        borderWidth: 3,
        borderColor: '#fff'
      },
      yearLabel: { show: false },
      dayLabel: {
        nameMap: ['日', '一', '二', '三', '四', '五', '六']
      },
      monthLabel: {
        nameMap: 'cn'
      }
    },
    series: [
      {
        type: 'heatmap',
        coordinateSystem: 'calendar',
        data: data
      }
    ]
  }
  
  heatmapChart.setOption(option)
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}门 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '课程状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}\n{d}%'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 12, name: '已完成', itemStyle: { color: '#43e97b' } },
          { value: 5, name: '进行中', itemStyle: { color: '#667eea' } },
          { value: 3, name: '未开始', itemStyle: { color: '#e0e0e0' } }
        ]
      }
    ]
  }
  
  pieChart.setOption(option)
}

// 响应式处理
const handleResize = () => {
  radarChart?.resize()
  trendChart?.resize()
  heatmapChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  // 加载真实学习数据
  loadLearningData()
  
  // 初始化图表
  initRadarChart()
  initTrendChart()
  initHeatmapChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  radarChart?.dispose()
  trendChart?.dispose()
  heatmapChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.ai-analytics {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    text-align: center;
    margin-bottom: 32px;

    h1 {
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      margin-bottom: 8px;

      .el-icon {
        font-size: 36px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }

    .subtitle {
      font-size: 16px;
      color: #909399;
    }
  }

  .stats-overview {
    margin-bottom: 24px;

    .stat-card {
      border-radius: 12px;
      border: none;

      :deep(.el-card__body) {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
      }

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
        flex-shrink: 0;
      }

      .stat-content {
        flex: 1;

        h3 {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          margin: 0 0 4px 0;
        }

        p {
          font-size: 14px;
          color: #909399;
          margin: 0;
        }
      }
    }
  }

  .charts-section {
    .chart-card,
    .insight-card {
      border-radius: 12px;
      border: none;
      margin-bottom: 20px;

      .card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;

        h3 {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }

      .chart-container {
        height: 350px;
        width: 100%;
      }

      .chart-container-large {
        height: 200px;
        width: 100%;
      }
    }

    .insights-list {
      .insight-item {
        display: flex;
        align-items: flex-start;
        gap: 16px;
        padding: 16px;
        border-radius: 8px;
        background: #f5f7fa;
        margin-bottom: 12px;
        transition: all 0.3s;

        &:hover {
          background: #e8eaf0;
          transform: translateX(4px);
        }

        &:last-child {
          margin-bottom: 0;
        }

        .insight-icon {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
          flex-shrink: 0;
        }

        .insight-content {
          flex: 1;

          h4 {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            margin: 0 0 6px 0;
          }

          p {
            font-size: 14px;
            color: #606266;
            margin: 0;
            line-height: 1.6;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .ai-analytics {
    padding: 16px;

    .page-header h1 {
      font-size: 24px;
    }

    .stats-overview .stat-card :deep(.el-card__body) {
      flex-direction: column;
      text-align: center;
    }

    .charts-section {
      .chart-container {
        height: 300px;
      }
    }
  }
}
</style>
