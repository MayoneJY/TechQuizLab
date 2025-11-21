<template>
  <div class="page-container animate-fade-in">
    <PageHeader title="대시보드" :icon="IconChart" />

    <!-- 통계 요약 -->
    <div class="stats-summary">
      <div class="stat-box">
        <div class="stat-icon">
          <IconTrophy :size="32" :color="primary" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalBattles }}</div>
          <div class="stat-label">총 전투 횟수</div>
        </div>
      </div>
      <div class="stat-box">
        <div class="stat-icon">
          <IconGem :size="32" :color="warning" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalXp }}</div>
          <div class="stat-label">총 획득 XP</div>
        </div>
      </div>
      <div class="stat-box">
        <div class="stat-icon">
          <IconStar :size="32" :color="success" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ topLevel }}</div>
          <div class="stat-label">최고 레벨</div>
        </div>
      </div>
      <div class="stat-box">
        <div class="stat-icon">
          <IconUsers :size="32" :color="secondary" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ raidCount }}</div>
          <div class="stat-label">레이드 참여</div>
        </div>
      </div>
    </div>

    <div class="grid grid-auto-fill">
      <!-- 빠른 액션 -->
      <GamificationCard variant="primary" class="animate-slide-in">
        <CardHeader title="빠른 액션" :icon="IconZap" icon-color="#f59e0b" />
        <div class="quick-actions">
          <router-link to="/monsters" class="action-button">
            <IconSword :size="24" :color="primary" />
            <div>
              <div class="action-title">개인전 시작</div>
              <div class="action-desc">혼자 연습하기</div>
            </div>
            <div class="action-arrow">→</div>
          </router-link>
          <router-link to="/raids" class="action-button">
            <IconRaid :size="24" :color="secondary" />
            <div>
              <div class="action-title">레이드 참가</div>
              <div class="action-desc">함께 전투하기</div>
            </div>
            <div class="action-arrow">→</div>
          </router-link>
          <router-link to="/board" class="action-button">
            <IconStar :size="24" :color="warning" />
            <div>
              <div class="action-title">커뮤니티</div>
              <div class="action-desc">정보 공유하기</div>
            </div>
            <div class="action-arrow">→</div>
          </router-link>
        </div>
      </GamificationCard>

      <!-- 오늘의 추천 몬스터 -->
      <GamificationCard variant="warning" class="animate-slide-in">
        <CardHeader title="오늘의 추천 몬스터" :icon="IconFire" icon-color="#f59e0b" />
        <EmptyState
          v-if="recommendedMonsters.length === 0"
          message="추천 몬스터가 없습니다."
          :icon="IconMonster"
        />
        <div v-else class="monster-list">
          <div
            v-for="monster in recommendedMonsters.slice(0, 3)"
            :key="monster.id"
            class="monster-item"
            @click="$router.push(`/monsters/${monster.id}`)"
          >
            <div class="monster-icon">
              <IconMonster :size="40" :color="primary" />
            </div>
            <div class="monster-content">
              <div class="monster-name">{{ monster.name }}</div>
              <div class="monster-title">{{ monster.title }}</div>
              <div class="monster-hp">
                <IconHeart :size="14" color="#ef4444" />
                HP: {{ monster.currentHp || monster.hpBase }}/{{ monster.hpMax }}
              </div>
            </div>
            <div class="monster-arrow">→</div>
          </div>
        </div>
      </GamificationCard>

      <!-- 최근 활동 -->
      <GamificationCard variant="success" class="animate-slide-in">
        <CardHeader title="최근 활동" :icon="IconChart" icon-color="#10b981" />
        <LoadingSpinner v-if="historyLoading" />
        <EmptyState
          v-else-if="history.length === 0"
          message="아직 활동 기록이 없습니다."
          :icon="IconTrophy"
        />
        <div v-else class="activity-list">
          <div
            v-for="item in history.slice(0, 3)"
            :key="item.id"
            class="activity-item"
          >
            <div class="activity-icon">
              <IconRaid
                v-if="item.mode === 'RAID'"
                :size="24"
                :color="secondary"
              />
              <IconSword
                v-else
                :size="24"
                :color="primary"
              />
            </div>
            <div class="activity-content">
              <div class="activity-title">{{ item.monsterName || '알 수 없음' }}</div>
              <div class="activity-meta">
                <span class="activity-mode" :class="item.mode.toLowerCase()">
                  {{ item.mode === 'RAID' ? '레이드' : '개인전' }}
                </span>
                <span class="activity-date">{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>
            <div class="activity-xp">
              <IconGem :size="16" :color="warning" />
              {{ item.xpTotal || 0 }}
            </div>
          </div>
          <router-link to="/profile" class="view-all-link">
            전체 활동 보기 →
          </router-link>
        </div>
      </GamificationCard>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStatsStore } from '@/stores/stats'
import { useMonsterStore } from '@/stores/monster'
import { computed } from 'vue'
import IconChart from '@/components/icons/IconChart.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconFire from '@/components/icons/IconFire.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconRaid from '@/components/icons/IconRaid.vue'
import IconZap from '@/components/icons/IconZap.vue'
import IconGem from '@/components/icons/IconGem.vue'
import GamificationCard from '@/components/GamificationCard.vue'
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

const primary = '#6366f1'
const secondary = '#8b5cf6'
const warning = '#f59e0b'
const success = '#10b981'

const totalBattles = computed(() => history.value.length)
const totalXp = computed(() => history.value.reduce((sum, item) => sum + (item.xpTotal || 0), 0))
const topLevel = computed(() => {
  if (stats.value.length === 0) return 0
  return Math.max(...stats.value.map(s => s.level || 1))
})
const raidCount = computed(() => history.value.filter(h => h.mode === 'RAID').length)

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

  const monstersResult = await monsterStore.fetchMonsters({ limit: 3 })
  if (monstersResult.success) {
    recommendedMonsters.value = monsterStore.monsters
  }
})
</script>

<style scoped>
/* 통계 요약 */
.stats-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

.stat-box {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  background: var(--bg-card);
  border-radius: 16px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
  transition: all 0.2s ease;
}

.stat-box:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--border-light);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* 빠른 액션 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.action-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-secondary);
  border-radius: 12px;
  border: 1px solid var(--border);
  text-decoration: none;
  transition: all 0.2s ease;
  cursor: pointer;
}

.action-button:hover {
  background: var(--bg-card-hover);
  border-color: var(--border-light);
  transform: translateX(4px);
}

.action-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.action-desc {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.action-arrow {
  margin-left: auto;
  font-size: 1.25rem;
  color: var(--text-muted);
  font-weight: 700;
}

/* 활동 목록 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.activity-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--bg-secondary);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: var(--bg-card-hover);
  transform: translateX(4px);
}

.activity-icon {
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.activity-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.activity-mode {
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 500;
}

.activity-mode.raid {
  background: rgba(139, 92, 246, 0.1);
  color: var(--secondary);
}

.activity-mode.solo {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
}

.activity-date {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.activity-xp {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.view-all-link {
  display: block;
  text-align: center;
  padding: var(--spacing-md);
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  font-size: 0.875rem;
  margin-top: var(--spacing-sm);
  transition: all 0.2s ease;
}

.view-all-link:hover {
  color: var(--primary-dark);
  transform: translateX(4px);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.history-item {
  display: flex;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
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
  transform: translateY(-2px);
  border-color: var(--border-light);
  background: var(--bg-card-hover);
  box-shadow: var(--shadow-md);
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
  gap: var(--spacing-xs);
  padding: 0.375rem 0.75rem;
  background: var(--bg-secondary);
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-secondary);
  border: 1px solid var(--border);
  transition: all 0.2s ease;
}

.stat-badge:hover {
  background: var(--bg-card);
  border-color: var(--border-light);
}

.monster-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.monster-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  padding: var(--spacing-lg);
  background: var(--bg-card);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
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
  transform: translateY(-2px);
  border-color: var(--border-light);
  background: var(--bg-card-hover);
  box-shadow: var(--shadow-md);
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
