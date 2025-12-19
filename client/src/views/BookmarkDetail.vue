<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      <!-- HEADER -->
      <div class="page-header">
        <h2 class="page-title pixel-text">오답노트 상세</h2>
        <button class="back-btn pixel-button" @click="router.back()">뒤로가기</button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="skeleton skeleton-text" style="width: 50%; height: 32px; margin-bottom: 20px;"></div>
        <div class="skeleton skeleton-box" style="width: 100%; height: 200px; border-radius: 12px;"></div>
      </div>

      <div v-else-if="bookmark" class="detail-content">
        <!-- Metadata Row -->
        <!-- Metadata Header -->
        <div class="meta-header">
             <div class="meta-left-col">
                 <div class="meta-main-row">
                     <span class="meta-badge category">{{ bookmark.jobCategory || 'General' }}</span>
                     <span class="meta-separator">|</span>
                     <span class="meta-text stage">{{ bookmark.stageTitle || 'Unknown Stage' }}</span>
                     <span class="meta-separator">|</span>
                     <span class="meta-text difficulty" :class="getDifficultyClass(bookmark.difficulty)">
                        {{ bookmark.difficulty || 'Normal' }}
                     </span>
                 </div>
                 <span class="meta-date">{{ formatDateTime(bookmark.createdAt) }}</span>
             </div>
             
             <div class="score-display">
                <span class="score-label">SCORE</span>
                <span class="score-value pixel-text">
                    {{ bookmark.damage || 0 }}<span class="sub-score">/1000</span>
                </span>
             </div>
        </div>

        <!-- Question Section -->
        <div class="section-card question-card">
          <div class="card-header-row">
             <div class="section-label pixel-text">QUESTION</div>
             <div class="tags-container" v-if="bookmark.keywordTags">
                <span class="tag-pill" v-for="tag in bookmark.keywordTags.split(',')" :key="tag">#{{ tag.trim() }}</span>
             </div>
          </div>
          <div class="question-text">{{ bookmark.questionText }}</div>
        </div>

        <!-- My Answer Section -->
        <div class="section-card user-answer">
          <div class="section-label pixel-text">MY ANSWER</div>
          <div class="answer-text">{{ bookmark.userAnswer || '(답변 없음)' }}</div>
        </div>

        <!-- AI Feedback Section -->
        <div class="section-card feedback-card">
          <div class="section-label pixel-text">AI UPDATE</div>
          <div class="feedback-text">{{ bookmark.aiFeedback || 'AI 피드백이 없습니다.' }}</div>
        </div>

        <!-- Memo Section -->
        <div class="section-card memo-card">
          <div class="section-label pixel-text">MY MEMO</div>
          <div class="memo-content">
            <textarea 
              v-if="isEditingMemo"
              v-model="editMemoText" 
              class="memo-textarea"
              placeholder="메모를 입력하세요..."
            ></textarea>
            <div v-else class="memo-display">{{ bookmark.memo || '메모가 없습니다.' }}</div>
            
            <div class="memo-actions">
               <button v-if="isEditingMemo" class="pixel-button small-btn" @click="cancelEdit">취소</button>
               <button v-if="isEditingMemo" class="pixel-button primary small-btn" @click="saveMemo">저장</button>
               <button v-else class="pixel-button small-btn" @click="startEdit">메모 수정</button>
            </div>
          </div>
        </div>

        <!-- Footer Actions -->
        <div class="detail-footer">
          <button class="pixel-button danger" @click="deleteBookmark">북마크 삭제</button>
          <button class="pixel-button secondary" @click="goToBattleResult">원본 결과 보기</button>
        </div>
      </div>

      <div v-else class="error-state">
        <p>북마크를 찾을 수 없습니다.</p>
        <button class="pixel-button" @click="router.back()">목록으로</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { battleApi } from '../services/api'
import { useModalStore } from '../stores/modal'

const route = useRoute()
const router = useRouter()
const modalStore = useModalStore()
const bookmarkId = Number(route.params.id)

const loading = ref(true)
const bookmark = ref<any>(null)
const isEditingMemo = ref(false)
const editMemoText = ref('')

onMounted(async () => {
  await fetchBookmark()
})

async function fetchBookmark() {
  try {
    loading.value = true
    const res = await battleApi.getBookmark(bookmarkId)
    bookmark.value = res.data
    
    if (bookmark.value) {
        editMemoText.value = bookmark.value.memo || ''
    }
  } catch (e) {
    console.error(e)
    // If 404, maybe redirect or show error
  } finally {
    loading.value = false
  }
}

function startEdit() {
    isEditingMemo.value = true
    editMemoText.value = bookmark.value.memo || ''
}

function cancelEdit() {
    isEditingMemo.value = false
    editMemoText.value = bookmark.value.memo || ''
}

async function saveMemo() {
    try {
        // Assume API exists: updateBookmark(id, memo)
        await battleApi.updateBookmark(bookmarkId, editMemoText.value)
        if (bookmark.value) bookmark.value.memo = editMemoText.value
        isEditingMemo.value = false
    } catch (e) {
        await modalStore.openAlert('메모 저장 실패')
    }
}

async function deleteBookmark() {
    if (!await modalStore.openConfirm('정말 삭제하시겠습니까?')) return
    try {
        // Assume API exists: deleteBookmark(id)
        await battleApi.deleteBookmark(bookmarkId)
        router.replace('/my-bookmarks')
    } catch (e) {
        await modalStore.openAlert('삭제 실패')
    }
}

function goToBattleResult() {
    if (bookmark.value?.refBattleId) {
        router.push(`/battle-result/${bookmark.value.refBattleId}`)
    }
}

function formatDateTime(dateStr: string) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}.${month}.${day} ${hour}:${minute}`
}

function getDifficultyClass(diff: string) {
    if (!diff) return ''
    const d = diff.toLowerCase()
    if (d === 'hard' || d === '상') return 'diff-hard'
    if (d === 'medium' || d === '중') return 'diff-medium'
    return 'diff-easy'
}
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 500px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 10px;
}

.page-title {
  color: #ffd43b;
  font-size: 24px;
}

.back-btn {
  font-size: 14px;
  padding: 8px 16px;
}

.detail-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.section-card {
    background: rgba(0,0,0,0.2);
    border-radius: 8px;
    padding: 15px;
    border: 1px solid rgba(255,255,255,0.05);
}

.section-label {
    font-size: 12px;
    color: #4a9eff;
    margin-bottom: 8px;
    font-weight: bold;
}

.question-text {
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    line-height: 1.5;
}

.answer-text {
    color: #ddd;
    line-height: 1.6;
}

.feedback-text {
    color: #51cf66;
    line-height: 1.6;
}

.memo-content {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.memo-display {
    color: #ffd43b;
    white-space: pre-wrap;
}

.memo-textarea {
    width: 100%;
    min-height: 80px;
    background: rgba(0,0,0,0.3);
    border: 1px solid #555;
    color: #fff;
    padding: 10px;
    border-radius: 4px;
    resize: vertical;
}

.memo-actions {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.small-btn {
    font-size: 12px;
    padding: 6px 12px;
}

.memo-actions .pixel-button {
    background: transparent !important;
    border: 1px solid #333 !important;
    color: #666;
    box-shadow: none !important;
    transition: all 0.2s;
    min-width: auto;
    padding: 6px 12px;
}

.memo-actions .pixel-button:hover {
    transform: translateY(-2px);
    border-color: #aaa !important;
    color: #fff;
    background: rgba(255, 255, 255, 0.1) !important;
    box-shadow: none !important;
}

.memo-actions .pixel-button.primary {
    color: #4a9eff;
}

.memo-actions .pixel-button.primary:hover {
    background: rgba(74, 158, 255, 0.1) !important;
    border-color: #4a9eff !important;
    color: #fff;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.2) !important;
}

.detail-footer {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid rgba(255,255,255,0.1);
}

.detail-footer .pixel-button {
    background: transparent !important;
    border: 1px solid #333 !important;
    color: #666;
    box-shadow: none !important;
    transition: all 0.2s;
}

.detail-footer .pixel-button:hover {
    transform: translateY(-2px);
}

.detail-footer .pixel-button.danger {
    color: #ff6b6b;
}

.detail-footer .pixel-button.danger:hover {
    background: rgba(255, 107, 107, 0.1) !important;
    border-color: #ff6b6b !important;
    box-shadow: 0 0 10px rgba(255, 107, 107, 0.2) !important;
}

.detail-footer .pixel-button.secondary {
    color: #4a9eff;
}

.detail-footer .pixel-button.secondary:hover {
    color: #4a9eff;
    background: rgba(74, 158, 255, 0.1) !important;
    border-color: #4a9eff !important;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.2) !important;
}

.error-state {
    text-align: center;
    padding: 50px;
}


/* Metadata Styles */
.meta-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    width: 100%;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}

.meta-left-col {
    display: flex;
    flex-direction: column;
    gap: 6px;
    align-items: flex-start;
}

.meta-main-row {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
}

.meta-badge.category {
    background: rgba(74, 158, 255, 0.2);
    color: #4a9eff;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    border: 1px solid rgba(74, 158, 255, 0.3);
}

.meta-separator {
    color: #555;
    font-size: 12px;
}

.meta-text.stage {
    color: #eee;
    font-weight: 600;
}

.meta-text.difficulty {
    font-weight: bold;
}

.diff-hard { color: #ff6b6b; }
.diff-medium { color: #ffd43b; }
.diff-easy { color: #51cf66; }

.spacer {
    flex: 1;
}

.meta-date {
    font-size: 13px;
    color: #666;
    font-family: monospace;
}

/* Card Header & Tags */
.card-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.tags-container {
    display: flex;
    gap: 6px;
}

.tag-pill {
    font-size: 11px;
    color: #aaa;
    background: rgba(0,0,0,0.3);
    padding: 2px 8px;
    border-radius: 10px;
}

.score-display {
    display: flex;
    align-items: center;
    gap: 10px;
    background: rgba(0,0,0,0.3);
    padding: 6px 16px;
    border-radius: 8px;
    border: 1px solid rgba(255,212,59,0.2);
    margin-right: 15px;
}

.score-label {
    font-size: 11px;
    color: #aaa;
    font-weight: 700;
    letter-spacing: 0.5px;
}

.score-value {
    font-size: 20px;
    color: #ffd43b;
    font-weight: bold;
    text-shadow: 0 0 10px rgba(255, 212, 59, 0.3);
}

.sub-score {
    font-size: 0.6em;
    color: #666;
    margin-left: 2px;
    font-weight: 500;
}
</style>
