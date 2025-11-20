<template>
  <div class="solo-battle">
    <div v-if="!battleStarted" class="start-screen game-card">
      <div class="start-icon">
        <IconSword :size="80" color="#6366f1" />
      </div>
      <h1>개인전 시작</h1>
      <p>몬스터와의 전투를 시작합니다.</p>
      <button @click="startBattle" class="btn-game btn-game-primary btn-large">
        <IconZap :size="24" color="white" />
        전투 시작
      </button>
    </div>

    <div v-else-if="currentQuestion" class="battle-screen">
      <div class="monster-info game-card">
        <div class="monster-battle-header">
          <IconMonster :size="48" color="#6366f1" />
          <h2>{{ monsterName }}</h2>
        </div>
        <div class="hp-bar-container">
          <div class="hp-label">
            <IconHeart :size="20" color="#ef4444" />
            HP
          </div>
          <div class="hp-progress">
            <div class="hp-bar-fill" :style="{ width: `${(currentHp / maxHp) * 100}%` }"></div>
            <div class="hp-bar-text">{{ currentHp }} / {{ maxHp }}</div>
          </div>
        </div>
      </div>

      <div class="question-section game-card">
        <div class="question-card">
          <div class="question-header">
            <IconStar :size="24" color="#f59e0b" />
            <h3>질문</h3>
          </div>
          <p class="question-text">{{ currentQuestion.text }}</p>
        </div>

        <div class="answer-section">
          <label>답변</label>
          <textarea
            v-model="answer"
            rows="10"
            placeholder="답변을 입력하세요..."
            class="answer-input"
          ></textarea>
          <button @click="submitAnswer" class="btn-game btn-game-primary btn-submit" :disabled="submitting">
            <IconSword :size="20" color="white" />
            {{ submitting ? '제출 중...' : '답변 제출' }}
          </button>
        </div>
      </div>
    </div>

    <div v-else-if="battleResult" class="result-screen">
      <div class="result-header game-card game-card-success">
        <IconTrophy :size="64" color="white" />
        <h1>전투 결과</h1>
      </div>
      <div class="result-card game-card">
        <div class="result-stats">
          <div class="result-item">
            <IconGem :size="32" color="#4ecdc4" />
            <div class="result-content">
              <span class="result-label">획득 XP</span>
              <span class="result-value">{{ battleResult.totalXp }}</span>
            </div>
          </div>
          <div class="result-item">
            <IconSword :size="32" color="#ef4444" />
            <div class="result-content">
              <span class="result-label">데미지</span>
              <span class="result-value">{{ battleResult.totalDamage }}</span>
            </div>
          </div>
        </div>
        <div v-if="battleResult.feedback" class="feedback">
          <h3>피드백</h3>
          <div v-if="battleResult.feedback.good && battleResult.feedback.good.length > 0" class="feedback-section good">
            <h4>
              <IconStar :size="20" color="#10b981" />
              잘한 부분
            </h4>
            <ul>
              <li v-for="(item, idx) in battleResult.feedback.good" :key="idx">{{ item }}</li>
            </ul>
          </div>
          <div v-if="battleResult.feedback.bad && battleResult.feedback.bad.length > 0" class="feedback-section bad">
            <h4>
              <IconFire :size="20" color="#ef4444" />
              보완할 부분
            </h4>
            <ul>
              <li v-for="(item, idx) in battleResult.feedback.bad" :key="idx">{{ item }}</li>
            </ul>
          </div>
        </div>
      </div>
      <div class="result-actions">
        <button @click="goToMonster" class="btn-game btn-game-secondary">몬스터로 돌아가기</button>
        <button @click="restartBattle" class="btn-game btn-game-primary">
          <IconZap :size="20" color="white" />
          다시 시작
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { soloApi } from '@/api/solo'
import { useMonsterStore } from '@/stores/monster'
import IconSword from '@/components/icons/IconSword.vue'
import IconZap from '@/components/icons/IconZap.vue'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconGem from '@/components/icons/IconGem.vue'
import IconFire from '@/components/icons/IconFire.vue'

const route = useRoute()
const router = useRouter()
const monsterStore = useMonsterStore()

const battleStarted = ref(false)
const currentQuestion = ref(null)
const answer = ref('')
const submitting = ref(false)
const battleResult = ref(null)
const monsterName = ref('')
const currentHp = ref(0)
const maxHp = ref(0)
const battleId = ref(null)

onMounted(async () => {
  await monsterStore.fetchMonster(route.params.monsterId)
  if (monsterStore.currentMonster) {
    monsterName.value = monsterStore.currentMonster.name
    maxHp.value = monsterStore.currentMonster.hpMax
    currentHp.value = monsterStore.currentMonster.currentHp || monsterStore.currentMonster.hpBase
  }
})

const startBattle = async () => {
  try {
    const response = await soloApi.startSolo({ monsterId: route.params.monsterId })
    battleId.value = response.data.battleId
    currentQuestion.value = response.data.question
    battleStarted.value = true
  } catch (error) {
    alert('전투 시작에 실패했습니다.')
  }
}

const submitAnswer = async () => {
  if (!answer.value.trim()) {
    alert('답변을 입력해주세요.')
    return
  }

  submitting.value = true
  try {
    const response = await soloApi.submitAnswer({
      battleId: battleId.value,
      questionId: currentQuestion.value.id,
      answer: answer.value,
    })

    const result = response.data
    currentHp.value -= result.damage

    if (currentHp.value <= 0 || result.isFinished) {
      battleResult.value = {
        totalXp: result.totalXp || 0,
        totalDamage: result.totalDamage || 0,
        feedback: result.feedback,
      }
      currentQuestion.value = null
    } else {
      currentQuestion.value = result.nextQuestion
      answer.value = ''
    }
  } catch (error) {
    alert('답변 제출에 실패했습니다.')
  } finally {
    submitting.value = false
  }
}

const goToMonster = () => {
  router.push(`/monsters/${route.params.monsterId}`)
}

const restartBattle = () => {
  battleStarted.value = false
  battleResult.value = null
  answer.value = ''
  currentHp.value = maxHp.value
}
</script>

<style scoped>
.solo-battle {
  max-width: 1000px;
  margin: 0 auto;
}

.start-screen {
  text-align: center;
  padding: 3rem;
}

.start-icon {
  margin-bottom: 1.5rem;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.start-screen h1 {
  font-size: 1.5rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
  margin-bottom: 1rem;
  letter-spacing: 0.02em;
}

.start-screen p {
  color: var(--text-secondary);
  margin-bottom: 2rem;
  font-size: 14px;
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
}

.btn-large {
  padding: 1.25rem 2.5rem;
  font-size: 1.2rem;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
}

.monster-info {
  margin-bottom: 2rem;
  padding: 2rem;
}

.monster-battle-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.monster-battle-header h2 {
  font-size: 1.25rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: 0.02em;
}

.hp-bar-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.hp-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  font-size: 14px;
  color: var(--text-primary);
  min-width: 80px;
}

.hp-progress {
  flex: 1;
  position: relative;
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

.answer-input::placeholder {
  color: var(--text-muted);
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

.result-screen {
  text-align: center;
}

.result-header {
  padding: 3rem;
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.result-header h1 {
  font-size: 1.5rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
  letter-spacing: 0.02em;
}

.result-card {
  padding: 2.5rem;
  margin-bottom: 2rem;
  text-align: left;
  border: 4px solid var(--border-bright);
  border-radius: 0;
  box-shadow: var(--pixel-shadow);
  image-rendering: pixelated;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: var(--bg-card);
  border-radius: 12px;
  border: 2px solid var(--border);
  box-shadow: var(--shadow-md);
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.result-label {
  font-size: 12px;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-secondary);
  font-weight: 500;
}

.result-value {
  font-weight: 600;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  font-size: 1.25rem;
  color: var(--primary);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
}

.feedback {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 4px solid var(--border);
}

.feedback h3 {
  font-size: 1rem;
  font-weight: 500;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  letter-spacing: 0.02em;
}

.feedback-section {
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: var(--bg-card);
  border-radius: 12px;
  border: 2px solid var(--border);
  box-shadow: var(--shadow-md);
}

.feedback-section.good {
  border-color: rgba(16, 185, 129, 0.3);
  background: rgba(16, 185, 129, 0.05);
}

.feedback-section.bad {
  border-color: rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.05);
}

.feedback-section h4 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.feedback-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feedback-section li {
  padding: 0.75rem 0;
  color: var(--text-secondary);
  line-height: 1.6;
  border-bottom: 1px solid var(--border);
}

.feedback-section li:last-child {
  border-bottom: none;
}

.result-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .start-screen {
    padding: 2rem 1.5rem;
  }

  .start-screen h1 {
    font-size: 2rem;
  }

  .monster-info {
    padding: 1.5rem;
  }

  .monster-battle-header h2 {
    font-size: 1.5rem;
  }

  .hp-bar-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .hp-progress {
    width: 100%;
  }

  .question-section {
    padding: 1.5rem;
  }

  .result-stats {
    grid-template-columns: 1fr;
  }

  .result-actions {
    flex-direction: column;
  }

  .result-actions .btn-game {
    width: 100%;
  }
}
</style>
