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
      
      <!-- Category Selection -->
      <div v-if="showOptions" class="category-selection">
        <h3 class="pixel-text">카테고리 선택</h3>
        <div class="category-buttons">
          <button
            v-for="cat in categories"
            :key="cat"
            class="pixel-button"
            :class="{ active: selectedCategory === cat }"
            @click="selectedCategory = cat"
          >
            {{ cat }}
          </button>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import { categories } from '../data/quizData'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelHeart from '../components/PixelHeart.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gameStore = useGameStore()
const showOptions = ref(false)
const showAbout = ref(false)
const selectedCategory = ref('전체')

function getStarStyle(index: number) {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${1 + Math.random() * 2}s`
  }
}

function startGame() {
  gameStore.setCategory(selectedCategory.value)
  router.push('/game')
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

