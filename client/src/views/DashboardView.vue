<template>
  <div class="page-container animate-fade-in">
    <PageHeader title="대시보드" :icon="IconChart" />

    <div class="grid grid-auto-fill">
      <GamificationCard variant="primary" class="animate-slide-in">
        <CardHeader title="능력치 레벨" :icon="IconStar" icon-color="#ffd93d" />
        <LoadingSpinner v-if="statsLoading" />
        <EmptyState
          v-else-if="stats.length === 0"
          message="아직 능력치가 없습니다. 몬스터와 전투를 시작해보세요!"
          :icon="IconMonster"
        />
        <div v-else class="stats-list">
          <StatCard
            v-for="stat in stats"
            :key="stat.statCode"
            :stat-code="stat.statCode"
            :stat-name="stat.statName || stat.statCode"
            :xp="stat.xp || 0"
            :level="stat.level || 1"
          />
        </div>
      </GamificationCard>

      <GamificationCard variant="success" class="animate-slide-in">
        <CardHeader title="최근 레이드 기록" :icon="IconTrophy" icon-color="#ffd93d" />
        <LoadingSpinner v-if="historyLoading" />
        <EmptyState
          v-else-if="history.length === 0"
          message="아직 레이드 기록이 없습니다."
          :icon="IconUsers"
        />
        <div v-else class="history-list">
          <div
            v-for="item in history.slice(0, 5)"
            :key="item.id"
            class="history-item"
          >
            <div class="history-icon">
              <IconRaid
                v-if="item.mode === 'RAID'"
                :size="32"
                color="#6c5ce7"
              />
              <IconSword
                v-else
                :size="32"
                color="#4ecdc4"
              />
            </div>
            <div class="history-content">
              <div class="history-title">{{ item.monsterName || '알 수 없음' }}</div>
              <div class="history-meta">
                <span class="history-mode" :class="item.mode.toLowerCase()">
                  {{ item.mode === 'RAID' ? '레이드' : '개인전' }}
                </span>
                <span class="history-date">{{ formatDate(item.createdAt) }}</span>
              </div>
              <div class="history-stats">
                <span class="stat-badge">
                  <IconStar :size="14" color="#ffd93d" />
                  {{ item.xpTotal || 0 }} XP
                </span>
                <span class="stat-badge">
                  <IconSword :size="14" color="#ff6b6b" />
                  {{ item.damage || 0 }} 데미지
                </span>
              </div>
            </div>
          </div>
        </div>
      </GamificationCard>

      <GamificationCard variant="warning" class="animate-slide-in">
        <CardHeader title="오늘의 추천 몬스터" :icon="IconFire" icon-color="#ff6b6b" />
        <EmptyState
          v-if="recommendedMonsters.length === 0"
          message="추천 몬스터가 없습니다."
          :icon="IconMonster"
        />
        <div v-else class="monster-list">
          <div
            v-for="monster in recommendedMonsters.slice(0, 5)"
            :key="monster.id"
            class="monster-item"
            @click="$router.push(`/monsters/${monster.id}`)"
          >
            <div class="monster-icon">
              <IconMonster :size="40" color="#6c5ce7" />
            </div>
            <div class="monster-content">
              <div class="monster-name">{{ monster.name }}</div>
              <div class="monster-title">{{ monster.title }}</div>
              <div class="monster-hp">
                <IconHeart :size="14" color="#ff6b6b" />
                HP: {{ monster.currentHp || monster.hpBase }}/{{ monster.hpMax }}
              </div>
            </div>
            <div class="monster-arrow">→</div>
          </div>
        </div>
      </GamificationCard>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStatsStore } from '@/stores/stats'
import { useMonsterStore } from '@/stores/monster'
import IconChart from '@/components/icons/IconChart.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconFire from '@/components/icons/IconFire.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconRaid from '@/components/icons/IconRaid.vue'
import GamificationCard from '@/components/GamificationCard.vue'
import StatCard from '@/components/StatCard.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import CardHeader from '@/components/common/CardHeader.vue'

const statsStore = useStatsStore()
const monsterStore = useMonsterStore()

const stats = ref([])
const history = ref([])
const statsLoading = ref(false)
const historyLoading = ref(false)
const recommendedMonsters = ref([])

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR')
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

  const monstersResult = await monsterStore.fetchMonsters({ limit: 5 })
  if (monstersResult.success) {
    recommendedMonsters.value = monsterStore.monsters
  }
})
</script>

<style scoped>



.history-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.history-item {
  display: flex;
  gap: 1rem;
  padding: 1.25rem;
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid var(--border);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.history-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s ease;
}

.history-item:hover {
  transform: translateY(-4px) scale(1.02);
  border-color: var(--primary);
  background: var(--bg-card-hover);
  box-shadow: var(--shadow-xl);
}

.history-item:hover::before {
  left: 100%;
}

.history-icon {
  flex-shrink: 0;
}

.history-content {
  flex: 1;
  min-width: 0;
}

.history-title {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  font-size: 1.05rem;
}

.history-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
}

.history-mode {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.history-mode.raid {
  background: var(--primary);
  color: var(--bg-primary);
  font-weight: 600;
}

.history-mode.solo {
  background: var(--success);
  color: var(--bg-primary);
  font-weight: 600;
}

.history-date {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.history-stats {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.375rem 0.875rem;
  background: var(--bg-card);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-secondary);
  box-shadow: var(--shadow-inset);
  border: 2px solid var(--border);
  transition: all 0.3s ease;
}

.stat-badge:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.monster-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.monster-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid var(--border);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}

.monster-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 230, 109, 0.2), transparent);
  transition: left 0.5s ease;
}

.monster-item:hover {
  transform: translateX(8px) translateY(-4px) scale(1.02);
  border-color: var(--warning);
  background: var(--bg-card-hover);
  box-shadow: var(--shadow-xl);
}

.monster-item:hover::before {
  left: 100%;
}

.monster-icon {
  flex-shrink: 0;
}

.monster-content {
  flex: 1;
  min-width: 0;
}

.monster-name {
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
  font-size: 1.1rem;
}

.monster-title {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.monster-hp {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.monster-arrow {
  font-size: 1.5rem;
  color: var(--primary-light);
  font-weight: 700;
}

@media (max-width: 768px) {
  .card-header h2 {
    font-size: 1.25rem;
  }

  .history-item,
  .monster-item {
    flex-direction: column;
    text-align: center;
  }

  .monster-arrow {
    transform: rotate(90deg);
  }
}
</style>
