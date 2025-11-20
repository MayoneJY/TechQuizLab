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
  gap: 0.25rem;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.85rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.level-1 {
  background: linear-gradient(135deg, #a8a8a8 0%, #888888 100%);
}

.level-2,
.level-3 {
  background: linear-gradient(135deg, #4ecdc4 0%, #44a08d 100%);
}

.level-4,
.level-5 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.level-6,
.level-7 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.level-8,
.level-9,
.level-10 {
  background: linear-gradient(135deg, #ffd93d 0%, #f6c23e 100%);
  color: #333;
}

.level-text {
  font-weight: 700;
}
</style>

