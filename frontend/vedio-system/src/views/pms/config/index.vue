<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd()">
        <i class="i-material-symbols:add mr-4 text-18" />
        新增配置
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      v-model:query-items="queryItems"
      :scroll-x="1200"
      :columns="columns"
      :get-data="api.read"
    >
      <MeQueryItem label="配置键" :label-width="60">
        <n-input
          v-model:value="queryItems.configKey"
          type="text"
          placeholder="请输入配置键"
          clearable
        />
      </MeQueryItem>

      <MeQueryItem label="类型" :label-width="50">
        <n-select
          v-model:value="queryItems.configType"
          clearable
          :options="configTypes"
        />
      </MeQueryItem>
    </MeCrud>

    <MeModal ref="modalRef" width="600px">
      <n-form
        ref="modalFormRef"
        label-placement="left"
        label-align="left"
        :label-width="80"
        :model="modalForm"
        :disabled="modalAction === 'view'"
      >
        <n-form-item
          label="配置键"
          path="configKey"
          :rule="{
            required: true,
            message: '请输入配置键',
            trigger: ['input', 'blur'],
          }"
        >
          <n-input 
            v-model:value="modalForm.configKey" 
            :disabled="modalAction === 'edit'"
            placeholder="例如: site_name"
          />
        </n-form-item>

        <n-form-item
          label="配置值"
          path="configValue"
          :rule="{
            required: true,
            message: '请输入配置值',
            trigger: ['input', 'blur'],
          }"
        >
          <n-input 
            v-model:value="modalForm.configValue" 
            type="textarea"
            :rows="3"
            placeholder="配置的值"
          />
        </n-form-item>

        <n-form-item label="类型" path="configType">
          <n-select
            v-model:value="modalForm.configType"
            :options="configTypes"
          />
        </n-form-item>

        <n-form-item label="描述" path="description">
          <n-input 
            v-model:value="modalForm.description" 
            type="textarea"
            :rows="2"
            placeholder="配置说明"
          />
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NButton, NTag } from 'naive-ui'
import { MeCrud, MeModal, MeQueryItem } from '@/components'
import { useCrud } from '@/composables'
import { formatDateTime } from '@/utils'
import api from './api'

defineOptions({ name: 'ConfigMgt' })

const $table = ref(null)
const queryItems = ref({})

onMounted(() => {
  $table.value?.handleSearch()
})

// 配置类型选项
const configTypes = [
  { label: '字符串', value: 'STRING' },
  { label: '数字', value: 'NUMBER' },
  { label: '布尔', value: 'BOOLEAN' },
  { label: 'JSON', value: 'JSON' },
]

const {
  modalRef,
  modalFormRef,
  modalForm,
  modalAction,
  handleAdd,
  handleDelete,
  handleEdit,
} = useCrud({
  name: '配置',
  initForm: { configType: 'STRING' },
  doCreate: api.create,
  doDelete: api.delete,
  doUpdate: api.update,
  refresh: () => $table.value?.handleSearch(),
})

const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80,
  },
  { 
    title: '配置键', 
    key: 'configKey', 
    width: 200, 
    ellipsis: { tooltip: true } 
  },
  { 
    title: '配置值', 
    key: 'configValue', 
    width: 250, 
    ellipsis: { tooltip: true } 
  },
  {
    title: '类型',
    key: 'configType',
    width: 100,
    render: ({ configType }) => {
      const typeMap = {
        STRING: { label: '字符串', type: 'info' },
        NUMBER: { label: '数字', type: 'success' },
        BOOLEAN: { label: '布尔', type: 'warning' },
        JSON: { label: 'JSON', type: 'primary' },
      }
      const t = typeMap[configType] || { label: configType, type: 'default' }
      return h(NTag, { type: t.type, size: 'small' }, { default: () => t.label })
    },
  },
  { 
    title: '描述', 
    key: 'description', 
    width: 200, 
    ellipsis: { tooltip: true } 
  },
  {
    title: '系统内置',
    key: 'isSystem',
    width: 100,
    render: ({ isSystem }) => 
      h(NTag, { type: isSystem ? 'error' : 'default', size: 'small' }, 
        { default: () => isSystem ? '是' : '否' }
      ),
  },
  {
    title: '更新时间',
    key: 'updateTime',
    width: 180,
    render(row) {
      return formatDateTime(row.updateTime)
    },
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
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
            onClick: () => handleEdit(row),
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
            type: 'error',
            style: 'margin-left: 12px;',
            disabled: row.isSystem === 1,
            onClick: () => handleDelete(row.id),
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
</script>
