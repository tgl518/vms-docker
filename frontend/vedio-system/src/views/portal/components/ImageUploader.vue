<template>
  <div class="image-uploader">
    <!-- 已上传图片预览 -->
    <div class="image-preview-list" v-if="images.length">
      <div 
        v-for="(img, idx) in images" 
        :key="idx" 
        class="image-preview-item"
      >
        <img :src="img.url" :alt="img.name" />
        <button class="remove-btn" @click="removeImage(idx)">×</button>
        <div class="upload-progress" v-if="img.uploading">
          <div class="progress-bar" :style="{ width: img.progress + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 上传按钮 -->
    <div 
      class="upload-trigger" 
      v-if="images.length < maxCount"
      @click="triggerUpload"
    >
      <n-icon size="20"><ImageIcon /></n-icon>
      <span>添加图片</span>
    </div>

    <!-- 隐藏的 input -->
    <input 
      ref="inputRef"
      type="file" 
      accept="image/*" 
      multiple
      @change="handleFileChange"
      style="display: none"
    />
  </div>
</template>

<script setup>
import { ref, h } from 'vue'
import { NIcon, useMessage } from 'naive-ui'
import { request } from '@/utils'

const ImageIcon = { 
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'currentColor' }, [
    h('path', { d: 'M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z' })
  ])
}

const props = defineProps({
  maxCount: { type: Number, default: 3 },  // 最多上传几张
  maxSize: { type: Number, default: 5 }    // 单张最大MB
})

const emit = defineEmits(['change'])
const message = useMessage()

const inputRef = ref(null)
const images = ref([])

function triggerUpload() {
  inputRef.value?.click()
}

async function handleFileChange(e) {
  const files = Array.from(e.target.files || [])
  if (!files.length) return

  for (const file of files) {
    // 检查数量限制
    if (images.value.length >= props.maxCount) {
      message.warning(`最多上传${props.maxCount}张图片`)
      break
    }
    
    // 检查大小
    if (file.size > props.maxSize * 1024 * 1024) {
      message.warning(`${file.name} 超过${props.maxSize}MB限制`)
      continue
    }

    // 检查类型
    if (!file.type.startsWith('image/')) {
      message.warning(`${file.name} 不是有效的图片`)
      continue
    }

    // 创建预览
    const localUrl = URL.createObjectURL(file)
    const imgItem = {
      file,
      name: file.name,
      url: localUrl,
      uploading: true,
      progress: 0,
      uploaded: false
    }
    images.value.push(imgItem)

    // 上传文件
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const { data } = await request.post('/file/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (e) => {
          imgItem.progress = Math.round((e.loaded * 100) / e.total)
        }
      })
      
      // 上传成功，替换为服务器URL
      imgItem.url = data.url || data
      imgItem.uploading = false
      imgItem.uploaded = true
      
      emitChange()
    } catch (err) {
      message.error(`${file.name} 上传失败`)
      // 移除失败的
      const idx = images.value.indexOf(imgItem)
      if (idx > -1) images.value.splice(idx, 1)
    }
  }

  // 清空 input
  e.target.value = ''
}

function removeImage(idx) {
  images.value.splice(idx, 1)
  emitChange()
}

function emitChange() {
  emit('change', images.value.filter(i => i.uploaded).map(i => i.url))
}

// 暴露方法给父组件
function clear() {
  images.value = []
}

function getUrls() {
  return images.value.filter(i => i.uploaded).map(i => i.url)
}

defineExpose({ clear, getUrls })
</script>

<style scoped>
.image-uploader {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image-preview-list {
  display: flex;
  gap: 8px;
}

.image-preview-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #3a3c42;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 14px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.remove-btn:hover {
  background: #FB7299;
}

.upload-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: rgba(0, 0, 0, 0.5);
}

.progress-bar {
  height: 100%;
  background: #FB7299;
  transition: width 0.2s;
}

.upload-trigger {
  width: 80px;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: #25272d;
  border: 1px dashed #4a4c52;
  border-radius: 8px;
  color: #999;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-trigger:hover {
  border-color: #FB7299;
  color: #FB7299;
}
</style>
