<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 10 L 30 30 L 20 50 L 30 70 L 50 90 L 70 70 L 80 50 L 70 30 Z"
      :fill="color"
    />
    <path
      d="M 50 20 L 35 35 L 25 50 L 35 65 L 50 80 L 65 65 L 75 50 L 65 35 Z"
      fill="#fff"
      opacity="0.3"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#6c5ce7' },
})
</script>
