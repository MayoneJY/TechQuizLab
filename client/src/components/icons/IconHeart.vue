<template>
  <Heart :size="size" :color="color" :stroke-width="2.5" :fill="fill ? color : 'none'" />
</template>

<script setup>
import { Heart } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#ef4444' },
  fill: { type: Boolean, default: false },
})
</script>
