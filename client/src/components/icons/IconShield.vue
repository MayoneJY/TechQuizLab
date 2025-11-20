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
    />
    <path
      d="M 50 20 L 30 28 L 30 50 Q 30 65 50 75 Q 70 65 70 50 L 70 28 Z"
      fill="#fff"
      opacity="0.3"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#95e1d3' },
})
</script>
