<template>
  <div class="pixel-heart" :class="{ broken: broken }">
    <svg 
      v-if="!broken"
      width="32" 
      height="32" 
      viewBox="0 0 32 32" 
      class="heart-svg"
    >
      <rect x="8" y="12" width="4" height="4" fill="#ff6b9d"/>
      <rect x="12" y="8" width="4" height="4" fill="#ff6b9d"/>
      <rect x="16" y="8" width="4" height="4" fill="#ff6b9d"/>
      <rect x="20" y="12" width="4" height="4" fill="#ff6b9d"/>
      <rect x="12" y="12" width="8" height="4" fill="#ff6b9d"/>
      <rect x="10" y="16" width="2" height="4" fill="#ff6b9d"/>
      <rect x="20" y="16" width="2" height="4" fill="#ff6b9d"/>
      <rect x="12" y="20" width="8" height="4" fill="#ff6b9d"/>
      <rect x="14" y="24" width="4" height="4" fill="#ff6b9d"/>
      <!-- Outline -->
      <rect x="7" y="11" width="6" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="11" y="7" width="10" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="9" y="15" width="4" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="19" y="15" width="4" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="11" y="19" width="10" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="13" y="23" width="6" height="6" fill="none" stroke="#000" stroke-width="1"/>
    </svg>
    <svg 
      v-else
      width="32" 
      height="32" 
      viewBox="0 0 32 32" 
      class="heart-svg broken"
    >
      <!-- Broken heart pieces -->
      <rect x="8" y="12" width="4" height="4" fill="#ff6b9d"/>
      <rect x="12" y="8" width="4" height="4" fill="#ff6b9d"/>
      <rect x="16" y="8" width="4" height="4" fill="#ff6b9d"/>
      <rect x="20" y="12" width="4" height="4" fill="#ff6b9d"/>
      <rect x="12" y="12" width="4" height="4" fill="#ff6b9d"/>
      <rect x="16" y="12" width="4" height="4" fill="#ff6b9d"/>
      <!-- Crack line -->
      <line x1="16" y1="8" x2="16" y2="28" stroke="#000" stroke-width="2"/>
      <!-- Outline -->
      <rect x="7" y="11" width="6" height="6" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="11" y="7" width="10" height="6" fill="none" stroke="#000" stroke-width="1"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
interface Props {
  broken?: boolean
}

withDefaults(defineProps<Props>(), {
  broken: false
})
</script>

<style scoped>
.pixel-heart {
  display: inline-block;
  filter: drop-shadow(2px 2px 0 #000);
  image-rendering: pixelated;
}

.heart-svg {
  display: block;
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
}

.pixel-heart.broken {
  opacity: 0.6;
  transform: rotate(-15deg);
}
</style>

