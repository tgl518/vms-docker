<template>
  <div class="portal-profile" :class="{ 'light-mode': !appStore.isDark }">
    <!-- 顶部占位 -->
    <div class="header-spacing"></div>

    <div class="profile-container">
      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <div class="avatar-wrapper" @click="showAvatarModal = true">
              <img :src="userAvatar" alt="头像" />
              <div class="avatar-overlay">
                <n-icon size="24"><CameraIcon /></n-icon>
              </div>
            </div>
            <div class="user-info">
              <h1 class="username">{{ userStore.nickName || userStore.username || '用户' }}</h1>
              <p class="user-email" v-if="userStore.userInfo?.email">
                <n-icon size="14"><EmailIcon /></n-icon>
                {{ userStore.userInfo.email }}
              </p>
            </div>
          </div>
          <div class="profile-actions">
            <n-button type="primary" @click="showEditModal = true">
              <template #icon><n-icon><EditIcon /></n-icon></template>
              编辑资料
            </n-button>
            <n-button @click="showPwdModal = true">
              <template #icon><n-icon><LockIcon /></n-icon></template>
              修改密码
            </n-button>
          </div>
        </div>

        <!-- 统计数据 -->
        <div class="stats-row">
          <div class="stat-item" @click="$router.push('/favorites')">
            <div class="stat-value">{{ stats.favorites }}</div>
            <div class="stat-label">收藏</div>
          </div>
          <div class="stat-item" @click="$router.push('/history')">
            <div class="stat-value">{{ stats.history }}</div>
            <div class="stat-label">历史</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.likes }}</div>
            <div class="stat-label">点赞</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.comments }}</div>
            <div class="stat-label">评论</div>
          </div>
        </div>
      </div>

      <!-- 个人信息详情 -->
      <div class="info-card">
        <h2 class="card-title">个人信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">昵称</span>
            <span class="info-value">{{ userStore.nickName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">性别</span>
            <span class="info-value">{{ getGenderText(userStore.userInfo?.gender) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">邮箱</span>
            <span class="info-value">{{ userStore.userInfo?.email || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机</span>
            <span class="info-value">{{ userStore.userInfo?.phone || '-' }}</span>
          </div>
          <div class="info-item full-width">
            <span class="info-label">地址</span>
            <span class="info-value">{{ userStore.userInfo?.address || '-' }}</span>
          </div>
          <div class="info-item full-width">
            <span class="info-label">个人简介</span>
            <span class="info-value bio">{{ userStore.userInfo?.intro || '这个人很懒，什么都没写~' }}</span>
          </div>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-links">
        <h2 class="card-title">快捷入口</h2>
        <div class="links-grid">
          <router-link to="/favorites" class="link-item">
            <div class="link-icon favorites">⭐</div>
            <span>我的收藏</span>
          </router-link>
          <router-link to="/history" class="link-item">
            <div class="link-icon history">📺</div>
            <span>观看历史</span>
          </router-link>
          <router-link to="/admin" class="link-item">
            <div class="link-icon admin">⚙️</div>
            <span>管理后台</span>
          </router-link>
          <div class="link-item" @click="handleLogout">
            <div class="link-icon logout">🚪</div>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <n-modal v-model:show="showEditModal" :mask-closable="false" preset="card" title="编辑资料" style="width: 500px;">
      <n-form ref="editFormRef" :model="editForm" label-placement="left" :label-width="80">
        <n-form-item label="昵称" path="nickName">
          <n-input v-model:value="editForm.nickName" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="性别" path="gender">
          <n-select
            v-model:value="editForm.gender"
            :options="genderOptions"
            placeholder="请选择性别"
          />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="editForm.email" placeholder="请输入邮箱" />
        </n-form-item>
        <n-form-item label="地址" path="address">
          <n-input v-model:value="editForm.address" placeholder="请输入地址" />
        </n-form-item>
        <n-form-item label="简介" path="intro">
          <n-input v-model:value="editForm.intro" type="textarea" placeholder="介绍一下自己吧" :rows="3" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showEditModal = false">取消</n-button>
          <n-button type="primary" @click="handleSaveProfile">保存</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 修改密码弹窗 -->
    <n-modal v-model:show="showPwdModal" :mask-closable="false" preset="card" title="修改密码" style="width: 420px;">
      <n-form ref="pwdFormRef" :model="pwdForm" label-placement="left" :label-width="80">
        <n-form-item label="原密码" path="oldPassword" :rule="{ required: true, message: '请输入原密码' }">
          <n-input v-model:value="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password-on="mousedown" />
        </n-form-item>
        <n-form-item label="新密码" path="newPassword" :rule="{ required: true, message: '请输入新密码' }">
          <n-input v-model:value="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password-on="mousedown" />
        </n-form-item>
        <n-form-item label="确认密码" path="confirmPassword" :rule="{ required: true, message: '请确认新密码' }">
          <n-input v-model:value="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password-on="mousedown" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showPwdModal = false">取消</n-button>
          <n-button type="primary" @click="handleChangePwd">确定</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 头像上传弹窗 -->
    <n-modal v-model:show="showAvatarModal" :mask-closable="false" preset="card" title="更换头像" style="width: 420px;">
      <ImageUpload
        v-model="newAvatarUrl"
        :preview-width="150"
        :preview-height="150"
        hint="支持 JPG、PNG、GIF、WebP 格式"
      />
      <template #footer>
        <n-space justify="end">
          <n-button @click="cancelAvatarUpload">取消</n-button>
          <n-button 
            type="primary" 
            :disabled="!newAvatarUrl"
            @click="saveAvatar"
          >
            保存
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NIcon, NModal, NForm, NFormItem, NInput, NSelect, NSpace } from 'naive-ui'
import { ImageUpload } from '@/components/common'
import { useAuthStore, useUserStore, useAppStore } from '@/store'
import userApi from '@/api/user'
import interactApi from '@/api/interact'

// 图标
const CameraIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M12 15.2A3.2 3.2 0 1 0 12 8.8a3.2 3.2 0 0 0 0 6.4z' }), h('path', { d: 'M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 15c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z' })]) }
const EmailIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z' })]) }
const EditIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34a.9959.9959 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z' })]) }
const LockIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z' })]) }
const UploadIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M9 16h6v-6h4l-7-7-7 7h4zm-4 2h14v2H5z' })]) }

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()
const appStore = useAppStore()

const userAvatar = computed(() => userStore.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${userStore.username || 'guest'}`)

const stats = ref({
  favorites: 0,
  history: 0,
  likes: 0,
  comments: 0
})

// 弹窗状态
const showEditModal = ref(false)
const showPwdModal = ref(false)
const showAvatarModal = ref(false)

// 头像上传
const newAvatarUrl = ref('')

// 编辑表单
const editFormRef = ref(null)
const editForm = ref({
  nickName: '',
  gender: 0,
  email: '',
  address: '',
  intro: ''
})

// 密码表单
const pwdFormRef = ref(null)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const genderOptions = [
  { label: '保密', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]

function getGenderText(gender) {
  const map = { 0: '保密', 1: '男', 2: '女' }
  return map[gender] ?? '保密'
}

function initEditForm() {
  editForm.value = {
    nickName: userStore.nickName || '',
    gender: userStore.userInfo?.gender ?? 0,
    email: userStore.userInfo?.email || '',
    address: userStore.userInfo?.address || '',
    intro: userStore.userInfo?.intro || ''
  }
}

async function handleSaveProfile() {
  try {
    await userApi.updateProfile(editForm.value)
    window.$message?.success('资料更新成功')
    showEditModal.value = false
    // 刷新用户信息
    await userStore.fetchUserInfo()
  } catch (err) {
    window.$message?.error(err.message || '保存失败')
  }
}

async function handleChangePwd() {
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    window.$message?.error('两次输入的密码不一致')
    return
  }
  try {
    await userApi.changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    window.$message?.success('密码修改成功')
    showPwdModal.value = false
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (err) {
    window.$message?.error(err.message || '修改失败')
  }
}

// 保存头像
async function saveAvatar() {
  if (!newAvatarUrl.value) {
    window.$message?.warning('请先上传或输入头像URL')
    return
  }
  
  try {
    await userApi.updateProfile({ avatar: newAvatarUrl.value })
    window.$message?.success('头像更新成功')
    showAvatarModal.value = false
    newAvatarUrl.value = ''
    await userStore.fetchUserInfo()
  } catch (error) {
    window.$message?.error(error.message || '保存失败')
  }
}

// 取消头像上传
function cancelAvatarUpload() {
  showAvatarModal.value = false
  newAvatarUrl.value = ''
}

function handleLogout() {
  authStore.resetLoginState()
  window.$message?.success('已退出登录')
  router.push('/')
}

onMounted(async () => {
  // 先获取用户信息，确保页面数据正确显示
  try {
    await userStore.fetchUserInfo()
  } catch (err) {
    console.error('获取用户信息失败', err)
  }
  
  initEditForm()
  
  // 加载用户统计数据
  try {
    const { data } = await interactApi.getUserStats()
    if (data) {
      stats.value = {
        favorites: data.favorites || 0,
        history: data.history || 0,
        likes: data.likes || 0,
        comments: data.comments || 0
      }
    }
  } catch (err) {
    console.error('加载统计数据失败', err)
  }
})
</script>

<style scoped>
.portal-profile {
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

.profile-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px 60px;
}

/* 用户信息卡片 */
.profile-card {
  background: linear-gradient(135deg, #25272d 0%, #1f2125 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 24px;
}

.avatar-section {
  display: flex;
  gap: 24px;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid #FB7299;
  box-shadow: 0 4px 20px rgba(251, 114, 153, 0.3);
}

.avatar-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.username {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

.user-id {
  font-size: 13px;
  color: #9499a0;
  margin: 0;
}

.user-email {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #9499a0;
  margin: 0;
}

.profile-actions {
  display: flex;
  gap: 12px;
}

/* 统计数据 */
.stats-row {
  display: flex;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(251, 114, 153, 0.1);
  transform: translateY(-2px);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #FB7299;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #9499a0;
}

/* 信息卡片 */
.info-card, .quick-links {
  background: linear-gradient(135deg, #1f2125 0%, #16181d 100%);
  border-radius: 16px;
  padding: 24px 32px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item.full-width {
  grid-column: span 2;
}

.info-label {
  font-size: 13px;
  color: #9499a0;
}

.info-value {
  font-size: 15px;
  color: #f1f2f3;
}

.info-value.bio {
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 快捷入口 */
.links-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  text-decoration: none;
  color: #f1f2f3;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.link-item:hover {
  background: rgba(251, 114, 153, 0.1);
  transform: translateY(-4px);
}

.link-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.05);
}

.link-icon.favorites { background: linear-gradient(135deg, rgba(255, 193, 7, 0.2), rgba(255, 152, 0, 0.1)); }
.link-icon.history { background: linear-gradient(135deg, rgba(33, 150, 243, 0.2), rgba(3, 169, 244, 0.1)); }
.link-icon.admin { background: linear-gradient(135deg, rgba(156, 39, 176, 0.2), rgba(103, 58, 183, 0.1)); }
.link-icon.logout { background: linear-gradient(135deg, rgba(244, 67, 54, 0.2), rgba(183, 28, 28, 0.1)); }

.link-item span {
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .avatar-section {
    flex-direction: column;
  }

  .profile-actions {
    width: 100%;
    justify-content: center;
  }

  .stats-row {
    flex-wrap: wrap;
  }

  .stat-item {
    min-width: calc(50% - 8px);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item.full-width {
    grid-column: span 1;
  }

  .links-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 头像上传弹窗样式 */
.avatar-upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-dragger {
  width: 200px;
  height: 200px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-dragger .dragger-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.avatar-dragger .upload-text {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin: 0;
}

.avatar-dragger .upload-hint {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin: 0;
}

.preview-avatar {
  position: relative;
  width: 100%;
  height: 100%;
}

.preview-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-avatar .preview-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.preview-avatar:hover .preview-overlay {
  opacity: 1;
}

.preview-avatar .preview-overlay span {
  font-size: 12px;
  margin-top: 4px;
}

/* 亮色模式 */
.portal-profile.light-mode {
  background: linear-gradient(180deg, #f4f5f7 0%, #f8f9fa 20%);
}

.portal-profile.light-mode .profile-card {
  background: #fff;
  border-color: rgba(0, 0, 0, 0.08);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.portal-profile.light-mode .username {
  color: #18191c;
}

.portal-profile.light-mode .info-card,
.portal-profile.light-mode .quick-links {
  background: #fff;
  border-color: rgba(0, 0, 0, 0.08);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.portal-profile.light-mode .card-title {
  color: #18191c;
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

.portal-profile.light-mode .info-value {
  color: #18191c;
}

.portal-profile.light-mode .stats-row {
  border-top-color: rgba(0, 0, 0, 0.08);
}

.portal-profile.light-mode .stat-item {
  background: rgba(0, 0, 0, 0.02);
}

.portal-profile.light-mode .stat-item:hover {
  background: rgba(251, 114, 153, 0.08);
}

.portal-profile.light-mode .link-item {
  background: rgba(0, 0, 0, 0.02);
  color: #18191c;
}

.portal-profile.light-mode .link-item:hover {
  background: rgba(251, 114, 153, 0.08);
}
</style>
