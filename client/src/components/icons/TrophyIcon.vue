<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 30 20 L 30 40 Q 30 50 50 50 Q 70 50 70 40 L 70 20 L 65 20 L 65 35 Q 65 42 50 42 Q 35 42 35 35 L 35 20 Z"
      :fill="color"
    />
    <path
      d="M 25 50 L 75 50 L 70 55 L 70 75 L 30 75 L 30 55 Z"
      :fill="color"
    />
    <path
      d="M 40 75 L 40 85 L 60 85 L 60 75"
      :fill="color"
    />
    <path
      d="M 45 85 L 45 90 L 55 90 L 55 85"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#ffd93d' },
})
</script>

