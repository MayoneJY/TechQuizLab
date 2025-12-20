<template>
  <div class="result-screen">
     <div class="result-container glass-card">
        <!-- Header -->
        <div class="modal-header">
          <span class="pixel-text modal-title">SYSTEM ERROR</span>
          <div class="header-decoration">
            <div class="dot red"></div>
            <div class="dot yellow"></div>
            <div class="dot green"></div>
          </div>
        </div>
        
        <div class="content"> 
            <div class="icon-wrapper error-icon">
                <span class="pixel-emoji">😢</span>
            </div>
        <h2 class="title pixel-text error-title">결제 실패</h2>
        <p class="description">{{ route.query.message || '결제가 취소되었습니다.' }}</p>
        
        <div class="action-buttons">
            <button class="pixel-button secondary" @click="router.push('/charge')">다시 시도하기</button>
            <button class="pixel-button text-btn" @click="router.push('/')">홈으로</button>
        </div>
       </div> <!-- Close content div -->
     </div>
     
      <!-- Decoration -->
    <div class="monster-decoration">
        <PixelMonster type="ghost" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
</script>

<style scoped>
.result-screen {
    min-height: 80vh;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    padding: 20px;
}

.result-container {
    padding: 0;
    width: 100%;
    max-width: 450px;
    text-align: center;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    backdrop-filter: blur(15px);
    box-shadow: 0 20px 50px rgba(0,0,0,0.5);
    z-index: 10;
    position: relative;
    overflow: hidden;
     display: flex;
    flex-direction: column;
}

/* Header styles reused */
.modal-header {
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  color: #ff4757; /* Red for error */
  font-size: 14px;
  letter-spacing: 1px;
}

.header-decoration {
  display: flex;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  opacity: 0.7;
}

.dot.red { background: #ff6b6b; }
.dot.yellow { background: #ffd43b; }
.dot.green { background: #51cf66; }

.content {
    padding: 40px;
}

.icon-wrapper {
    margin-bottom: 20px;
}

.pixel-emoji {
    font-size: 60px;
    filter: drop-shadow(0 0 10px rgba(0,0,0,0.5));
}

.title {
    font-size: 28px;
    margin-bottom: 20px;
    text-shadow: 2px 2px 0 #000;
}

.error-title {
    color: #ff4757;
}

.description {
    color: #ccc;
    font-size: 16px;
    line-height: 1.6;
    margin-bottom: 40px;
}

.action-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.text-btn {
    background: transparent;
    border: none;
    box-shadow: none;
    color: #888;
}

.text-btn:hover {
    color: #fff;
    transform: none;
    background: transparent;
}

.monster-decoration {
    position: absolute;
    bottom: 50px;
    left: 50px;
    animation: float 3s ease-in-out infinite;
    z-index: 5;
    opacity: 0.8;
}

@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-10px); }
}
</style>
