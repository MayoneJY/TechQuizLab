<template>
  <Check :size="size" :color="color" :stroke-width="3" />
</template>

<script setup>
import { Check } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#10b981' },
})
</script>

