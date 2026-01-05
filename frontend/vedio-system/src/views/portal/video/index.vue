<template>
  <div class="portal-video" :class="{ 'light-mode': !appStore.isDark }">
    <!-- 顶部占位，防止被 Header 遮挡 -->
    <div class="header-spacing"></div>

    <div class="video-container">
      <!-- 左侧：播放器和信息 -->
      <div class="main-column">
        <!-- 播放器 -->
        <div class="player-wrapper">
          <div id="xgplayer" ref="playerContainer"></div>
          <!-- 弹幕层 -->
          <div class="danmaku-layer" ref="danmakuLayer">
            <div 
              v-for="item in activeDanmaku" 
              :key="item.id" 
              class="danmaku-item"
              :style="{ 
                color: item.color,
                top: item.top + 'px',
                animationDuration: item.duration + 's'
              }"
            >
              {{ item.content }}
            </div>
          </div>
        </div>

        <!-- 弹幕发送框 -->
        <div class="danmaku-bar">
          <div class="danmaku-input-wrapper">
            <input 
              v-model="danmakuText"
              type="text" 
              placeholder="发个友善的弹幕见证当下~" 
              maxlength="100"
              @keyup.enter="sendDanmaku"
              :disabled="!isLoggedIn"
            />
            <div class="danmaku-tools">
              <input type="color" v-model="danmakuColor" class="color-picker" title="弹幕颜色" />
              <span class="char-count">{{ danmakuText.length }}/100</span>
            </div>
            <button class="send-btn" @click="sendDanmaku" :disabled="!danmakuText.trim() || !isLoggedIn">
              发送
            </button>
          </div>
          <div v-if="!isLoggedIn" class="login-tip">
            <span @click="showAuthModal = true">登录后发送弹幕</span>
          </div>
        </div>

        <!-- 视频信息区 -->
        <div class="video-info-card">
          <div class="video-header">
            <h1 :title="video.title">{{ video.title }}</h1>
            <div class="video-stats">
              <span class="stat-item"><n-icon><ViewIcon /></n-icon> {{ formatCount(video.viewCount) }}播放</span>
              <span class="stat-item"><n-icon><LikeIconSolid /></n-icon> {{ formatCount(video.likeCount) }}点赞</span>
              <span class="stat-item"><n-icon><TimeIcon /></n-icon> {{ video.createTime }}</span>
              <span class="stat-item copyright" v-if="video.copyright"><n-icon><StopIcon /></n-icon> 未经作者授权，禁止转载</span>
            </div>
          </div>

          <!-- 互动按钮区 -->
          <div class="action-bar">
            <button 
              class="action-btn like-btn" 
              :class="{ active: isLiked }"
              @click="handleToggleLike"
            >
              <n-icon size="22"><LikeIcon /></n-icon>
              <span>{{ isLiked ? '已点赞' : '点赞' }}</span>
            </button>
            <button 
              class="action-btn favorite-btn" 
              :class="{ active: isFavorited }"
              @click="handleToggleFavorite"
            >
              <n-icon size="22"><StarIcon /></n-icon>
              <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
            </button>
            <button class="action-btn share-btn">
              <n-icon size="22"><ShareIcon /></n-icon>
              <span>分享</span>
            </button>
          </div>

          <!-- 标签 -->
          <div class="video-tags" v-if="tags.length">
            <n-tag v-for="tag in tags" :key="tag.id" :bordered="false" class="custom-tag" size="small" round>
              {{ tag.name }}
            </n-tag>
          </div>

          <!-- 简介 -->
          <div class="video-intro">
            <p>{{ video.intro || '暂无简介' }}</p>
          </div>
        </div>
        
        <!-- 评论区 -->
        <CommentSection 
          :video-id="route.params.id"
          @need-login="showAuthModal = true"
        />
      </div>

      <!-- 右侧：分集与推荐 -->
      <div class="sidebar-column">
        <!-- UP主信息 -->
        <div class="up-card" v-if="uploader">
          <div class="up-avatar">
             <img :src="uploader.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${uploader.nickname || uploader.userId}`" />
          </div>
          <div class="up-info">
            <div class="up-name">{{ uploader.nickname || uploader.username || '未知用户' }}</div>
            <div class="up-desc">{{ uploader.intro || '这个人很懒，什么都没写' }}</div>
          </div>
        </div>

        <!-- 分集列表 -->
        <div class="video-sidebar" v-if="episodes.length > 1">
          <div class="sidebar-header">
            <h3>视频选集 ({{ episodes.length }})</h3>
          </div>
          <div class="episode-list">
            <div
              v-for="(ep, idx) in episodes"
              :key="ep.id"
              class="episode-item"
              :class="{ active: currentEpisode?.id === ep.id }"
              @click="playEpisode(ep)"
            >
              <span class="ep-num">P{{ idx + 1 }}</span>
              <span class="ep-title">{{ ep.title }}</span>
            </div>
          </div>
        </div>

        <!-- 相关推荐 -->
        <div class="related-videos" v-if="relatedVideos.length">
          <h3>相关推荐</h3>
          <div class="related-grid">
             <!-- 使用简化的列布局 -->
            <VideoCard v-for="v in relatedVideos" :key="v.id" :video="v" />
          </div>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <AuthModal v-model:show="showAuthModal" @success="onLoginSuccess" />
  </div>
</template>

<script setup>
import { ref, watch, h, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { NIcon, NTag, NButton } from 'naive-ui'
import Player from 'xgplayer'
import 'xgplayer/dist/index.min.css'
import videoApi from '@/api/video'
import interactApi from '@/api/interact'
import historyApi from '@/api/history'
import { useAuthStore, useAppStore } from '@/store'
import VideoCard from '../components/VideoCard.vue'
import CommentSection from '../components/CommentSection.vue'
import AuthModal from '../components/AuthModal.vue'

// 图标
const ViewIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z' })]) }
const LikeIconSolid = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z' })]) }
const LikeIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M9 21h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2zM9 9l4.34-4.34L12 10h9v2l-3 7H9V9zM1 9h4v12H1z' })]) }
const TimeIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z' })]) }
const StopIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15v-2h2v2h-2zm0-10v6h2V7h-2z' })]) }
const StarIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z' })]) }
const ShareIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92c0-1.61-1.31-2.92-2.92-2.92z' })]) }

const route = useRoute()
const authStore = useAuthStore()
const appStore = useAppStore()

const playerContainer = ref(null)
const showAuthModal = ref(false)
let player = null // xgplayer实例

const video = ref({})
const episodes = ref([])
const tags = ref([])
const currentEpisode = ref(null)
const currentVideoUrl = ref('')
const relatedVideos = ref([])
const uploader = ref(null) // 投稿人信息

// 互动状态
const isLiked = ref(false)
const isFavorited = ref(false)

// 弹幕相关
const danmakuText = ref('')
const danmakuColor = ref('#FFFFFF')
const danmakuList = ref([])
const activeDanmaku = ref([])
const danmakuLayer = ref(null)
let danmakuTimer = null

// 观看进度保存相关
const totalDuration = ref(0)
const currentTime = ref(0)
const saveProgressTimer = ref(null)

const isLoggedIn = computed(() => !!authStore.accessToken)

async function loadVideo() {
  try {
    const { data } = await videoApi.getVideoDetail(route.params.id)
    video.value = data?.video || {}
    episodes.value = data?.episodes || []
    tags.value = data?.tags || []
    uploader.value = data?.uploader || null
    
    let videoUrl = ''
    if (episodes.value.length) {
      currentEpisode.value = episodes.value[0]
      videoUrl = episodes.value[0].fileUrl
    } else {
      videoUrl = video.value.videoUrl
    }
    currentVideoUrl.value = videoUrl
    
    // 初始化或更新播放器
    await nextTick()
    initPlayer(videoUrl)

    loadRelated()
    loadInteractStatus()
    loadDanmaku() // 加载弹幕
  } catch (e) {
    console.error('加载视频失败', e)
  }
}

/**
 * 初始化 xgplayer 播放器
 */
function initPlayer(url) {
  // 如果已有播放器实例，先销毁
  if (player) {
    player.destroy()
    player = null
  }
  
  if (!playerContainer.value || !url) return
  
  // 获取上次播放进度
  const startTime = parseInt(route.query.t) || 0
  
  player = new Player({
    id: 'xgplayer',
    url: url,
    width: '100%',
    height: '100%',
    autoplay: true,
    playsinline: true,
    volume: 0.8,
    startTime: startTime,
    lang: 'zh-cn',
    // 主题风格
    commonStyle: {
      progressColor: '#FB7299',      // 进度条颜色
      playedColor: '#FB7299',        // 已播放颜色
      volumeColor: '#FB7299',        // 音量条颜色
    },
    // 功能配置
    playbackRate: [0.5, 0.75, 1, 1.25, 1.5, 2], // 倍速播放
    pip: true,           // 画中画
    download: false,     // 禁用下载
    screenShot: true,    // 截图
    keyShortcut: true,   // 键盘快捷键
    cssFullscreen: true, // 网页全屏
    rotate: true,        // 旋转
  })
  
  // 监听时间更新
  player.on('timeupdate', () => {
    currentTime.value = Math.floor(player.currentTime || 0)
  })
  
  // 监听元数据加载
  player.on('loadedmetadata', () => {
    totalDuration.value = Math.floor(player.duration || 0)
  })
}

async function loadRelated() {
  try {
    const { data } = await videoApi.getVideos({
      pageNo: 1,
      pageSize: 8,
      categoryId: video.value.categoryId,
      status: 2
    })
    relatedVideos.value = (data?.pageData || []).filter(v => v.id != route.params.id)
    
    // 如果没有相关视频，加载模拟数据填充（为了演示效果）
    if (relatedVideos.value.length === 0) {
      relatedVideos.value = Array.from({ length: 5 }).map((_, i) => ({
        id: 1000 + i,
        title: `推荐观看：热门番剧精彩片段 ${i + 1}`,
        coverUrl: `https://api.dicebear.com/7.x/shapes/svg?seed=${i}`,
        uploader: '官方推荐',
        viewCount: 10000 + i * 500,
        duration: 180 + i * 30
      }))
    }
  } catch (e) { 
    // 错误时也加载模拟数据
     relatedVideos.value = Array.from({ length: 5 }).map((_, i) => ({
        id: 1000 + i,
        title: `推荐观看：热门番剧精彩片段 ${i + 1}`,
        coverUrl: `https://api.dicebear.com/7.x/shapes/svg?seed=${i}`,
        uploader: '官方推荐',
        viewCount: 10000 + i * 500,
        duration: 180 + i * 30
      }))
  }
}

async function loadInteractStatus() {
  if (!isLoggedIn.value) return
  
  try {
    const { data } = await interactApi.getInteractStatus(route.params.id)
    isLiked.value = data?.liked || false
    isFavorited.value = data?.favorited || false
  } catch (e) { }
}

function playEpisode(ep) {
  currentEpisode.value = ep
  currentVideoUrl.value = ep.fileUrl
  // 使用xgplayer切换视频源
  if (player) {
    player.src = ep.fileUrl
    player.play()
  } else {
    initPlayer(ep.fileUrl)
  }
}

function formatCount(count) {
  if (!count) return '0'
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return count.toString()
}

// 点赞/收藏
async function handleToggleLike() {
  if (!isLoggedIn.value) {
    showAuthModal.value = true
    return
  }
  
  try {
    await interactApi.toggleLike('video', route.params.id)
    isLiked.value = !isLiked.value
    video.value.likeCount = (video.value.likeCount || 0) + (isLiked.value ? 1 : -1)
    window.$message?.success(isLiked.value ? '点赞成功' : '已取消点赞')
  } catch (e) {
    window.$message?.error('操作失败')
  }
}

async function handleToggleFavorite() {
  if (!isLoggedIn.value) {
    showAuthModal.value = true
    return
  }
  
  try {
    await interactApi.toggleFavorite(route.params.id)
    isFavorited.value = !isFavorited.value
    window.$message?.success(isFavorited.value ? '收藏成功' : '已取消收藏')
  } catch (e) {
    window.$message?.error('操作失败')
  }
}

// 观看进度保存
function handleLoadedMetadata() {
  if (videoRef.value) {
    totalDuration.value = Math.floor(videoRef.value.duration)
    
    // 如果有之前的进度，跳转
    const startTime = parseInt(route.query.t)
    if (startTime && startTime < totalDuration.value) {
      videoRef.value.currentTime = startTime
    }
  }
}

function handleTimeUpdate() {
  if (videoRef.value) {
    currentTime.value = Math.floor(videoRef.value.currentTime)
  }
}

function saveProgress() {
  if (!isLoggedIn.value || !totalDuration.value) return
  
  historyApi.saveProgress({
    videoId: Number(route.params.id),
    episodeId: currentEpisode.value?.id || null,
    watchDuration: currentTime.value,
    totalDuration: totalDuration.value
  }).catch(() => {})
}

async function onLoginSuccess() {
  // 登录成功后获取用户信息
  const userStore = (await import('@/store')).useUserStore()
  try {
    await userStore.fetchUserInfo()
  } catch (e) {
    console.error('获取用户信息失败', e)
  }
  window.$message?.success('登录成功！')
  loadInteractStatus()
}

// ==================== 弹幕功能 ====================
async function loadDanmaku() {
  try {
    const { data } = await interactApi.getDanmaku(route.params.id)
    danmakuList.value = data || []
    
    // 启动弹幕调度器
    startDanmakuScheduler()
  } catch (e) {
    console.error('加载弹幕失败', e)
  }
}

// 弹幕调度器 - 根据播放时间显示弹幕
function startDanmakuScheduler() {
  if (danmakuTimer) clearInterval(danmakuTimer)
  
  danmakuTimer = setInterval(() => {
    if (!player || player.paused) return
    
    const currentSec = player.currentTime || 0
    
    // 查找当前时间点附近的弹幕
    const toShow = danmakuList.value.filter(d => {
      return d.time >= currentSec - 0.5 && d.time <= currentSec + 0.5 && !d.shown
    })
    
    toShow.forEach(d => {
      d.shown = true
      showDanmakuItem(d)
    })
  }, 500)
}

// 显示单条弹幕
function showDanmakuItem(d) {
  const layerHeight = danmakuLayer.value?.clientHeight || 400
  const item = {
    id: d.id || Date.now(),
    content: d.content,
    color: d.color || '#FFFFFF',
    top: Math.random() * (layerHeight - 30),
    duration: 8 + Math.random() * 2
  }
  
  activeDanmaku.value.push(item)
  
  // 动画结束后移除
  setTimeout(() => {
    activeDanmaku.value = activeDanmaku.value.filter(i => i.id !== item.id)
  }, item.duration * 1000)
}

async function sendDanmaku() {
  if (!danmakuText.value.trim()) return
  if (!isLoggedIn.value) {
    showAuthModal.value = true
    return
  }
  
  const currentPlayerTime = player?.currentTime || 0
  
  try {
    await interactApi.addDanmaku({
      videoId: Number(route.params.id),
      content: danmakuText.value.trim(),
      time: currentPlayerTime,
      color: danmakuColor.value,
      mode: 1
    })
    
    // 立即显示发送的弹幕
    showDanmakuItem({
      id: Date.now(),
      content: danmakuText.value.trim(),
      color: danmakuColor.value,
      time: currentPlayerTime
    })
    
    window.$message?.success('弹幕发送成功！')
    danmakuText.value = ''
  } catch (e) {
    window.$message?.error(e.message || '发送失败')
  }
}

// 定时保存进度（每30秒）
onMounted(() => {
  saveProgressTimer.value = setInterval(() => {
    if (currentTime.value > 0) {
      saveProgress()
    }
  }, 30000)
})

onUnmounted(() => {
  if (saveProgressTimer.value) {
    clearInterval(saveProgressTimer.value)
  }
  if (danmakuTimer) {
    clearInterval(danmakuTimer)
  }
  // 离开页面时保存一次进度
  saveProgress()
  
  // 销毁xgplayer实例
  if (player) {
    player.destroy()
    player = null
  }
})

watch(() => route.params.id, loadVideo, { immediate: true })
</script>

<style scoped>
.portal-video {
  min-height: 100vh;
  /* background: #0f1115; 移除硬编码背景，使用透明以支持全局主题 */
  padding-bottom: 200px; /* 增加底部留白，解决评论区无法滑动问题 */
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.header-spacing {
  height: 64px;
}

.video-container {
  max-width: 1500px;
  margin: 20px auto;
  display: flex;
  gap: 30px;
  padding: 0 24px;
  animation: slideUp 0.5s ease 0.1s both;
  align-items: flex-start; /* 确保 sticky 生效 */
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.main-column {
  flex: 1;
  min-width: 0;
}

/* 播放器 */
.player-wrapper {
  position: relative;
  background: linear-gradient(135deg, #1a1a2e 0%, #0a0a0f 100%);
  width: 100%;
  aspect-ratio: 16 / 9;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.player-wrapper:hover {
  box-shadow: 
    0 12px 48px rgba(0, 0, 0, 0.7),
    0 0 0 1px rgba(251, 114, 153, 0.1);
}

/* 弹幕层 */
.danmaku-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 50px;
  pointer-events: none;
  overflow: hidden;
  z-index: 10;
}

.danmaku-item {
  position: absolute;
  white-space: nowrap;
  font-size: 20px;
  font-weight: 500;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.8), 0 0 3px rgba(0,0,0,0.5);
  animation: danmakuScroll linear forwards;
  right: -100%;
}

@keyframes danmakuScroll {
  from { transform: translateX(0); }
  to { transform: translateX(calc(-100vw - 100%)); }
}

/* 弹幕发送框 */
.danmaku-bar {
  background: rgba(30, 30, 35, 0.95);
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.danmaku-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.danmaku-input-wrapper input[type="text"] {
  flex: 1;
  background: rgba(50, 50, 55, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  padding: 10px 14px;
  color: #fff;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.danmaku-input-wrapper input[type="text"]:focus {
  border-color: #FB7299;
  background: rgba(60, 60, 65, 0.9);
}

.danmaku-input-wrapper input[type="text"]:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.danmaku-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-picker {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: transparent;
}

.char-count {
  color: #666;
  font-size: 12px;
  min-width: 45px;
}

.send-btn {
  background: linear-gradient(135deg, #FB7299, #f25d8e);
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(251, 114, 153, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-tip {
  margin-top: 8px;
  text-align: center;
}

.login-tip span {
  color: #FB7299;
  font-size: 13px;
  cursor: pointer;
}

.login-tip span:hover {
  text-decoration: underline;
}

/* 视频信息 */
.video-info-card {
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

/* 亮色模式边框适配 */
:global(.portal-layout.light-mode) .video-info-card {
  border-bottom-color: rgba(0, 0, 0, 0.1);
}

.video-header h1 {
  font-size: 20px;
  font-weight: 500;
  color: #fff; /* 默认暗色 */
  margin: 0 0 12px;
  line-height: 28px;
  transition: color 0.3s;
}

/* 亮色模式标题适配 */
:global(.portal-layout.light-mode) .video-header h1 {
  color: #18191c;
}

.video-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  font-size: 13px;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.copyright {
  color: #f25d8e;
}

/* 互动按钮区 */
.action-bar {
  display: flex;
  gap: 12px;
  margin: 24px 0;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #25272d 0%, #1f2125 100%);
  border: 1px solid #3a3c42;
  border-radius: 10px;
  color: #ddd;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

/* 亮色模式按钮适配 */
:global(.portal-layout.light-mode) .action-btn {
  background: #fff;
  border-color: #e3e5e7;
  color: #61666d;
}
:global(.portal-layout.light-mode) .action-btn:hover {
  background: #f6f7f8;
  color: #18191c;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
  transition: left 0.5s ease;
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:hover {
  background: linear-gradient(135deg, #2f3239 0%, #25272d 100%);
  border-color: #4a4c52;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.action-btn:active {
  transform: translateY(0) scale(0.98);
}

.action-btn.active {
  background: linear-gradient(135deg, rgba(251, 114, 153, 0.25), rgba(251, 114, 153, 0.1));
  border-color: #FB7299;
  color: #FB7299;
  box-shadow: 0 0 20px rgba(251, 114, 153, 0.2);
}

.action-btn.active:hover {
  background: linear-gradient(135deg, rgba(251, 114, 153, 0.35), rgba(251, 114, 153, 0.15));
  box-shadow: 0 4px 20px rgba(251, 114, 153, 0.3);
}

/* 按钮动效 */
.like-btn.active .n-icon {
  animation: heartBeat 0.6s ease;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  15% { transform: scale(1.3); }
  30% { transform: scale(0.9); }
  45% { transform: scale(1.2); }
  60% { transform: scale(1); }
}

.favorite-btn.active .n-icon {
  animation: starPop 0.5s ease;
}

@keyframes starPop {
  0% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.4) rotate(15deg); }
  100% { transform: scale(1) rotate(0deg); }
}

.video-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.custom-tag {
  background: linear-gradient(135deg, #1f2125 0%, #16181d 100%);
  color: #9499a0;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid transparent;
  padding: 4px 12px;
}

/* 亮色模式 Tag 适配 */
:global(.portal-layout.light-mode) .custom-tag {
  background: #f6f7f8;
  color: #61666d;
}
:global(.portal-layout.light-mode) .custom-tag:hover {
  background: #e3e5e7;
  color: #18191c;
}

.custom-tag:hover {
  color: #FB7299;
  background: rgba(251, 114, 153, 0.1);
  border-color: rgba(251, 114, 153, 0.3);
  transform: translateY(-1px);
}

.video-intro {
  color: #d0d0d0;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  background: linear-gradient(135deg, rgba(31, 33, 37, 0.5) 0%, transparent 100%);
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #FB7299;
}

/* 亮色简介适配 */
:global(.portal-layout.light-mode) .video-intro {
  color: #18191c;
  background: #f6f7f8;
}

/* 侧边栏 */
.sidebar-column {
  width: 350px;
  flex-shrink: 0;
  position: sticky; /* 悬浮效果 */
  top: 84px; /* header height + padding */
}

/* UP主卡片 */
.up-card {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.up-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #333;
  overflow: hidden;
}

.up-avatar img {
  width: 100%;
  height: 100%;
}

.up-info {
  flex: 1;
}

.up-name {
  color: #FB7299;
  font-weight: 500;
  font-size: 15px;
  margin-bottom: 2px;
}

.up-desc {
  color: #999;
  font-size: 12px;
}

/* 分集列表 */
.video-sidebar {
  background: #1f2125;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
}

/* 亮色分集 */
:global(.portal-layout.light-mode) .video-sidebar {
  background: #f1f2f3;
}
:global(.portal-layout.light-mode) .sidebar-header h3 {
  color: #18191c;
}

.sidebar-header {
  margin-bottom: 12px;
}

.sidebar-header h3 {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

.episode-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.episode-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #2b2d32;
  border-radius: 4px;
  padding: 8px;
  color: #ddd;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 1px solid transparent;
  transition: all 0.2s;
}

/* 亮色分集Item */
:global(.portal-layout.light-mode) .episode-item {
  background: #fff;
  color: #18191c;
  border: 1px solid #e3e5e7;
}
:global(.portal-layout.light-mode) .episode-item:hover {
  border-color: #FB7299;
  color: #FB7299;
}

.episode-item:hover {
  background: #383a42;
}

.episode-item.active {
  background: #2b2d32;
  color: #FB7299;
  border-color: #FB7299;
}
:global(.portal-layout.light-mode) .episode-item.active {
  background: #fff;
  color: #FB7299;
  border-color: #FB7299;
}

.ep-num {
  font-weight: bold;
}

.ep-title {
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 相关推荐 */
.related-videos h3 {
  font-size: 16px;
  color: #fff;
  margin: 0 0 16px;
}
:global(.portal-layout.light-mode) .related-videos h3 {
  color: #18191c;
}

.related-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

@media (max-width: 1000px) {
  .video-container {
    flex-direction: column;
  }
  .sidebar-column {
    width: 100%;
    position: static; /* 移动端取消悬浮 */
  }
  .episode-list {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 弹幕框亮色模式 */
.portal-video.light-mode .danmaku-bar {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(0, 0, 0, 0.1);
}

.portal-video.light-mode .danmaku-input-wrapper input[type="text"] {
  background: #f4f5f7;
  border-color: rgba(0, 0, 0, 0.1);
  color: #18191c;
}

.portal-video.light-mode .danmaku-input-wrapper input[type="text"]:focus {
  background: #fff;
}

.portal-video.light-mode .char-count {
  color: #999;
}

/* 视频简介亮色模式 */
.portal-video.light-mode .video-intro {
  background: rgba(0, 0, 0, 0.02);
}

.portal-video.light-mode .video-intro p {
  color: #505050;
}

/* 标签亮色模式 */
.portal-video.light-mode .custom-tag {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.08);
  color: #18191c;
}

/* 分集列表亮色模式 */
.portal-video.light-mode .episode-item {
  background: #f4f5f7;
  color: #18191c;
}

.portal-video.light-mode .episode-item:hover {
  background: #e9eaec;
}

/* 视频标题亮色模式 */
.portal-video.light-mode .video-header h1 {
  color: #18191c;
}

/* 统计信息亮色模式 */
.portal-video.light-mode .video-stats {
  color: #61666d;
}

/* 互动按钮亮色模式 */
.portal-video.light-mode .action-btn {
  background: #f4f5f7;
  color: #18191c;
  border-color: #e3e5e7;
}

.portal-video.light-mode .action-btn:hover {
  background: #e9eaec;
}

/* 右侧边栏亮色模式 */
.portal-video.light-mode .sidebar-header h3 {
  color: #18191c;
}

.portal-video.light-mode .uploader-intro {
  color: #61666d;
}

.portal-video.light-mode .related-videos h3 {
  color: #18191c;
}
</style>
