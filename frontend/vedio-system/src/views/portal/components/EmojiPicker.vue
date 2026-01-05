<template>
  <div class="emoji-picker" v-if="visible" @click.stop>
    <!-- 表情分类标签 -->
    <div class="emoji-tabs">
      <button 
        v-for="(cat, idx) in categories" 
        :key="idx"
        :class="{ active: activeTab === idx }"
        @click="activeTab = idx"
      >
        {{ cat.icon }}
      </button>
    </div>
    
    <!-- 表情网格 -->
    <div class="emoji-grid">
      <button 
        v-for="emoji in categories[activeTab].emojis" 
        :key="emoji"
        class="emoji-item"
        @click="selectEmoji(emoji)"
      >
        {{ emoji }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['select', 'close'])

const activeTab = ref(0)

// 表情分类数据
const categories = [
  {
    icon: '😀',
    name: '常用',
    emojis: ['😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂', '🙂', '🙃', '😉', '😊', '😇', '🥰', '😍', '🤩', '😘', '😗', '😚', '😙', '🥲', '😋', '😛', '😜', '🤪', '😝', '🤑', '🤗', '🤭', '🤫', '🤔', '🤐', '🤨', '😐', '😑', '😶']
  },
  {
    icon: '👍',
    name: '手势',
    emojis: ['👍', '👎', '👌', '🤌', '🤏', '✌️', '🤞', '🤟', '🤘', '🤙', '👈', '👉', '👆', '👇', '☝️', '👋', '🤚', '🖐️', '✋', '🖖', '👏', '🙌', '👐', '🤲', '🙏', '✍️', '💪', '🦾', '🦿']
  },
  {
    icon: '❤️',
    name: '符号',
    emojis: ['❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔', '❤️‍🔥', '❤️‍🩹', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '☮️', '✝️', '☪️', '🕉️', '☸️', '✡️', '🔯', '🕎', '☯️', '☦️']
  },
  {
    icon: '🎉',
    name: '庆祝',
    emojis: ['🎉', '🎊', '🎈', '🎁', '🎀', '🎄', '🎃', '🎇', '🎆', '✨', '🌟', '⭐', '💫', '🔥', '💥', '💯', '🏆', '🥇', '🥈', '🥉', '🏅', '🎖️', '🎗️', '🎟️', '🎫', '🎭', '🎨', '🎬', '🎤', '🎧']
  },
  {
    icon: '🐱',
    name: '动物',
    emojis: ['🐱', '🐶', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', '🦁', '🐮', '🐷', '🐸', '🐵', '🐔', '🐧', '🐦', '🐤', '🦆', '🦅', '🦉', '🦇', '🐺', '🐗', '🐴', '🦄', '🐝', '🐛', '🦋']
  }
]

function selectEmoji(emoji) {
  emit('select', emoji)
}
</script>

<style scoped>
.emoji-picker {
  position: absolute;
  bottom: 100%;
  left: 0;
  width: 320px;
  background: #25272d;
  border: 1px solid #3a3c42;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  z-index: 100;
  margin-bottom: 8px;
}

.emoji-tabs {
  display: flex;
  gap: 4px;
  padding: 8px 12px;
  border-bottom: 1px solid #3a3c42;
  background: #1f2125;
}

.emoji-tabs button {
  flex: 1;
  padding: 8px;
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.emoji-tabs button:hover {
  background: #3a3c42;
}

.emoji-tabs button.active {
  background: #FB7299;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(9, 1fr);
  gap: 2px;
  padding: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 20px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.15s;
}

.emoji-item:hover {
  background: #3a3c42;
  transform: scale(1.2);
}

/* 滚动条样式 */
.emoji-grid::-webkit-scrollbar {
  width: 6px;
}

.emoji-grid::-webkit-scrollbar-thumb {
  background: #4a4c52;
  border-radius: 3px;
}

.emoji-grid::-webkit-scrollbar-track {
  background: transparent;
}
</style>
