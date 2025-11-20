<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <circle cx="30" cy="30" r="15" :fill="color" />
    <path
      d="M 10 70 Q 10 55 30 55 Q 50 55 50 70"
      :fill="color"
    />
    <circle cx="70" cy="30" r="15" :fill="color" />
    <path
      d="M 50 70 Q 50 55 70 55 Q 90 55 90 70"
      :fill="color"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: Number, default: 24 },
  color: { type: String, default: '#6c5ce7' },
})
</script>

