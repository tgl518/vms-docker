<template>
  <div class="portal-category">
    <div class="header-spacing"></div>
    
    <div class="category-container">
      <div class="category-header">
        <h1>{{ categoryName }}</h1>
        <n-space>
          <n-button 
            v-for="sort in sortOptions" 
            :key="sort.value"
            :type="currentSort === sort.value ? 'primary' : 'default'"
            size="small"
            round
            @click="currentSort = sort.value"
          >
            {{ sort.label }}
          </n-button>
        </n-space>
      </div>

      <div class="video-grid" v-if="videos.length">
        <VideoCard v-for="video in videos" :key="video.id" :video="video" />
      </div>

      <div v-else-if="!loading" class="empty-state">
        <n-empty description="该分类暂无视频" />
      </div>

      <div v-if="hasMore" class="load-more">
        <n-button :loading="loading" size="large" round @click="loadMore">
          加载更多
        </n-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { NButton, NSpace, NEmpty } from 'naive-ui'
import videoApi from '@/api/video'
import VideoCard from '../components/VideoCard.vue'

const route = useRoute()

const sortOptions = [
  { label: '最新发布', value: 'latest' },
  { label: '最多播放', value: 'hot' },
]

const categoryName = ref('全部视频')
const videos = ref([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const currentSort = ref('latest')

async function loadVideos(reset = false) {
  if (reset) {
    page.value = 1
    hasMore.value = true
  }
  
  loading.value = true
  try {
    const { data } = await videoApi.getVideos({
      pageNo: page.value,
      pageSize: 20,
      categoryId: route.params.id,
      status: 2
    })
    
    const list = data?.pageData || []
    videos.value = reset ? list : [...videos.value, ...list]
    hasMore.value = list.length === 20
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  loadVideos()
}

watch(() => route.params.id, async (id) => {
  if (id) {
    const { data } = await videoApi.getEnabledCategories().catch(() => ({ data: [] }))
    const cat = (data || []).find(c => c.id == id)
    categoryName.value = cat?.name || '分类视频'
  }
  loadVideos(true)
}, { immediate: true })

watch(currentSort, () => loadVideos(true))
</script>

<style scoped>
.portal-category {
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

.category-container {
  max-width: 1700px;
  margin: 0 auto;
  padding: 30px 60px 60px;
}

.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding: 20px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.category-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  position: relative;
}

.category-header h1::after {
  content: '';
  position: absolute;
  bottom: -22px;
  left: 0;
  width: 50px;
  height: 3px;
  background: linear-gradient(90deg, #FB7299, transparent);
  border-radius: 2px;
}

/* 网格布局: 与首页保持一致 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 24px 20px;
}

@media (max-width: 1600px) {
  .video-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1200px) {
  .video-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .category-container {
    padding: 20px 24px 60px;
  }
}

@media (max-width: 768px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px 12px;
  }
  .category-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

.empty-state {
  padding: 100px 20px;
  text-align: center;
}

.load-more {
  padding: 50px;
  text-align: center;
}

/* 亮色模式 */
:global(.portal-layout.light-mode) .portal-category {
  background: #f4f5f7;
}

:global(.portal-layout.light-mode) .category-header {
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

:global(.portal-layout.light-mode) .category-header h1 {
  color: #18191c;
}
</style>
