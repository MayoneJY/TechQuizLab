<template>
  <div class="game-container">
    <!-- Particle Background -->
    <ParticleBackground />
    
    <!-- Explosion Effect -->
    <ExplosionEffect 
      :trigger="showExplosion"
      :x="0.5"
      :y="0.5"
      :color="explosionColor"
    />
    
    <!-- Stars background -->
    <div class="stars-container">
      <div 
        v-for="i in 100" 
        :key="i" 
        class="star"
        :style="getStarStyle(i)"
      ></div>
    </div>
    
    <!-- Game Over Screen -->
    <div v-if="gameStore.gameStatus === 'gameOver'" class="game-over-screen">
      <h1 class="pixel-text game-over-title">INVASION!</h1>
      <div class="monsters-invasion">
        <PixelMonster type="alien" />
        <PixelMonster type="alien" />
        <PixelMonster type="alien" />
      </div>
      <div class="broken-heart">
        <PixelHeart broken />
      </div>
      <p class="final-score pixel-text">Score: {{ gameStore.score }}</p>
      <div class="game-over-buttons">
        <button class="pixel-button primary" @click="restartGame">
          TRY AGAIN
        </button>
        <button class="pixel-button" @click="goHome">
          HOME
        </button>
      </div>
    </div>
    
    <!-- Victory Screen -->
    <div v-else-if="gameStore.gameStatus === 'victory'" class="victory-screen">
      <h1 class="pixel-text victory-title">CONGRATS!</h1>
      <div class="hearts-display">
        <PixelHeart 
          v-for="i in gameStore.lives" 
          :key="i"
        />
      </div>
      <p class="final-score pixel-text">Final Score: {{ gameStore.score }}</p>
      <div class="victory-buttons">
        <button class="pixel-button success" @click="restartGame">
          START OVER?
        </button>
        <button class="pixel-button" @click="goHome">
          HOME
        </button>
      </div>
    </div>
    
    <!-- Quiz Screen -->
    <div v-else class="quiz-screen">
      <!-- Header -->
      <div class="game-header">
        <div class="question-info">
          <span class="pixel-text">QUESTION {{ gameStore.currentQuestionIndex + 1 }}/{{ gameStore.totalQuestions }}</span>
        </div>
        <div class="lives-display">
          <PixelHeart 
            v-for="i in gameStore.lives" 
            :key="i"
          />
          <PixelHeart 
            v-for="i in (8 - gameStore.lives)" 
            :key="`empty-${i}`"
            broken
          />
        </div>
      </div>
      
      <!-- Progress Bar -->
      <div class="progress-bar-container">
        <div 
          class="progress-bar" 
          :style="{ width: `${gameStore.progress}%` }"
        ></div>
      </div>
      
      <!-- Score -->
      <div class="score-display pixel-text">
        Score: {{ gameStore.score }}
      </div>
      
      <!-- Question -->
      <div class="question-container">
        <h2 class="question-text pixel-text">
          {{ gameStore.currentQuiz?.question }}
        </h2>
      </div>
      
      <!-- Answer Options -->
      <div class="answers-container">
        <button
          v-for="(option, index) in gameStore.currentQuiz?.options"
          :key="index"
          class="answer-button pixel-button"
          :class="{
            selected: gameStore.selectedAnswer === index,
            correct: gameStore.selectedAnswer !== null && index === gameStore.currentQuiz?.correctAnswer,
            wrong: gameStore.selectedAnswer === index && gameStore.selectedAnswer !== gameStore.currentQuiz?.correctAnswer
          }"
          @click="selectAnswer(index)"
          :disabled="gameStore.selectedAnswer !== null"
        >
          {{ option }}
        </button>
      </div>
      
      <!-- Auto submit after selection -->
      <div v-if="gameStore.selectedAnswer !== null" class="auto-submit-message pixel-text">
        {{ gameStore.selectedAnswer === gameStore.currentQuiz?.correctAnswer ? '정답입니다! 🎉' : '오답입니다! 😢' }}
      </div>
      
      <!-- Explanation -->
      <div 
        v-if="gameStore.selectedAnswer !== null && gameStore.currentQuiz?.explanation"
        class="explanation-box"
      >
        <p class="explanation-text">{{ gameStore.currentQuiz.explanation }}</p>
      </div>
    </div>
    
    <!-- Spaceship decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import ParticleBackground from '../components/ParticleBackground.vue'
import ExplosionEffect from '../components/ExplosionEffect.vue'
import PixelHeart from '../components/PixelHeart.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gameStore = useGameStore()
const showExplosion = ref(false)
const explosionColor = ref('#ffd43b')

function getStarStyle(index: number) {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${1 + Math.random() * 2}s`
  }
}

function selectAnswer(index: number) {
  gameStore.selectAnswer(index)
  // Auto submit after a short delay
  setTimeout(() => {
    submitAnswer()
  }, 500)
}

function submitAnswer() {
  if (gameStore.selectedAnswer === null) return
  
  const isCorrect = gameStore.selectedAnswer === gameStore.currentQuiz?.correctAnswer
  explosionColor.value = isCorrect ? '#51cf66' : '#ff6b6b'
  showExplosion.value = true
  setTimeout(() => {
    showExplosion.value = false
  }, 100)
  
  gameStore.submitAnswer()
}

function restartGame() {
  gameStore.resetGame(gameStore.currentCategory)
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  if (!gameStore.currentQuiz) {
    router.push('/')
  }
})
</script>

<style scoped>
.quiz-screen {
  width: 90%;
  max-width: 900px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  z-index: 10;
  position: relative;
  padding: 20px;
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.question-info {
  font-size: 10px;
  color: #4a9eff;
}

.lives-display {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.progress-bar-container {
  width: 100%;
  height: 20px;
  background: #000;
  border: 4px solid #fff;
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #51cf66, #4a9eff);
  transition: width 0.3s ease;
}

.score-display {
  font-size: 14px;
  text-align: center;
  color: #ffd43b;
  padding: 10px;
  background: rgba(0, 0, 0, 0.5);
  border: 3px solid #ffd43b;
  display: inline-block;
  margin: 0 auto;
}

.question-container {
  background: rgba(255, 255, 255, 0.1);
  border: 4px solid #fff;
  padding: 30px;
  min-height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 
    inset 0 0 20px rgba(255, 255, 255, 0.1),
    0 0 0 2px #000,
    0 0 0 6px #fff;
}

.question-container::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #4a9eff, #ff6b6b, #ffd43b, #51cf66);
  z-index: -1;
  opacity: 0.3;
  animation: borderGlow 3s ease-in-out infinite;
}

@keyframes borderGlow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

.question-text {
  font-size: 14px;
  text-align: center;
  line-height: 2;
  color: #fff;
  padding: 10px;
}

.answers-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.answer-button {
  width: 100%;
  text-align: left;
  padding: 16px 20px;
  font-size: 10px;
  line-height: 1.6;
}

.answer-button.selected {
  background: #4a9eff;
  transform: scale(1.05);
}

.answer-button.correct {
  background: #51cf66;
  animation: correctPulse 0.5s;
}

.answer-button.wrong {
  background: #ff6b6b;
  animation: wrongShake 0.5s;
}

.answer-button:disabled {
  cursor: not-allowed;
}

.auto-submit-message {
  margin-top: 20px;
  font-size: 14px;
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 4px solid #fff;
  animation: messagePulse 0.5s ease-in-out;
}

@keyframes messagePulse {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.explanation-box {
  background: rgba(255, 255, 255, 0.1);
  border: 4px solid #ffd43b;
  padding: 20px;
  margin-top: 20px;
}

.explanation-text {
  font-size: 10px;
  line-height: 2;
  color: #fff;
}

.game-over-screen,
.victory-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
  z-index: 10;
  position: relative;
}

.monsters-invasion {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.monsters-invasion .pixel-monster {
  animation: invasionFloat 2s ease-in-out infinite;
}

.monsters-invasion .pixel-monster:nth-child(1) {
  animation-delay: 0s;
}

.monsters-invasion .pixel-monster:nth-child(2) {
  animation-delay: 0.3s;
}

.monsters-invasion .pixel-monster:nth-child(3) {
  animation-delay: 0.6s;
}

@keyframes invasionFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-10px) rotate(-5deg); }
  75% { transform: translateY(-10px) rotate(5deg); }
}

.game-over-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.game-over-title {
  font-size: 36px;
  color: #ff6b6b;
  animation: shake 0.5s infinite;
}

.victory-title {
  font-size: 36px;
  color: #51cf66;
  animation: victoryPulse 1s ease-in-out infinite;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

@keyframes victoryPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.victory-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.broken-heart {
  font-size: 64px;
  filter: drop-shadow(4px 4px 0 #000);
  animation: brokenHeart 1s ease-in-out infinite;
}

@keyframes brokenHeart {
  0%, 100% { transform: rotate(0deg) scale(1); }
  50% { transform: rotate(-10deg) scale(1.1); }
}

.final-score {
  font-size: 18px;
  color: #ffd43b;
  padding: 15px 30px;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #ffd43b;
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

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes correctPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

@keyframes wrongShake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}
</style>

