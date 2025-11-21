<template>
  <div class="card-header">
    <component :is="icon" v-if="icon" :size="iconSize" :color="iconColor" />
    <h2>{{ title }}</h2>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  icon: { type: Object, default: null },
  iconSize: { type: [Number, String], default: 24 },
  iconColor: { type: String, default: '#ffd93d' },
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border);
  position: relative;
}

.card-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 60px;
  height: 2px;
  background: var(--primary);
  border-radius: 2px;
}

.card-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.025em;
}

@media (max-width: 768px) {
  .card-header h2 {
    font-size: 1.25rem;
  }
}
</style>

