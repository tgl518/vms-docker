<template>
  <n-modal v-model:show="showModal" :mask-closable="true">
    <div class="auth-modal">
      <!-- 切换标签 -->
      <div class="auth-tabs">
        <button :class="{ active: mode === 'login' }" @click="mode = 'login'">登录</button>
        <button :class="{ active: mode === 'register' }" @click="mode = 'register'">注册</button>
      </div>

      <!-- 登录表单 -->
      <div v-if="mode === 'login'" class="auth-form">
        <div class="input-group">
          <n-icon size="20"><UserIcon /></n-icon>
          <input v-model="loginForm.username" type="text" placeholder="用户名" @keyup.enter="handleLogin" />
        </div>
        <div class="input-group">
          <n-icon size="20"><LockIcon /></n-icon>
          <input v-model="loginForm.password" type="password" placeholder="密码" @keyup.enter="handleLogin" />
        </div>
        <button class="submit-btn" :disabled="loading" @click="handleLogin">
          <n-spin v-if="loading" size="small" />
          <span v-else>登录</span>
        </button>
        <p class="tip">还没有账号？<a @click="mode = 'register'">立即注册</a></p>
      </div>

      <!-- 注册表单 -->
      <div v-if="mode === 'register'" class="auth-form">
        <div class="input-group">
          <n-icon size="20"><UserIcon /></n-icon>
          <input v-model="registerForm.username" type="text" placeholder="用户名" />
        </div>
        <div class="input-group">
          <n-icon size="20"><MailIcon /></n-icon>
          <input v-model="registerForm.email" type="email" placeholder="邮箱（可选）" />
        </div>
        <div class="input-group">
          <n-icon size="20"><LockIcon /></n-icon>
          <input v-model="registerForm.password" type="password" placeholder="密码" />
        </div>
        <div class="input-group">
          <n-icon size="20"><LockIcon /></n-icon>
          <input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" />
        </div>
        <button class="submit-btn" :disabled="loading" @click="handleRegister">
          <n-spin v-if="loading" size="small" />
          <span v-else>注册</span>
        </button>
        <p class="tip">已有账号？<a @click="mode = 'login'">去登录</a></p>
      </div>

      <!-- 关闭按钮 -->
      <button class="close-btn" @click="showModal = false">×</button>
    </div>
  </n-modal>
</template>

<script setup>
import { ref, h, watch } from 'vue'
import { NModal, NIcon, NSpin } from 'naive-ui'
import authApi from '@/api/auth'
import { useAuthStore, useUserStore } from '@/store'

// 图标
const UserIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z' })]) }
const LockIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z' })]) }
const MailIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z' })]) }

const props = defineProps({
  show: { type: Boolean, default: false }
})

const emit = defineEmits(['update:show', 'success'])

// 使用全局 $message 替代 useMessage()，避免在 NMessageProvider 上下文外调用
const authStore = useAuthStore()
const userStore = useUserStore()

const showModal = ref(false)
const mode = ref('login')
const loading = ref(false)

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ username: '', email: '', password: '', confirmPassword: '' })

// 同步 props
watch(() => props.show, (val) => { showModal.value = val })
watch(showModal, (val) => { emit('update:show', val) })

async function handleLogin() {
  if (!loginForm.value.username || !loginForm.value.password) {
    window.$message?.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const { data } = await authApi.login(loginForm.value)
    // 存储 token (后端返回 accessToken)
    authStore.setToken({ accessToken: data.accessToken })
    
    // 直接使用登录响应初始化用户信息（LoginVO 已包含完整用户数据）
    // 这样避免了需要先有 userId 才能调用 /user/info 的鸡生蛋问题
    const roles = data.roles || []
    const permissions = data.permissions || []
    userStore.setUser({
      id: data.userId,
      username: data.username,
      avatar: data.avatar,
      nickName: data.nickname,
      roles: roles,
      permissions: permissions,
    })
    
    window.$message?.success('登录成功')
    showModal.value = false
    emit('success')
    
    // 根据权限跳转不同页面
    if (permissions.includes('Dashboard') || roles.includes('admin') || roles.includes('SUPER_ADMIN')) {
      // 拥有仪表盘权限或管理员角色跳转后台
      window.location.href = '/#/admin'
    }
    // 普通用户留在当前页面
  } catch (e) {
    window.$message?.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.value.username || !registerForm.value.password) {
    window.$message?.warning('请输入用户名和密码')
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    window.$message?.warning('两次密码输入不一致')
    return
  }
  
  loading.value = true
  try {
    await authApi.register({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password,
      confirmPassword: registerForm.value.confirmPassword
    })
    window.$message?.success('注册成功，请登录')
    mode.value = 'login'
    loginForm.value.username = registerForm.value.username
  } catch (e) {
    window.$message?.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-modal {
  position: relative;
  width: 400px;
  background: rgba(30, 30, 40, 0.7);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 40px 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6), inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

/* 顶部光晕装饰 */
.auth-modal::before {
  content: '';
  position: absolute;
  top: -50px;
  left: -50px;
  width: 100px;
  height: 100px;
  background: #FB7299;
  filter: blur(60px);
  opacity: 0.3;
}

.auth-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.auth-tabs button {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.4);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 2px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.auth-tabs button.active {
  color: #fff;
  border-bottom-color: #FB7299;
  text-shadow: 0 0 10px rgba(251, 114, 153, 0.5);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 0 16px;
  transition: all 0.2s ease;
}

.input-group:focus-within {
  border-color: #FB7299;
  background: rgba(0, 0, 0, 0.35);
  box-shadow: 0 0 0 4px rgba(251, 114, 153, 0.1);
}

.input-group input {
  flex: 1;
  background: transparent;
  border: none;
  padding: 14px 0;
  color: #fff;
  font-size: 14px;
  outline: none;
}

.input-group input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.input-group .n-icon {
  color: rgba(255, 255, 255, 0.4);
  transition: color 0.2s;
}

.input-group:focus-within .n-icon {
  color: #FB7299;
}

.submit-btn {
  margin-top: 16px;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #FB7299 0%, #d84594 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(251, 114, 153, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(251, 114, 153, 0.5);
  filter: brightness(1.1);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  filter: grayscale(0.5);
}

.tip {
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
  margin-top: 8px;
}

.tip a {
  color: #FB7299;
  cursor: pointer;
  transition: color 0.2s;
}

.tip a:hover {
  color: #fc8bab;
  text-decoration: underline;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.5);
  font-size: 20px;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.2s;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #FB7299;
  color: #fff;
  transform: rotate(90deg);
}
</style>
