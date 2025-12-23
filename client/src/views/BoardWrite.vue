<template>
  <div class="board-write-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-write-container dashboard-screen">
       <!-- HEADER: Title -->
      <header class="dashboard-header">
        <h1 class="game-title pixel-text">
          <span class="title-main glitch" :data-text="isEdit ? 'EDIT POST' : 'WRITE POST'">
            {{ isEdit ? 'EDIT POST' : 'WRITE POST' }}
          </span>
        </h1>
      </header>

      <div class="write-card glass-panel">
        <div class="form-group">
          <label class="pixel-text form-label">카테고리</label>
          <div class="select-wrapper">
             <select v-model="form.category" class="glass-input form-select">
                <option value="general">일반</option>
                <option value="question">질문</option>
                <option value="tip">팁</option>
                <option value="free">자유</option>
             </select>
             <div class="select-arrow">▼</div>
          </div>
        </div>

        <div class="form-group">
          <label class="pixel-text form-label">제목</label>
          <input
            v-model="form.title"
            type="text"
            class="glass-input form-input"
            placeholder="제목을 입력하세요"
            maxlength="100"
          />
        </div>

        <div class="form-group">
          <label class="pixel-text form-label">내용</label>
          <textarea
            v-model="form.content"
            class="glass-input form-textarea"
            placeholder="내용을 입력하세요"
            rows="15"
          ></textarea>
        </div>

        <div class="form-actions">
          <button class="pixel-button cancel-button" @click="goBack">
            취소
          </button>
          <button class="pixel-button primary submit-button" @click="handleSubmit" :disabled="isSubmitting">
            {{ isEdit ? '수정 완료' : '작성 완료' }}
          </button>
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
import { useModalStore } from '../stores/modal'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const route = useRoute()
const boardStore = useBoardStore2()
const authStore = useAuthStore()
const modalStore = useModalStore()

const isEdit = computed(() => route.name === 'board-edit')
const postId = computed(() => isEdit.value ? Number(route.params.id) : null)

const form = ref({
  category: 'general' as 'general' | 'question' | 'tip' | 'free',
  title: '',
  content: ''
})

const isSubmitting = ref(false)

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await modalStore.openAlert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  if (isEdit.value && postId.value) {
    try {
      // Try to get post from store first
      let post = boardStore.posts.find(p => p.postId === postId.value) || boardStore.currentPost;
      
      // If not in simple list or currentPost, fetch it
      if (!post || post.postId !== postId.value) {
           // We might not know boardId here without finding it in a list first if our API requires boardId for fetchPostById.
           // Assuming we can find it in global list or we just fetch list.
           // Since we can't easily guess boardId, let's fetch list.
           if (boardStore.posts.length === 0) {
               await boardStore.fetchAllPosts();
           }
           post = boardStore.posts.find(p => p.postId === postId.value) || null;
           if (post) {
               await boardStore.fetchPostById(post.boardId, post.postId);
               post = boardStore.currentPost;
           }
      }

      if (!post) {
        modalStore.openAlert('게시글을 찾을 수 없습니다.')
        router.push('/board')
        return
      }

      if (post.userId !== authStore.user?.userId) {
        modalStore.openAlert('수정 권한이 없습니다.')
        router.push('/board')
        return
      }
      
      // Mapping korean tags back to keys if necessary, or just using tag if it matches
      const categoryMap: Record<string, 'general' | 'question' | 'tip' | 'free'> = {
          '일반': 'general',
          '질문': 'question',
          '팁': 'tip',
          '자유': 'free'
        }
        
      // If tag is already 'general', map returns undefined, so we fallback to tag as is.
      const mappedCategory = categoryMap[post.tags] || post.tags;
      
      form.value = {
        category: mappedCategory as any || 'general',
        title: post.title,
        content: post.content
      }
    } catch (error) {
      modalStore.openAlert('게시글을 불러오는데 실패했습니다.')
      router.push('/board')
    }
  }
})

function goBack() {
  if (isEdit.value && postId.value) {
    router.push(`/board/${postId.value}`)
  } else {
    router.push('/board')
  }
}

async function handleSubmit() {
  if (!form.value.title.trim()) {
    await modalStore.openAlert('제목을 입력해주세요.')
    return
  }

  if (!form.value.content.trim()) {
    await modalStore.openAlert('내용을 입력해주세요.')
    return
  }

  if (isSubmitting.value) return
  isSubmitting.value = true

  try {
    if (isEdit.value && postId.value) {
      const post = boardStore.currentPost
      if (!post) {
        alert('게시글 정보를 찾을 수 없습니다.')
        return
      }
      
      // 복합키 사용
      await boardStore.updatePost(
        post.boardId,
        postId.value,
        form.value.title,
        form.value.content,
        form.value.category,
      )
      await modalStore.openSuccess('게시글이 수정되었습니다.')
      router.push(`/board/${postId.value}`)
    } else {
      await boardStore.createPost(
        form.value.title,
        form.value.content,
        form.value.category,
      )
      await modalStore.openSuccess('게시글이 작성되었습니다.')

      router.push('/board')
    }
  } catch (error: any) {
    await modalStore.openAlert(error.message || '작성에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.board-write-screen {
  min-height: 100vh;
  position: relative;
  /* padding: 40px 20px; */
  /* display: flex; */
  /* flex-direction: column; */
  /* align-items: center; */
}

.board-write-container {
    width: 100%;
    max-width: 800px;
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
}

.write-card {
  padding: 40px;
  border-radius: 12px;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  font-size: 16px;
  color: #fff;
  margin-bottom: 10px;
}

.select-wrapper {
    position: relative;
    width: 200px;
}

.select-arrow {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #fff;
    pointer-events: none;
    font-size: 12px;
}

.form-select {
  width: 100%;
  appearance: none;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.1);
}

.form-select option {
    background: #1a0a2e;
    color: #fff;
}

.form-input {
    font-size: 16px;
    background: rgba(255, 255, 255, 0.1);
}

.form-textarea {
    font-size: 16px;
    line-height: 1.6;
    background: rgba(255, 255, 255, 0.1);
    resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.cancel-button,
.submit-button {
  font-size: 16px;
  padding: 12px 30px;
  min-width: 120px;
}

.cancel-button {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid #666;
  color: #ccc;
}

.cancel-button:hover {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
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
  .title-main {
    font-size: 32px;
  }
  
  .write-card {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .cancel-button,
  .submit-button {
    width: 100%;
  }
  
  .select-wrapper {
      width: 100%;
  }
}
</style>
