<template>
  <BarChart3 :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { BarChart3 } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#667eea' },
})
</script>
