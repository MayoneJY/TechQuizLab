<template>
  <Skull :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { Skull } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#ef4444' },
})
</script>

