<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 20 C 30 20 20 30 20 50 C 20 60 30 70 50 85 C 70 70 80 60 80 50 C 80 30 70 20 50 20 Z"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#ff6b6b' },
})
</script>
