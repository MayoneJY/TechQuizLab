<template>
  <div class="home">
    <div class="hero animate-slide-in">
      <div class="hero-icon">
        <IconMonster :size="120" color="#6366f1" />
      </div>
      <h1 class="hero-title">잡몬스터</h1>
      <p class="subtitle">기업 공고를 몬스터로, CS/면접 질문 답변을 공격 스킬로 만드는 취준 레이드 게임</p>
      <div class="features">
        <div class="feature-item">
          <IconSword :size="32" color="#4ecdc4" />
          <span>개인전 연습</span>
        </div>
        <div class="feature-item">
          <IconUsers :size="32" color="#ec4899" />
          <span>레이드 협동</span>
        </div>
        <div class="feature-item">
          <IconTrophy :size="32" color="#f59e0b" />
          <span>능력치 성장</span>
        </div>
      </div>
      <div class="actions">
        <router-link
          v-if="!authStore.isAuthenticated"
          to="/signup"
          class="btn-game btn-game-primary btn-large"
        >
          <IconZap :size="24" color="white" />
          시작하기
        </router-link>
        <router-link v-else to="/dashboard" class="btn-game btn-game-primary btn-large">
          <IconZap :size="24" color="white" />
          대시보드로 이동
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import IconMonster from '@/components/icons/IconMonster.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconZap from '@/components/icons/IconZap.vue'

const authStore = useAuthStore()
</script>

<style scoped>
.home {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 80px);
  width: 100%;
  max-width: 100%;
  padding: 4rem 2rem;
  background: var(--bg-primary);
}

.hero {
  text-align: center;
  width: 100%;
  max-width: 100%;
  padding: 3rem 2rem;
}

.hero-icon {
  margin-bottom: 1.5rem;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.hero-title {
  font-size: 3.5rem;
  margin-bottom: var(--spacing-xl);
  font-weight: 800;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans KR', sans-serif;
  color: var(--text-primary);
  letter-spacing: -0.05em;
  line-height: 1.1;
}

.subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
  margin-bottom: var(--spacing-3xl);
  line-height: 1.7;
  font-weight: 400;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.features {
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  color: var(--text-primary);
  font-weight: 500;
  font-size: 1rem;
  padding: var(--spacing-2xl);
  border-radius: 16px;
  background: var(--bg-card);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary) 0%, var(--secondary) 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

.feature-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(99, 102, 241, 0.05), transparent);
  transition: left 0.6s ease;
}

.feature-item:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
  border-color: var(--border-light);
}

.feature-item:hover::before {
  transform: scaleX(1);
}

.feature-item:hover::after {
  left: 100%;
}

.actions {
  margin-top: 2rem;
}

.btn-large {
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.1rem;
    margin-bottom: 2rem;
  }

  .features {
    gap: 2rem;
    margin-bottom: 2rem;
  }

  .hero-icon {
    margin-bottom: 1rem;
  }

  .btn-large {
    padding: 0.875rem 2rem;
    font-size: 1rem;
  }
}
</style>
