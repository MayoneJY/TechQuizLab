<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      
      <!-- Header -->
      <div class="page-header">
        <h2 class="page-title pixel-text">포트폴리오</h2>
        <button class="back-btn pixel-button primary" @click="goHome">홈으로</button>
      </div>

      <!-- Toolbar -->
      <div class="toolbar-section">
        <div class="search-container glass-input-wrapper">
          <input 
            v-model="searchKeyword" 
            type="text" 
            class="glass-input search-input" 
            placeholder="프로젝트 검색..."
            @keyup.enter="onSearch"
          />
          <button class="search-btn" @click="onSearch">🔍</button>
        </div>
        <button class="pixel-button primary new-project-btn" @click="goToCreate">
          + 새 프로젝트
        </button>
      </div>

      <!-- List -->
      <div v-if="isLoading" class="loading-state">
        <p class="pixel-text">로딩 중...</p>
      </div>

      <div v-else-if="portfolios.length === 0" class="empty-state">
         <img src="/assets/icons/icon-portfolio.png" alt="Empty" class="empty-icon-img" />
         <p class="pixel-text">프로젝트가 없습니다.</p>
         <button class="pixel-button primary" @click="goToCreate">첫 프로젝트 작성하기</button>
      </div>

      <div v-else class="portfolio-list">
        <div 
          v-for="pf in portfolios" 
          :key="pf.pfId" 
          class="portfolio-item clickable-item"
          @click="goToDetail(pf.pfId)"
        >
          <div class="icon-box">
            <img src="/assets/icons/icon-portfolio.png" class="icon-img" />
          </div>
          <div class="item-info">
             <div class="item-title pixel-text">{{ pf.title }}</div>
             <div class="item-date">{{ formatDate(pf.createdAt) }}</div>
          </div>
           <div class="arrow-icon">›</div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-container">
          <button 
            class="pagination-nav-btn prev"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            &lt;
          </button>
          
          <div class="page-numbers">
            <button
              v-for="page in getPageNumbers()"
              :key="page"
              class="page-number-btn pixel-text"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            class="pagination-nav-btn next"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            &gt;
          </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { portfolioApi } from '../services/api'
import type { Portfolio } from '../types/schema'

const router = useRouter()
const authStore = useAuthStore()

const portfolios = ref<Portfolio[]>([])
const isLoading = ref(false)
const searchKeyword = ref('')

// Pagination
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 10

function onSearch() {
  currentPage.value = 1
  fetchPortfolios()
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  await fetchPortfolios()
})

async function fetchPortfolios() {
  if (!authStore.user) return
  isLoading.value = true
  try {
    const response = await portfolioApi.getMyPortfolios(authStore.user.userId, {
        page: currentPage.value,
        size: pageSize,
        search: searchKeyword.value
    }) 
    const data = response.data
    portfolios.value = data.content
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Failed to fetch portfolios:', error)
  } finally {
    isLoading.value = false
  }
}

function goToDetail(id: number) {
  router.push(`/portfolio/${id}`)
}

function goToCreate() {
  router.push('/portfolio/new')
}

function goHome() {
  router.push('/')
}

function formatDate(dateString?: string) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}

function goToPage(page: number) {
    if (page < 1 || page > totalPages.value) return
    currentPage.value = page
    fetchPortfolios()
    window.scrollTo({ top: 0, behavior: 'smooth' })
}

function getPageNumbers() {
  const total = totalPages.value
  const current = currentPage.value
  const pages: number[] = []
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    let start = Math.max(1, current - 2)
    let end = Math.min(total, current + 2)
    if (end - start < 4) {
      if (start === 1) end = Math.min(total, start + 4)
      else if (end === total) start = Math.max(1, end - 4)
    }
    for (let i = start; i <= end; i++) pages.push(i)
  }
  return pages
}
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
  min-height: 100vh;
  
  /* Vertical Centering */
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 600px;
  background: rgba(0, 0, 0, 0.4); 
  border: 1px solid rgba(255,255,255,0.1);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(10px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 15px;
}

.page-title {
  color: #ffd43b;
  font-size: 24px;
  margin: 0;
}

.back-btn {
  font-size: 14px;
  padding: 8px 16px;
}

/* Toolbar */
.toolbar-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.search-container {
  display: flex;
  width: 300px;
  background: rgba(0,0,0,0.3);
  border-radius: 4px;
  border: 1px solid #444;
  overflow: hidden;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  padding: 10px;
  color: #fff;
  outline: none;
}

.search-btn {
  padding: 0 15px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 16px;
}

.new-project-btn {
  white-space: nowrap;
  padding: 10px 20px;
  font-weight: bold;
  background: transparent !important;
  border: 1px solid #4a9eff;
  color: #4a9eff;
}

.new-project-btn:hover {
  background: rgba(74, 158, 255, 0.1) !important;
  color: #fff;
  border-color: #fff;
}

/* List Items */
.portfolio-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.clickable-item {
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  padding: 15px;
  background: rgba(255,255,255,0.05); /* Match MyBattles item bg approximately */
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 15px;
}

.clickable-item:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(74, 158, 255, 0.5);
  transform: translateX(2px);
}

.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255,255,255,0.1);
}
.icon-img {
  width: 24px; image-rendering: pixelated; opacity: 0.8;
}

.item-info { flex: 1; }
.item-title { color: #fff; font-size: 16px; margin-bottom: 4px; font-weight: bold; }
.item-date { color: #888; font-size: 12px; }
.arrow-icon { color: #555; font-size: 20px; }
.clickable-item:hover .arrow-icon { color: #fff; }

.loading-state, .empty-state {
  text-align: center; padding: 60px 20px; color: #888;
  display: flex; flex-direction: column; align-items: center; gap: 15px;
}
.empty-icon-img { width: 64px; opacity: 0.3; filter: grayscale(1); }

/* Button Base */
.pixel-button {
  border: 1px solid rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.05);
  color: #ccc;
  transition: all 0.2s;
  cursor: pointer;
}
.pixel-button:hover { background: rgba(255,255,255,0.15); color: #fff; border-color: #fff; }
.pixel-button.primary { background: #4a9eff; border-color: #4a9eff; color: #fff; }
.pixel-button.primary:hover { background: #3b82f6; }


/* Pagination */
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
</style>
