<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 10 L 50 90"
      :stroke="color"
      stroke-width="8"
      stroke-linecap="round"
    />
    <path
      d="M 30 20 L 50 10 L 70 20"
      :stroke="color"
      stroke-width="6"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
    <path
      d="M 40 90 L 50 85 L 60 90"
      :stroke="color"
      stroke-width="8"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
    <circle cx="50" cy="30" r="5" :fill="color" />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#4ecdc4' },
})
</script>

