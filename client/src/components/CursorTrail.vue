<template>
  <div class="cursor-trail" ref="trailRef"></div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const trailRef = ref<HTMLElement | null>(null)
const particles: Array<{ 
  x: number
  y: number
  life: number
  vx: number
  vy: number
  size: number
  type: 'star' | 'spark'
}> = []
let prevX = 0
let prevY = 0

onMounted(() => {
  const handleMouseMove = (e: MouseEvent) => {
    if (!trailRef.value) return
    
    // 이동 속도 계산
    const dx = e.clientX - prevX
    const dy = e.clientY - prevY
    const speed = Math.sqrt(dx * dx + dy * dy)
    
    prevX = e.clientX
    prevY = e.clientY
    
    // 빠르게 움직일 때만 파티클 생성
    if (speed > 2) {
      // 별 파티클 생성
      for (let i = 0; i < 2; i++) {
        const angle = Math.atan2(dy, dx) + (Math.random() - 0.5) * 0.5
        const velocity = speed * 0.1 + Math.random() * 2
        particles.push({
          x: e.clientX,
          y: e.clientY,
          life: 1,
          vx: Math.cos(angle) * velocity,
          vy: Math.sin(angle) * velocity,
          size: 3 + Math.random() * 3,
          type: 'star'
        })
      }
      
      // 스파크 파티클 생성
      if (speed > 10) {
        for (let i = 0; i < 1; i++) {
          particles.push({
            x: e.clientX + (Math.random() - 0.5) * 10,
            y: e.clientY + (Math.random() - 0.5) * 10,
            life: 1,
            vx: (Math.random() - 0.5) * 3,
            vy: (Math.random() - 0.5) * 3,
            size: 2 + Math.random() * 2,
            type: 'spark'
          })
        }
      }
    }
    
    // 파티클 수 제한
    if (particles.length > 80) {
      particles.splice(0, particles.length - 80)
    }
  }
  
  const animate = () => {
    if (!trailRef.value) return
    
    // 파티클 업데이트
    for (let i = particles.length - 1; i >= 0; i--) {
      const p = particles[i]
      p.x += p.vx
      p.y += p.vy
      p.vx *= 0.95 // 마찰
      p.vy *= 0.95
      p.life -= 0.015
      
      if (p.life <= 0) {
        particles.splice(i, 1)
      }
    }
    
    // 파티클 렌더링
    trailRef.value.innerHTML = particles.map((p) => {
      const className = p.type === 'star' ? 'trail-star' : 'trail-spark'
      return `<div class="${className}" style="left: ${p.x}px; top: ${p.y}px; opacity: ${p.life}; transform: scale(${p.life}); width: ${p.size}px; height: ${p.size}px;"></div>`
    }).join('')
    
    requestAnimationFrame(animate)
  }
  
  window.addEventListener('mousemove', handleMouseMove)
  animate()
  
  onUnmounted(() => {
    window.removeEventListener('mousemove', handleMouseMove)
  })
})
</script>

<style scoped>
.cursor-trail {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9998;
}

.trail-star {
  position: absolute;
  background: radial-gradient(circle, #ffd43b 0%, #ff6b6b 50%, transparent 100%);
  border-radius: 50%;
  pointer-events: none;
  transform-origin: center;
  box-shadow: 0 0 8px #ffd43b;
  animation: starTwinkle 1s ease-out infinite;
}

.trail-spark {
  position: absolute;
  background: radial-gradient(circle, #4a9eff 0%, #ffd43b 50%, transparent 100%);
  border-radius: 50%;
  pointer-events: none;
  transform-origin: center;
  box-shadow: 0 0 6px #4a9eff;
  animation: sparkFlicker 0.5s ease-out infinite;
}

@keyframes starTwinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.8);
  }
}

@keyframes sparkFlicker {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.2);
  }
}
</style>

