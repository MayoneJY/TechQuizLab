<template>
  <Flame :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { Flame } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#f97316' },
})
</script>
