<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      <div class="page-header">
        <h2 class="page-title pixel-text">전투 기록</h2>
        <button class="back-btn pixel-button" @click="router.push('/')">뒤로가기</button>
      </div>

      <!-- Toolbar: Search, Sort -->
      <div class="toolbar-section">
          <div class="search-container glass-input-wrapper">
              <input 
                  v-model="searchKeyword" 
                  @keyup.enter="handleSearch"
                  type="text" 
                  class="glass-input search-input" 
                  placeholder="스테이지 검색..."
              >
              <button class="search-btn" @click="handleSearch">🔍</button>
          </div>

          <div class="sort-dropdown-wrapper">
            <select v-model="sortOption" @change="handleSort" class="glass-input sort-select">
                <option value="latest">최신순</option>
                <option value="oldest">오래된순</option>
                <option value="score">점수순</option>
            </select>
          </div>
      </div>

      <!-- Category Tabs -->
      <div class="category-tabs">
        <button 
            v-for="cat in categories" 
            :key="cat.value"
            class="pixel-button tab-button" 
            :class="{ active: selectedCategory === cat.value }"
            @click="handleCategoryChange(cat.value)"
        >
            {{ cat.label }}
        </button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="skeleton-list">
          <div class="skeleton-item-card" v-for="i in 5" :key="i">
            <div class="skeleton skeleton-circle" style="width: 40px; height: 40px; border-radius: 10px;"></div>
            <div class="skeleton-content" style="flex: 1;">
               <div class="skeleton skeleton-text" style="width: 60%; height: 16px; margin-bottom: 5px;"></div>
               <div class="skeleton skeleton-text" style="width: 40%; height: 12px;"></div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="battles.length > 0" class="battle-list">
        <div 
          v-for="battle in battles" 
          :key="battle.battleId" 
          class="battle-item"
          :class="{ 'creating-item': battle.isTemp, 'clickable-item': !battle.isTemp }"
          @click="!battle.isTemp && router.push(`/battle-result/${battle.battleId}?from=battles`)"
        >
          <!-- Special Case: Generating -->
          <template v-if="battle.isTemp">
              <div class="rank-icon-wrapper">
                  <div class="rank-circle generating-rank">
                      <div class="loading-spinner-small"></div>
                  </div>
              </div>

              <div class="battle-info">
                  <span class="battle-stage">{{ battle.stageTitle }}</span>
                  <div class="battle-meta">
                      <span class="battle-category badge">{{ battle.jobCategory }}</span>
                      <span class="glitch-text-small">AI 면접관 생성중...</span>
                  </div>
              </div>
          </template>

          <template v-else>
            <!-- Rank Icon -->
            <div class="rank-icon-wrapper">
                <div class="rank-circle pixel-text" :class="isUnplayed(battle) ? 'rank-none' : getRankClass(battle.totalDamage)">
                    {{ isUnplayed(battle) ? '?' : getRank(battle.totalDamage) }}
                </div>
            </div>

            <div class="battle-info">
                <span class="battle-stage">{{ battle.stageTitle || '알 수 없는 스테이지' }}</span>
                <div class="battle-meta">
                    <span class="battle-category badge">{{ battle.jobCategory || 'General' }}</span>
                    <span class="battle-date">{{ formatDateTime(battle.createdAt) }}</span>
                    <span class="battle-score-text">
                        {{ isUnplayed(battle) ? '도전 대기' : `${battle.totalDamage}점` }}
                    </span>
                </div>
            </div>
            
            <!-- Start Button if unplayed -->
            <div v-if="isUnplayed(battle)" class="action-area" @click.stop>
                <button class="pixel-button primary small-btn" @click="handleStartBattle(battle.battleId)">
                    도전하기
                </button>
            </div>
            <div v-else class="arrow-icon">›</div>
          </template>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">⚔️</div>
        <p class="empty-text">아직 진행한 전투가 없습니다.</p>
        <p class="empty-subtext">지금 바로 모의 면접에 도전해보세요!</p>
        <button class="pixel-button primary big-btn" @click="router.push('/stages')">
            새로운 전투 시작하기
        </button>
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
import { battleApi } from '../services/api'

const router = useRouter()
const loading = ref(true)
const battles = ref<any[]>([])

// Filters & Pagination
const searchKeyword = ref('')
const sortOption = ref('latest')
const selectedCategory = ref('all')
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 10

const categories = ref([
    { label: '전체', value: 'all' }
])

async function fetchCategories() {
    try {
        const res = await battleApi.getBattleCategories()
        const myCats = res.data // List<String>
        
        if (myCats && myCats.length > 0) {
            const dynamicCats = myCats.filter((c: string) => c).map((cat: string) => ({
                label: cat,
                value: cat
            }))
            categories.value = [
                { label: '전체', value: 'all' },
                ...dynamicCats
            ]
        } else {
             // If no categories found (no battles), just keep 'All'
             categories.value = [{ label: '전체', value: 'all' }]
        }
    } catch (e) {
        console.error('Failed to fetch categories', e)
        // Fallback to minimal
        categories.value = [{ label: '전체', value: 'all' }]
    }
}

function getRank(score: number) {
  if (score >= 9000) return 'S'
  if (score >= 8000) return 'A'
  if (score >= 6000) return 'B'
  if (score >= 4000) return 'C'
  return 'F'
}

function getRankClass(score: number) {
  const rank = getRank(score)
  return `rank-${rank}`
}

async function fetchBattles() {
    loading.value = true;
    try {
        const res = await battleApi.getMyBattles({
            category: selectedCategory.value === 'all' ? undefined : selectedCategory.value,
            search: searchKeyword.value,
            sort: sortOption.value,
            page: currentPage.value,
            size: pageSize
        })
        // Assuming response structure from pagination implementation
        if (res.data && res.data.content) {
            battles.value = res.data.content;
            totalPages.value = res.data.totalPages;
            currentPage.value = res.data.currentPage;
        } else {
            // Fallback if API hasn't updated yet or structure mismatch
           battles.value = Array.isArray(res.data) ? res.data : [];
        }

    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}



function formatDateTime(dateStr: string) {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}.${month}.${day} ${hour}:${minute}`
}

function handleSearch() {
    currentPage.value = 1;
    fetchBattles();
}

function handleSort() {
    currentPage.value = 1;
    fetchBattles();
}

function handleCategoryChange(category: string) {
    selectedCategory.value = category;
    currentPage.value = 1;
    fetchBattles();
}

function goToPage(page: number) {
    if (page < 1 || page > totalPages.value) return;
    currentPage.value = page;
    fetchBattles();
    window.scrollTo({ top: 0, behavior: 'smooth' });
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

import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import { useRoute } from 'vue-router'

const route = useRoute()
const authStore = useAuthStore()
const modalStore = useModalStore()

// ... existing code ...

const isCreating = ref(false)

// Imports already at top

// ...

async function createBattleFromQuery() {
    const { createStageId, title, category } = route.query
    if (createStageId) {
        isCreating.value = true
        // Add temporary item
        const tempBattle = {
            battleId: -1,
            stageTitle: title || 'Loading...',
            jobCategory: category || 'General',
            totalDamage: 0,
            createdAt: new Date().toISOString(),
            isTemp: true
        }
        battles.value.unshift(tempBattle)

        try {
            await battleApi.createBattle({
                stageId: Number(createStageId),
                userId: authStore.user?.userId || 0
            })
            // Refresh list to show real item
            await fetchBattles()
            
            // Clean URL
            router.replace({ path: '/my-battles', query: {} })
        } catch (e: any) {
            console.error(e)
            battles.value.shift() // remove temp
            
            // Handle No Portfolio Error
            if (e.response?.data?.message?.toLowerCase().includes('portfolio') || e.message?.toLowerCase().includes('portfolio')) {
                if (await modalStore.openConfirm('포트폴리오가 필요합니다. 지금 생성하시겠습니까?')) {
                    router.push('/portfolio')
                }
            } else {
                await modalStore.openAlert(e.response?.data?.message || '전투 생성에 실패했습니다.')
            }
        } finally {
            isCreating.value = false
        }
    }
}

function handleStartBattle(battleId: number) {
    router.push(`/game?battleId=${battleId}`)
}

function isUnplayed(battle: any) {
    // If status is READY, it's unplayed
    return battle.status === 'READY' || (!battle.totalDamage && battle.status !== 'COMPLETED')
}

onMounted(async () => {
    await fetchCategories()
    await fetchBattles()
    await createBattleFromQuery()
})
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 500px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 10px;
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
    border-color: #ffd43b; /* Active border color */
    background: rgba(0,0,0,0.6);
}

.search-input {
    flex: 1;
    padding: 12px;
    border: none; /* Removed individual border */
    background: transparent; /* Transparent to show container bg */
    color: #fff;
    font-family: 'DungGeunMo', sans-serif;
    font-size: 14px;
    outline: none;
}

.search-input:focus {
    /* Focus handled by container */
}

.search-btn {
    padding: 0 20px;
    border: none; /* Removed individual border */
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
    transform: none;
}

.search-btn:active {
    background: #3a8eef;
    box-shadow: inset 2px 2px 0 rgba(0,0,0,0.2);
}

.sort-select {
    padding: 10px 16px;
    border-radius: 4px;
    background: #222;
    border: 2px solid #555;
    color: #fff;
    cursor: pointer;
    font-family: 'DungGeunMo', sans-serif;
    box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
    height: 44px; /* Match search height approx */
}

/* Categories - Matching Board.vue Style */
.category-tabs {
    padding: 10px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    border-radius: 12px;
    background: rgba(0,0,0,0.2); /* Added background for panel look if desired, or keep specific to match exact look */
    justify-content: center; /* Centered tabs */
    margin-bottom: 24px;
}

.tab-button {
  font-size: 13px;
  padding: 8px 16px;
  min-width: 60px;
  border-radius: 8px; /* Rounded consistent with Board */
  background: rgba(255, 255, 255, 0.1);
  border-color: transparent; /* Or border-width: 2px if keeping pixel border */
  border: 1px solid rgba(255,255,255,0.2); /* Refined border */
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

.tab-button.active:hover {
    cursor: default;
}


.battle-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.clickable-item {
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  padding: 15px;
  background: rgba(0,0,0,0.2);
  border: 1px solid transparent;
}

.clickable-item:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(74, 158, 255, 0.3);
  transform: translateX(2px);
}

.rank-circle {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  border: 2px solid #555;
  font-family: 'DungGeunMo', monospace;
}

.rank-S { 
    background: linear-gradient(135deg, #ffd43b, #fab005); 
    color: #000; 
    border-color: #fff;
    box-shadow: 0 0 10px rgba(255, 212, 59, 0.6);
    text-shadow: 1px 1px 0 rgba(255,255,255,0.5);
    font-size: 24px;
}
.rank-A { background: #51cf66; color: #fff; border-color: #2f9e44; }
.rank-B { background: #4a9eff; color: #fff; border-color: #228be6; }
.rank-C { background: #ced4da; color: #495057; border-color: #868e96; }
.rank-F { background: #ff6b6b; color: #fff; border-color: #fa5252; }
.rank-none { background: #444; color: #888; border-color: #666; border-style: dashed; }

.battle-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.battle-stage {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 4px;
}

.battle-meta {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: #888;
}

.badge {
    padding: 2px 6px;
    background: rgba(255,255,255,0.1);
    border-radius: 4px;
    font-size: 11px;
    color: #aaa;
}

.battle-score-text {
    color: #ffd43b;
    font-weight: bold;
}


.arrow-icon {
  color: #555;
  font-size: 20px;
}

.clickable-item:hover .arrow-icon {
  color: #fff;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #888;
  background: rgba(0,0,0,0.2);
  border-radius: 12px;
  border: 2px dashed rgba(255,255,255,0.1);
  margin-top: 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
  opacity: 0.5;
  filter: grayscale(100%);
}

.empty-text {
  font-size: 18px;
  color: #fff;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.empty-subtext {
  font-size: 14px;
  color: #888;
  margin: 0 0 30px 0;
}

.big-btn {
    padding: 12px 24px;
    font-size: 16px;
}

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
    border: 1px solid transparent; /* invisible border to prevent layout shift */
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

/* Skeleton Styles */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.skeleton-item-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
}
.skeleton {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}
.skeleton::after {
  content: "";
  position: absolute;
  top: 0; right: 0; bottom: 0; left: 0;
  transform: translateX(-100%);
  background-image: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  100% { transform: translateX(100%); }
}

/* Generating State Styles */
.creating-item {
    border-radius: 8px;
    padding: 15px;
    background: rgba(0,0,0,0.2);
    border: 1px solid transparent;
}

.generating-rank {
    background: transparent;
    border: none;
}

.loading-spinner-small {
    width: 24px;
    height: 24px;
    border: 3px solid transparent;
    border-top-color: #4a9eff;
    border-radius: 50%;
    animation: spin 1s infinite linear;
}

.glitch-text-small {
    color: #4a9eff;
    font-weight: bold;
    font-size: 13px;
    animation: glitch 1.5s infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

@keyframes glitch {
    0% { opacity: 1; transform: translate(0); }
    20% { opacity: 0.8; transform: translate(-1px, 0); }
    40% { opacity: 1; transform: translate(1px, 0); }
    60% { opacity: 1; transform: translate(0); }
    100% { opacity: 1; transform: translate(0); }
}

.small-btn {
    font-size: 12px;
    padding: 6px 12px;
}

</style>
