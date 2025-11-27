<template>
  <canvas 
    ref="canvasRef" 
    class="particle-canvas"
    :width="width"
    :height="height"
  ></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
const width = ref(window.innerWidth)
const height = ref(window.innerHeight)

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  color: string
  life: number
  maxLife: number
}

const particles: Particle[] = []

function createParticle(x: number, y: number, color: string = '#fff') {
  particles.push({
    x,
    y,
    vx: (Math.random() - 0.5) * 2,
    vy: (Math.random() - 0.5) * 2,
    size: Math.random() * 3 + 1,
    color,
    life: 0,
    maxLife: 60 + Math.random() * 40
  })
}

function updateParticles() {
  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i]
    p.x += p.vx
    p.y += p.vy
    p.life++
    
    if (p.life > p.maxLife || p.x < 0 || p.x > width.value || p.y < 0 || p.y > height.value) {
      particles.splice(i, 1)
    }
  }
}

function drawParticles(ctx: CanvasRenderingContext2D) {
  ctx.fillStyle = '#fff'
  particles.forEach(p => {
    const alpha = 1 - (p.life / p.maxLife)
    ctx.globalAlpha = alpha
    ctx.fillRect(Math.floor(p.x), Math.floor(p.y), p.size, p.size)
  })
  ctx.globalAlpha = 1
}

function animate() {
  if (!canvasRef.value) return
  
  const ctx = canvasRef.value.getContext('2d')
  if (!ctx) return
  
  ctx.clearRect(0, 0, width.value, height.value)
  
  // Create new particles occasionally
  if (Math.random() < 0.1) {
    createParticle(
      Math.random() * width.value,
      Math.random() * height.value,
      ['#fff', '#4a9eff', '#ffd43b', '#ff6b6b'][Math.floor(Math.random() * 4)]
    )
  }
  
  updateParticles()
  drawParticles(ctx)
  
  requestAnimationFrame(animate)
}

function handleResize() {
  width.value = window.innerWidth
  height.value = window.innerHeight
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  animate()
  
  // Initial particles
  for (let i = 0; i < 50; i++) {
    createParticle(
      Math.random() * width.value,
      Math.random() * height.value
    )
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  image-rendering: pixelated;
}
</style>

