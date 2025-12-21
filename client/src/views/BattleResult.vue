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
        
        <!-- Metadata Header -->
        <div class="meta-header">
             <div class="meta-left-col">
                 <div class="meta-main-row">
                     <span class="meta-badge category">{{ result.battle?.jobCategory || 'General' }}</span>
                     <span class="meta-separator">|</span>
                     <span class="meta-text stage">{{ result.battle?.stageTitle || 'Unknown Stage' }}</span>
                 </div>
                 <span class="meta-date">{{ formatDateTime(result.battle?.createdAt) }}</span>
             </div>

             <div class="score-display">
                <span class="score-label">FINAL SCORE</span>
                <span class="score-value pixel-text">
                    {{ result.totalScore }} <span class="sub-score">/ {{ result.details.length * 1000 }}</span>
                </span>
             </div>
        </div>
        
        <div class="grading-results-scroll">
            <div v-for="(detail, index) in result.details" :key="detail.detailId" class="grading-card">
                <div class="grading-header">
                    <div class="header-left">
                        <span class="question-number">Q.{{ index + 1 }}</span>
                        <span class="grading-score" :class="getScoreClass(detail.damage)">
                            {{ detail.damage }}<span class="sub-score">/1000</span>
                        </span>
                    </div>
                    <button class="pixel-button small bookmark-btn" @click.stop="bookmarkQuestion(detail.detailId)">
                        ⭐ 오답노트 저장
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
          <button class="pixel-button secondary" @click="goBack">
            {{ backButtonText }}
          </button>
        </div>
      </div>
      
      <div v-else class="error-state">
        <h1 class="pixel-text victory-title error-title">접근 불가</h1>
        <div class="error-content">
            <p class="pixel-text error-msg">결과를 찾을 수 없거나<br>접근 권한이 없습니다.</p>
        </div>
        <button class="pixel-button" @click="goBack">로비로 이동</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { battleApi } from '../services/api'
import { useGameStore } from '../stores/game'
import { useModalStore } from '../stores/modal'

const router = useRouter()
const route = useRoute()
const gameStore = useGameStore()
const modalStore = useModalStore()

const loading = ref(true)
const result = ref<any>(null)

const backButtonText = computed(() => {
    return route.query.from === 'battles' ? '목록으로 이동' : '로비로 이동'
})

function getScoreClass(score: number) {
  if (score >= 800) return 'score-high'
  if (score >= 500) return 'score-medium'
  return 'score-low'
}

function formatDateTime(dateStr: string) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}.${month}.${day} ${hour}:${minute}`
}

function goBack() {
  const from = route.query.from
  if (from === 'battles') {
    router.back()
  } else {
    router.push('/')
  }
}

async function bookmarkQuestion(detailId: number) {
    try {
        const battleId = Number(route.params.id)
        if (!battleId) return
        
        await battleApi.bookmarkBattleDetail(battleId, detailId)
        await modalStore.openSuccess('오답노트에 저장되었습니다!')
    } catch (e) {
        console.error(e)
        await modalStore.openAlert('오답노트 저장에 실패했습니다.')
    }
}

onMounted(async () => {
    const battleId = route.params.id
    
    // If coming directly from game finish, we might use store but store might miss metadata like stage title if not populated
    // So let's fetch fresh data to be consistent with "My Battles" view
    // Or check if store has everything. GameStore usually has game state.
    
    if (battleId) {
        try {
           const [battleRes, detailsRes] = await Promise.all([
               battleApi.getBattle(Number(battleId)),
               battleApi.getBattleDetails(Number(battleId))
           ])
           
           const battle = battleRes.data
           const details = detailsRes.data
           
           if (!battle || !details) {
               throw new Error('Data not found')
           }

           const totalScore = battle.totalDamage || details.reduce((acc: number, cur: any) => acc + (cur.damage || 0), 0)
           
           result.value = {
               battle,
               details,
               totalScore
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

/* Card Styles - Matches BookmarkDetail .section-card */
.grading-card {
    background: rgba(0,0,0,0.25);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    padding: 20px;
    text-align: left;
    transition: all 0.2s ease;
}

.grading-card:hover {
    transform: translateY(-2px);
    border-color: rgba(74, 158, 255, 0.3);
    background: rgba(0,0,0,0.4);
    box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.grading-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    border-bottom: 1px solid rgba(255,255,255,0.05);
    padding-bottom: 12px;
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
    font-size: 11px;
    padding: 4px 10px;
    height: auto;
    background: transparent !important;
    border: 1px solid #333 !important;
    color: #666;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: none !important;
}

.bookmark-btn:hover {
    background: rgba(255, 212, 59, 0.1) !important;
    border-color: #ffd43b !important;
    color: #ffd43b;
    transform: translateY(-2px);
    box-shadow: 0 0 10px rgba(255, 212, 59, 0.2) !important;
}

.grading-question-text {
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    line-height: 1.5;
    margin-bottom: 20px;
}

.grading-body {
    display: flex;
    flex-direction: column;
    gap: 15px;
    font-size: 14px;
}

/* Updated Box Styles */
.answer-box, .feedback-box {
    background: transparent;
    padding: 0;
    border: none;
}

.label {
    display: block;
    font-size: 12px;
    font-weight: bold;
    color: #4a9eff; /* Matches section-label */
    margin-bottom: 6px;
    letter-spacing: 0.5px;
}

/* Specifically for AI Feedback label to match BookmarkDetail */
.feedback-box .label {
    color: #4a9eff; 
}
/* But wait, BookmarkDetail uses "AI UPDATE" text which is same color. */

.user-answer {
    color: #ddd;
    line-height: 1.6;
    margin: 0;
    padding-left: 10px;
    border-left: 2px solid #555;
}

.ai-feedback {
    color: #51cf66;
    line-height: 1.6;
    margin: 0;
    padding-left: 10px;
    border-left: 2px solid rgba(81, 207, 102, 0.5);
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

/* Metadata Styles */
.meta-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    width: 100%;
    margin-bottom: 20px;
    padding: 0 10px; /* Reduced padding slightly */
    box-sizing: border-box;
}

.meta-left-col {
    display: flex;
    flex-direction: column;
    gap: 6px;
    align-items: flex-start;
}

.meta-main-row {
    display: flex;
    align-items: center;
    gap: 10px;
}

.meta-badge.category {
    background: rgba(74, 158, 255, 0.2);
    color: #4a9eff;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    border: 1px solid rgba(74, 158, 255, 0.3);
}

.meta-separator {
    color: #555;
    font-size: 12px;
}

.meta-text.stage {
    color: #eee;
    font-weight: 600;
}

.meta-date {
    font-size: 13px;
    color: #666;
    font-family: monospace;
}

.spacer {
    flex: 1;
}

.score-display {
    display: flex;
    align-items: center;
    gap: 10px;
    background: rgba(0,0,0,0.3);
    padding: 6px 16px;
    border-radius: 8px;
    border: 1px solid rgba(255,212,59,0.2);
}

.score-label {
    font-size: 11px;
    color: #aaa;
    font-weight: 700;
    letter-spacing: 0.5px;
}

.score-value {
    font-size: 20px;
    color: #ffd43b;
    font-weight: bold;
    text-shadow: 0 0 10px rgba(255, 212, 59, 0.3);
}

/* Override footer button styles */
.victory-buttons .pixel-button {
    background: transparent !important;
    border: 1px solid #333 !important;
    color: #666;
    box-shadow: none !important;
    transition: all 0.2s;
}

.victory-buttons .pixel-button:hover {
    transform: translateY(-2px);
}

.victory-buttons .pixel-button.secondary {
    color: #4a9eff;
}

.victory-buttons .pixel-button.secondary:hover {
    background: rgba(74, 158, 255, 0.1) !important;
    border-color: #4a9eff !important;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.2) !important;
}
.sub-score {
    font-size: 0.6em;
    color: #666;
    margin-left: 2px;
    font-weight: 500;
}
</style>
