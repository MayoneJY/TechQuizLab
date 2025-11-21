<template>
  <div class="raid-battle-container">
    <div v-if="!raidState" class="loading-state">
      <LoadingSpinner />
      <p>레이드 정보를 불러오는 중...</p>
    </div>
    
    <div v-else class="battle-layout">
      <!-- 좌측: 몬스터 정보 및 HP -->
      <div class="left-panel">
        <div class="monster-info-card">
          <div class="monster-header">
            <IconMonster :size="48" :color="primary" />
            <div>
              <h2 class="monster-name">{{ raidState.monsterName }}</h2>
              <div class="participants-info">
                <IconUsers :size="16" :color="textSecondary" />
                <span>{{ raidState.participants?.length || 0 }}명 참가</span>
              </div>
            </div>
          </div>
          
          <div class="hp-section">
            <div class="hp-label">
              <IconHeart :size="20" color="#ef4444" />
              <span>몬스터 HP</span>
            </div>
            <HPBar
              :current="raidState.currentHp"
              :max="raidState.maxHp"
              label=""
            />
            <div class="hp-text">
              {{ raidState.currentHp }} / {{ raidState.maxHp }}
            </div>
          </div>
        </div>

        <!-- 현재 턴 정보 -->
        <div v-if="currentTurn" class="turn-info-card">
          <div class="turn-header">
            <IconStar :size="24" :color="warning" />
            <h3>턴 {{ currentTurn.turnNumber }}</h3>
          </div>
          <div class="question-box">
            <div class="question-label">질문</div>
            <p class="question-text">{{ currentTurn.question?.text || '질문을 불러오는 중...' }}</p>
          </div>
          <div class="turn-status">
            <span :class="['status-badge', `status-${currentTurn.status}`]">
              {{ getTurnStatusText(currentTurn.status) }}
            </span>
            <span class="turn-timer" v-if="currentTurn.status === 'WAITING'">
              대기 중...
            </span>
          </div>
        </div>
      </div>

      <!-- 중앙: 채팅방 스타일 답변 목록 -->
      <div class="center-panel">
        <div class="chat-header">
          <h3>참가자 답변</h3>
          <div class="turn-indicator">
            <span v-if="currentTurn">턴 {{ currentTurn.turnNumber }}</span>
          </div>
        </div>
        
        <div class="chat-messages" ref="chatContainer">
          <div
            v-for="message in chatMessages"
            :key="message.id"
            :class="['message', `message-${message.type}`, { 'is-mine': message.isMine }]"
          >
            <div class="message-avatar">
              <IconUsers :size="24" :color="message.isMine ? primary : textSecondary" />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-author">{{ message.author }}</span>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
              <div class="message-body">
                <p v-if="message.type === 'answer'">{{ message.content }}</p>
                <div v-else-if="message.type === 'result'" class="result-message">
                  <div class="result-stats">
                    <span class="stat-item">
                      <IconSword :size="16" color="#ef4444" />
                      데미지: {{ message.damage }}
                    </span>
                    <span class="stat-item">
                      <IconGem :size="16" color="#f59e0b" />
                      XP: {{ message.xp }}
                    </span>
                  </div>
                </div>
                <div v-else-if="message.type === 'system'" class="system-message">
                  {{ message.content }}
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="chatMessages.length === 0" class="empty-chat">
            <IconStar :size="48" :color="textMuted" />
            <p>아직 답변이 없습니다.</p>
            <p class="hint">첫 번째 답변을 작성해보세요!</p>
          </div>
        </div>

        <!-- 답변 입력 영역 -->
        <div v-if="canAnswer" class="answer-input-area">
          <div class="input-header">
            <label>답변 작성</label>
            <span class="char-count">{{ answer.length }} / 500</span>
          </div>
          <textarea
            v-model="answer"
            placeholder="질문에 대한 답변을 입력하세요..."
            class="answer-textarea"
            rows="4"
            maxlength="500"
          ></textarea>
          <button
            @click="submitAnswer"
            class="btn btn-primary btn-submit"
            :disabled="submitting || !answer.trim()"
          >
            <IconSword :size="20" color="white" />
            {{ submitting ? '제출 중...' : '답변 제출' }}
          </button>
        </div>
        
        <div v-else class="waiting-area">
          <div class="waiting-message">
            <IconZap :size="24" :color="warning" />
            <p>이미 답변을 제출했습니다. 다른 참가자들의 답변을 기다리는 중...</p>
          </div>
        </div>
      </div>

      <!-- 우측: 참가자 목록 및 턴 히스토리 -->
      <div class="right-panel">
        <div class="participants-card">
          <h3 class="card-title">참가자</h3>
          <div class="participants-list">
            <div
              v-for="participant in raidState.participants"
              :key="participant.userId"
              :class="['participant-item', { 'has-answered': hasAnswered(participant.userId) }]"
            >
              <div class="participant-avatar">
                <IconUsers :size="20" :color="hasAnswered(participant.userId) ? success : textMuted" />
              </div>
              <span class="participant-name">{{ participant.nickname }}</span>
              <span v-if="hasAnswered(participant.userId)" class="answered-badge">
                ✓
              </span>
            </div>
          </div>
        </div>

        <div class="turn-history-card">
          <h3 class="card-title">턴 히스토리</h3>
          <div class="history-list">
            <div
              v-for="turn in turnHistory"
              :key="turn.turnNumber"
              :class="['history-item', { 'current': turn.turnNumber === currentTurn?.turnNumber }]"
            >
              <div class="history-turn">턴 {{ turn.turnNumber }}</div>
              <div class="history-status">{{ getTurnStatusText(turn.status) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 레이드 클리어 모달 -->
    <div v-if="raidState.status === 'CLEARED'" class="clear-modal">
      <div class="modal-content">
        <IconTrophy :size="80" color="#f59e0b" />
        <h2>레이드 클리어!</h2>
        <p>축하합니다! 몬스터를 물리쳤습니다.</p>
        <button @click="goToRaids" class="btn btn-primary btn-large">
          레이드 목록으로
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { raidApi } from '@/api/raid'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconGem from '@/components/icons/IconGem.vue'
import IconZap from '@/components/icons/IconZap.vue'
import HPBar from '@/components/HPBar.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()

const primary = '#6366f1'
const warning = '#f59e0b'
const success = '#10b981'
const textSecondary = '#475569'
const textMuted = '#94a3b8'

const raidState = ref(null)
const answer = ref('')
const submitting = ref(false)
const chatMessages = ref([])
const currentTurn = ref(null)
const turnHistory = ref([])
const myUserId = ref(1) // TODO: 실제 사용자 ID 가져오기
const chatContainer = ref(null)

const canAnswer = computed(() => {
  if (!currentTurn.value || currentTurn.value.status !== 'WAITING') return false
  return !hasAnswered(myUserId.value)
})

const fetchRaidState = async () => {
  try {
    const response = await raidApi.getRaidState(route.params.id)
    raidState.value = response.data
    
    // 턴 정보 업데이트
    if (response.data.currentTurn) {
      currentTurn.value = response.data.currentTurn
    }
    
    // 채팅 메시지 업데이트
    updateChatMessages(response.data)
    
    // 턴 히스토리 업데이트
    if (response.data.turnHistory) {
      turnHistory.value = response.data.turnHistory
    }
  } catch (error) {
    console.error('레이드 상태 조회 실패:', error)
  }
}

const updateChatMessages = (state) => {
  const messages = []
  
  // 시스템 메시지: 턴 시작
  if (state.currentTurn && state.currentTurn.status === 'WAITING') {
    messages.push({
      id: `turn-${state.currentTurn.turnNumber}-start`,
      type: 'system',
      content: `턴 ${state.currentTurn.turnNumber}이 시작되었습니다.`,
      timestamp: new Date(),
    })
  }
  
  // 참가자들의 답변
  if (state.currentTurn && state.currentTurn.answers) {
    state.currentTurn.answers.forEach((answer, index) => {
      messages.push({
        id: `answer-${answer.userId}-${index}`,
        type: 'answer',
        author: answer.nickname || `사용자 ${answer.userId}`,
        content: answer.answer,
        timestamp: new Date(answer.submittedAt || Date.now()),
        userId: answer.userId,
        isMine: answer.userId === myUserId.value,
      })
    })
  }
  
  // 결과 메시지
  if (state.currentTurn && state.currentTurn.results) {
    state.currentTurn.results.forEach((result) => {
      messages.push({
        id: `result-${result.userId}`,
        type: 'result',
        author: result.nickname || `사용자 ${result.userId}`,
        damage: result.damage,
        xp: result.xp,
        timestamp: new Date(),
        userId: result.userId,
        isMine: result.userId === myUserId.value,
      })
    })
  }
  
  chatMessages.value = messages
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const submitAnswer = async () => {
  if (!answer.value.trim()) {
    alert('답변을 입력해주세요.')
    return
  }

  submitting.value = true

  try {
    const response = await raidApi.submitAnswer(route.params.id, {
      questionId: currentTurn.value.question.id,
      answer: answer.value,
    })

    // 답변을 채팅에 추가
    chatMessages.value.push({
      id: `my-answer-${Date.now()}`,
      type: 'answer',
      author: '나',
      content: answer.value,
      timestamp: new Date(),
      userId: myUserId.value,
      isMine: true,
    })

    answer.value = ''
    scrollToBottom()
    
    // 상태 새로고침
    await fetchRaidState()
  } catch (error) {
    alert('답변 제출에 실패했습니다.')
  } finally {
    submitting.value = false
  }
}

const hasAnswered = (userId) => {
  if (!currentTurn.value || !currentTurn.value.answers) return false
  return currentTurn.value.answers.some(a => a.userId === userId)
}

const getTurnStatusText = (status) => {
  const statusMap = {
    WAITING: '답변 대기 중',
    EVALUATING: '평가 중',
    COMPLETED: '완료',
  }
  return statusMap[status] || status
}

const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
}

const goToRaids = () => {
  router.push('/raids')
}

let pollInterval = null

onMounted(() => {
  fetchRaidState()
  pollInterval = setInterval(fetchRaidState, 3000)
})

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})

watch(chatMessages, () => {
  scrollToBottom()
}, { deep: true })
</script>

<style scoped>
.raid-battle-container {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: var(--spacing-2xl);
  min-height: calc(100vh - 80px);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-3xl);
  text-align: center;
  color: var(--text-secondary);
}

.battle-layout {
  display: grid;
  grid-template-columns: 320px 1fr 280px;
  gap: var(--spacing-xl);
  height: calc(100vh - 160px);
}

/* 좌측 패널 */
.left-panel {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  overflow-y: auto;
}

.monster-info-card,
.turn-info-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
}

.monster-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.monster-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.5rem 0;
}

.participants-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.hp-section {
  margin-top: var(--spacing-lg);
}

.hp-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.hp-text {
  text-align: center;
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-top: var(--spacing-sm);
}

.turn-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.turn-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.question-box {
  background: var(--bg-secondary);
  border-radius: 12px;
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.question-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: var(--spacing-sm);
}

.question-text {
  font-size: 1rem;
  line-height: 1.6;
  color: var(--text-primary);
  margin: 0;
}

.turn-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
}

.status-waiting {
  background: rgba(245, 158, 11, 0.1);
  color: var(--warning);
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.status-evaluating {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
  border: 1px solid rgba(99, 102, 241, 0.2);
}

.status-completed {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.turn-timer {
  font-size: 0.875rem;
  color: var(--text-muted);
}

/* 중앙 패널 - 채팅방 */
.center-panel {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border);
  background: var(--bg-secondary);
}

.chat-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.turn-indicator {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.message {
  display: flex;
  gap: var(--spacing-md);
  animation: slideIn 0.3s ease;
}

.message.is-mine {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message.is-mine .message-avatar {
  background: rgba(99, 102, 241, 0.1);
}

.message-content {
  flex: 1;
  max-width: 70%;
  min-width: 0;
}

.message.is-mine .message-content {
  text-align: right;
}

.message-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: 0.25rem;
}

.message.is-mine .message-header {
  justify-content: flex-end;
}

.message-author {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.message-time {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.message-body {
  background: var(--bg-secondary);
  border-radius: 12px;
  padding: var(--spacing-md);
}

.message.is-mine .message-body {
  background: rgba(99, 102, 241, 0.1);
}

.message-body p {
  margin: 0;
  line-height: 1.6;
  color: var(--text-primary);
  font-size: 0.9375rem;
}

.result-message {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.result-stats {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.system-message {
  text-align: center;
  color: var(--text-muted);
  font-size: 0.875rem;
  font-style: italic;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-3xl);
  color: var(--text-muted);
  text-align: center;
}

.empty-chat p {
  margin: 0;
  font-size: 1rem;
}

.hint {
  font-size: 0.875rem;
  color: var(--text-muted);
}

.answer-input-area,
.waiting-area {
  padding: var(--spacing-xl);
  border-top: 1px solid var(--border);
  background: var(--bg-secondary);
}

.input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.input-header label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.char-count {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.answer-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border);
  border-radius: 12px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: 0.9375rem;
  line-height: 1.6;
  resize: vertical;
  margin-bottom: var(--spacing-md);
  transition: all 0.2s ease;
}

.answer-textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.btn-submit {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.waiting-message {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: rgba(245, 158, 11, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.waiting-message p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.9375rem;
}

/* 우측 패널 */
.right-panel {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  overflow-y: auto;
}

.participants-card,
.turn-history-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
}

.card-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-lg) 0;
}

.participants-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.participant-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-secondary);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.participant-item.has-answered {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.participant-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.participant-name {
  flex: 1;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-primary);
}

.answered-badge {
  display: flex;
  align-items: center;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--bg-secondary);
  border-radius: 12px;
  font-size: 0.875rem;
}

.history-item.current {
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
}

.history-turn {
  font-weight: 600;
  color: var(--text-primary);
}

.history-status {
  color: var(--text-secondary);
}

/* 클리어 모달 */
.clear-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: var(--bg-card);
  border-radius: 24px;
  padding: var(--spacing-3xl);
  text-align: center;
  box-shadow: var(--shadow-2xl);
  max-width: 500px;
  width: 90%;
}

.modal-content h2 {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: var(--spacing-lg) 0;
}

.modal-content p {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-2xl);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1400px) {
  .battle-layout {
    grid-template-columns: 280px 1fr 240px;
  }
}

@media (max-width: 1024px) {
  .battle-layout {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .left-panel,
  .right-panel {
    display: none;
  }
  
  .center-panel {
    min-height: 600px;
  }
}
</style>
