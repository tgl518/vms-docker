<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd()">
        <i class="i-material-symbols:add mr-4 text-18" />
        创建新用户
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      v-model:query-items="queryItems"
      :scroll-x="1200"
      :columns="columns"
      :get-data="api.read"
    >
      <MeQueryItem label="用户名" :label-width="50">
        <n-input
          v-model:value="queryItems.username"
          type="text"
          placeholder="请输入用户名"
          clearable
        />
      </MeQueryItem>

      <MeQueryItem label="邮箱" :label-width="50">
        <n-input
          v-model:value="queryItems.email"
          type="text"
          placeholder="请输入邮箱"
          clearable
        />
      </MeQueryItem>

      <MeQueryItem label="角色" :label-width="50">
        <n-select v-model:value="queryItems.role" clearable :options="roles" />
      </MeQueryItem>

      <MeQueryItem label="状态" :label-width="50">
        <n-select
          v-model:value="queryItems.status"
          clearable
          :options="[
            { label: '启用', value: 1 },
            { label: '禁用', value: 0 },
          ]"
        />
      </MeQueryItem>
    </MeCrud>

    <MeModal ref="modalRef" width="520px">
      <n-form
        ref="modalFormRef"
        label-placement="left"
        label-align="left"
        :label-width="80"
        :model="modalForm"
        :disabled="modalAction === 'view'"
      >
        <n-form-item
          v-if="['add', 'edit'].includes(modalAction)"
          label="用户名"
          path="username"
          :rule="{
            required: true,
            message: '请输入用户名',
            trigger: ['input', 'blur'],
          }"
        >
          <n-input v-model:value="modalForm.username" placeholder="请输入用户名" />
        </n-form-item>

        <n-form-item
          v-if="['add', 'reset'].includes(modalAction)"
          :label="modalAction === 'reset' ? '重置密码' : '初始密码'"
          path="password"
          :rule="{
            required: true,
            message: '请输入密码',
            trigger: ['input', 'blur'],
          }"
        >
          <n-input v-model:value="modalForm.password" type="password" show-password-on="mousedown" />
        </n-form-item>

        <n-form-item
          v-if="['edit'].includes(modalAction)"
          label="昵称"
          path="nickname"
        >
          <n-input v-model:value="modalForm.nickname" placeholder="请输入昵称" />
        </n-form-item>

        <n-form-item
          v-if="['edit'].includes(modalAction)"
          label="头像"
          path="avatar"
        >
          <n-input v-model:value="modalForm.avatar" placeholder="请输入头像URL" />
        </n-form-item>

        <n-form-item
          v-if="['add', 'edit'].includes(modalAction)"
          label="邮箱"
          path="email"
        >
          <n-input v-model:value="modalForm.email" />
        </n-form-item>

        <n-form-item
          v-if="['add', 'edit'].includes(modalAction)"
          label="手机号"
          path="phone"
        >
          <n-input v-model:value="modalForm.phone" />
        </n-form-item>

        <n-form-item v-if="['add', 'edit'].includes(modalAction)" label="角色" path="role">
          <n-select
            v-model:value="modalForm.role"
            :options="roles"
            clearable
          />
        </n-form-item>

        <n-form-item v-if="['add', 'edit'].includes(modalAction)" label="状态" path="status">
          <NSwitch v-model:value="modalForm.status" :checked-value="1" :unchecked-value="0">
            <template #checked>
              启用
            </template>
            <template #unchecked>
              禁用
            </template>
          </NSwitch>
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NAvatar, NButton, NSwitch, NTag } from 'naive-ui'
import { MeCrud, MeModal, MeQueryItem } from '@/components'
import { useCrud } from '@/composables'
import { formatDateTime } from '@/utils'
import api from './api'

defineOptions({ name: 'UserMgt' })

const $table = ref(null)
const queryItems = ref({})

onMounted(() => {
  $table.value?.handleSearch()
})

const roles = [
  { label: '超级管理员', value: 'SUPER_ADMIN' },
  { label: '管理员', value: 'admin' },
  { label: '普通用户', value: 'user' },
]

const {
  modalRef,
  modalFormRef,
  modalForm,
  modalAction,
  handleAdd,
  handleDelete,
  handleOpen,
  handleSave,
} = useCrud({
  name: '用户',
  initForm: { status: 1, role: 'user' },
  doCreate: api.create,
  doDelete: api.delete,
  doUpdate: api.update,
  refresh: () => $table.value?.handleSearch(),
})

const columns = [
  {
    title: '用户ID',
    key: 'userId',
    width: 80,
  },
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    render: ({ avatar }) =>
      h(NAvatar, {
        size: 'medium',
        src: avatar,
      }),
  },
  { title: '用户名', key: 'username', width: 150, ellipsis: { tooltip: true } },
  { title: '昵称', key: 'nickname', width: 150, ellipsis: { tooltip: true } },
  {
    title: '角色',
    key: 'roles',
    width: 120,
    render: ({ roles }) => {
      const roleList = roles || []
      let type = 'info'
      let label = '普通用户'
      if (roleList.includes('SUPER_ADMIN')) {
        type = 'error'
        label = '超级管理员'
      } else if (roleList.includes('admin')) {
        type = 'primary'
        label = '管理员'
      }
      return h(NTag, { type }, { default: () => label })
    },
  },
  { title: '邮箱', key: 'email', width: 150, ellipsis: { tooltip: true } },
  { title: '手机号', key: 'phone', width: 120, ellipsis: { tooltip: true } },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render(row) {
      return formatDateTime(row.createTime)
    },
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    render: row =>
      h(
        NSwitch,
        {
          size: 'small',
          rubberBand: false,
          value: row.status === 'ENABLED' || row.status === 1,
          loading: !!row.enableLoading,
          onUpdateValue: () => handleEnable(row),
        },
        {
          checked: () => '启用',
          unchecked: () => '停用',
        },
      ),
  },
  {
    title: '操作',
    key: 'actions',
    width: 320,
    align: 'right',
    fixed: 'right',
    render(row) {
      return [
        h(
          NButton,
          {
            size: 'small',
            type: 'primary',
            secondary: true,
            onClick: () => handleOpen({
              action: 'edit',
              title: '编辑用户',
              row: {
                ...row,
                id: row.userId,
                nickname: row.nickname,
                avatar: row.avatar,
                status: (row.status === 'ENABLED' || row.status === 1) ? 1 : 0,
              },
              onOk: handleSave,
            }),
          },
          {
            default: () => '编辑',
            icon: () => h('i', { class: 'i-material-symbols:edit-outline text-14' }),
          },
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'warning',
            style: 'margin-left: 12px;',
            onClick: () => handleOpen({
              action: 'reset',
              title: '重置密码',
              row: { ...row, id: row.userId },
              onOk: onSave,
            }),
          },
          {
            default: () => '重置密码',
            icon: () => h('i', { class: 'i-radix-icons:reset text-14' }),
          },
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            style: 'margin-left: 12px;',
            onClick: () => handleDelete(row.userId),
          },
          {
            default: () => '删除',
            icon: () => h('i', { class: 'i-material-symbols:delete-outline text-14' }),
          },
        ),
      ]
    },
  },
]

async function handleEnable(row) {
  row.enableLoading = true
  try {
    const newStatus = (row.status === 'ENABLED' || row.status === 1) ? 0 : 1
    await api.update({ id: row.userId, status: newStatus })
    row.status = newStatus === 1 ? 'ENABLED' : 'DISABLED'
    row.enableLoading = false
    $message.success('操作成功')
    $table.value?.handleSearch()
  }
  catch (error) {
    console.error(error)
    row.enableLoading = false
  }
}

function onSave() {
  if (modalAction.value === 'reset') {
    return handleSave({
      api: () => api.resetPwd(modalForm.value.id, modalForm.value),
      cb: () => $message.success('密码重置成功'),
    })
  }
  handleSave()
}
</script>
