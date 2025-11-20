<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 90 Q 30 70 30 50 Q 30 30 50 20 Q 50 10 50 10 Q 50 20 60 25 Q 70 30 70 50 Q 70 70 50 90 Z"
      :fill="color"
    />
    <path
      d="M 50 90 Q 40 75 40 60 Q 40 45 50 35 Q 50 30 50 30 Q 50 35 55 38 Q 60 42 60 55 Q 60 70 50 90 Z"
      fill="#fff"
      opacity="0.6"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#ff6b6b' },
})
</script>

