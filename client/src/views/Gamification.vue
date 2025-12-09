<template>
  <div class="gamification-screen">

    
    <div class="gamification-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="업적 및 랭킹">업적 및 랭킹</span>
        </h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div class="tabs">
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'achievements' }"
          @click="activeTab = 'achievements'"
        >
          업적
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'missions' }"
          @click="activeTab = 'missions'"
        >
          미션
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'ranking' }"
          @click="activeTab = 'ranking'"
        >
          랭킹
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'friends' }"
          @click="activeTab = 'friends'"
        >
          친구
        </button>
      </div>

      <!-- Achievements Tab -->
      <transition name="fade" mode="out-in">
        <div v-if="activeTab === 'achievements'" class="tab-content" key="achievements">
          <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">데이터 로딩 중...</div>
          <div v-else-if="gamificationStore.achievements.length === 0" class="empty-message pixel-text">
            달성한 업적이 없습니다.
          </div>
          <div v-else class="achievements-list">
            <div
              v-for="achievement in gamificationStore.achievements"
              :key="achievement.id"
              class="achievement-item glass-card"
              :class="{ unlocked: gamificationStore.isAchievementUnlocked(achievement.id) }"
            >
              <div class="achievement-icon">🏆</div>
              <div class="achievement-info">
                <p class="pixel-text achievement-name">{{ achievement.name || '알 수 없음' }}</p>
                <p class="achievement-desc">{{ achievement.description || '설명이 없습니다.' }}</p>
                <p class="pixel-text achievement-reward">보상: 경험치 +{{ achievement.rewardExp || 0 }}</p>
              </div>
              <div v-if="gamificationStore.isAchievementUnlocked(achievement.id)" class="achievement-badge">
                <span class="checkmark">✓</span>
              </div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>

        <!-- Missions Tab -->
        <div v-else-if="activeTab === 'missions'" class="tab-content" key="missions">
          <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">데이터 로딩 중...</div>
          <div v-else-if="gamificationStore.dailyMissions.length === 0" class="empty-message pixel-text">
            오늘의 미션이 없습니다.
          </div>
          <div v-else class="missions-list">
            <div
              v-for="mission in gamificationStore.dailyMissions"
              :key="mission.id"
              class="mission-item glass-card"
              :class="{ completed: mission.isCompleted, claimed: mission.isClaimed }"
            >
              <div class="mission-header">
                <div class="mission-icon">📋</div>
                <div class="mission-info">
                  <p class="pixel-text mission-name">{{ mission.missionName || '일일 미션' }}</p>
                  <p class="mission-desc">{{ mission.description || '미션을 완료하세요!' }}</p>
                </div>
              </div>
              <div class="mission-progress-container">
                <div class="mission-progress">
                  <div class="progress-bar" :style="{ width: `${Math.min(mission.progress || 0, 100)}%` }"></div>
                  <p class="pixel-text progress-text">{{ Math.min(mission.progress || 0, 100) }}%</p>
                </div>
              </div>
              <div class="mission-actions">
                <button
                  v-if="mission.isCompleted && !mission.isClaimed"
                  class="pixel-button success small-action-btn"
                  @click="claimMission(mission.id)"
                >
                  보상 받기
                </button>
                <p v-else-if="mission.isClaimed" class="pixel-text claimed-text">완료됨</p>
                <p v-else class="pixel-text progress-status">진행 중</p>
              </div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>

        <!-- Ranking Tab -->
        <div v-else-if="activeTab === 'ranking'" class="tab-content" key="ranking">
          <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">데이터 로딩 중...</div>
          <div v-else class="ranking-list">
            <div 
              v-for="rank in gamificationStore.rankings" 
              :key="rank.userId"
              class="ranking-item glass-card"
              :class="{ 'my-rank': rank.userId === authStore.user?.userId }"
            >
              <div class="rank-number pixel-text">{{ rank.rank }}</div>
              <div class="rank-info">
                <p class="pixel-text rank-name">{{ rank.nickname }}</p>
                <p class="rank-level">Lv.{{ rank.level }}</p>
              </div>
              <div class="rank-exp pixel-text">{{ rank.exp }} EXP</div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>

        <!-- Friends Tab -->
        <div v-else-if="activeTab === 'friends'" class="tab-content" key="friends">
          <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">데이터 로딩 중...</div>
          <div v-else class="friends-list">
            <div 
              v-for="friend in gamificationStore.friends" 
              :key="friend.userId"
              class="friend-item glass-card"
              :class="{ 'is-rival': friend.isRival }"
            >
              <div class="friend-icon">{{ friend.isRival ? '⚔️' : '😊' }}</div>
              <div class="friend-info">
                <p class="pixel-text friend-name">{{ friend.nickname }}</p>
                <p class="friend-stats">Lv.{{ friend.level }} | Solved: {{ friend.solvedCount }}</p>
              </div>
              <div class="friend-action">
                <span v-if="friend.isRival" class="rival-badge pixel-text">라이벌</span>
              </div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
    <div class="monster monster-2">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGamificationStore } from '../stores/gamification'
import { useAuthStore } from '../stores/auth'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gamificationStore = useGamificationStore()
const authStore = useAuthStore()

const activeTab = ref<'achievements' | 'missions' | 'ranking' | 'friends'>('achievements')

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  try {
    await Promise.all([
      gamificationStore.fetchAllAchievements(),
      gamificationStore.fetchUserAchievements(),
      gamificationStore.fetchDailyMissions(),
      gamificationStore.fetchRankings(),
      gamificationStore.fetchFriends()
    ])
  } catch (error) {
    console.error('Failed to load gamification data:', error)
  }
})

function goHome() {
  router.push('/')
}

async function claimMission(missionId: number) {
  try {
    await gamificationStore.claimMissionReward(missionId)
  } catch (error: any) {
    const errorMessage = error.response?.data?.message || 
                        error.response?.data?.error || 
                        error.message ||
                        'Failed to claim reward.'
    alert(errorMessage)
  }
}
</script>

<style scoped>
.gamification-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-x: hidden;
}

.gamification-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  justify-content: center;
}

.tab-button {
  flex: 1;
  min-width: 120px;
  font-size: 14px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: #888;
}

.tab-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.tab-button.active {
  background: rgba(74, 158, 255, 0.2);
  border-color: #4a9eff;
  color: #fff;
  box-shadow: 0 0 15px rgba(74, 158, 255, 0.3);
}

.tab-content {
  width: 100%;
  min-height: 400px;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  background: rgba(0,0,0,0.3);
  border-radius: 8px;
  border: 1px dashed rgba(255,255,255,0.1);
}

.loading-text {
  text-align: center;
  color: #4a9eff;
  font-size: 16px;
  padding: 40px;
}

/* LIST ITEMS COMMON STYLES */
.achievement-item,
.mission-item,
.ranking-item,
.friend-item {
  position: relative;
  display: flex;
  gap: 15px;
  padding: 20px;
  margin-bottom: 15px;
  align-items: center;
  overflow: hidden; /* For scan-line */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.achievement-item:hover,
.mission-item:hover,
.ranking-item:hover,
.friend-item:hover {
  transform: translateY(-2px);
  border-color: rgba(74, 158, 255, 0.5);
  background: rgba(255, 255, 255, 0.1);
}

/* SCAN LINE ANIMATION */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(74, 158, 255, 0.1), transparent);
  transform: translateY(-100%);
  padding: 0;
  margin: 0;
  pointer-events: none;
}

.achievement-item:hover .scan-line,
.mission-item:hover .scan-line,
.ranking-item:hover .scan-line,
.friend-item:hover .scan-line {
  animation: scan 1.5s infinite linear;
}

@keyframes scan {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(200%); }
}

/* ACHIEVEMENTS */
.achievement-item.unlocked {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.1);
}

.achievement-icon {
  font-size: 32px;
  filter: grayscale(1);
  opacity: 0.5;
}

.achievement-item.unlocked .achievement-icon {
  filter: grayscale(0);
  opacity: 1;
  text-shadow: 0 0 10px rgba(255, 212, 59, 0.5);
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  font-size: 16px;
  color: #aaa;
  margin-bottom: 4px;
}

.achievement-item.unlocked .achievement-name {
  color: #fff;
  font-weight: 700;
}

.achievement-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.achievement-item.unlocked .achievement-desc {
  color: #ccc;
}

.achievement-reward {
  font-size: 12px;
  color: #444;
}

.achievement-item.unlocked .achievement-reward {
  color: #51cf66;
}

.checkmark {
  font-size: 24px;
  color: #ffd43b;
  text-shadow: 0 0 10px #ffd43b;
}

/* MISSIONS */
.mission-item.completed {
  border-color: #51cf66;
  background: rgba(81, 207, 102, 0.1);
}

.mission-item.claimed {
  opacity: 0.6;
  filter: grayscale(0.5);
}

.mission-header {
  display: flex;
  gap: 15px;
  width: 100%;
}

.mission-item {
  flex-direction: column;
  align-items: flex-start;
}

.mission-icon {
  font-size: 24px;
}

.mission-info {
  flex: 1;
}

.mission-name {
  font-size: 16px;
  color: #fff;
  margin-bottom: 4px;
}

.mission-desc {
  font-size: 13px;
  color: #aaa;
}

.mission-progress-container {
  width: 100%;
  margin: 10px 0;
}

.mission-progress {
  width: 100%;
  height: 20px;
  background: rgba(0, 0, 0, 0.5);
  border: 2px solid rgba(255,255,255,0.2);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff, #51cf66);
  transition: width 0.5s ease;
}

.progress-text {
  position: absolute;
  width: 100%;
  text-align: center;
  top: 0;
  line-height: 18px;
  font-size: 10px;
  color: #fff;
  text-shadow: 1px 1px 0 #000;
}

.mission-actions {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.small-action-btn {
  padding: 8px 16px;
  font-size: 12px;
}

.progress-status {
  font-size: 12px;
  color: #4a9eff;
}

.claimed-text {
  font-size: 12px;
  color: #51cf66;
}

/* RANKING */
.ranking-item {
  justify-content: space-between;
}

.ranking-item.my-rank {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.1);
  box-shadow: 0 0 15px rgba(255, 212, 59, 0.1);
}

.rank-number {
  font-size: 24px;
  color: #fff;
  width: 40px;
  text-align: center;
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 16px;
  color: #fff;
}

.rank-level {
  font-size: 12px;
  color: #888;
}

.rank-exp {
  font-size: 14px;
  color: #51cf66;
}

/* FRIENDS */
.friend-item.is-rival {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.friend-icon {
  font-size: 24px;
}

.friend-info {
  flex: 1;
}

.friend-name {
  font-size: 16px;
  color: #fff;
}

.friend-stats {
  font-size: 12px;
  color: #888;
}

.rival-badge {
  font-size: 10px;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  padding: 2px 6px;
  border-radius: 4px;
}

/* MONSTERS */
.monster {
  position: fixed;
  z-index: 1;
  pointer-events: none;
  opacity: 0.8;
}

.monster-1 {
  top: 150px;
  left: 5%;
  animation: float 4s ease-in-out infinite;
}

.monster-2 {
  bottom: 100px;
  right: 5%;
  animation: float 5s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

/* TRANSITIONS */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }
  
  .tab-button {
    min-width: auto;
    font-size: 12px;
    padding: 10px;
  }
}
</style>

