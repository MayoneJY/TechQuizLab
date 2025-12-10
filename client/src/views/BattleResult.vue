<template>
  <div class="result-screen">
    <div class="result-container glass-panel">
      
      <!-- Skeleton Loading State -->
      <div v-if="loading" class="result-content skeleton-content">
        <h1 class="pixel-text victory-title">결과 분석 중...</h1>
        <h2 class="final-score pixel-text skeleton-title">
            <div class="skeleton-text short"></div>
        </h2>
        
        <div class="grading-results-scroll">
            <div v-for="i in 3" :key="i" class="grading-card skeleton-card">
                <div class="grading-header">
                     <div class="header-left">
                        <div class="skeleton-text mini"></div>
                        <div class="skeleton-text small"></div>
                     </div>
                     <div class="skeleton-text button-shape"></div>
                </div>
                
                <div class="skeleton-text medium"></div>
                
                <div class="grading-body">
                    <div class="answer-box">
                        <div class="skeleton-text label-size"></div>
                        <div class="skeleton-text long"></div>
                    </div>
                    
                    <div class="feedback-box">
                        <div class="skeleton-text label-size"></div>
                        <div class="skeleton-text long"></div>
                        <div class="skeleton-text long"></div>
                    </div>
                </div>
            </div>
            <div class="scroll-spacer"></div> 
        </div>

        <div class="victory-buttons">
             <div class="skeleton-button"></div>
        </div>
      </div>
      
      <div v-else-if="result" class="result-content">
        <h1 class="pixel-text victory-title glitch" data-text="미션 성공">미션 성공</h1>
        <h2 class="final-score pixel-text">최종 점수: <span class="score-highlight">{{ result.totalScore }}</span></h2>
        
        <div class="grading-results-scroll">
            <div v-for="(detail, index) in result.details" :key="detail.detailId" class="grading-card">
                <div class="grading-header">
                    <div class="header-left">
                        <span class="question-number">Q.{{ index + 1 }}</span>
                        <span class="grading-score" :class="getScoreClass(detail.damage)">{{ detail.damage }}점</span>
                    </div>
                    <button class="pixel-button small bookmark-btn" @click.stop="bookmarkQuestion(detail.detailId)">
                        ⭐ 북마크
                    </button>
                </div>
                
                <p class="grading-question-text">{{ detail.questionText }}</p>
                
                <div class="grading-body">
                    <div class="answer-box">
                        <span class="label">나의 답변:</span>
                        <p class="user-answer">{{ detail.userAnswer || '(답변 없음)' }}</p>
                    </div>
                    
                    <div class="feedback-box">
                        <span class="label">AI 피드백:</span>
                        <p class="ai-feedback">{{ detail.aiFeedback }}</p>
                    </div>
                </div>
            </div>
            <!-- Add spacer to ensure last item hover visible and button not blocked -->
            <div class="scroll-spacer"></div> 
        </div>

        <div class="victory-buttons">
          <button class="pixel-button primary" @click="goHome">
            로비로 이동
          </button>
        </div>
      </div>
      
      <div v-else class="error-state">
        <h1 class="pixel-text victory-title error-title">접근 불가</h1>
        <div class="error-content">
            <p class="pixel-text error-msg">결과를 찾을 수 없거나<br>접근 권한이 없습니다.</p>
        </div>
        <button class="pixel-button" @click="goHome">로비로 이동</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { battleApi } from '../services/api'
import { useGameStore } from '../stores/game'

const router = useRouter()
const route = useRoute()
const gameStore = useGameStore()

const loading = ref(true)
const result = ref<any>(null)

function getScoreClass(score: number) {
  if (score >= 800) return 'score-high'
  if (score >= 500) return 'score-medium'
  return 'score-low'
}

function goHome() {
  router.push('/')
}

async function bookmarkQuestion(detailId: number) {
    // Note: detailId is from BattleDetail. We usually need QuestionId to bookmark.
    // However, in this generated questions setup, the question might not exist in the 'Question' table as a permanent entity
    // or it might be mapped differently. 
    // Assuming 'detailId' acts as a reference or we can find original question.
    // Wait, the detail has 'detailId', but questionStore.bookmarkQuestion expects 'questionId'.
    // If these are transient AI questions, bookmarking might fail if they aren't in the Question table.
    // For now, let's assume we pass the detailId and backend handles it, or we simply alert "Feature under construction" if logic differs.
    // But user asked for it. 
    // The previous implementation used questionStore.bookmarkQuestion(gameStore.currentQuiz.id). 
    // Here we have detail.detailId.
    // Let's try to pass the question text/answer to save? 
    try {
        const battleId = Number(route.params.id)
        if (!battleId) return
        
        await battleApi.bookmarkBattleDetail(battleId, detailId)
        alert('북마크에 저장되었습니다!')
    } catch (e) {
        console.error(e)
        alert('북마크 저장에 실패했습니다.')
    }
}

onMounted(async () => {
    const battleId = route.params.id
    
    if (gameStore.battleResult && String(gameStore.battleId) === battleId) {
        result.value = gameStore.battleResult
        loading.value = false
        return
    }

    if (!result.value && battleId) {
        try {
           const response = await battleApi.getBattleDetails(Number(battleId))
           const details = response.data
           
           if (!details || details.length === 0) {
               // Unauthorized access or invalid battle ID results in empty list
               throw new Error('Access denied or battle not found')
           }

           const totalScore = details.reduce((acc: number, cur: any) => acc + (cur.damage || 0), 0)
           
           result.value = {
               totalScore,
               details
           }
        } catch(e) {
            console.error(e)
        }
    }
    loading.value = false
})
</script>

<style scoped>
.result-screen {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  /* background-color: rgba(0,0,0,0.3); Darken background slightly */
}

.result-container {
    width: 100%;
    max-width: 800px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px;
    height: 85vh; /* Robust height */
    max-height: 900px;
    box-sizing: border-box;
    /* background: rgba(20, 20, 30, 0.85); Ensure good contrast */
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 16px;
    box-shadow: 0 0 30px rgba(0,0,0,0.5);
    backdrop-filter: blur(10px);
}

.result-content {
    width: 100%;
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden; 
    position: relative;
}

.victory-title {
    font-size: 42px;
    color: #51cf66;
    margin-bottom: 20px;
    text-align: center;
    flex-shrink: 0;
    text-shadow: 0 0 10px rgba(81, 207, 102, 0.5);
}

.final-score {
    font-size: 28px;
    margin-bottom: 20px;
    border-bottom: 2px solid rgba(255,255,255,0.1);
    padding-bottom: 15px;
    text-align: center;
    flex-shrink: 0;
}

.score-highlight {
    color: #ffd43b;
    text-shadow: 0 0 10px rgba(255, 212, 59, 0.5);
}

.grading-results-scroll {
    overflow-y: auto;
    padding: 10px; 
    padding-right: 15px; 
    display: flex;
    flex-direction: column;
    gap: 15px;
    flex: 1; 
    margin-bottom: 15px;
    min-height: 0; /* Important for flex scrolling */
}

.grading-results-scroll::-webkit-scrollbar {
    width: 8px;
}

.grading-results-scroll::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.2);
    border-radius: 4px;
}

.grading-results-scroll::-webkit-scrollbar-thumb:hover {
    background: rgba(255,255,255,0.3);
}

.scroll-spacer {
    height: 10px; 
}

.grading-card {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 20px;
    text-align: left;
    transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;
}

.grading-card:hover {
    transform: translateY(-2px);
    border-color: #4a9eff;
    box-shadow: 0 5px 15px rgba(0,0,0,0.3);
    background: rgba(255, 255, 255, 0.08);
}

.grading-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    border-bottom: 1px solid rgba(255,255,255,0.05);
    padding-bottom: 10px;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 12px;
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

.bookmark-btn {
    font-size: 12px;
    padding: 6px 12px;
    height: auto;
    background: rgba(0,0,0,0.3);
    border: 1px solid #555;
    color: #ffd43b;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.2s;
}

.bookmark-btn:hover {
    background: rgba(255, 212, 59, 0.15);
    border-color: #ffd43b;
    transform: scale(1.05);
}

.grading-question-text {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 15px;
    line-height: 1.6;
    color: #eee;
}

.grading-body {
    display: flex;
    flex-direction: column;
    gap: 12px;
    font-size: 14px;
}

.answer-box {
    background: rgba(0, 0, 0, 0.3);
    padding: 12px;
    border-radius: 8px;
}

.feedback-box {
    background: rgba(74, 158, 255, 0.1);
    padding: 12px;
    border-radius: 8px;
    border-left: 3px solid #4a9eff;
}

.label {
    display: block;
    font-size: 12px;
    font-weight: 700;
    color: #888;
    margin-bottom: 6px;
    text-transform: uppercase;
}

.user-answer {
    color: #ccc;
    line-height: 1.5;
    margin: 0;
}

.ai-feedback {
    color: #fff;
    line-height: 1.5;
    margin: 0;
}

.victory-buttons {
    display: flex;
    justify-content: center;
    margin-top: auto;
    padding-bottom: 10px;
    padding-top: 10px;
    flex-shrink: 0;
}

/* Skeleton Styles */
.skeleton-title {
    display: flex;
    justify-content: center;
    width: 100%;
}

.skeleton-text {
    background: linear-gradient(90deg, rgba(255,255,255,0.05) 25%, rgba(255,255,255,0.1) 50%, rgba(255,255,255,0.05) 75%);
    background-size: 200% 100%;
    animation: loading 1.5s infinite;
    border-radius: 4px;
    height: 1em;
}

.skeleton-text.short { width: 200px; height: 32px; }
.skeleton-text.mini { width: 30px; height: 16px; }
.skeleton-text.small { width: 50px; height: 16px; }
.skeleton-text.medium { width: 80%; height: 20px; margin-bottom: 15px; }
.skeleton-text.long { width: 100%; height: 16px; margin-bottom: 8px; }
.skeleton-text.label-size { width: 60px; height: 12px; margin-bottom: 8px; }
.skeleton-text.button-shape { width: 60px; height: 24px; border-radius: 12px; }

.skeleton-button {
    width: 200px;
    height: 50px;
    background: rgba(255,255,255,0.1);
    border-radius: 8px;
    animation: loading 1.5s infinite;
}

.skeleton-card {
    /* reuse grading-card styles but maybe simpler */
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    gap: 30px;
}

.error-title {
    color: #ff6b6b;
    text-shadow: 0 0 10px rgba(255, 107, 107, 0.5);
    margin-bottom: 0;
}

.error-content {
    background: rgba(255, 107, 107, 0.1);
    border: 1px solid rgba(255, 107, 107, 0.3);
    padding: 30px;
    border-radius: 12px;
    text-align: center;
    max-width: 400px;
}

.error-msg {
    font-size: 18px;
    line-height: 1.6;
    color: #eee;
    margin: 0;
}
</style>
