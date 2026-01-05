<template>
  <CommonPage>
    <template #action>
      <NButton type="primary" @click="handleAdd">
        <i class="i-material-symbols:add mr-4 text-18" />
        新增轮播图
      </NButton>
    </template>

    <MeCrud
      ref="$table"
      :columns="columns"
      :get-data="api.getAllBanners"
    />

    <MeModal ref="modalRef" width="600px">
      <n-form
        ref="modalFormRef"
        label-placement="left"
        label-align="left"
        :label-width="100"
        :model="modalForm"
      >
        <n-form-item label="标题" path="title">
          <n-input v-model:value="modalForm.title" placeholder="轮播图标题" />
        </n-form-item>

        <!-- 链接类型选择 -->
        <n-form-item label="链接类型" path="linkType">
          <n-radio-group v-model:value="modalForm.linkType" @update:value="handleLinkTypeChange">
            <n-radio-button :value="0">外链</n-radio-button>
            <n-radio-button :value="1">视频链接</n-radio-button>
          </n-radio-group>
        </n-form-item>

        <!-- 外链输入框 -->
        <n-form-item v-if="modalForm.linkType === 0" label="跳转链接" path="linkUrl">
          <n-input v-model:value="modalForm.linkUrl" placeholder="点击跳转的URL" />
        </n-form-item>

        <!-- 视频选择器 -->
        <n-form-item v-else label="选择视频" path="targetId">
          <n-select
            v-model:value="modalForm.targetId"
            :options="videoOptions"
            :loading="loadingVideos"
            filterable
            placeholder="搜索并选择视频"
            @update:value="handleVideoSelect"
          />
        </n-form-item>

        <!-- 图片上传/显示 -->
        <n-form-item
          label="图片"
          path="imgUrl"
          :rule="{ required: true, message: '请上传图片或选择视频', trigger: ['input', 'blur'] }"
        >
          <ImageUpload
            v-model="modalForm.imgUrl"
            :preview-width="240"
            :preview-height="100"
            :hint="modalForm.linkType === 1 ? '选择视频后自动使用视频封面' : '支持 JPG、PNG、GIF、WebP 格式，可上传图片或输入URL'"
          />
        </n-form-item>

        <n-form-item label="排序" path="sort">
          <NInputNumber v-model:value="modalForm.sort" :min="0" />
        </n-form-item>

        <n-form-item label="是否展示" path="isShow">
          <NSwitch v-model:value="modalForm.isShow" :checked-value="1" :unchecked-value="0">
            <template #checked>展示</template>
            <template #unchecked>隐藏</template>
          </NSwitch>
        </n-form-item>
      </n-form>
    </MeModal>
  </CommonPage>
</template>

<script setup>
import { NButton, NImage, NInputNumber, NRadioButton, NRadioGroup, NSelect, NSwitch } from 'naive-ui'
import api from '@/api/banner'
import videoApi from '@/api/video'
import { MeCrud, MeModal } from '@/components'
import { ImageUpload } from '@/components/common'
import { useCrud } from '@/composables'
import { formatDateTime } from '@/utils'

defineOptions({ name: 'BannerList' })

const $table = ref(null)

// 视频列表相关
const videoList = ref([])
const loadingVideos = ref(false)

// 将视频列表转换为下拉选项格式
const videoOptions = computed(() =>
  videoList.value.map(v => ({
    label: v.title,
    value: v.id,
    coverUrl: v.coverUrl, // 保存封面URL用于自动填充
  })),
)

// 加载视频列表
async function loadVideos() {
  if (videoList.value.length > 0)
    return // 已加载过则跳过
  loadingVideos.value = true
  try {
    const res = await videoApi.getVideos({ pageNo: 1, pageSize: 1000 })
    // 后端返回格式为 { pageData: [...], total: ... }
    videoList.value = res.data?.pageData || res.data?.list || res.data?.records || []
  }
  catch (e) {
    console.error('加载视频列表失败:', e)
  }
  finally {
    loadingVideos.value = false
  }
}

// 链接类型改变时
function handleLinkTypeChange(type) {
  if (type === 1) {
    // 切换到视频链接，加载视频列表
    loadVideos()
    modalForm.value.linkUrl = ''
  }
  else {
    // 切换到外链，清空视频相关字段
    modalForm.value.targetId = null
  }
}

// 选择视频时，自动填充图片和链接
function handleVideoSelect(videoId) {
  if (!videoId)
    return
  const video = videoOptions.value.find(v => v.value === videoId)
  if (video) {
    // 自动使用视频封面
    modalForm.value.imgUrl = video.coverUrl || ''
    // 设置跳转链接为视频详情页
    modalForm.value.linkUrl = `/video/${videoId}`
  }
}

// 组件挂载后自动加载数据
onMounted(() => {
  $table.value?.handleSearch()
})

const {
  modalRef,
  modalFormRef,
  modalForm,
  handleAdd: originalHandleAdd,
  handleDelete,
  handleOpen: originalHandleOpen,
  handleSave,
} = useCrud({
  name: '轮播图',
  initForm: { sort: 0, isShow: 1, linkType: 0, targetId: null },
  doCreate: api.addBanner,
  doUpdate: data => api.updateBanner(data.id, data),
  doDelete: api.deleteBanner,
  refresh: () => $table.value?.handleSearch(),
})

// 包装 handleAdd，预加载视频列表
function handleAdd() {
  loadVideos()
  originalHandleAdd()
}

// 包装 handleOpen，编辑时加载视频列表
function handleOpen(options) {
  loadVideos()
  // 如果编辑时有 targetId，设置 linkType 为视频链接
  if (options.row?.targetId) {
    options.row.linkType = 1
  }
  else {
    options.row.linkType = options.row?.linkType ?? 0
  }
  originalHandleOpen(options)
}

const columns = [
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '图片',
    key: 'imgUrl',
    width: 200,
    render: ({ imgUrl }) => h(NImage, { src: imgUrl, width: 160, height: 80, objectFit: 'cover' }),
  },
  { title: '标题', key: 'title', width: 150, ellipsis: { tooltip: true } },
  { title: '跳转链接', key: 'linkUrl', width: 200, ellipsis: { tooltip: true } },
  { title: '排序', key: 'sort', width: 80 },
  {
    title: '展示状态',
    key: 'isShow',
    width: 100,
    render: row => h(
      NSwitch,
      {
        value: row.isShow === 1,
        size: 'small',
        onUpdateValue: async (val) => {
          await api.updateBanner(row.id, { id: row.id, isShow: val ? 1 : 0 })
          $message.success('状态更新成功')
          $table.value?.handleSearch()
        },
      },
      {
        checked: () => '展示',
        unchecked: () => '隐藏',
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
    width: 180,
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
            onClick: () => handleOpen({ action: 'edit', title: '编辑轮播图', row, onOk: handleSave }),
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
