<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 10 L 60 40 L 90 40 L 68 60 L 78 90 L 50 70 L 22 90 L 32 60 L 10 40 L 40 40 Z"
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
