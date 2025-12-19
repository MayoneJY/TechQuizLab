<template>
  <div class="game-container">
    <!-- Title Screen (Hero Section) -->
    <div class="dashboard-screen">
      
      <!-- HEADER: Title & Status -->
      <header class="dashboard-header">
        <h1 class="game-title pixel-text">
          <span class="title-main glitch" data-text="Job Space">Job Space</span>
        </h1>
        
        <!-- User Status Card Moved to Widget Column -->
      </header>

      <!-- MAIN CONTENT: Dashboard Grid -->
      <div v-if="authStore.isAuthenticated" class="dashboard-content">
        
        <!-- Left Column: Feature Navigation -->
        <div class="feature-grid">
          
          

          

          <!-- LIFE STATUS CARD -->
          <div class="feature-card glass-card life-card">
              <div class="life-header">
                  <span class="pixel-text">남은 도전 기회</span>
              </div>
              <div class="hearts-container">
                  <img 
                    v-for="i in 5" 
                    :key="i" 
                    :src="i > (authStore.user?.remainingLives ?? 5) ? '/assets/icons/icon-heart-empty.png' : '/assets/icons/icon-heart-full.png'"
                    alt="Life"
                    class="pixel-heart-img"
                  />
                  <span class="life-count">{{ authStore.user?.remainingLives ?? 5 }} / 5</span>
              </div>
          </div>

          <!-- CARD 1: BATTLE (Main Action) -->
          <div class="feature-card glass-card battle-card" @click="goToStages">
            <img src="/assets/icons/icon-battle.png" alt="Battle" class="card-icon-img" />
            <div class="card-info card-info-battle">
              <h3 class="pixel-text">실전 모의면접</h3>
              <p>기업별 공고에 도전하고<br>면접 스킬을 겨루세요!</p>
            </div>
            <div class="card-action">
              도전하기
            </div>
          </div>

          <!-- CARD 2: GAMIFICATION -->
          <div class="feature-card glass-card" @click="goToGamification">
            <img src="/assets/icons/icon-mission.png" alt="Mission" class="card-icon-img" />
            <div class="card-info">
              <h3 class="pixel-text">미션 & 랭킹</h3>
              <p>나의 성장을 확인하세요.</p>
            </div>
          </div>

          <!-- CARD 3: PORTFOLIO -->
          <div class="feature-card glass-card" @click="goToPortfolio">
            <img src="/assets/icons/icon-portfolio.png" alt="Portfolio" class="card-icon-img" />
            <div class="card-info">
              <h3 class="pixel-text">포트폴리오</h3>
              <p>나만의 이력을 관리하세요.</p>
            </div>
          </div>

          <!-- CARD 4: COMMUNITY -->
          <div class="feature-card glass-card" @click="goToBoard">
            <img src="/assets/icons/icon-community.png" alt="Community" class="card-icon-img" />
            <div class="card-info">
              <h3 class="pixel-text">커뮤니티</h3>
              <p>정보를 공유하고 소통하세요.</p>
            </div>
          </div>

          <!-- CARD 6: BOOKMARK PRACTICE (Review) -->
          <div class="feature-card glass-card" @click="startPractice">
            <img src="/assets/icons/icon-practice.png" alt="Practice" class="card-icon-img" />
            <div class="card-info">
              <h3 class="pixel-text">오답 복습</h3>
              <p>북마크한 문제를 다시 풀어보세요.</p>
            </div>
          </div>

          <!-- CARD 5: SYSTEM (My Info) -->
          <div class="feature-card glass-card low-card" @click="goToMyPage">
            <!-- <div class="card-icon">⚙️</div> -->
            <div class="card-info">
              <h3 class="pixel-text">내 정보</h3>
              <p>프로필 수정 및 관리</p>
            </div>
          </div>

          <!-- CARD 7: LOGOUT -->
          <div class="feature-card glass-card logout-card low-card" @click="handleLogout">
            <!-- <div class="card-icon">🚪</div> -->
            <div class="card-info">
              <h3 class="pixel-text">로그아웃</h3>
              <p>접속을 종료합니다.</p>
            </div>
          </div>
        </div>

        <!-- Right Column: Widgets -->
        <div class="widget-column">
          
          <!-- Widget: User Info (Moved) -->
          <div class="dashboard-widget glass-panel clickable-card user-info-widget" @click="goToMyPage">
             <template v-if="isDashboardLoading">
                <div class="user-profile-header">
                  <div class="skeleton skeleton-text" style="width: 100px; height: 26px;"></div>
                  <div class="skeleton skeleton-text" style="width: 50px; height: 26px;"></div>
                </div>
                <div class="exp-bar-container">
                   <div class="skeleton skeleton-bar" style="width: 100%; height: 12px; margin-bottom: 5px; border-radius: 6px;"></div>
                   <div class="skeleton skeleton-text" style="width: 80px; height: 12px; float: right;"></div>
                </div>
             </template>
             <template v-else>
                <div class="user-profile-header">
                  <span class="user-name pixel-text">{{ authStore.user?.nickname }}</span>
                  <span class="user-level pixel-text">Lv.{{ authStore.user?.level || 1 }}</span>
                </div>
                
                <div class="exp-bar-container">
                  <div class="exp-bar">
                    <div 
                      class="exp-fill" 
                      :style="{ width: `${expPercentage}%` }"
                    ></div>
                  </div>
                  <span class="exp-text pixel-text">{{ authStore.user?.exp || 0 }} / {{ maxExp }} EXP</span>
                </div>
             </template>
          </div>

          <!-- Widget: Daily Mission Status -->
          <div class="dashboard-widget glass-panel">
            <h3 class="widget-title pixel-text">오늘의 미션</h3>
            
            <template v-if="isDashboardLoading">
              <div class="mini-mission-list">
                 <div class="mini-mission-item" v-for="i in 3" :key="i">
                    <div class="skeleton skeleton-box" style="width: 16px; height: 16px; border-radius: 4px; padding: 0; margin: 0; margin-bottom: 5px;"></div>
                    <div class="skeleton skeleton-text" style="width: 70%; height: 16px; padding: 0; margin: 0; margin-bottom: 5px;"></div>
                 </div>
              </div>
            </template>
            <template v-else>
              <div v-if="dailyMissions.length > 0" class="mini-mission-list">
                <div 
                  v-for="mission in dailyMissions" 
                  :key="mission.missionId"
                  class="mini-mission-item"
                  :class="{ completed: mission.isCompleted }"
                >
                  <div class="mini-mission-icon">
                    <img 
                      :src="mission.isCompleted ? '/assets/icons/icon-check-on.png' : '/assets/icons/icon-check-off.png'" 
                      alt="Check"
                      class="checkbox-icon"
                    />
                  </div>
                  <div class="mini-mission-name">
                    {{ mission.mission?.title || '일일 미션' }}
                    <span class="mini-mission-progress">
                      ({{ mission.currentCount }}/{{ mission.mission?.goalCount }})
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-widget-text">
                미션을 불러오는 중...
              </div>
            </template>
          </div>

          <!-- Widget: Rival Status -->
          <div v-if="rival" class="dashboard-widget glass-panel rival-widget">
            <h3 class="widget-title pixel-text">RIVAL STATUS</h3>
            <div class="rival-content">
              <div class="rival-avartar">👾</div>
              <div class="rival-info">
                <div class="rival-name">{{ rival.nickname }}</div>
                <div class="rival-score">{{ rival.solvedCount }} 문제 해결</div>
              </div>
            </div>
          </div>

          <!-- Widget: Recent Battles -->
          <div class="dashboard-widget glass-panel">
            <div class="widget-header-row">
              <h3 class="widget-title pixel-text">최근 전투 기록</h3>
              <span class="view-all-btn" @click="router.push('/my-battles')">전체보기 ›</span>
            </div>
            
            <template v-if="isDashboardLoading">
               <div class="skeleton-list">
                 <div class="skeleton-item-card" v-for="i in 3" :key="i">
                    <div class="skeleton skeleton-circle" style="width: 40px; height: 40px; border-radius: 50%;"></div>
                    <div class="skeleton-content" style="flex: 1;">
                        <div class="skeleton skeleton-text" style="width: 60%; height: 20px; margin-bottom: 2px;"></div>
                        <div class="skeleton skeleton-text" style="width: 40%; height: 12px;"></div>
                    </div>
                 </div>
               </div>
            </template>
            <template v-else>
               <div v-if="recentBattles.length > 0">
                 <div class="battle-list">
                    <div 
                      v-for="battle in recentBattles.slice(0, 3)" 
                      :key="battle.battleId" 
                      class="battle-item clickable-item"
                      @click="router.push(`/battle-result/${battle.battleId}?from=home`)"
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
                              <span class="battle-date">{{ new Date(battle.createdAt).toLocaleDateString() }}</span>
                              <span class="battle-score-text">{{ battle.totalDamage }}점</span>
                          </div>
                      </div>
                      
                      <div class="arrow-icon">›</div>
                    </div>
                 </div>
               </div>
               <div v-else class="empty-widget-text">
                  아직 전투 기록이 없습니다.
               </div>
            </template>
          </div>

          <!-- Widget: Bookmarks -->
          <div class="dashboard-widget glass-panel">
            <div class="widget-header-row">
              <h3 class="widget-title pixel-text">내 오답노트</h3>
              <span class="view-all-btn" @click="router.push('/my-bookmarks')">전체보기 ›</span>
            </div>

            <template v-if="isDashboardLoading">
               <div class="skeleton-list">
                 <div class="skeleton-item-card" v-for="i in 3" :key="i">
                    <div class="skeleton skeleton-box" style="width: 24px; height: 24px; border-radius: 4px;"></div>
                    <div class="skeleton-content" style="flex: 1;">
                         <div class="skeleton skeleton-text" style="width: 90%; height: 16px; margin-bottom: 6px;"></div>
                         <div class="skeleton skeleton-text" style="width: 50%; height: 12px;"></div>
                    </div>
                 </div>
               </div>
            </template>
            <template v-else>
               <div v-if="recentBookmarks.length > 0">
                 <div class="bookmark-list">
                    <div 
                      v-for="bookmark in recentBookmarks.slice(0, 3)" 
                      :key="bookmark.bookmarkId" 
                      class="bookmark-item clickable-item"
                      @click="router.push(`/bookmarks/${bookmark.bookmarkId}`)"
                    >
                      <div class="bookmark-icon-wrapper">
                          <span class="bookmark-icon">📑</span>
                      </div>
                      <div class="bookmark-text">
                          <div class="bookmark-q">{{ bookmark.questionText || '질문 내용 없음' }}</div>
                          <div class="bookmark-date">
                              {{ new Date(bookmark.createdAt).toLocaleDateString() }}
                          </div>
                      </div>

                      <div class="arrow-icon">›</div>
                    </div>
                 </div>
               </div>
               <div v-else class="empty-widget-text">
                  저장된 북마크가 없습니다.
               </div>
            </template>
          </div>

        </div>
      </div>

      <!-- GUEST VIEW -->
      <div v-else class="guest-view">
        <div class="hearts-display">
           <img v-for="i in 5" :key="i" src="/assets/icons/icon-heart-full.png" class="pixel-heart-img" />
        </div>
        <p class="guest-msg pixel-text">로그인이 필요합니다</p>
        <button class="pixel-button primary big-button" @click="goToLogin">
          게임 시작
        </button>
      </div>

    </div>

    <!-- Background Monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
    <div class="monster monster-2">
      <PixelMonster type="alien" />
    </div>
    <div class="planet">
      <PixelPlanet type="earth" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGamificationStore } from '../stores/gamification'
import { useModalStore } from '../stores/modal'
import { battleApi } from '../services/api'
import PixelMonster from '../components/PixelMonster.vue'
import PixelPlanet from '../components/PixelPlanet.vue'

const router = useRouter()
const authStore = useAuthStore()
const gamificationStore = useGamificationStore()
const modalStore = useModalStore()

// Interfaces for Dashboard Data
interface DashboardBattle {
  battleId: number
  userId: number
  stageId: number
  totalDamage: number
  stageTitle?: string
  createdAt: string
}

interface DashboardBookmark {
  bookmarkId: number
  refBattleId: number
  questionText?: string
  memo?: string
  createdAt: string
}

const isDashboardLoading = ref(true)
const recentBattles = ref<DashboardBattle[]>([])
const recentBookmarks = ref<DashboardBookmark[]>([])

const dailyMissions = computed(() => gamificationStore.dailyMissions)
const rival = computed(() => gamificationStore.friends.find(f => f.isRival))

// Mock max exp logic (could be from store config)
const maxExp = computed(() => (authStore.user?.level || 1) * 1000)
const expPercentage = computed(() => {
  if (!authStore.user?.exp) return 0
  return Math.min((authStore.user.exp / maxExp.value) * 100, 100)
})

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

onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      isDashboardLoading.value = true
      // Request 1: Fetch Missions (Triggers Auto-Complete & EXP Reward on Server)
      await gamificationStore.fetchDailyMissions()
      
      // Request 2: Fetch User (Gets updated EXP/Level)
      await authStore.fetchUser()

      // Request 3: Others
      await gamificationStore.fetchFriends()

      // Request 4: Battles & Bookmarks
      const battlesRes = await battleApi.getMyBattles({ page: 1, size: 5 })
      recentBattles.value = battlesRes.data.content || []

      const bookmarksRes = await battleApi.getMyBookmarks({ page: 1, size: 5 })
      recentBookmarks.value = bookmarksRes.data.content || []
      
    } catch (e) {
      console.error('Failed to fetch dashboard data', e)
    } finally {
      isDashboardLoading.value = false
    }
  }
})

// Navigation
function goToLogin() { router.push('/login') }
async function handleLogout() { 
  if (await modalStore.openConfirm('정말 로그아웃 하시겠습니까?')) {
    authStore.logout() 
    router.push('/login')
  }
} 
function goToStages() { 
  if ((authStore.user?.remainingLives ?? 0) <= 0) {
    // Alert is synchronous, modal is async but we just return here anyway. 
    // Ideally we should await it if we wanted to block code, but here we just show and return.
    // However, best practice is to await to ensure it opens before navigation logic (though here it returns).
    // Marking async to be safe.
    modalStore.openAlert('오늘의 도전 횟수를 모두 소진했습니다. 내일 다시 도전해주세요!');
    return;
  }
  router.push('/stages');
}
function goToGamification() { router.push('/gamification') }
function goToPortfolio() { router.push('/portfolio') }
function goToBoard() { router.push('/board') }
function goToMyPage() { router.push('/mypage') }

async function startPractice() {
  if (await modalStore.openConfirm('오답노트에 저장된 문제로 연습 게임을 시작하시겠습니까?')) {
    try {
      const response = await battleApi.createPracticeBattle()
      router.push(`/game?battleId=${response.data.battleId}`)
    } catch (error: any) {
      console.error(error)
      await modalStore.openAlert(error.response?.data?.message || '연습 게임 생성에 실패했습니다. 북마크된 문제가 있는지 확인해주세요.')
    }
  }
}

</script>

<style scoped>
.dashboard-screen {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 40px;
  box-sizing: border-box;
  z-index: 10;
  position: relative;
  min-height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Skeleton Effect */
.skeleton {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
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

.skeleton-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px;
}

/* NEW SKELETON CLASSES */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 8px;
  background: rgba(255,255,255,0.05);
  border-radius: 8px;
}

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}


/* Header */
.dashboard-header {
  width: 100%;
  text-align: center;
  /* margin-bottom: 20px; */
  animation: slideDown 0.8s ease-out;
}

.game-title {
  /* margin-bottom: 20px; */
}

.title-main {
  font-size: 72px;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  display: block;
  font-weight: 900;
}

/* User Status Card */
/* User Info Within Feature Grid */
.user-info-widget {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.clickable-card {
  cursor: pointer;
  transition: all 0.3s;
}

.clickable-card:hover {
  transform: translateY(-2px);
  border-color: rgba(74, 158, 255, 0.5);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.2);
}

.user-profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.user-level {
  color: #4a9eff;
}

/* EXP Bar */
.exp-bar-container {
  width: 100%;
}

.exp-bar {
  width: 100%;
  height: 12px;
  background: rgba(0,0,0,0.5);
  border: 2px solid #fff;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 5px;
}

.exp-fill {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff, #5bb0ff);
  transition: width 0.5s ease-out;
}

.exp-text {
  font-size: 12px;
  color: rgba(255,255,255,0.7);
  float: right;
}

/* Dashboard Content Grid */
.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 300px; 
  gap: 20px;
  width: 100%;
  animation: fadeIn 1s ease-out;
  align-items: start; /* Prevent columns from stretching to same height */
}

@media (max-width: 768px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

/* Feature Grid */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.feature-card {
  padding: 15px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  padding-top: 40px;
  padding-bottom: 40px;
  gap: 8px;
  align-items: center;
  text-align: center;
  border-width: 2px;
  min-height: 120px;
  justify-content: center;
}

.feature-card:hover {
  border-color: #ffd43b;
}

.card-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 2px rgba(0,0,0,0.5));
}

.card-icon-img {
  width: 48px;
  height: 48px;
  object-fit: contain; /* Preserve aspect ratio */
  image-rendering: pixelated;
  filter: drop-shadow(2px 2px 0 rgba(0,0,0,0.3));
  margin-bottom: 5px;
}

.card-info h3 {
  color: #fff;
  margin: 0;
  font-size: 16px;
  margin-bottom: 5px;
}

.card-info p {
  color: #ccc;
  font-size: 12px;
  margin: 0;
  line-height: 1.4;
}

.low-card {
  padding-top: 10px;
  padding-bottom: 10px;
}

/* Battle Card Special Style */
.battle-card {
  grid-column: span 2; 
  background: rgba(74, 158, 255, 0.15); 
  border-color: #4a9eff;
  flex-direction: row; 
  text-align: left;
  align-items: center;
  gap: 20px;
  justify-content: space-between;
  padding-left: 40px;
  padding-right: 40px;
}

.card-info-battle {
  padding-left: 10px;
}

.battle-card .card-info {
  flex: 1;
  text-align: left;
}

.battle-card .card-info h3 {
  font-size: 20px;
  color: #ffd43b;
}

.card-action {
  background: #ff6b6b;
  color: #fff;
  padding: 6px 16px;
  border-radius: 4px;
  font-weight: 700;
  font-size: 13px;
  box-shadow: 0 3px 0 #c92a2a;
}

.battle-card:hover .card-action {
  transform: scale(1.05);
}

.practice-card {
  border-color: #51cf66;
  background: rgba(81, 207, 102, 0.1);
}

.logout-card {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.logout-card:hover {
  border-color: #fa5252;
  background: rgba(255, 107, 107, 0.2);
}

/* Life Card (Separate, Compact) */
.life-card {
  grid-column: span 2;
  background: rgba(0, 0, 0, 0.3);
  border-color: #ff6b6b;
  min-height: auto;
  padding: 12px 20px;
  cursor: default;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  gap: 0;
}

.life-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 0;
  color: #ff6b6b;
  font-weight: 700;
  font-size: 14px;
}

.hearts-container {
  display: flex;
  align-items: center;
  gap: 6px;
}

.life-count {
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 700;
  margin-left: 5px;
}


/* Heart Images */
.hearts-container .pixel-heart-img {
  width: 20px;
  height: 20px;
  object-fit: contain; /* Preserve aspect ratio */
  image-rendering: pixelated;
  /* No filter needed as we swap images */
}

/* Checkbox Images */
.checkbox-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
  image-rendering: pixelated;
  display: block;
}

/* Widget Column */
.widget-column {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.dashboard-widget {
  padding: 15px;
  border-radius: 12px;
}

.widget-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 5px;
}

.widget-title {
  font-size: 14px;
  color: #4a9eff;
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.view-all-btn {
  font-size: 12px;
  color: #888;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s;
}

.view-all-btn:hover {
  color: #ffd43b;
}

.mini-mission-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-mission-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #ccc;
  padding: 5px;
  border-radius: 4px;
  background: rgba(0,0,0,0.2);
}

.mini-mission-progress {
  font-size: 11px;
  color: #888;
  margin-left: 5px;
}

.mini-mission-item.completed {
  color: #51cf66;
  background: rgba(81, 207, 102, 0.1);
}

.rival-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.rival-avartar {
  font-size: 24px;
  background: rgba(255, 107, 107, 0.2);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 1px solid #ff6b6b;
}

.rival-info {
  display: flex;
  flex-direction: column;
}

.rival-name {
  color: #fff;
  font-weight: 700;
  font-size: 14px;
}

.rival-score {
  color: #888;
  font-size: 12px;
}

/* Guest View */
.guest-view {
  text-align: center;
  margin-top: 50px;
  animation: fadeIn 1s;
}

.guest-msg {
  color: #fff;
  font-size: 18px;
  margin: 20px 0;
}

.big-button {
  font-size: 16px;
  padding: 12px 30px;
}

/* Animations */
@keyframes slideDown {
  from { transform: translateY(-50px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.monster {
  position: fixed;
  z-index: 5;
  pointer-events: none;
}

.monster-1 { top: 15%; left: 5%; animation: float 6s infinite ease-in-out; }
.monster-2 { bottom: 15%; right: 5%; animation: float 5s infinite ease-in-out reverse; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}

.planet {
  position: fixed;
  top: 10%;
  right: 15%;
  z-index: 1;
  pointer-events: none;
  opacity: 0.8;
  transform: scale(2);
}

/* --- NEW COMPONENT STYLES --- */

/* Updated List Styles */
.battle-list, .bookmark-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.clickable-item {
    cursor: pointer;
    transition: all 0.2s;
    border-radius: 8px;
    padding: 10px;
    background: rgba(0,0,0,0.2);
    border: 1px solid transparent;
}
.clickable-item:hover {
    background: rgba(255,255,255,0.1);
    border-color: rgba(74, 158, 255, 0.3);
    transform: translateX(2px);
}

.battle-item {
    display: flex;
    align-items: center;
    gap: 12px;
}

.rank-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
}

.rank-circle {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background: #333;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
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
    font-size: 18px; 
}
.rank-A { background: #51cf66; color: #fff; border-color: #2f9e44; }
.rank-B { background: #4a9eff; color: #fff; border-color: #228be6; }
.rank-C { background: #ced4da; color: #495057; border-color: #868e96; }
.rank-F { background: #ff6b6b; color: #fff; border-color: #fa5252; }

.battle-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.battle-stage {
    font-size: 14px;
    color: #fff;
    font-weight: 600;
    margin-bottom: 2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.battle-meta {
    display: flex;
    gap: 8px;
    font-size: 11px;
    color: #888;
}

.battle-score-text {
    color: #ced4da;
}

.arrow-icon {
    font-size: 18px;
    color: #555;
}

.clickable-item:hover .arrow-icon {
    color: #fff;
}

/* Bookmark Styles */
.bookmark-item {
    display: flex;
    gap: 12px;
    align-items: flex-start;
    padding: 11px;
}

.bookmark-icon-wrapper {
    width: 32px;
    height: 32px;
    background: rgba(255, 212, 59, 0.1);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
}

.bookmark-text {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    gap: 0px;
}

.bookmark-q {
    font-size: 13px;
    color: #eee;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.bookmark-date {
    font-size: 11px;
    color: #888;
    margin-top: 0px;
}

.empty-widget-text {
    color: #666;
    font-size: 12px;
    text-align: center;
    padding: 20px 0;
    background: rgba(0,0,0,0.1);
    border-radius: 8px;
}

.widget-footer {
    display: none;
}

</style>
