<template>
  <div class="game-container">
    
    <!-- Explosion Effect -->
    <ExplosionEffect 
      :trigger="showExplosion"
      :x="0.5"
      :y="0.5"
      :color="explosionColor"
    />
    
    <!-- Game Over Screen -->
    <div v-if="gameStore.gameStatus === 'gameOver'" class="game-over-screen">
      <h1 class="pixel-text game-over-title glitch" data-text="GAME OVER">GAME OVER</h1>
      <div class="monsters-invasion">
        <PixelMonster type="alien" />
        <PixelMonster type="alien" />
        <PixelMonster type="alien" />
      </div>
      <div class="broken-heart">
        <PixelHeart broken />
      </div>
      <div class="result-card glass-panel">
        <p class="final-score pixel-text">Score: {{ gameStore.score }}</p>
      </div>
      <div class="game-over-buttons">
        <button class="pixel-button primary" @click="restartGame">
          RETRY
        </button>
        <button class="pixel-button" @click="goHome">
          LOBBY
        </button>
      </div>
    </div>
    
    <!-- Victory Screen -->
    <div v-else-if="gameStore.gameStatus === 'victory'" class="victory-screen">
      <h1 class="pixel-text victory-title glitch" data-text="MISSION CLEAR">MISSION CLEAR</h1>
      <div class="victory-content glass-panel">
        <div class="hearts-display">
          <PixelHeart 
            v-for="i in gameStore.lives" 
            :key="i"
          />
        </div>
        <p class="final-score pixel-text">Total Score: {{ gameStore.score }}</p>
      </div>
      
      <div class="victory-buttons">
        <button class="pixel-button success" @click="restartGame">
          PLAY AGAIN
        </button>
        <button class="pixel-button" @click="goHome">
          LOBBY
        </button>
      </div>
    </div>
    
    <!-- Quiz Screen -->
    <div v-else class="quiz-screen">
      <!-- Unified Glass Header -->
      <div class="game-glass-header glass-panel">
        <div class="header-top">
          <div class="question-info">
            <span class="pixel-text">Q.{{ gameStore.currentQuestionIndex + 1 }} / {{ gameStore.totalQuestions }}</span>
          </div>
          
          <div class="score-display pixel-text">
            SCORE: {{ gameStore.score }}
          </div>

          <div class="lives-display">
            <PixelHeart v-for="i in gameStore.lives" :key="i" />
            <PixelHeart v-for="i in (8 - gameStore.lives)" :key="`empty-${i}`" broken />
          </div>
        </div>

        <div class="progress-bar-container">
          <div class="progress-bar" :style="{ width: `${gameStore.progress}%` }"></div>
        </div>
      </div>
      
      <!-- Timer (Floating) -->
      <div class="timer-float pixel-text" :class="{ 'time-low': timeLeft <= 10 }">
        {{ timeLeft }}
      </div>
      
      <!-- Loading State -->
      <div v-if="gameStore.isLoading || questionStore.isLoading" class="loading-container glass-panel">
        <p class="pixel-text loading-text">LOADING SYSTEM...</p>
      </div>
      
      <!-- Question Card -->
      <div v-else-if="gameStore.currentQuiz" class="question-card glass-panel">
        <div class="question-content">
          <h2 class="question-text pixel-text">
            {{ gameStore.currentQuiz.question }}
          </h2>
        </div>
      </div>
      
      <!-- Answer Section -->
      <div v-if="gameStore.currentQuiz && !gameStore.selectedAnswer" class="answer-section">
        <div class="input-wrapper glass-panel">
          <input
            v-model="answerInput"
            type="text"
            placeholder="TYPE_YOUR_ANSWER..."
            class="pixel-input glass-input answer-input"
            @keyup.enter="handleSubmit"
            :disabled="gameStore.isLoading"
            ref="inputRef"
            autofocus
          />
        </div>
        
        <div class="action-buttons">
          <button
            class="pixel-button primary submit-button"
            @click="handleSubmit"
            :disabled="!answerInput || gameStore.isLoading"
          >
            SUBMIT
          </button>
          <button
              v-if="gameStore.currentQuiz && !isAnswerCorrect"
              class="pixel-button warning bookmark-button"
              @click="bookmarkQuestion"
              title="북마크"
            >
              ★
          </button>
        </div>
      </div>
      
      <!-- Result Message -->
      <div v-if="gameStore.selectedAnswer !== null" class="result-message-container">
        <div class="glass-panel message-panel" :class="isAnswerCorrect ? 'correct' : 'wrong'">
          <h3 class="pixel-text result-text">
            {{ isAnswerCorrect ? 'CORRECT!' : 'WRONG ANSWER' }}
          </h3>
          <div v-if="!isAnswerCorrect && gameStore.currentQuiz" class="correct-answer">
            ANSWER: <span class="answer-highlight">{{ gameStore.currentQuiz.answer }}</span>
          </div>
        </div>

        <div v-if="!isAnswerCorrect" class="bookmark-action">
           <button class="pixel-button warning small-btn" @click="bookmarkQuestion">
             오답 노트 저장
           </button>
        </div>
      </div>
      
      <!-- Explanation -->
      <div 
        v-if="gameStore.selectedAnswer !== null && gameStore.currentQuiz?.explanation"
        class="explanation-box glass-panel"
      >
        <p class="explanation-text">{{ gameStore.currentQuiz.explanation }}</p>
      </div>
    </div>
    
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import { useQuestionStore } from '../stores/question'
import { retroMusicPlayer } from '../utils/retroMusic'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gameStore = useGameStore()
const questionStore = useQuestionStore()
const showExplosion = ref(false)
const explosionColor = ref('#ffd43b')
const answerInput = ref('')
const inputRef = ref<HTMLInputElement | null>(null)

// Timer Logic
const timeLeft = ref(60)
const timerInterval = ref<number | null>(null)

function startTimer() {
  stopTimer()
  timeLeft.value = 60
  timerInterval.value = window.setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      stopTimer()
      handleTimeout()
    }
  }, 1000)
}

function stopTimer() {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

async function handleTimeout() {
  if (gameStore.selectedAnswer !== null) return // Already submitted
  
  // Time out treated as wrong answer
  // alert('TIME OVER!') // Removed alert for smoother flow
  await submitAnswer(true) // Force submit
}

const isAnswerCorrect = computed(() => {
  if (!gameStore.currentQuiz || gameStore.selectedAnswer === null) return false
  return gameStore.selectedAnswer === gameStore.currentQuiz.answer
})

async function handleSubmit() {
  if (!answerInput.value || !gameStore.currentQuiz) return
  await submitAnswer()
}

async function bookmarkQuestion() {
  if (!gameStore.currentQuiz) return
  
  try {
    await questionStore.bookmarkQuestion(gameStore.currentQuiz.id)
    alert('SAVED TO BOOKMARK!')
  } catch (error) {
    console.error('Failed to bookmark:', error)
  }
}

async function submitAnswer(isTimeout = false) {
  if (!answerInput.value && gameStore.selectedAnswer === null && !isTimeout) return
  if (!gameStore.currentQuiz) return
  
  stopTimer() // Stop timer on submit
  
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
    const errorMsg = error.serverMessage || error.message || 'Error occurred.'
    alert(errorMsg)
  }
}

async function restartGame() {
  if (gameStore.topicId) {
    await gameStore.resetGame(gameStore.topicId)
    startTimer() 
  }
}

function goHome() {
  router.push('/')
}

// Watch for question change to reset timer
watch(() => gameStore.currentQuestionIndex, () => {
  if (gameStore.gameStatus === 'playing') {
    startTimer()
    answerInput.value = ''
    // Auto focus input
    setTimeout(() => {
      inputRef.value?.focus()
    }, 100)
  } else {
    stopTimer()
  }
})

onMounted(async () => {
  if (!gameStore.currentQuiz && gameStore.totalQuestions === 0) {
    // Ideally user should come from lobby with data loaded, or we fetch here based on query param
    // For now assuming store has data or redirection needed
    // alert('No active game session.')
    // router.push('/')
    // return
  }
  
  if (gameStore.gameStatus === 'playing') {
    startTimer()
    setTimeout(() => {
      inputRef.value?.focus()
    }, 100)
  }

  try {
    await retroMusicPlayer.playGameMusic()
    console.log('🎮 Game music started')
  } catch (error) {
    console.error('Failed to play game music:', error)
  }
})

onUnmounted(() => {
  stopTimer()
  retroMusicPlayer.stopGameMusic()
  console.log('🎮 Game music stopped')
})
</script>

<style scoped>
.game-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  min-height: 100vh;
  justify-content: center;
}

.quiz-screen {
  width: 90%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  z-index: 10;
  position: relative;
  padding: 20px;
}

/* Glass Header */
.game-glass-header {
  padding: 15px 25px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  animation: slideDown 0.5s ease-out;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-info {
  font-size: 14px;
  color: #4a9eff;
  font-weight: 700;
}

.score-display {
  font-size: 16px;
  color: #ffd43b;
  font-weight: 700;
}

.lives-display {
  display: flex;
  gap: 5px;
}

.progress-bar-container {
  width: 100%;
  height: 8px;
  background: rgba(0,0,0,0.5);
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #51cf66, #4a9eff);
  transition: width 0.3s ease;
}

/* Floating Timer */
.timer-float {
  position: absolute;
  top: -40px; 
  left: 50%;
  transform: translateX(-50%);
  font-size: 32px;
  font-weight: 900;
  color: #fff;
  text-shadow: 2px 2px 0 #000;
  z-index: 20;
}

.timer-float.time-low {
  color: #ff6b6b;
  animation: pulse 0.5s infinite;
}

/* Question Card */
.question-card {
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  text-align: center;
  border: 1px solid rgba(255,255,255,0.2);
  animation: fadeIn 0.5s ease-out;
}

.question-text {
  font-size: 20px;
  line-height: 1.6;
  color: #fff;
  word-break: keep-all;
}

/* Answer Section */
.answer-section {
  display: flex;
  gap: 10px;
  align-items: center;
}

.input-wrapper {
  flex: 1;
  padding: 0; 
  border-radius: 8px;
  overflow: hidden;
}

.answer-input {
  width: 100%;
  height: 50px;
  font-size: 16px;
  padding: 0 20px;
  border: none;
  background: transparent;
  color: #fff;
}

.answer-input:focus {
  outline: none;
  background: rgba(255,255,255,0.1);
}

.submit-button {
  height: 50px;
  padding: 0 30px;
  font-size: 14px;
}

.bookmark-button {
  height: 50px;
  width: 50px;
  padding: 0;
  font-size: 20px;
}

/* Results */
.result-message-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.message-panel {
  width: 100%;
  padding: 20px;
  text-align: center;
}

.message-panel.correct {
  border-color: #51cf66;
  background: rgba(81, 207, 102, 0.15);
}

.message-panel.wrong {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.15);
}

.result-text {
  font-size: 24px;
  margin: 0;
  color: #fff;
}

.correct .result-text { color: #51cf66; }
.wrong .result-text { color: #ff6b6b; }

.correct-answer {
  margin-top: 10px;
  font-size: 14px;
  color: #ccc;
}

.answer-highlight {
  color: #ffd43b;
  font-weight: 700;
  font-size: 16px;
}

.explanation-box {
  padding: 15px;
  border-left: 4px solid #4a9eff;
}

/* Game Over & Victory */
.game-over-screen, .victory-screen {
  text-align: center;
  gap: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 20;
}

.game-over-title {
  font-size: 64px;
  color: #ff6b6b;
}

.victory-title {
  font-size: 64px;
  color: #51cf66;
}

.result-card, .victory-content {
  padding: 30px 50px;
  border-radius: 12px;
}

.final-score {
  font-size: 24px;
  color: #ffd43b;
  margin: 0;
}

.game-over-buttons, .victory-buttons {
  display: flex;
  gap: 15px;
}

/* Animations */
@keyframes slideDown {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes popIn {
  from { transform: scale(0.8); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

@keyframes pulse {
  0%, 100% { transform: translateX(-50%) scale(1); }
  50% { transform: translateX(-50%) scale(1.2); }
}

.monsters-invasion {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}
</style>

