<template>
  <div class="level-badge" :class="`level-${level}`">
    <IconStar :size="16" color="#ffd93d" />
    <span class="level-text">Lv.{{ level }}</span>
  </div>
</template>

<script setup>
import IconStar from './icons/IconStar.vue'

defineProps({
  level: { type: Number, default: 1 },
})
</script>

<style scoped>
.level-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.875rem;
  border-radius: 12px;
  border: 2px solid transparent;
  font-weight: 500;
  font-family: 'DungGeunMo', sans-serif;
  font-size: 12px;
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.level-badge:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.level-1 {
  background: linear-gradient(135deg, #888888 0%, #666666 100%);
  color: var(--text-primary);
}

.level-2,
.level-3 {
  background: var(--gradient-success);
  color: var(--bg-primary);
  font-weight: 600;
}

.level-4,
.level-5 {
  background: var(--gradient-primary);
  color: var(--text-primary);
  font-weight: 600;
}

.level-6,
.level-7 {
  background: linear-gradient(135deg, var(--accent) 0%, var(--primary) 100%);
  color: var(--bg-primary);
  font-weight: 600;
}

.level-8,
.level-9,
.level-10 {
  background: linear-gradient(135deg, var(--accent) 0%, var(--warning) 100%);
  color: var(--bg-primary);
  font-weight: 600;
  box-shadow: var(--shadow-md);
}

.level-text {
  font-weight: normal;
}
</style>

