<template>
  <div class="stage-screen">

    
    <div class="stage-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="실전 모의면접">실전 모의면접</span>
        </h1>
        <button class="pixel-button link-button home-toolbar-btn" @click="goHome">
            <img :src="iconHome" class="btn-icon" /> 메인
        </button>
      </div>

      <!-- Toolbar: Search -->
      <div class="toolbar-section">
            <div class="search-container glass-input-wrapper">
              <input 
                  v-model="searchKeyword" 
                  @keyup.enter="handleSearch"
                  type="text" 
                  class="glass-input search-input" 
                  placeholder="기업명, 직무 검색..."
              >
              <button class="search-btn" @click="handleSearch">🔍</button>
          </div>

          <div class="sort-dropdown-wrapper">
             <div class="checkbox-wrapper">
                 <input type="checkbox" id="showClosed" v-model="showClosed" @change="fetchStages">
                 <label for="showClosed" class="pixel-text checkbox-label">마감된 공고 포함</label>
             </div>
             <select v-model="sortOption" @change="handleSort" class="glass-input sort-select">
                 <option value="deadline">마감임박순</option>
                 <option value="latest">최신순</option>
                 <option value="oldest">오래된순</option>
             </select>
          </div>
      </div>

      <!-- Category Tabs -->
      <div class="category-tabs">
        <button 
          class="pixel-button tab-button"
          :class="{ active: selectedJobCategories.length === 0 }"
          @click="clearFilters"
        >
          전체
        </button>
        <button
          v-for="option in jobCategoryOptions"
          :key="option.value"
          class="pixel-button tab-button"
          :class="{ active: selectedJobCategories.includes(option.value) }"
          @click="toggleJobCategory(option.value)"
        >
          {{ option.label }}
        </button>
      </div>

      <!-- Pagination Info -->
      <div v-if="!isLoading && totalElements > 0" class="pagination-info pixel-text">
        총 {{ totalElements }}개 중 {{ (page * size) + 1 }}-{{ Math.min((page + 1) * size, totalElements) }}개 표시
      </div>
      <div v-else-if="isLoading" class="pagination-info">
          <div class="skeleton skeleton-text" style="width: 200px; height: 16px; margin: 0 auto;"></div>
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
          @click="startChallenge(stage)"
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
      <div v-if="!isLoading && totalPages > 1" class="pagination-container">
        <button
          class="pagination-nav-btn prev"
          :disabled="page === 0"
          @click="changePage(page - 1)"
        >
        &lt;
        </button>

        <div class="page-numbers">
            <button
            v-for="p in getPageNumbers()"
            :key="p"
            class="page-number-btn pixel-text"
            :class="{ active: page + 1 === p }"
            @click="changePage(p - 1)"
            >
            {{ p }}
            </button>
        </div>

        <button
          class="pagination-nav-btn next"
          :disabled="page >= totalPages - 1"
          @click="changePage(page + 1)"
        >
          &gt;
        </button>
      </div>
    </div>

    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>

    <!-- Battle Creation Loading Overlay -->

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { stageApi } from '../services/api'
import type { Stage, StageDTO } from '../types/schema'
import { useModalStore } from '../stores/modal'
import PixelMonster from '../components/PixelMonster.vue'
import iconHome from '../assets/images/icon_home.png'

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

const searchKeyword = ref<string>('')
const sortOption = ref<string>('deadline') 
const showClosed = ref<boolean>(false) 

const stages = ref<Stage[]>([])
const isLoading = ref(false)

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
  if (isLoading.value) return
  isLoading.value = true
  try {
    const params: Record<string, any> = {
      page: page.value,
      size: size.value,
      sort: sortOption.value,
      showClosed: showClosed.value
    }
    
    if (selectedJobCategories.value.length > 0) {
      params.jobCategories = selectedJobCategories.value.join(',')
    }

    if (searchKeyword.value.trim() !== '') {
      params.keyword = searchKeyword.value.trim()
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

function handleSearch() {
  page.value = 0
  fetchStages()
}

function handleSort() {
  page.value = 0
  fetchStages()
}


function changePage(newPage: number) {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    fetchStages()
  }
}

function getPageNumbers() {
  const total = totalPages.value
  if (total <= 0) return []

  const current = page.value + 1 // 화면용(1-based)
  const maxButtons = 5

  // totalPages가 5 이하면 1..total 그대로
  if (total <= maxButtons) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }

  // 기본은 current 기준으로 좌우 2개씩 보이게
  let start = current - 2
  let end = current + 2

  // 범위 보정
  if (start < 1) {
    start = 1
    end = start + (maxButtons - 1)
  }
  if (end > total) {
    end = total
    start = end - (maxButtons - 1)
  }

  const pages: number[] = []
  for (let p = start; p <= end; p++) pages.push(p)
  return pages
}




async function startChallenge(stage: Stage) {
  if (!authStore.isAuthenticated) {
    if (await modalStore.openConfirm('로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?')) {
      router.push('/login')
    }
    return
  }

  // Redirect to My Battles with creation intent
  router.push({
    path: '/my-battles',
    query: {
        createStageId: stage.stageId,
        company: stage.companyName,
        title: stage.title,
        category: stage.jobCategory
    }
  })
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

/* Toolbar & Search */
.toolbar-section {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    align-items: center;
}

.search-container {
    flex: 1;
    display: flex;
    gap: 0;
    min-width: 250px;
    box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
    border-radius: 4px;
    overflow: hidden;
    border: 2px solid #555;
    transition: border-color 0.2s;
    background: rgba(0,0,0,0.4);
}

.search-container:focus-within {
    border-color: #ffd43b;
    background: rgba(0,0,0,0.6);
}

.search-input {
    flex: 1;
    padding: 12px;
    border: none;
    background: transparent;
    color: #fff;
    font-family: 'DungGeunMo', sans-serif;
    font-size: 14px;
    outline: none;
}

.search-btn {
    padding: 0 20px;
    border: none;
    background: #4a9eff;
    color: #fff;
    cursor: pointer;
    font-family: 'DungGeunMo', sans-serif;
    transition: all 0.1s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.search-btn:hover {
    background: #5bb0ff;
}

.sort-dropdown-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
}

.checkbox-wrapper {
    display: flex;
    align-items: center;
    gap: 6px;
    background: rgba(0,0,0,0.4);
    padding: 0 12px;
    height: 44px;
    border-radius: 4px;
    border: 2px solid #555;
    white-space: nowrap;
    flex-shrink: 0;
}

.checkbox-label {
    font-size: 13px;
    color: #ccc;
    cursor: pointer;
}

.sort-select {
    padding: 10px 16px;
    border-radius: 4px;
    background: rgba(0,0,0,0.6);
    border: 2px solid #555;
    color: #fff;
    cursor: pointer;
    font-family: 'DungGeunMo', sans-serif;
    box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
    height: 44px;
    outline: none;
}
.sort-select:focus {
    border-color: #ffd43b;
}

/* Category Tabs */
.category-tabs {
    padding: 10px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    border-radius: 12px;
    background: rgba(0,0,0,0.2);
    justify-content: center;
    margin-bottom: 24px;
}

.tab-button {
  font-size: 13px;
  padding: 8px 16px;
  min-width: 60px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-color: transparent;
  border: 1px solid rgba(255,255,255,0.2);
  color: #ccc;
  box-shadow: none;
  font-family: 'DungGeunMo', sans-serif;
  transition: all 0.2s;
}

.tab-button:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    color: #fff;
}

.tab-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  border-color: rgba(255,255,255,0.5);
  box-shadow: 0 0 15px rgba(74, 158, 255, 0.5);
  transform: translateY(-2px);
  color: #fff;
  font-weight: bold;
}

/* Home Button */
.home-toolbar-btn {
    min-width: auto;
    padding: 8px 16px;
    font-size: 14px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255,255,255,0.2);
    display: flex;
    align-items: center;
    gap: 4px;
    border-radius: 4px;
}

.home-toolbar-btn:hover {
    background: rgba(255, 255, 255, 0.2);
}

.btn-icon {
    width: 16px;
    height: 16px;
}


/* Pagination */
.pagination-info {
  margin-bottom: 20px;
  font-size: 12px;
  color: #888;
  text-align: center;
}

.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    padding: 20px 0;
    margin-top: 10px;
}

.pagination-nav-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    font-weight: bold;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pagination-nav-btn:disabled {
    opacity: 0.3;
    cursor: not-allowed;
}

.page-numbers {
    display: flex;
    gap: 6px;
}

.page-number-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid transparent; 
    background: transparent;
    color: #aaa;
    cursor: pointer;
    font-size: 14px;
}

.page-number-btn.active {
    background: #4a9eff;
    color: #fff;
    font-weight: bold;
    border: 1px solid #7cbcf0;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
}

.page-number-btn:hover:not(.active) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}


.pagination-controls .pixel-button:disabled:hover {
  transform: none;
  border-color: rgba(255, 255, 255, 0.2);
}


.page-numbers {
    display: flex;
    gap: 8px;
}

.page-number-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid transparent;
    background: transparent;
    color: #ccc;
    cursor: pointer;
    transition: all 0.2s;
    font-size: 14px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.page-number-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}

.page-number-btn.active {
    background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
    color: #fff;
    border: 1px solid #fff;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
    transform: translateY(-2px);
    font-weight: bold;
}


</style>
