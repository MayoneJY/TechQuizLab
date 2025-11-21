<template>
  <div class="page-container">
    <div class="waiting-room">
      <div class="waiting-header">
        <div class="header-content">
          <h1 class="room-title">
            <IconRaid :size="40" :color="primary" />
            {{ raidInfo.monsterName }} 레이드 대기방
          </h1>
          <div class="room-status">
            <span :class="['status-badge', `status-${raidInfo.status.toLowerCase()}`]">
              {{ getStatusText(raidInfo.status) }}
            </span>
            <span class="participant-count">
              <IconUsers :size="20" :color="primary" />
              {{ raidInfo.participants?.length || 0 }} / {{ maxParticipants }}명
            </span>
          </div>
        </div>
      </div>

      <div class="waiting-content">
        <div class="participants-section">
          <h2 class="section-title">참가자 목록</h2>
          <div class="participants-grid">
            <div
              v-for="participant in raidInfo.participants"
              :key="participant.userId"
              class="participant-card"
            >
              <div class="participant-avatar">
                <IconUsers :size="32" :color="primary" />
              </div>
              <div class="participant-info">
                <div class="participant-name">{{ participant.nickname }}</div>
                <div class="participant-status">대기 중</div>
              </div>
            </div>
            <div
              v-for="n in emptySlots"
              :key="`empty-${n}`"
              class="participant-card empty"
            >
              <div class="participant-avatar empty">
                <IconUsers :size="32" color="#94a3b8" />
              </div>
              <div class="participant-info">
                <div class="participant-name empty">대기 중...</div>
              </div>
            </div>
          </div>
        </div>

        <div class="monster-preview-section">
          <h2 class="section-title">몬스터 정보</h2>
          <div class="monster-preview-card">
            <div class="monster-icon-large">
              <IconMonster :size="80" :color="primary" />
            </div>
            <div class="monster-details">
              <h3 class="monster-name">{{ raidInfo.monsterName }}</h3>
              <div class="monster-hp">
                <HPBar
                  :current="raidInfo.currentHp"
                  :max="raidInfo.maxHp"
                  label="HP"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="waiting-actions">
        <div v-if="raidInfo.status === 'OPEN'" class="waiting-message">
          <IconZap :size="24" :color="warning" />
          <p>최소 {{ minParticipants }}명 이상 모이면 자동으로 시작됩니다.</p>
        </div>
        <div v-else-if="raidInfo.status === 'IN_PROGRESS'" class="start-message">
          <IconSword :size="24" :color="success" />
          <p>레이드가 시작되었습니다!</p>
          <button @click="enterBattle" class="btn btn-primary btn-large">
            <IconSword :size="20" color="white" />
            전투 입장
          </button>
        </div>
        <button
          v-if="raidInfo.status === 'OPEN'"
          @click="leaveRaid"
          class="btn btn-secondary"
        >
          나가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { raidApi } from '@/api/raid'
import IconRaid from '@/components/icons/IconRaid.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconZap from '@/components/icons/IconZap.vue'
import HPBar from '@/components/HPBar.vue'

const route = useRoute()
const router = useRouter()

const raidInfo = ref({
  id: null,
  monsterName: '',
  status: 'OPEN',
  currentHp: 0,
  maxHp: 0,
  participants: [],
})

const primary = '#6366f1'
const warning = '#f59e0b'
const success = '#10b981'
const minParticipants = 2
const maxParticipants = 4

const emptySlots = computed(() => {
  const current = raidInfo.value.participants?.length || 0
  return Math.max(0, maxParticipants - current)
})

const fetchRaidInfo = async () => {
  try {
    const response = await raidApi.getRaid(route.params.id)
    raidInfo.value = response.data
  } catch (error) {
    console.error('레이드 정보 조회 실패:', error)
  }
}

const enterBattle = () => {
  router.push(`/raids/${route.params.id}/battle`)
}

const leaveRaid = async () => {
  if (confirm('정말 나가시겠습니까?')) {
    // TODO: API 호출
    router.push('/raids')
  }
}

const getStatusText = (status) => {
  const statusMap = {
    OPEN: '모집 중',
    IN_PROGRESS: '진행 중',
    CLOSED: '종료',
  }
  return statusMap[status] || status
}

let pollInterval = null

onMounted(() => {
  fetchRaidInfo()
  pollInterval = setInterval(() => {
    fetchRaidInfo()
    if (raidInfo.value.status === 'IN_PROGRESS') {
      clearInterval(pollInterval)
    }
  }, 3000)
})

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})
</script>

<style scoped>
.waiting-room {
  max-width: 1200px;
  margin: 0 auto;
}

.waiting-header {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
}

.room-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.room-status {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.status-badge {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
}

.status-open {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.status-in_progress {
  background: rgba(245, 158, 11, 0.1);
  color: var(--warning);
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.participant-count {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.waiting-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}

.participants-section {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
}

.participants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.participant-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-secondary);
  border-radius: 12px;
  border: 1px solid var(--border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.participant-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--primary);
  transform: scaleY(0);
  transform-origin: bottom;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.participant-card:hover:not(.empty) {
  background: var(--bg-card-hover);
  border-color: var(--border-light);
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.participant-card:hover:not(.empty)::before {
  transform: scaleY(1);
}

.participant-card.empty {
  opacity: 0.5;
  border-style: dashed;
}

.participant-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.participant-avatar.empty {
  background: var(--bg-secondary);
}

.participant-info {
  flex: 1;
  min-width: 0;
}

.participant-name {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.participant-name.empty {
  color: var(--text-muted);
  font-weight: 400;
}

.participant-status {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.monster-preview-section {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
}

.monster-preview-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--bg-secondary);
  border-radius: 12px;
}

.monster-icon-large {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  background: rgba(99, 102, 241, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.monster-details {
  width: 100%;
  text-align: center;
}

.monster-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}

.monster-hp {
  width: 100%;
}

.waiting-actions {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
  text-align: center;
}

.waiting-message,
.start-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.waiting-message p,
.start-message p {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin: 0;
}

.btn-large {
  padding: 1rem 2rem;
  font-size: 1.125rem;
}

@media (max-width: 1024px) {
  .waiting-content {
    grid-template-columns: 1fr;
  }
  
  .participants-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>

