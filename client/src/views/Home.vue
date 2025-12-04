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
        <span class="title-main">잡스페이스</span>
        <span class="title-sub">JOB SPACE</span>
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
          @click="goToStages"
        >
          START
        </button>
        <button 
          class="pixel-button" 
          @click="goToAbout"
        >
          ABOUT
        </button>
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
          <button class="pixel-button link-button" @click="goToGamification">
            업적 & 미션
          </button>
          <button class="pixel-button link-button" @click="goToPortfolio">
            포트폴리오
          </button>

          <button class="pixel-button link-button" @click="goToBoard">
            게시판
          </button>
          <button class="pixel-button link-button" @click="handleLogout">
            로그아웃
          </button>
        </div>
        
        <!-- Rival Info -->
        <div v-if="rival" class="rival-info-card">
          <p class="pixel-text rival-title">RIVAL STATUS</p>
          <div class="rival-details">
            <span class="rival-name">{{ rival.nickname }}</span>
            <span class="rival-score">{{ rival.solvedCount }} 문제 해결</span>
          </div>
        </div>
      </div>
      <div v-else class="user-info">
        <button class="pixel-button" @click="goToLogin">
          로그인
        </button>
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
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useGamificationStore } from '../stores/gamification'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelHeart from '../components/PixelHeart.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const authStore = useAuthStore()
const gamificationStore = useGamificationStore()

const rival = computed(() => gamificationStore.friends.find(f => f.isRival))

onMounted(async () => {
  if (authStore.isAuthenticated) {
    await gamificationStore.fetchFriends()
  }
})

function getStarStyle(_: number) {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${1 + Math.random() * 2}s`
  }
}



function goToAbout() {
  router.push('/about')
}


function handleLogout() {
  authStore.logout()
}

function goToLogin() {
  router.push('/login')
}

function goToGamification() {
  router.push('/gamification')
}

function goToPortfolio() {
  router.push('/portfolio')
}

function goToStages() {
  router.push('/stages')
}

function goToBoard() {
  router.push('/board')
}
</script>

<style scoped>
.rival-info-card {
  margin-top: 15px;
  padding: 15px;
  background: rgba(255, 107, 107, 0.15);
  border: 2px solid #ff6b6b;
  border-radius: 8px;
  width: 100%;
  max-width: 300px;
  animation: pulse 2s infinite;
}

.rival-title {
  font-size: 12px;
  color: #ff6b6b;
  margin-bottom: 8px;
}

.rival-details {
  display: flex;
  justify-content: space-between;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.title-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 30px;
  z-index: 10;
  position: relative;
  animation: screen-enter 0.8s ease-out;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
  box-sizing: border-box;
  min-height: calc(100vh - 40px);
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

.game-title {
  font-size: 48px;
  text-align: center;
  margin-bottom: 30px;
  animation: titlePulse 2s ease-in-out infinite;
}

.title-main {
  display: block;
  color: #ffd43b;
  font-size: 64px;
  margin-bottom: 15px;
  font-weight: 900;
  text-shadow: 
    4px 4px 0 #000,
    -2px -2px 0 #000,
    2px -2px 0 #000,
    -2px 2px 0 #000,
    0 0 20px rgba(255, 212, 59, 0.5);
  letter-spacing: -1px;
  word-break: keep-all;
}

.title-sub {
  display: block;
  color: #ff6b6b;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 
    3px 3px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  letter-spacing: 2px;
}

@media (max-width: 768px) {
  .title-main {
    font-size: 40px;
  }
  
  .title-sub {
    font-size: 18px;
  }
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



.user-info {
  margin-top: 20px;
  text-align: center;
}

.user-profile {
  margin-bottom: 15px;
}

.user-name {
  font-size: 18px;
  font-weight: 700;
  color: #ffd43b;
  margin-bottom: 5px;
}

.user-level {
  font-size: 12px;
  font-weight: 600;
  color: #4a9eff;
}

.user-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
  max-width: 500px;
}

@media (max-width: 768px) {
  .user-buttons {
    gap: 8px;
  }
  
  .user-buttons .pixel-button {
    flex: 1;
    min-width: 100px;
    font-size: 11px;
    padding: 10px 15px;
  }
}

.achievements-screen,
.missions-screen {
  margin-top: 30px;
  margin-bottom: 50px;
  padding: 25px;
  background: rgba(0, 0, 0, 0.85);
  border: 4px solid #fff;
  border-radius: 8px;
  max-width: 700px;
  width: 100%;
  min-height: 200px;
  box-sizing: border-box;
  box-shadow: 0 0 30px rgba(74, 158, 255, 0.3);
}

.achievements-screen h3,
.missions-screen h3 {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  text-align: center;
  color: #ffd43b;
  text-shadow: 
    3px 3px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  padding-bottom: 15px;
  border-bottom: 3px solid rgba(255, 255, 255, 0.3);
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
  padding: 40px 20px;
  color: #888;
  font-size: 14px;
  font-weight: 500;
}

.achievement-item {
  display: flex;
  gap: 15px;
  padding: 18px;
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
  font-size: 36px;
  filter: drop-shadow(2px 2px 0 #000);
  flex-shrink: 0;
  line-height: 1;
}

.achievement-info {
  flex: 1;
  min-width: 0;
}

.achievement-name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  word-break: keep-all;
}

.achievement-desc {
  font-size: 13px;
  font-weight: 400;
  color: #ccc;
  margin-bottom: 8px;
  line-height: 1.6;
  word-break: keep-all;
}

.achievement-reward {
  font-size: 12px;
  font-weight: 600;
  color: #51cf66;
}

.achievement-badge {
  font-size: 24px;
  color: #ffd43b;
  flex-shrink: 0;
  filter: drop-shadow(2px 2px 0 #000);
}

.mission-item {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 18px;
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
  font-size: 28px;
  filter: drop-shadow(2px 2px 0 #000);
  flex-shrink: 0;
  line-height: 1;
}

.mission-info {
  flex: 1;
  min-width: 0;
}

.mission-name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
  word-break: keep-all;
  line-height: 1.4;
}

.mission-desc {
  font-size: 13px;
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

.loading-text {
  text-align: center;
  color: #4a9eff;
  font-size: 14px;
  font-weight: 600;
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
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
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

