<template>
  <div class="loading-container">
    <div class="spinner"></div>
    <p v-if="message" class="loading-message">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: '로딩 중...' },
})
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 0;
  color: var(--text-muted);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border);
  border-top-color: var(--primary);
  border-right-color: var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: var(--shadow-sm);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-message {
  margin: 0;
  font-size: 14px;
  font-family: 'DungGeunMo', sans-serif;
}
</style>

