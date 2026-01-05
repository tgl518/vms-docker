<template>
  <n-config-provider :theme="appStore.isDark ? darkTheme : null" :locale="zhCN" :date-locale="dateZhCN" :theme-overrides="themeOverrides">
    <div class="portal-layout" :class="{ 'light-mode': !appStore.isDark }">
      <!-- 顶部导航 -->
      <header class="portal-header" :class="{ 'scrolled': isScrolled }">
        <div class="portal-header-inner">
          <!-- Logo -->
          <router-link to="/" class="portal-logo">
            <n-icon size="32" color="#FB7299">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2zm0 2v12h16V6H4zm6 2l6 4-6 4V8z"/>
              </svg>
            </n-icon>
            <span>VMS</span>
          </router-link>

          <!-- 分类导航 -->
          <nav class="portal-nav">
            <router-link to="/" class="portal-nav-item" :class="{ active: route.path === '/' }">
              首页
            </router-link>
            <router-link 
              v-for="cat in categories" 
              :key="cat.id" 
              :to="`/category/${cat.id}`"
              class="portal-nav-item"
              :class="{ active: route.path === `/category/${cat.id}` }"
            >
              {{ cat.name }}
            </router-link>
          </nav>

          <!-- 搜索框 -->
          <div class="portal-search">
            <div class="search-wrapper">
              <input 
                v-model="searchKeyword"
                type="text" 
                placeholder="搜索视频..." 
                @keyup.enter="handleSearch"
              />
              <button class="search-btn" @click="handleSearch">
                <n-icon size="18"><SearchIcon /></n-icon>
              </button>
            </div>
          </div>

          <!-- 右侧用户区 -->
          <div class="portal-user">
            <!-- 主题切换 -->
            <ToggleTheme />
            
            <template v-if="isLoggedIn">
              <!-- 已登录：显示头像和下拉菜单 -->
              <n-dropdown :options="userMenuOptions" @select="handleUserMenu">
                <div class="user-avatar">
                  <img :src="userAvatar" alt="User" />
                </div>
              </n-dropdown>
            </template>
            <template v-else>
              <!-- 未登录：显示登录/注册按钮 -->
              <n-button quaternary @click="showAuthModal = true">登录</n-button>
              <n-button type="primary" round @click="showAuthModal = true">注册</n-button>
            </template>
          </div>
        </div>
      </header>

      <!-- 登录弹窗 -->
      <AuthModal v-model:show="showAuthModal" @success="onLoginSuccess" />

      <!-- 主内容 -->
      <main class="portal-main">
        <router-view />
      </main>

      <!-- 页脚 -->
      <footer class="portal-footer">
        <div class="footer-content">
          <p>VMS视频管理系统 © {{ new Date().getFullYear() }}</p>
          <p class="slogan">哔哩哔哩 (゜-゜)つロ 干杯~</p>
        </div>
      </footer>
    </div>
  </n-config-provider>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NConfigProvider, NButton, NIcon, NDropdown, darkTheme, zhCN, dateZhCN } from 'naive-ui'
import videoApi from '@/api/video'
import { useAuthStore, useUserStore, useAppStore } from '@/store'
import AuthModal from '@/views/portal/components/AuthModal.vue'
import { ToggleTheme } from '@/components'

// 主题覆盖，使用粉色为主色调
const themeOverrides = {
  common: {
    primaryColor: '#FB7299',
    primaryColorHover: '#fc8bab',
    primaryColorPressed: '#d9547b',
  },
  Button: {
    textColor: '#FB7299',
  }
}

const SearchIcon = {
  render() {
    return h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [
      h('path', { d: 'M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z' })
    ])
  }
}

const route = useRoute()
const router = useRouter()
// 注意：useMessage() 必须在 NConfigProvider 内部使用，这里使用全局 $message
const authStore = useAuthStore()
const userStore = useUserStore()
const appStore = useAppStore()

const categories = ref([])
const searchKeyword = ref('')
const isScrolled = ref(false)
const showAuthModal = ref(false)

// 用户状态
const isLoggedIn = computed(() => !!authStore.accessToken)
const userAvatar = computed(() => userStore.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${userStore.username || 'guest'}`)

// 用户下拉菜单选项
const userMenuOptions = computed(() => {
  const options = [
    { label: userStore.nickName || userStore.username || '用户', key: 'profile', disabled: true },
    { type: 'divider' },
    { label: '个人中心', key: 'user-center' },
    { label: '我的收藏', key: 'favorites' },
    { label: '观看历史', key: 'history' },
    { type: 'divider' },
  ]
  
  // 检查是否有进入后端的权限 (拥有任意管理权限或本身是管理员角色)
  const hasAdminAccess = userStore.permissions.length > 0 || 
                         userStore.roles.includes('admin') || 
                         userStore.roles.includes('SUPER_ADMIN')
                         
  if (hasAdminAccess) {
    options.push({ label: '管理后台', key: 'admin' })
    options.push({ type: 'divider' })
  }
  
  options.push({ label: '退出登录', key: 'logout' })
  return options
})

function handleUserMenu(key) {
  switch (key) {
    case 'user-center':
      router.push('/profile')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'history':
      router.push('/history')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      authStore.resetLoginState()
      window.$message.success('已退出登录')
      break
  }
}

async function onLoginSuccess() {
  // 登录成功后获取用户信息
  try {
    await userStore.fetchUserInfo()
  } catch (e) {
    console.error('获取用户信息失败', e)
  }
  window.$message.success('欢迎回来！')
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 0
}

onMounted(async () => {
  window.addEventListener('scroll', handleScroll)
  
  // 如果已登录，获取用户信息
  if (isLoggedIn.value) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }
  
  try {
    const { data } = await videoApi.getEnabledCategories()
    categories.value = (data || []).slice(0, 8)
  } catch (e) {
    console.error('加载分类失败', e)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value } })
  }
}

function goAdmin() {
  router.push('/admin')
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  background: linear-gradient(125deg, #0f1115 0%, #1e1e2d 40%, #0f1115 100%);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* Header */
.portal-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  z-index: 1000;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: #0f1115; /* 改为实色背景，移除透明度和虚化 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.portal-header.scrolled {
  background: #090a0d; /* 滚动后稍微深一点点 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.portal-header-inner {
  max-width: 1700px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 30px;
}

/* Logo */
.portal-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
  font-weight: 800;
  color: #fff;
  text-decoration: none;
  letter-spacing: -0.5px;
}
.portal-logo span {
  display: block;
}

/* Nav */
.portal-nav {
  display: flex;
  gap: 4px;
  margin-left: 10px;
}

.portal-nav-item {
  padding: 0 16px;
  height: 64px;
  line-height: 64px;
  color: #e5e5e5;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  position: relative;
  transition: color 0.2s;
}

.portal-nav-item:hover {
  color: #fff;
  animation: bounce 0.3s;
}

.portal-nav-item.active {
  color: #FB7299;
}

.portal-nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #FB7299;
  border-radius: 2px;
}

/* Search */
.portal-search {
  flex: 1;
  display: flex;
  justify-content: center;
}

.search-wrapper {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 36px;
  display: flex;
  align-items: center;
  background: #25272d;
  border: 1px solid #383a42;
  border-radius: 8px;
  transition: all 0.2s;
}

.search-wrapper:hover,
.search-wrapper:focus-within {
  background: #2f323a; /* 优化暗色模式下的交互颜色，不直接变纯白 */
  border-color: #FB7299;
}

.search-wrapper input {
  flex: 1;
  background: transparent;
  border: none;
  padding: 0 12px;
  color: #ccc;
  font-size: 14px;
  outline: none;
}

.search-wrapper:hover input,
.search-wrapper:focus-within input {
  color: #333;
}

.search-btn {
  width: 40px;
  height: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #757575;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 8px 8px 0;
  transition: color 0.2s;
}

.search-btn:hover {
  color: #FB7299;
}

/* User Area */
.portal-user {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.user-avatar:hover {
  border-color: #FB7299;
  transform: scale(1.1);
}

.user-avatar img {
  width: 100%;
  height: 100%;
}

.upload-btn {
  background-color: #FB7299 !important;
  border: none;
  padding: 0 24px;
  height: 36px;
  font-weight: 600;
}
.upload-btn:hover {
  background-color: #fc8bab !important;
}

/* Main */
.portal-main {
  /* padding-top: 64px; Handled by pages usually, but for fixed header we need spacing if no banner */
}

/* Footer */
.portal-footer {
  padding: 40px 0;
  background: #0f1115;
  border-top: 1px solid #202228;
  text-align: center;
  margin-top: 60px;
}

.footer-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: #61666d;
}

.slogan {
  font-size: 12px;
  opacity: 0.6;
}

/* Responsive */
@media (max-width: 1024px) {
  .portal-nav {
    display: none;
  }
}

/* =================================================================
   Light Mode Styles
   ================================================================= */
.portal-layout.light-mode {
  background: #f4f5f7; /* Bilibili 亮色背景 */
  animation: none; /* 移除暗色渐变动画 */
  color: #18191c;
}

/* Header in Light Mode */
.portal-layout.light-mode .portal-header {
  background: #ffffff; /* 亮色模式改为实白 */
  border-bottom: 1px solid #e3e5e7;
}

.portal-layout.light-mode .portal-header.scrolled {
  background: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e3e5e7;
}

/* Logo & Text */
.portal-layout.light-mode .portal-logo {
  color: #FB7299; /* 保持主题色，或者用深色 #18191c */
}

/* Nav Items */
.portal-layout.light-mode .portal-nav-item {
  color: #18191c;
}
.portal-layout.light-mode .portal-nav-item:hover {
  color: #FB7299;
}
.portal-layout.light-mode .portal-nav-item.active {
  color: #FB7299;
}

/* Search Box */
.portal-layout.light-mode .search-wrapper {
  background: #f1f2f3;
  border-color: #e3e5e7;
}
.portal-layout.light-mode .search-wrapper:hover,
.portal-layout.light-mode .search-wrapper:focus-within {
  background: #fff;
  border-color: #FB7299;
}
.portal-layout.light-mode .search-wrapper input {
  color: #18191c;
}
.portal-layout.light-mode .search-wrapper:hover input,
.portal-layout.light-mode .search-wrapper:focus-within input {
  color: #18191c;
}

/* Footer */
.portal-layout.light-mode .portal-footer {
  background: #f6f9fa;
  border-top: 1px solid #e3e5e7;
}
</style>
