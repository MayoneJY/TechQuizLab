<template>
  <Star :size="size" :color="color" :stroke-width="2.5" :fill="fill ? color : 'none'" />
</template>

<script setup>
import { Star } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#fbbf24' },
  fill: { type: Boolean, default: false },
})
</script>
