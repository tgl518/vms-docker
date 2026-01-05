<template>
  <AppPage>
    <!-- 欢迎区 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <n-avatar round :size="64" :src="userStore.avatar" class="avatar" />
        <div class="welcome-info">
          <h2>Hi, {{ userStore.nickName ?? userStore.username }} 👋</h2>
          <p>欢迎回到 VMS 视频管理系统</p>
        </div>
        <div class="time-info">
          <span class="date">{{ currentDate }}</span>
          <span class="time">{{ currentTime }}</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.title">
        <div class="stat-icon" :style="{ background: stat.gradient }">
          <component :is="stat.icon" />
        </div>
        <div class="stat-content">
          <p class="stat-title">{{ stat.title }}</p>
          <n-number-animation
            ref="numberAnimationInstRef"
            :from="0"
            :to="stat.value"
            :active="true"
            :duration="1500"
          />
          <p class="stat-trend" v-if="stat.trend">
            <span :class="stat.trend > 0 ? 'up' : 'down'">
              {{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%
            </span>
            较昨日
          </p>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="charts-row">
      <n-card class="chart-card" title="📈 7天播放趋势">
        <div class="chart-container">
          <VChart :option="trendOption" autoresize />
        </div>
      </n-card>

      <n-card class="chart-card" title="🎬 视频分类占比">
        <div class="chart-container">
          <VChart :option="categoryOption" autoresize />
        </div>
      </n-card>
    </div>

    <!-- 快捷操作 -->
    <n-card class="quick-actions" title="⚡ 快捷操作">
      <div class="actions-grid">
        <router-link to="/admin/vms/video" class="action-item">
          <div class="action-icon video">📹</div>
          <span>视频管理</span>
        </router-link>
        <router-link to="/admin/pms/user" class="action-item">
          <div class="action-icon user">👥</div>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/vms/banner" class="action-item">
          <div class="action-icon banner">🖼️</div>
          <span>轮播图管理</span>
        </router-link>
        <router-link to="/admin/vms/category" class="action-item">
          <div class="action-icon category">📁</div>
          <span>分类管理</span>
        </router-link>
      </div>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref, onMounted, onUnmounted, h } from 'vue'
import { NNumberAnimation } from 'naive-ui'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import { useUserStore } from '@/store'
import statisticsApi from '@/api/statistics'

const userStore = useUserStore()

echarts.use([
  TooltipComponent,
  GridComponent,
  LegendComponent,
  LineChart,
  PieChart,
  CanvasRenderer,
])

// 时间显示
const currentDate = ref('')
const currentTime = ref('')
let timer = null

function updateTime() {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', { weekday: 'long', month: 'long', day: 'numeric' })
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 60000)
  loadStats()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

// 统计数据
const stats = ref([
  { title: '视频总数', value: 0, icon: '📹', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', trend: 12 },
  { title: '用户总数', value: 0, icon: '👥', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', trend: 8 },
  { title: '今日播放', value: 0, icon: '▶️', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', trend: 25 },
  { title: '今日新增用户', value: 0, icon: '🆕', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', trend: -3 },
])

async function loadStats() {
  try {
    const { data } = await statisticsApi.getDashboard()
    if (data) {
      stats.value[0].value = data.videoCount || 0
      stats.value[1].value = data.userCount || 0
      stats.value[2].value = data.todayViews || 0
      stats.value[3].value = data.todayNewUsers || 0
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 播放趋势图表
const trendOption = {
  tooltip: {
    trigger: 'axis',
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisLine: { lineStyle: { color: '#666' } },
    axisLabel: { color: '#999' },
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: '#666' } },
    axisLabel: { color: '#999' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
  },
  series: [
    {
      name: '播放量',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(251, 114, 153, 0.5)' },
          { offset: 1, color: 'rgba(251, 114, 153, 0.05)' },
        ]),
      },
      lineStyle: { color: '#FB7299', width: 3 },
      itemStyle: { color: '#FB7299' },
      data: [820, 932, 901, 1234, 1290, 1530, 1820],
    },
  ],
}

// 分类占比图表
const categoryOption = {
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)',
  },
  legend: {
    orient: 'vertical',
    right: '5%',
    top: 'center',
    textStyle: { color: '#999' },
  },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#1f2125',
        borderWidth: 2,
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold',
          color: '#fff',
        },
      },
      data: [
        { value: 35, name: '影视', itemStyle: { color: '#FB7299' } },
        { value: 28, name: '音乐', itemStyle: { color: '#667eea' } },
        { value: 18, name: '游戏', itemStyle: { color: '#4facfe' } },
        { value: 12, name: '科技', itemStyle: { color: '#43e97b' } },
        { value: 7, name: '其他', itemStyle: { color: '#f5576c' } },
      ],
    },
  ],
}
</script>

<style scoped>
/* 欢迎区 */
.welcome-section {
  margin-bottom: 24px;
}

.welcome-card {
  display: flex;
  align-items: center;
  padding: 24px 32px;
  background: linear-gradient(135deg, rgba(251, 114, 153, 0.15) 0%, rgba(102, 126, 234, 0.15) 100%);
  border-radius: 16px;
  border: 1px solid rgba(251, 114, 153, 0.2);
}

.avatar {
  flex-shrink: 0;
  border: 3px solid rgba(251, 114, 153, 0.5);
}

.welcome-info {
  margin-left: 20px;
  flex: 1;
}

.welcome-info h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #f1f2f3;
}

.welcome-info p {
  margin: 8px 0 0;
  color: #9499a0;
  font-size: 14px;
}

.time-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  color: #9499a0;
}

.time-info .date {
  font-size: 14px;
}

.time-info .time {
  font-size: 28px;
  font-weight: 600;
  color: #FB7299;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, rgba(31, 33, 37, 0.8) 0%, rgba(22, 24, 29, 0.8) 100%);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  border-color: rgba(251, 114, 153, 0.3);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-content {
  margin-left: 16px;
}

.stat-title {
  margin: 0;
  font-size: 14px;
  color: #9499a0;
}

.stat-content :deep(.n-number-animation) {
  font-size: 28px;
  font-weight: 700;
  color: #f1f2f3;
  line-height: 1.3;
}

.stat-trend {
  margin: 4px 0 0;
  font-size: 12px;
  color: #9499a0;
}

.stat-trend .up {
  color: #43e97b;
}

.stat-trend .down {
  color: #f5576c;
}

/* 图表区 */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: linear-gradient(135deg, rgba(31, 33, 37, 0.8) 0%, rgba(22, 24, 29, 0.8) 100%);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.chart-card :deep(.n-card-header) {
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 600;
}

.chart-container {
  height: 280px;
  padding: 0 16px 16px;
}

/* 快捷操作 */
.quick-actions {
  background: linear-gradient(135deg, rgba(31, 33, 37, 0.8) 0%, rgba(22, 24, 29, 0.8) 100%);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.quick-actions :deep(.n-card-header) {
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 600;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 0 4px 8px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.action-item:hover {
  background: rgba(251, 114, 153, 0.1);
  transform: translateY(-4px);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.action-item span {
  color: #9499a0;
  font-size: 14px;
  transition: color 0.3s;
}

.action-item:hover span {
  color: #FB7299;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
