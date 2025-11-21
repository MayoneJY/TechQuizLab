<template>
  <MessageSquare :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { MessageSquare } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#4ecdc4' },
})
</script>

