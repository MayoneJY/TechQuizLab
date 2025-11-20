<template>
  <div class="xp-bar">
    <div class="xp-bar-label">
      <span>{{ label }}</span>
      <span class="xp-value">{{ current }} / {{ max }}</span>
    </div>
    <div class="xp-bar-container">
      <div
        class="xp-bar-fill"
        :style="{ width: `${percentage}%` }"
        :class="{ 'xp-full': percentage >= 100 }"
      >
        <span v-if="percentage > 10" class="xp-text">{{ percentage }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: { type: Number, default: 0 },
  max: { type: Number, default: 100 },
  label: { type: String, default: 'XP' },
})

const percentage = computed(() => {
  if (props.max === 0) return 0
  return Math.min(100, Math.round((props.current / props.max) * 100))
})
</script>

<style scoped>
.xp-bar {
  width: 100%;
}

.xp-bar-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 14px;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  color: var(--text-primary);
}

.xp-value {
  color: var(--text-secondary);
  font-weight: 500;
}

.xp-bar-container {
  width: 100%;
  height: 24px;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  box-shadow: var(--shadow-inset);
}

.xp-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--info) 0%, var(--secondary) 100%);
  border-radius: 12px;
  transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.xp-bar-fill.xp-full {
  background: linear-gradient(90deg, var(--accent) 0%, var(--warning) 100%);
  animation: pulse 1s ease-in-out infinite;
}

.xp-text {
  color: var(--text-primary);
  font-size: 12px;
  font-weight: 600;
  font-family: 'DungGeunMo', sans-serif;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}
</style>

