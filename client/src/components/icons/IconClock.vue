<template>
  <Clock :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { Clock } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#6366f1' },
})
</script>

