// 레트로 게이밍 브금 재생 유틸리티
// HTML5 Audio API를 사용하여 MP3 파일 재생

class RetroMusicPlayer {
  private audio: HTMLAudioElement | null = null
  private isPlaying = false
  private volume = 0.5
  private musicPath = '/music.mp3' // public 폴더에 music.mp3 파일을 넣어주세요
  private gameMusicPath = '/game-music.mp3' // 게임 화면용 브금 (public 폴더에 game-music.mp3 파일을 넣어주세요)
  private gameAudio: HTMLAudioElement | null = null
  private isGameMusicPlaying = false
  
  // 랜덤 메인 브금 파일 경로 배열 (5개)
  private mainMusicPaths = [
    '/main-music1.mp3',
    '/main-music2.mp3',
    '/main-music3.mp3',
    '/main-music4.mp3',
    '/main-music5.mp3'
  ]

  async init() {
    if (!this.audio) {
      try {
        this.audio = new Audio(this.musicPath)
        this.audio.loop = true // 반복 재생
        this.audio.volume = this.volume
        this.audio.preload = 'auto'
        
        // 에러 핸들링
        this.audio.addEventListener('error', (e) => {
          console.error('❌ Audio load error:', e)
          console.error('❌ Make sure music.mp3 file exists in public folder')
        })
        
        this.audio.addEventListener('loadeddata', () => {
          console.log('✅ Music file loaded successfully')
        })
        
        console.log('✅ Audio element initialized, path:', this.musicPath)
      } catch (error) {
        console.error('❌ Failed to initialize Audio:', error)
        throw error
      }
    }
  }

  async play() {
    if (this.isPlaying) {
      console.log('⚠️ Already playing')
      return
    }

    try {
      await this.init()
      
      if (!this.audio) {
        console.error('❌ Audio element is null')
        return
      }

      this.isPlaying = true
      this.audio.volume = this.volume
      
      try {
        // 자동 재생을 위한 추가 시도
        const playPromise = this.audio.play()
        
        if (playPromise !== undefined) {
          await playPromise
        }
        
        console.log('🎵 Music started playing')
        console.log('🎵 File path:', this.musicPath)
        console.log('🎵 Volume:', this.volume)
      } catch (playError: any) {
        console.error('❌ Failed to play audio:', playError)
        // 브라우저 정책으로 인한 자동 재생 차단일 수 있음
        if (playError.name === 'NotAllowedError' || playError.name === 'NotSupportedError') {
          console.warn('⚠️ Autoplay blocked by browser policy. User interaction required.')
          // 자동 재생이 차단되어도 에러를 던지지 않음 (사용자가 버튼을 클릭할 수 있도록)
          this.isPlaying = false
          return
        }
        this.isPlaying = false
        throw playError
      }
    } catch (error) {
      console.error('❌ Failed to play music:', error)
      this.isPlaying = false
      // 자동 재생 실패는 에러를 던지지 않음
    }
  }

  stop() {
    console.log('🛑 Stopping music')
    this.isPlaying = false
    
    if (this.audio) {
      this.audio.pause()
      this.audio.currentTime = 0
    }
  }

  setVolume(volume: number) {
    this.volume = Math.max(0, Math.min(1, volume))
    if (this.audio) {
      this.audio.volume = this.volume
      console.log('🔊 Volume set to:', this.volume)
    }
  }

  getVolume(): number {
    return this.volume
  }

  getIsPlaying(): boolean {
    return this.isPlaying && this.audio !== null && !this.audio.paused
  }

  // 음악 파일 경로 설정 (선택적)
  setMusicPath(path: string) {
    this.musicPath = path
    if (this.audio) {
      this.audio.src = path
    }
  }

  // 게임 브금 초기화
  async initGameMusic() {
    if (!this.gameAudio) {
      try {
        this.gameAudio = new Audio(this.gameMusicPath)
        this.gameAudio.loop = true
        this.gameAudio.volume = this.volume
        this.gameAudio.preload = 'auto'
        
        this.gameAudio.addEventListener('error', (e) => {
          console.error('❌ Game music load error:', e)
          console.error('❌ Make sure game-music.mp3 file exists in public folder')
        })
        
        this.gameAudio.addEventListener('loadeddata', () => {
          console.log('✅ Game music file loaded successfully')
        })
        
        console.log('✅ Game audio element initialized, path:', this.gameMusicPath)
      } catch (error) {
        console.error('❌ Failed to initialize game audio:', error)
        throw error
      }
    }
  }

  // 게임 브금 재생
  async playGameMusic() {
    if (this.isGameMusicPlaying) {
      console.log('⚠️ Game music already playing')
      return
    }

    try {
      // 메인 브금 정지
      if (this.audio && !this.audio.paused) {
        this.audio.pause()
        this.isPlaying = false
      }

      await this.initGameMusic()
      
      if (!this.gameAudio) {
        console.error('❌ Game audio element is null')
        return
      }

      this.isGameMusicPlaying = true
      this.gameAudio.volume = this.volume
      
      try {
        const playPromise = this.gameAudio.play()
        if (playPromise !== undefined) {
          await playPromise
        }
        console.log('🎮 Game music started playing')
        console.log('🎮 File path:', this.gameMusicPath)
      } catch (playError: any) {
        console.error('❌ Failed to play game music:', playError)
        if (playError.name === 'NotAllowedError' || playError.name === 'NotSupportedError') {
          console.warn('⚠️ Game music autoplay blocked by browser policy.')
          this.isGameMusicPlaying = false
          return
        }
        this.isGameMusicPlaying = false
        throw playError
      }
    } catch (error) {
      console.error('❌ Failed to play game music:', error)
      this.isGameMusicPlaying = false
    }
  }

  // 게임 브금 정지
  stopGameMusic() {
    console.log('🛑 Stopping game music')
    this.isGameMusicPlaying = false
    
    if (this.gameAudio) {
      this.gameAudio.pause()
      this.gameAudio.currentTime = 0
    }
  }

  // 게임 브금 파일 경로 설정
  setGameMusicPath(path: string) {
    this.gameMusicPath = path
    if (this.gameAudio) {
      this.gameAudio.src = path
    }
  }

  // 랜덤 메인 브금 재생
  async playRandomMainMusic() {
    // 게임 브금이 재생 중이면 정지
    if (this.isGameMusicPlaying && this.gameAudio) {
      this.stopGameMusic()
    }

    // 랜덤으로 메인 브금 선택
    const randomIndex = Math.floor(Math.random() * this.mainMusicPaths.length)
    const selectedPath = this.mainMusicPaths[randomIndex]
    
    console.log(`🎲 Random main music selected: ${selectedPath} (${randomIndex + 1}/5)`)
    
    // 기존 오디오가 있으면 정지하고 새로 초기화
    if (this.audio) {
      this.audio.pause()
      this.audio.currentTime = 0
      this.isPlaying = false
    }
    
    // 새로운 경로로 설정
    this.musicPath = selectedPath
    this.audio = null // 기존 오디오 제거하여 새로 초기화
    
    // 재생
    await this.play()
  }

  // 메인 브금 파일 경로 배열 설정 (선택적)
  setMainMusicPaths(paths: string[]) {
    if (paths.length > 0) {
      this.mainMusicPaths = paths
    }
  }
}

// 싱글톤 인스턴스
export const retroMusicPlayer = new RetroMusicPlayer()
