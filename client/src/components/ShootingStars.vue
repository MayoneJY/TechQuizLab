<template>
  <canvas 
    ref="canvasRef" 
    class="shooting-stars-canvas"
    :width="width"
    :height="height"
  ></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
const width = ref(window.innerWidth)
const height = ref(window.innerHeight)

interface Star {
  x: number
  y: number
  len: number
  speed: number
  opacity: number
}

const stars: Star[] = []
let animationFrameId: number | null = null

function spawnStar() {
  // Start mainly from top area, can overshoot width slightly
  const startX = Math.random() * width.value
  const startY = Math.random() * -100 // Start slightly above viewport to fall in
  
  stars.push({
    x: startX,
    y: startY,
    len: Math.random() * 80 + 20,
    speed: Math.random() * 10 + 15, // Increase speed for dramatic fall
    opacity: 1
  })
  
  if (!animationFrameId) {
    animate()
  }
}

function updateStars() {
  for (let i = stars.length - 1; i >= 0; i--) {
    const s = stars[i]
    // Slight drift for realism, but mostly down
    s.x -= s.speed * 0.1 // Small horizontal drift left
    s.y += s.speed // Major vertical movement
    
    s.opacity -= 0.015
    
    if (s.opacity <= 0 || s.y > height.value + 100) {
      stars.splice(i, 1)
    }
  }
}

function drawStars(ctx: CanvasRenderingContext2D) {
  ctx.clearRect(0, 0, width.value, height.value)
  
  ctx.lineWidth = 2
  ctx.lineCap = 'round'
  
  stars.forEach(s => {
    ctx.beginPath()
    ctx.strokeStyle = `rgba(255, 255, 255, ${s.opacity})`
    ctx.moveTo(s.x, s.y)
    // Tail should be opposite to movement vector
    // Moving: x -= speed*0.1, y += speed
    // Tail: x += speed*0.1 * factor, y -= speed * factor
    // Simplified tail direction: Up and slightly right
    ctx.lineTo(s.x + s.len * 0.1, s.y - s.len) 
    ctx.stroke()
  })
}

function animate() {
  if (!canvasRef.value) return
  
  const ctx = canvasRef.value.getContext('2d')
  if (!ctx) return
  
  updateStars()
  drawStars(ctx)
  
  if (stars.length > 0) {
    animationFrameId = requestAnimationFrame(animate)
  } else {
    // Clear canvas when no stars
    ctx.clearRect(0, 0, width.value, height.value)
    animationFrameId = null
  }
}

function handleResize() {
  width.value = window.innerWidth
  height.value = window.innerHeight
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
})

defineExpose({
  spawnStar
})
</script>

<style scoped>
.shooting-stars-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1; /* Behind content but above static background */
  pointer-events: none;
}
</style>
