<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 5 L 60 35 L 95 35 L 68 55 L 78 90 L 50 70 L 22 90 L 32 55 L 5 35 L 40 35 Z"
      :fill="color"
      :stroke="color"
      stroke-width="2"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#ffd93d' },
})
</script>

