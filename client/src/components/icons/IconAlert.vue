<template>
  <AlertCircle :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { AlertCircle } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#f59e0b' },
})
</script>

