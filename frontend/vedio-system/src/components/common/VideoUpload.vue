<!--
  通用视频上传组件
  仅支持本地文件上传
-->
<template>
  <div class="video-upload">
    <!-- 本地文件上传 -->
    <div class="upload-area">
      <n-upload
        :custom-request="handleUpload"
        :show-file-list="false"
        :accept="accept"
        :disabled="uploading"
        @before-upload="onBeforeUpload"
      >
        <n-upload-dragger class="dragger">
          <div v-if="!previewUrl" class="dragger-content">
            <i class="upload-icon i-fe:video" />
            <p class="upload-text">点击或拖拽视频文件到此处上传</p>
            <p class="upload-hint">{{ hint }}</p>
          </div>
          <div v-else class="preview-container">
            <video
              :src="previewUrl"
              :style="{ width: previewWidth + 'px', height: previewHeight + 'px' }"
              class="preview-video"
              controls
              preload="metadata"
            />
            <div class="preview-overlay">
              <i class="i-fe:refresh-cw" />
              <span>重新上传</span>
            </div>
          </div>
        </n-upload-dragger>
      </n-upload>
      <!-- 删除按钮（上传完成后显示） -->
      <n-button
        v-if="previewUrl && !uploading"
        size="small"
        type="error"
        class="mt-8"
        @click.stop="handleClear"
      >
        <i class="i-fe:trash-2 mr-4" />
        删除视频
      </n-button>
      <n-progress
        v-if="uploading"
        type="line"
        :percentage="uploadProgress"
        :show-indicator="false"
        class="mt-8"
      />
    </div>
  </div>
</template>

<script setup>
import { request } from '@/utils'

defineOptions({ name: 'VideoUpload' })

const props = defineProps({
  // 当前值（视频URL）
  modelValue: {
    type: String,
    default: '',
  },
  // 接受的文件类型
  accept: {
    type: String,
    default: '.mp4,.avi,.mkv,.mov,.wmv,.flv,.webm',
  },
  // 提示文字
  hint: {
    type: String,
    default: '支持 MP4、AVI、MKV、MOV、WebM 格式，最大 500MB',
  },
  // 预览宽度
  previewWidth: {
    type: [Number, String],
    default: 320,
  },
  // 预览高度
  previewHeight: {
    type: [Number, String],
    default: 180,
  },
  // 最大文件大小 (MB)
  maxSize: {
    type: Number,
    default: 500,
  },
})

const emit = defineEmits(['update:modelValue', 'change'])

// 状态
const uploading = ref(false)
const uploadProgress = ref(0)

// 预览URL
const previewUrl = computed(() => props.modelValue)

// 更新值
function updateValue(url) {
  emit('update:modelValue', url)
  emit('change', url)
}

// 上传前校验
function onBeforeUpload({ file }) {
  const isVideo = file.file?.type.startsWith('video/')
  
  if (!isVideo) {
    $message.error('只能上传视频文件')
    return false
  }
  
  // 文件大小校验
  const sizeMB = file.file.size / (1024 * 1024)
  if (sizeMB > props.maxSize) {
    $message.error(`视频文件不能超过 ${props.maxSize}MB`)
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

    // 模拟进度（视频文件较大，进度更慢）
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 5
      }
    }, 500)

    const res = await request.post('/file/upload/video', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })

    clearInterval(progressInterval)
    uploadProgress.value = 100

    if (res.code === 0 || res.code === 200) {
      const uploadedUrl = res.data?.url || res.data
      updateValue(uploadedUrl)
      $message.success('视频上传成功')
      onFinish()
    } else {
      throw new Error(res.message || '上传失败')
    }
  } catch (error) {
    $message.error(error.message || '视频上传失败')
    onError()
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 清除（同时删除已上传的文件）
async function handleClear() {
  const currentUrl = props.modelValue
  
  // 先清空本地值
  updateValue('')
  
  // 如果有已上传的文件，调用后端删除
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
.video-upload {
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
  min-height: 120px;
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

.preview-video {
  border-radius: 4px;
  object-fit: cover;
  background: #000;
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
</style>
