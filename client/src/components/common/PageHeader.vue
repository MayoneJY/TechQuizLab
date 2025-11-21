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
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-2xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border);
  position: relative;
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary) 0%, var(--secondary) 100%);
  border-radius: 2px;
}

.page-header h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.05em;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
}
</style>

