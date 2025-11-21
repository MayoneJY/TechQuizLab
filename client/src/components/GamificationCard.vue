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
  border-radius: 16px;
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 100%;
}

/* 상단 강조선 */
.gamification-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

/* 배경 그라데이션 효과 */
.gamification-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.03) 0%, rgba(139, 92, 246, 0.03) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.card-primary {
  border-color: rgba(99, 102, 241, 0.2);
}

.card-primary::before {
  background: var(--primary);
}

.card-primary:hover {
  border-color: rgba(99, 102, 241, 0.4);
  box-shadow: var(--shadow-lg);
}

.card-primary:hover::before {
  transform: scaleX(1);
}

.card-primary:hover::after {
  opacity: 1;
}

.card-success {
  border-color: rgba(16, 185, 129, 0.2);
}

.card-success::before {
  background: var(--success);
}

.card-success:hover {
  border-color: rgba(16, 185, 129, 0.4);
  box-shadow: var(--shadow-lg);
}

.card-success:hover::before {
  transform: scaleX(1);
}

.card-success:hover::after {
  opacity: 1;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.03) 0%, rgba(5, 150, 105, 0.03) 100%);
}

.card-warning {
  border-color: rgba(245, 158, 11, 0.2);
}

.card-warning::before {
  background: var(--warning);
}

.card-warning:hover {
  border-color: rgba(245, 158, 11, 0.4);
  box-shadow: var(--shadow-lg);
}

.card-warning:hover::before {
  transform: scaleX(1);
}

.card-warning:hover::after {
  opacity: 1;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.03) 0%, rgba(217, 119, 6, 0.03) 100%);
}

.card-danger {
  border-color: rgba(239, 68, 68, 0.2);
}

.card-danger::before {
  background: var(--danger);
}

.card-danger:hover {
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: var(--shadow-lg);
}

.card-danger:hover::before {
  transform: scaleX(1);
}

.card-danger:hover::after {
  opacity: 1;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.03) 0%, rgba(220, 38, 38, 0.03) 100%);
}

.card-hover:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
  background: var(--bg-card-hover);
  border-color: var(--primary);
}

.card-hover:hover::before {
  transform: scaleX(1);
}

.card-hover:active {
  transform: translateY(-3px);
  box-shadow: var(--shadow-lg);
}

@media (max-width: 768px) {
  .gamification-card {
    padding: 1rem;
    border-radius: 16px;
  }
}
</style>

