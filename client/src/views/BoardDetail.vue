<template>
  <div class="board-detail-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-detail-container">
      <div class="header">
        <button class="pixel-button back-button" @click="goBack">
          ← 목록으로
        </button>
        <div v-if="post && authStore.user && post.id === authStore.user.id" class="post-actions">
          <button class="pixel-button edit-button" @click="goToEdit">
            수정
          </button>
          <button class="pixel-button delete-button" @click="handleDelete">
            삭제
          </button>
        </div>
      </div>

      <div v-if="!post" class="loading-text pixel-text">
        게시글을 불러오는 중...
      </div>

      <div v-else class="post-detail">
        <div class="post-header">
          <span class="post-category" :class="post.tags">
            {{ getCategoryName(post.tags) }}
          </span>
          <h1 class="pixel-text post-title">{{ post.title }}</h1>
        </div>

        <div class="post-meta">
          <div class="post-author-info">
            <span class="post-author">{{ post.userId }}</span>
            <span class="post-date">{{ formatDate(post?.createdAt) }}</span>
            <span v-if="post.updatedAt && post.updatedAt !== post.createdAt" class="post-updated">
              (수정됨: {{ formatDate(post.updatedAt) }})
            </span>
          </div>
          <div class="post-stats">
            <span>👁 {{ post.view }}</span>
            <button class="like-button" @click="handleLike">
              ❤️
            </button>
          </div>
        </div>

        <div class="post-content">
          <pre class="post-content-text">{{ post.content }}</pre>
        </div>

        <div class="comments-section">
          <h3 class="pixel-text comments-title">댓글 ({{ comments.length }})</h3>
          
          <div v-if="authStore.isAuthenticated" class="comment-form">
            <textarea
              v-model="newComment"
              class="pixel-input comment-input"
              placeholder="댓글을 입력하세요..."
              rows="3"
            ></textarea>
            <button class="pixel-button comment-submit" @click="handleAddComment">
              댓글 작성
            </button>
          </div>
          <div v-else class="login-prompt">
            <p class="pixel-text">댓글을 작성하려면 <button class="link-button" @click="goToLogin">로그인</button>이 필요합니다.</p>
          </div>

          <div class="comments-list">
            <div v-if="comments.length === 0" class="empty-comments pixel-text">
              댓글이 없습니다. 첫 댓글을 작성해보세요!
            </div>
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="comment-item"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.author }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                <button
                  v-if="authStore.user && comment.authorId === authStore.user.id"
                  class="comment-delete"
                  @click="handleDeleteComment(comment.id)"
                >
                  삭제
                </button>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
            </div>
          </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBoardStore2 } from '../stores/board2'
import { useAuthStore } from '../stores/auth'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const boardStore = useBoardStore2()
const authStore = useAuthStore()

const postId = computed(() => Number(route.params.id))
const post = computed(() => boardStore.currentPost)
// const comments = computed(() => boardStore.getComments(postId.value)) // 아직 미구현
const comments = ref([]) 
const newComment = ref('')

onMounted(async () => {
  await boardStore.fetchPostById(postId.value)
  if (!post.value) {
    alert('게시글을 찾을 수 없습니다.')
    goBack()
  }
})

function goBack() {
  router.push('/board')
}

function goToEdit() {
  router.push(`/board/${postId.value}/edit`)
}

function goToLogin() {
  router.push('/login')
}

async function handleDelete() {
  if (!confirm('정말 삭제하시겠습니까?')) return

  try {
    await boardStore.deletePost(postId.value)
    alert('게시글이 삭제되었습니다.')
    goBack()
  } catch (error: any) {
    alert(error.message || '삭제에 실패했습니다.')
  }
}

function handleLike() {
  // boardStore.toggleLike(postId.value) // 아직 미구현
  alert('준비 중인 기능입니다.')
}

function handleAddComment() {
  if (!newComment.value.trim()) {
    alert('댓글을 입력해주세요.')
    return
  }
  alert('준비 중인 기능입니다.')

  /*
  try {
    boardStore.createComment(postId.value, newComment.value)
    newComment.value = ''
  } catch (error: any) {
    alert(error.message || '댓글 작성에 실패했습니다.')
  }
  */
}

function handleDeleteComment(commentId: number) {
  if (!confirm('댓글을 삭제하시겠습니까?')) return
  alert('준비 중인 기능입니다.')

  /*
  try {
    boardStore.deleteComment(commentId)
  } catch (error: any) {
    alert(error.message || '댓글 삭제에 실패했습니다.')
  }
  */
}

function getCategoryName(category: string) {
  const names: Record<string, string> = {
    general: '일반',
    question: '질문',
    tip: '팁',
    free: '자유'
  }
  return names[category] || category
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
</script>

<style scoped>
.board-detail-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: screen-enter 0.8s ease-out;
}

.board-detail-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 15px;
}

.back-button,
.edit-button,
.delete-button {
  font-size: 14px;
  padding: 12px 24px;
}

.delete-button {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
}

.post-actions {
  display: flex;
  gap: 10px;
}

.post-detail {
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 8px;
  padding: 30px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.post-category {
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid #666;
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

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  flex: 1;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.2);
  flex-wrap: wrap;
  gap: 15px;
}

.post-author-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
  color: #ccc;
}

.post-author {
  font-weight: 600;
  color: #4a9eff;
}

.post-date,
.post-updated {
  color: #888;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.like-button {
  background: none;
  border: none;
  color: #ff6b6b;
  cursor: pointer;
  font-size: 14px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.like-button:hover {
  background: rgba(255, 107, 107, 0.2);
}

.post-content {
  margin-bottom: 40px;
}

.post-content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #fff;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  margin: 0;
}

.comments-section {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 3px solid rgba(255, 255, 255, 0.2);
}

.comments-title {
  font-size: 20px;
  font-weight: 700;
  color: #ffd43b;
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-input {
  width: 100%;
  margin-bottom: 10px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

.comment-submit {
  width: 100%;
}

.login-prompt {
  margin-bottom: 30px;
  padding: 15px;
  background: rgba(255, 212, 59, 0.1);
  border: 2px solid #ffd43b;
  border-radius: 4px;
  text-align: center;
}

.link-button {
  background: none;
  border: none;
  color: #4a9eff;
  cursor: pointer;
  text-decoration: underline;
  font-weight: 600;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 12px;
}

.comment-author {
  font-weight: 600;
  color: #4a9eff;
}

.comment-date {
  color: #888;
}

.comment-delete {
  margin-left: auto;
  background: none;
  border: none;
  color: #ff6b6b;
  cursor: pointer;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.comment-delete:hover {
  background: rgba(255, 107, 107, 0.2);
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #ccc;
  white-space: pre-wrap;
  word-break: break-word;
}

.empty-comments {
  text-align: center;
  padding: 40px 20px;
  color: #888;
  font-size: 14px;
}

.loading-text {
  text-align: center;
  padding: 60px 20px;
  color: #4a9eff;
  font-size: 18px;
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
  .post-detail {
    padding: 20px;
  }
  
  .post-title {
    font-size: 20px;
  }
  
  .post-content-text {
    font-size: 14px;
  }
}
</style>


