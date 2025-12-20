<template>
  <div class="result-screen">
    <div class="result-container glass-card">
      
      <!-- Header -->
      <div class="modal-header">
          <span class="pixel-text modal-title">SYSTEM NOTICE</span>
          <div class="header-decoration">
            <div class="dot red"></div>
            <div class="dot yellow"></div>
            <div class="dot green"></div>
          </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-content">
        <div class="spinner"></div>
        <p class="status-text pixel-text">결제 확인중...</p>
      </div>

      <!-- Success State -->
      <div v-else-if="success" class="success-content">
        <div class="icon-wrapper success-icon">
          <img src="/assets/icons/icon-heart-full.png" class="result-icon pulse" />
        </div>
        <h2 class="title pixel-text">결제 성공!</h2>
        <p class="description">
            도전 기회가 <span class="highlight">{{ quantity }}개</span> 충전되었습니다.<br>
            이제 면접관에 도전하세요!
        </p>
        
        <div class="action-buttons">
            <button class="pixel-button primary" @click="router.push('/')">메인으로</button>
            <button class="pixel-button secondary" @click="router.push('/charge')">더 충전하기</button>
        </div>
      </div>

      <!-- Error State -->
      <div v-else class="error-content">
        <div class="icon-wrapper error-icon">
            <span class="pixel-emoji">💔</span>
        </div>
        <h2 class="title pixel-text error-title">결제 실패</h2>
        <p class="description">{{ errorMessage }}</p>
        <button class="pixel-button secondary" @click="router.push('/charge')">다시 시도하기</button>
      </div>

    </div>

    <!-- Decoration -->
    <div class="monster-decoration">
        <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '../services/api'
import { useAuthStore } from '../stores/auth'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(true)
const success = ref(false)
const errorMessage = ref('')
const quantity = ref(0) // Store quantity for display

onMounted(async () => {
    const { paymentKey, orderId, amount, quantity: qty } = route.query
    
    if (qty) quantity.value = Number(qty)

    if (!paymentKey || !orderId || !amount || !qty) {
        success.value = false
        errorMessage.value = '잘못된 접근입니다.'
        loading.value = false
        return
    }

    try {
        await api.post('/api/payments/confirm', {
            paymentKey,
            orderId,
            amount: Number(amount),
            quantity: Number(qty)
        })
        
        success.value = true
        if (authStore.user) {
             authStore.user.remainingLives = (authStore.user.remainingLives || 0) + Number(qty)
        }

    } catch (e: any) {
        console.error(e)
        success.value = false
        
        const rawMsg = e.response?.data?.message || e.message || ''
        let displayMsg = rawMsg

        // Try to extract "message":"..." pattern from the nested JSON string
        // Example: ... "{\"code\":\"...\",\"message\":\"이미 처리된 결제 입니다.\"}"
        const match = rawMsg.match(/"message":"([^"]+)"/)
        if (match && match[1]) {
            displayMsg = match[1]
        }
        
        errorMessage.value = displayMsg || '결제 승인 중 오류가 발생했습니다.'
    } finally {
        loading.value = false
    }
})
</script>

<style scoped>
.result-screen {
    min-height: 80vh; /* Adjust height */
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    padding: 20px;
}

.result-container {
    padding: 0; /* Remove padding from container, move to content */
    width: 100%;
    max-width: 500px;
    text-align: center;
    background: rgba(255, 255, 255, 0.05); /* Dark Glass */
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
  color: #4a9eff;
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

.success-content, .error-content, .loading-content {
    padding: 40px;
}

.loading-content {
    padding: 40px 0;
}

.spinner {
    width: 50px;
    height: 50px;
    border: 5px solid rgba(255,255,255,0.1);
    border-top: 5px solid #4facfe;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

.status-text {
    font-size: 18px;
    color: #ccc;
}

.icon-wrapper {
    margin-bottom: 30px;
    display: flex;
    justify-content: center;
}

.result-icon {
    width: 80px;
    height: 80px;
    image-rendering: pixelated;
    filter: drop-shadow(0 0 15px rgba(72, 187, 120, 0.5));
}

.pulse {
    animation: pulse 2s infinite;
}

.pixel-emoji {
    font-size: 60px;
    filter: drop-shadow(0 0 10px rgba(255, 71, 87, 0.5));
}

.title {
    font-size: 28px;
    color: #fff;
    margin-bottom: 20px;
    text-shadow: 2px 2px 0 #000;
}

.highlight {
    color: #ffd43b;
    font-weight: bold;
    font-size: 1.1em;
}

.description {
    color: #ccc;
    font-size: 16px;
    line-height: 1.6;
    margin-bottom: 40px;
}

.action-buttons {
    display: flex;
    gap: 15px;
    justify-content: center;
}

.error-title {
    color: #ff4757;
}

.monster-decoration {
    position: absolute;
    bottom: 50px;
    right: 50px;
    animation: float 3s ease-in-out infinite;
    z-index: 5;
    opacity: 0.8;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

@keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.1); }
    100% { transform: scale(1); }
}

@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-15px); }
}
</style>
