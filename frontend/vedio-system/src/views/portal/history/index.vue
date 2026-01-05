<template>
  <div class="history-page">
    <!-- 顶部占位 -->
    <div class="header-spacing"></div>

    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>观看历史</h1>
        <n-button 
          v-if="historyList.length" 
          quaternary 
          type="error"
          @click="handleClearAll"
        >
          <template #icon>
            <n-icon><DeleteIcon /></n-icon>
          </template>
          清空全部
        </n-button>
      </div>

      <!-- 历史列表 -->
      <div class="history-list" v-if="historyList.length">
        <div 
          v-for="item in historyList" 
          :key="item.id" 
          class="history-item"
        >
          <!-- 封面 -->
          <div class="item-cover" @click="goToVideo(item)">
            <img :src="item.coverUrl || defaultCover" :alt="item.title" />
            <!-- 播放进度条 -->
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getProgress(item) + '%' }"></div>
            </div>
            <!-- 时长/进度标签 -->
            <div class="duration-tag">
              {{ formatDuration(item.watchDuration) }} / {{ formatDuration(item.totalDuration || item.watchDuration) }}
            </div>
          </div>

          <!-- 信息 -->
          <div class="item-info">
            <h3 class="item-title" @click="goToVideo(item)">{{ item.title || '视频' + item.videoId }}</h3>
            <p class="item-meta">
              <span class="watch-time">{{ formatTime(item.lastWatchTime) }}</span>
            </p>
            <div class="item-actions">
              <n-button size="tiny" type="primary" @click="continueWatch(item)">
                继续播放
              </n-button>
              <n-button size="tiny" quaternary @click="removeItem(item)">
                删除
              </n-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="!loading">
        <div class="empty-icon">📺</div>
        <h3>暂无观看历史</h3>
        <p>您还没有观看过任何视频</p>
        <n-button type="primary" @click="$router.push('/')">去首页看看</n-button>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore && historyList.length">
        <n-button quaternary :loading="loading" @click="loadMore">加载更多</n-button>
      </div>

      <!-- 加载中 -->
      <div class="loading-state" v-if="loading && !historyList.length">
        <n-spin size="large" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NIcon, NSpin } from 'naive-ui'
import historyApi from '@/api/history'

const DeleteIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z' })]) }

const router = useRouter()

const defaultCover = 'https://via.placeholder.com/320x180?text=No+Cover'

const historyList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const pageNo = ref(1)
const pageSize = 20

onMounted(() => {
  loadHistory()
})

async function loadHistory() {
  if (loading.value) return
  loading.value = true

  try {
    const { data } = await historyApi.getList({
      pageNo: pageNo.value,
      pageSize
    })

    const list = data?.pageData || []
    
    if (pageNo.value === 1) {
      historyList.value = list
    } else {
      historyList.value.push(...list)
    }

    hasMore.value = list.length >= pageSize
  } catch (err) {
    console.error('加载历史失败', err)
    if (err.code === 401) {
      window.$message?.warning('请先登录')
    }
  } finally {
    loading.value = false
  }
}

function loadMore() {
  pageNo.value++
  loadHistory()
}

function goToVideo(item) {
  router.push(`/video/${item.videoId}`)
}

function continueWatch(item) {
  // 携带进度参数跳转
  router.push({
    path: `/video/${item.videoId}`,
    query: { t: item.watchDuration }
  })
}

async function removeItem(item) {
  try {
    await historyApi.delete(item.id)
    const idx = historyList.value.indexOf(item)
    if (idx > -1) historyList.value.splice(idx, 1)
    window.$message?.success('已删除')
  } catch (err) {
    window.$message?.error('删除失败')
  }
}

function handleClearAll() {
  window.$dialog?.warning({
    title: '清空观看历史',
    content: '确定要清空所有观看历史吗？此操作不可恢复。',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await historyApi.clear()
        historyList.value = []
        window.$message?.success('已清空')
      } catch (err) {
        window.$message?.error('操作失败')
      }
    }
  })
}

function getProgress(item) {
  if (!item.totalDuration || item.totalDuration === 0) return 100
  return Math.min(100, (item.watchDuration / item.totalDuration) * 100)
}

function formatDuration(seconds) {
  if (!seconds) return '00:00'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = (now - date) / 1000

  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 2592000) return Math.floor(diff / 86400) + '天前'

  return date.toLocaleDateString()
}
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  /* 背景色继承自父容器 portal-layout */
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.header-spacing {
  height: 64px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

/* 历史列表 */
.history-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.history-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #1f2125;
  border-radius: 12px;
  transition: all 0.3s;
}

.history-item:hover {
  background: #25272d;
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.item-cover {
  position: relative;
  width: 160px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.item-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.item-cover:hover img {
  transform: scale(1.05);
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: rgba(0, 0, 0, 0.6);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FB7299, #f05b7a);
  border-radius: 0 2px 2px 0;
}

.duration-tag {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.item-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin: 0 0 8px;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.item-title:hover {
  color: #FB7299;
}

.item-meta {
  font-size: 12px;
  color: #999;
  margin: 0 0 auto;
}

.item-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

/* 空状态 */
.empty-state {
  padding: 80px 0;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #fff;
  margin: 0 0 8px;
}

.empty-state p {
  color: #666;
  margin-bottom: 24px;
}

/* 加载 */
.load-more {
  text-align: center;
  margin-top: 32px;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

@media (max-width: 768px) {
  .history-list {
    grid-template-columns: 1fr;
  }

  .history-item {
    flex-direction: column;
  }

  .item-cover {
    width: 100%;
    height: 180px;
  }
}

/* 亮色模式 */
:global(.portal-layout.light-mode) .history-page {
  background: #f4f5f7;
}

:global(.portal-layout.light-mode) .page-header h1 {
  color: #18191c;
}

:global(.portal-layout.light-mode) .history-item {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

:global(.portal-layout.light-mode) .history-item:hover {
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:global(.portal-layout.light-mode) .item-title {
  color: #18191c;
}

:global(.portal-layout.light-mode) .empty-state h3 {
  color: #18191c;
}
</style>
