<template>
  <div class="gamification-card" :class="[`card-${variant}`, { 'card-hover': hover }]">
    <slot />
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default' },
  hover: { type: Boolean, default: true },
})
</script>

<style scoped>
.gamification-card {
  background: #fff;
  border-radius: 20px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 100%;
}

.gamification-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.gamification-card.card-hover:hover::before {
  transform: scaleX(1);
}

.card-primary {
  border-color: rgba(102, 126, 234, 0.2);
}

.card-primary::before {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.card-success {
  border-color: rgba(78, 205, 196, 0.2);
}

.card-success::before {
  background: linear-gradient(90deg, #4ecdc4 0%, #44a08d 100%);
}

.card-warning {
  border-color: rgba(255, 217, 61, 0.2);
}

.card-warning::before {
  background: linear-gradient(90deg, #ffd93d 0%, #f6c23e 100%);
}

.card-danger {
  border-color: rgba(255, 107, 107, 0.2);
}

.card-danger::before {
  background: linear-gradient(90deg, #ff6b6b 0%, #ee5a6f 100%);
}

.card-hover:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

@media (max-width: 768px) {
  .gamification-card {
    padding: 1rem;
    border-radius: 16px;
  }
}
</style>

