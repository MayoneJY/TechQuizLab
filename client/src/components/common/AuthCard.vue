<template>
  <div class="auth-page">
    <GamificationCard variant="primary" class="auth-card">
      <div class="auth-header">
        <IconMonster :size="64" color="#6366f1" />
        <h1>{{ title }}</h1>
      </div>
      <slot />
    </GamificationCard>
  </div>
</template>

<script setup>
import GamificationCard from '@/components/GamificationCard.vue'
import IconMonster from '@/components/icons/IconMonster.vue'

defineProps({
  title: { type: String, required: true },
})
</script>

<style scoped>
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 80px);
  padding: 2rem;
  width: 100%;
}

.auth-card {
  width: 100%;
  max-width: 500px;
  padding: 2.5rem;
}

.auth-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.auth-header h1 {
  font-size: 1.75rem;
  font-weight: normal;
  font-family: 'DungGeunMo', 'Black Han Sans', sans-serif;
  color: var(--text-primary);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.9);
  margin: 0;
  letter-spacing: 0.02em;
}

@media (max-width: 768px) {
  .auth-page {
    padding: 1rem;
  }

  .auth-card {
    padding: 2rem 1.5rem;
  }

  .auth-header h1 {
    font-size: 1.75rem;
  }
}
</style>

