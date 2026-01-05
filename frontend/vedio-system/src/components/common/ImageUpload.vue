<!--
  通用图片上传组件
  支持本地文件上传和URL输入
-->
<template>
  <div class="image-upload">
    <!-- 上传方式切换 -->
    <n-tabs v-model:value="uploadMode" type="segment" size="small" class="mb-12">
      <n-tab-pane name="file" tab="上传文件" />
      <n-tab-pane name="url" tab="网络链接" />
    </n-tabs>

    <!-- 本地文件上传 -->
    <div v-if="uploadMode === 'file'" class="upload-area">
      <n-upload
        :custom-request="handleUpload"
        :show-file-list="false"
        :accept="accept"
        :disabled="uploading"
        @before-upload="onBeforeUpload"
      >
        <n-upload-dragger class="dragger">
          <div v-if="!previewUrl" class="dragger-content">
            <i class="upload-icon i-fe:upload-cloud" />
            <p class="upload-text">点击或拖拽文件到此处上传</p>
            <p class="upload-hint">{{ hint }}</p>
          </div>
          <div v-else class="preview-container">
            <n-image
              :src="previewUrl"
              :width="previewWidth"
              :height="previewHeight"
              object-fit="cover"
              class="preview-image"
            />
            <div class="preview-overlay">
              <i class="i-fe:refresh-cw" />
              <span>重新上传</span>
            </div>
          </div>
        </n-upload-dragger>
      </n-upload>
      <n-progress
        v-if="uploading"
        type="line"
        :percentage="uploadProgress"
        :show-indicator="false"
        class="mt-8"
      />
    </div>

    <!-- URL 输入 -->
    <div v-else class="url-input-area">
      <n-input
        v-model:value="urlInput"
        placeholder="请输入图片URL（输入后自动保存）"
        clearable
        @update:value="handleUrlInput"
      />
      <!-- URL预览 -->
      <div v-if="previewUrl" class="url-preview">
        <n-image
          :src="previewUrl"
          :width="previewWidth"
          :height="previewHeight"
          object-fit="cover"
          class="preview-image"
          :fallback-src="'data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2250%22>🖼️</text></svg>'"
        />
      </div>
    </div>

    <!-- 删除按钮 -->
    <n-button
      v-if="previewUrl && !uploading"
      size="small"
      type="error"
      class="mt-8"
      @click.stop="handleClear"
    >
      <i class="i-fe:trash-2 mr-4" />
      删除图片
    </n-button>
  </div>
</template>

<script setup>
import { NTabs, NTabPane } from 'naive-ui'
import { request } from '@/utils'

defineOptions({ name: 'ImageUpload' })

const props = defineProps({
  modelValue: { type: String, default: '' },
  type: { type: String, default: 'image' },
  accept: { type: String, default: '.jpg,.jpeg,.png,.gif,.webp' },
  hint: { type: String, default: '支持 JPG、PNG、GIF、WebP 格式' },
  previewWidth: { type: [Number, String], default: 200 },
  previewHeight: { type: [Number, String], default: 120 },
})

const emit = defineEmits(['update:modelValue', 'change'])

// 状态
const uploadMode = ref('file')
const uploading = ref(false)
const uploadProgress = ref(0)
const urlInput = ref('')

// 同步外部值到 URL 输入框
watch(() => props.modelValue, (val) => {
  if (val && !val.includes('/media/')) {
    urlInput.value = val
  }
}, { immediate: true })

// 预览URL
const previewUrl = computed(() => props.modelValue)

// 更新值
function updateValue(url) {
  emit('update:modelValue', url)
  emit('change', url)
}

// 上传前校验
function onBeforeUpload({ file }) {
  const isImage = file.file?.type.startsWith('image/')
  const isVideo = file.file?.type.startsWith('video/')
  
  if (props.type === 'image' && !isImage) {
    $message.error('只能上传图片文件')
    return false
  }
  if (props.type === 'video' && !isVideo) {
    $message.error('只能上传视频文件')
    return false
  }
  return true
}

// 本地文件上传
async function handleUpload({ file, onFinish, onError }) {
  uploading.value = true
  uploadProgress.value = 0

  try {
    const formData = new FormData()
    formData.append('file', file.file)

    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 200)

    const endpoint = props.type === 'video' ? '/file/upload/video' : '/file/upload/image'
    const res = await request.post(endpoint, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })

    clearInterval(progressInterval)
    uploadProgress.value = 100

    if (res.code === 0 || res.code === 200) {
      const uploadedUrl = res.data?.url || res.data
      updateValue(uploadedUrl)
      $message.success('上传成功')
      onFinish()
    } else {
      throw new Error(res.message || '上传失败')
    }
  } catch (error) {
    $message.error(error.message || '上传失败')
    onError()
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// URL 输入实时更新
function handleUrlInput(url) {
  if (!url) {
    updateValue('')
    return
  }
  // 只同步有效的 URL
  if (url.startsWith('http://') || url.startsWith('https://')) {
    updateValue(url)
  }
}

// 清除
async function handleClear() {
  const currentUrl = props.modelValue
  
  updateValue('')
  urlInput.value = ''
  
  // 只有本地上传的文件才调用后端删除
  if (currentUrl && currentUrl.includes('/media/')) {
    try {
      await request.delete(`/file?url=${encodeURIComponent(currentUrl)}`)
    } catch (error) {
      // 删除失败不影响操作
    }
  }
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.dragger {
  padding: 24px;
  transition: all 0.3s;
}

.dragger:hover {
  border-color: var(--primary-color);
}

.dragger-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.upload-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.upload-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

.preview-container {
  position: relative;
  display: inline-block;
}

.preview-image {
  border-radius: 4px;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 4px;
  cursor: pointer;
}

.preview-container:hover .preview-overlay {
  opacity: 1;
}

.preview-overlay i {
  font-size: 24px;
  margin-bottom: 4px;
}

/* URL 输入区域 */
.url-input-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.url-input-row {
  display: flex;
  gap: 8px;
}

.url-input-row .n-input {
  flex: 1;
}

.url-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border: 1px dashed #e0e0e0;
  border-radius: 4px;
  background: #fafafa;
}

.url-hint {
  font-size: 12px;
  color: #999;
  margin: 0;
}
</style>
