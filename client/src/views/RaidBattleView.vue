<template>
  <div class="raid-battle">
    <div v-if="!raidState" class="loading">
      <div class="spinner"></div>
      레이드 정보를 불러오는 중...
    </div>
    <div v-else class="battle-content">
      <div class="raid-header game-card game-card-primary">
        <div class="raid-header-content">
          <div>
            <h1>
              <IconUsers :size="32" color="white" />
              {{ raidState.monsterName }}
            </h1>
            <div class="participants">
              <IconUsers :size="20" color="white" />
              참여자: {{ raidState.participants?.length || 0 }}명
            </div>
          </div>
        </div>
      </div>

      <div class="monster-hp game-card">
        <div class="hp-header">
          <IconHeart :size="24" color="#ef4444" />
          <span class="hp-title">몬스터 HP</span>
        </div>
        <div class="hp-bar-container">
          <div
            class="hp-bar-fill"
            :style="{ width: `${(raidState.currentHp / raidState.maxHp) * 100}%` }"
          ></div>
          <div class="hp-bar-text">
            {{ raidState.currentHp }} / {{ raidState.maxHp }}
          </div>
        </div>
      </div>

      <div v-if="raidState.currentQuestion" class="question-section game-card">
        <div class="question-card">
          <div class="question-header">
            <IconStar :size="24" color="#f59e0b" />
            <h3>질문</h3>
          </div>
          <p class="question-text">{{ raidState.currentQuestion.text }}</p>
        </div>

        <div class="answer-section">
          <label>답변</label>
          <textarea
            v-model="answer"
            rows="10"
            placeholder="답변을 입력하세요..."
            class="answer-input"
          ></textarea>
          <button
            @click="submitAnswer"
            class="btn-game btn-game-primary btn-submit"
            :disabled="submitting || !canSubmit"
          >
            <IconSword :size="20" color="white" />
            {{ submitting ? '제출 중...' : '답변 제출' }}
          </button>
        </div>
      </div>

      <div v-if="turnResult" class="turn-result game-card">
        <h3>
          <IconTrophy :size="24" color="#ffd700" />
          턴 결과
        </h3>
        <div class="result-grid">
          <div class="result-item">
            <IconSword :size="32" color="#ef4444" />
            <div class="result-content">
              <span class="result-label">내 데미지</span>
              <span class="result-value">{{ turnResult.myDamage }}</span>
            </div>
          </div>
          <div class="result-item">
            <IconUsers :size="32" color="#4ecdc4" />
            <div class="result-content">
              <span class="result-label">팀 데미지</span>
              <span class="result-value">{{ turnResult.teamDamage }}</span>
            </div>
          </div>
          <div class="result-item">
            <IconGem :size="32" color="#f59e0b" />
            <div class="result-content">
              <span class="result-label">획득 XP</span>
              <span class="result-value">{{ turnResult.myXp }}</span>
            </div>
          </div>
        </div>
        <button @click="nextTurn" class="btn-game btn-game-secondary btn-full">
          다음 턴
        </button>
      </div>

      <div v-if="raidState.status === 'CLEARED'" class="raid-cleared game-card game-card-success">
        <IconTrophy :size="80" color="white" />
        <h2>레이드 클리어!</h2>
        <p>축하합니다! 몬스터를 물리쳤습니다.</p>
        <button @click="goToMonsters" class="btn-game btn-game-primary">
          <IconZap :size="20" color="white" />
          몬스터 목록으로
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { raidApi } from '@/api/raid'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconGem from '@/components/icons/IconGem.vue'
import IconZap from '@/components/icons/IconZap.vue'

const route = useRoute()
const router = useRouter()

const raidState = ref(null)
const answer = ref('')
const submitting = ref(false)
const turnResult = ref(null)
const canSubmit = ref(true)
let pollInterval = null

const fetchRaidState = async () => {
  try {
    const response = await raidApi.getRaidState(route.params.id)
    raidState.value = response.data
  } catch (error) {
    console.error('레이드 상태 조회 실패:', error)
  }
}

const submitAnswer = async () => {
  if (!answer.value.trim()) {
    alert('답변을 입력해주세요.')
    return
  }

  submitting.value = true
  canSubmit.value = false

  try {
    const response = await raidApi.submitAnswer(route.params.id, {
      questionId: raidState.value.currentQuestion.id,
      answer: answer.value,
    })

    turnResult.value = response.data
    answer.value = ''

    await fetchRaidState()
  } catch (error) {
    alert('답변 제출에 실패했습니다.')
  } finally {
    submitting.value = false
  }
}

const nextTurn = () => {
  turnResult.value = null
  canSubmit.value = true
  fetchRaidState()
}

const goToMonsters = () => {
  router.push('/monsters')
}

onMounted(() => {
  fetchRaidState()
  pollInterval = setInterval(fetchRaidState, 5000)
})

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})
</script>

<style scoped>
.raid-battle {
  max-width: 1000px;
  margin: 0 auto;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 4rem 2rem;
  text-align: center;
  color: var(--text-secondary);
}

.spinner {
  width: 32px;
  height: 32px;
  border: 4px solid var(--border);
  border-top-color: var(--primary);
  border-right-color: var(--primary);
  border-radius: 8px;
  animation: spin 0.5s linear infinite;
  box-shadow: var(--shadow-sm);
}

@keyframes spin {
  to {
    transform: rotate(90deg);
  }
}

.raid-header {
  padding: 2rem;
  margin-bottom: 2rem;
  border: 2px solid var(--primary) !important;
  border-radius: 12px !important;
  box-shadow: var(--shadow-md) !important;
}

.raid-header-content h1 {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.25rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  margin: 0 0 1rem 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
  letter-spacing: 0.02em;
}

.participants {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
}

.monster-hp {
  padding: 2rem;
  margin-bottom: 2rem;
}

.hp-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.hp-title {
  font-size: 1rem;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-primary);
  letter-spacing: 0.02em;
}

.hp-bar-container {
  position: relative;
  width: 100%;
  height: 40px;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--shadow-inset);
}

.hp-bar-fill {
  height: 100%;
  background: var(--gradient-success);
  border-radius: 20px;
  transition: width 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.hp-bar-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  font-size: 12px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  z-index: 1;
  color: var(--text-primary);
}

.question-section {
  padding: 2rem;
  margin-bottom: 2rem;
}

.question-card {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: var(--bg-card);
  border-radius: 12px;
  border: 2px solid var(--border);
  box-shadow: var(--shadow-md);
}

.question-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.question-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  letter-spacing: 0.02em;
}

.question-text {
  font-size: 16px;
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
  line-height: 1.7;
  color: var(--text-primary);
  margin: 0;
}

.answer-section label {
  display: block;
  margin-bottom: 0.75rem;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  font-size: 14px;
  color: var(--text-primary);
}

.answer-input {
  width: 100%;
  padding: 1rem;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 12px;
  font-size: 14px;
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  resize: vertical;
  min-height: 200px;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-inset);
}

.answer-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
  background: var(--bg-card-hover);
}

.btn-submit {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem;
  font-size: 1.1rem;
}

.turn-result {
  padding: 2rem;
  margin-bottom: 2rem;
}

.turn-result h3 {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: var(--bg-secondary);
  border-radius: 16px;
  border: 2px solid var(--border);
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.result-label {
  font-size: 0.9rem;
  color: var(--text-muted);
  font-weight: 500;
}

.result-value {
  font-weight: 800;
  font-size: 1.8rem;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.btn-full {
  width: 100%;
}

.raid-cleared {
  text-align: center;
  padding: 3rem;
  border: none;
}

.raid-cleared h2 {
  font-size: 2.5rem;
  font-weight: 800;
  color: white;
  margin: 1rem 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.raid-cleared p {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 2rem;
}

@media (max-width: 768px) {
  .raid-header {
    padding: 1.5rem;
  }

  .raid-header-content h1 {
    font-size: 1.5rem;
  }

  .monster-hp,
  .question-section,
  .turn-result {
    padding: 1.5rem;
  }

  .result-grid {
    grid-template-columns: 1fr;
  }
}
</style>
