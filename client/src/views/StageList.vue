<template>
  <div class="stage-screen">

    
    <div class="stage-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="실전 모의면접">실전 모의면접</span>
        </h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <!-- Filter Bar -->
      <div class="filter-bar">
        <div class="filter-section">
          <div class="filter-label pixel-text">직무 카테고리</div>
          <div class="filter-chips">
            <button 
              class="filter-chip pixel-button"
              :class="{ active: selectedJobCategories.length === 0 }"
              @click="clearFilters"
            >
              전체
            </button>
            <button
              v-for="option in jobCategoryOptions"
              :key="option.value"
              class="filter-chip pixel-button"
              :class="{ active: selectedJobCategories.includes(option.value) }"
              @click="toggleJobCategory(option.value)"
            >
              {{ option.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- Pagination Info -->
      <div v-if="!isLoading && totalElements > 0" class="pagination-info pixel-text">
        총 {{ totalElements }}개 중 {{ (page * size) + 1 }}-{{ Math.min((page + 1) * size, totalElements) }}개 표시
      </div>

      <div v-if="isLoading" class="stage-grid">
         <div v-for="i in 3" :key="i" class="stage-card skeleton-card">
           <div class="card-badges">
              <div class="skeleton skeleton-text" style="width: 50px; height: 20px;"></div>
              <div class="skeleton skeleton-text" style="width: 60px; height: 20px;"></div>
           </div>
           
           <div class="card-content" style="gap: 15px;">
              <div class="skeleton skeleton-text" style="width: 40%; height: 16px;"></div>
              <div class="skeleton skeleton-text" style="width: 80%; height: 24px;"></div>
           </div>
           
           <div class="card-footer">
              <div class="skeleton skeleton-bar action-btn" style="height: 36px; border-radius: 4px;"></div>
           </div>
         </div>
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

      <!-- Pagination Controls -->
      <div v-if="!isLoading && totalPages > 1" class="pagination-controls">
        <button 
          class="pixel-button"
          :disabled="page === 0"
          @click="changePage(page - 1)"
        >
          이전
        </button>
        <span class="pixel-text pagination-text">
          {{ page + 1 }} / {{ totalPages }}
        </span>
        <button 
          class="pixel-button"
          :disabled="page >= totalPages - 1"
          @click="changePage(page + 1)"
        >
          다음
        </button>
      </div>
    </div>

    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>

    <!-- Battle Creation Loading Overlay -->
    <div v-if="isCreatingBattle" class="loading-overlay">
      <div class="loading-content">
        <h2 class="pixel-text glitch" data-text="AI 면접관 생성중...">AI 면접관 생성중...</h2>
        <div class="loading-bar">
          <div class="loading-progress"></div>
        </div>
        <p class="pixel-text blink">잠시만 기다려주세요</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { stageApi, battleApi } from '../services/api'
import type { Stage, StageDTO } from '../types/schema'
import { useModalStore } from '../stores/modal'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const modalStore = useModalStore()

const jobCategoryOptions = [
  { value: '백엔드/서버개발', label: '백엔드/서버개발' },
  { value: '게임개발', label: '게임개발' },
  { value: '데이터 사이언티스트', label: '데이터 사이언스' },
  { value: '데이터분석가', label: '데이터분석가' },
  { value: '프론트엔드', label: '프론트엔드' },
  { value: 'SE(시스템엔지니어)', label: 'SE(시스템엔지니어)' },
  { value: 'SI개발', label: 'SI개발' },
  { value: '데이터엔지니어', label: '데이터엔지니어' },
  { value: '앱개발', label: '앱개발' },
  { value: '정보보안', label: '정보보안' }
]

const selectedJobCategories = ref<string[]>([])
const page = ref<number>(0)
const size = ref<number>(12)
const totalElements = ref<number>(0)
const totalPages = ref<number>(0)

const stages = ref<Stage[]>([])
const isLoading = ref(false)
const isCreatingBattle = ref(false)

onMounted(async () => {
  // 라우터 query에서 상태 복원
  const query = route.query
  if (query.jobCategories) {
    selectedJobCategories.value = (query.jobCategories as string).split(',').filter(Boolean)
  }
  if (query.page) {
    page.value = parseInt(query.page as string, 10) || 0
  }
  
  await fetchStages()
})

function updateQuery() {
  const query: Record<string, string> = {}
  if (selectedJobCategories.value.length > 0) {
    query.jobCategories = selectedJobCategories.value.join(',')
  }
  if (page.value > 0) {
    query.page = page.value.toString()
  }
  
  router.replace({ query })
}

async function fetchStages() {
  isLoading.value = true
  try {
    const params: Record<string, any> = {
      page: page.value,
      size: size.value
    }
    
    if (selectedJobCategories.value.length > 0) {
      params.jobCategories = selectedJobCategories.value.join(',')
    }
    
    console.log('Fetching stages with params:', params)
    const response = await stageApi.getStagesWithPaging(params)
    console.log('API Response:', response)
    const data = response.data as StageDTO
    console.log('Response data:', data)
    
    stages.value = data.content || []
    totalElements.value = data.totalElements || 0
    totalPages.value = data.totalPages || 0
    
    console.log('Stages loaded:', stages.value.length, 'Total:', totalElements.value)
    
    updateQuery()
  } catch (error) {
    console.error('Failed to fetch stages:', error)
    if (error instanceof Error) {
      console.error('Error details:', error.message, error.stack)
    }
  } finally {
    isLoading.value = false
  }
}

function toggleJobCategory(value: string) {
  const index = selectedJobCategories.value.indexOf(value)
  if (index > -1) {
    selectedJobCategories.value.splice(index, 1)
  } else {
    selectedJobCategories.value.push(value)
  }
  page.value = 0
  fetchStages()
}

function clearFilters() {
  selectedJobCategories.value = []
  page.value = 0
  fetchStages()
}

function changePage(newPage: number) {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    fetchStages()
  }
}



async function startChallenge(stageId: number) {
  if (!authStore.isAuthenticated || !authStore.user) {
    if (await modalStore.openConfirm('로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?')) {
      router.push('/login')
    }
    return
  }

  isCreatingBattle.value = true
  try {
    // 배틀 생성 요청
    const response = await battleApi.createBattle({
      stageId: stageId,
      userId: authStore.user.userId
    })
    
    // 배틀 ID로 문제 로딩
    const battleId = response.data.battleId
    
    // 성공 시 게임 화면으로 이동 (Query Param으로 battleId 전달)
    router.push(`/game?battleId=${battleId}`)
  } catch (error: any) {
    console.error('Failed to create battle:', error)
    await modalStore.openAlert('배틀 생성에 실패했습니다.')
    isCreatingBattle.value = false // Reset loading state on error
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

/* Skeleton Styles - Consistent with other pages */
.skeleton-card {
  cursor: default;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.05);
  border-color: #444;
}

.skeleton-card:hover {
  transform: none;
  border-color: #444;
}

.skeleton {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.skeleton::after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  transform: translateX(-100%);
  background-image: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0,
    rgba(255, 255, 255, 0.1) 20%,
    rgba(255, 255, 255, 0.2) 60%,
    rgba(255, 255, 255, 0)
  );
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}
@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}

/* Loading Overlay Styles */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.loading-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.loading-bar {
  width: 300px;
  height: 4px;
  background: #333;
  border-radius: 2px;
  overflow: hidden;
  position: relative;
}

.loading-progress {
  width: 100%;
  height: 100%;
  background: #4a9eff;
  position: absolute;
  top: 0;
  left: 0;
  animation: loading 2s infinite ease-in-out;
}

.blink {
  animation: blink 1.5s infinite;
  color: #888;
  font-size: 14px;
}

@keyframes loading {
  0% { transform: translateX(-100%); }
  50% { transform: translateX(0); }
  100% { transform: translateX(100%); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* Filter Bar Styles */
.filter-bar {
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #ffd43b;
  font-weight: 700;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-chip {
  padding: 8px 16px;
  font-size: 12px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.5);
  color: #ccc;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-chip:hover {
  border-color: #4a9eff;
  transform: translateY(-2px);
}

.filter-chip.active {
  background: rgba(74, 158, 255, 0.3);
  border-color: #4a9eff;
  color: #fff;
}

.filter-select {
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.5);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  color: #fff;
  font-size: 12px;
  cursor: pointer;
  max-width: 200px;
}

.filter-select:hover {
  border-color: #4a9eff;
}

.filter-select:focus {
  outline: none;
  border-color: #4a9eff;
}

.pagination-info {
  margin-bottom: 20px;
  font-size: 12px;
  color: #888;
  text-align: center;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.pagination-text {
  font-size: 14px;
  color: #fff;
}

.pagination-controls .pixel-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-controls .pixel-button:disabled:hover {
  transform: none;
  border-color: rgba(255, 255, 255, 0.2);
}
</style>
