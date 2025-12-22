<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      <div class="page-header">
        <h2 class="page-title pixel-text">전투 기록</h2>
        <button class="back-btn pixel-button" @click="router.back()">뒤로가기</button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="skeleton-list">
          <div class="skeleton-item-card" v-for="i in 5" :key="i">
            <div class="skeleton skeleton-circle" style="width: 40px; height: 40px; border-radius: 50%;"></div>
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
          class="battle-item clickable-item"
          @click="router.push(`/battle-result/${battle.battleId}`)"
        >
          <!-- Rank Icon -->
          <div class="rank-icon-wrapper">
              <div class="rank-circle pixel-text" :class="getRankClass(battle.totalDamage)">
                  {{ getRank(battle.totalDamage) }}
              </div>
          </div>

          <div class="battle-info">
              <span class="battle-stage">{{ battle.stageTitle || '알 수 없는 스테이지' }}</span>
              <div class="battle-meta">
                  <span class="battle-date">{{ new Date(battle.createdAt).toLocaleString() }}</span>
                  <span class="battle-score-text">{{ battle.totalDamage }}점</span>
              </div>
          </div>
          
          <div class="arrow-icon">›</div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>전투 기록이 없습니다.</p>
        <button class="pixel-button primary" @click="router.push('/stages')">전투 시작하기</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { battleApi } from '../services/api'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import { useBattleGlobalStore } from '../stores/battleGlobal'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const modalStore = useModalStore()
const battleGlobalStore = useBattleGlobalStore()

const loading = ref(true)
const battles = ref<any[]>([])

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
        syncPendingBattles()
    }
}

function syncPendingBattles() {
    // Get pending list from global store
    const pendingList = battleGlobalStore.pendingBattles

    pendingList.forEach(p => {
        // Check if this pending battle is already active/completed in the loaded list
        // Note: The API response 'battles' should contain stageId.
        const exists = battles.value.some(b => {
            if (b.stageId !== p.stageId || b.isTemp) return false
             // Same timestamp logic as Global Store
            const battleCreated = new Date(b.createdAt).getTime()
            return battleCreated > p.timestamp
        })

        if (!exists) {
            // It's not in the list yet, show it as generating
            // But don't duplicate if we already have a temp item in view
            const viewDuplicate = battles.value.find(b => b.isTemp && b.stageId === p.stageId)
            
            if (!viewDuplicate) {
                const tempBattle = {
                    battleId: -1, 
                    stageId: p.stageId,
                    stageTitle: p.title,
                    jobCategory: p.category,
                    totalDamage: 0,
                    createdAt: new Date(p.timestamp).toISOString(),
                    isTemp: true
                }
                battles.value.unshift(tempBattle)
            }
        }
    })
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

// Imports and consts moved to top

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
        
        // Mark as pending in Global Store immediately
        battleGlobalStore.addPendingBattle({
            stageId: Number(createStageId),
            title: (title as string) || '제목 없음',
            category: (category as string) || 'General',
            timestamp: Date.now()
        })

        try {
            await battleApi.createBattle({
                stageId: Number(createStageId),
                userId: authStore.user?.userId || 0
            })

            // Check if user is still on the page
            if (router.currentRoute.value.path !== '/my-battles') {
                // User navigated away.
                // Leave it in pending so global store picks it up and shows Toast.
                return
            }

            // If we are here, we are on the page.
            // Refresh list - syncPendingBattles will handle cleanup if it appears
            await fetchBattles()
            
            // Clean URL
            router.replace({ path: '/my-battles', query: {} })
            // Also explicitly remove from global just in case to avoid race with poll
            battleGlobalStore.removePendingBattle(Number(createStageId))
        } catch (e: any) {
            console.error(e)
            battles.value.shift() // remove temp
            
            // Remove from pending since it failed
            battleGlobalStore.removePendingBattle(Number(createStageId))
            
            // Handle No Portfolio Error
            if (e.response?.data?.message?.toLowerCase().includes('portfolio') || e.message?.toLowerCase().includes('portfolio')) {
                if (await modalStore.openConfirm('포트폴리오가 필요합니다. 지금 생성하시겠습니까?')) {
                    router.push('/portfolio')
                }
            } 
            // Handle Life Exhausted Error
            else if (e.response?.data?.message?.includes('[LIFE_EXHAUSTED]') || e.message?.includes('[LIFE_EXHAUSTED]')) {
                 if (await modalStore.openConfirm('오늘의 도전 횟수를 모두 소진했습니다.\n충전 페이지로 이동하시겠습니까?')) {
                     router.push('/charge')
                 }
            }
            else {
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 400px;
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

.battle-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.clickable-item {
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  padding: 15px;
  background: rgba(0,0,0,0.2);
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 15px;
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

.battle-date {
  font-size: 13px;
  color: #888;
  margin-right: 12px;
}

.arrow-icon {
  color: #555;
  font-size: 20px;
}

.clickable-item:hover .arrow-icon {
  color: #fff;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
  color: #888;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.skeleton-item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: rgba(255,255,255,0.05);
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
</style>
