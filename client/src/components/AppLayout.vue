<template>
  <div class="app-layout">
    <header class="header">
      <div class="container">
        <div class="header-content">
          <router-link to="/dashboard" class="logo">
            <IconMonster :size="32" color="#6c5ce7" />
            <h1>잡몬스터</h1>
          </router-link>
          <nav class="nav">
            <router-link to="/dashboard" class="nav-link">
              <IconChart :size="20" color="currentColor" />
              <span>대시보드</span>
            </router-link>
            <router-link to="/monsters" class="nav-link">
              <IconMonster :size="20" color="currentColor" />
              <span>몬스터</span>
            </router-link>
            <router-link to="/raids" class="nav-link">
              <IconRaid :size="20" color="currentColor" />
              <span>레이드</span>
            </router-link>
            <router-link to="/board" class="nav-link">
              <IconStar :size="20" color="currentColor" />
              <span>게시판</span>
            </router-link>
            <router-link to="/profile" class="nav-link">
              <IconTrophy :size="20" color="currentColor" />
              <span>프로필</span>
            </router-link>
            <button
              v-if="authStore.isAuthenticated"
              @click="handleLogout"
              class="btn btn-secondary"
            >
              로그아웃
            </button>
            <router-link v-else to="/login" class="btn btn-primary">
              로그인
            </router-link>
          </nav>
          <button class="mobile-menu-btn" @click="toggleMobileMenu" :class="{ active: showMobileMenu }">
            <span></span>
            <span></span>
            <span></span>
          </button>
        </div>
        <nav v-if="showMobileMenu" class="mobile-nav">
          <router-link to="/dashboard" @click="toggleMobileMenu" class="mobile-nav-link">
            <IconChart :size="20" color="currentColor" />
            <span>대시보드</span>
          </router-link>
          <router-link to="/monsters" @click="toggleMobileMenu" class="mobile-nav-link">
            <IconMonster :size="20" color="currentColor" />
            <span>몬스터</span>
          </router-link>
          <router-link to="/raids" @click="toggleMobileMenu" class="mobile-nav-link">
            <IconRaid :size="20" color="currentColor" />
            <span>레이드</span>
          </router-link>
          <router-link to="/board" @click="toggleMobileMenu" class="mobile-nav-link">
            <IconStar :size="20" color="currentColor" />
            <span>게시판</span>
          </router-link>
          <router-link to="/profile" @click="toggleMobileMenu" class="mobile-nav-link">
            <IconTrophy :size="20" color="currentColor" />
            <span>프로필</span>
          </router-link>
          <button
            v-if="authStore.isAuthenticated"
            @click="handleLogout"
            class="btn btn-secondary mobile-btn"
          >
            로그아웃
          </button>
          <router-link v-else to="/login" @click="toggleMobileMenu" class="btn btn-primary mobile-btn">
            로그인
          </router-link>
        </nav>
      </div>
    </header>
    <main class="main">
      <div class="container">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import IconMonster from './icons/IconMonster.vue'
import IconChart from './icons/IconChart.vue'
import IconStar from './icons/IconStar.vue'
import IconTrophy from './icons/IconTrophy.vue'
import IconRaid from './icons/IconRaid.vue'

const authStore = useAuthStore()
const router = useRouter()
const showMobileMenu = ref(false)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
  showMobileMenu.value = false
}

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
  width: 100%;
}

.header {
  background: var(--bg-card);
  padding: var(--spacing-lg) 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: var(--shadow-sm);
  border-bottom: 1px solid var(--border);
  transition: all 0.2s ease;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.8);
}

.header:hover {
  box-shadow: var(--shadow-md);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  color: var(--text-primary);
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans KR', sans-serif;
  color: var(--text-primary);
  letter-spacing: -0.025em;
  transition: all 0.2s ease;
}

.logo:hover h1 {
  filter: brightness(1.1);
  transform: scale(1.05);
}

.nav {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid transparent;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: transparent;
}

.nav-link::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--gradient-primary);
  transform: translateX(-50%);
  transition: width 0.3s ease;
}

.nav-link:hover {
  color: var(--primary);
  background: rgba(99, 102, 241, 0.1);
}

.nav-link.router-link-active {
  color: var(--primary);
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.2);
}

.mobile-menu-btn {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  z-index: 1001;
}

.mobile-menu-btn span {
  width: 25px;
  height: 3px;
  background: #6c5ce7;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.mobile-menu-btn.active span:nth-child(1) {
  transform: rotate(45deg) translate(8px, 8px);
}

.mobile-menu-btn.active span:nth-child(2) {
  opacity: 0;
}

.mobile-menu-btn.active span:nth-child(3) {
  transform: rotate(-45deg) translate(7px, -7px);
}

.mobile-nav {
  display: none;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 2px solid var(--border);
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  color: #666;
  font-weight: 600;
  padding: 0.875rem 1rem;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.mobile-nav-link:hover,
.mobile-nav-link.router-link-active {
  color: #6c5ce7;
  background: rgba(108, 92, 231, 0.15);
}

.mobile-btn {
  margin-top: 0.5rem;
  width: 100%;
  justify-content: center;
}

.main {
  flex: 1;
  padding: 0;
  min-height: calc(100vh - 80px);
  width: 100%;
  max-width: 100%;
}

.container {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .mobile-nav {
    display: flex;
  }

  .logo h1 {
    font-size: 1.5rem;
  }

  .main {
    padding: 0;
  }

  .container {
    padding: 0;
  }
}
</style>
