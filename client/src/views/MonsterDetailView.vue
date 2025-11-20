<template>
  <div class="page-container">
    <LoadingSpinner v-if="monsterStore.loading" />
    <EmptyState
      v-else-if="!monsterStore.currentMonster"
      message="몬스터를 찾을 수 없습니다."
      :icon="IconMonster"
    />
    <div v-else class="monster-content animate-fade-in" style="max-width: 1200px; margin: 0 auto;">
      <GamificationCard variant="primary" class="monster-header">
        <div class="monster-icon-large">
          <IconMonster :size="80" color="#fff" />
        </div>
        <div class="monster-header-content">
          <h1>{{ monsterStore.currentMonster.name }}</h1>
          <span class="status" :class="monsterStore.currentMonster.active ? 'active' : 'closed'">
            {{ monsterStore.currentMonster.active ? '진행중' : '마감' }}
          </span>
        </div>
      </GamificationCard>

      <GamificationCard class="monster-info">
        <h2>{{ monsterStore.currentMonster.title }}</h2>
        <div class="info-row">
          <div v-if="monsterStore.currentMonster.locationName" class="info-item">
            <span class="info-label">📍 지역</span>
            <span class="info-value">{{ monsterStore.currentMonster.locationName }}</span>
          </div>
          <div v-if="monsterStore.currentMonster.jobTypeName" class="info-item">
            <span class="info-label">💼 직무</span>
            <span class="info-value">{{ monsterStore.currentMonster.jobTypeName }}</span>
          </div>
          <div v-if="monsterStore.currentMonster.experienceLevelName" class="info-item">
            <span class="info-label">⭐ 경력</span>
            <span class="info-value">{{ monsterStore.currentMonster.experienceLevelName }}</span>
          </div>
        </div>
        <HPBar
          :current="monsterStore.currentMonster.currentHp || monsterStore.currentMonster.hpBase"
          :max="monsterStore.currentMonster.hpMax"
          label="몬스터 HP"
        />
      </GamificationCard>

      <div class="actions">
        <button @click="startSolo" class="btn btn-primary btn-action">
          <IconSword :size="24" />
          <span>개인전 시작</span>
        </button>
        <button @click="createRaid" class="btn btn-secondary btn-action">
          <IconRaid :size="24" />
          <span>레이드 생성</span>
        </button>
        <button @click="joinRaid" class="btn btn-secondary btn-action">
          <IconUsers :size="24" />
          <span>레이드 참가</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMonsterStore } from '@/stores/monster'
import { raidApi } from '@/api/raid'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconRaid from '@/components/icons/IconRaid.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import GamificationCard from '@/components/GamificationCard.vue'
import HPBar from '@/components/HPBar.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const monsterStore = useMonsterStore()

onMounted(async () => {
  await monsterStore.fetchMonster(route.params.id)
})

const startSolo = () => {
  router.push(`/solo/${route.params.id}`)
}

const createRaid = async () => {
  try {
    const response = await raidApi.createRaid({ monsterId: route.params.id })
    router.push(`/raids/${response.data.id}`)
  } catch (error) {
    alert('레이드 생성에 실패했습니다.')
  }
}

const joinRaid = async () => {
  alert('레이드 참가 기능은 준비 중입니다.')
}
</script>

<style scoped>
.monster-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.monster-header {
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 2rem;
  margin-bottom: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.monster-header::before {
  display: none;
}

.monster-icon-large {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  border: 3px solid rgba(255, 255, 255, 0.2);
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

.monster-header-content {
  flex: 1;
}

.monster-header-content h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: white;
  margin: 0 0 1rem 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.status {
  padding: 0.5rem 1.25rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: inline-block;
}

.status.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.status.closed {
  background: rgba(0, 0, 0, 0.2);
  color: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.monster-info {
  padding: 2rem;
  margin-bottom: 2rem;
}

.monster-info h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 1.5rem;
}

.info-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
}

.info-label {
  font-size: 0.85rem;
  color: #999;
  font-weight: 500;
}

.info-value {
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
}

.hp-section {
  margin-top: 2rem;
}

.hp-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.hp-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #333;
}

.hp-bar-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-weight: 800;
  font-size: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  z-index: 1;
  color: white;
}

.actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.btn-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1.25rem 2rem;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .monster-header {
    flex-direction: column;
    text-align: center;
    gap: 1.5rem;
    padding: 1.5rem;
  }

  .monster-header-content h1 {
    font-size: 2rem;
  }

  .monster-icon-large {
    width: 100px;
    height: 100px;
  }

  .monster-info {
    padding: 1.5rem;
  }

  .info-row {
    grid-template-columns: 1fr;
  }

  .actions {
    grid-template-columns: 1fr;
  }

  .btn-action {
    padding: 1rem 1.5rem;
    font-size: 1rem;
  }
}
</style>
