<template>
  <div class="board-write-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="board-write-container">
      <div class="header">
        <h1 class="pixel-text title">{{ isEdit ? '게시글 수정' : '게시글 작성' }}</h1>
        <button class="pixel-button back-button" @click="goBack">
          ← 목록으로
        </button>
      </div>

      <div class="write-form">
        <div class="form-group">
          <label class="pixel-text form-label">카테고리</label>
          <select v-model="form.category" class="pixel-input form-select">
            <option value="general">일반</option>
            <option value="question">질문</option>
            <option value="tip">팁</option>
            <option value="free">자유</option>
          </select>
        </div>

        <div class="form-group">
          <label class="pixel-text form-label">제목</label>
          <input
            v-model="form.title"
            type="text"
            class="pixel-input form-input"
            placeholder="제목을 입력하세요"
            maxlength="100"
          />
        </div>

        <div class="form-group">
          <label class="pixel-text form-label">내용</label>
          <textarea
            v-model="form.content"
            class="pixel-input form-textarea"
            placeholder="내용을 입력하세요"
            rows="15"
          ></textarea>
        </div>

        <div class="form-actions">
          <button class="pixel-button cancel-button" @click="goBack">
            취소
          </button>
          <button class="pixel-button submit-button" @click="handleSubmit">
            {{ isEdit ? '수정' : '작성' }}
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

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    await modalStore.openAlert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  if (isEdit.value && postId.value) {
    await boardStore.fetchPostById(postId.value)
    const post = boardStore.currentPost
    if (!post) {
      modalStore.openAlert('게시글을 찾을 수 없습니다.')
      router.push('/board')
      return
    }

    if (post.userId !== authStore.user?.userId) {
      await modalStore.openAlert('수정 권한이 없습니다.')
      router.push('/board')
      return
    }

    form.value = {
      category: post.category,
      title: post.title,
      content: post.content
    }
    form.value = {
      category: (post.tags as 'general' | 'question' | 'tip' | 'free') || 'general',
      title: post.title,
      content: post.content
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

try {
    if (isEdit.value && postId.value) {
      await boardStore.updatePost(
        postId.value,
        form.value.title,
        form.value.content,
        form.value.category,
      )
      await modalStore.openAlert('게시글이 수정되었습니다.')
      router.push(`/board/${postId.value}`)
    } else {

      await boardStore.createPost(
        form.value.title,
        form.value.content,
        form.value.category,
      )
      await modalStore.openAlert('게시글이 작성되었습니다.')

      router.push('/board')
    }
  } catch (error: any) {
    await modalStore.openAlert(error.message || '작성에 실패했습니다.')
  }
}
</script>

<style scoped>
.board-write-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: screen-enter 0.8s ease-out;
}

.board-write-container {
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
  gap: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 
    4px 4px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.write-form {
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 8px;
  padding: 30px;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 10px;
}

.form-select,
.form-input,
.form-textarea {
  width: 100%;
  font-family: 'Pretendard', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, sans-serif;
}

.form-select {
  padding: 12px;
  font-size: 14px;
}

.form-input {
  padding: 12px;
  font-size: 16px;
}

.form-textarea {
  padding: 12px;
  font-size: 16px;
  resize: vertical;
  line-height: 1.6;
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
  padding: 15px 30px;
  min-width: 120px;
}

.cancel-button {
  background: linear-gradient(135deg, #666 0%, #555 100%);
}

.submit-button {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
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
  .title {
    font-size: 24px;
  }
  
  .write-form {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .cancel-button,
  .submit-button {
    width: 100%;
  }
}

/* Override text-transform for Korean text */
.pixel-text,
.pixel-button {
  text-transform: none !important;
}
</style>


