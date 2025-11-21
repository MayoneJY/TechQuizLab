<template>
  <div class="pixel-monster" :class="type">
    <svg 
      width="64" 
      height="64" 
      viewBox="0 0 64 64" 
      class="monster-svg"
    >
      <!-- Body -->
      <rect v-if="type === 'alien'" x="20" y="30" width="24" height="24" fill="#51cf66"/>
      <rect v-if="type === 'alien'" x="22" y="32" width="20" height="20" fill="#69db7c"/>
      
      <!-- Eyes -->
      <rect x="24" y="36" width="6" height="6" fill="#000"/>
      <rect x="34" y="36" width="6" height="6" fill="#000"/>
      <rect x="25" y="37" width="2" height="2" fill="#fff"/>
      <rect x="35" y="37" width="2" height="2" fill="#fff"/>
      
      <!-- Mouth -->
      <rect x="28" y="44" width="8" height="4" fill="#000"/>
      
      <!-- Antenna -->
      <rect v-if="type === 'alien'" x="30" y="20" width="4" height="12" fill="#51cf66"/>
      <rect v-if="type === 'alien'" x="31" y="18" width="2" height="4" fill="#ffd43b"/>
      
      <!-- Outline -->
      <rect x="19" y="29" width="26" height="26" fill="none" stroke="#000" stroke-width="2"/>
      <rect x="23" y="35" width="8" height="8" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="33" y="35" width="8" height="8" fill="none" stroke="#000" stroke-width="1"/>
      <rect x="27" y="43" width="10" height="6" fill="none" stroke="#000" stroke-width="1"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
interface Props {
  type?: 'alien' | 'ghost' | 'robot'
}

withDefaults(defineProps<Props>(), {
  type: 'alien'
})
</script>

<style scoped>
.pixel-monster {
  display: inline-block;
  filter: drop-shadow(3px 3px 0 #000);
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
  animation: float 2s ease-in-out infinite;
}

.monster-svg {
  display: block;
  image-rendering: pixelated;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}
</style>

