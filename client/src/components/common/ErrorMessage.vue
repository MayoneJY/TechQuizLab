<template>
  <div v-if="message" class="error-message">
    <IconFire :size="20" color="#ef4444" />
    <span>{{ message }}</span>
  </div>
</template>

<script setup>
import IconFire from '@/components/icons/IconFire.vue'

defineProps({
  message: { type: String, default: '' },
})
</script>

<style scoped>
.error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #ef4444;
  margin-bottom: 1rem;
  padding: 0.75rem 1rem;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  font-size: 0.9rem;
}
</style>

