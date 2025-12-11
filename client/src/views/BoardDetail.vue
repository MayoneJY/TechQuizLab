<template>
  <div class="board-detail-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-detail-container dashboard-screen">
       <!-- HEADER: Title -->
      <header class="dashboard-header">
        <h1 class="game-title pixel-text">
          <span class="title-main glitch" data-text="VIEW POST">VIEW POST</span>
        </h1>
      </header>

      <div class="dashboard-content">
        <!-- Left Column: Post Content & Comments -->
        <div class="main-column">
             <div v-if="!post || boardStore.isLoading" class="post-detail-card glass-panel">
                <!-- Skeleton Structure mimicking actual content -->
                <div class="post-header">
                    <!-- Category Badge -->
                    <div class="category-badge">
                         <div class="skeleton" style="width: 60px; height: 24px; border-radius: 6px;"></div>
                    </div>
                    
                    <!-- Title (Large) -->
                    <div class="skeleton skeleton-title" style="width: 70%; height: 32px; margin-bottom: 20px;"></div>
                    
                    <!-- Meta Row -->
                    <div class="post-meta-row">
                        <div class="author-info">
                            <!-- Label Skeleton -->
                             <div class="skeleton skeleton-text" style="width: 45px; height: 18px; margin: 0; border-radius: 4px;"></div>
                            <!-- Name Skeleton -->
                             <div class="skeleton skeleton-text" style="width: 80px; height: 16px; margin: 0;"></div>
                        </div>
                        <!-- Date Skeleton -->
                        <div class="skeleton skeleton-text" style="width: 60px; height: 14px; margin: 0;"></div>
                    </div>
                </div>

                <div class="post-body">
                    <!-- Content lines mimicking paragraph -->
                    <div class="skeleton skeleton-text" style="width: 100%; height: 18px; margin-bottom: 12px;"></div>
                    <div class="skeleton skeleton-text" style="width: 95%; height: 18px; margin-bottom: 12px;"></div>
                    <div class="skeleton skeleton-text" style="width: 92%; height: 18px; margin-bottom: 12px;"></div>
                    <div class="skeleton skeleton-text" style="width: 98%; height: 18px; margin-bottom: 12px;"></div>
                    <div class="skeleton skeleton-text" style="width: 60%; height: 18px; margin-bottom: 12px;"></div>
                </div>
                <div class="skeleton-footer-actions" style="display: flex; justify-content: space-between; width: 100%; align-items: center; border-top: 2px solid rgba(255, 255, 255, 0.1); padding-top: 16px;">
                    <div class="skeleton skeleton-text" style="width: 60px; height: 20px; margin: 0;"></div>
                    <div class="skeleton skeleton-button" style="width: 120px; height: 50px; border-radius: 8px;"></div>
                </div>
             </div>

             <div v-else class="post-detail-card glass-panel">
                <div class="post-header">
                    <div class="category-badge">
                         <span class="post-category pixel-text" :class="post.tags">
                            {{ getCategoryName(post.tags) }}
                        </span>
                    </div>
                    <h1 class="pixel-text post-title">{{ post.title }}</h1>
                    
                    <div class="post-meta-row">
                        <div class="author-info">
                            <span class="author-label">WRITER</span>
                            <span class="author-name">{{ post.nickname }}</span>
                        </div>
                        <span class="post-date">{{ formatDate(post?.createdAt) }}</span>
                    </div>
                </div>

                <div class="post-body">
                    <pre class="post-content-text">{{ post.content }}</pre>
                </div>

                <div class="post-footer-actions">
                    <div class="stats-display">
                        <span class="view-count-container">
                            <img :src="iconView" class="view-icon" /> {{ post.viewCount }}
                        </span>
                    </div>
                    <button 
                      class="pixel-button like-button" 
                      :class="{ 'liked': isLiked }"
                      @click="handleLike"
                      :disabled="!authStore.isAuthenticated || isLoadingLike"
                    >
                      <span class="like-icon-container">
                          <img :src="isLiked ? iconHeartFilled : iconHeartEmpty" class="like-heart-icon" />
                      </span>
                      <span class="like-label">LIKE</span>
                      <span class="like-count">{{ likeCount }}</span>
                    </button>
                </div>
             </div>

             <!-- Comments Section -->
             <div class="comments-section glass-panel">
                <!-- Comments Header -->
                <div class="comments-header">
                    <h3 class="pixel-text" style="padding-bottom: 12px;">
                        <span class="comment-icon">💬</span> 댓글 
                        <span class="comment-count" v-if="!commentStore.isLoading">({{ commentCount }})</span>
                        <span class="skeleton skeleton-text" style="width: 40px; height: 16px; margin: 0; display: inline-block;" v-else></span>
                    </h3>
                </div>

                <!-- Comment Form -->
                <!-- Comment Form (Visible even when loading) -->
                <div v-if="authStore.isAuthenticated" class="comment-form">
                    <textarea
                      v-model="newComment"
                      class="glass-input comment-textarea"
                      placeholder="댓글을 입력하세요..."
                      rows="3"
                    ></textarea>
                    <button class="pixel-button small comment-submit-btn" @click="handleAddComment">
                      등록
                    </button>
                </div>
                 <div v-if="!authStore.isAuthenticated" class="login-prompt glass-card">
                    <p class="pixel-text">댓글을 작성하려면 <button class="link-text-btn" @click="goToLogin">로그인</button>이 필요합니다.</p>
                </div>

                <!-- Comments Loading Skeleton -->
                <div v-if="commentStore.isLoading" class="skeleton-comments">
                    <div v-for="i in 3" :key="i" class="comment-item glass-panel skeleton-item" style="margin-bottom: 12px; padding: 16px;">
                        <div class="comment-header-row" style="display: flex; gap: 10px; margin-bottom: 8px; align-items: center;">
                             <!-- No Avatar, just Name and Date -->
                             <div class="skeleton skeleton-text" style="width: 80px; height: 14px; margin: 0;"></div>
                             <div class="skeleton skeleton-text" style="width: 60px; height: 12px; margin: 0;"></div>
                        </div>
                        <!-- Content -->
                        <div class="skeleton skeleton-text" style="width: 100%; height: 14px; margin-bottom: 4px;"></div>
                        <div class="skeleton skeleton-text" style="width: 90%; height: 14px;"></div>
                    </div>
                </div>

                <!-- Comment List -->
                <div v-if="!commentStore.isLoading" class="comments-list">
                    <div v-if="comments.length === 0" class="empty-comments pixel-text">
                        첫 댓글을 작성해보세요!
                    </div>
                    
                    <template v-for="comment in comments" :key="comment.commentId">
                        <div v-if="!comment.parentCommentId" class="comment-item glass-card">
                            <div class="comment-header-row">
                                <span class="comment-author" :class="{'deleted-name': comment.isDeleted === 1}">
                                    {{ comment.isDeleted === 1 ? 'Unknown' : comment.nickname }}
                                </span>
                                <span v-if="comment.isDeleted === 0" class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                                <div v-if="comment.isDeleted === 0" class="comment-actions">
                                    <button v-if="authStore.isAuthenticated" class="action-text-btn" @click="handleReply(comment.commentId)">
                                        답글
                                    </button>
                                    <button 
                                        v-if="authStore.user && comment.userId === authStore.user.userId" 
                                        class="action-text-btn delete-text-btn" 
                                        @click="handleDeleteComment(comment.commentId)"
                                    >
                                        삭제
                                    </button>
                                </div>
                            </div>
                            
                            <div class="comment-content" :class="{ 'deleted-content': comment.isDeleted === 1 }">
                                {{ comment.isDeleted === 1 ? '삭제된 댓글입니다.' : comment.content }}
                            </div>

                            <!-- Reply Form -->
                            <div v-if="replyingTo === comment.commentId && comment.isDeleted === 0" class="reply-form-container">
                                <textarea
                                    v-model="replyContent"
                                    class="glass-input reply-input"
                                    placeholder="답글을 입력하세요..."
                                    rows="2"
                                ></textarea>
                                <div class="reply-form-actions">
                                    <button class="pixel-button small primary" @click="handleAddReply(comment.commentId)">등록</button>
                                    <button class="pixel-button small warning" @click="cancelReply">취소</button>
                                </div>
                            </div>

                            <!-- Replies -->
                            <div v-for="reply in comments.filter(c => c.parentCommentId === comment.commentId && c.isDeleted === 0)" :key="reply.commentId" class="reply-item">
                                <div class="comment-header-row">
                                    <span class="reply-arrow">↳</span>
                                    <span class="comment-author">{{ reply.nickname }}</span>
                                    <span class="comment-date">{{ formatDate(reply.createdAt) }}</span>
                                    <div class="comment-actions">
                                          <button 
                                            v-if="authStore.user && reply.userId === authStore.user.userId" 
                                            class="action-text-btn delete-text-btn" 
                                            @click="handleDeleteComment(reply.commentId)"
                                        >
                                            삭제
                                        </button>
                                    </div>
                                </div>
                                <div class="comment-content reply-content-text">
                                    {{ reply.content }}
                                </div>
                            </div>
                        </div>
                    </template>
                </div>
             </div>
        </div>

        <!-- Right Column: Sidebar Widgets -->
        <div class="widget-column">
             <div class="action-widget">
                 <button class="pixel-button back-btn" @click="goBack">
                    <img :src="btnList" class="btn-icon" /> 목록으로
                 </button>
                 
                 <template v-if="post && authStore.user && post.userId === authStore.user.userId">
                     <div class="owner-actions">
                        <button class="pixel-button warning edit-btn" @click="goToEdit">
                            <img :src="btnEdit" class="btn-icon" /> 수정하기
                        </button>
                        <button class="pixel-button primary delete-btn" @click="handleDelete">
                            <img :src="btnDelete" class="btn-icon" /> 삭제하기
                        </button>
                     </div>
                 </template>
             </div>

             <!-- Author Widget -->
             <!-- Author Widget -->
             <div class="dashboard-widget glass-panel author-widget">
                 <div v-if="!post || boardStore.isLoading" class="skeleton-author-widget" style="width: 100%; display: flex; flex-direction: column; align-items: center; gap: 12px;">
                      <!-- Avatar Skeleton -->
                      <div class="skeleton" style="width: 80px; height: 80px; border-radius: 50%;"></div>
                      <!-- Name Skeleton -->
                      <div class="skeleton skeleton-text" style="width: 100px; height: 20px;"></div>
                      <!-- Stats Skeleton -->
                      <div class="skeleton skeleton-text" style="width: 60px; height: 14px;"></div>
                 </div>
                 <template v-else>
                     <h3 class="widget-title pixel-text">작성자 정보</h3>
                     <div class="author-profile">
                         <div class="avatar-placeholder">👾</div>
                         <div class="author-details">
                             <div class="author-name-large pixel-text">{{ post.nickname }}</div>
                             <div class="author-level">Lv.{{ post.level || 1 }}</div>
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
import btnList from '../assets/images/btn_list.png'
import btnEdit from '../assets/images/btn_edit.png'
import btnDelete from '../assets/images/btn_delete.png'
import iconView from '../assets/images/icon_view.png'
import iconHeartFilled from '../assets/images/icon_heart_filled.png'
import iconHeartEmpty from '../assets/images/icon_heart_empty.png'

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
    // Fix: Clear previous post data to prevent flash of old content
    boardStore.clearCurrentPost();
    commentStore.clearComments(); // Clear comments too
    
    // Fix: Manually start loading state to prevent "Content Empty" -> "Skeleton" flash
    // while waiting for boardStore.fetchAllPosts() to find the boardId.
    commentStore.isLoading = true;
    
    try {
        // Optimize: Only fetch list if empty (navigating from list already has data)
        // If refreshing page, we need list to find boardId (assuming we don't know it)
        if (boardStore.posts.length === 0) {
             await boardStore.fetchAllPosts()
        }
    } catch(e) {/* ignore */}

  // Now fetch specific post
  const foundPost = boardStore.posts.find(p => p.postId === postId.value)
  
  if (foundPost) {
    // Parallel fetching for faster load and simultaneous skeletons
    const fetchPostPromise = boardStore.fetchPostById(foundPost.boardId, foundPost.postId)
    const fetchCommentsPromise = commentStore.fetchComments(foundPost.boardId, foundPost.postId)
    
    await Promise.all([fetchPostPromise, fetchCommentsPromise])
    
    // fetchLikeStatus depends on post being loaded (to get boardId/postId from currentPost if needed, 
    // though here we have foundPost vars). 
    // Just run it after post is likely ready or use foundPost ids directly if store action supports it?
    // Store action uses internal state? No, it takes arguments usually.
    // fetchLikeStatus uses post.value.boardId. So we wait for post fetch.
    if (boardStore.currentPost) {
        await fetchLikeStatus()
    }

  } else {
      commentStore.isLoading = false; // Reset if not proceeding
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
  /* padding: 40px 20px; */
  /* display: flex; */
  /* flex-direction: column; */
  /* align-items: center; */
}

.board-detail-container {
    width: 100%;
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
    padding-bottom: 40px;
    z-index: 10;
    position: relative;
    display: flex;
    flex-direction: column;
}

.dashboard-header {
    text-align: center;
    margin-bottom: 30px;
}

.title-main {
  font-size: 48px;
  color: #ffd43b;
  text-shadow: 4px 4px 0 #000;
  font-weight: 900;
  letter-spacing: 2px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 280px; 
  gap: 20px;
  width: 100%;
  align-items: start;
}

/* Main Column */
.main-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.loading-panel {
    text-align: center;
    padding: 30px;
    color: #888;
}

/* Post Detail Card */
.post-detail-card {
    padding: 30px;
    border-radius: 12px;
    position: relative;
    min-height: 300px;
}

.post-header {
    border-bottom: 2px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 20px;
    margin-bottom: 30px;
}

.category-badge {
    margin-bottom: 15px;
}

.post-category {
    font-size: 12px;
    padding: 6px 12px;
    border-radius: 6px;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.post-category.general { color: #4a9eff; border-color: #4a9eff; background: rgba(74, 158, 255, 0.15); }
.post-category.question { color: #ffd43b; border-color: #ffd43b; background: rgba(255, 212, 59, 0.15); }
.post-category.tip { color: #51cf66; border-color: #51cf66; background: rgba(81, 207, 102, 0.15); }
.post-category.free { color: #ff6b6b; border-color: #ff6b6b; background: rgba(255, 107, 107, 0.15); }

.post-title {
    font-size: 28px;
    color: #fff;
    margin: 0;
    margin-bottom: 15px;
    line-height: 1.3;
}

.post-meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 13px;
    color: #888;
}

.author-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.author-label {
    background: rgba(255, 255, 255, 0.1);
    color: #ccc;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 10px;
    font-weight: 700;
}

.author-name {
    color: #4a9eff;
    font-weight: 600;
}

.post-body {
    min-height: 150px;
    margin-bottom: 30px;
}

.post-content-text {
    font-family: inherit;
    font-size: 16px;
    line-height: 1.8;
    color: #eee;
    white-space: pre-wrap;
    word-break: break-all;
}

.post-footer-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 2px solid rgba(255, 255, 255, 0.1);
}

.stats-display {
    display: flex;
    gap: 15px;
    color: #888;
    font-size: 14px;
}

.view-count-container {
    display: flex;
    align-items: center;
    gap: 6px;
}

.view-icon {
    width: auto;
    height: 20px;
    image-rendering: pixelated;
}

.like-heart-icon {
    width: auto;
    height: 20px;
    vertical-align: middle;
    image-rendering: pixelated;
}

.like-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    min-width: auto;
    font-size: 14px;
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.3);
}

.like-button:hover:not(:disabled) {
    background: rgba(255, 107, 107, 0.2);
    border-color: #ff6b6b;
}

.like-button.liked {
    background: rgba(255, 107, 107, 0.2);
    border-color: #ff6b6b;
    color: #ff6b6b;
}

/* Sidebar Widgets */
.widget-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.dashboard-widget {
    padding: 20px;
    border-radius: 12px;
}

.action-widget {
    display: flex;
    flex-direction: column;
    gap: 15px;
    align-items: center;
}



.btn-icon {
    width: auto;
    height: 24px;
    margin-right: 8px;
    vertical-align: middle;
    image-rendering: pixelated;
}

.back-btn, .edit-btn, .delete-btn {
    width: 100%;
    font-size: 14px;
    padding: 12px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.owner-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
    align-items: center;
}

.author-widget {
    text-align: center;
}

.widget-title {
    font-size: 14px;
    color: #ccc;
    margin-bottom: 15px;
    text-align: left;
}

.author-profile {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
}

.avatar-placeholder {
    font-size: 40px;
    background: rgba(255, 255, 255, 0.1);
    width: 80px;
    height: 80px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.author-name-large {
    font-size: 18px;
    color: #fff;
}

.author-level {
    color: #4a9eff;
    font-size: 14px;
}


/* Comments Section */
.comments-section {
    padding: 30px;
    border-radius: 12px;
}

.comments-title {
    font-size: 18px;
    margin-bottom: 25px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.comment-count-badge {
    background: #4a9eff;
    color: #000;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
}

.comment-form {
    display: flex;
    /* flex-direction: column; Removed to allow row layout */
    gap: 10px;
    margin-bottom: 30px;
    align-items: flex-end; /* Align button to bottom */
}

.comment-textarea {
    flex: 1; /* Fill available width */
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.2);
    resize: vertical;
    min-height: 48px;
}

.comment-submit-btn {
    /* align-self: flex-end; Removed */
    flex-shrink: 0;
    padding: 0 24px;
    font-size: 14px;
    height: 74px; /* Match a comfortable height or textarea min-height */
    display: flex;
    align-items: center;
    justify-content: center;
}


.login-prompt {
    text-align: center;
    padding: 20px;
    margin-bottom: 30px;
    color: #ccc;
}

.link-text-btn {
    background: none;
    border: none;
    color: #4a9eff;
    text-decoration: underline;
    cursor: pointer;
    font-weight: bold;
    font-size: inherit;
}

.comments-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.empty-comments {
    text-align: center;
    color: #888;
    padding: 20px;
}

.comment-item {
    padding: 15px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.03);
}

.comment-header-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
    font-size: 13px;
}

.comment-author {
    color: #4a9eff;
    font-weight: 600;
}

.deleted-name {
    color: #888;
}

.comment-date {
    color: #666;
    font-size: 12px;
}

.comment-actions {
    margin-left: auto;
    display: flex;
    gap: 10px;
}

.action-text-btn {
    background: none;
    border: none;
    color: #ccc;
    font-size: 12px;
    cursor: pointer;
}

.action-text-btn:hover {
    color: #fff;
    text-decoration: underline;
}

.delete-text-btn:hover {
    color: #ff6b6b;
}

.comment-content {
    font-size: 14px;
    color: #ddd;
    line-height: 1.5;
    padding-left: 2px;
}

.deleted-content {
    color: #666;
    font-style: italic;
}

/* Replies */
.reply-item {
    margin-top: 10px;
    margin-left: 20px;
    padding-top: 10px;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.reply-arrow {
    color: #666;
}

.reply-form-container {
    margin-top: 10px;
    padding: 10px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 6px;
}

.reply-form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
}

.pixel-button.small {
    padding: 6px 20px;
    font-size: 11px;
    min-width: auto;
}

/* Animations & Decorations */
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

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
    .dashboard-content {
        grid-template-columns: 1fr;
    }
    
    .title-main {
        font-size: 32px;
    }
    
    .widget-column {
        order: -1; /* Widgets on top on mobile? Or maybe keeping at bottom is better? Keeping default (column flow) means bottom. */
    }
}
</style>
