<template>
  <CommonPage>
    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-16 mb-24">
      <n-card v-for="stat in stats" :key="stat.title" class="stat-card" hoverable>
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-400 text-14 mb-8">{{ stat.title }}</p>
            <p class="text-28 font-bold" :style="{ color: stat.color }">{{ stat.value }}</p>
            <p class="text-12 mt-4">
              <span :class="stat.trend > 0 ? 'text-green-500' : 'text-red-500'">
                {{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%
              </span>
              <span class="text-gray-400 ml-4">较昨日</span>
            </p>
          </div>
          <div class="w-48 h-48 rounded-full flex items-center justify-center" :style="{ backgroundColor: stat.bgColor }">
            <i :class="`${stat.icon} text-24`" :style="{ color: stat.color }" />
          </div>
        </div>
      </n-card>
    </div>

    <!-- 图表区域 -->
    <div class="grid grid-cols-2 gap-16 mb-24">
      <!-- 播放量趋势 -->
      <n-card title="播放量趋势 (近7天)">
        <div class="h-280">
          <div class="flex items-end justify-between h-full pt-20">
            <div 
              v-for="(item, index) in viewTrend" 
              :key="index"
              class="flex flex-col items-center flex-1"
            >
              <div 
                class="w-32 rounded-t-4 transition-all duration-300 hover:opacity-80"
                :style="{ 
                  height: `${(item.value / maxViewValue) * 200}px`,
                  backgroundColor: '#316C72'
                }"
              />
              <p class="text-12 text-gray-400 mt-8">{{ item.day }}</p>
            </div>
          </div>
        </div>
      </n-card>

      <!-- 分类占比 -->
      <n-card title="视频分类占比">
        <div class="h-280 flex items-center justify-center">
          <div class="flex items-center gap-32">
            <!-- 模拟饼图 -->
            <div class="relative w-160 h-160">
              <div class="pie-chart" />
            </div>
            <!-- 图例 -->
            <div class="space-y-12">
              <div v-for="cat in categories" :key="cat.name" class="flex items-center gap-8">
                <span class="w-12 h-12 rounded-full" :style="{ backgroundColor: cat.color }" />
                <span class="text-14">{{ cat.name }}</span>
                <span class="text-gray-400 text-12 ml-8">{{ cat.percent }}%</span>
              </div>
            </div>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 热门视频和最新动态 -->
    <div class="grid grid-cols-2 gap-16">
      <!-- 热门视频 -->
      <n-card title="热门视频 TOP 5">
        <n-table :bordered="false" :single-line="false" size="small">
          <thead>
            <tr>
              <th>排名</th>
              <th>视频标题</th>
              <th>播放量</th>
              <th>点赞</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(video, index) in hotVideos" :key="video.id">
              <td>
                <n-tag :type="index < 3 ? 'warning' : 'default'" size="small">
                  {{ index + 1 }}
                </n-tag>
              </td>
              <td class="max-w-200 truncate">{{ video.title }}</td>
              <td>{{ formatNumber(video.views) }}</td>
              <td>{{ formatNumber(video.likes) }}</td>
            </tr>
          </tbody>
        </n-table>
      </n-card>

      <!-- 最新动态 -->
      <n-card title="最新动态">
        <n-timeline>
          <n-timeline-item
            v-for="(item, index) in recentActivities"
            :key="index"
            :type="item.type"
            :title="item.title"
            :content="item.content"
            :time="item.time"
          />
        </n-timeline>
      </n-card>
    </div>
  </CommonPage>
</template>

<script setup>
import { NCard, NTable, NTag, NTimeline, NTimelineItem } from 'naive-ui'

defineOptions({ name: 'Dashboard' })

// 统计卡片数据（假数据）
const stats = ref([
  {
    title: '总视频数',
    value: '1,286',
    trend: 12.5,
    icon: 'i-fe:video',
    color: '#316C72',
    bgColor: 'rgba(49, 108, 114, 0.1)',
  },
  {
    title: '总播放量',
    value: '892.3K',
    trend: 8.2,
    icon: 'i-fe:play-circle',
    color: '#67C23A',
    bgColor: 'rgba(103, 194, 58, 0.1)',
  },
  {
    title: '注册用户',
    value: '15,847',
    trend: -2.4,
    icon: 'i-fe:users',
    color: '#E6A23C',
    bgColor: 'rgba(230, 162, 60, 0.1)',
  },
  {
    title: '今日收益',
    value: '¥3,286',
    trend: 18.6,
    icon: 'i-fe:dollar-sign',
    color: '#F56C6C',
    bgColor: 'rgba(245, 108, 108, 0.1)',
  },
])

// 播放量趋势（假数据）
const viewTrend = ref([
  { day: '周一', value: 12000 },
  { day: '周二', value: 18000 },
  { day: '周三', value: 15000 },
  { day: '周四', value: 22000 },
  { day: '周五', value: 28000 },
  { day: '周六', value: 35000 },
  { day: '周日', value: 32000 },
])

const maxViewValue = computed(() => Math.max(...viewTrend.value.map(i => i.value)))

// 分类占比（假数据）
const categories = ref([
  { name: '电影', percent: 35, color: '#316C72' },
  { name: '电视剧', percent: 25, color: '#67C23A' },
  { name: '动漫', percent: 20, color: '#E6A23C' },
  { name: '综艺', percent: 12, color: '#F56C6C' },
  { name: '其他', percent: 8, color: '#909399' },
])

// 热门视频（假数据）
const hotVideos = ref([
  { id: 1, title: '【4K修复】经典老电影合集', views: 128000, likes: 8900 },
  { id: 2, title: '2024年度热门番剧盘点', views: 95000, likes: 6700 },
  { id: 3, title: '最新韩剧推荐TOP10', views: 82000, likes: 5400 },
  { id: 4, title: '周星驰经典喜剧全集', views: 76000, likes: 4800 },
  { id: 5, title: '纪录片：地球脉动', views: 68000, likes: 4200 },
])

// 最新动态（假数据）
const recentActivities = ref([
  { type: 'success', title: '新视频上传', content: '用户 test123 上传了视频《春节特辑》', time: '2分钟前' },
  { type: 'info', title: '用户注册', content: '新用户 movie_lover 完成注册', time: '15分钟前' },
  { type: 'warning', title: '视频审核', content: '视频《游戏实况》需要审核', time: '30分钟前' },
  { type: 'error', title: '举报处理', content: '用户举报视频ID #1234 侵权', time: '1小时前' },
  { type: 'success', title: '收益到账', content: '今日广告收益 ¥286.50 已到账', time: '2小时前' },
])

// 数字格式化
function formatNumber(num) {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}
</script>

<style scoped>
.stat-card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-4px);
}

.pie-chart {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: conic-gradient(
    #316C72 0% 35%,
    #67C23A 35% 60%,
    #E6A23C 60% 80%,
    #F56C6C 80% 92%,
    #909399 92% 100%
  );
  position: relative;
}
.pie-chart::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: var(--n-color);
  border-radius: 50%;
}
</style>
