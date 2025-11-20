<template>
  <div class="board">
    <div class="board-header">
      <h1>게시판</h1>
      <button @click="showCreateModal = true" class="btn btn-primary">글 작성</button>
    </div>

    <div class="board-filters">
      <select v-model="filters.category" @change="fetchPosts">
        <option value="">전체</option>
        <option value="FREE">자유</option>
        <option value="REVIEW">공고 후기</option>
        <option value="RECRUIT">레이드 모집</option>
        <option value="SHARE">질문 공유</option>
        <option value="INTERVIEW">면접 후기</option>
      </select>
    </div>

    <LoadingSpinner v-if="loading" />
    <EmptyState
      v-else-if="posts.length === 0"
      message="게시글이 없습니다."
    />
    <div v-else class="posts-list">
      <div
        v-for="post in posts"
        :key="post.id"
        class="post-item"
        @click="$router.push(`/board/${post.id}`)"
      >
        <div class="post-header">
          <span class="post-category">{{ getCategoryName(post.category) }}</span>
          <span class="post-date">{{ formatDate(post.createdAt) }}</span>
        </div>
        <div class="post-title">{{ post.title }}</div>
        <div class="post-author">{{ post.authorNickname || '익명' }}</div>
      </div>
    </div>

    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-content" @click.stop>
        <h2>글 작성</h2>
        <form @submit.prevent="createPost">
          <div class="form-group">
            <label>카테고리</label>
            <select v-model="newPost.category" required>
              <option value="FREE">자유</option>
              <option value="REVIEW">공고 후기</option>
              <option value="RECRUIT">레이드 모집</option>
              <option value="SHARE">질문 공유</option>
              <option value="INTERVIEW">면접 후기</option>
            </select>
          </div>
          <div class="form-group">
            <label>제목</label>
            <input v-model="newPost.title" required placeholder="제목을 입력하세요" />
          </div>
          <div class="form-group">
            <label>내용</label>
            <textarea v-model="newPost.content" required rows="8" placeholder="내용을 입력하세요"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="showCreateModal = false" class="btn btn-secondary">취소</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '작성 중...' : '작성' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { boardApi } from '@/api/board'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const posts = ref([])
const loading = ref(false)
const showCreateModal = ref(false)
const submitting = ref(false)
const filters = ref({
  category: '',
})

const newPost = ref({
  category: 'FREE',
  title: '',
  content: '',
})

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
  return date.toLocaleDateString('ko-KR')
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const response = await boardApi.getPosts(filters.value)
    posts.value = response.data
  } catch (error) {
    alert('게시글 목록을 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const createPost = async () => {
  submitting.value = true
  try {
    await boardApi.createPost(newPost.value)
    showCreateModal.value = false
    newPost.value = { category: 'FREE', title: '', content: '' }
    fetchPosts()
  } catch (error) {
    alert('글 작성에 실패했습니다.')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.board {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.board-header h1 {
  font-size: 1.5rem;
  font-family: 'Press Start 2P', monospace;
  margin: 0;
  color: var(--primary);
  text-shadow: 2px 2px 0px var(--primary-dark), 4px 4px 0px rgba(0, 0, 0, 0.8);
  letter-spacing: 0.1em;
}

.board-filters {
  margin-bottom: 2rem;
}

.board-filters select {
  padding: 0.5rem 1rem;
  border: 4px solid var(--border-bright);
  border-radius: 0;
  font-size: 10px;
  font-family: 'Press Start 2P', monospace;
  background: var(--bg-card);
  color: var(--text-primary);
  box-shadow: var(--pixel-shadow);
  image-rendering: pixelated;
}

.loading,
.empty {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
  font-family: 'Pixelify Sans', monospace;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-item {
  background: var(--bg-card);
  padding: 1.5rem;
  border-radius: 0;
  border: 4px solid var(--border-bright);
  cursor: pointer;
  transition: transform 0.1s;
  box-shadow: var(--pixel-shadow);
  image-rendering: pixelated;
}

.post-item:hover {
  transform: translate(4px, -2px);
  box-shadow: var(--pixel-shadow-lg);
  border-color: var(--primary);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.post-category {
  padding: 0.25rem 0.75rem;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: 0;
  font-size: 8px;
  font-weight: normal;
  font-family: 'Press Start 2P', monospace;
  color: var(--text-secondary);
  box-shadow: var(--pixel-shadow-sm);
  image-rendering: pixelated;
}

.post-date {
  font-size: 8px;
  font-family: 'Pixelify Sans', monospace;
  color: var(--text-muted);
}

.post-title {
  font-size: 0.9rem;
  font-weight: normal;
  font-family: 'Press Start 2P', monospace;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  letter-spacing: 0.05em;
}

.post-author {
  font-size: 0.75rem;
  font-family: 'Pixelify Sans', monospace;
  color: var(--text-secondary);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  image-rendering: pixelated;
}

.modal-content {
  background: var(--bg-card);
  padding: 2rem;
  border-radius: 0;
  border: 4px solid var(--border-bright);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--pixel-shadow-lg);
  image-rendering: pixelated;
}

.modal-content h2 {
  margin-bottom: 1.5rem;
  font-family: 'Press Start 2P', monospace;
  font-size: 1rem;
  color: var(--text-primary);
  letter-spacing: 0.05em;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: normal;
  font-family: 'Press Start 2P', monospace;
  font-size: 10px;
  color: var(--text-primary);
}

.form-group select,
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 4px solid var(--border-bright);
  border-radius: 0;
  font-size: 12px;
  font-family: 'Pixelify Sans', monospace;
  background: var(--bg-card);
  color: var(--text-primary);
  box-shadow: var(--pixel-shadow-inset);
  image-rendering: pixelated;
}

.form-group textarea {
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
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

