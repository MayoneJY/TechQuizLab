<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect x="20" y="60" width="15" height="30" :fill="color" />
    <rect x="40" y="40" width="15" height="50" :fill="color" />
    <rect x="60" y="30" width="15" height="60" :fill="color" />
    <rect x="80" y="50" width="15" height="40" :fill="color" />
    <line x1="15" y1="90" x2="100" y2="90" stroke="#333" stroke-width="2" />
    <line x1="15" y1="90" x2="15" y2="10" stroke="#333" stroke-width="2" />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#667eea' },
})
</script>

