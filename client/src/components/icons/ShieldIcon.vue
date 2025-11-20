<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 10 L 20 20 L 20 50 Q 20 70 50 85 Q 80 70 80 50 L 80 20 Z"
      :fill="color"
      :stroke="color"
      stroke-width="2"
    />
    <path
      d="M 50 10 L 20 20 L 20 50 Q 20 70 50 85 Q 80 70 80 50 L 80 20 Z"
      fill="none"
      stroke="#fff"
      stroke-width="3"
    />
    <circle cx="50" cy="50" r="15" fill="#fff" />
    <path
      d="M 45 50 L 50 55 L 55 45"
      stroke="#333"
      stroke-width="3"
      stroke-linecap="round"
      stroke-linejoin="round"
      fill="none"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#95e1d3' },
})
</script>

