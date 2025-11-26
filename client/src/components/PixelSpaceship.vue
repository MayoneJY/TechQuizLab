<template>
  <div class="pixel-spaceship" :class="direction">
    <svg 
      width="80" 
      height="80" 
      viewBox="0 0 80 80" 
      class="spaceship-svg"
    >
      <!-- Main body -->
      <rect x="20" y="30" width="40" height="20" fill="#888"/>
      <rect x="25" y="25" width="30" height="30" fill="#aaa"/>
      <rect x="30" y="20" width="20" height="35" fill="#ccc"/>
      
      <!-- Cockpit -->
      <rect x="35" y="25" width="10" height="15" fill="#4a9eff"/>
      <rect x="36" y="26" width="8" height="13" fill="#6bb3ff"/>
      
      <!-- Wings -->
      <rect x="10" y="35" width="15" height="10" fill="#666"/>
      <rect x="55" y="35" width="15" height="10" fill="#666"/>
      
      <!-- Engine glow -->
      <rect x="28" y="50" width="6" height="8" fill="#ffd43b"/>
      <rect x="46" y="50" width="6" height="8" fill="#ffd43b"/>
      <rect x="30" y="52" width="2" height="4" fill="#ff6b6b"/>
      <rect x="48" y="52" width="2" height="4" fill="#ff6b6b"/>
      
      <!-- Outline -->
      <rect x="19" y="29" width="42" height="22" fill="none" stroke="#000" stroke-width="2"/>
      <rect x="24" y="24" width="32" height="32" fill="none" stroke="#000" stroke-width="2"/>
      <rect x="29" y="19" width="22" height="37" fill="none" stroke="#000" stroke-width="2"/>
      <rect x="9" y="34" width="17" height="12" fill="none" stroke="#000" stroke-width="2"/>
      <rect x="54" y="34" width="17" height="12" fill="none" stroke="#000" stroke-width="2"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
interface Props {
  direction?: 'up' | 'down' | 'left' | 'right'
}

withDefaults(defineProps<Props>(), {
  direction: 'up'
})
</script>

<style scoped>
.pixel-spaceship {
  display: inline-block;
  filter: drop-shadow(4px 4px 0 #000);
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
}

.spaceship-svg {
  display: block;
  image-rendering: pixelated;
}

.pixel-spaceship.up {
  transform: rotate(0deg);
}

.pixel-spaceship.down {
  transform: rotate(180deg);
}

.pixel-spaceship.left {
  transform: rotate(-90deg);
}

.pixel-spaceship.right {
  transform: rotate(90deg);
}
</style>

