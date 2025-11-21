<template>
  <AppLayout>
    <RouterView />
  </AppLayout>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { RouterView } from 'vue-router'
import AppLayout from './components/AppLayout.vue'

function logDimensions() {
  const html = document.documentElement
  const body = document.body
  const app = document.getElementById('app')
  const appLayout = document.querySelector('.app-layout')
  const main = document.querySelector('.main')
  const container = document.querySelector('.container')
  const pageContainer = document.querySelector('.page-container')

  console.log('=== 화면 크기 디버깅 ===')
  console.log('Window innerWidth:', window.innerWidth)
  console.log('Window innerHeight:', window.innerHeight)
  console.log('Window outerWidth:', window.outerWidth)
  console.log('Window outerHeight:', window.outerHeight)
  
  if (html) {
    console.log('HTML clientWidth:', html.clientWidth)
    console.log('HTML clientHeight:', html.clientHeight)
    const htmlStyle = window.getComputedStyle(html)
    console.log('HTML computed width:', htmlStyle.width)
    console.log('HTML computed maxWidth:', htmlStyle.maxWidth)
  }
  
  if (body) {
    console.log('Body clientWidth:', body.clientWidth)
    console.log('Body clientHeight:', body.clientHeight)
    const bodyStyle = window.getComputedStyle(body)
    console.log('Body computed width:', bodyStyle.width)
    console.log('Body computed maxWidth:', bodyStyle.maxWidth)
  }
  
  if (app) {
    console.log('#app clientWidth:', app.clientWidth)
    console.log('#app clientHeight:', app.clientHeight)
    const appStyle = window.getComputedStyle(app)
    console.log('#app computed width:', appStyle.width)
    console.log('#app computed maxWidth:', appStyle.maxWidth)
    console.log('#app computed height:', appStyle.height)
  }
  
  if (appLayout) {
    console.log('.app-layout clientWidth:', appLayout.clientWidth)
    const appLayoutStyle = window.getComputedStyle(appLayout)
    console.log('.app-layout computed width:', appLayoutStyle.width)
    console.log('.app-layout computed maxWidth:', appLayoutStyle.maxWidth)
  }
  
  if (main) {
    console.log('.main clientWidth:', main.clientWidth)
    const mainStyle = window.getComputedStyle(main)
    console.log('.main computed width:', mainStyle.width)
    console.log('.main computed maxWidth:', mainStyle.maxWidth)
  }
  
  if (container) {
    console.log('.container clientWidth:', container.clientWidth)
    const containerStyle = window.getComputedStyle(container)
    console.log('.container computed width:', containerStyle.width)
    console.log('.container computed maxWidth:', containerStyle.maxWidth)
  }
  
  if (pageContainer) {
    console.log('.page-container clientWidth:', pageContainer.clientWidth)
    const pageContainerStyle = window.getComputedStyle(pageContainer)
    console.log('.page-container computed width:', pageContainerStyle.width)
    console.log('.page-container computed maxWidth:', pageContainerStyle.maxWidth)
  }
  
  console.log('==================')
}

let resizeTimer = null

const handleResize = () => {
  if (resizeTimer) {
    clearTimeout(resizeTimer)
  }
  resizeTimer = setTimeout(() => {
    console.log('📏 화면 크기 변경 감지')
    logDimensions()
  }, 100)
}

onMounted(() => {
  console.log('🚀 앱 마운트 완료 - 초기 크기 로그')
  logDimensions()
  
  // Resize 이벤트 리스너 추가 (디바운싱)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (resizeTimer) {
    clearTimeout(resizeTimer)
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style>
@import './assets/gamification.css';
@import './assets/common.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

html {
  font-size: 16px;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans KR', 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;
  color: var(--text-primary);
  line-height: 1.6;
  background: var(--bg-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}

#app {
  width: 100%;
  min-height: 100vh;
  height: 100%;
}
</style>
