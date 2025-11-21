<template>
  <div class="game-container">
    <ParticleBackground />
    
    <div class="login-screen">
      <h1 class="pixel-text game-title">잡몬스터</h1>
      
      <div class="login-form">
        <div v-if="isLogin" class="form-section">
          <h2 class="pixel-text section-title">로그인</h2>
          <input
            v-model="loginEmail"
            type="email"
            placeholder="이메일"
            class="pixel-input"
          />
          <input
            v-model="loginPassword"
            type="password"
            placeholder="비밀번호"
            class="pixel-input"
            @keyup.enter="handleLogin"
          />
          <button class="pixel-button primary" @click="handleLogin" :disabled="authStore.isLoading">
            {{ authStore.isLoading ? '로그인 중...' : '로그인' }}
          </button>
          <p v-if="authStore.error" class="error-text pixel-text">
            {{ typeof authStore.error === 'string' ? authStore.error : JSON.stringify(authStore.error) }}
          </p>
          <button class="pixel-button link-button" @click="isLogin = false">
            회원가입
          </button>
        </div>
        
        <div v-else class="form-section">
          <h2 class="pixel-text section-title">회원가입</h2>
          <input
            v-model="registerEmail"
            type="email"
            placeholder="이메일"
            class="pixel-input"
          />
          <input
            v-model="registerNickname"
            type="text"
            placeholder="닉네임"
            class="pixel-input"
          />
          <input
            v-model="registerPassword"
            type="password"
            placeholder="비밀번호"
            class="pixel-input"
          />
          <input
            v-model="registerPasswordConfirm"
            type="password"
            placeholder="비밀번호 확인"
            class="pixel-input"
            @keyup.enter="handleRegister"
          />
          <button class="pixel-button primary" @click="handleRegister" :disabled="authStore.isLoading">
            {{ authStore.isLoading ? '가입 중...' : '회원가입' }}
          </button>
          <p v-if="authStore.error" class="error-text pixel-text">
            {{ typeof authStore.error === 'string' ? authStore.error : JSON.stringify(authStore.error) }}
          </p>
          <button class="pixel-button link-button" @click="isLogin = true">
            로그인
          </button>
        </div>
      </div>
      
      <button class="pixel-button" @click="goHome">
        홈으로
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ParticleBackground from '../components/ParticleBackground.vue'

const router = useRouter()
const authStore = useAuthStore()

const isLogin = ref(true)
const loginEmail = ref('')
const loginPassword = ref('')
const registerEmail = ref('')
const registerNickname = ref('')
const registerPassword = ref('')
const registerPasswordConfirm = ref('')

async function handleLogin() {
  if (!loginEmail.value || !loginPassword.value) {
    authStore.error = '이메일과 비밀번호를 입력해주세요.'
    return
  }
  
  try {
    await authStore.login(loginEmail.value, loginPassword.value)
    router.push('/')
  } catch (error) {
    // Error is handled in store
  }
}

async function handleRegister() {
  if (!registerEmail.value || !registerNickname.value || !registerPassword.value) {
    authStore.error = '모든 필드를 입력해주세요.'
    return
  }
  
  if (registerPassword.value !== registerPasswordConfirm.value) {
    authStore.error = '비밀번호가 일치하지 않습니다.'
    return
  }
  
  try {
    await authStore.register(registerEmail.value, registerPassword.value, registerNickname.value)
    router.push('/')
  } catch (error) {
    // Error is handled in store
  }
}

function goHome() {
  router.push('/')
}
</script>

<style scoped>
.login-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 30px;
  z-index: 10;
  position: relative;
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  padding: 40px;
  max-width: 500px;
  width: 100%;
  box-sizing: border-box;
  min-height: auto;
}

.game-title {
  font-size: 56px;
  font-weight: 900;
  color: #ffd43b;
  margin-bottom: 20px;
  letter-spacing: -1px;
}

.login-form {
  width: 100%;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #4a9eff;
  margin-bottom: 10px;
}

.pixel-input {
  font-family: 'Pretendard', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, sans-serif;
  font-size: 14px;
  font-weight: 500;
  padding: 12px 16px;
  background: #fff;
  border: 4px solid #000;
  color: #000;
  width: 100%;
  box-sizing: border-box;
  border-radius: 8px;
}

.pixel-input::placeholder {
  color: #888;
}

.pixel-input:focus {
  outline: none;
  box-shadow: 
    0 0 0 2px #4a9eff,
    0 0 0 4px #000;
}

.link-button {
  background: transparent;
  border: 2px solid #fff;
  margin-top: 10px;
  font-size: 10px;
  padding: 10px 20px;
}

.error-text {
  color: #ff6b6b;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  margin-top: 10px;
  word-break: keep-all;
}
</style>

