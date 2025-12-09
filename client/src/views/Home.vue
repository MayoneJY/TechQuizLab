<template>
  <div class="game-container">
    <!-- Title Screen (Hero Section) -->
    <div class="dashboard-screen">
      
      <!-- HEADER: Title & Status -->
      <header class="dashboard-header">
        <h1 class="game-title pixel-text">
          <span class="title-main glitch" data-text="잡스페이스">잡스페이스</span>
        </h1>
        
        <!-- User Status Card with Skeleton -->
        <div v-if="authStore.isAuthenticated" class="user-status-card glass-panel clickable-card" @click="goToMyPage">
          <template v-if="isDashboardLoading">
            <div class="user-profile-header">
              <div class="skeleton skeleton-text" style="width: 100px; height: 28px;"></div>
              <div class="skeleton skeleton-text" style="width: 50px; height: 28px;"></div>
            </div>
            <div class="exp-bar-container">
               <div class="skeleton skeleton-bar" style="width: 100%; height: 12px; margin-bottom: 5px; border-radius: 6px;"></div>
               <div class="skeleton skeleton-text" style="width: 80px; height: 20px; float: right;"></div>
            </div>
          </template>
          <template v-else>
            <div class="user-profile-header">
              <span class="user-name pixel-text">{{ authStore.user?.nickname }}</span>
              <span class="user-level pixel-text">Lv.{{ authStore.user?.level || 1 }}</span>
            </div>
            
            <!-- Experience Bar -->
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
      </header>

      <!-- MAIN CONTENT: Dashboard Grid -->
      <div v-if="authStore.isAuthenticated" class="dashboard-content">
        
        <!-- Left Column: Feature Navigation -->
        <div class="feature-grid">
          <!-- CARD 1: BATTLE (Main Action) -->
          <div class="feature-card glass-card battle-card" @click="goToStages">
            <div class="card-icon">⚔️</div>
            <div class="card-info">
              <h3 class="pixel-text">실전 모의면접</h3>
              <p>기업별 공고에 도전하고<br>면접 스킬을 겨루세요!</p>
            </div>
            <div class="card-action">
              도전하기
            </div>
          </div>

          <!-- CARD 2: GAMIFICATION -->
          <div class="feature-card glass-card" @click="goToGamification">
            <div class="card-icon">🏆</div>
            <div class="card-info">
              <h3 class="pixel-text">미션 & 랭킹</h3>
              <p>나의 성장을 확인하세요.</p>
            </div>
          </div>

          <!-- CARD 3: PORTFOLIO -->
          <div class="feature-card glass-card" @click="goToPortfolio">
            <div class="card-icon">📂</div>
            <div class="card-info">
              <h3 class="pixel-text">포트폴리오</h3>
              <p>나만의 이력을 관리하세요.</p>
            </div>
          </div>

          <!-- CARD 4: COMMUNITY -->
          <div class="feature-card glass-card" @click="goToBoard">
            <div class="card-icon">💬</div>
            <div class="card-info">
              <h3 class="pixel-text">커뮤니티</h3>
              <p>정보를 공유하고 소통하세요.</p>
            </div>
          </div>

          <!-- CARD 5: SYSTEM (My Page) -->
          <div class="feature-card glass-card" @click="goToMyPage">
            <div class="card-icon">⚙️</div>
            <div class="card-info">
              <h3 class="pixel-text">시스템</h3>
              <p>내 정보 수정 및 로그아웃</p>
            </div>
          </div>
        </div>

        <!-- Right Column: Widgets -->
        <div class="widget-column">
          
          <!-- Widget: Daily Mission Status -->
          <div class="dashboard-widget glass-panel">
            <h3 class="widget-title pixel-text">오늘의 미션</h3>
            
            <template v-if="isDashboardLoading">
              <div class="mini-mission-list">
                 <div class="mini-mission-item skeleton-item" v-for="i in 3" :key="i">
                    <div class="skeleton skeleton-box" style="width: 20px; height: 20px; border-radius: 4px;"></div>
                    <div class="skeleton skeleton-text" style="width: 70%; height: 16px;"></div>
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
                    {{ mission.isCompleted ? '✅' : '⬜' }}
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

        </div>
      </div>

      <!-- GUEST VIEW -->
      <div v-else class="guest-view">
        <div class="hearts-display">
           <PixelHeart v-for="_ in 5" :key="`heart-${_}`" />
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGamificationStore } from '../stores/gamification'
import PixelHeart from '../components/PixelHeart.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const authStore = useAuthStore()
const gamificationStore = useGamificationStore()

const isDashboardLoading = ref(true)

const dailyMissions = computed(() => gamificationStore.dailyMissions)
const rival = computed(() => gamificationStore.friends.find(f => f.isRival))

// Mock max exp logic (could be from store config)
const maxExp = computed(() => (authStore.user?.level || 1) * 1000)
const expPercentage = computed(() => {
  if (!authStore.user?.exp) return 0
  return Math.min((authStore.user.exp / maxExp.value) * 100, 100)
})

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
    } catch (e) {
      console.error('Failed to fetch dashboard data', e)
    } finally {
      isDashboardLoading.value = false
    }
  }
})

// Navigation
function goToLogin() { router.push('/login') }
// function handleLogout() { authStore.logout() } 
function goToStages() { router.push('/stages') }
function goToGamification() { router.push('/gamification') }
function goToPortfolio() { router.push('/portfolio') }
function goToBoard() { router.push('/board') }
function goToAbout() { router.push('/about') }
function goToMyPage() { router.push('/mypage') }

</script>

<style scoped>
.dashboard-screen {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
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

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}


/* Header */
.dashboard-header {
  width: 100%;
  text-align: center;
  margin-bottom: 40px;
  animation: slideDown 0.8s ease-out;
}

.game-title {
  margin-bottom: 20px;
}

.title-main {
  font-size: 48px;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  display: block;
  font-weight: 900;
}

/* User Status Card */
.user-status-card {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
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
  grid-template-columns: 1fr 300px; /* 2 Columns: Features | Widgets */
  gap: 20px;
  width: 100%;
  animation: fadeIn 1s ease-out;
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
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  text-align: center;
  border-width: 2px;
  min-height: 140px;
  justify-content: center;
}

.feature-card:hover {
  border-color: #ffd43b;
}

.card-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 2px rgba(0,0,0,0.5));
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

/* Battle Card Special Style */
.battle-card {
  grid-column: span 2; /* Take full width */
  background: rgba(74, 158, 255, 0.15); /* Slight blue tint */
  border-color: #4a9eff;
  flex-direction: row; /* Horizontal layout */
  text-align: left;
  align-items: center;
  gap: 20px;
  justify-content: space-between;
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
  padding: 8px 20px;
  border-radius: 4px;
  font-weight: 700;
  font-size: 14px;
  box-shadow: 0 4px 0 #c92a2a;
}

.battle-card:hover .card-action {
  transform: scale(1.05);
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

.widget-title {
  font-size: 14px;
  color: #4a9eff;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 5px;
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
  font-size: 20px;
  padding: 15px 40px;
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
</style>

