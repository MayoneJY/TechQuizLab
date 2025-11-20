<template>
  <div class="page-header">
    <component :is="icon" v-if="icon" :size="iconSize" :color="iconColor" />
    <h1>{{ title }}</h1>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  icon: { type: Object, default: null },
  iconSize: { type: [Number, String], default: 32 },
  iconColor: { type: String, default: '#6c5ce7' },
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
  margin: 0;
  letter-spacing: 0.02em;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
}
</style>

