<template>
  <Shield :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { Shield } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#95e1d3' },
})
</script>
