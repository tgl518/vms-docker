<template>
  <div class="error-page">
    <!-- 背景动画 -->
    <div class="grid-bg"></div>
    
    <div class="error-content">
      <!-- 锁定图标 -->
      <div class="lock-icon">
        <span class="lock">🔒</span>
        <div class="pulse-ring"></div>
        <div class="pulse-ring delay"></div>
      </div>
      
      <div class="error-code">403</div>
      <h1 class="error-title">访问被拒绝</h1>
      <p class="error-desc">抱歉，您暂无权限访问此页面</p>
      
      <div class="error-actions">
        <button class="home-btn" @click="goHome">
          <span class="btn-icon">🏠</span>
          <span>返回首页</span>
        </button>
        <button class="back-btn" @click="goBack" v-if="canGoBack">
          <span class="btn-icon">←</span>
          <span>返回上页</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const router = useRouter()

const canGoBack = history.state.back

const goHome = () => router.replace('/')
const goBack = () => router.back()
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #2d132c 50%, #0f0f23 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 网格背景 */
.grid-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255,107,157,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,107,157,0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% { transform: perspective(500px) rotateX(60deg) translateY(0); }
  100% { transform: perspective(500px) rotateX(60deg) translateY(50px); }
}

.error-content {
  text-align: center;
  z-index: 10;
}

.lock-icon {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
}

.lock {
  font-size: 5rem;
  display: block;
  animation: shake 0.5s ease-in-out infinite alternate;
}

@keyframes shake {
  0% { transform: rotate(-5deg); }
  100% { transform: rotate(5deg); }
}

.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120px;
  height: 120px;
  border: 2px solid #ff6b9d;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 2s ease-out infinite;
  opacity: 0;
}

.pulse-ring.delay {
  animation-delay: 1s;
}

@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(0.5); opacity: 0.8; }
  100% { transform: translate(-50%, -50%) scale(1.5); opacity: 0; }
}

.error-code {
  font-size: 6rem;
  font-weight: 900;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8c42 50%, #ffcc4d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 20px;
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
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8c42 100%);
  color: white;
  box-shadow: 0 8px 25px rgba(255, 107, 157, 0.4);
}

.home-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(255, 107, 157, 0.5);
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
  .error-code {
    font-size: 4rem;
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

