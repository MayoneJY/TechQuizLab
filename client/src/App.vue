<template>
  <CustomCursor />
  <CursorTrail />
  <MusicControl />
  <router-view />
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import CustomCursor from './components/CustomCursor.vue'
import CursorTrail from './components/CursorTrail.vue'
import MusicControl from './components/MusicControl.vue'
import { useMusicStore } from './stores/music'

const musicStore = useMusicStore()
const route = useRoute()

// 라우터 변경 감지하여 화면별 브금 재생
watch(
  () => route.name,
  async (newRouteName, oldRouteName) => {
    // 게임 화면이 아닌 경우 랜덤 메인 브금 재생
    if (newRouteName !== 'game') {
      // 게임 화면에서 나온 경우에만 랜덤 메인 브금 재생
      if (oldRouteName === 'game') {
        try {
          await musicStore.playRandomMainMusic()
          console.log('🎲 Random main music started after leaving game')
        } catch (error) {
          console.log('⚠️ Failed to play random main music:', error)
        }
      } else if (oldRouteName !== newRouteName) {
        // 다른 화면으로 이동한 경우에도 랜덤 메인 브금 재생
        try {
          await musicStore.playRandomMainMusic()
          console.log('🎲 Random main music started on route change')
        } catch (error) {
          console.log('⚠️ Failed to play random main music:', error)
        }
      }
    }
  },
  { immediate: false }
)

onMounted(async () => {
  // 초기 로드 시 (홈 화면) 랜덤 메인 브금 재생 시도
  if (route.name !== 'game') {
    try {
      setTimeout(async () => {
        try {
          await musicStore.playRandomMainMusic()
          console.log('🎲 Auto-play random main music started')
        } catch (error) {
          console.log('⚠️ Auto-play blocked by browser policy. User interaction required.')
        }
      }, 500)
    } catch (error) {
      console.error('Failed to auto-play random main music:', error)
    }
  }
})
</script>

