<template>
  <div class="page-container">
    <PageHeader title="레이드 목록" :icon="IconRaid" />
    
    <div class="raids-filters">
      <select v-model="statusFilter" class="filter-select">
        <option value="">전체</option>
        <option value="OPEN">모집 중</option>
        <option value="IN_PROGRESS">진행 중</option>
        <option value="CLOSED">종료</option>
      </select>
      <button @click="refreshRaids" class="btn btn-secondary">
        <IconZap :size="20" />
        새로고침
      </button>
    </div>

    <LoadingSpinner v-if="loading" />
    <EmptyState v-else-if="filteredRaids.length === 0" message="레이드가 없습니다." :icon="IconRaid" />
    
    <div v-else class="raids-grid">
      <GamificationCard
        v-for="raid in filteredRaids"
        :key="raid.id"
        :variant="getRaidVariant(raid.status)"
        class="raid-card card-hover"
        @click="handleRaidClick(raid)"
      >
        <div class="raid-header">
          <div class="raid-title-section">
            <h3 class="raid-name">{{ raid.monsterName }}</h3>
            <span :class="['raid-status', `status-${raid.status.toLowerCase()}`]">
              {{ getStatusText(raid.status) }}
            </span>
          </div>
          <IconRaid :size="32" :color="getStatusColor(raid.status)" />
        </div>

        <div class="raid-hp-section">
          <HPBar
            :current="raid.currentHp"
            :max="raid.maxHp"
            label="HP"
          />
        </div>

        <div class="raid-info">
          <div class="info-row">
            <span class="info-label">참여자</span>
            <span class="info-value">{{ raid.participants.length }}명</span>
          </div>
          <div class="info-row">
            <span class="info-label">생성자</span>
            <span class="info-value">{{ raid.createdBy.nickname }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">생성일</span>
            <span class="info-value">{{ formatDate(raid.createdAt) }}</span>
          </div>
        </div>

        <div class="raid-participants">
          <div class="participants-label">참여자 목록</div>
          <div class="participants-list">
            <span
              v-for="participant in raid.participants"
              :key="participant.userId"
              class="participant-badge"
            >
              {{ participant.nickname }}
            </span>
            <span v-if="raid.participants.length === 0" class="no-participants">
              참여자 없음
            </span>
          </div>
        </div>

        <div class="raid-actions">
          <button
            v-if="raid.status === 'OPEN' && !isParticipating(raid)"
            @click.stop="joinRaid(raid.id)"
            class="btn btn-primary btn-sm"
            :disabled="joiningRaidId === raid.id"
          >
            <IconUsers :size="16" />
            {{ joiningRaidId === raid.id ? '참가 중...' : '참가하기' }}
          </button>
          <button
            v-else-if="raid.status !== 'CLOSED'"
            @click.stop="goToRaid(raid.id)"
            class="btn btn-primary btn-sm"
          >
            <IconSword :size="16" />
            입장하기
          </button>
          <span v-else class="closed-text">종료됨</span>
        </div>
      </GamificationCard>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { raidApi } from '@/api/raid'
import GamificationCard from '@/components/GamificationCard.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import HPBar from '@/components/HPBar.vue'
import IconRaid from '@/components/icons/IconRaid.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconZap from '@/components/icons/IconZap.vue'

const router = useRouter()

const raids = ref([])
const loading = ref(false)
const statusFilter = ref('')
const joiningRaidId = ref(null)

const filteredRaids = computed(() => {
  if (!statusFilter.value) return raids.value
  return raids.value.filter(raid => raid.status === statusFilter.value)
})

const fetchRaids = async () => {
  loading.value = true
  try {
    const response = await raidApi.getRaids()
    raids.value = response.data
  } catch (error) {
    alert('레이드 목록을 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const refreshRaids = () => {
  fetchRaids()
}

const joinRaid = async (raidId) => {
  joiningRaidId.value = raidId
  try {
    await raidApi.joinRaid(raidId)
    await fetchRaids()
    // 참가 후 대기방으로 이동
    router.push(`/raids/${raidId}/waiting`)
  } catch (error) {
    alert('레이드 참가에 실패했습니다.')
  } finally {
    joiningRaidId.value = null
  }
}

const goToRaid = (raidId) => {
  // 레이드 상태에 따라 대기방 또는 전투로 이동
  const raid = raids.value.find(r => r.id === raidId)
  if (raid && raid.status === 'IN_PROGRESS') {
    router.push(`/raids/${raidId}/battle`)
  } else {
    router.push(`/raids/${raidId}/waiting`)
  }
}

const handleRaidClick = (raid) => {
  if (raid.status !== 'CLOSED') {
    goToRaid(raid.id)
  }
}

const getRaidVariant = (status) => {
  if (status === 'OPEN') return 'primary'
  if (status === 'IN_PROGRESS') return 'warning'
  return 'danger'
}

const getStatusText = (status) => {
  const statusMap = {
    OPEN: '모집 중',
    IN_PROGRESS: '진행 중',
    CLOSED: '종료',
  }
  return statusMap[status] || status
}

const getStatusColor = (status) => {
  if (status === 'OPEN') return '#4ecdc4'
  if (status === 'IN_PROGRESS') return '#feca57'
  return '#999999'
}

const isParticipating = (raid) => {
  // 현재 사용자 ID는 1로 가정
  return raid.participants.some(p => p.userId === 1)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  fetchRaids()
  // 주기적으로 새로고침
  setInterval(fetchRaids, 10000)
})
</script>

<style scoped>
.raids-filters {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 0.75rem 1.25rem;
  border: 1px solid var(--border);
  border-radius: 10px;
  font-size: 0.875rem;
  font-weight: 500;
  background: var(--bg-card);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-sm);
}

.filter-select:hover {
  border-color: var(--border-light);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.filter-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.raids-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: var(--spacing-2xl);
}

.raid-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.raid-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
}

.raid-title-section {
  flex: 1;
}

.raid-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
  letter-spacing: -0.025em;
  transition: color 0.3s ease;
}

.raid-card:hover .raid-name {
  color: var(--primary);
}

.raid-status {
  display: inline-block;
  padding: 0.375rem 0.875rem;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid;
  transition: all 0.3s ease;
}

.status-open {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
  border-color: rgba(16, 185, 129, 0.2);
}

.status-in_progress {
  background: rgba(245, 158, 11, 0.1);
  color: var(--warning);
  border-color: rgba(245, 158, 11, 0.2);
}

.status-closed {
  background: var(--bg-secondary);
  color: var(--text-muted);
  border-color: var(--border);
}

.raid-hp-section {
  margin-bottom: 1.5rem;
}

.raid-info {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: var(--bg-secondary);
  border-radius: 12px;
  border: 1px solid var(--border);
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
  font-size: 0.875rem;
  font-weight: 500;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: var(--text-secondary);
}

.info-value {
  color: var(--text-primary);
  font-weight: 500;
}

.raid-participants {
  margin-bottom: 1.5rem;
}

.participants-label {
  font-size: 12px;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.participants-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.participant-badge {
  padding: 0.375rem 0.75rem;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.participant-badge:hover {
  background: var(--bg-card-hover);
  border-color: var(--border-light);
  transform: translateY(-1px);
}

.no-participants {
  color: var(--text-muted);
  font-size: 12px;
  font-family: 'DungGeunMo', sans-serif;
}

.raid-actions {
  display: flex;
  justify-content: flex-end;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 12px;
}

.closed-text {
  color: var(--text-muted);
  font-size: 14px;
  font-family: 'DungGeunMo', sans-serif;
}

@media (max-width: 768px) {
  .raids-grid {
    grid-template-columns: 1fr;
  }
}
</style>

