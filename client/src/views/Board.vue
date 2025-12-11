<template>
  <div class="board-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-page-wrapper">
       <!-- HEADER: Title -->
      <header class="dashboard-header">
        <h1 class="game-title pixel-text">
          <span class="title-main glitch" data-text="Community">Community</span>
        </h1>
      </header>
      
      <div class="board-content-area">
        
        <!-- Toolbar: Search, Sort, Write -->
        <div class="board-toolbar glass-panel">
            <button class="pixel-button link-button home-toolbar-btn" @click="goHome">
                <span class="btn-icon">🏡</span> 메인
            </button>
            <div class="search-container">
                <!-- <span class="search-icon">🔍</span> -->
                <input 
                    v-model="searchKeyword" 
                    @keyup.enter="handleSearch"
                    type="text" 
                    class="glass-input search-input" 
                    placeholder="검색어를 입력하세요..."
                >

            </div>

            <div class="toolbar-actions">
                 <div class="sort-dropdown-wrapper">
                    <select v-model="sortOption" @change="handleSort" class="glass-input sort-select">
                        <option value="latest">최신순</option>
                        <option value="viewCount">조회순</option>
                        <!-- <option value="likeCount">좋아요순</option> -->
                    </select>
                 </div>

              </div>
                <button class="pixel-button search-btn" @click="handleSearch">
                   검색
                </button>
        </div>

        <!-- Category Tabs (Centered) -->
        <div class="category-tabs glass-panel centered-tabs">
            <button 
              class="pixel-button tab-button" 
              :class="{ active: selectedCategory === 'all' }"
              @click="handleCategoryChange('all')"
            >
              전체
            </button>
            <button 
              class="pixel-button tab-button" 
              :class="{ active: selectedCategory === 'general' }"
              @click="handleCategoryChange('general')"
            >
              일반
            </button>
            <button 
              class="pixel-button tab-button" 
              :class="{ active: selectedCategory === 'question' }"
              @click="handleCategoryChange('question')"
            >
              질문
            </button>
            <button 
              class="pixel-button tab-button" 
              :class="{ active: selectedCategory === 'tip' }"
              @click="handleCategoryChange('tip')"
            >
              팁
            </button>
            <button 
              class="pixel-button tab-button" 
              :class="{ active: selectedCategory === 'free' }"
              @click="handleCategoryChange('free')"
            >
              자유
            </button>
            <!-- <div class="divider-vertical"></div> -->
            <button class="pixel-button transparent-write-btn" @click="goToWrite">
                <span class="btn-icon">✨</span> 글쓰기
            </button>
        </div>

        <!-- Error Message -->
        <div v-if="boardStore.error" class="error-state pixel-text">
            <h3>데이터 로드 실패</h3>
            <p>{{ boardStore.error }}</p>
            <button class="pixel-button secondary" @click="loadPosts(1)">다시 시도</button>
        </div>

        <!-- Post List -->
        <div class="posts-list">
            <!-- Loading Skeleton -->
            <div v-if="boardStore.isLoading" class="skeleton-list">
                <div v-for="i in 5" :key="i" class="post-item glass-card skeleton-item" style="margin-bottom: 16px;">
                    <!-- Header Row: [Category] [Title] ........ [Author] [Date] -->
                    <div class="post-header-row" style="margin-bottom: 8px;">
                        <!-- Category Badge -->
                        <div class="skeleton skeleton-text" style="width: 50px; height: 30px; border-radius: 6px; margin: 0;"></div>
                        <!-- Title -->
                        <div class="skeleton skeleton-title" style="flex: 1; height: 24px; margin: 0;"></div>
                        <!-- Meta Right -->
                        <div style="display: flex; gap: 15px; margin-left: auto;">
                             <div class="skeleton skeleton-text" style="width: 60px; height: 16px; margin: 0;"></div>
                             <div class="skeleton skeleton-text" style="width: 40px; height: 16px; margin: 0;"></div>
                        </div>
                    </div>

                    <!-- Content Preview -->
                    <div style="display: flex; flex-direction: column; gap: 6px;">
                        <!-- <div class="skeleton skeleton-text" style="width: 100%;"></div> -->
                        <div class="skeleton skeleton-text" style="width: 70%;"></div>
                    </div>
                    
                    <!-- Footer Stats -->
                    <div class="">
                        <div class="post-stats">
                             <div class="skeleton skeleton-text" style="width: 30px; height: 14px; margin: 0;"></div>
                             <div class="skeleton skeleton-text" style="width: 30px; height: 14px; margin: 0;"></div>
                             <div class="skeleton skeleton-text" style="width: 30px; height: 14px; margin: 0;"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div v-else-if="filteredPosts.length === 0" class="empty-state glass-card">
                <div class="empty-icon-wrapper">
                    <span class="empty-icon">🛸</span>
                </div>
                <h3 class="pixel-text empty-title">게시글이 존재하지 않습니다</h3>
                <p class="empty-desc">새로운 이야기를 시작해보세요!</p>
                <div class="empty-actions">
                     <button class="pixel-button premium-write-btn" @click="goToWrite">
                        <span class="btn-icon">✨</span> 첫 게시글 작성하기
                     </button>
                     <button v-if="searchKeyword" class="pixel-button secondary" @click="handleResetSearch">
                        🔄 검색 초기화
                     </button>
                </div>
            </div>
            <div
              v-for="post in filteredPosts"
              :key="post.postId"
              class="post-item glass-card clickable-card"
              @click="goToDetail(post.postId)"
            >
              <div class="post-header-row">
                 <span class="post-category pixel-text" :class="post.tags">
                  {{ getCategoryLabel(post.tags || '') }}
                </span>
                <h3 class="pixel-text post-title">{{ post.title }}</h3>
                <div class="post-meta-right">
                    <span class="author-name">{{ post.nickname || post.userId }}</span>
                    <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                </div>
              </div>
              
              <div class="post-content-preview">
                {{ truncateContent(post.content) }}
              </div>
              
              <div class="post-footer">
                <div class="post-stats">
                  <div class="stat-item">
                     <span class="stat-icon">👁</span> {{ post.viewCount }}
                  </div>
                  <div class="stat-item">
                     <span class="stat-icon">❤️</span> {{ getLikeCount(post.boardId, post.postId) }}
                  </div>
                  <div class="stat-item">
                     <span class="stat-icon">💬</span> {{ getCommentCount(post) }}
                  </div>
                </div>
              </div>
            </div>
        </div>

        <!-- Pagination -->
        <div v-if="boardStore.totalPages > 1" class="pagination-container">
            <button 
              class="pagination-nav-btn prev"
              :disabled="boardStore.currentPage === 1"
              @click="goToPage(boardStore.currentPage - 1)"
            >
              &lt;
            </button>
            
            <div class="page-numbers">
              <button
                v-for="page in getPageNumbers()"
                :key="page"
                class="page-number-btn pixel-text"
                :class="{ active: page === boardStore.currentPage }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>
            </div>
            
            <button 
              class="pagination-nav-btn next"
              :disabled="boardStore.currentPage === boardStore.totalPages"
              @click="goToPage(boardStore.currentPage + 1)"
            >
              &gt;
            </button>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useBoardStore2 } from '../stores/board2'
import { useAuthStore } from '../stores/auth'
import {useBoardPostlikeStore } from '../stores/boardPostlike'
import { useModalStore } from '../stores/modal'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const boardStore = useBoardStore2()
const authStore = useAuthStore()
const boardpostlikeStore = useBoardPostlikeStore()
const modalStore = useModalStore()

const selectedCategory = ref('all')
const searchKeyword = ref('')
const sortOption = ref('latest')

// 게시글 목록 가져오기
onMounted(async () => {
    // Reset store state on mount
    boardStore.currentSearch = '';
    boardStore.currentSort = 'latest';
    searchKeyword.value = ''; // Ensure local state also reset
    sortOption.value = 'latest';
    
  try {
    await boardStore.fetchAllPosts(1, 10)
    await fetchAllLikeCounts()
  } catch (error) {
    console.error('게시글 목록 로드 실패:', error)
  }
})

// 검색 초기화
async function handleResetSearch() {
    searchKeyword.value = '';
    boardStore.currentSearch = '';
    await loadPosts(1);
}

// 카테고리 변경 함수
async function handleCategoryChange(category: string) {
    selectedCategory.value = category;
}


// 검색 핸들러
async function handleSearch() {
    boardStore.currentSearch = searchKeyword.value;
    // Reset page to 1
    await loadPosts(1);
}

// 정렬 핸들러
async function handleSort() {
    boardStore.currentSort = sortOption.value;
    await loadPosts(1);
}

// 통합 로드 함수
async function loadPosts(page: number) {
    try {
        if (selectedCategory.value === 'all') {
            await boardStore.fetchAllPosts(page, 10);
        } else {
            await boardStore.fetchPostsByTags(selectedCategory.value, page, 10);
        }
        await fetchAllLikeCounts();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (e) {
        console.error('게시글 로드 실패', e);
    }
}

// 카테고리 변경 시 게시글 필터링
watch(selectedCategory, async () => {
  // 카테고리 변경 시 검색어는 유지하되 페이지는 1로 리셋
  await loadPosts(1);
})

// 필터링된 게시글 목록
const filteredPosts = computed(() => {
  return boardStore.posts
})

function goHome() {
  router.push('/')
}

function goToWrite() {
    if (!authStore.isAuthenticated) {
        modalStore.openAlert("로그인이 필요한 서비스입니다.");
        return;
    }
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

function truncateContent(content: string, maxLength: number = 80) {
    if (!content) return '';
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
  /* padding: 20px; */
  /* padding-bottom: 40px; */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* animation: screen-enter 0.8s ease-out; */
}

/* Reusing dashboard layout styles from global/Home */
/* Global/Page Layout Override */
.board-page-wrapper {
    width: 100%;
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px 20px 40px 20px;
    z-index: 10;
    position: relative;
    display: flex;
    flex-direction: column;
}

/* Renamed from dashboard-content/board-layout to avoid ALL global conflicts */
.board-content-area {
  display: flex !important;
  flex-direction: column !important;
  gap: 20px;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

/* Toolbar Styles */
.board-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    margin-bottom: 20px;
    gap: 20px;
    border-radius: 12px;
    flex-wrap: wrap; /* responsive */
}

.search-container {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
    min-width: 200px;
    position: relative;
}

.search-icon {
    font-size: 20px;
}

.search-input {
    width: 100%;
    /* glass-input handles padding/border */
}

.toolbar-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.sort-dropdown-wrapper {
    min-width: 100px;
}

.sort-select {
    cursor: pointer;
}



.home-toolbar-btn {
    min-width: auto;
    padding: 12px 20px; /* Increased padding */
    font-size: 14px; /* Adjusted size */
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255,255,255,0.2);
    display: flex;
    align-items: center;
    gap: 8px;
    height: 46px; /* Explicit height to match inputs */
}

.search-btn {
    padding: 0 20px;
    height: 46px;
    min-width: auto;
    font-size: 14px;
    white-space: nowrap;
}

/* Transparent Write Button in Tabs */
.divider-vertical {
    width: 1px;
    height: 24px;
    background: rgba(255,255,255,0.2);
    margin: 0 8px;
    margin-left: auto; /* Push to right */
}

.transparent-write-btn {
    background: transparent;
    border: 1px solid rgba(255,255,255,0.2);
    color: #ccc;
    font-size: 13px;
    padding: 8px 16px;
    min-width: auto;
    display: flex;
    align-items: center;
    gap: 6px;
    box-shadow: none; /* No default shadow for cleaner look */
    margin-left: auto; /* Push to right */
}

.transparent-write-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    border-color: #fff;
    transform: translateY(-2px);
}

.home-toolbar-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
}

.dashboard-header {
    text-align: center;
    margin-bottom: 30px;
}

.title-main {
  font-size: 60px;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  font-weight: 900;
}



/* Left Column */
.main-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* Category Tabs */
.category-tabs {
    padding: 10px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    border-radius: 12px;
}

.tab-button {
  font-size: 13px;
  padding: 8px 16px;
  min-width: 60px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-width: 2px;
  box-shadow: none;
}

.tab-button:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
}

.tab-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  border-color: #fff;
  box-shadow: 0 0 15px rgba(74, 158, 255, 0.5);
  transform: translateY(-2px);
}

/* Post List */
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  padding: 20px;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
  overflow: hidden;
}

.post-item::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: transparent;
    transition: background 0.3s;
}

.post-item:hover {
    border-color: rgba(74, 158, 255, 0.5);
}

.post-item:hover::before {
    background: #4a9eff;
}

.post-header-row {
    display: flex;
    align-items: center;
    gap: 10px;
}

.post-category {
  padding: 4px 8px;
  font-size: 11px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  font-weight: normal;
}

.post-category.general { color: #4a9eff; border-color: #4a9eff; background: rgba(74, 158, 255, 0.15); }
.post-category.question { color: #ffd43b; border-color: #ffd43b; background: rgba(255, 212, 59, 0.15); }
.post-category.tip { color: #51cf66; border-color: #51cf66; background: rgba(81, 207, 102, 0.15); }
.post-category.free { color: #ff6b6b; border-color: #ff6b6b; background: rgba(255, 107, 107, 0.15); }


.post-title {
  font-size: 18px;
  margin: 0;
  color: #fff;
  flex: 1;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.post-meta-right {
    display: flex;
    align-items: center;
    gap: 15px; /* Spacing between Author and Date */
    font-size: 13px;
    color: #aaa;
    margin-left: auto; /* Push to right */
    white-space: nowrap;
}

.author-name {
    color: #4a9eff;
    font-weight: 600;
}

.post-date {
    color: #888;
}

.post-content-preview {
  font-size: 13px;
  color: #ccc;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #888;
    margin-top: 5px;
}

.author-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.author-name {
    color: #4a9eff;
    font-weight: 600;
}

.post-stats {
    display: flex;
    gap: 12px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
}

/* Pagination */
/* Pagination */
.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    padding: 20px;
    margin-top: 20px;
}

.pagination-nav-btn {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
}

.pagination-nav-btn:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
}

.pagination-nav-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.page-numbers {
    display: flex;
    gap: 8px;
}

.page-number-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid transparent;
    background: transparent;
    color: #ccc;
    cursor: pointer;
    transition: all 0.2s;
    font-size: 14px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.page-number-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
}

.page-number-btn.active {
    background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
    color: #fff;
    border: 1px solid #fff;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
    transform: translateY(-2px);
    font-weight: bold;
}

/* Widget Column */
.widget-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.dashboard-widget {
    padding: 20px;
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.action-widget {
    gap: 10px;
}

.write-btn {
    width: 100%;
}

.back-btn {
    width: 100%;
    background: transparent;
    border-color: #fff;
}

.back-btn:hover {
    background: rgba(255, 255, 255, 0.1);
}

.widget-title {
    font-size: 16px;
    color: #ffd43b;
    margin: 0;
    margin-bottom: 10px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 5px;
}

.rule-content p {
    font-size: 13px;
    color: #ccc;
    margin-bottom: 8px;
    line-height: 1.4;
}


/* Animations & Decorations */
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

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
    .board-content-area {
        width: 100%;
    }
    
    .title-main {
        font-size: 36px;
    }

    .post-title {
        font-size: 16px;
    }
}
/* Empty State Styles */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    text-align: center;
    border-radius: 20px;
    border: 2px dashed rgba(255, 255, 255, 0.2);
    background: rgba(0, 0, 0, 0.2);
}

.empty-icon-wrapper {
    font-size: 60px;
    margin-bottom: 20px;
    animation: float 3s ease-in-out infinite;
}

.empty-title {
    font-size: 24px;
    color: #ffd43b;
    margin-bottom: 10px;
}

.empty-desc {
    color: #ccc;
    margin-bottom: 30px;
}

.empty-actions {
    display: flex;
    gap: 15px;
}

.secondary {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid #fff;
}

.secondary:hover {
    background: rgba(255, 255, 255, 0.2);
}

.error-state {
    padding: 40px;
    background: rgba(255, 0, 0, 0.2);
    border: 1px solid #ff6b6b;
    border-radius: 12px;
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}
</style>
