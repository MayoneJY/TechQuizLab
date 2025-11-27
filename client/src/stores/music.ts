import { defineStore } from 'pinia'
import { ref, onMounted } from 'vue'
import { retroMusicPlayer } from '../utils/retroMusic'

export const useMusicStore = defineStore('music', () => {
  const isPlaying = ref(false)
  const volume = ref(0.4) // 슈퍼마리오 스타일로 볼륨 약간 증가
  const isMuted = ref(false)

  // localStorage에서 음악 설정 불러오기
  function init() {
    const savedIsPlaying = localStorage.getItem('musicPlaying')
    const savedVolume = localStorage.getItem('musicVolume')
    const savedIsMuted = localStorage.getItem('musicMuted')

    // 브라우저 정책상 사용자 상호작용 전에는 자동 재생하지 않음
    // 저장된 설정만 불러오되, 실제 재생 상태는 false로 시작
    // (사용자가 버튼을 클릭해야 실제로 재생됨)
    isPlaying.value = false

    if (savedVolume) {
      volume.value = parseFloat(savedVolume)
      retroMusicPlayer.setVolume(volume.value)
    }

    if (savedIsMuted === 'true') {
      isMuted.value = true
      retroMusicPlayer.setVolume(0)
    } else {
      isMuted.value = false
    }
  }

  function toggle() {
    if (isPlaying.value) {
      stop()
    } else {
      play()
    }
  }

  async function play() {
    console.log('🎵 MusicStore.play() called')
    console.log('🎵 Current isPlaying:', isPlaying.value)
    console.log('🎵 Volume:', volume.value)
    
    // 먼저 실제 재생 상태 확인
    const actuallyPlaying = retroMusicPlayer.getIsPlaying()
    console.log('🎵 retroMusicPlayer.getIsPlaying():', actuallyPlaying)
    
    if (actuallyPlaying) {
      console.log('⚠️ Music is already playing, just unmuting')
      isMuted.value = false
      retroMusicPlayer.setVolume(volume.value)
      localStorage.setItem('musicMuted', 'false')
      return
    }
    
    isMuted.value = false
    retroMusicPlayer.setVolume(volume.value)
    
    try {
      console.log('🎵 Calling retroMusicPlayer.play()')
      await retroMusicPlayer.play()
      
      // 실제로 재생이 시작되었는지 확인
      const checkPlaying = () => {
        const isActuallyPlaying = retroMusicPlayer.getIsPlaying()
        console.log('🎵 After play() call, isActuallyPlaying:', isActuallyPlaying)
        isPlaying.value = isActuallyPlaying
        
        if (isActuallyPlaying) {
          console.log('✅ Music started playing successfully')
          localStorage.setItem('musicPlaying', 'true')
          localStorage.setItem('musicMuted', 'false')
        } else {
          console.error('❌ Music did not start playing')
          isPlaying.value = false
        }
      }
      
      // 약간의 지연 후 상태 확인
      setTimeout(checkPlaying, 100)
      
    } catch (error) {
      console.error('❌ Failed to play music:', error)
      isPlaying.value = false
      throw error
    }
  }

  function stop() {
    isPlaying.value = false
    retroMusicPlayer.stop()
    localStorage.setItem('musicPlaying', 'false')
  }

  function toggleMute() {
    console.log('🔇 toggleMute() called')
    console.log('🔇 Current isMuted:', isMuted.value)
    console.log('🔇 Current isPlaying:', isPlaying.value)
    console.log('🔇 retroMusicPlayer.getIsPlaying():', retroMusicPlayer.getIsPlaying())
    
    // 실제 재생 상태 확인
    const actuallyPlaying = retroMusicPlayer.getIsPlaying()
    
    if (!actuallyPlaying && isPlaying.value) {
      // 상태가 불일치 - 실제로는 재생 안 되고 있음
      console.log('⚠️ State mismatch - fixing...')
      isPlaying.value = false
      isMuted.value = false
      localStorage.setItem('musicPlaying', 'false')
      localStorage.setItem('musicMuted', 'false')
      return
    }
    
    isMuted.value = !isMuted.value
    if (isMuted.value) {
      console.log('🔇 Muting...')
      retroMusicPlayer.setVolume(0)
    } else {
      console.log('🔊 Unmuting...')
      retroMusicPlayer.setVolume(volume.value)
    }
    localStorage.setItem('musicMuted', isMuted.value.toString())
  }

  function setVolume(newVolume: number) {
    volume.value = Math.max(0, Math.min(1, newVolume))
    if (!isMuted.value) {
      retroMusicPlayer.setVolume(volume.value)
    }
    localStorage.setItem('musicVolume', volume.value.toString())
  }

  // 랜덤 메인 브금 재생 (다른 화면 진입 시 사용)
  async function playRandomMainMusic() {
    isMuted.value = false
    retroMusicPlayer.setVolume(volume.value)
    
    try {
      console.log('🎲 Playing random main music...')
      await retroMusicPlayer.playRandomMainMusic()
      
      const checkPlaying = () => {
        const isActuallyPlaying = retroMusicPlayer.getIsPlaying()
        isPlaying.value = isActuallyPlaying
        
        if (isActuallyPlaying) {
          console.log('✅ Random main music started playing successfully')
          localStorage.setItem('musicPlaying', 'true')
          localStorage.setItem('musicMuted', 'false')
        } else {
          console.error('❌ Random main music did not start playing')
          isPlaying.value = false
        }
      }
      
      setTimeout(checkPlaying, 100)
    } catch (error) {
      console.error('❌ Failed to play random main music:', error)
      isPlaying.value = false
      throw error
    }
  }

  // 초기화
  init()

  return {
    isPlaying,
    volume,
    isMuted,
    toggle,
    play,
    stop,
    toggleMute,
    setVolume,
    playRandomMainMusic
  }
})

