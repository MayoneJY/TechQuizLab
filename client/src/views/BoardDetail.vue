<template>
  <div class="board-detail-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-detail-container">
      <div class="header">
        <button class="pixel-button back-button" @click="goBack">
          ← 목록으로
        </button>
        <div v-if="post && authStore.user && post.userId === authStore.user.userId" class="post-actions">
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
            <span class="post-author">{{ post.nickname }}</span>
            <span class="post-date">{{ formatDate(post?.createdAt) }}</span>
          </div>
          <div class="post-stats">
            <span>👁 {{ post.viewCount }}</span>
            <button 
              class="like-button" 
              :class="{ 'liked': isLiked }"
              @click="handleLike"
              :disabled="!authStore.isAuthenticated || isLoadingLike"
            >
              <span class="like-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
              <span class="like-count">{{ likeCount }}</span>
            </button>
          </div>
        </div>

        <div class="post-content">
          <pre class="post-content-text">{{ post.content }}</pre>
        </div>

        <div class="comments-section">
          <h3 class="pixel-text comments-title">
            댓글 ({{ commentCount}})

          </h3>
          
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
            <template v-for="comment in comments" :key="comment.commentId">
              <div
                v-if="!comment.parentCommentId"
                class="comment-item"
              >
                <div class="comment-header" :class="{ 'deleted-comment-header': comment.isDeleted === 1 }">
                  <span class="comment-author">{{ comment.isDeleted === 1 ? '삭제된 댓글입니다.' : comment.nickname }}</span>
                  <span v-if="comment.isDeleted === 0" class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                  <div v-if="comment.isDeleted === 0" class="comment-actions">
                    <button
                      v-if="authStore.isAuthenticated"
                      class="comment-reply"
                      @click="handleReply(comment.commentId)"
                    >
                      답글
                    </button>
                    <button
                      v-if="authStore.user && comment.userId === authStore.user.userId"
                      class="comment-delete"
                      @click="handleDeleteComment(comment.commentId)"
                    >
                      삭제
                    </button>
                  </div>
                </div>
                <div class="comment-content" :class="{ 'deleted-comment-content': comment.isDeleted === 1 }">
                  <span v-if="comment.isDeleted === 1" class="deleted-text">이 댓글은 삭제되었습니다.</span>
                  <span v-else>{{ comment.content }}</span>
                </div>
                <div v-if="replyingTo === comment.commentId && comment.isDeleted === 0" class="reply-form">
                  <textarea
                    v-model="replyContent"
                    class="pixel-input reply-input"
                    placeholder="대댓글을 입력하세요..."
                    rows="2"
                  ></textarea>
                  <div class="reply-actions">
                    <button class="pixel-button reply-submit" @click="handleAddReply(comment.commentId)">
                      등록
                    </button>
                    <button class="pixel-button reply-cancel" @click="cancelReply">
                      취소
                    </button>
                  </div>
                </div>
                <div v-for="reply in comments.filter(c => c.parentCommentId === comment.commentId && c.isDeleted === 0)" :key="reply.commentId" class="reply-item">
                  <div class="comment-header">
                    <span class="comment-author">↳ {{ reply.nickname }}</span>
                    <span class="comment-date">{{ formatDate(reply.createdAt) }}</span>
                    <div class="comment-actions">
                      <button
                        v-if="authStore.user && reply.userId === authStore.user.userId"
                        class="comment-delete"
                        @click="handleDeleteComment(reply.commentId)"
                      >
                        삭제
                      </button>
                    </div>
                  </div>
                  <div class="comment-content">
                    {{ reply.content }}
                  </div>
                </div>
              </div>
            </template>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBoardStore2 } from '../stores/board2'
import { useBoardCommentStore } from '../stores/boardComment'
import { useAuthStore } from '../stores/auth'
import { useBoardPostlikeStore } from '../stores/boardPostlike'
import { useModalStore } from '../stores/modal'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const boardStore = useBoardStore2()
const commentStore = useBoardCommentStore()
const authStore = useAuthStore()
const modalStore = useModalStore()
const boardPostlikeStore = useBoardPostlikeStore()

const postId = computed(() => Number(route.params.id))
const post = computed(() => boardStore.currentPost)
const comments = computed(() => commentStore.comments)
const newComment = ref('')
const replyingTo = ref<number | null>(null)
const replyContent = ref('')
const isLiked = ref(false)
const likeCount = ref(0)
const isLoadingLike = ref(false)

// 댓글 수 계산 (삭제되지 않은 댓글만 카운트)
const commentCount = computed(() => {
  return comments.value.filter(comment => comment.isDeleted === 0).length
})

// 일반 댓글 수 (대댓글 제외)
const parentCommentCount = computed(() => {
  return comments.value.filter(comment => 
    !comment.parentCommentId && comment.isDeleted === 0
  ).length
})

// 대댓글 수
const replyCount = computed(() => {
  return comments.value.filter(comment => 
    comment.parentCommentId !== null && comment.isDeleted === 0
  ).length
})

async function fetchLikeStatus() {
  if (!post.value) return

  try {
    const status = await boardPostlikeStore.fetchLikeStatus(post.value.boardId, post.value.postId)
    isLiked.value = status.isLiked
    likeCount.value = status.likeCount
  } catch (error: any) {
    console.error('좋아요 상태 조회 실패:', error)
  }
}

onMounted(async () => {
  await boardStore.fetchAllPosts()
  const foundPost = boardStore.posts.find(p => p.postId === postId.value)
  
  if (foundPost) {
    await boardStore.fetchPostById(foundPost.boardId, foundPost.postId)
    
    if (post.value) {
      await commentStore.fetchComments(post.value.boardId, post.value.postId)
      await fetchLikeStatus()
    }
  }
  
  if (!post.value) {
    await modalStore.openAlert('게시글을 찾을 수 없습니다.')
    goBack()
  }
})

watch(() => post.value?.boardId, () => {
  if (post.value) {
    fetchLikeStatus()
  }
})

watch(() => authStore.isAuthenticated, () => {
  if (post.value) {
    fetchLikeStatus()
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
  if (!await modalStore.openConfirm('정말 삭제하시겠습니까?')) return
  
  if (!post.value) {
    alert('게시글 정보를 찾을 수 없습니다.')
    return
  }
  try {
    await boardStore.deletePost(post.value.boardId, post.value.postId)
    await modalStore.openSuccess('게시글이 삭제되었습니다.')
    goBack()
  } catch (error: any) {
    await modalStore.openAlert(error.message || '삭제에 실패했습니다.')
  }
}

async function handleLike() {
  if (!authStore.isAuthenticated) {
    modalStore.openAlert('로그인이 필요합니다.')
    return
  }

  if (!post.value) {
    modalStore.openAlert('게시글 정보를 찾을 수 없습니다.')
    return
  }

  if (isLoadingLike.value) return

  isLoadingLike.value = true

  try {
    if (isLiked.value) {
      await boardPostlikeStore.removePostLike(post.value.boardId, post.value.postId)
      isLiked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await boardPostlikeStore.addPostLike(post.value.boardId, post.value.postId)
      isLiked.value = true
      likeCount.value += 1
    }
  } catch (error: any) {
    const errorMsg = error.response?.data?.resvalue || error.response?.data?.resmsg || '좋아요 처리에 실패했습니다.'
    modalStore.openAlert(errorMsg)
    
    await fetchLikeStatus()
  } finally {
    isLoadingLike.value = false
  }
}

async function handleAddComment() {
  if (!newComment.value.trim()) {
    modalStore.openAlert('댓글을 입력해주세요.')
    return
  }

  if (!post.value) {
    modalStore.openAlert('게시글 정보를 찾을 수 없습니다.')
    return
  }

  try {
    await commentStore.createComment(post.value.boardId, post.value.postId, newComment.value)
    newComment.value = ''
  } catch (error: any) {
    const errorMsg = error.response?.data?.resvalue || error.response?.data?.resmsg || error.message || '댓글 작성에 실패했습니다.'
    alert(errorMsg)
  }
}

async function handleDeleteComment(commentId: number) {
  if (!await modalStore.openConfirm('댓글을 삭제하시겠습니까?')) return

  if (!post.value) {
    modalStore.openAlert('게시글 정보를 찾을 수 없습니다.')
    return
  }

  try {
    await commentStore.deleteComment(post.value.boardId, post.value.postId, commentId)
  } catch (error: any) {
    const errorMsg = error.response?.data?.resvalue || error.response?.data?.resmsg || error.message || '댓글 삭제에 실패했습니다.'
    modalStore.openAlert(errorMsg)
  }
}

function handleReply(commentId: number) {
  replyingTo.value = commentId
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

async function handleAddReply(parentCommentId: number) {
  if (!replyContent.value.trim()) {
    modalStore.openAlert('대댓글을 입력해주세요.')
    return
  }

  if (!post.value) {
    modalStore.openAlert('게시글 정보를 찾을 수 없습니다.')
    return
  }

  try {
    await commentStore.createReply(post.value.boardId, post.value.postId, parentCommentId, replyContent.value)
    replyingTo.value = null
    replyContent.value = ''
  } catch (error: any) {
    const errorMsg = error.response?.data?.resvalue || error.response?.data?.resmsg || error.message || '대댓글 작성에 실패했습니다.'
    alert(errorMsg)
  }
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
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #fff;
}

.like-button:hover:not(:disabled) {
  background: rgba(255, 107, 107, 0.2);
  border-color: #ff6b6b;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 107, 107, 0.3);
}

.like-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.like-button.liked {
  background: rgba(255, 107, 107, 0.2);
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.like-button.liked:hover:not(:disabled) {
  background: rgba(255, 107, 107, 0.3);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.like-icon {
  font-size: 18px;
  line-height: 1;
}

.like-count {
  font-weight: 600;
  min-width: 20px;
  text-align: center;
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
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.comment-count-badge {
  background: linear-gradient(135deg, #4a9eff 0%, #6bb3ff 100%);
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 2px 4px rgba(74, 158, 255, 0.3);
}

.reply-count-info {
  font-size: 14px;
  color: #ccc;
  font-weight: 400;
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

.comment-actions {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.comment-reply,
.comment-delete {
  background: none;
  border: none;
  color: #4a9eff;
  cursor: pointer;
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s;
  font-weight: 500;
}

.comment-delete {
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.3);
}

.comment-reply:hover {
  background: rgba(74, 158, 255, 0.2);
  color: #6bb3ff;
}

.comment-delete:hover {
  background: rgba(255, 107, 107, 0.2);
  border-color: rgba(255, 107, 107, 0.5);
  color: #ff8a8a;
}

.reply-item {
  margin-left: 30px;
  margin-top: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.03);
  border-left: 2px solid rgba(74, 158, 255, 0.3);
  border-radius: 4px;
}

.reply-form {
  margin-top: 10px;
  padding: 10px;
  background: rgba(74, 158, 255, 0.1);
  border-radius: 4px;
}

.reply-input {
  width: 100%;
  margin-bottom: 10px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

.reply-actions {
  display: flex;
  gap: 10px;
}

.reply-submit,
.reply-cancel {
  flex: 1;
  font-size: 12px;
  padding: 8px 16px;
}

.reply-cancel {
  background: rgba(255, 255, 255, 0.1);
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #ccc;
}

.deleted-text {
  color: #999;
  font-style: italic;
}

.deleted-comment-header .comment-author {
  color: #888;
}
</style>
