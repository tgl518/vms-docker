<template>
  <div class="comment-section" :class="{ 'light-mode': !appStore.isDark }">
    <!-- 评论区标题 -->
    <div class="section-header">
      <h3>评论 <span class="comment-count">({{ total }})</span></h3>
    </div>

    <!-- 发表评论区域 -->
    <div class="comment-input-area" v-if="isLoggedIn">
      <div class="user-avatar">
        <img :src="userAvatar" alt="用户头像" />
      </div>
      <div class="input-wrapper">
        <div class="textarea-container">
          <textarea 
            ref="textareaRef"
            v-model="newComment"
            :placeholder="replyTo ? `回复 @${replyTo.username}...` : '善语结善缘，恶言伤人心'"
            @focus="showActions = true"
            @keydown.ctrl.enter="submitComment"
          />
          
          <!-- 表情选择器 -->
          <EmojiPicker 
            :visible="showEmoji"
            @select="insertEmoji"
          />
        </div>
        
        <!-- 工具栏和提交按钮 -->
        <div class="input-actions" v-show="showActions || newComment">
          <div class="left-tools">
            <button class="tool-btn" @click.stop="showEmoji = !showEmoji" :class="{ active: showEmoji }">
              <n-icon size="20"><EmojiIcon /></n-icon>
              <span>表情</span>
            </button>
            <button class="tool-btn" @click="showImageUploader = !showImageUploader" :class="{ active: showImageUploader }">
              <n-icon size="20"><ImageIcon /></n-icon>
              <span>图片</span>
            </button>
          </div>
          <div class="right-actions">
            <span class="char-count" :class="{ 'over-limit': newComment.length > 1000 }">
              {{ newComment.length }}/1000
            </span>
            <n-button 
              v-if="replyTo"
              quaternary
              size="small"
              @click="cancelReply"
            >取消回复</n-button>
            <n-button 
              type="primary" 
              size="small"
              :disabled="!newComment.trim() || newComment.length > 1000"
              :loading="submitting"
              @click="submitComment"
            >
              {{ replyTo ? '回复' : '发布' }}
            </n-button>
          </div>
        </div>
        
        <!-- 图片上传区 -->
        <ImageUploader 
          v-if="showImageUploader"
          ref="imageUploaderRef"
          :max-count="3"
          @change="handleImagesChange"
        />
      </div>
    </div>
    
    <!-- 未登录提示 -->
    <div class="login-tip" v-else>
      <p>登录后才能发表评论哦~</p>
      <n-button type="primary" size="small" @click="$emit('needLogin')">立即登录</n-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length">
      <div 
        v-for="comment in comments" 
        :key="comment.id" 
        class="comment-item"
      >
        <!-- 主评论 -->
        <div class="comment-main">
          <div class="comment-avatar">
            <img :src="getAvatar(comment)" alt="" />
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <span class="username">{{ comment.nickname || '用户' + comment.userId }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content" v-html="renderContent(comment.content)"></div>
            
            <!-- 评论图片 -->
            <div class="comment-images" v-if="comment.images && comment.images.length">
              <img 
                v-for="(img, idx) in comment.images" 
                :key="idx" 
                :src="img" 
                @click="previewImage(comment.images, idx)"
              />
            </div>
            
            <div class="comment-actions">
              <button 
                class="action-btn" 
                :class="{ liked: comment.isLiked }"
                @click="toggleLikeComment(comment)"
              >
                <n-icon><LikeIcon /></n-icon>
                <span>{{ comment.likeCount || '' }}</span>
              </button>
              <button class="action-btn" @click="startReply(comment)">
                <n-icon><ReplyIcon /></n-icon>
                <span>回复</span>
              </button>
              <button 
                v-if="comment.userId === currentUserId" 
                class="action-btn delete"
                @click="deleteComment(comment)"
              >
                <n-icon><DeleteIcon /></n-icon>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 子评论（回复） -->
        <div class="reply-list" v-if="comment.replies && comment.replies.length">
          <div 
            v-for="reply in comment.replies" 
            :key="reply.id" 
            class="reply-item"
          >
            <div class="comment-avatar small">
              <img :src="getAvatar(reply)" alt="" />
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="username">{{ reply.nickname || '用户' + reply.userId }}</span>
                <span v-if="reply.replyUserId" class="reply-to">
                  回复 <span class="reply-target">@{{ reply.replyNickname }}</span>
                </span>
                <span class="time">{{ formatTime(reply.createTime) }}</span>
              </div>
              <div class="comment-content" v-html="renderContent(reply.content)"></div>
              <div class="comment-actions">
                <button 
                  class="action-btn" 
                  :class="{ liked: reply.isLiked }"
                  @click="toggleLikeComment(reply)"
                >
                  <n-icon><LikeIcon /></n-icon>
                  <span>{{ reply.likeCount || '' }}</span>
                </button>
                <button class="action-btn" @click="startReply(comment, reply)">
                  <n-icon><ReplyIcon /></n-icon>
                  <span>回复</span>
                </button>
                <button 
                  v-if="reply.userId === currentUserId" 
                  class="action-btn delete"
                  @click="deleteComment(reply)"
                >
                  <n-icon><DeleteIcon /></n-icon>
                  <span>删除</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">💬</div>
      <p>还没有评论，快来抢沙发吧~</p>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore && comments.length">
      <n-button quaternary :loading="loading" @click="loadMore">加载更多</n-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h, nextTick, watch } from 'vue'
import { NIcon, NButton } from 'naive-ui'
import interactApi from '@/api/interact'
import { useAuthStore, useUserStore, useAppStore } from '@/store'
import EmojiPicker from './EmojiPicker.vue'
import ImageUploader from './ImageUploader.vue'

// 图标组件
const EmojiIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z' })]) }
const ImageIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z' })]) }
const LikeIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z' })]) }
const ReplyIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M10 9V5l-7 7 7 7v-4.1c5 0 8.5 1.6 11 5.1-1-5-4-10-11-11z' })]) }
const DeleteIcon = { render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [h('path', { d: 'M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z' })]) }

const props = defineProps({
  videoId: { type: [Number, String], required: true }
})

const emit = defineEmits(['needLogin'])
const authStore = useAuthStore()
const userStore = useUserStore()
const appStore = useAppStore()

const textareaRef = ref(null)
const imageUploaderRef = ref(null)

// 状态
const comments = ref([])
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const pageNo = ref(1)
const pageSize = 10

const newComment = ref('')
const showActions = ref(false)
const showEmoji = ref(false)
const showImageUploader = ref(false)
const submitting = ref(false)
const uploadedImages = ref([])

const replyTo = ref(null)       // 回复的评论对象
const replyParent = ref(null)   // 回复的父级评论（用于嵌套）

// 计算属性
const isLoggedIn = computed(() => !!authStore.accessToken)
const currentUserId = computed(() => userStore.userId)
const userAvatar = computed(() => userStore.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${userStore.username || 'guest'}`)

// 点击其他区域关闭表情选择器
function handleClickOutside(e) {
  if (showEmoji.value && !e.target.closest('.tool-btn')) {
    showEmoji.value = false
  }
}

onMounted(() => {
  loadComments()
  document.addEventListener('click', handleClickOutside)
})

// 加载评论
async function loadComments() {
  if (loading.value) return
  loading.value = true
  
  try {
    const { data } = await interactApi.getComments(props.videoId, {
      pageNo: pageNo.value,
      pageSize
    })
    
    const list = data?.pageData || []
    total.value = data?.total || 0
    
    if (pageNo.value === 1) {
      comments.value = list
    } else {
      comments.value.push(...list)
    }
    
    hasMore.value = comments.value.length < total.value
  } catch (err) {
    console.error('加载评论失败', err)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  pageNo.value++
  loadComments()
}

// 插入表情
function insertEmoji(emoji) {
  const textarea = textareaRef.value
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const text = newComment.value
    newComment.value = text.slice(0, start) + emoji + text.slice(end)
    
    nextTick(() => {
      textarea.focus()
      textarea.setSelectionRange(start + emoji.length, start + emoji.length)
    })
  } else {
    newComment.value += emoji
  }
  showEmoji.value = false
}

// 图片变化
function handleImagesChange(urls) {
  uploadedImages.value = urls
}

// 开始回复
function startReply(comment, reply = null) {
  if (!isLoggedIn.value) {
    emit('needLogin')
    return
  }
  
  replyParent.value = comment
  replyTo.value = reply || comment
  showActions.value = true
  
  nextTick(() => {
    textareaRef.value?.focus()
  })
}

// 取消回复
function cancelReply() {
  replyTo.value = null
  replyParent.value = null
}

// 提交评论
async function submitComment() {
  if (!newComment.value.trim()) return
  if (!isLoggedIn.value) {
    emit('needLogin')
    return
  }

  submitting.value = true
  
  try {
    const payload = {
      videoId: Number(props.videoId),
      content: newComment.value.trim(),
      parentId: replyParent.value?.id || null,
      replyUserId: replyTo.value?.userId || null
    }
    
    // 如果有图片，序列化到content中（简化处理）
    if (uploadedImages.value.length) {
      payload.content += '\n[images]' + JSON.stringify(uploadedImages.value) + '[/images]'
    }

    await interactApi.addComment(payload)
    
    window.$message?.success(replyTo.value ? '回复成功' : '评论成功')
    
    // 清空输入
    newComment.value = ''
    uploadedImages.value = []
    imageUploaderRef.value?.clear()
    showImageUploader.value = false
    cancelReply()
    
    // 刷新评论列表
    pageNo.value = 1
    await loadComments()
    
  } catch (err) {
    window.$message?.error(err.message || '发表失败')
  } finally {
    submitting.value = false
  }
}

// 点赞评论
async function toggleLikeComment(comment) {
  if (!isLoggedIn.value) {
    emit('needLogin')
    return
  }
  
  try {
    await interactApi.toggleLike('comment', comment.id)
    // 乐观更新
    comment.isLiked = !comment.isLiked
    comment.likeCount = (comment.likeCount || 0) + (comment.isLiked ? 1 : -1)
  } catch (err) {
    window.$message?.error('操作失败')
  }
}

// 删除评论
async function deleteComment(comment) {
  try {
    await interactApi.deleteComment(comment.id)
    window.$message?.success('删除成功')
    
    // 刷新列表
    pageNo.value = 1
    await loadComments()
  } catch (err) {
    window.$message?.error(err.message || '删除失败')
  }
}

// 渲染内容（处理表情和图片标记）
function renderContent(content) {
  if (!content) return ''
  
  // 提取图片
  let text = content.replace(/\[images\](.*?)\[\/images\]/g, '')
  
  // 转义HTML（安全处理）
  text = text.replace(/</g, '&lt;').replace(/>/g, '&gt;')
  
  // 换行
  text = text.replace(/\n/g, '<br>')
  
  return text
}

// 解析图片
function parseImages(content) {
  if (!content) return []
  const match = content.match(/\[images\](.*?)\[\/images\]/)
  if (match) {
    try {
      return JSON.parse(match[1])
    } catch (e) {}
  }
  return []
}

// 格式化时间
function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = (now - date) / 1000
  
  if (diff < 60) return '刚刚'
  if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
  if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
  if (diff < 2592000) return Math.floor(diff / 86400) + '天前'
  
  return date.toLocaleDateString()
}

// 获取头像（优先使用后端返回的 avatar，没有则使用 dicebear 生成）
function getAvatar(comment) {
  if (comment?.avatar) {
    return comment.avatar
  }
  // fallback: 使用 dicebear 生成头像
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${comment?.nickname || comment?.userId || 'guest'}`
}

// 预览图片
function previewImage(images, idx) {
  // 可以集成图片预览组件
  window.open(images[idx], '_blank')
}

// 监听videoId变化重新加载
watch(() => props.videoId, () => {
  pageNo.value = 1
  comments.value = []
  loadComments()
})
</script>

<style scoped>
.comment-section {
  padding: 20px 0;
  min-height: 400px; /* 增加最小高度，避免底部太挤 */
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 500;
  color: #fff;
  margin: 0;
}
.comment-section.light-mode .section-header h3 {
  color: #18191c;
}

.comment-count {
  color: #999;
  font-weight: normal;
}

/* 评论输入区 */
.comment-input-area {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.input-wrapper {
  flex: 1;
}

.textarea-container {
  position: relative;
}

.textarea-container textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px 16px;
  background: #1f2125;
  border: 1px solid #3a3c42;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.2s;
}

/* 亮色模式输入框 */
.comment-section.light-mode .textarea-container textarea {
  background: #f6f7f8;
  border-color: #e3e5e7;
  color: #18191c;
}
.comment-section.light-mode .textarea-container textarea:focus {
  background: #fff;
  border-color: #FB7299;
}

.textarea-container textarea:focus {
  outline: none;
  border-color: #FB7299;
  background: #25272d;
}

.textarea-container textarea::placeholder {
  color: #666;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.left-tools {
  display: flex;
  gap: 8px;
}

.tool-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: transparent;
  border: 1px solid #3a3c42;
  border-radius: 6px;
  color: #999;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

/* 亮色工具按钮 */
.comment-section.light-mode .tool-btn {
  border-color: #e3e5e7;
  color: #61666d;
}
.comment-section.light-mode .tool-btn:hover {
  background: #f6f7f8;
}

.tool-btn:hover,
.tool-btn.active {
  border-color: #FB7299;
  color: #FB7299;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.char-count {
  font-size: 12px;
  color: #666;
}

.char-count.over-limit {
  color: #f56c6c;
}

/* 登录提示 */
.login-tip {
  padding: 32px;
  text-align: center;
  background: #1f2125;
  border-radius: 8px;
  margin-bottom: 32px;
}
.comment-section.light-mode .login-tip {
  background: #f6f7f8;
}
.comment-section.light-mode .login-tip p {
  color: #61666d;
}

.login-tip p {
  color: #999;
  margin-bottom: 12px;
}

/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  padding-bottom: 24px;
  border-bottom: 1px solid #222;
}

/* 亮色分割线 */
.comment-section.light-mode .comment-item {
  border-bottom-color: #e3e5e7;
}

.comment-main {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.comment-avatar.small {
  width: 32px;
  height: 32px;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #FB7299;
}

.reply-to {
  font-size: 13px;
  color: #999;
}

.reply-target {
  color: #FB7299;
}

.time {
  font-size: 12px;
  color: #666;
}

.comment-content {
  font-size: 14px;
  line-height: 1.8;
  color: #ddd;
  word-break: break-word;
}

/* 亮色评论内容 */
.comment-section.light-mode .comment-content {
  color: #18191c;
}

.comment-images {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.comment-images img {
  max-width: 200px;
  max-height: 150px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.comment-images img:hover {
  transform: scale(1.02);
}

.comment-actions {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: transparent;
  border: none;
  color: #999;
  font-size: 13px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.comment-section.light-mode .action-btn {
  color: #9499a0;
}
.comment-section.light-mode .action-btn:hover {
  color: #FB7299;
  background: #f1f2f3;
}

.action-btn:hover {
  color: #FB7299;
  background: rgba(251, 114, 153, 0.1);
}

.action-btn.liked {
  color: #FB7299;
}

.action-btn.delete:hover {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

/* 回复列表 */
.reply-list {
  margin-left: 64px;
  margin-top: 16px;
  padding: 16px;
  background: #16181d;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 亮色回复背景 */
.comment-section.light-mode .reply-list {
  background: #f6f7f8;
}

.reply-item {
  display: flex;
  gap: 12px;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  color: #666;
}

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: 24px;
}
</style>
