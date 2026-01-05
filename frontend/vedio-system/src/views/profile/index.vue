<template>
  <CommonPage title="个人信息">
    <n-grid :cols="24" :x-gap="24">
      <!-- 左侧：基本信息卡片 -->
      <n-gi :span="8">
        <n-card title="账号信息">
          <div class="profile-card">
            <div class="avatar-section" @click="avatarModalRef.open()">
              <n-avatar round :size="100" :src="userStore.avatar || defaultAvatar" />
              <div class="avatar-edit">
                <i class="i-fe:camera" />
              </div>
            </div>
            <div class="info-section">
              <div class="username">{{ userStore.username }}</div>
              <n-tag :type="roleType" size="small">{{ roleName }}</n-tag>
              <div class="nickname">{{ userStore.nickName || '暂无昵称' }}</div>
            </div>
          </div>
          <n-divider />
          <n-button type="primary" block @click="pwdModalRef.open()">
            <i class="i-fe:lock mr-8" />
            修改密码
          </n-button>
        </n-card>
      </n-gi>

      <!-- 右侧：详细资料 -->
      <n-gi :span="16">
        <n-card title="详细资料">
          <template #header-extra>
            <n-button type="primary" text @click="profileModalRef.open()">
              <i class="i-fe:edit mr-4" />
              编辑资料
            </n-button>
          </template>
          <n-descriptions label-placement="left" :label-style="{ width: '100px' }" :column="2" bordered>
            <n-descriptions-item label="用户名">{{ userStore.username }}</n-descriptions-item>
            <n-descriptions-item label="昵称">{{ userStore.nickName || '-' }}</n-descriptions-item>
            <n-descriptions-item label="邮箱">{{ userStore.userInfo?.email || '-' }}</n-descriptions-item>
            <n-descriptions-item label="手机">{{ userStore.userInfo?.phone || '-' }}</n-descriptions-item>
            <n-descriptions-item label="角色">{{ roleName }}</n-descriptions-item>
            <n-descriptions-item label="性别">
              {{ genderLabel }}
            </n-descriptions-item>
            <n-descriptions-item label="地址" :span="2">{{ userStore.userInfo?.location || '-' }}</n-descriptions-item>
            <n-descriptions-item label="个人简介" :span="2">
              {{ userStore.userInfo?.intro || '这个人很懒，什么都没写~' }}
            </n-descriptions-item>
          </n-descriptions>
        </n-card>
      </n-gi>
    </n-grid>

    <!-- 头像修改弹窗 -->
    <MeModal ref="avatarModalRef" width="400px" title="更换头像" @ok="handleAvatarSave()">
      <ImageUpload
        v-model="newAvatar"
        :preview-width="120"
        :preview-height="120"
        hint="支持 JPG、PNG、GIF 格式"
      />
    </MeModal>

    <!-- 修改密码弹窗 -->
    <MeModal ref="pwdModalRef" title="修改密码" width="400px" @ok="handlePwdSave()">
      <n-form ref="pwdFormRef" :model="pwdForm" label-placement="left" :label-width="80">
        <n-form-item label="原密码" path="oldPassword" :rule="required">
          <n-input v-model:value="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password-on="mousedown" />
        </n-form-item>
        <n-form-item label="新密码" path="newPassword" :rule="required">
          <n-input v-model:value="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password-on="mousedown" />
        </n-form-item>
      </n-form>
    </MeModal>

    <!-- 修改资料弹窗 -->
    <MeModal ref="profileModalRef" title="编辑资料" width="480px" @ok="handleProfileSave()">
      <n-form ref="profileFormRef" :model="profileForm" label-placement="left" :label-width="80">
        <n-form-item label="昵称" path="nickname">
          <n-input v-model:value="profileForm.nickname" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="性别" path="gender">
          <n-select v-model:value="profileForm.gender" :options="genders" placeholder="请选择性别" />
        </n-form-item>
        <n-form-item label="地址" path="location">
          <n-input v-model:value="profileForm.location" placeholder="请输入地址" />
        </n-form-item>
        <n-form-item label="简介" path="intro">
          <n-input v-model:value="profileForm.intro" type="textarea" placeholder="介绍一下自己吧" :rows="3" />
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { NGrid, NGi, NTabs, NTabPane, NTag, NDivider, NDescriptions, NDescriptionsItem } from 'naive-ui'
import { MeModal } from '@/components'
import { ImageUpload } from '@/components/common'
import { useForm, useModal } from '@/composables'
import { useUserStore } from '@/store'
import { getUserInfo } from '@/store/helper'
import api from './api'

defineOptions({ name: 'AdminProfile' })

const userStore = useUserStore()
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=guest'

const required = {
  required: true,
  message: '此为必填项',
  trigger: ['blur', 'change'],
}

const genders = [
  { label: '保密', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]

// 角色显示
const roleName = computed(() => {
  const roles = userStore.userInfo?.roles || []
  if (roles.includes('SUPER_ADMIN')) return '超级管理员'
  if (roles.includes('admin')) return '管理员'
  return '普通用户'
})

const roleType = computed(() => {
  const roles = userStore.userInfo?.roles || []
  if (roles.includes('SUPER_ADMIN')) return 'error'
  if (roles.includes('admin')) return 'primary'
  return 'info'
})

const genderLabel = computed(() => {
  const g = userStore.userInfo?.gender
  return genders.find(item => item.value === g)?.label ?? '保密'
})

// ============ 密码修改 ============
const [pwdModalRef] = useModal()
const [pwdFormRef, pwdForm, pwdValidation] = useForm()

async function handlePwdSave() {
  await pwdValidation()
  await api.changePassword(pwdForm.value)
  $message.success('密码修改成功')
  pwdForm.value = {}
}

// ============ 头像修改 ============
const newAvatar = ref('')
const [avatarModalRef] = useModal()

async function handleAvatarSave() {
  if (!newAvatar.value) {
    $message.error('请上传头像或输入头像地址')
    return false
  }
  await api.updateProfile({ avatar: newAvatar.value })
  $message.success('头像修改成功')
  await refreshUserInfo()
  newAvatar.value = ''
}

// ============ 资料修改 ============
const [profileModalRef] = useModal()
const [profileFormRef, profileForm, profileValidation] = useForm({
  nickname: userStore.nickName,
  gender: userStore.userInfo?.gender ?? 0,
  location: userStore.userInfo?.location,
  intro: userStore.userInfo?.intro,
})

async function handleProfileSave() {
  await profileValidation()
  await api.updateProfile(profileForm.value)
  $message.success('资料修改成功')
  await refreshUserInfo()
}

async function refreshUserInfo() {
  const user = await getUserInfo()
  userStore.setUser(user)
}

onMounted(() => {
  // 初始化表单数据
  profileForm.value = {
    nickname: userStore.nickName,
    gender: userStore.userInfo?.gender ?? 0,
    location: userStore.userInfo?.location,
    intro: userStore.userInfo?.intro,
  }
})
</script>

<style scoped>
.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-section {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-edit {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  font-size: 24px;
}

.avatar-section:hover .avatar-edit {
  opacity: 1;
}

.info-section {
  margin-top: 16px;
  text-align: center;
}

.username {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.nickname {
  margin-top: 8px;
  color: #999;
  font-size: 14px;
}
</style>
