<template>
  <div class="page-container">
    <div class="page-header-with-filters">
      <PageHeader title="몬스터 목록" :icon="IconMonster" />
      <div class="filters">
        <select v-model="filters.jobType" @change="fetchMonsters" class="filter-select">
          <option value="">전체 직무</option>
          <option value="BACKEND">백엔드</option>
          <option value="FRONTEND">프론트엔드</option>
          <option value="MOBILE">모바일</option>
        </select>
        <select v-model="filters.status" @change="fetchMonsters" class="filter-select">
          <option value="">전체 상태</option>
          <option value="ACTIVE">진행중</option>
          <option value="CLOSED">마감</option>
        </select>
      </div>
    </div>

    <LoadingSpinner v-if="monsterStore.loading" />
    <EmptyState
      v-else-if="monsterStore.monsters.length === 0"
      message="몬스터가 없습니다."
      :icon="IconMonster"
    />
    <div v-else class="grid grid-auto-fill">
      <GamificationCard
        v-for="monster in monsterStore.monsters"
        :key="monster.id"
        variant="primary"
        class="monster-card"
        @click="$router.push(`/monsters/${monster.id}`)"
      >
        <div class="monster-card-header">
          <div class="monster-icon-wrapper">
            <IconMonster :size="48" color="#6c5ce7" />
          </div>
          <span class="status" :class="monster.active ? 'active' : 'closed'">
            {{ monster.active ? '진행중' : '마감' }}
          </span>
        </div>
        <div class="monster-content">
          <h3 class="monster-name">{{ monster.name }}</h3>
          <div class="monster-title">{{ monster.title }}</div>
          <div class="monster-info">
            <span v-if="monster.locationName" class="info-badge">
              📍 {{ monster.locationName }}
            </span>
            <span v-if="monster.jobTypeName" class="info-badge">
              💼 {{ monster.jobTypeName }}
            </span>
          </div>
          <HPBar
            :current="monster.currentHp || monster.hpBase"
            :max="monster.hpMax"
            label="HP"
          />
        </div>
        <div class="monster-arrow">→</div>
      </GamificationCard>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useMonsterStore } from '@/stores/monster'
import IconMonster from '@/components/icons/IconMonster.vue'
import GamificationCard from '@/components/GamificationCard.vue'
import HPBar from '@/components/HPBar.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const monsterStore = useMonsterStore()

const filters = ref({
  jobType: '',
  status: '',
})

const fetchMonsters = async () => {
  await monsterStore.fetchMonsters(filters.value)
}

onMounted(() => {
  fetchMonsters()
})
</script>

<style scoped>
.page-header-with-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.filters {
  display: flex;
  gap: 1rem;
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



.monster-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.monster-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
}

.monster-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.monster-icon-wrapper {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.monster-card:hover .monster-icon-wrapper {
  transform: scale(1.1) rotate(5deg);
  box-shadow: var(--shadow-md);
  border-color: var(--primary);
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.status {
  padding: 0.375rem 0.875rem;
  border-radius: 12px;
  border: 2px solid;
  font-size: 12px;
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  box-shadow: var(--shadow-sm);
}

.status.active {
  background: var(--success);
  color: var(--bg-primary);
  border-color: var(--success-dark);
}

.status.closed {
  background: var(--danger);
  color: var(--text-primary);
  border-color: var(--danger-dark);
}

.monster-content {
  flex: 1;
}

.monster-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
  letter-spacing: -0.025em;
  transition: color 0.3s ease;
}

.monster-card:hover .monster-name {
  color: var(--primary);
}

.monster-title {
  font-size: 0.875rem;
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
  color: var(--text-secondary);
  margin-bottom: 1rem;
  line-height: 1.6;
}

.monster-info {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.info-badge {
  padding: 0.375rem 0.75rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.info-badge:hover {
  background: var(--bg-card);
  border-color: var(--border-light);
}

.monster-hp-section {
  margin-top: 1rem;
}

.hp-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.hp-bar-container {
  height: 24px;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 0.5rem;
  box-shadow: var(--shadow-inset);
}

.hp-bar-fill {
  height: 100%;
  background: var(--gradient-success);
  border-radius: 12px;
  transition: width 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.hp-bar-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.hp-text {
  font-size: 0.85rem;
  color: #666;
  font-weight: 600;
  text-align: right;
}

.monster-arrow {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  font-size: 1.5rem;
  color: var(--text-muted);
  font-weight: 700;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.monster-card:hover .monster-arrow {
  transform: translateX(6px);
  color: var(--primary);
}

@media (max-width: 768px) {
  .page-header-with-filters {
    flex-direction: column;
    align-items: flex-start;
  }

  .filters {
    width: 100%;
  }

  .filter-select {
    flex: 1;
    min-width: 150px;
  }

  .monster-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .monster-card {
    padding: 1.25rem;
  }

  .monster-name {
    font-size: 1.25rem;
  }
}
</style>
