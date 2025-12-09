<template>
  <div class="pixel-planet" :class="type">
    <svg 
      width="100" 
      height="100" 
      viewBox="0 0 100 100" 
      class="planet-svg"
    >
      <!-- Base Circle (Ocean) -->
      <circle cx="50" cy="50" r="40" fill="#4a9eff" />
      
      <!-- Land Masses (Green) - Pixelated approximation -->
      <rect x="30" y="30" width="10" height="10" fill="#51cf66" />
      <rect x="40" y="25" width="15" height="10" fill="#51cf66" />
      <rect x="35" y="40" width="20" height="15" fill="#51cf66" />
      <rect x="60" y="50" width="15" height="20" fill="#51cf66" />
      <rect x="25" y="55" width="10" height="15" fill="#51cf66" />
      <rect x="65" y="30" width="10" height="10" fill="#51cf66" />
      
      <!-- Clouds (White) -->
      <rect x="20" y="45" width="15" height="5" fill="#ffffff" opacity="0.8" />
      <rect x="60" y="20" width="15" height="5" fill="#ffffff" opacity="0.8" />
      <rect x="50" y="70" width="20" height="5" fill="#ffffff" opacity="0.8" />
      
      <!-- Atmosphere Glow -->
      <circle cx="50" cy="50" r="42" fill="none" stroke="rgba(74, 158, 255, 0.3)" stroke-width="4" />
      
      <!-- Pixel Grid Overlay for extra retro feel (Optional) -->
      <!-- <pattern id="pixelGrid" width="2" height="2" patternUnits="userSpaceOnUse">
        <rect width="2" height="2" fill="rgba(0,0,0,0.1)"/>
      </pattern>
      <circle cx="50" cy="50" r="40" fill="url(#pixelGrid)" /> -->
    </svg>
  </div>
</template>

<script setup lang="ts">
interface Props {
  type?: 'earth' | 'mars' | 'moon'
}

withDefaults(defineProps<Props>(), {
  type: 'earth'
})
</script>

<style scoped>
.pixel-planet {
  display: inline-block;
  image-rendering: pixelated;
  filter: drop-shadow(0 0 20px rgba(74, 158, 255, 0.4));
  animation: float 8s ease-in-out infinite;
}

.planet-svg {
  display: block;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(2deg); }
}
</style>
