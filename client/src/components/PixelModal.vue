<template>
  <Transition name="modal-fade">
    <div v-if="modalStore.isVisible" class="modal-overlay">
      <div class="glass-panel modal-card">
        <!-- Header -->
        <div class="modal-header">
          <span class="pixel-text modal-title">{{ modalTitle }}</span>
          <div class="header-decoration">
            <div class="dot red"></div>
            <div class="dot yellow"></div>
            <div class="dot green"></div>
          </div>
        </div>

        <!-- Content -->
        <div class="modal-content">
          <div class="message-icon">
            <img 
              v-if="modalStore.type === 'confirm'" 
              src="/assets/icons/icon-pixel-question.png" 
              alt="Confirm" 
              class="pixel-icon"
            />
            <img 
              v-else 
              src="/assets/icons/icon-pixel-alert.png" 
              alt="Alert" 
              class="pixel-icon"
            />
          </div>
          <p class="pixel-text modal-message">{{ modalStore.message }}</p>
        </div>
        
        <!-- Footer / Buttons -->
        <div class="modal-buttons">
          <button 
            v-if="modalStore.type === 'confirm'" 
            class="pixel-button ghost-btn" 
            @click="modalStore.cancelAction"
          >
            NO
          </button>
          
          <button 
            class="pixel-button primary" 
            @click="modalStore.confirmAction"
          >
            {{ modalStore.type === 'confirm' ? 'YES' : 'OK' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useModalStore } from '../stores/modal'

const modalStore = useModalStore()

const modalTitle = computed(() => {
  return modalStore.type === 'confirm' ? 'CONFIRMATION' : 'SYSTEM NOTICE'
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-card {
  min-width: 360px;
  max-width: 90%;
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5), 0 0 20px rgba(74, 158, 255, 0.2);
  display: flex;
  flex-direction: column;
}

/* Header */
.modal-header {
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  color: #4a9eff;
  font-size: 14px;
  letter-spacing: 1px;
}

.header-decoration {
  display: flex;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  opacity: 0.7;
}

.dot.red { background: #ff6b6b; }
.dot.yellow { background: #ffd43b; }
.dot.green { background: #51cf66; }

/* Content */
.modal-content {
  padding: 30px 20px;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.message-icon {
  margin-bottom: 5px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
}

.pixel-icon {
  width: 48px;
  height: 48px;
  image-rendering: pixelated;
  object-fit: contain;
}

.modal-message {
  font-size: 16px;
  color: #fff;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
}

/* Buttons */
.modal-buttons {
  padding: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.2);
}

.pixel-button {
  min-width: 100px;
  padding: 12px 24px; 
  font-size: 14px;
}

/* Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-card {
  animation: popIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.modal-fade-leave-active .modal-card {
  animation: popOut 0.2s ease-in;
}

@keyframes popIn {
  from { transform: scale(0.9) translateY(10px); opacity: 0; }
  to { transform: scale(1) translateY(0); opacity: 1; }
}

@keyframes popOut {
  from { transform: scale(1); opacity: 1; }
  to { transform: scale(0.95); opacity: 0; }
}

/* Responsive */
@media (max-width: 480px) {
  .modal-card {
    min-width: 90%;
  }
}
</style>
