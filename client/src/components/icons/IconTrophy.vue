<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 30 20 L 30 40 Q 30 50 40 50 L 60 50 Q 70 50 70 40 L 70 20 L 65 20 L 65 35 Q 65 45 60 45 L 40 45 Q 35 45 35 35 L 35 20 Z"
      :fill="color"
    />
    <path
      d="M 25 50 L 30 50 L 30 60 L 70 60 L 70 50 L 75 50 L 75 70 L 25 70 Z"
      :fill="color"
    />
    <circle cx="50" cy="75" r="8" :fill="color" />
    <path
      d="M 40 20 L 40 15 L 60 15 L 60 20"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#ffd93d' },
})
</script>
