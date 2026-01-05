<template>
  <div class="portal-search">
    <div class="header-spacing"></div>
    
    <div class="search-container">
      <!-- 搜索框区域 -->
      <div class="search-box-wrapper" v-if="!keyword">
        <h1 class="search-title">🔍 发现更多精彩视频</h1>
        
        <!-- 热门搜索 -->
        <div class="hot-search" v-if="hotKeywords.length">
          <div class="section-title">🔥 热门搜索</div>
          <div class="tag-list">
            <n-tag
              v-for="(item, index) in hotKeywords"
              :key="item"
              :type="index < 3 ? 'error' : 'default'"
              round
              class="search-tag"
              :class="{ 'hot-rank': index < 3 }"
              @click="doSearch(item)"
            >
              <span v-if="index < 3" class="rank-num">{{ index + 1 }}</span>
              {{ item }}
            </n-tag>
          </div>
        </div>
        
        <!-- 搜索历史 -->
        <div class="search-history" v-if="searchHistory.length">
          <div class="section-title">
            <span>🕒 搜索历史</span>
            <n-button text type="error" size="small" @click="clearHistory">
              <i class="i-fe:trash-2 mr-4" /> 清空
            </n-button>
          </div>
          <div class="tag-list">
            <n-tag
              v-for="item in searchHistory"
              :key="item"
              round
              closable
              class="search-tag history-tag"
              @click="doSearch(item)"
              @close="removeHistory(item)"
            >
              {{ item }}
            </n-tag>
          </div>
        </div>
      </div>

      <!-- 搜索结果 -->
      <template v-else>
        <div class="search-header">
          <div class="header-main">
            <h1>搜索结果：<span class="keyword">{{ keyword }}</span></h1>
            <p class="result-count" v-if="videos.length">共找到 {{ total }} 个相关视频</p>
          </div>
        </div>

        <!-- 筛选栏 -->
        <div class="filter-bar">
          <div class="filter-group">
            <span class="filter-label">分类：</span>
            <n-button 
              v-for="cat in [{ id: null, name: '全部' }, ...categories]" 
              :key="cat.id ?? 'all'"
              :type="selectedCategory === cat.id ? 'primary' : 'default'"
              size="small"
              quaternary
              class="filter-btn"
              @click="selectedCategory = cat.id; search(true)"
            >
              {{ cat.name }}
            </n-button>
          </div>
          
          <div class="filter-right">
            <span class="filter-label">排序：</span>
            <n-select
              v-model:value="sortBy"
              :options="sortOptions"
              size="small"
              style="width: 120px"
              @update:value="search(true)"
            />
          </div>
        </div>

        <div class="video-grid" v-if="videos.length">
          <VideoCard v-for="video in videos" :key="video.id" :video="video" />
        </div>

        <div v-else-if="!loading" class="empty-state">
          <div class="empty-icon">🔍</div>
          <n-empty description="什么都没找到耶...">
            <template #extra>
               <div class="empty-tips">
                 可以尝试更换关键词，或者<router-link to="/">返回首页</router-link>看看
               </div>
            </template>
          </n-empty>
        </div>

        <div v-if="loading" class="loading-state">
          <n-spin size="large" />
          <p class="loading-text">搜索中...</p>
        </div>
        
        <div v-if="hasMore && !loading" class="load-more">
          <n-button :loading="loading" size="large" round secondary @click="loadMore">
            加载更多
          </n-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NEmpty, NButton, NSpin, NTag, NSelect } from 'naive-ui'
import videoApi from '@/api/video'
import VideoCard from '../components/VideoCard.vue'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const videos = ref([])
const loading = ref(false)
const page = ref(1)
const hasMore = ref(false)
const total = ref(0)

// 筛选条件
const categories = ref([])
const selectedCategory = ref(null)
const sortBy = ref('latest')

const sortOptions = [
  { label: '最新发布', value: 'latest' },
  { label: '最多播放', value: 'views' },
  { label: '最多点赞', value: 'likes' },
]

// 搜索历史 (存储在 localStorage)
const HISTORY_KEY = 'vms_search_history'
const MAX_HISTORY = 10
const searchHistory = ref([])

// 热门搜索词 (可以从后端获取，这里用静态数据)
const hotKeywords = ref([
  '动漫', '音乐', '舞蹈', '游戏', '科技', 
  '生活', '美食', '旅行', '知识', '搞笑'
])

async function loadCategories() {
  try {
    const { data } = await videoApi.getEnabledCategories()
    categories.value = (data || []).slice(0, 8)
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

function loadHistory() {
  try {
    const saved = localStorage.getItem(HISTORY_KEY)
    searchHistory.value = saved ? JSON.parse(saved) : []
  } catch (e) {
    searchHistory.value = []
  }
}

function saveHistory(kw) {
  if (!kw) return
  const history = searchHistory.value.filter(h => h !== kw)
  history.unshift(kw)
  if (history.length > MAX_HISTORY) history.pop()
  searchHistory.value = history
  localStorage.setItem(HISTORY_KEY, JSON.stringify(history))
}

function removeHistory(item) {
  searchHistory.value = searchHistory.value.filter(h => h !== item)
  localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
}

function clearHistory() {
  searchHistory.value = []
  localStorage.removeItem(HISTORY_KEY)
}

function doSearch(kw) {
  router.push({ path: '/search', query: { q: kw } })
}

async function search(reset = false) {
  if (reset) {
    keyword.value = route.query.q || ''
    page.value = 1
    videos.value = []
  }
  
  if (!keyword.value) return
  
  // 保存到历史记录
  saveHistory(keyword.value)
  
  loading.value = true
  try {
    const params = {
      pageNo: page.value,
      pageSize: 20,
      title: keyword.value,
      status: 2
    }
    
    // 添加分类筛选
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    
    // 可以在后端支持排序后添加排序参数
    // params.orderBy = sortBy.value
    
    const { data } = await videoApi.getVideos(params)
    
    let list = data?.pageData || []
    
    // 前端排序（后端支持后可移除）
    if (sortBy.value === 'views') {
      list = list.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    } else if (sortBy.value === 'likes') {
      list = list.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
    }
    
    videos.value = reset ? list : [...videos.value, ...list]
    total.value = data?.total || 0
    hasMore.value = list.length === 20
  } catch (e) {
    console.error('搜索失败', e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  search(false)
}

onMounted(() => {
  loadHistory()
  loadCategories()
})

watch(() => route.query.q, () => {
  selectedCategory.value = null
  sortBy.value = 'latest'
  search(true)
}, { immediate: true })
</script>

<style scoped>
.portal-search {
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

.search-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 60px;
}

/* 搜索框区域 */
.search-box-wrapper {
  max-width: 800px;
  margin: 0 auto;
  padding: 80px 20px;
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 70px;
  color: #fff;
  background: linear-gradient(135deg, #fff, #FB7299);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #ddd;
  margin-bottom: 18px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 50px;
}

.search-tag {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  padding: 6px 16px;
}

.search-tag:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 4px 12px rgba(251, 114, 153, 0.3);
}

.search-tag.hot-rank {
  font-weight: 600;
}

.rank-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  font-size: 11px;
  margin-right: 6px;
}

.history-tag {
  background: rgba(255, 255, 255, 0.05);
}

/* 搜索结果 */
.search-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.header-main {
  display: flex;
  align-items: baseline;
  gap: 16px;
  flex-wrap: wrap;
}

.search-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.search-header h1 .keyword {
  color: #FB7299;
  font-weight: 700;
}

.result-count {
  color: #999;
  font-size: 14px;
  margin: 0;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(31, 33, 37, 0.8) 0%, rgba(22, 24, 29, 0.8) 100%);
  border-radius: 12px;
  margin-bottom: 28px;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  color: #9499a0;
  font-size: 14px;
  white-space: nowrap;
}

.filter-btn {
  transition: all 0.2s ease;
}

.filter-btn:hover {
  color: #FB7299;
}

/* 视频网格 */
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
  .search-container {
    padding: 30px 24px;
  }
}

@media (max-width: 768px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px 12px;
  }
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }
}

.empty-state, .loading-state {
  padding: 100px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.loading-text {
  color: #9499a0;
  margin-top: 16px;
}

.empty-tips {
  color: #666;
  margin-top: 10px;
}

.empty-tips a {
  color: #FB7299;
  text-decoration: none;
  transition: color 0.2s;
}

.empty-tips a:hover {
  color: #fc8bab;
  text-decoration: underline;
}

.load-more {
  padding: 50px;
  text-align: center;
}

/* 亮色模式 */
:global(.portal-layout.light-mode) .portal-search {
  background: #f4f5f7;
}

:global(.portal-layout.light-mode) .search-title {
  background: linear-gradient(135deg, #18191c, #FB7299);
  -webkit-background-clip: text;
  background-clip: text;
}

:global(.portal-layout.light-mode) .section-title {
  color: #18191c;
}

:global(.portal-layout.light-mode) .search-header h1 {
  color: #18191c;
}

:global(.portal-layout.light-mode) .filter-bar {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.08);
}

:global(.portal-layout.light-mode) .filter-label {
  color: #61666d;
}
</style>

