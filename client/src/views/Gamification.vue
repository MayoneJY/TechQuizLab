<template>
  <div class="gamification-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="gamification-container">
      <div class="header">
        <h1 class="pixel-text title">게이미피케이션</h1>
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
          일일 미션
        </button>
      </div>

      <!-- Achievements Tab -->
      <div v-if="activeTab === 'achievements'" class="tab-content">
        <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">로딩 중...</div>
        <div v-else-if="gamificationStore.achievements.length === 0" class="empty-message pixel-text">
          업적이 없습니다.
        </div>
        <div v-else class="achievements-list">
          <div
            v-for="achievement in gamificationStore.achievements"
            :key="achievement.id"
            class="achievement-item"
            :class="{ unlocked: gamificationStore.isAchievementUnlocked(achievement.id) }"
          >
            <div class="achievement-icon">🏆</div>
            <div class="achievement-info">
              <p class="pixel-text achievement-name">{{ achievement.name || '업적' }}</p>
              <p class="achievement-desc">{{ achievement.description || '업적 설명이 없습니다.' }}</p>
              <p class="pixel-text achievement-reward">보상: EXP +{{ achievement.rewardExp || 0 }}</p>
            </div>
            <div v-if="gamificationStore.isAchievementUnlocked(achievement.id)" class="achievement-badge">
              ✓
            </div>
          </div>
        </div>
      </div>

      <!-- Missions Tab -->
      <div v-if="activeTab === 'missions'" class="tab-content">
        <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">로딩 중...</div>
        <div v-else-if="gamificationStore.dailyMissions.length === 0" class="empty-message pixel-text">
          오늘의 미션이 없습니다.
        </div>
        <div v-else class="missions-list">
          <div
            v-for="mission in gamificationStore.dailyMissions"
            :key="mission.id"
            class="mission-item"
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
                class="pixel-button success"
                @click="claimMission(mission.id)"
              >
                보상 받기
              </button>
              <p v-else-if="mission.isClaimed" class="pixel-text claimed-text">✓ 완료</p>
              <p v-else class="pixel-text progress-status">진행 중...</p>
            </div>
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
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gamificationStore = useGamificationStore()
const authStore = useAuthStore()

const activeTab = ref<'achievements' | 'missions'>('achievements')

onMounted(async () => {
  // 로그인 확인
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  // 업적 및 미션 데이터 로드
  try {
    await Promise.all([
      gamificationStore.fetchAllAchievements(),
      gamificationStore.fetchUserAchievements(),
      gamificationStore.fetchDailyMissions()
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
                        '보상 수령에 실패했습니다.'
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
  animation: screen-enter 0.8s ease-out;
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
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 
    4px 4px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  justify-content: center;
  flex-wrap: wrap;
}

.tab-button {
  font-size: 16px;
  padding: 15px 30px;
  min-width: 150px;
  transition: all 0.3s;
}

.tab-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  box-shadow: 
    0 0 20px rgba(74, 158, 255, 0.6),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.tab-content {
  width: 100%;
}

.achievements-list,
.missions-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  font-weight: 500;
}

.loading-text {
  text-align: center;
  color: #4a9eff;
  font-size: 16px;
  font-weight: 600;
  padding: 40px;
}

.achievement-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  align-items: flex-start;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.achievement-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.achievement-item:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.achievement-item:hover::before {
  left: 100%;
}

.achievement-item.unlocked {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.25);
  box-shadow: 0 0 15px rgba(255, 212, 59, 0.3);
}

.achievement-item.unlocked:hover {
  border-color: #ffd43b;
  box-shadow: 0 0 25px rgba(255, 212, 59, 0.5);
}

.achievement-icon {
  font-size: 40px;
  filter: drop-shadow(2px 2px 0 #000);
  flex-shrink: 0;
  line-height: 1;
}

.achievement-info {
  flex: 1;
  min-width: 0;
}

.achievement-name {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  word-break: keep-all;
  line-height: 1.4;
}

.achievement-item.unlocked .achievement-name {
  color: #ffd43b;
}

.achievement-desc {
  font-size: 14px;
  font-weight: 400;
  color: #ccc;
  margin-bottom: 8px;
  line-height: 1.6;
  word-break: keep-all;
}

.achievement-item.unlocked .achievement-desc {
  color: #fff;
}

.achievement-reward {
  font-size: 13px;
  font-weight: 600;
  color: #51cf66;
  margin-top: 4px;
}

.achievement-badge {
  font-size: 28px;
  color: #ffd43b;
  flex-shrink: 0;
  filter: drop-shadow(2px 2px 0 #000);
}

.mission-item {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.mission-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.mission-item:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.mission-item:hover::before {
  left: 100%;
}

.mission-item.completed {
  border-color: #51cf66;
  background: rgba(81, 207, 102, 0.15);
  box-shadow: 0 0 15px rgba(81, 207, 102, 0.3);
}

.mission-item.claimed {
  opacity: 0.7;
  border-color: #888;
}

.mission-header {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.mission-icon {
  font-size: 32px;
  filter: drop-shadow(2px 2px 0 #000);
  flex-shrink: 0;
  line-height: 1;
}

.mission-info {
  flex: 1;
  min-width: 0;
}

.mission-name {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
  word-break: keep-all;
  line-height: 1.4;
}

.mission-desc {
  font-size: 14px;
  font-weight: 400;
  color: #ccc;
  line-height: 1.6;
  word-break: keep-all;
  margin-bottom: 8px;
}

.mission-progress-container {
  width: 100%;
  margin: 8px 0;
}

.mission-progress {
  position: relative;
  width: 100%;
  height: 28px;
  background: rgba(0, 0, 0, 0.6);
  border: 3px solid #fff;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.5);
}

.mission-progress .progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff 0%, #5bb0ff 50%, #51cf66 100%);
  transition: width 0.5s ease;
  border-radius: 3px;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
}

.mission-item.completed .mission-progress .progress-bar {
  background: linear-gradient(90deg, #51cf66 0%, #61df76 50%, #51cf66 100%);
  box-shadow: 0 0 15px rgba(81, 207, 102, 0.7);
  animation: progressGlow 2s ease-in-out infinite;
}

@keyframes progressGlow {
  0%, 100% { box-shadow: 0 0 15px rgba(81, 207, 102, 0.7); }
  50% { box-shadow: 0 0 25px rgba(81, 207, 102, 1); }
}

.mission-progress .progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  text-shadow: 
    2px 2px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  z-index: 1;
  pointer-events: none;
  white-space: nowrap;
}

.mission-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 45px;
  margin-top: 5px;
}

.mission-actions .pixel-button {
  min-width: 150px;
  font-size: 12px;
  padding: 12px 20px;
}

.claimed-text {
  font-size: 14px;
  font-weight: 700;
  color: #51cf66;
  text-align: center;
  margin: 0;
  padding: 8px;
  background: rgba(81, 207, 102, 0.2);
  border-radius: 4px;
}

.progress-status {
  font-size: 12px;
  font-weight: 600;
  color: #4a9eff;
  text-align: center;
  margin: 0;
  padding: 8px;
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
}

.monster {
  position: fixed;
  z-index: 3;
  animation: float 2s ease-in-out infinite;
}

.monster-1 {
  top: 100px;
  left: 50px;
  animation-delay: 0s;
}

.monster-2 {
  top: 200px;
  right: 100px;
  animation-delay: 1s;
}

@keyframes screen-enter {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }
  
  .tabs {
    gap: 10px;
  }
  
  .tab-button {
    min-width: 120px;
    font-size: 14px;
    padding: 12px 20px;
  }
  
  .achievement-item,
  .mission-item {
    padding: 15px;
  }
}
</style>

