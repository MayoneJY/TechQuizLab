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
  border-radius: 12px;
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 2px solid var(--border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 100%;
}

.gamification-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 107, 157, 0.1), transparent);
  transition: left 0.5s ease;
}

.gamification-card:hover::before {
  left: 100%;
}


.card-primary {
  border-color: rgba(255, 107, 157, 0.3);
}

.card-primary:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-lg);
}

.card-success {
  border-color: rgba(149, 225, 211, 0.3);
}

.card-success:hover {
  border-color: var(--success);
  box-shadow: var(--shadow-lg);
}

.card-warning {
  border-color: rgba(254, 202, 87, 0.3);
}

.card-warning:hover {
  border-color: var(--warning);
  box-shadow: var(--shadow-lg);
}

.card-danger {
  border-color: rgba(255, 107, 107, 0.3);
}

.card-danger:hover {
  border-color: var(--danger);
  box-shadow: var(--shadow-lg);
}

.card-hover:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: var(--shadow-xl);
  background: var(--bg-card-hover);
  border-color: var(--primary);
}

.card-hover:active {
  transform: translateY(-2px) scale(1.01);
}

@media (max-width: 768px) {
  .gamification-card {
    padding: 1rem;
    border-radius: 16px;
  }
}
</style>

