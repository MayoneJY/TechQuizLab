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
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

.xp-value {
  color: #666;
  font-weight: 600;
}

.xp-bar-container {
  width: 100%;
  height: 24px;
  background: #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.xp-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #4ecdc4 0%, #44a08d 100%);
  border-radius: 12px;
  transition: width 0.5s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.xp-bar-fill.xp-full {
  background: linear-gradient(90deg, #ffd93d 0%, #f6c23e 100%);
  animation: pulse 1s ease-in-out infinite;
}

.xp-text {
  color: #fff;
  font-size: 0.75rem;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
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

