<template>
  <div 
    class="spaceship-cursor" 
    :class="{ hover: isHovering, click: isClicking }"
    :style="spaceshipStyle"
    ref="cursorRef"
  >
    <!-- 우주선 본체 -->
    <div class="spaceship-body">
      <!-- 상단 날개 -->
      <div class="spaceship-top-wing"></div>
      <!-- 본체 -->
      <div class="spaceship-main">
        <div class="spaceship-cockpit"></div>
        <div class="spaceship-body-line"></div>
      </div>
      <!-- 좌우 날개 -->
      <div class="spaceship-wing-left"></div>
      <div class="spaceship-wing-right"></div>
      <!-- 하단 엔진 -->
      <div class="spaceship-engine"></div>
    </div>
    
    <!-- 추진기 효과 -->
    <div class="thruster" :class="{ active: isMoving }">
      <div class="thruster-flame flame-1"></div>
      <div class="thruster-flame flame-2"></div>
      <div class="thruster-flame flame-3"></div>
    </div>
    
    <!-- 별 파티클 효과 -->
    <div 
      v-for="i in 3" 
      :key="i"
      class="star-trail"
      :style="getStarTrailStyle(i)"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, computed } from 'vue'

const x = ref(0)
const y = ref(0)
const prevX = ref(0)
const prevY = ref(0)
const isHovering = ref(false)
const isClicking = ref(false)
const isMoving = ref(false)
const angle = ref(0)
const cursorRef = ref<HTMLElement | null>(null)

const spaceshipStyle = computed(() => {
  return {
    left: x.value + 'px',
    top: y.value + 'px',
    transform: `translate(-50%, -50%) rotate(${angle.value}deg)`
  }
})

function getStarTrailStyle(index: number) {
  const delay = index * 0.1
  const offset = index * 8
  return {
    animationDelay: `${delay}s`,
    left: `${-offset}px`
  }
}

function calculateAngle(x1: number, y1: number, x2: number, y2: number): number {
  const dx = x2 - x1
  const dy = y2 - y1
  return Math.atan2(dy, dx) * (180 / Math.PI) + 90
}

onMounted(() => {
  // 터치 디바이스에서는 커서 숨기기
  const isTouchDevice = 'ontouchstart' in window || navigator.maxTouchPoints > 0
  if (isTouchDevice && cursorRef.value) {
    cursorRef.value.style.display = 'none'
    return
  }
  
  let animationFrame: number
  
  const handleMouseMove = (e: MouseEvent) => {
    prevX.value = x.value
    prevY.value = y.value
    x.value = e.clientX
    y.value = e.clientY
    
    // 이동 속도 계산
    const dx = x.value - prevX.value
    const dy = y.value - prevY.value
    const speed = Math.sqrt(dx * dx + dy * dy)
    
    isMoving.value = speed > 0.5
    
    // 우주선 회전 각도 계산
    if (speed > 0) {
      angle.value = calculateAngle(prevX.value, prevY.value, x.value, y.value)
    }
    
    // 호버 상태 확인
    const target = e.target as HTMLElement
    const isInteractive = target.matches('button, a, input, select, textarea, [role="button"]') ||
                         target.closest('button, a, input, select, textarea, [role="button"]')
    
    isHovering.value = !!isInteractive
    
    // 부드러운 애니메이션
    if (animationFrame) {
      cancelAnimationFrame(animationFrame)
    }
    animationFrame = requestAnimationFrame(() => {
      // 추가 애니메이션 로직
    })
  }
  
  const handleMouseDown = () => {
    isClicking.value = true
  }
  
  const handleMouseUp = () => {
    isClicking.value = false
  }
  
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mousedown', handleMouseDown)
  window.addEventListener('mouseup', handleMouseUp)
  
  onUnmounted(() => {
    window.removeEventListener('mousemove', handleMouseMove)
    window.removeEventListener('mousedown', handleMouseDown)
    window.removeEventListener('mouseup', handleMouseUp)
    if (animationFrame) {
      cancelAnimationFrame(animationFrame)
    }
  })
})
</script>

<style scoped>
.spaceship-cursor {
  position: fixed;
  pointer-events: none;
  z-index: 10000;
  transform-origin: center center;
  transition: transform 0.1s ease-out;
  mix-blend-mode: normal;
  will-change: transform;
  backface-visibility: hidden;
}

.spaceship-body {
  position: relative;
  width: 32px;
  height: 36px;
  transform: translate(-50%, -50%);
  opacity: 1;
  z-index: 2;
}

/* 상단 날개 */
.spaceship-top-wing {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 8px solid #4a9eff;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.8));
  z-index: 3;
}

/* 우주선 본체 */
.spaceship-main {
  position: absolute;
  top: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 18px;
  background: linear-gradient(180deg, #5bb0ff 0%, #4a9eff 50%, #357abd 100%);
  border: 2px solid #000;
  border-radius: 3px 3px 0 0;
  z-index: 2;
  box-shadow: 
    inset 0 2px 4px rgba(255, 255, 255, 0.3),
    0 0 8px rgba(74, 158, 255, 0.6);
}

/* 조종석 */
.spaceship-cockpit {
  position: absolute;
  top: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 8px;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 2px;
  box-shadow: 
    inset 0 0 4px rgba(255, 255, 255, 0.6),
    0 0 3px rgba(74, 158, 255, 0.8);
  z-index: 3;
}

/* 본체 구분선 */
.spaceship-body-line {
  position: absolute;
  bottom: 4px;
  left: 2px;
  right: 2px;
  height: 2px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 1px;
}

/* 왼쪽 날개 */
.spaceship-wing-left {
  position: absolute;
  top: 10px;
  left: -6px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-right: 10px solid #5bb0ff;
  filter: drop-shadow(2px 0 2px rgba(0, 0, 0, 0.8));
  z-index: 1;
}

/* 오른쪽 날개 */
.spaceship-wing-right {
  position: absolute;
  top: 10px;
  right: -6px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 10px solid #5bb0ff;
  filter: drop-shadow(-2px 0 2px rgba(0, 0, 0, 0.8));
  z-index: 1;
}

/* 하단 엔진 */
.spaceship-engine {
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 12px;
  height: 4px;
  background: linear-gradient(180deg, #357abd 0%, #1e3a5f 100%);
  border: 1px solid #000;
  border-radius: 0 0 2px 2px;
  z-index: 1;
}

/* 추진기 */
.thruster {
  position: absolute;
  top: 36px;
  left: 50%;
  transform: translateX(-50%);
  width: 8px;
  height: 15px;
  opacity: 0;
  transition: opacity 0.2s;
  z-index: 0;
}

.thruster.active {
  opacity: 1;
}

.thruster-flame {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  border-radius: 50%;
  animation: thrusterPulse 0.3s ease-in-out infinite;
}

.flame-1 {
  width: 6px;
  height: 8px;
  background: radial-gradient(circle, #ffd43b 0%, #ff6b6b 50%, transparent 100%);
  top: 0;
  animation-delay: 0s;
}

.flame-2 {
  width: 8px;
  height: 10px;
  background: radial-gradient(circle, #ff6b6b 0%, #ffd43b 50%, transparent 100%);
  top: 5px;
  animation-delay: 0.1s;
}

.flame-3 {
  width: 6px;
  height: 8px;
  background: radial-gradient(circle, #ffd43b 0%, #4a9eff 50%, transparent 100%);
  top: 10px;
  animation-delay: 0.2s;
}

@keyframes thrusterPulse {
  0%, 100% {
    transform: translateX(-50%) scale(1);
    opacity: 0.8;
  }
  50% {
    transform: translateX(-50%) scale(1.2);
    opacity: 1;
  }
}

/* 별 궤적 효과 */
.star-trail {
  position: absolute;
  top: 50%;
  width: 4px;
  height: 4px;
  background: #ffd43b;
  border-radius: 50%;
  box-shadow: 0 0 6px #ffd43b;
  animation: starTrail 1s ease-out infinite;
}

@keyframes starTrail {
  0% {
    opacity: 1;
    transform: translateY(-50%) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-50%) scale(0.3);
  }
}

/* 호버 상태 */
.spaceship-cursor.hover .spaceship-top-wing {
  border-bottom-color: #6bc0ff;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.8)) drop-shadow(0 0 8px rgba(74, 158, 255, 0.8));
}

.spaceship-cursor.hover .spaceship-main {
  background: linear-gradient(180deg, #6bc0ff 0%, #5bb0ff 50%, #4a9eff 100%);
  box-shadow: 
    inset 0 2px 4px rgba(255, 255, 255, 0.4),
    0 0 12px rgba(74, 158, 255, 0.9);
}

.spaceship-cursor.hover .spaceship-wing-left {
  border-right-color: #6bc0ff;
  filter: drop-shadow(2px 0 2px rgba(0, 0, 0, 0.8)) drop-shadow(3px 0 6px rgba(74, 158, 255, 0.6));
}

.spaceship-cursor.hover .spaceship-wing-right {
  border-left-color: #6bc0ff;
  filter: drop-shadow(-2px 0 2px rgba(0, 0, 0, 0.8)) drop-shadow(-3px 0 6px rgba(74, 158, 255, 0.6));
}

.spaceship-cursor.hover .spaceship-cockpit {
  background: rgba(255, 255, 255, 0.5);
  box-shadow: 
    inset 0 0 4px rgba(255, 255, 255, 0.7),
    0 0 5px rgba(74, 158, 255, 1);
}

/* 클릭 상태 */
.spaceship-cursor.click .spaceship-top-wing {
  border-bottom-color: #ff6b6b;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.8)) drop-shadow(0 0 8px rgba(255, 107, 107, 0.8));
}

.spaceship-cursor.click .spaceship-main {
  background: linear-gradient(180deg, #ff7b7b 0%, #ff6b6b 50%, #ee5a5a 100%);
  box-shadow: 
    inset 0 2px 4px rgba(255, 255, 255, 0.3),
    0 0 12px rgba(255, 107, 107, 0.9);
}

.spaceship-cursor.click .spaceship-wing-left {
  border-right-color: #ff6b6b;
  filter: drop-shadow(2px 0 2px rgba(0, 0, 0, 0.8)) drop-shadow(3px 0 6px rgba(255, 107, 107, 0.6));
}

.spaceship-cursor.click .spaceship-wing-right {
  border-left-color: #ff6b6b;
  filter: drop-shadow(-2px 0 2px rgba(0, 0, 0, 0.8)) drop-shadow(-3px 0 6px rgba(255, 107, 107, 0.6));
}

.spaceship-cursor.click .thruster.active .flame-1,
.spaceship-cursor.click .thruster.active .flame-2,
.spaceship-cursor.click .thruster.active .flame-3 {
  background: radial-gradient(circle, #ff6b6b 0%, #ffd43b 50%, transparent 100%);
}
</style>

