<template>
  <router-link :to="`/video/${video.id}`" class="anime-card">
    <div class="anime-cover">
      <img :src="video.coverUrl || defaultCover" :alt="video.title" loading="lazy" />
      
      <!-- 评分标记 -->
      <div class="rating-badge" v-if="video.likeCount > 100">
        {{ (9.0 + (video.likeCount % 10) / 10).toFixed(1) }}
      </div>

      <!-- 底部播放数据 -->
      <div class="stats-overlay">
        <span class="stat-item">
          <n-icon size="12"><PlayIcon /></n-icon>
          {{ formatCount(video.viewCount) }}
        </span>
      </div>
      
      <!-- 悬停播放图标 -->
      <div class="hover-play">
        <n-icon size="24"><PlayArrowIcon /></n-icon>
      </div>
    </div>
    
    <div class="anime-info">
      <h3 class="anime-title" :title="video.title">{{ video.title }}</h3>
      <p class="anime-status">
        {{ video.uploader ? `UP ${video.uploader}` : '全 12 话' }}
      </p>
    </div>
  </router-link>
</template>

<script setup>
import { h } from 'vue'
import { NIcon } from 'naive-ui'

defineProps({
  video: { type: Object, required: true }
})

const defaultCover = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="300" height="400" fill="%23333"><rect width="300" height="400"/><text x="50%" y="50%" fill="%23666" text-anchor="middle" dy=".3em">无封面</text></svg>'

const PlayIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M8 5v14l11-7z' })]) }
const PlayArrowIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M8 5v14l11-7z' })]) }

function formatCount(count) {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}
</script>

<style scoped>
.anime-card {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  width: 100%;
  cursor: pointer;
  group: hover;
}

/* 封面容器 3:4 比例 */
.anime-cover {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4;
  border-radius: 8px;
  overflow: hidden;
  background: #f1f2f3;
  transition: all 0.2s ease-in-out;
}

.anime-card:hover .anime-cover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.anime-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 评分角标 */
.rating-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #FB7299;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 底部数据 */
.stats-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 30px 8px 6px;
  background: linear-gradient(to top, rgba(0,0,0,0.6), transparent);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: flex-end;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 悬停播放图标 */
.hover-play {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  color: #fff;
}

.anime-card:hover .hover-play {
  opacity: 1;
}

.hover-play .n-icon {
  background: rgba(251, 114, 153, 0.9);
  border-radius: 50%;
  padding: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.3);
  transform: scale(0.8);
  transition: transform 0.2s;
}

.anime-card:hover .hover-play .n-icon {
  transform: scale(1);
}

/* 信息区 */
.anime-info {
  margin-top: 10px;
}

.anime-title {
  font-size: 14px;
  color: #e1e1e1; /* 默认暗色模式颜色 */
  line-height: 20px;
  height: 40px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: color 0.2s;
}

.anime-card:hover .anime-title {
  color: #FB7299;
}

:global(.portal-layout.light-mode) .anime-title {
  color: #18191c; /* 亮色模式颜色 */
}

:global(.portal-layout.light-mode) .anime-card:hover .anime-title {
  color: #FB7299;
}

.anime-status {
  font-size: 12px;
  color: #9499a0;
  margin: 4px 0 0;
}
</style>
