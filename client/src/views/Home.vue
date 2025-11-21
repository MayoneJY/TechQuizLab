<template>
  <div class="game-container">
    <!-- Particle Background -->
    <ParticleBackground />
    
    <!-- Stars background -->
    <div class="stars-container">
      <div 
        v-for="i in 100" 
        :key="i" 
        class="star"
        :style="getStarStyle(i)"
      ></div>
    </div>
    
    <!-- Title Screen -->
    <div class="title-screen">
      <h1 class="game-title pixel-text">
        <span class="title-main">잡몬스터</span>
        <span class="title-sub">JOB MONSTER</span>
      </h1>
      
      <div class="hearts-display">
        <PixelHeart 
          v-for="_ in 8" 
          :key="`heart-${_}`"
        />
      </div>
      
      <div class="menu-buttons">
        <button 
          class="pixel-button primary" 
          @click="startGame"
        >
          START
        </button>
        <button 
          class="pixel-button" 
          @click="showOptions = !showOptions"
        >
          OPTION
        </button>
        <button 
          class="pixel-button" 
          @click="showAbout = !showAbout"
        >
          ABOUT
        </button>
      </div>
      
      <!-- Topic Selection -->
      <div v-if="showOptions" class="category-selection">
        <h3 class="pixel-text">주제 선택</h3>
        <div v-if="topicStore.isLoading" class="loading-text pixel-text">
          로딩 중...
        </div>
        <div v-else class="category-buttons">
          <button
            v-for="topic in topicStore.topics"
            :key="topic.id"
            class="pixel-button"
            :class="{ active: selectedTopicId === topic.id }"
            @click="selectedTopicId = topic.id"
          >
            {{ topic.name }}
          </button>
        </div>
        <p v-if="topicStore.error" class="error-text pixel-text">
          {{ typeof topicStore.error === 'string' ? topicStore.error : JSON.stringify(topicStore.error) }}
        </p>
      </div>
      
      <!-- User Info -->
      <div v-if="authStore.isAuthenticated" class="user-info">
        <div class="user-profile">
          <p class="pixel-text user-name">{{ authStore.user?.nickname }}</p>
          <p v-if="authStore.user?.level" class="pixel-text user-level">
            Lv.{{ authStore.user.level }} | EXP: {{ authStore.user.exp || 0 }}
          </p>
        </div>
        <div class="user-buttons">
          <button class="pixel-button link-button" @click="showAchievements = !showAchievements">
            업적
          </button>
          <button class="pixel-button link-button" @click="showMissions = !showMissions">
            미션
          </button>
          <button class="pixel-button link-button" @click="handleLogout">
            로그아웃
          </button>
        </div>
      </div>
      <div v-else class="user-info">
        <button class="pixel-button" @click="goToLogin">
          로그인
        </button>
      </div>
      
      <!-- Achievements -->
      <div v-if="showAchievements && authStore.isAuthenticated" class="achievements-screen">
        <h3 class="pixel-text">업적</h3>
        <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">로딩 중...</div>
        <div v-else class="achievements-list">
          <div
            v-for="achievement in gamificationStore.achievements"
            :key="achievement.id"
            class="achievement-item"
            :class="{ unlocked: gamificationStore.isAchievementUnlocked(achievement.id) }"
          >
            <div class="achievement-icon">🏆</div>
            <div class="achievement-info">
              <p class="pixel-text achievement-name">{{ achievement.name }}</p>
              <p class="achievement-desc">{{ achievement.description }}</p>
              <p class="pixel-text achievement-reward">보상: EXP +{{ achievement.rewardExp }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Daily Missions -->
      <div v-if="showMissions && authStore.isAuthenticated" class="missions-screen">
        <h3 class="pixel-text">일일 미션</h3>
        <div v-if="gamificationStore.isLoading" class="loading-text pixel-text">로딩 중...</div>
        <div v-else class="missions-list">
          <div
            v-for="mission in gamificationStore.dailyMissions"
            :key="mission.id"
            class="mission-item"
            :class="{ completed: mission.isCompleted, claimed: mission.isClaimed }"
          >
            <div class="mission-progress">
              <div class="progress-bar" :style="{ width: `${(mission.progress / 100) * 100}%` }"></div>
              <p class="pixel-text">{{ mission.progress }}%</p>
            </div>
            <button
              v-if="mission.isCompleted && !mission.isClaimed"
              class="pixel-button success"
              @click="claimMission(mission.id)"
            >
              보상 받기
            </button>
            <p v-else-if="mission.isClaimed" class="pixel-text claimed-text">완료</p>
          </div>
        </div>
      </div>
      
      <!-- About -->
      <div v-if="showAbout" class="about-screen">
        <h3 class="pixel-text">ABOUT</h3>
        <p class="about-text">
          취업을 위한 다양한 테마의 퀴즈를 풀며<br/>
          잡몬스터를 물리치세요!<br/><br/>
          정답을 맞추면 점수를 얻고,<br/>
          오답을 맞추면 생명력이 줄어듭니다.<br/><br/>
          모든 퀴즈를 완료하면 승리!
        </p>
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
import { useGameStore } from '../stores/game'
import { useAuthStore } from '../stores/auth'
import { useTopicStore } from '../stores/topic'
import { useGamificationStore } from '../stores/gamification'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelHeart from '../components/PixelHeart.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gameStore = useGameStore()
const authStore = useAuthStore()
const topicStore = useTopicStore()
const gamificationStore = useGamificationStore()

const showOptions = ref(false)
const showAbout = ref(false)
const showAchievements = ref(false)
const showMissions = ref(false)
const selectedTopicId = ref<number | null>(null)

onMounted(async () => {
  try {
    // 주제 목록 가져오기
    await topicStore.fetchTopics()
    if (topicStore.topics.length > 0) {
      selectedTopicId.value = topicStore.topics[0].id
    } else {
      console.warn('주제 목록이 비어있습니다.')
    }
    
    // 로그인된 경우 업적 및 미션 정보 가져오기
    if (authStore.isAuthenticated) {
      try {
        await gamificationStore.fetchAllAchievements()
        await gamificationStore.fetchUserAchievements()
        await gamificationStore.fetchDailyMissions()
      } catch (error) {
        console.error('Failed to load gamification data:', error)
        // 업적/미션 로드 실패는 게임 진행에 필수는 아니므로 에러를 던지지 않음
      }
    }
  } catch (error: any) {
    console.error('Failed to load data:', error)
    const errorMsg = error.serverMessage || error.message || '데이터를 불러오는데 실패했습니다.'
    // 주제 목록 로드 실패는 사용자에게 알림
    if (errorMsg) {
      alert(errorMsg)
    }
  }
})

function getStarStyle(index: number) {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${1 + Math.random() * 2}s`
  }
}

async function startGame() {
  if (!authStore.isAuthenticated) {
    if (confirm('로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?')) {
      router.push('/login')
    }
    return
  }
  
  if (!selectedTopicId.value) {
    alert('주제를 선택해주세요.')
    return
  }
  
  try {
    await gameStore.setTopicId(selectedTopicId.value)
    
    // 문제가 로드되었는지 확인
    if (gameStore.totalQuestions === 0) {
      alert('해당 주제에 문제가 없습니다.')
      return
    }
    
    router.push('/game')
  } catch (error: any) {
    console.error('Failed to start game:', error)
    const errorMsg = error.serverMessage || error.message || '게임을 시작할 수 없습니다.'
    alert(errorMsg)
  }
}

async function claimMission(missionId: number) {
  try {
    await gamificationStore.claimMissionReward(missionId)
  } catch (error) {
    console.error('Failed to claim mission:', error)
  }
}

function handleLogout() {
  authStore.logout()
}

function goToLogin() {
  router.push('/login')
}
</script>

<style scoped>
.title-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
  z-index: 10;
  position: relative;
}

.game-title {
  font-size: 48px;
  text-align: center;
  margin-bottom: 30px;
  animation: titlePulse 2s ease-in-out infinite;
}

.title-main {
  display: block;
  color: #ffd43b;
  font-size: 56px;
  margin-bottom: 15px;
  text-shadow: 
    4px 4px 0 #000,
    -2px -2px 0 #000,
    2px -2px 0 #000,
    -2px 2px 0 #000,
    0 0 20px rgba(255, 212, 59, 0.5);
}

.title-sub {
  display: block;
  color: #ff6b6b;
  font-size: 24px;
  text-shadow: 
    3px 3px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
}

@keyframes titlePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.hearts-display {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 400px;
}

.hearts-display {
  gap: 8px;
}

.menu-buttons {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.category-selection {
  margin-top: 30px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 0;
}

.category-selection h3 {
  margin-bottom: 15px;
  font-size: 14px;
}

.category-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.category-buttons .pixel-button.active {
  background: #51cf66;
}

.about-screen {
  margin-top: 30px;
  padding: 30px;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 0;
  max-width: 500px;
  text-align: center;
}

.about-screen h3 {
  font-size: 16px;
  margin-bottom: 20px;
}

.about-text {
  font-size: 10px;
  line-height: 2;
  color: #fff;
}

.user-info {
  margin-top: 20px;
  text-align: center;
}

.user-profile {
  margin-bottom: 15px;
}

.user-name {
  font-size: 14px;
  color: #ffd43b;
  margin-bottom: 5px;
}

.user-level {
  font-size: 10px;
  color: #4a9eff;
}

.user-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.achievements-screen,
.missions-screen {
  margin-top: 30px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 0;
  max-width: 600px;
  max-height: 400px;
  overflow-y: auto;
}

.achievements-screen h3,
.missions-screen h3 {
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.achievements-list,
.missions-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.achievement-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  align-items: center;
}

.achievement-item.unlocked {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.2);
}

.achievement-icon {
  font-size: 32px;
  filter: drop-shadow(2px 2px 0 #000);
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  font-size: 10px;
  color: #fff;
  margin-bottom: 5px;
}

.achievement-desc {
  font-size: 8px;
  color: #ccc;
  margin-bottom: 5px;
  line-height: 1.4;
}

.achievement-reward {
  font-size: 8px;
  color: #51cf66;
}

.mission-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
}

.mission-item.completed {
  border-color: #51cf66;
}

.mission-item.claimed {
  opacity: 0.6;
}

.mission-progress {
  position: relative;
  width: 100%;
  height: 20px;
  background: #000;
  border: 2px solid #fff;
}

.mission-progress .progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff, #51cf66);
  transition: width 0.3s;
}

.mission-progress .pixel-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 8px;
  z-index: 1;
}

.claimed-text {
  font-size: 10px;
  color: #51cf66;
  text-align: center;
}

.loading-text {
  text-align: center;
  color: #4a9eff;
  font-size: 10px;
  padding: 20px;
}

.user-info {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 12px;
  color: #4a9eff;
}

.loading-text {
  font-size: 10px;
  color: #ffd43b;
  text-align: center;
  padding: 20px;
}

.stars-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.spaceship {
  position: absolute;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
}

.monster {
  position: absolute;
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

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}
</style>

