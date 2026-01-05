<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd">
        <i class="i-material-symbols:add mr-4 text-18" />
        新增分类
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      :columns="columns"
      :get-data="api.getAllCategories"
    />

    <MeModal ref="modalRef" width="600px">
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
          :rule="{ required: true, message: '请输入分类名称', trigger: ['input', 'blur'] }"
        >
          <n-input v-model:value="modalForm.name" placeholder="请输入分类名称" />
        </n-form-item>

        <n-form-item label="别名" path="slug">
          <n-input v-model:value="modalForm.slug" placeholder="英文别名，用于URL" />
        </n-form-item>

        <n-form-item label="图标" path="icon">
          <ImageUpload
            v-model="modalForm.icon"
            :preview-width="80"
            :preview-height="80"
            hint="分类图标，建议 80x80 像素"
          />
        </n-form-item>

        <n-form-item label="描述" path="description">
          <n-input v-model:value="modalForm.description" type="textarea" placeholder="分类描述" />
        </n-form-item>

        <n-form-item label="排序" path="sort">
          <NInputNumber v-model:value="modalForm.sort" :min="0" />
        </n-form-item>

        <n-form-item label="状态" path="status">
          <n-select
            v-model:value="modalForm.status"
            :options="[
              { label: '启用', value: 1 },
              { label: '禁用', value: 0 },
            ]"
          />
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NAvatar, NButton, NInputNumber, NSwitch } from 'naive-ui'
import api from '@/api/video'
import { MeCrud, MeModal } from '@/components'
import { ImageUpload } from '@/components/common'
import { useCrud } from '@/composables'
import { formatDateTime } from '@/utils'

defineOptions({ name: 'CategoryList' })

const $table = ref(null)

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
  name: '分类',
  initForm: { sort: 0, status: 1 },
  doCreate: api.addCategory,
  doUpdate: data => api.updateCategory(data.id, data),
  doDelete: api.deleteCategory,
  refresh: () => $table.value?.handleSearch(),
})

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '图标',
    key: 'icon',
    width: 80,
    render: ({ icon }) => icon ? h(NAvatar, { size: 'small', src: icon, shape: 'square' }) : '-',
  },
  { title: '名称', key: 'name', width: 150 },
  { title: '别名', key: 'slug', width: 120, ellipsis: { tooltip: true } },
  { title: '排序', key: 'sort', width: 80 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: row => h(
      NSwitch,
      {
        value: row.status === 1,
        size: 'small',
        onUpdateValue: async (val) => {
          await api.updateCategory(row.id, { id: row.id, status: val ? 1 : 0 })
          $message.success('状态更新成功')
          $table.value?.handleSearch()
        },
      },
      {
        checked: () => '启用',
        unchecked: () => '禁用',
      },
    ),
  },
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
            onClick: () => handleOpen({ action: 'edit', title: '编辑分类', row, onOk: handleSave }),
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



