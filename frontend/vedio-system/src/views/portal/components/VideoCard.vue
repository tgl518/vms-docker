<template>
  <router-link :to="`/video/${video.id}`" class="video-card">
    <div class="video-cover">
      <img :src="video.coverUrl || defaultCover" :alt="video.title" />
      
      <!-- 底部渐变层：放置播放量和时长 -->
      <div class="video-stats-overlay">
        <div class="left-stats">
          <span class="stat-item">
            <n-icon size="12"><PlayIcon /></n-icon>
            {{ formatCount(video.viewCount) }}
          </span>
          <span class="stat-item">
            <n-icon size="12"><LikeIcon /></n-icon>
            {{ formatCount(video.likeCount) }}
          </span>
        </div>
        <span class="video-duration">{{ formatDuration(video.duration) }}</span>
      </div>

      <!-- 悬停时的遮罩 -->
      <div class="hover-overlay"></div>
    </div>
    
    <div class="video-info">
      <h3 class="video-title" :title="video.title">{{ video.title }}</h3>
      <div class="video-meta">
        <span class="up-name" v-if="video.uploader">UP {{ video.uploader }}</span>
        <span class="upload-date" v-else>{{ video.createTime?.split(' ')[0] || '2025-01-01' }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { h } from 'vue'
import { NIcon } from 'naive-ui'

defineProps({
  video: { type: Object, required: true }
})

const defaultCover = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180" fill="%23333"><rect width="320" height="180"/><text x="50%" y="50%" fill="%23666" text-anchor="middle" dy=".3em">无封面</text></svg>'

// 图标
const PlayIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M8 5v14l11-7z' })]) }
const LikeIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z' })]) }

function formatCount(count) {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

function formatDuration(seconds) {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.video-card {
  display: flex;
  flex-direction: column;
  background: transparent;
  border-radius: 8px;
  overflow: hidden;
  text-decoration: none;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-6px);
}

/* 封面区 */
.video-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a2e 0%, #16161a 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: box-shadow 0.3s ease;
}

.video-card:hover .video-cover {
  box-shadow: 0 8px 32px rgba(251, 114, 153, 0.25);
}

.video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease, filter 0.3s ease;
}

.video-card:hover .video-cover img {
  transform: scale(1.08);
  filter: brightness(1.1) contrast(1.1);
}

/* 底部数据条 */
.video-stats-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 48px;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.85) 100%);
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 8px 10px;
  color: #fff;
  font-size: 11px;
  pointer-events: none;
}

.left-stats {
  display: flex;
  gap: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0.9;
  transition: opacity 0.2s;
}

.video-card:hover .stat-item {
  opacity: 1;
}

.video-duration {
  background: rgba(0, 0, 0, 0.6);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

/* 悬停效果 - 播放图标 */
.hover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.hover-overlay::after {
  content: '';
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(251, 114, 153, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transform: scale(0.5);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 20px rgba(251, 114, 153, 0.5);
  /* 播放三角形 */
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='white' viewBox='0 0 24 24'%3E%3Cpath d='M8 5v14l11-7z'/%3E%3C/svg%3E");
  background-size: 24px;
  background-position: center;
  background-repeat: no-repeat;
}

.video-card:hover .hover-overlay {
  background: rgba(0, 0, 0, 0.15);
}

.video-card:hover .hover-overlay::after {
  opacity: 1;
  transform: scale(1);
}

/* 信息区 */
.video-info {
  padding-top: 12px;
}

.video-title {
  font-size: 14px;
  color: #f1f2f3;
  line-height: 22px;
  margin: 0;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-weight: 600;
  transition: color 0.2s ease;
  letter-spacing: -0.2px;
}

.video-card:hover .video-title {
  color: #FB7299;
}

.video-meta {
  margin-top: 8px;
  font-size: 12px;
  color: #9499a0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.up-name {
  transition: color 0.2s;
  cursor: pointer;
}

.up-name:hover {
  color: #FB7299;
}

.upload-date {
  opacity: 0.8;
}

/* 新增：进入动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.video-card {
  animation: fadeInUp 0.4s ease forwards;
}

/* 亮色模式 */
:global(.portal-layout.light-mode) .video-title {
  color: #18191c;
}

:global(.portal-layout.light-mode) .video-cover {
  background: linear-gradient(135deg, #e5e6eb 0%, #f4f5f7 100%);
}

:global(.portal-layout.light-mode) .video-card:hover .video-title {
  color: #FB7299;
}
</style>

