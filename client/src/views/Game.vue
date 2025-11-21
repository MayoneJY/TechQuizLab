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
      
      <!-- Loading State -->
      <div v-if="gameStore.isLoading || questionStore.isLoading" class="loading-container">
        <p class="pixel-text loading-text">로딩 중...</p>
      </div>
      
      <!-- Question -->
      <div v-else-if="gameStore.currentQuiz" class="question-container">
        <h2 class="question-text pixel-text">
          {{ gameStore.currentQuiz.question }}
        </h2>
      </div>
      
      <!-- Answer Input -->
      <div v-if="gameStore.currentQuiz && !gameStore.selectedAnswer" class="answer-input-container">
        <input
          v-model="answerInput"
          type="text"
          placeholder="답을 입력하세요"
          class="pixel-input answer-input"
          @keyup.enter="handleSubmit"
          :disabled="gameStore.isLoading"
        />
        <button
          class="pixel-button primary submit-button"
          @click="handleSubmit"
          :disabled="!answerInput || gameStore.isLoading"
        >
          제출
        </button>
        <button
          v-if="gameStore.currentQuiz && !isAnswerCorrect"
          class="pixel-button link-button bookmark-button"
          @click="bookmarkQuestion"
          title="오답 노트에 추가"
        >
          북마크
        </button>
      </div>
      
      <!-- Result Message -->
      <div v-if="gameStore.selectedAnswer !== null" class="auto-submit-message pixel-text">
        <div :class="isAnswerCorrect ? 'correct-message' : 'wrong-message'">
          {{ isAnswerCorrect ? '정답입니다! 🎉' : '오답입니다! 😢' }}
        </div>
        <div v-if="!isAnswerCorrect && gameStore.currentQuiz" class="correct-answer">
          정답: {{ gameStore.currentQuiz.answer }}
        </div>
      </div>
      
      <!-- Bookmark Button -->
      <div v-if="gameStore.selectedAnswer !== null && !isAnswerCorrect" class="bookmark-button-container">
        <button
          class="pixel-button warning bookmark-button"
          @click="bookmarkQuestion"
        >
          오답 노트에 추가
        </button>
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
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import { useQuestionStore } from '../stores/question'
import ParticleBackground from '../components/ParticleBackground.vue'
import ExplosionEffect from '../components/ExplosionEffect.vue'
import PixelHeart from '../components/PixelHeart.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gameStore = useGameStore()
const questionStore = useQuestionStore()
const showExplosion = ref(false)
const explosionColor = ref('#ffd43b')
const answerInput = ref('')

function getStarStyle(index: number) {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${1 + Math.random() * 2}s`
  }
}

const isAnswerCorrect = computed(() => {
  if (!gameStore.currentQuiz || gameStore.selectedAnswer === null) return false
  return gameStore.selectedAnswer === gameStore.currentQuiz.answer
})

function selectAnswer(answer: string) {
  gameStore.selectAnswer(answer)
}

async function handleSubmit() {
  if (!answerInput.value || !gameStore.currentQuiz) return
  
  await submitAnswer()
}

async function bookmarkQuestion() {
  if (!gameStore.currentQuiz) return
  
  try {
    await questionStore.bookmarkQuestion(gameStore.currentQuiz.id)
    alert('오답 노트에 추가되었습니다!')
  } catch (error) {
    console.error('Failed to bookmark:', error)
    alert('북마크 추가에 실패했습니다.')
  }
}

async function submitAnswer() {
  if (!answerInput.value && gameStore.selectedAnswer === null) return
  if (!gameStore.currentQuiz) return
  
  const answer = answerInput.value || gameStore.selectedAnswer || ''
  if (!gameStore.selectedAnswer) {
    gameStore.selectAnswer(answer)
  }
  
  try {
    const isCorrect = await gameStore.submitAnswer()
    explosionColor.value = isCorrect ? '#51cf66' : '#ff6b6b'
    showExplosion.value = true
    setTimeout(() => {
      showExplosion.value = false
    }, 100)
    
    answerInput.value = ''
  } catch (error: any) {
    console.error('Failed to submit answer:', error)
    // 에러 메시지 표시
    const errorMsg = error.serverMessage || error.message || '답안 제출에 실패했습니다.'
    alert(errorMsg)
  }
}

async function restartGame() {
  if (gameStore.topicId) {
    await gameStore.resetGame(gameStore.topicId)
  }
}

function goHome() {
  router.push('/')
}

onMounted(async () => {
  // 문제가 없으면 홈으로 리다이렉트
  if (!gameStore.currentQuiz && gameStore.totalQuestions === 0) {
    alert('문제를 불러올 수 없습니다.')
    router.push('/')
    return
  }
  
  // 문제가 로드 중이면 대기
  if (gameStore.isLoading || questionStore.isLoading) {
    // 로딩 완료 대기
    return
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
  font-size: 12px;
  font-weight: 600;
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
  font-size: 18px;
  font-weight: 700;
  text-align: center;
  color: #ffd43b;
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.5);
  border: 3px solid #ffd43b;
  border-radius: 8px;
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
  font-size: 18px;
  text-align: center;
  line-height: 2;
  color: #fff;
  padding: 20px;
  font-weight: 700;
  word-break: keep-all;
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
  margin: 20px auto 0;
  font-size: 18px;
  font-weight: 700;
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 4px solid #fff;
  border-radius: 8px;
  animation: messagePulse 0.5s ease-in-out;
  width: 100%;
  max-width: 700px;
  box-sizing: border-box;
}

@keyframes messagePulse {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.explanation-box {
  background: rgba(255, 255, 255, 0.1);
  border: 4px solid #ffd43b;
  padding: 20px;
  margin: 20px auto 0;
  width: 100%;
  max-width: 700px;
  box-sizing: border-box;
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
  justify-content: center;
  gap: 30px;
  z-index: 10;
  position: relative;
  width: 100%;
  max-width: 600px;
  padding: 40px 20px;
  box-sizing: border-box;
  min-height: calc(100vh - 40px);
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
  font-size: 48px;
  font-weight: 900;
  color: #ff6b6b;
  animation: shake 0.5s infinite;
  letter-spacing: 2px;
}

.victory-title {
  font-size: 48px;
  font-weight: 900;
  color: #51cf66;
  animation: victoryPulse 1s ease-in-out infinite;
  letter-spacing: 2px;
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
  font-size: 20px;
  font-weight: 700;
  color: #ffd43b;
  padding: 15px 30px;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #ffd43b;
  border-radius: 8px;
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

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes correctPulse {
  0%, 100% { 
    transform: scale(1);
    filter: brightness(1);
  }
  25% {
    transform: scale(1.15) rotate(2deg);
    filter: brightness(1.3);
  }
  50% { 
    transform: scale(1.1) rotate(-2deg);
    filter: brightness(1.2);
  }
  75% {
    transform: scale(1.15) rotate(2deg);
    filter: brightness(1.3);
  }
}

@keyframes wrongShake {
  0%, 100% { 
    transform: translateX(0) rotate(0deg);
    filter: hue-rotate(0deg);
  }
  10% { 
    transform: translateX(-15px) rotate(-5deg);
    filter: hue-rotate(10deg);
  }
  20% { 
    transform: translateX(15px) rotate(5deg);
    filter: hue-rotate(-10deg);
  }
  30% { 
    transform: translateX(-10px) rotate(-3deg);
    filter: hue-rotate(5deg);
  }
  40% { 
    transform: translateX(10px) rotate(3deg);
    filter: hue-rotate(-5deg);
  }
  50% { 
    transform: translateX(-5px) rotate(-2deg);
    filter: hue-rotate(2deg);
  }
  60% { 
    transform: translateX(5px) rotate(2deg);
    filter: hue-rotate(-2deg);
  }
}

.answer-input-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  animation: input-enter 0.5s ease-out 0.2s both;
  box-sizing: border-box;
  align-items: stretch;
}

@keyframes input-enter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.answer-input {
  width: 100%;
  font-size: 16px;
  padding: 15px 20px;
  font-family: 'Pretendard', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, sans-serif;
  font-weight: 500;
  border-radius: 8px;
  box-sizing: border-box;
}

.submit-button {
  width: 100%;
  font-size: 16px;
  padding: 15px 30px;
  min-height: 50px;
}

.bookmark-button-container {
  width: 100%;
  max-width: 700px;
  margin: 20px auto 0;
  box-sizing: border-box;
}

.bookmark-button {
  width: 100%;
  font-size: 14px;
  padding: 12px 20px;
}

.correct-answer {
  margin-top: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #ffd43b;
  padding: 12px;
  background: rgba(255, 212, 59, 0.2);
  border: 2px solid #ffd43b;
  border-radius: 6px;
  word-break: keep-all;
}

.correct-message {
  color: #51cf66;
  animation: correctPulse 0.5s;
}

.wrong-message {
  color: #ff6b6b;
  animation: wrongShake 0.5s;
}
</style>

