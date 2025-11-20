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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
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
  font-size: 4.5rem;
  margin-bottom: 1.5rem;
  font-weight: 900;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
  line-height: 1.1;
}

.subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
  margin-bottom: 3rem;
  line-height: 1.8;
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
  gap: 1rem;
  color: var(--text-primary);
  font-weight: 600;
  font-size: 1.05rem;
  padding: 1.5rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.feature-item:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-lg);
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
