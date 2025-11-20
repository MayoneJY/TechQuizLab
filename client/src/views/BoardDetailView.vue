<template>
  <div class="board-detail">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="!post" class="empty">게시글을 찾을 수 없습니다.</div>
    <div v-else class="post-content">
      <div class="post-header">
        <div class="post-meta">
          <span class="post-category">{{ getCategoryName(post.category) }}</span>
          <span class="post-date">{{ formatDate(post.createdAt) }}</span>
        </div>
        <div class="post-title">{{ post.title }}</div>
        <div class="post-author">작성자: {{ post.authorNickname || '익명' }}</div>
      </div>

      <div class="post-body">
        <div class="post-text">{{ post.content }}</div>
      </div>

      <div class="comments-section">
        <h3>댓글 ({{ comments.length }})</h3>
        <div class="comment-form">
          <textarea
            v-model="newComment"
            rows="3"
            placeholder="댓글을 입력하세요..."
          ></textarea>
          <button @click="submitComment" class="btn btn-primary" :disabled="submitting">
            {{ submitting ? '작성 중...' : '댓글 작성' }}
          </button>
        </div>
        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-author">{{ comment.authorNickname || '익명' }}</div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-date">{{ formatDate(comment.createdAt) }}</div>
          </div>
        </div>
      </div>

      <div class="actions">
        <button @click="$router.push('/board')" class="btn btn-secondary">목록으로</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { boardApi } from '@/api/board'

const route = useRoute()

const post = ref(null)
const comments = ref([])
const loading = ref(false)
const newComment = ref('')
const submitting = ref(false)

const getCategoryName = (category) => {
  const names = {
    FREE: '자유',
    REVIEW: '공고 후기',
    RECRUIT: '레이드 모집',
    SHARE: '질문 공유',
    INTERVIEW: '면접 후기',
  }
  return names[category] || category
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
}

const fetchPost = async () => {
  loading.value = true
  try {
    const response = await boardApi.getPost(route.params.id)
    post.value = response.data
    comments.value = response.data.comments || []
  } catch (error) {
    alert('게시글을 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    alert('댓글을 입력해주세요.')
    return
  }

  submitting.value = true
  try {
    const response = await boardApi.createComment(route.params.id, { content: newComment.value })
    comments.value.push(response.data)
    newComment.value = ''
  } catch (error) {
    alert('댓글 작성에 실패했습니다.')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchPost()
})
</script>

<style scoped>
.board-detail {
  max-width: 900px;
  margin: 0 auto;
}

.loading,
.empty {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
  font-family: 'Pixelify Sans', monospace;
}

.post-content {
  background: var(--bg-card);
  padding: 2rem;
  border-radius: 0;
  border: 4px solid var(--border-bright);
  box-shadow: var(--pixel-shadow);
  image-rendering: pixelated;
}

.post-header {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 4px solid var(--border);
}

.post-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 8px;
  font-family: 'Pixelify Sans', monospace;
  color: var(--text-secondary);
}

.post-category {
  padding: 0.25rem 0.75rem;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 0;
  box-shadow: var(--pixel-shadow-sm);
  font-weight: 500;
}

.post-title {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.post-author {
  font-size: 0.95rem;
  color: #666;
}

.post-body {
  margin-bottom: 3rem;
}

.post-text {
  font-size: 1.05rem;
  line-height: 1.8;
  white-space: pre-wrap;
}

.comments-section h3 {
  margin-bottom: 1.5rem;
  font-size: 1.2rem;
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-form textarea {
  width: 100%;
  padding: 1rem;
  border: 4px solid var(--border-bright);
  border-radius: 0;
  font-size: 12px;
  font-family: 'Pixelify Sans', monospace;
  margin-bottom: 0.75rem;
  resize: vertical;
  background: var(--bg-card);
  color: var(--text-primary);
  box-shadow: var(--pixel-shadow-inset);
  image-rendering: pixelated;
}

.comment-form textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: var(--pixel-shadow);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comment-item {
  padding: 1.5rem;
  background: var(--bg-card);
  border: 4px solid var(--border-bright);
  border-radius: 0;
  box-shadow: var(--pixel-shadow);
  image-rendering: pixelated;
}

.comment-author {
  font-weight: normal;
  font-family: 'Press Start 2P', monospace;
  font-size: 8px;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.comment-text {
  margin-bottom: 0.5rem;
  line-height: 1.6;
}

.comment-date {
  font-size: 0.85rem;
  color: #999;
}

.actions {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #333;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #555;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #fff;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #f5f5f5;
}
</style>

