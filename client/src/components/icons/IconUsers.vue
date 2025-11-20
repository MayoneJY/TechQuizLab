<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <circle cx="30" cy="35" r="12" :fill="color" />
    <circle cx="70" cy="35" r="12" :fill="color" />
    <path
      d="M 20 70 Q 20 55 35 55 L 65 55 Q 80 55 80 70 L 80 85 L 20 85 Z"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#667eea' },
})
</script>
