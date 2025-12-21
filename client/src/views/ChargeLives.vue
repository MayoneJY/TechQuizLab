<template>
  <div class="charge-screen">
    <div class="charge-container">
        <div class="header">
            <h1 class="pixel-text title">
                <span class="glitch" data-text="도전 기회 충전">도전 기회 충전</span>
            </h1>
            <button class="pixel-button back-button" @click="router.push('/')">
                ← 홈으로
            </button>
        </div>

        <div class="charge-grid">
            <div 
                v-for="i in 10" 
                :key="i" 
                class="charge-card glass-card"
                :class="{ active: selectedQuantity === i }"
                @click="selectQuantity(i)"
            >
                <div class="card-badges">
                    <span class="badge count-badge pixel-text">x{{ i }}</span>
                    <span v-if="getDiscount(i) > 0" class="badge discount-badge pixel-text">BIG SALE</span>
                </div>

                <div class="card-content">
                    <div class="heart-display">
                        <img src="/assets/icons/icon-heart-full.png" class="heart-icon" />
                    </div>
                    
                    <div class="price-info">
                        <span class="original-price pixel-text" v-if="getDiscount(i) > 0">{{ formatPrice(i * 3000) }}</span>
                        <h2 class="final-price pixel-text">{{ formatPrice(calculatePrice(i)) }}원</h2>
                    </div>
                </div>

                <div class="card-footer">
                     <div class="selection-indicator">
                        {{ selectedQuantity === i ? 'Checking...' : 'Select' }}
                     </div>
                </div>
                
                <div class="scan-line"></div>
            </div>
        </div>

        <!-- Floating Footer for Action -->
        <div class="action-dock glass-panel" v-if="selectedQuantity > 0">
            <div class="dock-info">
                <span class="dock-label pixel-text">Total</span>
                <span class="dock-price pixel-text">{{ formatPrice(calculatePrice(selectedQuantity)) }}원</span>
            </div>
            <button class="pixel-button primary dock-btn" @click="requestPayment">
                충전하기
            </button>
        </div>
    </div>
    
    <!-- Floating monsters decoration -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useModalStore } from '../stores/modal'
import { loadTossPayments } from '@tosspayments/payment-sdk'
import { useAuthStore } from '../stores/auth'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const modalStore = useModalStore()
const authStore = useAuthStore()

onMounted(() => {
    // console.log('ChargeLives Component Mounted')
})

const selectedQuantity = ref(1)

const clientKey = import.meta.env.VITE_TOSS_CLIENT_KEY

function calculatePrice(qty: number) {
    let unitPrice = 3000
    if (qty >= 10) unitPrice = 2600
    else if (qty >= 5) unitPrice = 2700
    return qty * unitPrice
}

function getDiscount(qty: number) {
    if (qty >= 10) return 400
    if (qty >= 5) return 300
    return 0
}

function formatPrice(price: number) {
    return price.toLocaleString()
}

function selectQuantity(i: number) {
    selectedQuantity.value = i
}

async function requestPayment() {
    try {
        const tossPayments = await loadTossPayments(clientKey)
        const amount = calculatePrice(selectedQuantity.value)
        const orderId = 'ORD-' + Date.now() + '-' + Math.random().toString(36).substring(2, 9)

        await tossPayments.requestPayment('카드', {
            amount: amount,
            orderId: orderId,
            orderName: `도전 기회 ${selectedQuantity.value}개 충전`,
            customerName: authStore.user?.nickname || 'Guest',
            successUrl: window.location.origin + `/payment/success?quantity=${selectedQuantity.value}`,
            failUrl: window.location.origin + '/payment/fail',
        })
    } catch (e: any) {
        // console.error(e)
        if (e.code === 'USER_CANCEL') return
        modalStore.openAlert('결제 요청 중 오류가 발생했습니다.')
    }
}
</script>

<style scoped>
.charge-screen {
    min-height: 100vh;
    /* background-color: #111; */
    position: relative;
    padding: 80px 20px 40px; /* Top padding for breathing room */
    display: flex;
    justify-content: center;
}

.charge-container {
    width: 100%;
    max-width: 1000px;
    z-index: 5;
    position: relative;
    padding-bottom: 120px; /* Space for dock */
}

/* Header reused from StageList */
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 50px;
    border-bottom: 2px solid rgba(255,255,255,0.1);
    padding-bottom: 20px;
}

.title {
    font-size: 32px;
    color: #fff;
    margin: 0;
    text-shadow: 2px 2px 0 #000;
}

.back-button {
    font-size: 14px;
    padding: 10px 20px;
}

/* Grid Layout */
.charge-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 25px;
}

/* Card Style based on StageCard */
.charge-card {
    position: relative;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    padding: 24px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 200px;
    backdrop-filter: blur(10px);
    overflow: hidden;
}

.charge-card:hover {
    transform: translateY(-8px);
    background: rgba(255, 255, 255, 0.08);
    border-color: rgba(255, 255, 255, 0.3);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.3);
}

.charge-card.active {
    border-color: #48bb78;
    background: rgba(72, 187, 120, 0.1);
    box-shadow: 0 0 20px rgba(72, 187, 120, 0.2);
}

.card-badges {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

.badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
}

.count-badge {
    background: rgba(255,255,255,0.1);
    color: #fff;
}

.discount-badge {
    background: #ff4757;
    color: #fff;
}

.card-content {
    text-align: center;
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 15px;
}

.heart-icon {
    width: 64px;
    height: 64px;
    image-rendering: pixelated;
    filter: drop-shadow(0 0 10px rgba(235, 72, 72, 0.4));
}

.price-info {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.original-price {
    font-size: 14px;
    color: #888;
    text-decoration: line-through;
}

.final-price {
    font-size: 24px;
    color: #fff;
    margin: 0;
}

.card-footer {
    margin-top: 20px;
    text-align: center;
}

.selection-indicator {
    font-size: 12px;
    color: rgba(255,255,255,0.3);
    text-transform: uppercase;
    letter-spacing: 1px;
}

.charge-card.active .selection-indicator {
    color: #48bb78;
    font-weight: bold;
}

/* Floating Action Dock */
.action-dock {
    position: fixed;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: 30px;
    background: rgba(20, 20, 20, 0.9);
    backdrop-filter: blur(20px);
    padding: 15px 30px;
    border-radius: 50px;
    border: 1px solid rgba(255,255,255,0.15);
    box-shadow: 0 10px 40px rgba(0,0,0,0.5);
    z-index: 100;
    width: 90%;
    max-width: 600px;
    justify-content: space-between;
}

.dock-info {
    display: flex;
    flex-direction: column;
}

.dock-label {
    font-size: 12px;
    color: #888;
}

.dock-price {
    font-size: 24px;
    color: #48bb78;
}

.dock-btn {
    padding: 12px 30px;
    border-radius: 30px;
    font-size: 18px;
}

.monster {
  position: fixed;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

.monster-1 {
  top: 120px;
  right: 5%;
}

@media (max-width: 768px) {
    .charge-grid {
        grid-template-columns: repeat(2, 1fr);
    }
    
    .header {
        flex-direction: column;
        gap: 15px;
        align-items: flex-start;
    }
    
    .back-button {
        width: 100%;
    }
}

@media (max-width: 480px) {
    .charge-screen {
        padding: 40px 15px 40px; /* Reduced top padding */
    }

    .charge-grid {
        grid-template-columns: 1fr; /* Single column */
        gap: 15px;
    }
    
    .title {
        font-size: 24px;
    }

    /* Floating Dock Mobile Optimization */
    .action-dock {
        width: 95%;
        padding: 12px 20px;
        bottom: 20px;
        gap: 15px;
    }
    
    .dock-price {
        font-size: 20px;
    }
    
    .dock-btn {
        padding: 10px 20px;
        font-size: 16px;
    }
    
    /* Hide decorative monsters on mobile to prevent obstruction */
    .monster {
        display: none;
    }
}
</style>
