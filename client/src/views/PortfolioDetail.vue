<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      
      <!-- Header -->
      <div class="page-header">
        <h2 class="page-title pixel-text">{{ isNew ? '새 프로젝트' : (isEditing ? '프로젝트 수정' : '프로젝트 상세') }}</h2>
        <button class="back-btn pixel-button" @click="goBack">목록으로</button>
      </div>

      <div v-if="loading" class="loading-state">
        <p class="pixel-text">로딩 중...</p>
      </div>

      <!-- VIEW MODE -->
      <div v-else-if="!isEditing" class="detail-view">
        <div class="detail-header">
           <h1 class="detail-title pixel-text">{{ portfolio?.title }}</h1>
           <span class="detail-date">{{ formatDate(portfolio?.createdAt) }}</span>
        </div>
        
        <div class="detail-content">
           {{ portfolio?.content }}
        </div>

        <div class="detail-footer">
           <button class="pixel-button danger" @click="deletePortfolio">삭제</button>
           <div class="spacer"></div>
           <button class="pixel-button primary" @click="startEdit">수정</button>
        </div>
      </div>

      <!-- EDIT MODE -->
      <div v-else class="editor-view">
         <div class="input-group">
            <label class="pixel-text label">제목</label>
            <input 
              v-model="editorTitle" 
              type="text" 
              class="glass-input" 
              placeholder="프로젝트 제목을 입력하세요" 
            />
         </div>
         <div class="input-group full-height">
            <label class="pixel-text label">내용</label>
            <textarea 
              v-model="editorContent" 
              class="glass-input textarea" 
              placeholder="내용을 입력하세요"
            ></textarea>
         </div>

         <div class="editor-footer">
            <button v-if="!isNew" class="pixel-button secondary" @click="cancelEdit">취소</button>
            <div class="spacer"></div>
            <button class="pixel-button primary" @click="savePortfolio">저장하기</button>
         </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import { portfolioApi } from '../services/api'
import type { Portfolio } from '../types/schema'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const modalStore = useModalStore()

const loading = ref(false)
const portfolio = ref<Portfolio | null>(null)

// Edit State
const isEditing = ref(false)
const editorTitle = ref('')
const editorContent = ref('')

const idParam = route.params.id as string
const isNew = computed(() => idParam === 'new')

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (isNew.value) {
    isEditing.value = true
  } else {
    await fetchPortfolio(parseInt(idParam))
  }
})

async function fetchPortfolio(pfId: number) {
  loading.value = true
  try {
    const response = await portfolioApi.getPortfolio(pfId)
    const found = response.data
    if (found) {
      portfolio.value = found
      editorTitle.value = found.title
      editorContent.value = found.content || ''
    } else {
       throw new Error('Not found')
    }
  } catch (error) {
    console.error('Failed to fetch portfolio', error)
    modalStore.openAlert('프로젝트를 찾을 수 없습니다.')
    router.push('/portfolio')
  } finally {
    loading.value = false
  }
}

function startEdit() {
  editorTitle.value = portfolio.value?.title || ''
  editorContent.value = portfolio.value?.content || ''
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
  editorTitle.value = portfolio.value?.title || ''
  editorContent.value = portfolio.value?.content || ''
}

async function savePortfolio() {
  if (!editorTitle.value.trim()) {
     modalStore.openAlert('제목을 입력해주세요.')
     return
  }

  try {
    if (isNew.value) {
      await portfolioApi.createPortfolio({
        userId: authStore.user!.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
    } else {
      await portfolioApi.updatePortfolio(portfolio.value!.pfId, {
        userId: authStore.user!.userId,
        title: editorTitle.value,
        content: editorContent.value
      })
    }
    router.push('/portfolio')
  } catch (error) {
    console.error('Save failed', error)
    modalStore.openAlert('저장에 실패했습니다.')
  }
}

async function deletePortfolio() {
  if (!await modalStore.openConfirm('정말 삭제하시겠습니까?')) return
  try {
    await portfolioApi.deletePortfolio(portfolio.value!.pfId)
    router.push('/portfolio')
  } catch (error) {
    modalStore.openAlert('삭제 실패')
  }
}

function goBack() {
  router.push('/portfolio')
}

function formatDate(dateString?: string) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString() + ' ' + new Date(dateString).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
}
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
  min-height: 100vh;
}

.content-wrapper {
  padding: 30px;
  border-radius: 12px;
  min-height: 600px;
  background: rgba(0, 0, 0, 0.4); 
  border: 1px solid rgba(255,255,255,0.1);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(10px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 15px;
}

.page-title {
  color: #ffd43b;
  font-size: 24px;
  margin: 0;
}

.back-btn {
  font-size: 14px;
  padding: 8px 16px;
}

/* Detail View */
.detail-header {
  margin-bottom: 30px;
}

.detail-title {
  color: #fff;
  font-size: 28px;
  margin-bottom: 10px;
}

.detail-date {
  color: #888;
  font-size: 14px;
}

.detail-content {
  flex: 1;
  color: #eee;
  line-height: 1.6;
  white-space: pre-line;
  font-size: 16px;
  background: rgba(0,0,0,0.2);
  padding: 20px;
  border-radius: 8px;
  min-height: 300px;
}

.detail-footer {
  margin-top: 30px;
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

/* Editor View */
.editor-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  color: #aaa;
  font-size: 14px;
  font-weight: bold;
}

.glass-input {
  background: rgba(0,0,0,0.3);
  border: 1px solid #444;
  color: #fff;
  padding: 12px;
  border-radius: 6px;
  font-family: inherit;
  width: 100%;
  box-sizing: border-box;
  font-size: 16px;
}

.glass-input:focus {
  border-color: #4a9eff;
  outline: none;
  background: rgba(0,0,0,0.5);
}

.textarea {
  min-height: 400px;
  resize: vertical;
  line-height: 1.6;
}

.editor-footer {
  margin-top: auto;
  display: flex;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.spacer { flex: 1; }

.loading-state {
  text-align: center;
  padding: 50px;
  color: #888;
}

/* Button & Base */
.pixel-button {
  border: 1px solid rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.05);
  color: #ccc;
  transition: all 0.2s;
  cursor: pointer;
}
.pixel-button:hover { background: rgba(255,255,255,0.15); color: #fff; border-color: #fff; }

.pixel-button.primary { background: #4a9eff; border-color: #4a9eff; color: #fff; }
.pixel-button.primary:hover { background: #3b82f6; }

.pixel-button.secondary { background: rgba(255,255,255,0.1); }

.pixel-button.danger { border-color: #ff6b6b; color: #ff6b6b; }
.pixel-button.danger:hover { background: #ff6b6b; color: #fff; }

</style>
