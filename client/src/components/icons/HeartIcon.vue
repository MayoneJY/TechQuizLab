<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 85 Q 20 60 20 40 Q 20 25 35 25 Q 42 25 50 35 Q 58 25 65 25 Q 80 25 80 40 Q 80 60 50 85 Z"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#ff6b6b' },
})
</script>

