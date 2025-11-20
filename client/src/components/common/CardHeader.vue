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
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid rgba(108, 92, 231, 0.2);
}

.card-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
}

@media (max-width: 768px) {
  .card-header h2 {
    font-size: 1.25rem;
  }
}
</style>

