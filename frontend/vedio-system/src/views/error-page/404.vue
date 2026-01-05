<template>
  <div class="error-page">
    <!-- 星空背景 -->
    <div class="stars"></div>
    <div class="stars2"></div>
    <div class="stars3"></div>
    
    <div class="error-content">
      <!-- 动态 404 数字 -->
      <div class="error-code">
        <span class="digit">4</span>
        <span class="digit zero">0</span>
        <span class="digit">4</span>
      </div>
      
      <h1 class="error-title">哎呀，页面走丢了 🎬</h1>
      <p class="error-desc">这个视频似乎被外星人绑架了...</p>
      
      <div class="error-actions">
        <button class="home-btn" @click="goHome">
          <span class="btn-icon">🏠</span>
          <span>返回首页</span>
        </button>
        <button class="back-btn" @click="goBack">
          <span class="btn-icon">←</span>
          <span>返回上页</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const router = useRouter()

const goHome = () => router.replace('/')
const goBack = () => {
  if (history.state.back) {
    router.back()
  } else {
    router.replace('/')
  }
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f0f23 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 星空动画 */
.stars, .stars2, .stars3 {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.stars {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='400'%3E%3Ccircle cx='50' cy='50' r='1' fill='white' opacity='0.8'/%3E%3Ccircle cx='150' cy='100' r='0.5' fill='white' opacity='0.6'/%3E%3Ccircle cx='250' cy='150' r='1.5' fill='white' opacity='0.9'/%3E%3Ccircle cx='350' cy='80' r='0.8' fill='white' opacity='0.7'/%3E%3Ccircle cx='100' cy='250' r='1' fill='white' opacity='0.5'/%3E%3Ccircle cx='200' cy='300' r='1.2' fill='white' opacity='0.8'/%3E%3Ccircle cx='300' cy='350' r='0.6' fill='white' opacity='0.6'/%3E%3C/svg%3E") repeat;
  animation: twinkle 4s ease-in-out infinite;
}

.stars2 {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Ccircle cx='80' cy='80' r='0.8' fill='%23ff6b9d' opacity='0.6'/%3E%3Ccircle cx='180' cy='120' r='0.5' fill='%2363b3ed' opacity='0.5'/%3E%3Ccircle cx='250' cy='200' r='1' fill='%23f6e05e' opacity='0.4'/%3E%3C/svg%3E") repeat;
  animation: twinkle 6s ease-in-out infinite reverse;
}

.stars3 {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Ccircle cx='50' cy='100' r='1.5' fill='white' opacity='0.3'/%3E%3Ccircle cx='150' cy='50' r='1' fill='white' opacity='0.4'/%3E%3C/svg%3E") repeat;
  animation: twinkle 8s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 1; transform: translateY(0); }
  50% { opacity: 0.6; transform: translateY(-10px); }
}

.error-content {
  text-align: center;
  z-index: 10;
}

.error-code {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
}

.digit {
  font-size: 10rem;
  font-weight: 900;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 50%, #4facfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 80px rgba(240, 147, 251, 0.3);
  animation: float 3s ease-in-out infinite;
}

.digit.zero {
  animation-delay: -1.5s;
}

.digit:nth-child(3) {
  animation-delay: -0.75s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.error-title {
  font-size: 2rem;
  color: #ffffff;
  margin-bottom: 15px;
  font-weight: 600;
}

.error-desc {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.home-btn, .back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border: none;
  border-radius: 30px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.home-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  box-shadow: 0 8px 25px rgba(240, 147, 251, 0.4);
}

.home-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(240, 147, 251, 0.5);
}

.back-btn {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
}

.btn-icon {
  font-size: 1.2rem;
}

/* 响应式 */
@media (max-width: 768px) {
  .digit {
    font-size: 5rem;
  }
  
  .error-title {
    font-size: 1.5rem;
  }
  
  .error-actions {
    flex-direction: column;
    gap: 15px;
  }
}
</style>

