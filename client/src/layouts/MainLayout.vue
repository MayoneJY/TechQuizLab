<template>
  <div class="main-layout">
    <!-- Persistent Background Elements -->
    <ParticleBackground />
    <StarsBackground />
    
    <!-- Global Decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
    </div>

    <!-- Page Content with Transition -->
    <div class="content-container">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import ParticleBackground from '../components/ParticleBackground.vue'
import StarsBackground from '../components/StarsBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
</script>

<style scoped>
.main-layout {
  width: 100%;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.spaceship {
  position: fixed;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.content-container {
  position: relative;
  z-index: 10;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  /* Ensure content is above background */
}
</style>
