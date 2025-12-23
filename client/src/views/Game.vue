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
    
    <!-- Deferred Grading Loading Screen -->
    <div v-if="gameStore.gameStatus === 'grading'" class="loading-overlay">
      <div class="loading-content">
        <h2 class="pixel-text glitch" data-text="AI 채점 중...">AI 채점 중...</h2>
        <div class="loading-bar">
          <div class="loading-progress"></div>
        </div>
        <p class="pixel-text blink">잠시만 기다려주세요</p>
      </div>
    </div>

    <!-- Victory Screen with Grading Results -->
    <div v-if="gameStore.gameStatus === 'victory'">
       <!-- This section is now handled by redirect to /battle-result -->
    </div>
    
    <!-- Quiz Screen -->
    <div v-else class="quiz-screen">
      <!-- Top Controls -->
      <div class="top-controls">
         <button class="pixel-button ghost-btn small-btn" @click="handleGiveUp">
           포기
         </button>
      </div>

      <!-- Unified Glass Header -->
      <div class="game-glass-header glass-panel">
        <div class="header-top">
          <div class="question-info">
            <span class="pixel-text">Q.{{ gameStore.currentQuestionIndex + 1 }} / {{ gameStore.totalQuestions }}</span>
          </div>
          
          <div class="score-display pixel-text">
             <span>TIME: {{ timeLeft }}s</span>
          </div>

          <div class="lives-display">
             <!-- Removed Hearts for Battle Mode, or keep as visual only -->
             <PixelHeart v-for="i in 5" :key="i" />
          </div>
        </div>

        <div class="progress-bar-container">
          <div class="progress-bar" :style="{ width: `${(timeLeft / 300) * 100}%` }"></div>
        </div>
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
      <div v-if="gameStore.currentQuiz && !gameStore.selectedAnswer && !gameStore.isLoading" class="answer-section">
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
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useGameStore } from '../stores/game'
import { useQuestionStore } from '../stores/question'
import { useModalStore } from '../stores/modal'
import { retroMusicPlayer } from '../utils/retroMusic'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const gameStore = useGameStore()
const questionStore = useQuestionStore()
const modalStore = useModalStore()
const showExplosion = ref(false)
const explosionColor = ref('#ffd43b')
const answerInput = ref('')
const inputRef = ref<HTMLInputElement | null>(null)

// Timer Logic
const timeLeft = ref(300)
const timerInterval = ref<number | null>(null)

function startTimer() {
  stopTimer()
  // timeLeft.value = 300 // Don't reset if already running in correct logic, but for simplified:
  // We should manage global game timer, not per question.
  // Actually, per requirements, it seems to be a single 300s timer for the whole "Battle" or per question?
  // User said "limit time 300s" for "solving problems". Usually total time.
  // Current logic resets on every question.
  // Let's make it 300s TOTAL for the game.
  
  if (timerInterval.value === null) {
      timerInterval.value = window.setInterval(() => {
        if (gameStore.gameStatus !== 'playing') {
             stopTimer()
             return
        }

        if (timeLeft.value > 0) {
          timeLeft.value--
        } else {
          stopTimer()
          handleTimeout()
        }
      }, 1000)
  }
}

function stopTimer() {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

async function handleTimeout() {
  // Time over -> Finish Game
  await gameStore.finishGame()
}

async function handleSubmit() {
  if (!answerInput.value || !gameStore.currentQuiz) return
  await submitAnswer()
}

// function bookmarkQuestion() ... removed

async function submitAnswer(isTimeout = false) {
  if (!answerInput.value && gameStore.selectedAnswer === null && !isTimeout) return
  if (!gameStore.currentQuiz) return
  
  // stopTimer() // Don't stop timer, it's total time
  
  const answer = answerInput.value || gameStore.selectedAnswer || ''
  if (!gameStore.selectedAnswer) {
    gameStore.selectAnswer(answer)
  }
  
  try {
    await gameStore.submitAnswer()
    // No immediate feedback
    answerInput.value = ''
  } catch (error: any) {
    console.error('Failed to submit answer:', error)
    const errorMsg = error.serverMessage || error.message || 'Error occurred.'
    await modalStore.openAlert(errorMsg)
  }
}

async function restartGame() {
    router.push('/')
}

function goHome() {
  router.push('/')
}

async function handleGiveUp() {
  if (await modalStore.openConfirm('도전 포기시 채점이 진행되지 않습니다.\n정말 포기하시겠습니까?')) {
    // Stop timer immediately to prevent background ticking
    stopTimer()
    if (gameStore.battleId) {
        await gameStore.giveUp()
    }
    router.push('/')
  }
}

// Watch for question change
watch(() => gameStore.currentQuestionIndex, () => {
  if (gameStore.gameStatus === 'playing') {
    answerInput.value = ''
    setTimeout(() => {
      inputRef.value?.focus()
    }, 100)
  }
})

// Watch for game status change to redirect
watch(() => gameStore.gameStatus, (newStatus) => {
    if (newStatus === 'grading' || newStatus === 'victory' || newStatus === 'gameOver') {
        stopTimer()
    }

    if (newStatus === 'victory' && gameStore.battleId) {
        // give it a moment to show "Grading Complete" or just redirect
        // For now direct redirect as per previous code, but typically we might want to show result first?
        // Actually, previous code redirected immediately on victory.
        // If we want to show 'grading' screen, gameStatus will be 'grading' first.
        // When it switches to 'victory', we redirect.
        router.push(`/battle-result/${gameStore.battleId}`)
    }
})




onMounted(async () => {
  const battleId = route.query.battleId
  if (battleId) {
      try {
        await gameStore.loadBattleQuestions(Number(battleId))
        startTimer()
      } catch (e: any) {
        if (e.message === 'ALREADY_COMPLETED') {
             await modalStore.openAlert('이미 종료된 배틀입니다.\n비정상적인 접근입니다.')
             router.replace('/')
        } else {
             console.error(e)
             await modalStore.openAlert('배틀을 불러올 수 없습니다.')
             router.replace('/')
        }
      }
  }
  
  if (gameStore.gameStatus === 'playing') {
    setTimeout(() => {
      inputRef.value?.focus()
    }, 100)
  }

  try {
    await retroMusicPlayer.playGameMusic()
  } catch (error) {
    console.error('Failed to play game music:', error)
  }
})

onUnmounted(() => {
  stopTimer()
  retroMusicPlayer.stopGameMusic()
})
</script>

<style scoped>
/* Previous styles remain... */

/* Result Container Styles */
.result-container {
    width: 100%;
    max-width: 900px !important;
    display: flex;
    flex-direction: column;
    max-height: 80vh;
}

.final-score {
    font-size: 32px;
    margin-bottom: 20px;
    border-bottom: 2px solid rgba(255,255,255,0.1);
    padding-bottom: 20px;
}

.score-highlight {
    color: #ffd43b;
    text-shadow: 0 0 10px rgba(255, 212, 59, 0.5);
}

.grading-results-scroll {
    overflow-y: auto;
    padding-right: 10px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.grading-results-scroll::-webkit-scrollbar {
    width: 8px;
}

.grading-results-scroll::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.2);
    border-radius: 4px;
}

.grading-card {
    background: rgba(0, 0, 0, 0.4);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 20px;
    text-align: left;
    transition: transform 0.2s, border-color 0.2s;
}

.grading-card:hover {
    transform: translateY(-2px);
    border-color: #4a9eff;
}

.grading-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.question-number {
    font-size: 14px;
    color: #4a9eff;
    font-weight: 700;
}

.grading-score {
    font-weight: 800;
    font-size: 18px;
}

.score-high { color: #51cf66; }
.score-medium { color: #ffd43b; }
.score-low { color: #ff6b6b; }

.grading-question-text {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 15px;
    line-height: 1.5;
    color: #eee;
}

.grading-body {
    display: flex;
    flex-direction: column;
    gap: 15px;
    font-size: 14px;
}

.answer-box {
    background: rgba(255, 255, 255, 0.05);
    padding: 10px;
    border-radius: 4px;
}

.feedback-box {
    background: rgba(74, 158, 255, 0.1);
    padding: 10px;
    border-radius: 4px;
    border-left: 3px solid #4a9eff;
}

.label {
    display: block;
    font-size: 12px;
    color: #888;
    margin-bottom: 5px;
}

.user-answer {
    color: #ccc;
    line-height: 1.4;
}

.ai-feedback {
    color: #fff;
    line-height: 1.4;
}

/* Ensure other styles are preserved by just appending if possible, but replace tool replaces chunks */
/* Re-adding previous key styles to ensure no breakage if chunk was large */
.game-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
  min-height: 100vh;
  justify-content: center;
}
/* ... rest of styles assumed safe or I should have included them if I replaced the whole style block */
/* Since I'm replacing from onMounted down to end of file, I need to include all styles */
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
  background: linear-gradient(90deg, #ff6b6b, #ff8787);
  transition: width 1s linear;
}

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

.small-btn {
  height: 30px;
  padding: 0 10px;
  font-size: 12px;
  line-height: 1;
}

.ghost-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: rgba(255, 255, 255, 0.5);
}

.ghost-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border-color: #fff;
  transform: translateY(-2px);
}

.top-controls {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

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

.game-over-screen, .victory-screen {
  text-align: center;
  gap: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 20;
  width: 100%;
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


.game-over-buttons, .victory-buttons {
  display: flex;
  gap: 15px;
}

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

/* Loading Overlay Styles (from StageList but good to have here too if needed locally, though app-wide might be better) */
.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    padding: 40px;
}

.loading-bar {
    width: 200px;
    height: 4px;
    background: #333;
    border-radius: 2px;
    overflow: hidden;
    position: relative;
}

.loading-progress {
    width: 100%;
    height: 100%;
    background: #4a9eff;
    position: absolute;
    top: 0;
    left: 0;
    animation: loading 2s infinite ease-in-out;
}

.blink {
    animation: blink 1.5s infinite;
    color: #888;
}

@keyframes loading {
  0% { transform: translateX(-100%); }
  50% { transform: translateX(0); }
  100% { transform: translateX(100%); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* Loading Overlay Styles (Full Screen) */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.loading-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}


/* Responsive Adjustments */
@media (max-width: 768px) {
  .quiz-screen {
      width: 95%;
      padding: 15px;
  }
}

@media (max-width: 480px) {
  .game-container {
      padding: 10px;
  }

  .quiz-screen {
      width: 100%;
      padding: 10px;
      gap: 15px;
  }
  
  .game-glass-header {
      padding: 12px;
  }

  .header-top {
      flex-direction: column;
      gap: 8px;
      align-items: flex-start;
  }
  
  .lives-display {
      align-self: flex-end; /* Move hearts to right or just standard flow */
      margin-top: -30px; /* Hacky overlap or just let it flow? Let's flow properly first. */
      margin-top: 0;
      align-self: flex-start;
  }
  
  /* Better mobile header layout: 
     Row 1: Question#  Timer
     Row 2: Hearts
  */
  .header-top {
      display: grid;
      grid-template-columns: 1fr auto;
      grid-template-areas: 
          "info timer"
          "lives lives";
      gap: 8px;
  }
  
  .question-info { grid-area: info; }
  .score-display { grid-area: timer; }
  .lives-display { grid-area: lives; justify-content: flex-start; }

  .question-card {
      min-height: 140px;
      padding: 20px 15px;
  }
  
  .question-text {
      font-size: 18px;
  }

  .answer-section {
      flex-direction: column;
      align-items: stretch;
  }

  .answer-input {
      height: 46px;
  }

  .submit-button {
      height: 46px;
      width: 100%;
  }
  
  .top-controls {
      margin-bottom: -10px; /* pull closer */
  }
  
  .small-btn {
      padding: 5px 12px;
  }
}
</style>

