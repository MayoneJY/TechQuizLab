<template>
  <MusicControl />
  <PixelModal />
  <GlobalToast />
  <MainLayout>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </MainLayout>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import MusicControl from './components/MusicControl.vue'
import PixelModal from './components/PixelModal.vue'
import GlobalToast from './components/GlobalToast.vue'
import MainLayout from './layouts/MainLayout.vue'
import { useMusicStore } from './stores/music'
import { useBattleGlobalStore } from './stores/battleGlobal'

const musicStore = useMusicStore()
const battleGlobalStore = useBattleGlobalStore()
const route = useRoute()

// 라우터 변경 감지하여 화면별 브금 재생
watch(
  () => route.name,
  async (newRouteName, oldRouteName) => {
    // 게임 화면으로 진입할 때는 Game.vue에서 처리하므로 패스
    if (newRouteName === 'game') return

    // 1. 게임 화면에서 나온 경우: 메인 브금 다시 시작 (랜덤)
    if (oldRouteName === 'game') {
      try {
        await musicStore.playRandomMainMusic()
        console.log('🎲 Random main music started after leaving game')
      } catch (error) {
        console.log('⚠️ Failed to play random main music:', error)
      }
      return
    }

    // 2. 일반 화면 간 이동 (Home <-> Portfolio 등)
    // 이미 재생 중이라면 끊지 않고 유지
    if (musicStore.isPlaying) {
      console.log('🎵 Music already playing, keeping current track')
      return
    }

    // 재생 중이 아니라면 (사용자가 멈춘 경우 제외 - store에서 처리됨)
    // 혹은 끊겼던 상태라면 재생 시도
    try {
      await musicStore.playRandomMainMusic()
      console.log('🎲 Random main music started on route change')
    } catch (error) {
      console.log('⚠️ Failed to play random main music:', error)
    }
  },
  { immediate: false }
)

onMounted(async () => {
  // Global Monitoring
  battleGlobalStore.init()

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

