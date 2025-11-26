<template>
  <div class="board-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-container">
      <div class="header">
        <h1 class="pixel-text title">게시판</h1>
        <div class="header-actions">
          <button class="pixel-button back-button" @click="goHome">
            ← 홈으로
          </button>
          <button 
            v-if="authStore.isAuthenticated" 
            class="pixel-button write-button" 
            @click="goToWrite"
          >
            글쓰기
          </button>
        </div>
      </div>

      <div class="category-tabs">
        <button 
          class="pixel-button tab-button" 
          :class="{ active: selectedCategory === 'all' }"
          @click="selectedCategory = 'all'"
        >
          전체
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: selectedCategory === 'general' }"
          @click="selectedCategory = 'general'"
        >
          일반
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: selectedCategory === 'question' }"
          @click="selectedCategory = 'question'"
        >
          질문
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: selectedCategory === 'tip' }"
          @click="selectedCategory = 'tip'"
        >
          팁
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: selectedCategory === 'free' }"
          @click="selectedCategory = 'free'"
        >
          자유
        </button>
      </div>

      <div class="posts-list">
        <div v-if="filteredPosts.length === 0" class="empty-message pixel-text">
          게시글이 없습니다.
        </div>
        <div
          v-for="post in filteredPosts"
          :key="post.id"
          class="post-item"
          @click="goToDetail(post.id)"
        >
          <div class="post-header">
            <span class="post-category" :class="post.category">
              {{ getCategoryName(post.category) }}
            </span>
            <h3 class="pixel-text post-title">{{ post.title }}</h3>
          </div>
          <div class="post-content-preview">
            {{ truncateContent(post.content) }}
          </div>
          <div class="post-footer">
            <span class="post-author">{{ post.author }}</span>
            <span class="post-date">{{ formatDate(post.createdAt) }}</span>
            <div class="post-stats">
              <span>👁 {{ post.views }}</span>
              <span>❤️ {{ post.likes }}</span>
              <span>💬 {{ getCommentCount(post.id) }}</span>
            </div>
          </div>
        </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBoardStore } from '../stores/board'
import { useAuthStore } from '../stores/auth'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const boardStore = useBoardStore()
const authStore = useAuthStore()

const selectedCategory = ref<'all' | 'general' | 'question' | 'tip' | 'free'>('all')

const filteredPosts = computed(() => {
  if (selectedCategory.value === 'all') {
    return boardStore.getPosts()
  }
  return boardStore.getPosts(selectedCategory.value)
})

function goHome() {
  router.push('/')
}

function goToWrite() {
  router.push('/board/write')
}

function goToDetail(id: number) {
  router.push(`/board/${id}`)
}

function getCategoryName(category: string) {
  const names: Record<string, string> = {
    general: '일반',
    question: '질문',
    tip: '팁',
    free: '자유'
  }
  return names[category] || category
}

function truncateContent(content: string, maxLength: number = 100) {
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

function formatDate(dateString: string) {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '방금 전'
  if (minutes < 60) return `${minutes}분 전`
  if (hours < 24) return `${hours}시간 전`
  if (days < 7) return `${days}일 전`
  return date.toLocaleDateString('ko-KR')
}

function getCommentCount(postId: number) {
  return boardStore.getComments(postId).length
}
</script>

<style scoped>
.board-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: screen-enter 0.8s ease-out;
}

.board-container {
  width: 100%;
  max-width: 1000px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
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

.header-actions {
  display: flex;
  gap: 10px;
}

.back-button,
.write-button {
  font-size: 14px;
  padding: 12px 24px;
}

.category-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  justify-content: center;
}

.tab-button {
  font-size: 14px;
  padding: 12px 20px;
  min-width: 80px;
  transition: all 0.3s;
}

.tab-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  box-shadow: 
    0 0 20px rgba(74, 158, 255, 0.6),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.post-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.post-item:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.post-item:hover::before {
  left: 100%;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.post-category {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid #666;
}

.post-category.general {
  background: rgba(74, 158, 255, 0.3);
  border-color: #4a9eff;
  color: #4a9eff;
}

.post-category.question {
  background: rgba(255, 212, 59, 0.3);
  border-color: #ffd43b;
  color: #ffd43b;
}

.post-category.tip {
  background: rgba(81, 207, 102, 0.3);
  border-color: #51cf66;
  color: #51cf66;
}

.post-category.free {
  background: rgba(255, 107, 107, 0.3);
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  flex: 1;
}

.post-content-preview {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-footer {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
  color: #888;
  flex-wrap: wrap;
}

.post-author {
  font-weight: 600;
  color: #4a9eff;
}

.post-date {
  color: #888;
}

.post-stats {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.post-stats span {
  color: #ccc;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  font-weight: 500;
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
  
  .category-tabs {
    gap: 8px;
  }
  
  .tab-button {
    min-width: 60px;
    font-size: 12px;
    padding: 10px 15px;
  }
  
  .post-item {
    padding: 15px;
  }
  
  .post-title {
    font-size: 16px;
  }
}
</style>

