<template>
  <div class="stage-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="stage-container">
      <div class="header">
        <h1 class="pixel-text title">채용 공고 (Stage)</h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div v-if="isLoading" class="loading-text pixel-text">로딩 중...</div>
      <div v-else-if="stages.length === 0" class="empty-message pixel-text">
        진행 중인 공고가 없습니다.
      </div>
      <div v-else class="stage-list">
        <div 
          v-for="stage in stages" 
          :key="stage.stageId" 
          class="stage-card"
        >
          <div class="stage-header">
            <span class="company-name pixel-text">{{ stage.companyName }}</span>
            <span class="deadline" :class="{ urgent: isUrgent(stage.deadline) }">
              {{ formatDeadline(stage.deadline) }}
            </span>
          </div>
          
          <h3 class="stage-title pixel-text">{{ stage.title }}</h3>
          
          <div class="stage-info">
            <span class="job-category">{{ stage.jobCategory || '직무 미정' }}</span>
          </div>
          
          <div class="stage-actions">
            <button class="pixel-button challenge-btn" @click="startChallenge(stage.stageId)">
              공략하기 (Battle)
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Spaceship decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
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
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
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
    // 우선은 알림만 표시
    alert('배틀이 생성되었습니다! (게임 화면으로 이동 기능 구현 필요)')
    // router.push(`/battle/${response.data.battleId}`) 
  } catch (error: any) {
    console.error('Failed to create battle:', error)
    alert('배틀 생성에 실패했습니다.')
  }
}

function goHome() {
  router.push('/')
}

function formatDeadline(dateString?: string) {
  if (!dateString) return '상시 채용'
  const date = new Date(dateString)
  return `~ ${date.getMonth() + 1}/${date.getDate()}`
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
  animation: screen-enter 0.8s ease-out;
}

.stage-container {
  width: 100%;
  max-width: 800px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  margin: 0;
}

.stage-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stage-card {
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.stage-card:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-5px);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.company-name {
  font-size: 14px;
  color: #4a9eff;
  font-weight: 700;
}

.deadline {
  font-size: 12px;
  color: #ccc;
  background: rgba(0, 0, 0, 0.5);
  padding: 4px 8px;
  border-radius: 4px;
}

.deadline.urgent {
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  animation: pulse 2s infinite;
}

.stage-title {
  font-size: 20px;
  color: #fff;
  margin-bottom: 15px;
  line-height: 1.4;
}

.stage-info {
  margin-bottom: 20px;
}

.job-category {
  font-size: 12px;
  color: #51cf66;
  border: 1px solid #51cf66;
  padding: 4px 8px;
  border-radius: 12px;
}

.stage-actions {
  display: flex;
  justify-content: flex-end;
}

.challenge-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #e03131 100%);
  border-color: #c92a2a;
  padding: 12px 24px;
  font-size: 14px;
}

.challenge-btn:hover {
  box-shadow: 0 0 15px rgba(255, 107, 107, 0.6);
}

.empty-message {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

.stars-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.spaceship {
  position: fixed;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
  pointer-events: none;
}

.monster {
  position: fixed;
  top: 100px;
  left: 50px;
  animation: float 2s ease-in-out infinite;
  z-index: 3;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}
</style>
