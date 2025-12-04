<template>
  <div class="portfolio-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="portfolio-container">
      <div class="header">
        <h1 class="pixel-text title">포트폴리오 관리</h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div class="content-wrapper">
        <!-- Portfolio List -->
        <div v-if="viewMode === 'list'" class="portfolio-list-section">
          <h2 class="pixel-text section-title">내 포트폴리오</h2>
          
          <div v-if="isLoading" class="loading-text pixel-text">로딩 중...</div>
          <div v-else-if="portfolios.length === 0" class="empty-message pixel-text">
            등록된 포트폴리오가 없습니다.
          </div>
          <div v-else class="portfolio-list">
            <div 
              v-for="pf in portfolios" 
              :key="pf.pfId" 
              class="portfolio-item"
              @click="selectPortfolio(pf)"
            >
              <div class="pf-icon">📄</div>
              <div class="pf-info">
                <p class="pixel-text pf-title">{{ pf.title }}</p>
                <p class="pf-date">{{ formatDate(pf.createdAt) }}</p>
              </div>
              <button class="pixel-button delete-btn" @click.stop="deletePortfolio(pf.pfId)">
                삭제
              </button>
            </div>
          </div>
          
          <button class="pixel-button add-btn" @click="startNewPortfolio">
            + 새 포트폴리오
          </button>
        </div>

        <!-- Portfolio Editor -->
        <div v-if="viewMode === 'editor'" class="portfolio-editor-section">
          <div class="editor-header">
            <h2 class="pixel-text section-title">
              {{ isEditing ? '포트폴리오 수정' : '새 포트폴리오 작성' }}
            </h2>
            <button class="pixel-button back-list-btn" @click="backToList">
              목록으로
            </button>
          </div>
          
          <div class="editor-form">
            <div class="form-group">
              <label class="pixel-text">제목</label>
              <input 
                v-model="editorTitle" 
                type="text" 
                class="pixel-input" 
                placeholder="포트폴리오 제목을 입력하세요"
              />
            </div>
            
            <div class="form-group">
              <label class="pixel-text">내용</label>
              <textarea 
                v-model="editorContent" 
                class="pixel-textarea" 
                placeholder="포트폴리오 내용을 입력하세요 (경력, 프로젝트 등)"
              ></textarea>
            </div>
            
            <div class="editor-actions">
              <button class="pixel-button save-btn" @click="savePortfolio">
                저장하기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Spaceship decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { portfolioApi } from '../services/api'
import type { Portfolio } from '../types/schema'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'

const router = useRouter()
const authStore = useAuthStore()

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
}

async function savePortfolio() {
  if (!authStore.user) return
  if (!editorTitle.value.trim()) {
    alert('제목을 입력해주세요.')
    return
  }

  try {
    if (isEditing.value && selectedPfId.value) {
      await portfolioApi.updatePortfolio(selectedPfId.value, {
        userId: authStore.user.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
      alert('수정되었습니다.')
    } else {
      await portfolioApi.createPortfolio({
        userId: authStore.user.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
      alert('저장되었습니다.')
    }
    await fetchPortfolios()
    backToList()
  } catch (error: any) {
    console.error('Failed to save portfolio:', error)
    alert('저장에 실패했습니다.')
  }
}

async function deletePortfolio(pfId: number) {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await portfolioApi.deletePortfolio(pfId)
    await fetchPortfolios()
  } catch (error) {
    console.error('Failed to delete portfolio:', error)
    alert('삭제에 실패했습니다.')
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
  animation: screen-enter 0.8s ease-out;
}

.portfolio-container {
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
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  margin: 0;
}

.content-wrapper {
  display: flex;
  gap: 30px;
  min-height: 600px;
}

.portfolio-list-section {
  flex: 1;
  background: rgba(0, 0, 0, 0.6);
  border: 3px solid #666;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.portfolio-editor-section {
  flex: 2;
  background: rgba(0, 0, 0, 0.6);
  border: 3px solid #666;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #4a9eff;
  padding-bottom: 10px;
}

.section-title {
  font-size: 20px;
  color: #fff;
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.back-list-btn {
  padding: 8px 16px;
  font-size: 12px;
  min-width: auto;
  background: #868e96;
  border-color: #495057;
}

.portfolio-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
  max-height: 500px;
}

/* ... existing styles ... */

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
  pointer-events: none;
}

.portfolio-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid transparent;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.portfolio-item:hover {
  background: rgba(255, 255, 255, 0.15);
}

.portfolio-item.selected {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.1);
}

.pf-icon {
  font-size: 24px;
}

.pf-info {
  flex: 1;
  min-width: 0;
}

.pf-title {
  font-size: 16px;
  color: #fff;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pf-date {
  font-size: 12px;
  color: #888;
}

.delete-btn {
  padding: 6px 12px;
  font-size: 12px;
  background: #ff6b6b;
  border-color: #e03131;
}

.add-btn {
  width: 100%;
  padding: 12px;
  background: #51cf66;
  border-color: #2f9e44;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: #4a9eff;
  margin-bottom: 8px;
  font-size: 16px;
}

.pixel-input,
.pixel-textarea {
  width: 100%;
  background: rgba(0, 0, 0, 0.3);
  border: 2px solid #666;
  border-radius: 4px;
  padding: 12px;
  color: #fff;
  font-family: inherit;
  font-size: 16px;
  box-sizing: border-box;
}

.pixel-input:focus,
.pixel-textarea:focus {
  border-color: #4a9eff;
  outline: none;
}

.pixel-textarea {
  height: 300px;
  resize: none;
}

.editor-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
}

.save-btn {
  padding: 12px 30px;
  font-size: 16px;
  background: #4a9eff;
  border-color: #357abd;
}

.empty-message {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

.spaceship {
  position: fixed;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
    height: auto;
  }
  
  .portfolio-list-section,
  .portfolio-editor-section {
    height: 400px;
  }
}
</style>
