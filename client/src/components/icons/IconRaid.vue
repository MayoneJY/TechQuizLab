<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <circle cx="50" cy="50" r="40" :fill="color" />
    <circle cx="30" cy="35" r="8" fill="#fff" />
    <circle cx="50" cy="30" r="8" fill="#fff" />
    <circle cx="70" cy="35" r="8" fill="#fff" />
    <circle cx="30" cy="65" r="8" fill="#fff" />
    <circle cx="50" cy="70" r="8" fill="#fff" />
    <circle cx="70" cy="65" r="8" fill="#fff" />
    <path
      d="M 30 35 L 50 30 L 70 35 M 30 65 L 50 70 L 70 65 M 30 35 L 30 65 M 50 30 L 50 70 M 70 35 L 70 65"
      stroke="#fff"
      stroke-width="2"
      opacity="0.5"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#f38181' },
})
</script>

