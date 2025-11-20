<template>
  <div class="page-container">
    <PageHeader title="프로필" :icon="IconTrophy" />

    <div class="profile-content">
      <GamificationCard variant="primary">
        <CardHeader title="사용자 정보" :icon="IconStar" icon-color="#f59e0b" />
        <div class="info-item">
          <span class="label">닉네임</span>
          <span class="value">{{ authStore.user?.nickname || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">이메일</span>
          <span class="value">{{ authStore.user?.email || '-' }}</span>
        </div>
      </GamificationCard>

      <GamificationCard variant="success">
        <CardHeader title="능력치" :icon="IconGem" icon-color="#4ecdc4" />
        <LoadingSpinner v-if="statsLoading" />
        <EmptyState
          v-else-if="stats.length === 0"
          message="능력치가 없습니다."
          :icon="IconMonster"
        />
        <div v-else class="stats-grid">
          <GamificationCard v-for="stat in stats" :key="stat.statCode" class="stat-card">
            <div class="stat-card-icon">
              <IconStar :size="32" color="#f59e0b" />
            </div>
            <div class="stat-name">{{ stat.statName || stat.statCode }}</div>
            <div class="level-badge">
              Lv.{{ stat.level || 1 }}
            </div>
            <div class="stat-xp">
              <IconGem :size="16" color="#4ecdc4" />
              {{ stat.xp || 0 }} XP
            </div>
          </GamificationCard>
        </div>
      </GamificationCard>

      <GamificationCard variant="warning">
        <CardHeader title="전투 이력" :icon="IconTrophy" icon-color="#ffd700" />
        <LoadingSpinner v-if="historyLoading" />
        <EmptyState
          v-else-if="history.length === 0"
          message="전투 이력이 없습니다."
          :icon="IconSword"
        />
        <div v-else class="history-list">
          <GamificationCard v-for="item in history" :key="item.id" class="history-item">
            <div class="history-icon">
              <IconMonster
                :size="40"
                :color="item.mode === 'RAID' ? '#ec4899' : '#4ecdc4'"
              />
            </div>
            <div class="history-content">
              <div class="history-title">{{ item.monsterName || '알 수 없음' }}</div>
              <div class="history-mode" :class="item.mode.toLowerCase()">
                {{ item.mode === 'SOLO' ? '개인전' : '레이드' }}
              </div>
              <div class="history-stats">
                <span class="stat-badge">
                  <IconGem :size="14" color="#f59e0b" />
                  {{ item.xpTotal || 0 }} XP
                </span>
                <span class="stat-badge">
                  <IconSword :size="14" color="#ef4444" />
                  {{ item.damage || 0 }} 데미지
                </span>
              </div>
              <div class="history-date">{{ formatDate(item.createdAt) }}</div>
            </div>
          </GamificationCard>
        </div>
      </GamificationCard>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useStatsStore } from '@/stores/stats'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconGem from '@/components/icons/IconGem.vue'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconSword from '@/components/icons/IconSword.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import GamificationCard from '@/components/GamificationCard.vue'
import CardHeader from '@/components/common/CardHeader.vue'

const authStore = useAuthStore()
const statsStore = useStatsStore()

const stats = ref([])
const history = ref([])
const statsLoading = ref(false)
const historyLoading = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  statsLoading.value = true
  const statsResult = await statsStore.fetchMyStats()
  if (statsResult.success) {
    stats.value = statsStore.stats
  }
  statsLoading.value = false

  historyLoading.value = true
  const historyResult = await statsStore.fetchMyHistory()
  if (historyResult.success) {
    history.value = statsStore.history
  }
  historyLoading.value = false
})
</script>

<style scoped>
.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}


.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 0;
  border-bottom: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.info-item:hover {
  padding-left: 0.5rem;
  background: linear-gradient(90deg, rgba(139, 92, 246, 0.1) 0%, transparent 100%);
  border-radius: 8px;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.info-item .value {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 1.05rem;
}



.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  text-align: center;
  padding: 1.5rem 1rem !important;
  border: 2px solid var(--border) !important;
  border-radius: 12px !important;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-md) !important;
}

.stat-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: var(--shadow-xl) !important;
  border-color: var(--primary) !important;
}

.stat-card-icon {
  margin-bottom: 1.25rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.stat-name {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 1rem;
  font-size: 1.1rem;
  letter-spacing: -0.01em;
}

.stat-xp {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-top: 0.75rem;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.history-item {
  display: flex;
  gap: 1.5rem;
  padding: 1rem !important;
  border: 2px solid var(--border) !important;
  border-radius: 12px !important;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: var(--shadow-md) !important;
}

.history-item:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: var(--shadow-xl) !important;
  border-color: var(--primary) !important;
}

.history-icon {
  flex-shrink: 0;
}

.history-content {
  flex: 1;
  min-width: 0;
}

.history-title {
  font-weight: 700;
  font-size: 1.1rem;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.history-mode {
  display: inline-block;
  padding: 0.375rem 0.875rem;
  border-radius: 12px;
  border: 2px solid;
  font-size: 12px;
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  margin-bottom: 0.75rem;
  box-shadow: var(--shadow-sm);
}

.history-mode.raid {
  background: var(--accent);
  color: var(--bg-primary);
  border-color: var(--accent);
}

.history-mode.solo {
  background: var(--success);
  color: var(--bg-primary);
  border-color: var(--success);
}

.history-stats {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.375rem 0.875rem;
  background: var(--bg-card);
  border-radius: 12px;
  border: 2px solid var(--border);
  font-size: 12px;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-primary);
  box-shadow: var(--shadow-inset);
}

.history-date {
  font-size: 0.85rem;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .history-item {
    flex-direction: column;
    text-align: center;
  }

  .history-icon {
    margin: 0 auto;
  }
}
</style>
