<template>
  <div class="stat-card" :class="`stat-${statCode.toLowerCase()}`">
    <div class="stat-header">
      <div class="stat-icon">
        <IconGem :size="32" :color="iconColor" />
      </div>
      <div class="stat-info">
        <h3 class="stat-name">{{ statName }}</h3>
        <LevelBadge :level="level" />
      </div>
    </div>
    <XPBar :current="xp" :max="xpForNextLevel" :label="`XP`" />
    <div class="stat-progress">
      <span class="stat-xp-text">{{ xp }} XP</span>
      <span class="stat-next-level">다음 레벨까지 {{ xpForNextLevel - xp }} XP</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import IconGem from './icons/IconGem.vue'
import LevelBadge from './LevelBadge.vue'
import XPBar from './XPBar.vue'

const props = defineProps({
  statCode: { type: String, required: true },
  statName: { type: String, required: true },
  xp: { type: Number, default: 0 },
  level: { type: Number, default: 1 },
})

const xpForNextLevel = computed(() => {
  // 레벨당 XP 계산: level^2 * 100
  return Math.pow(props.level + 1, 2) * 100
})

const iconColor = computed(() => {
  const colors = {
    cs_base: '#4ecdc4',
    algo: '#95e1d3',
    os: '#f38181',
    network: '#ffd93d',
    db: '#6c5ce7',
    backend: '#ff6b6b',
    mobile: '#a29bfe',
    sys_design: '#fd79a8',
    collab: '#00b894',
    career: '#fdcb6e',
  }
  return colors[props.statCode.toLowerCase()] || '#667eea'
})
</script>

<style scoped>
.stat-card {
  background: var(--bg-card);
  padding: 1.5rem;
  border-radius: 12px;
  border: 2px solid var(--border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-md);
}

.stat-card:hover {
  transform: translateY(-4px) scale(1.02);
  border-color: var(--primary);
  box-shadow: var(--shadow-xl);
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.stat-icon {
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stat-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  letter-spacing: 0.02em;
}

.stat-progress {
  display: flex;
  justify-content: space-between;
  margin-top: 0.75rem;
  font-size: 14px;
}

.stat-xp-text {
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  font-size: 14px;
  color: var(--primary);
}

.stat-next-level {
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .stat-card {
    padding: 1rem;
  }

  .stat-header {
    gap: 0.75rem;
  }

  .stat-name {
    font-size: 1rem;
  }
}
</style>

