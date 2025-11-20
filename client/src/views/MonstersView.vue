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
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:hover {
  border-color: #6c5ce7;
}

.filter-select:focus {
  outline: none;
  border-color: #6c5ce7;
  box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.1);
}



.monster-card {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.monster-card:hover {
  transform: translateY(-8px) scale(1.02);
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
  background: linear-gradient(135deg, rgba(108, 92, 231, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 16px;
  animation: float 3s ease-in-out infinite;
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
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status.active {
  background: linear-gradient(135deg, #51cf66 0%, #40c057 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(81, 207, 102, 0.4);
}

.status.closed {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.monster-content {
  flex: 1;
}

.monster-name {
  font-size: 1.5rem;
  font-weight: 800;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.monster-title {
  font-size: 1rem;
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.monster-info {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.info-badge {
  padding: 0.375rem 0.75rem;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #666;
  font-weight: 500;
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
  background: #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 0.5rem;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hp-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #51cf66 0%, #40c057 100%);
  border-radius: 12px;
  transition: width 0.5s ease;
  position: relative;
  overflow: hidden;
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
  top: 1rem;
  right: 1rem;
  font-size: 1.5rem;
  color: #6c5ce7;
  font-weight: 700;
  transition: transform 0.3s ease;
}

.monster-card:hover .monster-arrow {
  transform: translateX(4px);
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
