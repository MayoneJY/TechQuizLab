<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 100 100"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M 50 20 Q 40 30 45 50 Q 50 40 55 50 Q 60 30 50 20 Z"
      fill="#ff6b35"
    />
    <path
      d="M 50 30 Q 45 40 48 55 Q 50 50 52 55 Q 55 40 50 30 Z"
      fill="#f7931e"
    />
    <path
      d="M 50 40 Q 48 50 49 65 Q 50 60 51 65 Q 52 50 50 40 Z"
      fill="#ffd23f"
    />
    <path
      d="M 50 50 Q 49 60 50 75 Q 50 70 50 75 Q 51 60 50 50 Z"
      fill="#ffd23f"
    />
    <path
      d="M 50 60 Q 49 70 50 85 L 50 90 L 50 85 Q 51 70 50 60 Z"
      fill="#ff6b35"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: { type: [Number, String], default: 24 },
})
</script>
