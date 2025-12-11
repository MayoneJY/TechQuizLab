<template>
  <canvas 
    ref="canvasRef" 
    class="explosion-canvas"
    :width="width"
    :height="height"
  ></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

interface Props {
  trigger: boolean
  x?: number
  y?: number
  color?: string
}

const props = withDefaults(defineProps<Props>(), {
  x: 0.5,
  y: 0.5,
  color: '#ffd43b'
})

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

function createExplosion(centerX: number, centerY: number, color: string) {
  for (let i = 0; i < 30; i++) {
    const angle = (Math.PI * 2 * i) / 30
    const speed = 2 + Math.random() * 4
    particles.push({
      x: centerX,
      y: centerY,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed,
      size: 3 + Math.random() * 4,
      color,
      life: 0,
      maxLife: 30 + Math.random() * 20
    })
  }
}

function updateParticles() {
  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i]
    p.x += p.vx
    p.y += p.vy
    p.vy += 0.1 // gravity
    p.life++
    
    if (p.life > p.maxLife) {
      particles.splice(i, 1)
    }
  }
}

function drawParticles(ctx: CanvasRenderingContext2D) {
  particles.forEach(p => {
    const alpha = 1 - (p.life / p.maxLife)
    ctx.globalAlpha = alpha
    ctx.fillStyle = p.color
    ctx.fillRect(Math.floor(p.x - p.size / 2), Math.floor(p.y - p.size / 2), p.size, p.size)
  })
  ctx.globalAlpha = 1
}

function animate() {
  if (!canvasRef.value) return
  
  const ctx = canvasRef.value.getContext('2d')
  if (!ctx) return
  
  ctx.clearRect(0, 0, width.value, height.value)
  
  updateParticles()
  drawParticles(ctx)
  
  if (particles.length > 0) {
    requestAnimationFrame(animate)
  }
}

watch(() => props.trigger, (newVal) => {
  if (newVal) {
    const centerX = props.x * width.value
    const centerY = props.y * height.value
    createExplosion(centerX, centerY, props.color)
    animate()
  }
})

function handleResize() {
  width.value = window.innerWidth
  height.value = window.innerHeight
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.explosion-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 100;
  pointer-events: none;
  image-rendering: pixelated;
}
</style>

