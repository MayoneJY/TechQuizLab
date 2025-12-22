<template>
  <Transition name="slide-down">
    <div v-if="toastStore.isVisible" class="global-toast" :class="toastStore.type" @click="toastStore.hideToast">
      <div class="toast-icon">
        <img v-if="toastStore.type === 'success'" src="/assets/icons/icon-pixel-check.png" alt="Success" />
        <img v-else-if="toastStore.type === 'error'" src="/assets/icons/icon-pixel-alert.png" alt="Error" />
        <img v-else-if="toastStore.type === 'warning'" src="/assets/icons/icon-pixel-alert.png" alt="Warning" />
        <span v-else>ℹ️</span>
      </div>
      <div class="toast-message pixel-text">{{ toastStore.message }}</div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { useToastStore } from '../stores/toast'

const toastStore = useToastStore()
</script>

<style scoped>
.global-toast {
  position: fixed;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  display: flex;
  align-items: center;
  gap: 12px; /* Decreased gap */
  padding: 10px 24px; /* Thinner padding */
  border-radius: 8px; /* Slightly tighter radius */
  background: rgba(20, 20, 20, 0.85);
  color: #fff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  min-width: 300px;
  max-width: 90%;
  cursor: pointer;
  border: 2px solid #444;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  font-family: 'DungGeunMo', sans-serif;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.global-toast:hover {
  transform: translateX(-50%) translateY(2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.6);
}

.global-toast.success {
  border-color: #51cf66;
  box-shadow: 0 4px 20px rgba(81, 207, 102, 0.25);
  background: linear-gradient(145deg, rgba(81, 207, 102, 0.1), rgba(0, 0, 0, 0.9));
}

.global-toast.error {
  border-color: #ff6b6b;
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.25);
  background: linear-gradient(145deg, rgba(255, 107, 107, 0.1), rgba(0, 0, 0, 0.9));
}

.global-toast.warning {
  border-color: #ffd43b;
  box-shadow: 0 4px 20px rgba(255, 212, 59, 0.25);
  background: linear-gradient(145deg, rgba(255, 212, 59, 0.1), rgba(0, 0, 0, 0.9));
}

.toast-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast-icon img {
  width: 20px; /* Specific size for pixel icons */
  height: 20px;
  object-fit: contain;
  filter: drop-shadow(0 0 5px rgba(255,255,255,0.3));
}

.toast-message {
  font-size: 14px; /* Slightly smaller for thinner look */
  font-weight: 500;
  letter-spacing: 0.5px;
  line-height: 1.4;
  text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
}

/* Transition Effects */
.slide-down-enter-active {
  animation: slide-in 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.slide-down-leave-active {
  transition: all 0.3s ease-in;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translate(-50%, -150%);
  opacity: 0;
}

@keyframes slide-in {
  0% {
    transform: translate(-50%, -150%);
    opacity: 0;
  }
  100% {
    transform: translate(-50%, 0);
    opacity: 1;
  }
}
</style>
