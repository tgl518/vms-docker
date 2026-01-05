<template>
  <div class="wh-full flex-col bg-[url(@/assets/images/login_bg.webp)] bg-cover">
    <div
      class="m-auto max-w-700 min-w-345 f-c-c rounded-8 auto-bg bg-opacity-20 bg-cover p-12 card-shadow"
    >
      <div class="hidden w-380 px-20 py-35 md:block">
        <img src="@/assets/images/login_banner.webp" class="w-full" alt="login_banner">
      </div>

      <div class="w-320 flex-col px-20 py-32">
        <h2 class="f-c-c text-24 text-#6a6a6a font-normal">
          <img src="@/assets/images/logo.png" class="mr-12 h-50">
          {{ title }}
        </h2>

        <!-- Tab 切换 -->
        <n-tabs v-model:value="activeTab" type="segment" class="mt-24">
          <n-tab name="login">
            登录
          </n-tab>
          <n-tab name="register">
            注册
          </n-tab>
        </n-tabs>

        <!-- 登录表单 -->
        <template v-if="activeTab === 'login'">
          <n-input
            v-model:value="loginForm.username"
            autofocus
            class="mt-24 h-40 items-center"
            placeholder="请输入用户名"
            :maxlength="20"
          >
            <template #prefix>
              <i class="i-fe:user mr-12 opacity-20" />
            </template>
          </n-input>
          <n-input
            v-model:value="loginForm.password"
            class="mt-16 h-40 items-center"
            type="password"
            show-password-on="mousedown"
            placeholder="请输入密码"
            :maxlength="20"
            @keydown.enter="handleLogin()"
          >
            <template #prefix>
              <i class="i-fe:lock mr-12 opacity-20" />
            </template>
          </n-input>

          <n-checkbox
            class="mt-16"
            :checked="isRemember"
            label="记住我"
            :on-update:checked="(val) => (isRemember = val)"
          />

          <n-button
            class="mt-20 h-40 w-full rounded-5 text-16"
            type="primary"
            :loading="loading"
            @click="handleLogin()"
          >
            登录
          </n-button>

          <div class="mt-12 text-center text-14 opacity-60">
            还没有账号？
            <n-button text type="primary" @click="activeTab = 'register'">
              立即注册
            </n-button>
          </div>
        </template>

        <!-- 注册表单 -->
        <template v-else>
          <n-input
            v-model:value="registerForm.username"
            autofocus
            class="mt-24 h-40 items-center"
            placeholder="请输入用户名"
            :maxlength="20"
          >
            <template #prefix>
              <i class="i-fe:user mr-12 opacity-20" />
            </template>
          </n-input>

          <n-input
            v-model:value="registerForm.nickname"
            class="mt-16 h-40 items-center"
            placeholder="请输入昵称 (可选)"
            :maxlength="20"
          >
            <template #prefix>
              <i class="i-fe:smile mr-12 opacity-20" />
            </template>
          </n-input>

          <n-input
            v-model:value="registerForm.password"
            class="mt-16 h-40 items-center"
            type="password"
            show-password-on="mousedown"
            placeholder="请输入密码"
            :maxlength="20"
          >
            <template #prefix>
              <i class="i-fe:lock mr-12 opacity-20" />
            </template>
          </n-input>

          <n-input
            v-model:value="registerForm.confirmPassword"
            class="mt-16 h-40 items-center"
            type="password"
            show-password-on="mousedown"
            placeholder="请确认密码"
            :maxlength="20"
            @keydown.enter="handleRegister()"
          >
            <template #prefix>
              <i class="i-fe:lock mr-12 opacity-20" />
            </template>
          </n-input>

          <n-button
            class="mt-20 h-40 w-full rounded-5 text-16"
            type="primary"
            :loading="loading"
            @click="handleRegister()"
          >
            注册
          </n-button>

          <div class="mt-12 text-center text-14 opacity-60">
            已有账号？
            <n-button text type="primary" @click="activeTab = 'login'">
              立即登录
            </n-button>
          </div>
        </template>
      </div>
    </div>

    <TheFooter class="py-12" />
  </div>
</template>

<script setup>
import { useStorage } from '@vueuse/core'
import { useAuthStore } from '@/store'
import { lStorage } from '@/utils'
import api from './api'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const title = import.meta.env.VITE_TITLE

// Tab状态
const activeTab = ref('login')

// 登录表单
const loginForm = ref({
  username: '',
  password: '',
})

// 注册表单
const registerForm = ref({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
})

// 记住我
const isRemember = useStorage('isRemember', true)
const loading = ref(false)

// 初始化登录表单（记住的账号）
const localLoginInfo = lStorage.get('loginInfo')
if (localLoginInfo) {
  loginForm.value.username = localLoginInfo.username || ''
  loginForm.value.password = localLoginInfo.password || ''
}

// 登录处理
async function handleLogin() {
  const { username, password } = loginForm.value
  if (!username || !password) {
    return $message.warning('请输入用户名和密码')
  }

  try {
    loading.value = true
    $message.loading('正在登录...', { key: 'login' })
    const { data } = await api.login({ username, password })

    // 记住账号
    if (isRemember.value) {
      lStorage.set('loginInfo', { username, password })
    }
    else {
      lStorage.remove('loginInfo')
    }

    onLoginSuccess(data)
  }
  catch (error) {
    $message.destroy('login')
    console.error(error)
  }
  loading.value = false
}

// 注册处理
async function handleRegister() {
  const { username, nickname, password, confirmPassword } = registerForm.value

  if (!username) {
    return $message.warning('请输入用户名')
  }
  if (!password) {
    return $message.warning('请输入密码')
  }
  if (password.length < 6) {
    return $message.warning('密码长度不能少于6位')
  }
  if (password !== confirmPassword) {
    return $message.warning('两次密码输入不一致')
  }

  try {
    loading.value = true
    $message.loading('正在注册...', { key: 'register' })
    await api.register({ username, nickname, password, confirmPassword })
    $message.success('注册成功，请登录', { key: 'register' })

    // 自动填充登录表单
    loginForm.value.username = username
    loginForm.value.password = password
    activeTab.value = 'login'
  }
  catch (error) {
    $message.destroy('register')
    console.error(error)
  }
  loading.value = false
}

// 登录成功处理
async function onLoginSuccess(data = {}) {
  authStore.setToken(data)
  $message.success('登录成功', { key: 'login' })

  if (route.query.redirect) {
    const path = route.query.redirect
    delete route.query.redirect
    router.push({ path, query: route.query })
  }
  else {
    // 如果包含 Dashboard 权限或管理类角色，跳转到后台管理
    const roles = data.roles || []
    const permissions = data.permissions || []
    if (permissions.includes('Dashboard') || roles.includes('admin') || roles.includes('SUPER_ADMIN')) {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  }
}
</script>
