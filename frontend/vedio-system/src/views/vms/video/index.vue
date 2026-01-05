<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd">
        <i class="i-material-symbols:add mr-4 text-18" />
        新增视频
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      v-model:query-items="queryItems"
      :scroll-x="1200"
      :columns="columns"
      :get-data="api.getVideos"
    >
      <MeQueryItem label="标题" :label-width="50">
        <n-input
          v-model:value="queryItems.title"
          type="text"
          placeholder="请输入标题"
          clearable
        />
      </MeQueryItem>

      <MeQueryItem label="分类" :label-width="50">
        <n-select
          v-model:value="queryItems.categoryId"
          clearable
          :options="categories"
          label-field="name"
          value-field="id"
          placeholder="请选择分类"
        />
      </MeQueryItem>

      <MeQueryItem label="状态" :label-width="50">
        <n-select
          v-model:value="queryItems.status"
          clearable
          :options="[
            { label: '草稿', value: 0 },
            { label: '审核中', value: 1 },
            { label: '已发布', value: 2 },
            { label: '已下架', value: 3 },
          ]"
        />
      </MeQueryItem>
    </MeCrud>

    <MeModal ref="modalRef" width="800px">
      <n-form
        ref="modalFormRef"
        label-placement="left"
        label-align="left"
        :label-width="100"
        :model="modalForm"
      >
        <n-form-item
          label="视频标题"
          path="title"
          :rule="{ required: true, message: '请输入视频标题', trigger: ['input', 'blur'] }"
        >
          <n-input v-model:value="modalForm.title" />
        </n-form-item>

        <n-form-item
          label="简介"
          path="intro"
        >
          <n-input v-model:value="modalForm.intro" type="textarea" />
        </n-form-item>

        <n-form-item
          label="封面图"
          path="coverUrl"
          :rule="{ required: true, message: '请上传封面图', trigger: ['input', 'blur'] }"
        >
          <ImageUpload
            v-model="modalForm.coverUrl"
            :preview-width="200"
            :preview-height="120"
            hint="支持 JPG、PNG、GIF、WebP 格式"
          />
        </n-form-item>

        <n-form-item
          label="视频文件"
          path="videoUrl"
          :rule="{ required: true, message: '请上传视频文件', trigger: ['input', 'blur'] }"
        >
          <VideoUpload
            v-model="modalForm.videoUrl"
            :preview-width="320"
            :preview-height="180"
            hint="支持 MP4、AVI、MKV、MOV、WebM 格式"
          />
        </n-form-item>

        <n-form-item label="视频时长(秒)" path="duration">
          <n-input-number
            v-model:value="modalForm.duration"
            :min="0"
            :max="86400"
            placeholder="请输入视频时长（秒）"
            style="width: 200px"
          >
            <template #suffix>秒</template>
          </n-input-number>
          <span class="duration-hint">
            {{ formatDurationHint(modalForm.duration) }}
          </span>
        </n-form-item>

        <n-form-item
          label="分类"
          path="categoryId"
          :rule="{ required: true, type: 'number', message: '请选择分类', trigger: ['blur', 'change'] }"
        >
          <n-select
            v-model:value="modalForm.categoryId"
            :options="categories"
            label-field="name"
            value-field="id"
            filterable
            placeholder="请选择分类"
          />
        </n-form-item>

        <n-form-item label="标签" path="tagIds">
          <n-select
            v-model:value="modalForm.tagIds"
            :options="tags"
            label-field="name"
            value-field="id"
            multiple
            filterable
            placeholder="请选择标签（可多选）"
          />
        </n-form-item>

        <!-- 状态选择 -->
        <n-form-item label="状态" path="status">
          <n-select
            v-model:value="modalForm.status"
            :options="[
              { label: '草稿', value: 0 },
              { label: '审核中', value: 1 },
              { label: '已发布', value: 2 },
              { label: '已下架', value: 3 },
            ]"
          />
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NAvatar, NButton, NInputNumber, NTag } from 'naive-ui'
import api from '@/api/video'
import { MeCrud, MeModal, MeQueryItem } from '@/components'
import { ImageUpload, VideoUpload } from '@/components/common'
import { useCrud } from '@/composables'
import { formatDateTime, request } from '@/utils'

defineOptions({ name: 'VideoList' })

const $table = ref(null)
const queryItems = ref({})
const categories = ref([])
const tags = ref([])

// 格式化时长提示（秒 -> 分:秒）
function formatDurationHint(seconds) {
  if (!seconds || seconds === 0) return ''
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `≈ ${mins}分${secs}秒`
}

onMounted(async () => {
  // 初始加载分类和标签列表
  await loadCategories()
  await loadTags()
  
  $table.value?.handleSearch()
})

// 每次从其他页面返回时刷新分类列表，确保分类名称实时同步
onActivated(async () => {
  await loadCategories()
})

// 加载分类列表（获取所有分类，确保禁用分类的视频也能正确显示分类名）
async function loadCategories() {
  const { data: catData } = await api.getAllCategories()
  categories.value = catData?.pageData || catData || []
}

// 加载标签列表
async function loadTags() {
  const { data: tagData } = await api.getAllTags()
  tags.value = tagData?.pageData || tagData || []
}

const {
  modalRef,
  modalFormRef,
  modalForm,
  handleAdd: _handleAdd,
  handleDelete,
  handleOpen,
  handleSave,
} = useCrud({
  name: '视频',
  initForm: { status: 0, tagIds: [], categoryId: null, duration: 0 },
  doCreate: api.addVideo,
  doUpdate: data => api.updateVideo(data.id, data),
  doDelete: api.deleteVideo,
  refresh: () => $table.value?.handleSearch(),
})

// 记录原始表单数据（用于判断取消时是否有新上传的文件）
const originalForm = ref({})

/**
 * 取消时清理已上传但未保存的文件
 */
async function cleanupUploads(formData) {
  const filesToDelete = []
  
  // 检查封面图是否是新上传的（与原始数据不同且包含/media/）
  if (formData.coverUrl && formData.coverUrl !== originalForm.value.coverUrl && formData.coverUrl.includes('/media/')) {
    filesToDelete.push(formData.coverUrl)
  }
  
  // 检查视频是否是新上传的
  if (formData.videoUrl && formData.videoUrl !== originalForm.value.videoUrl && formData.videoUrl.includes('/media/')) {
    filesToDelete.push(formData.videoUrl)
  }
  
  // 批量删除文件
  for (const url of filesToDelete) {
    try {
      await request.delete(`/file?url=${encodeURIComponent(url)}`)
    } catch (error) {
      // 删除失败不影响操作
    }
  }
}

/**
 * 新增视频（带清理回调）
 */
function handleAdd() {
  originalForm.value = {} // 新增时原始表单为空
  handleOpen({
    action: 'add',
    title: '新增视频',
    row: { status: 0, tagIds: [], categoryId: null, duration: 0 },
    onOk: handleSave,
    onCancel: cleanupUploads // 取消时清理新上传的文件
  })
}

/**
 * 编辑视频（带清理回调）
 */
async function handleEdit(row) {
  try {
    // 获取视频详情（包含tags）
    const { data } = await api.getVideoDetail(row.id)
    const videoData = data?.video || row
    const videoTags = data?.tags || []
    
    // 提取标签ID列表
    const tagIds = videoTags.map(t => t.id)
    
    // 合并数据
    const rowWithTags = { ...videoData, tagIds }
    
    originalForm.value = { ...rowWithTags } // 记录原始数据
    handleOpen({ 
      action: 'edit', 
      title: '编辑视频', 
      row: rowWithTags, 
      onOk: handleSave,
      onCancel: cleanupUploads // 取消时清理新上传的文件
    })
  } catch (error) {
    console.error('获取视频详情失败', error)
    // 降级处理：使用原始row数据
    originalForm.value = { ...row, tagIds: [] }
    handleOpen({ 
      action: 'edit', 
      title: '编辑视频', 
      row: { ...row, tagIds: [] }, 
      onOk: handleSave,
      onCancel: cleanupUploads
    })
  }
}

const columns = [
  {
    title: '封面',
    key: 'coverUrl',
    width: 100,
    render: ({ coverUrl }) => h(NAvatar, { size: 'large', src: coverUrl, shape: 'square' }),
  },
  { title: '标题', key: 'title', width: 200, ellipsis: { tooltip: true } },
  { title: '简介', key: 'intro', width: 200, ellipsis: { tooltip: true } },
  {
    title: '分类',
    key: 'categoryId',
    width: 100,
    render: ({ categoryId }) => {
      const cat = categories.value.find(c => c.id === categoryId)
      return cat ? h(NTag, { type: 'info' }, { default: () => cat.name }) : '未知'
    },
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: ({ status }) => {
      const statusMap = {
        0: { type: 'default', label: '草稿' },
        1: { type: 'warning', label: '审核中' },
        2: { type: 'success', label: '已发布' },
        3: { type: 'error', label: '已下架' },
      }
      const s = statusMap[status] || { type: 'default', label: status ?? '未知' }
      return h(NTag, { type: s.type }, { default: () => s.label })
    },
  },
  {
    title: '播放量',
    key: 'viewCount',
    width: 100,
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
            onClick: () => handleEdit(row),
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

<style scoped>
.duration-hint {
  margin-left: 12px;
  color: #999;
  font-size: 13px;
}
</style>
