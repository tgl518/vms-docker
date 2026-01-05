<template>
  <div class="favorites-page">
    <!-- 顶部占位 -->
    <div class="header-spacing"></div>

    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>我的收藏</h1>
        <span class="count">共 {{ total }} 个视频</span>
      </div>

      <!-- 收藏列表 -->
      <div class="video-grid" v-if="favorites.length">
        <div 
          v-for="item in favorites" 
          :key="item.id" 
          class="video-card"
        >
          <!-- 封面 -->
          <div class="card-cover" @click="goToVideo(item)">
            <img :src="item.coverUrl || defaultCover" :alt="item.title" />
            <div class="play-overlay">
              <n-icon size="40"><PlayIcon /></n-icon>
            </div>
            <div class="duration-tag" v-if="item.duration">{{ formatDuration(item.duration) }}</div>
          </div>

          <!-- 信息 -->
          <div class="card-info">
            <h3 class="card-title" @click="goToVideo(item)">{{ item.title || '视频' + item.videoId }}</h3>
            <div class="card-meta">
              <span class="stat">
                <n-icon size="14"><ViewIcon /></n-icon>
                {{ formatCount(item.viewCount) }}
              </span>
              <span class="stat">
                <n-icon size="14"><LikeIcon /></n-icon>
                {{ formatCount(item.likeCount) }}
              </span>
            </div>
            <div class="card-footer">
              <span class="collect-time">{{ formatTime(item.createTime) }}收藏</span>
              <n-button 
                size="tiny" 
                quaternary 
                type="error"
                @click="handleUnfavorite(item)"
              >
                取消收藏
              </n-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="!loading">
        <div class="empty-icon">⭐</div>
        <h3>暂无收藏</h3>
        <p>您还没有收藏任何视频，快去探索吧</p>
        <n-button type="primary" @click="$router.push('/')">去首页看看</n-button>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore && favorites.length">
        <n-button quaternary :loading="loading" @click="loadMore">加载更多</n-button>
      </div>

      <!-- 加载中 -->
      <div class="loading-state" v-if="loading && !favorites.length">
        <n-spin size="large" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NIcon, NSpin } from 'naive-ui'
import interactApi from '@/api/interact'

const PlayIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M8 5v14l11-7z' })]) }
const ViewIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z' })]) }
const LikeIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z' })]) }

const router = useRouter()

const defaultCover = 'https://via.placeholder.com/320x180?text=No+Cover'

const favorites = ref([])
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const pageNo = ref(1)
const pageSize = 12

onMounted(() => {
  loadFavorites()
})

async function loadFavorites() {
  if (loading.value) return
  loading.value = true

  try {
    const { data } = await interactApi.getFavorites({
      pageNo: pageNo.value,
      pageSize
    })

    const list = data?.pageData || []
    total.value = data?.total || 0

    if (pageNo.value === 1) {
      favorites.value = list
    } else {
      favorites.value.push(...list)
    }

    hasMore.value = list.length >= pageSize
  } catch (err) {
    console.error('加载收藏失败', err)
    if (err.code === 401) {
      window.$message?.warning('请先登录')
    }
  } finally {
    loading.value = false
  }
}

function loadMore() {
  pageNo.value++
  loadFavorites()
}

function goToVideo(item) {
  router.push(`/video/${item.videoId}`)
}

async function handleUnfavorite(item) {
  try {
    await interactApi.toggleFavorite(item.videoId)
    const idx = favorites.value.indexOf(item)
    if (idx > -1) favorites.value.splice(idx, 1)
    total.value--
    window.$message?.success('已取消收藏')
  } catch (err) {
    window.$message?.error('操作失败')
  }
}

function formatDuration(seconds) {
  if (!seconds) return ''
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

function formatCount(count) {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
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
.favorites-page {
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
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 20px;
}

.page-header {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.count {
  font-size: 14px;
  color: #999;
}

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.video-card {
  background: #1f2125;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.video-card:hover {
  background: #25272d;
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
}

.video-card:hover .play-overlay {
  opacity: 1;
}

.card-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  cursor: pointer;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.video-card:hover .card-cover img {
  transform: scale(1.05);
}

.play-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  opacity: 0;
  transition: opacity 0.3s;
}

.video-card:hover .play-overlay {
  opacity: 1;
}

.play-overlay .n-icon {
  color: #fff;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.4));
}

.duration-tag {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 8px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.card-info {
  padding: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin: 0 0 10px;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  min-height: 42px;
}

.card-title:hover {
  color: #FB7299;
}

.card-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collect-time {
  font-size: 12px;
  color: #666;
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

@media (max-width: 640px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
}

/* 亮色模式 */
:global(.portal-layout.light-mode) .favorites-page {
  background: #f4f5f7;
}

:global(.portal-layout.light-mode) .page-header h1 {
  color: #18191c;
}

:global(.portal-layout.light-mode) .video-card {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

:global(.portal-layout.light-mode) .video-card:hover {
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:global(.portal-layout.light-mode) .card-title {
  color: #18191c;
}

:global(.portal-layout.light-mode) .empty-state h3 {
  color: #18191c;
}
</style>
