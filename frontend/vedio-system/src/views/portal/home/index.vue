<template>
  <div class="portal-home">
    <!-- 顶部 Banner 区 (沉浸式) -->
    <section class="hero-section">
      <!-- 动态模糊背景 -->
      <div 
        class="hero-bg" 
        :style="{ backgroundImage: `url(${currentBannerImg})` }"
      ></div>
      
      <div class="hero-content-wrapper">
        <!-- 轮播图主体 -->
        <div class="hero-carousel">
          <n-carousel 
            autoplay 
            :interval="5000" 
            draggable 
            effect="fade"
            :on-update:current-index="handleBannerChange"
          >
            <div 
              v-for="banner in banners" 
              :key="banner.id" 
              class="banner-slide" 
              @click="handleBannerClick(banner)"
            >
              <img :src="banner.imgUrl" class="banner-img" />
              <div class="banner-overlay"></div>
              <div class="banner-info">
                <h2>{{ banner.title }}</h2>
                <p class="banner-desc">连载中 · 2024年热门番剧</p>
              </div>
            </div>
            <template #dots="{ total, currentIndex, to }">
             <div class="custom-dots">
               <div 
                 v-for="index in total" 
                 :key="index" 
                 class="dot" 
                 :class="{ active: currentIndex === index - 1 }"
                 @click="to(index - 1)"
               ></div>
             </div>
            </template>
          </n-carousel>
        </div>
      </div>
    </section>

    <!-- 推荐内容区 -->
    <div class="main-container">
      <!-- 热门推荐 -->
      <section class="content-section">
        <div class="section-header">
          <div class="header-left">
            <n-icon size="28" color="#f07775"><FireIcon /></n-icon>
            <h2>热门推荐</h2>
          </div>
          <div class="header-right">
            <n-button text class="refresh-btn" :loading="refreshing" @click="handleRefresh">
              <template #icon><n-icon :class="{ 'spin-animation': refreshing }"><RefreshIcon /></n-icon></template>
              换一换
            </n-button>
          </div>
        </div>
        
        <div class="anime-grid">
          <AnimeCard v-for="video in hotVideos" :key="video.id" :video="video" />
        </div>
      </section>

      <!-- 最新视频 -->
      <section class="content-section">
        <div class="section-header">
          <div class="header-left">
            <n-icon size="28" color="#4a90e2"><ClockIcon /></n-icon>
            <h2>最新上传</h2>
          </div>
          <div class="header-right">
            <n-button text class="more-btn" @click="$router.push('/search')">
              查看更多 <n-icon><ArrowRightIcon /></n-icon>
            </n-button>
          </div>
        </div>
        
        <div class="anime-grid">
          <AnimeCard v-for="video in latestVideos" :key="video.id" :video="video" />
        </div>
      </section>
      
      <!-- 加载状态 -->
       <div v-if="loading" class="loading-state">
         <n-spin size="large" />
       </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { NCarousel, NIcon, NButton, NSpin } from 'naive-ui'
import videoApi from '@/api/video'
import bannerApi from '@/api/banner'
import AnimeCard from '../components/AnimeCard.vue'

const router = useRouter()
// 图标
const FireIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M13.5.67s.74 2.65.74 4.8c0 2.06-1.35 3.73-3.41 3.73-2.07 0-3.63-1.67-3.63-3.73l.03-.36C5.21 7.51 4 10.62 4 14c0 4.42 3.58 8 8 8s8-3.58 8-8C20 8.61 17.41 3.8 13.5.67zM11.71 19c-1.78 0-3.22-1.4-3.22-3.14 0-1.62 1.05-2.76 2.81-3.12 1.77-.36 3.6-1.21 4.62-2.58.39 1.29.59 2.65.59 4.04 0 2.65-2.15 4.8-4.8 4.8z' })]) }
const ClockIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z' })]) }
const RefreshIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z' })]) }
const ArrowRightIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z' })]) }

const loading = ref(true)
const refreshing = ref(false)
const banners = ref([])
const hotVideos = ref([])
const latestVideos = ref([])
const currentBannerIndex = ref(0)
const currentBannerImg = computed(() => {
  return banners.value[currentBannerIndex.value]?.imgUrl || ''
})

onMounted(async () => {
  try {
    const [bannersRes, hotRes, latestRes] = await Promise.all([
      bannerApi.getActiveBanners().catch(() => ({ data: [] })),
      videoApi.getVideos({ pageNo: 1, pageSize: 12, status: 2 }).catch(() => ({ data: { pageData: [] } })),
      videoApi.getVideos({ pageNo: 1, pageSize: 12, status: 2 }).catch(() => ({ data: { pageData: [] } })),
    ])

    banners.value = bannersRes.data || []
    if(banners.value.length === 0) {
      banners.value = [
         { id: 1, title: '欢迎来到 VMS 视频站', imgUrl: 'https://picsum.photos/1920/600?random=1' },
         { id: 2, title: '探索无限可能', imgUrl: 'https://picsum.photos/1920/600?random=2' },
         { id: 3, title: '发现精彩内容', imgUrl: 'https://picsum.photos/1920/600?random=3' }
      ]
    }
    
    hotVideos.value = hotRes.data?.pageData || []
    latestVideos.value = latestRes.data?.pageData || []
  } finally {
    loading.value = false
  }
})

function handleBannerChange(currentIndex) {
  currentBannerIndex.value = currentIndex
}

function handleBannerClick(banner) {
  if (!banner.linkUrl) return
  if (banner.linkUrl.startsWith('http')) {
    window.open(banner.linkUrl, '_blank')
  } else {
    router.push(banner.linkUrl)
  }
}

async function handleRefresh() {
  if (refreshing.value) return
  refreshing.value = true
  
  try {
    const randomPage = Math.floor(Math.random() * 5) + 1
    const { data } = await videoApi.getVideos({ pageNo: randomPage, pageSize: 12, status: 2 })
    
    const newVideos = data?.pageData || []
    if (newVideos.length > 0) {
      hotVideos.value = newVideos
      window.$message?.success('已为您更换推荐内容')
    } else {
      window.$message?.info('暂无更多推荐内容')
    }
  } catch (err) {
    window.$message?.error('刷新失败')
  } finally {
    refreshing.value = false
  }
}
</script>

<style scoped>
.portal-home {
  padding-bottom: 60px;
  /* background: #f4f5f7; 移除，由父级 layout 控制背景 */
  min-height: 100vh;
}

/* ================= Banner 区域 ================= */
.hero-section {
  position: relative;
  height: 420px; /* 调整高度 */
  overflow: hidden;
  margin-bottom: 40px;
}

/* 动态模糊背景 */
.hero-bg {
  position: absolute;
  inset: -20px;
  background-size: cover;
  background-position: center;
  filter: blur(20px);
  opacity: 0.6;
  transition: background-image 0.5s ease;
  z-index: 0;
}

/* 遮罩层 */
.hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(40px);
}

.hero-content-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  padding: 0; /* 移除外边距 */
}

.hero-carousel {
  flex: 1;
  height: 100%; /* 占满高度 */
  overflow: hidden;
  /* 移除投影和圆角，使其更贴合全屏感，或者保留看喜好，这里先改为全屏贴合 */
  border-radius: 0; 
  box-shadow: none;
}

.banner-slide {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40%;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
}

.banner-info {
  position: absolute;
  bottom: 40px;
  left: 30px;
  color: #fff;
}

.banner-info h2 {
  font-size: 24px;
  margin: 0 0 6px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.banner-desc {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.custom-dots {
  position: absolute;
  bottom: 20px;
  right: 30px;
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.4);
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: #fff;
  transform: scale(1.2);
}

/* ================= 索引卡片 ================= */
/* ================= 内容区 ================= */

/* ================= 内容区 ================= */
/* ================= 内容区 ================= */
.main-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 60px;
}

.content-section {
  margin-bottom: 50px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left h2 {
  font-size: 24px;
  color: #fff;
  margin: 0;
  font-weight: 500;
  transition: color 0.3s ease;
}

/* 亮色模式适配 */
:global(.portal-layout.light-mode) .header-left h2 {
  color: #18191c;
}

.anime-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr); /* 6列布局 */
  gap: 20px;
}

/* 响应式 */
@media (max-width: 1400px) {
  .anime-grid { grid-template-columns: repeat(5, 1fr); }
  .hero-content-wrapper { padding: 30px 40px 0; }
  .main-container { padding: 0 40px; }
}

@media (max-width: 1200px) {
  .anime-grid { grid-template-columns: repeat(4, 1fr); }
  .hero-index-card { display: none; } /* 小屏隐藏索引卡片 */
  .hero-section { height: 360px; }
  .hero-carousel { height: 320px; }
}

@media (max-width: 768px) {
  .anime-grid { grid-template-columns: repeat(2, 1fr); }
  .main-container { padding: 0 16px; }
  .hero-content-wrapper { padding: 20px 16px 0; }
  .hero-section { height: 240px; }
  .hero-carousel { height: 200px; }
}

/* 刷新动画 */
.spin-animation {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.empty-state .icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}
</style>
