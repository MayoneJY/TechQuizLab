<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <circle cx="50" cy="50" r="45" :fill="color" />
    <circle cx="35" cy="40" r="8" fill="#fff" />
    <circle cx="65" cy="40" r="8" fill="#fff" />
    <circle cx="35" cy="40" r="4" fill="#333" />
    <circle cx="65" cy="40" r="4" fill="#333" />
    <path
      d="M 30 65 Q 50 75 70 65"
      stroke="#fff"
      stroke-width="3"
      fill="none"
      stroke-linecap="round"
    />
    <path
      d="M 20 30 L 30 25 L 35 35 L 30 25"
      stroke="#fff"
      stroke-width="2"
      fill="none"
    />
    <path
      d="M 80 30 L 70 25 L 65 35 L 70 25"
      stroke="#fff"
      stroke-width="2"
      fill="none"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#ff6b6b' },
})
</script>

