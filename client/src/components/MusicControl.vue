<template>
  <div class="music-control">
    <button
      class="music-button pixel-button-retro"
      @click="handleToggle"
      :title="musicStore.isPlaying && !musicStore.isMuted ? '음악 끄기' : '음악 켜기'"
    >
      <span class="icon-wrapper">
        <span class="icon" v-if="musicStore.isPlaying && !musicStore.isMuted">♪</span>
        <span class="icon muted" v-else>♫</span>
      </span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { useMusicStore } from '../stores/music'

const musicStore = useMusicStore()

async function handleToggle() {
  console.log('🎵 Music button clicked')
  console.log('🎵 Current state - isPlaying:', musicStore.isPlaying, 'isMuted:', musicStore.isMuted)
  
  // 실제 재생 상태 확인
  const actuallyPlaying = musicStore.isPlaying && !musicStore.isMuted
  
  if (actuallyPlaying) {
    // 재생 중이면 음소거 토글
    console.log('🎵 Currently playing - toggling mute')
    musicStore.toggleMute()
  } else {
    // 정지 중이면 재생 시작
    console.log('🎵 Not playing - starting playback')
    try {
      await musicStore.play()
      console.log('🎵 Playback command sent')
    } catch (error) {
      console.error('🎵 Failed to start playback:', error)
    }
  }
}
</script>

<style scoped>
.music-control {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.music-button {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #ffd43b 0%, #ff6b6b 100%);
  border: 4px solid #000;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 
    0 0 0 2px #fff,
    0 4px 0 0 #000,
    0 6px 12px rgba(0, 0, 0, 0.4),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  transition: all 0.1s ease;
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
}

.music-button::before {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  right: 2px;
  height: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.3) 0%, transparent 100%);
  border-radius: 2px;
  pointer-events: none;
}

.music-button:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 0 0 2px #fff,
    0 6px 0 0 #000,
    0 8px 16px rgba(0, 0, 0, 0.5),
    inset 0 2px 4px rgba(255, 255, 255, 0.4);
}

.music-button:active {
  transform: translateY(2px);
  box-shadow: 
    0 0 0 2px #fff,
    0 2px 0 0 #000,
    0 4px 8px rgba(0, 0, 0, 0.3),
    inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

.icon-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.icon {
  font-size: 32px;
  line-height: 1;
  color: #fff;
  text-shadow: 
    2px 2px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  font-weight: 900;
  animation: bounce 0.6s ease-in-out infinite;
}

.icon.muted {
  opacity: 0.5;
  animation: none;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

/* 모바일 대응 */
@media (max-width: 768px) {
  .music-control {
    top: 15px;
    right: 15px;
  }

  .music-button {
    width: 50px;
    height: 50px;
  }

  .icon {
    font-size: 26px;
  }
}
</style>

