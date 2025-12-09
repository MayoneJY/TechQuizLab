<template>
  <div class="topic-selection-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="topic-selection-container">
      <div class="header">
        <h1 class="pixel-text title">주제 선택</h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div v-if="topicStore.isLoading" class="topics-grid">
        <div v-for="i in 3" :key="i" class="topic-card skeleton-card">
          <div class="skeleton skeleton-icon"></div>
          <div class="skeleton skeleton-text title-skeleton"></div>
          <div class="skeleton skeleton-text desc-skeleton"></div>
          <div class="skeleton skeleton-text desc-skeleton short"></div>
        </div>
      </div>

      <div v-else-if="topicStore.topics.length === 0" class="empty-message pixel-text">
        주제가 없습니다.
      </div>

      <div v-else class="topics-grid">
        <div
          v-for="topic in topicStore.topics"
          :key="topic.id"
          class="topic-card"
          :class="{ selected: selectedTopicId === topic.id }"
          @click="selectTopic(topic.id)"
        >
          <div class="topic-icon">📚</div>
          <h3 class="pixel-text topic-name">{{ topic.name }}</h3>
          <p class="topic-description">{{ topic.description || '주제 설명이 없습니다.' }}</p>
          <div v-if="selectedTopicId === topic.id" class="selected-badge">
            ✓ 선택됨
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <button
          class="pixel-button start-button"
          :disabled="!selectedTopicId || gameStore.isLoading"
          @click="startGame"
        >
          게임 시작
        </button>
      </div>
    </div>

    <!-- Spaceship decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
    </div>
    
    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
    <div class="monster monster-2">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTopicStore } from '../stores/topic'
import { useGameStore } from '../stores/game'
import { useAuthStore } from '../stores/auth'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const topicStore = useTopicStore()
const gameStore = useGameStore()
const authStore = useAuthStore()

const selectedTopicId = ref<number | null>(null)

onMounted(async () => {
  try {
    await topicStore.fetchTopics()
    if (topicStore.topics.length > 0) {
      selectedTopicId.value = topicStore.topics[0].id
    }
  } catch (error) {
    console.error('Failed to load topics:', error)
  }
})

function goHome() {
  router.push('/')
}

function selectTopic(topicId: number) {
  selectedTopicId.value = topicId
}

async function startGame() {
  if (!authStore.isAuthenticated) {
    if (confirm('로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?')) {
      router.push('/login')
    }
    return
  }

  if (!selectedTopicId.value) {
    alert('주제를 선택해주세요.')
    return
  }

  try {
    await gameStore.setTopicId(selectedTopicId.value)

    // 문제가 로드되었는지 확인
    if (gameStore.totalQuestions === 0) {
      alert('해당 주제에 문제가 없습니다.')
      return
    }

    router.push('/game')
  } catch (error: any) {
    console.error('Failed to start game:', error)
    const errorMsg = error.serverMessage || error.message || '게임을 시작할 수 없습니다.'
    alert(errorMsg)
  }
}
</script>

<style scoped>
.topic-selection-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: screen-enter 0.8s ease-out;
}

.topic-selection-container {
  width: 100%;
  max-width: 1200px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 
    4px 4px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-text {
  color: #4a9eff;
  font-size: 18px;
  font-weight: 600;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  font-weight: 500;
}

.topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.topic-card {
  position: relative;
  padding: 25px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
  box-sizing: border-box;
}

.topic-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.topic-card:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-5px);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.topic-card:hover::before {
  left: 100%;
}

.topic-card.selected {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.2);
  box-shadow: 0 0 25px rgba(255, 212, 59, 0.5);
}

.topic-card.selected::before {
  background: linear-gradient(90deg, transparent, rgba(255, 212, 59, 0.2), transparent);
}

.topic-icon {
  font-size: 48px;
  text-align: center;
  margin-bottom: 15px;
  filter: drop-shadow(2px 2px 0 #000);
}

.topic-name {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 10px;
  text-align: center;
  word-break: keep-all;
}

.topic-card.selected .topic-name {
  color: #ffd43b;
}

.topic-description {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
  text-align: center;
  word-break: keep-all;
  margin-bottom: 10px;
}

.topic-card.selected .topic-description {
  color: #fff;
}

.selected-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 6px 12px;
  background: rgba(255, 212, 59, 0.9);
  border: 2px solid #ffd43b;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  color: #000;
  text-shadow: none;
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.start-button {
  font-size: 18px;
  padding: 18px 50px;
  min-width: 200px;
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  box-shadow: 
    0 0 30px rgba(74, 158, 255, 0.6),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
}

.start-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: linear-gradient(135deg, #666 0%, #555 100%);
}

.start-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 
    0 0 40px rgba(74, 158, 255, 0.8),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
}

.stars-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.spaceship {
  position: fixed;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
}

.monster {
  position: fixed;
  z-index: 3;
  animation: float 2s ease-in-out infinite;
}

.monster-1 {
  top: 100px;
  left: 50px;
  animation-delay: 0s;
}

.monster-2 {
  top: 200px;
  right: 100px;
  animation-delay: 1s;
}

@keyframes screen-enter {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }
  
  .topics-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .topic-card {
    padding: 20px;
  }
  
  .topic-icon {
    font-size: 36px;
  }
  
  .topic-name {
    font-size: 18px;
  }
}


/* Skeleton Styles */
.skeleton-card {
  cursor: default;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.05);
  border-color: #444;
}

.skeleton-card:hover {
  transform: none;
  box-shadow: none;
  background: rgba(255, 255, 255, 0.05);
  border-color: #444;
}

.skeleton {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.skeleton::after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  transform: translateX(-100%);
  background-image: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0,
    rgba(255, 255, 255, 0.1) 20%,
    rgba(255, 255, 255, 0.2) 60%,
    rgba(255, 255, 255, 0)
  );
  animation: shimmer 1.5s infinite;
}

.skeleton-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin: 0 auto 15px;
}

.title-skeleton {
  width: 60%;
  height: 24px;
  margin: 0 auto 10px;
}

.desc-skeleton {
  width: 90%;
  height: 16px;
  margin: 0 auto 8px;
}

.desc-skeleton.short {
  width: 70%;
}

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}
</style>

