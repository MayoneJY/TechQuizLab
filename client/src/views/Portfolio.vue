<template>
  <div class="portfolio-screen">

    
    <div class="portfolio-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="포트폴리오">포트폴리오</span>
        </h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div class="content-wrapper">
        <!-- Portfolio List -->
        <div class="portfolio-list-section glass-panel" :class="{ 'collapsed': viewMode === 'editor' }">
          <div class="list-header">
            <h2 class="pixel-text section-title">MY PROJECTS</h2>
          <transition name="fade-delay">
            <button class="pixel-button add-btn small" @click="startNewPortfolio" v-if="viewMode === 'list'">
              + 새 항목
            </button>
          </transition>
        </div>
          
          <div v-if="isLoading" class="loading-state">
             <p class="pixel-text">로딩 중...</p>
          </div>
          <div v-else-if="portfolios.length === 0" class="empty-state">
            <div class="empty-icon">📂</div>
            <p class="pixel-text">데이터가 없습니다</p>
            <button class="pixel-button primary" @click="startNewPortfolio">
              새 프로젝트 만들기
            </button>
          </div>
          <div v-else class="portfolio-list">
            <div 
              v-for="pf in portfolios" 
              :key="pf.pfId" 
              class="portfolio-item glass-card"
              :class="{ 'active': selectedPfId === pf.pfId }"
              @click="selectPortfolio(pf)"
            >
              <div class="item-icon">💾</div>
              <div class="item-content">
                <h3 class="pixel-text item-title">{{ pf.title }}</h3>
                <span class="item-date">{{ formatDate(pf.createdAt) }}</span>
              </div>
              <div class="item-arrow">→</div>
            </div>
          </div>
        </div>

        <!-- Portfolio Editor -->
        <transition name="slide-fade">
          <div v-if="viewMode === 'editor'" class="portfolio-editor-section glass-panel">
            <div class="editor-header">
              <h2 class="pixel-text section-title">
                {{ isEditing ? '프로젝트 수정' : '새 프로젝트' }}
              </h2>
              <button class="pixel-button secondary small" @click="backToList">
                취소
              </button>
            </div>
            
            <div class="editor-form">
              <div class="form-group">
                <label class="pixel-text label">제목</label>
                <input 
                  v-model="editorTitle" 
                  type="text" 
                  class="pixel-input glass-input" 
                  placeholder="프로젝트 이름을 입력하세요"
                />
              </div>
              
              <div class="form-group">
                <label class="pixel-text label">내용</label>
                <textarea 
                  v-model="editorContent" 
                  class="pixel-textarea glass-input" 
                  placeholder="프로젝트 상세 내용, 기술 스택, 역할 등을 작성하세요..."
                ></textarea>
              </div>
              
              <div class="editor-actions">
                <button 
                  v-if="isEditing" 
                  class="pixel-button danger delete-btn" 
                  @click="deletePortfolio(selectedPfId!)"
                >
                  삭제
                </button>
                <button class="pixel-button primary save-btn" @click="savePortfolio">
                  저장하기
                </button>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
    
    <!-- Decor elements -->
    <div class="decoration-circle"></div>
    <div class="scan-lines"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import { portfolioApi } from '../services/api'
import type { Portfolio } from '../types/schema'


const router = useRouter()
const authStore = useAuthStore()
const modalStore = useModalStore()

const portfolios = ref<Portfolio[]>([])
const isLoading = ref(false)
const selectedPfId = ref<number | null>(null)
const isEditing = ref(false)
const viewMode = ref<'list' | 'editor'>('list')

const editorTitle = ref('')
const editorContent = ref('')

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  await fetchPortfolios()
})

async function fetchPortfolios() {
  if (!authStore.user) return
  
  isLoading.value = true
  try {
    const response = await portfolioApi.getMyPortfolios(authStore.user.userId)
    portfolios.value = response.data
  } catch (error) {
    console.error('Failed to fetch portfolios:', error)
  } finally {
    isLoading.value = false
  }
}

function selectPortfolio(pf: Portfolio) {
  selectedPfId.value = pf.pfId
  editorTitle.value = pf.title
  editorContent.value = pf.content || ''
  isEditing.value = true
  viewMode.value = 'editor'
}

function startNewPortfolio() {
  selectedPfId.value = null
  editorTitle.value = ''
  editorContent.value = ''
  isEditing.value = false
  viewMode.value = 'editor'
}

function backToList() {
  viewMode.value = 'list'
  selectedPfId.value = null
}

async function savePortfolio() {
  if (!authStore.user) return
  if (!editorTitle.value.trim()) {
    await modalStore.openAlert('Please enter a title.')
    return
  }

  try {
    if (isEditing.value && selectedPfId.value) {
      await portfolioApi.updatePortfolio(selectedPfId.value, {
        userId: authStore.user.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
    } else {
      await portfolioApi.createPortfolio({
        userId: authStore.user.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
    }
    await fetchPortfolios()
    backToList()
  } catch (error: any) {
    console.error('Failed to save portfolio:', error)
    await modalStore.openAlert('Failed to save.')
  }
}

async function deletePortfolio(pfId: number) {
  if (!await modalStore.openConfirm('Area you sure you want to delete this project?')) return

  try {
    await portfolioApi.deletePortfolio(pfId)
    await fetchPortfolios()
    backToList()
  } catch (error) {
    console.error('Failed to delete portfolio:', error)
    await modalStore.openAlert('Failed to delete.')
  }
}

function goHome() {
  router.push('/')
}

function formatDate(dateString?: string) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}
</script>

<style scoped>
.portfolio-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow: hidden;
}

.portfolio-container {
  width: 100%;
  max-width: 1200px; /* Increased max-width for better split view */
  padding: 20px; /* Added internal padding */
  box-sizing: border-box;
  z-index: 10;
  position: relative;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
}

.title {
  font-size: 32px;
  color: #ffd43b;
  margin: 0;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  flex: 1;
  overflow: hidden; 
}

/* List Section */
.portfolio-list-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: all 0.5s ease;
  min-width: 300px;
  padding: 20px; /* Added padding to fix "sticking to wall" issue */
}

.portfolio-list-section.collapsed {
  flex: 0 0 300px; 
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  color: #4a9eff;
  margin: 0;
}

.portfolio-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding-right: 5px;
}

.portfolio-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.portfolio-item:hover {
  transform: translateX(5px);
  background: rgba(255, 255, 255, 0.1);
  border-color: #4a9eff;
}

.portfolio-item.active {
  background: rgba(74, 158, 255, 0.15);
  border-color: #ffd43b;
  box-shadow: 0 0 15px rgba(255, 212, 59, 0.2);
}

.item-icon {
  font-size: 24px;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 16px;
  color: #fff;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-date {
  font-size: 12px;
  color: #888;
}

.item-arrow {
  color: #4a9eff;
  opacity: 0;
  transition: opacity 0.2s;
}

.portfolio-item:hover .item-arrow,
.portfolio-item.active .item-arrow {
  opacity: 1;
}

/* Editor Section */
.portfolio-editor-section {
  flex: 2;
  display: flex;
  flex-direction: column;
  padding: 30px;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 15px;
}

.editor-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.label {
  font-size: 14px;
  color: #888;
}

.pixel-input.glass-input, 
.pixel-textarea.glass-input {
  background: rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  width: 100%;
}

.pixel-input:focus, 
.pixel-textarea:focus {
  border-color: #4a9eff;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.2);
}

.pixel-textarea {
  min-height: 200px;
  resize: vertical;
}

.editor-actions {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  padding-top: 20px;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  color: #888;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.5;
}


/* Transitions */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease-out;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}

/* Fade Delay for New Button */
.fade-delay-enter-active {
  transition: opacity 0.5s ease-out;
  transition-delay: 0.3s; /* Delay the appearance */
}

.fade-delay-leave-active {
  transition: none; /* Immediate disappearance */
}

.fade-delay-enter-from,
.fade-delay-leave-to {
  opacity: 0;
}


/* Background Decorations */
.scan-lines {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0) 0px,
    rgba(0, 0, 0, 0) 1px,
    rgba(255, 255, 255, 0.02) 2px,
    rgba(255, 255, 255, 0.02) 3px
  );
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: fixed;
  top: -100px;
  right: -100px;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(74, 158, 255, 0.1) 0%, transparent 70%);
  filter: blur(50px);
  z-index: 1;
}

@media (max-width: 768px) {
  .portfolio-list-section.collapsed {
    display: none;
  }
}
</style>
