<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd">
        <i class="i-material-symbols:add mr-4 text-18" />
        新增标签
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      :columns="columns"
      :get-data="api.getAllTags"
    />

    <MeModal ref="modalRef" width="500px">
      <n-form
        ref="modalFormRef"
        label-placement="left"
        label-align="left"
        :label-width="80"
        :model="modalForm"
      >
        <n-form-item
          label="名称"
          path="name"
          :rule="{ required: true, message: '请输入标签名称', trigger: ['input', 'blur'] }"
        >
          <n-input v-model:value="modalForm.name" placeholder="请输入标签名称" />
        </n-form-item>

        <n-form-item label="颜色" path="color">
          <n-color-picker
            v-model:value="modalForm.color"
            :swatches="tagColors"
            :show-alpha="false"
          />
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NButton, NTag } from 'naive-ui'
import api from '@/api/video'
import { MeCrud, MeModal } from '@/components'
import { useCrud } from '@/composables'
import { formatDateTime } from '@/utils'

defineOptions({ name: 'TagList' })

const $table = ref(null)

// 预设标签颜色
const tagColors = [
  '#18a058', '#2080f0', '#f0a020', '#d03050',
  '#8a2be2', '#ff6b6b', '#4ecdc4', '#45b7d1',
]

// 组件挂载后自动加载数据
onMounted(() => {
  $table.value?.handleSearch()
})

const {
  modalRef,
  modalFormRef,
  modalForm,
  handleAdd,
  handleDelete,
  handleOpen,
  handleSave,
} = useCrud({
  name: '标签',
  initForm: { color: '#18a058' },
  doCreate: api.addTag,
  doUpdate: data => api.updateTag(data.id, data),
  doDelete: api.deleteTag,
  refresh: () => $table.value?.handleSearch(),
})

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '标签',
    key: 'name',
    width: 150,
    render: ({ name, color }) => h(NTag, { 
      color: color ? { color, textColor: '#fff' } : undefined,
      size: 'small',
      round: true,
    }, { default: () => name }),
  },
  { title: '使用次数', key: 'useCount', width: 100 },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: row => formatDateTime(row.createTime),
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
            onClick: () => handleOpen({ action: 'edit', title: '编辑标签', row, onOk: handleSave }),
          },
          { default: () => '编辑' },
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            style: 'margin-left: 12px;',
            onClick: () => handleDelete(row.id),
          },
          { default: () => '删除' },
        ),
      ]
    },
  },
]
</script>

