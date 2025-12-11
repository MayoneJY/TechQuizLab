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
          :key="post.postId"
          class="post-item"
          @click="goToDetail(post.postId)"
        >
          <div class="post-header">
            <span class="post-category" :class="post.tags">
              {{ getCategoryLabel(post.tags || '') }}
            </span>
            <h3 class="pixel-text post-title">{{ post.title }}</h3>
          </div>
          <div class="post-content-preview">
            {{ truncateContent(post.content) }}
          </div>
          <div class="post-footer">
            <span class="post-author">작성자 {{ post.nickname || post.userId }}</span>
            <span class="post-date">{{ formatDate(post.createdAt) }}</span>
            <div class="post-stats">
              <span>조회수 {{ post.viewCount }}</span>
              <span>좋아요 {{ getLikeCount(post.boardId, post.postId) }}</span>
              <span>댓글 {{ getCommentCount(post) }}개</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 페이징 UI -->
      <div v-if="boardStore.totalPages > 1" class="pagination">
        <button 
          class="pixel-button pagination-button"
          :disabled="boardStore.currentPage === 1"
          @click="goToPage(boardStore.currentPage - 1)"
        >
          이전
        </button>
        <div class="page-numbers">
          <button
            v-for="page in getPageNumbers()"
            :key="page"
            class="pixel-button page-button"
            :class="{ active: page === boardStore.currentPage }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </div>
        <button 
          class="pixel-button pagination-button"
          :disabled="boardStore.currentPage === boardStore.totalPages"
          @click="goToPage(boardStore.currentPage + 1)"
        >
          다음
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useBoardStore2 } from '../stores/board2'
import { useAuthStore } from '../stores/auth'
import {useBoardPostlikeStore } from '../stores/boardPostlike'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const boardStore = useBoardStore2()
const authStore = useAuthStore()
const boardpostlikeStore = useBoardPostlikeStore()

const selectedCategory = ref('all')

// 게시글 목록 가져오기
onMounted(async () => {
  try {
    await boardStore.fetchAllPosts(1, 10)
    await fetchAllLikeCounts()
  } catch (error) {
    console.error('게시글 목록 로드 실패:', error)
  }
})

// 카테고리 변경 시 게시글 필터링
watch(selectedCategory, async (newCategory) => {
  try {
    // 카테고리 변경 시 첫 페이지로 리셋
    if (newCategory === 'all') {
      await boardStore.fetchAllPosts(1, 10)
    } else {
      await boardStore.fetchPostsByTags(newCategory, 1, 10)
    }
    await fetchAllLikeCounts()
  } catch (error) {
    console.error('게시글 필터링 실패:', error)
  }
})

// 필터링된 게시글 목록
const filteredPosts = computed(() => {
  return boardStore.posts
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

// 카테고리 태그를 한글 이름으로 변환
function getCategoryLabel(category: string) {
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

function getCommentCount(post: any) {
  return post.commentCount || 0
}

function getLikeCount(boardId: number, postId: number) {
  const status = boardpostlikeStore.getLikeStatus(boardId, postId)
  return status?.likeCount ?? 0
}

async function fetchAllLikeCounts() {
  const posts = boardStore.posts
  if (posts.length === 0) return

  try {
    const promises = posts.map(post => 
      boardpostlikeStore.getLikeCount(post.boardId, post.postId)
    )
    await Promise.all(promises)
  } catch (error) {
    console.error('좋아요 개수 조회 실패:', error)
  }
}

// 페이지 이동 함수
async function goToPage(page: number) {
  if (page < 1 || page > boardStore.totalPages) return
  
  try {
    if (selectedCategory.value === 'all') {
      await boardStore.fetchAllPosts(page, 10)
    } else {
      await boardStore.fetchPostsByTags(selectedCategory.value, page, 10)
    }
    await fetchAllLikeCounts()
    // 페이지 상단으로 스크롤
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } catch (error) {
    console.error('페이지 이동 실패:', error)
  }
}

// 페이지 번호 배열 생성 (최대 5개 표시)
function getPageNumbers() {
  const current = boardStore.currentPage
  const total = boardStore.totalPages
  const pages: number[] = []
  
  if (total <= 5) {
    // 전체 페이지가 5개 이하면 모두 표시
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 현재 페이지 기준으로 앞뒤 2개씩 표시
    let start = Math.max(1, current - 2)
    let end = Math.min(total, current + 2)
    
    // 시작이나 끝에 가까우면 조정
    if (end - start < 4) {
      if (start === 1) {
        end = Math.min(total, start + 4)
      } else if (end === total) {
        start = Math.max(1, end - 4)
      }
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
  }
  
  return pages
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
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 10px;
}

.post-category {
  display: inline-block;
  width: max-content;
  padding: 2px 8px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9999px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid #666;
  text-transform: none !important;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  width: 100%;
  line-height: 1.4;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 40px;
  padding: 20px;
}

.pagination-button {
  font-size: 14px;
  padding: 10px 20px;
  min-width: 80px;
}

.pagination-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-button {
  font-size: 14px;
  padding: 10px 16px;
  min-width: 40px;
}

.page-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  box-shadow: 
    0 0 20px rgba(74, 158, 255, 0.6),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
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

/* Override text-transform for Korean text */
.pixel-text,
.pixel-button {
  text-transform: none !important;
}
</style>
