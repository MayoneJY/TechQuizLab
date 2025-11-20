<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect x="10" y="60" width="15" height="30" :fill="color" />
    <rect x="30" y="40" width="15" height="50" :fill="color" />
    <rect x="50" y="30" width="15" height="60" :fill="color" />
    <rect x="70" y="50" width="15" height="40" :fill="color" />
    <line
      x1="5"
      y1="90"
      x2="95"
      y2="90"
      stroke="#333"
      stroke-width="2"
    />
    <line
      x1="5"
      y1="90"
      x2="5"
      y2="10"
      stroke="#333"
      stroke-width="2"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#4ecdc4' },
})
</script>

