<template>
  <div class="board">
    <PageHeader title="게시판" :icon="IconStar" />
    
    <div class="board-actions">
      <div class="board-filters">
        <select v-model="filters.category" @change="fetchPosts" class="filter-select">
          <option value="">전체 카테고리</option>
          <option value="FREE">자유</option>
          <option value="REVIEW">공고 후기</option>
          <option value="RECRUIT">레이드 모집</option>
          <option value="SHARE">질문 공유</option>
          <option value="INTERVIEW">면접 후기</option>
        </select>
      </div>
      <button @click="showCreateModal = true" class="btn btn-primary">
        <IconZap :size="20" color="white" />
        글 작성
      </button>
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
import PageHeader from '@/components/common/PageHeader.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import IconStar from '@/components/icons/IconStar.vue'
import IconZap from '@/components/icons/IconZap.vue'

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
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-2xl);
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
  gap: var(--spacing-lg);
}

.post-item {
  background: var(--bg-card);
  padding: var(--spacing-xl);
  border-radius: 16px;
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.post-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary) 0%, var(--secondary) 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-light);
}

.post-item:hover::before {
  transform: scaleX(1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.post-category {
  padding: 0.375rem 0.875rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.post-item:hover .post-category {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.2);
  color: var(--primary);
}

.post-date {
  font-size: 8px;
  font-family: 'Pixelify Sans', monospace;
  color: var(--text-muted);
}

.post-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.025em;
  transition: color 0.3s ease;
}

.post-item:hover .post-title {
  color: var(--primary);
}

.post-author {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: var(--bg-card);
  padding: var(--spacing-3xl);
  border-radius: 16px;
  border: 1px solid var(--border);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-2xl);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-content h2 {
  margin-bottom: var(--spacing-2xl);
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.025em;
}

.form-group {
  margin-bottom: var(--spacing-xl);
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  font-size: 0.875rem;
  color: var(--text-primary);
}

.form-group select,
.form-group input,
.form-group textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--border);
  border-radius: 10px;
  font-size: 0.9375rem;
  font-weight: 500;
  background: var(--bg-card);
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 200px;
  font-family: inherit;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-2xl);
  padding-top: var(--spacing-xl);
  border-top: 1px solid var(--border);
}

/* BoardView의 버튼은 common.css의 .btn 스타일 사용 */
</style>

