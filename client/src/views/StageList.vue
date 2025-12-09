<template>
  <div class="stage-screen">

    
    <div class="stage-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="미션 선택">미션 선택</span>
        </h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div v-if="isLoading" class="loading-state">
        <p class="pixel-text loading-text">데이터 로딩 중...</p>
      </div>
      <div v-else-if="stages.length === 0" class="empty-state glass-panel">
        <p class="pixel-text">진행 가능한 미션이 없습니다</p>
      </div>
      <div v-else class="stage-grid">
        <div 
          v-for="stage in stages" 
          :key="stage.stageId" 
          class="stage-card glass-card"
          @click="startChallenge(stage.stageId)"
        >
          <div class="card-badges">
            <span class="badge deadline" :class="{ urgent: isUrgent(stage.deadline) }">
              {{ formatDeadline(stage.deadline) }}
            </span>
            <span class="badge category">{{ stage.jobCategory || 'General' }}</span>
          </div>
          
          <div class="card-content">
            <h3 class="company-name pixel-text">{{ stage.companyName }}</h3>
            <h2 class="stage-title pixel-text">{{ stage.title }}</h2>
          </div>
          
          <div class="card-footer">
            <button class="pixel-button primary action-btn">
              도전하기
            </button>
          </div>
          
          <!-- Hover overlay effect -->
          <div class="scan-line"></div>
        </div>
      </div>
    </div>

    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { stageApi, battleApi } from '../services/api'
import type { Stage } from '../types/schema'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const authStore = useAuthStore()

const stages = ref<Stage[]>([])
const isLoading = ref(false)

onMounted(async () => {
  await fetchStages()
})

async function fetchStages() {
  isLoading.value = true
  try {
    const response = await stageApi.getAllStages()
    stages.value = response.data
  } catch (error) {
    console.error('Failed to fetch stages:', error)
  } finally {
    isLoading.value = false
  }
}

async function startChallenge(stageId: number) {
  if (!authStore.isAuthenticated) {
    if (confirm('로그인이 필요합니다. 로그인하시겠습니까?')) {
      router.push('/login')
    }
    return
  }

  if (!authStore.user) return

  try {
    // 배틀 생성 요청
    await battleApi.createBattle({
      stageId: stageId,
      userId: authStore.user.userId
    })
    
    // 배틀 생성 후 게임 화면으로 이동 (배틀 ID 전달 필요 시 수정)
    // 현재 Game.vue는 topicId 기반이므로, 배틀 모드에 맞게 수정 필요할 수 있음
    // 우선은 알림만 표시하고 게임 화면으로 넘어가는 척 (실제 라우팅은 백엔드 응답 구조에 따라 다름)
    alert('BATTLE START! (Simulation)')
    // router.push(`/battle/${response.data.battleId}`) 
    // For now, redirect to game with a query param or just game view
    router.push('/game')
  } catch (error: any) {
    console.error('Failed to create battle:', error)
    alert('배틀 생성에 실패했습니다.')
  }
}

function goHome() {
  router.push('/')
}

function formatDeadline(dateString?: string) {
  if (!dateString) return 'OPEN'
  const deadline = new Date(dateString).getTime()
  const now = new Date().getTime()
  const diffDays = Math.ceil((deadline - now) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) return 'D-Day'
  if (diffDays > 0) return `D-${diffDays}`
  return `D+${Math.abs(diffDays)}`
}

function isUrgent(dateString?: string) {
  if (!dateString) return false
  const deadline = new Date(dateString).getTime()
  const now = new Date().getTime()
  const diffDays = (deadline - now) / (1000 * 60 * 60 * 24)
  return diffDays <= 3 && diffDays >= 0
}
</script>

<style scoped>
.stage-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;
}

.stage-container {
  width: 100%;
  max-width: 1000px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  border-bottom: 2px solid rgba(255,255,255,0.1);
  padding-bottom: 20px;
  flex-wrap: wrap; /* Prevent overlap on small screens */
  gap: 20px;
}

.title {
  font-size: 36px;
  color: #ffd43b;
  margin: 0;
}

.stage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  width: 100%;
}

.stage-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  cursor: pointer;
  min-height: 200px;
  justify-content: space-between;
  border-width: 2px;
  position: relative; /* Ensure it's a positioning context */
  overflow: hidden; /* Contain the absolute scan-line element */
}

.stage-card:hover {
  border-color: #4a9eff;
  transform: translateY(-5px) scale(1.02);
}

.card-badges {
  display: flex;
  justify-content: space-between;
}

.badge {
  font-size: 11px;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(0,0,0,0.5);
  color: #ccc;
  border: 1px solid #444;
}

.badge.urgent {
  color: #ff6b6b;
  border-color: #ff6b6b;
  animation: pulse 1s infinite;
}

.badge.category {
  color: #51cf66;
  border-color: #51cf66;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.company-name {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.stage-title {
  font-size: 18px;
  color: #fff;
  margin: 0;
  line-height: 1.4;
}

.card-footer {
  margin-top: 15px;
}

.action-btn {
  width: 100%;
  font-size: 12px;
  padding: 10px;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 50px;
}

.loading-text {
  color: #4a9eff;
  font-size: 18px;
}

/* Scan line effect on hover */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(74, 158, 255, 0.1), transparent);
  transform: translateY(-100%);
  transition: transform 0.5s;
  pointer-events: none;
}

.stage-card:hover .scan-line {
  animation: scan 1.5s infinite linear;
}

@keyframes scan {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(200%); }
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.monster {
  position: fixed;
  top: 100px;
  left: 50px;
  z-index: 5;
  pointer-events: none;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}
</style>
