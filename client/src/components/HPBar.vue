<template>
  <div class="hp-bar">
    <div class="hp-bar-label">
      <IconMonster :size="20" color="#ff6b6b" />
      <span>{{ label }}</span>
      <span class="hp-value">{{ current }} / {{ max }}</span>
    </div>
    <div class="hp-bar-container">
      <div
        class="hp-bar-fill"
        :style="{ width: `${percentage}%` }"
        :class="hpClass"
      >
        <span v-if="percentage > 15" class="hp-text">{{ percentage }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import IconMonster from './icons/IconMonster.vue'

const props = defineProps({
  current: { type: Number, default: 0 },
  max: { type: Number, default: 100 },
  label: { type: String, default: 'HP' },
})

const percentage = computed(() => {
  if (props.max === 0) return 0
  return Math.min(100, Math.round((props.current / props.max) * 100))
})

const hpClass = computed(() => {
  if (percentage.value > 60) return 'hp-high'
  if (percentage.value > 30) return 'hp-medium'
  return 'hp-low'
})
</script>

<style scoped>
.hp-bar {
  width: 100%;
}

.hp-bar-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 14px;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-primary);
}

.hp-value {
  margin-left: auto;
  color: var(--text-secondary);
  font-weight: 500;
}

.hp-bar-container {
  width: 100%;
  height: 32px;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  box-shadow: var(--shadow-inset);
}

.hp-bar-fill {
  height: 100%;
  border-radius: 16px;
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.hp-high {
  background: var(--gradient-success);
}

.hp-medium {
  background: var(--gradient-warning);
}

.hp-low {
  background: var(--gradient-danger);
  animation: shake 0.5s ease-in-out infinite;
}

.hp-text {
  color: var(--text-primary);
  font-size: 12px;
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-2px);
  }
  75% {
    transform: translateX(2px);
  }
}
</style>

