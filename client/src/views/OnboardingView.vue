<template>
  <div class="onboarding">
    <div class="onboarding-container">
      <div class="onboarding-header">
        <div class="logo-section">
          <IconMonster :size="64" :color="primary" />
          <h1 class="app-title">잡몬스터</h1>
        </div>
        <p class="app-subtitle">기업 공고를 몬스터로, 면접 질문을 전투로 만드는 취준 레이드 게임</p>
      </div>

      <div class="features-section">
        <div class="feature-card" v-for="(feature, index) in features" :key="index">
          <div class="feature-icon">
            <component :is="feature.icon" :size="48" :color="feature.color" />
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-description">{{ feature.description }}</p>
        </div>
      </div>

      <div class="how-it-works">
        <h2 class="section-title">어떻게 작동하나요?</h2>
        <div class="steps">
          <div class="step" v-for="(step, index) in steps" :key="index">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <h3 class="step-title">{{ step.title }}</h3>
              <p class="step-description">{{ step.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="cta-section">
        <h2 class="cta-title">지금 시작하세요!</h2>
        <p class="cta-description">무료로 가입하고 취준 레이드를 시작해보세요</p>
        <div class="cta-buttons">
          <router-link to="/signup" class="btn btn-primary btn-large">
            <IconZap :size="24" color="white" />
            무료로 시작하기
          </router-link>
          <router-link to="/login" class="btn btn-secondary btn-large">
            이미 계정이 있나요?
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import IconMonster from '@/components/icons/IconMonster.vue'
import IconSword from '@/components/icons/IconSword.vue'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconTrophy from '@/components/icons/IconTrophy.vue'
import IconGem from '@/components/icons/IconGem.vue'
import IconZap from '@/components/icons/IconZap.vue'
import { ref } from 'vue'

const primary = '#6366f1'
const secondary = '#8b5cf6'
const warning = '#f59e0b'
const success = '#10b981'

const features = ref([
  {
    icon: IconSword,
    color: primary,
    title: '개인전 연습',
    description: '혼자서 몬스터와 전투하며 면접 질문에 답변하는 연습을 할 수 있습니다.',
  },
  {
    icon: IconUsers,
    color: secondary,
    title: '레이드 협동',
    description: '최대 4명이 함께 레이드에 참가하여 협동으로 강한 몬스터를 물리칩니다.',
  },
  {
    icon: IconTrophy,
    color: warning,
    title: '능력치 성장',
    description: '전투를 통해 XP를 획득하고 다양한 능력치를 레벨업할 수 있습니다.',
  },
  {
    icon: IconGem,
    color: success,
    title: '실시간 피드백',
    description: 'AI가 답변을 평가하고 개선점을 제시하여 실력을 향상시킵니다.',
  },
])

const steps = ref([
  {
    title: '몬스터 선택',
    description: '원하는 기업의 공고를 몬스터로 선택합니다.',
  },
  {
    title: '질문에 답변',
    description: '면접 질문에 대한 답변을 작성합니다.',
  },
  {
    title: 'AI 평가',
    description: 'AI가 답변을 평가하고 데미지와 XP를 계산합니다.',
  },
  {
    title: '능력치 성장',
    description: '획득한 XP로 능력치를 레벨업하고 더 강해집니다.',
  },
])
</script>

<style scoped>
.onboarding {
  min-height: 100vh;
  background: var(--bg-primary);
  padding: var(--spacing-3xl) var(--spacing-xl);
}

.onboarding-container {
  max-width: 1200px;
  margin: 0 auto;
}

.onboarding-header {
  text-align: center;
  margin-bottom: var(--spacing-3xl);
  padding: var(--spacing-3xl) 0;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.app-title {
  font-size: 3.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.05em;
}

.app-subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.7;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-3xl);
}

.feature-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-2xl);
  text-align: center;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
  transition: all 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-light);
}

.feature-icon {
  margin-bottom: var(--spacing-lg);
  display: flex;
  justify-content: center;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.feature-description {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

.how-it-works {
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-3xl);
  margin-bottom: var(--spacing-3xl);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  text-align: center;
  margin: 0 0 var(--spacing-2xl) 0;
}

.steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.step {
  display: flex;
  gap: var(--spacing-lg);
  align-items: flex-start;
}

.step-number {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.step-description {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
}

.cta-section {
  text-align: center;
  background: var(--bg-card);
  border-radius: 16px;
  padding: var(--spacing-3xl);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
}

.cta-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.cta-description {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin: 0 0 var(--spacing-2xl) 0;
}

.cta-buttons {
  display: flex;
  gap: var(--spacing-lg);
  justify-content: center;
  flex-wrap: wrap;
}

.btn-large {
  padding: 1rem 2rem;
  font-size: 1.125rem;
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
}

@media (max-width: 768px) {
  .app-title {
    font-size: 2.5rem;
  }

  .features-section {
    grid-template-columns: 1fr;
  }

  .steps {
    grid-template-columns: 1fr;
  }

  .cta-buttons {
    flex-direction: column;
  }

  .btn-large {
    width: 100%;
    justify-content: center;
  }
}
</style>

