<template>
  <div class="empty-state">
    <component :is="icon" v-if="icon" :size="iconSize" :color="iconColor" />
    <p class="empty-message">{{ message }}</p>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: '데이터가 없습니다.' },
  icon: { type: Object, default: null },
  iconSize: { type: [Number, String], default: 48 },
  iconColor: { type: String, default: '#6c5ce7' },
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 0;
  text-align: center;
  color: var(--text-muted);
}

.empty-message {
  margin: 0;
  line-height: 1.6;
  font-size: 16px;
  font-family: 'DungGeunMo', 'Noto Sans KR', sans-serif;
}
</style>

