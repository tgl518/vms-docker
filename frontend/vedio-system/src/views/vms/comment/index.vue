<template>
  <CommonPage>
    <template #action>
      <NButton v-if="currentView === 'comments'" @click="backToList">
        <i class="i-material-symbols:arrow-back mr-4 text-16" />
        返回视频列表
      </NButton>
    </template>

    <!-- 视频列表视图 -->
    <div v-if="currentView === 'list'">
      <MeCrud
        ref="$table"
        v-model:query-items="queryItems"
        :scroll-x="1000"
        :columns="videoColumns"
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
      </MeCrud>
    </div>

    <!-- 评论管理视图 -->
    <div v-else>
      <div class="mb-16 p-16 rounded-8" style="background: var(--n-color-embedded);">
        <div class="flex items-center gap-16">
          <NAvatar :size="80" :src="currentVideo.coverUrl" shape="square" />
          <div>
            <h3 class="text-18 font-bold mb-8">{{ currentVideo.title }}</h3>
            <p class="text-14 opacity-70">
              评论数：{{ currentVideo.commentCount || 0 }}
            </p>
          </div>
        </div>
      </div>

      <NDataTable
        :columns="commentColumns"
        :data="comments"
        :loading="loading"
        :pagination="pagination"
        :scroll-x="800"
      />

      <NEmpty v-if="comments.length === 0 && !loading" description="暂无评论" />
    </div>
  </CommonPage>
</template>

<script setup>
import { NAvatar, NButton, NDataTable, NEmpty, NPopconfirm, NTag } from 'naive-ui'
import videoApi from '@/api/video'
import commentApi from '@/api/comment'
import { MeCrud, MeQueryItem } from '@/components'
import { formatDateTime } from '@/utils'

defineOptions({ name: 'CommentList' })

// 当前视图状态
const currentView = ref('list')
const currentVideo = ref({})

// 视频列表相关
const $table = ref(null)
const queryItems = ref({})

// 评论列表相关
const comments = ref([])
const loading = ref(false)
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  onChange: (page) => {
    pagination.page = page
    fetchComments()
  },
})

// API wrapper - 复用视频API
const api = {
  getVideos: async (params) => {
    const result = await videoApi.getVideos(params)
    // 后端已经返回了 pageData 格式，直接返回即可
    return result
  },
}

// 组件挂载后自动加载视频列表
onMounted(() => {
  $table.value?.handleSearch()
})

// 进入评论管理视图
function handleManageComments(video) {
  currentVideo.value = video
  currentView.value = 'comments'
  pagination.page = 1
  fetchComments()
}

// 返回视频列表
function backToList() {
  currentView.value = 'list'
  currentVideo.value = {}
  comments.value = []
  // 刷新视频列表以更新评论数
  $table.value?.handleSearch()
}

// 获取评论列表
async function fetchComments() {
  if (!currentVideo.value.id) return
  
  loading.value = true
  try {
    const { data } = await commentApi.getCommentsByVideo(currentVideo.value.id, {
      pageNum: pagination.page,
      pageSize: pagination.pageSize,
    })
    comments.value = data?.records || []
    pagination.itemCount = data?.total || 0
  }
  catch (error) {
    console.error(error)
  }
  loading.value = false
}

// 删除评论
async function handleDelete(id) {
  try {
    await commentApi.deleteComment(id)
    $message.success('删除成功')
    await fetchComments()
    // 更新当前视频的评论数
    if (currentVideo.value.commentCount > 0) {
      currentVideo.value.commentCount--
    }
  }
  catch (error) {
    console.error(error)
  }
}

// 视频列表列配置
const videoColumns = [
  {
    title: '封面',
    key: 'coverUrl',
    width: 100,
    render: ({ coverUrl }) => h(NAvatar, { size: 'large', src: coverUrl, shape: 'square' }),
  },
  { title: '标题', key: 'title', width: 250, ellipsis: { tooltip: true } },
  {
    title: '评论数',
    key: 'commentCount',
    width: 100,
    render: ({ commentCount }) => h(NTag, { type: commentCount > 0 ? 'info' : 'default' }, { default: () => commentCount || 0 }),
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
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: row => formatDateTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    align: 'right',
    fixed: 'right',
    render(row) {
      return h(
        NButton,
        {
          size: 'small',
          type: 'primary',
          onClick: () => handleManageComments(row),
        },
        { default: () => '管理评论' },
      )
    },
  },
]

// 评论列表列配置
const commentColumns = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户ID', key: 'userId', width: 100 },
  { title: '评论内容', key: 'content', ellipsis: { tooltip: true } },
  { title: '点赞数', key: 'likeCount', width: 80 },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: ({ status }) => h(NTag, { type: status === 1 ? 'success' : 'error' }, { default: () => status === 1 ? '正常' : '隐藏' }),
  },
  {
    title: '评论时间',
    key: 'createTime',
    width: 180,
    render: row => formatDateTime(row.createTime),
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    align: 'right',
    fixed: 'right',
    render(row) {
      return h(
        NPopconfirm,
        {
          onPositiveClick: () => handleDelete(row.id),
        },
        {
          trigger: () => h(NButton, { size: 'small', type: 'error' }, { default: () => '删除' }),
          default: () => '确认删除此评论?',
        },
      )
    },
  },
]
</script>
