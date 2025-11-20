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
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
}
</style>

