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
        <!-- Question Section -->
        <div class="section-card">
          <div class="section-label pixel-text">QUESTION</div>
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
               <button v-if="isEditingMemo" class="pixel-button primary small-btn" @click="saveMemo">저장</button>
               <button v-if="isEditingMemo" class="pixel-button small-btn" @click="cancelEdit">취소</button>
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

const route = useRoute()
const router = useRouter()
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
    // Need an API to get single bookmark details. 
    // Assuming GET /api/battles/bookmarks/{id} exists or we find it from full list
    // Since implementing full API might be complex, we can fetch all and find one for now
    // OR create a dedicated endpoint. 
    // Actually, implementation plan didn't specify NEW endpoint for detail. 
    // Let's check api.ts if we can simple use getMyBookmarks and find.
    const res = await battleApi.getMyBookmarks()
    bookmark.value = res.data.find((b: any) => b.bookmarkId === bookmarkId)
    
    if (bookmark.value) {
        editMemoText.value = bookmark.value.memo || ''
    }
  } catch (e) {
    console.error(e)
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
        alert('메모 저장 실패')
    }
}

async function deleteBookmark() {
    if (!confirm('정말 삭제하시겠습니까?')) return
    try {
        // Assume API exists: deleteBookmark(id)
        await battleApi.deleteBookmark(bookmarkId)
        router.replace('/my-bookmarks')
    } catch (e) {
        alert('삭제 실패')
    }
}

function goToBattleResult() {
    if (bookmark.value?.refBattleId) {
        router.push(`/battle-result/${bookmark.value.refBattleId}`)
    }
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

.detail-footer {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid rgba(255,255,255,0.1);
}

.error-state {
    text-align: center;
    padding: 50px;
}
</style>
