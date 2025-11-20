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
  background: var(--bg-card);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 100%;
  backdrop-filter: blur(10px);
}

.gamification-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  background: var(--gradient-primary);
  transform: scaleX(0);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 24px 24px 0 0;
}

.gamification-card.card-hover:hover::before {
  transform: scaleX(1);
}

.card-primary {
  border-color: rgba(99, 102, 241, 0.15);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.card-primary::before {
  background: var(--gradient-primary);
}

.card-success {
  border-color: rgba(16, 185, 129, 0.15);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(240, 253, 250, 0.98) 100%);
}

.card-success::before {
  background: var(--gradient-success);
}

.card-warning {
  border-color: rgba(245, 158, 11, 0.15);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 251, 235, 0.98) 100%);
}

.card-warning::before {
  background: var(--gradient-warning);
}

.card-danger {
  border-color: rgba(239, 68, 68, 0.15);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(254, 242, 242, 0.98) 100%);
}

.card-danger::before {
  background: var(--gradient-danger);
}

.card-hover:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: var(--shadow-xl);
  border-color: var(--border);
}

@media (max-width: 768px) {
  .gamification-card {
    padding: 1rem;
    border-radius: 16px;
  }
}
</style>

