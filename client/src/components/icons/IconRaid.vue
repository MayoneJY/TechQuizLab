<template>
  <UsersRound :size="size" :color="color" :stroke-width="2.5" />
</template>

<script setup>
import { UsersRound } from 'lucide-vue-next'

defineProps({
  size: { type: [Number, String], default: 24 },
  color: { type: String, default: '#ec4899' },
})
</script>
