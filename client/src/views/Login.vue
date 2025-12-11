<template>
  <div class="game-container">
    <div class="login-screen glass-panel">
      <h1 class="pixel-text game-title">
        <span class="glitch" data-text="잡스페이스">잡스페이스</span>
      </h1>
      
      <div class="login-form">
        <transition name="fade" mode="out-in">
          <div v-if="isLogin" key="login" class="form-section">
            <h2 class="pixel-text section-title">로그인</h2>
            <input
              v-model="loginEmail"
              type="email"
              placeholder="이메일"
              class="glass-input"
              :disabled="authStore.isLoading"
            />
            <input
              v-model="loginPassword"
              type="password"
              placeholder="비밀번호"
              class="glass-input"
              @keyup.enter="handleLogin"
              :disabled="authStore.isLoading"
            />
            <button class="pixel-button primary" @click="handleLogin" :disabled="authStore.isLoading">
              {{ authStore.isLoading ? '로그인 중...' : '로그인' }}
            </button>
            <p v-if="authStore.error" class="error-text pixel-text">
              {{ typeof authStore.error === 'string' ? authStore.error : JSON.stringify(authStore.error) }}
            </p>
            <button class="pixel-button link-button" @click="switchMode(false)">
              회원가입
            </button>
          </div>
          
          <div v-else key="register" class="form-section">
            <h2 class="pixel-text section-title">회원가입</h2>
            <input
              v-model="registerEmail"
              type="email"
              placeholder="이메일"
              class="glass-input"
              :class="{ 'error': !isEmailValid && registerEmail }"
              :disabled="authStore.isLoading"
            />
            <input
              v-model="registerNickname"
              type="text"
              placeholder="닉네임"
              class="glass-input"
              :disabled="authStore.isLoading"
            />
            <input
              v-model="registerPassword"
              type="password"
              placeholder="비밀번호"
              class="glass-input"
              :disabled="authStore.isLoading"
            />
            <input
              v-model="registerPasswordConfirm"
              type="password"
              placeholder="비밀번호 확인"
              class="glass-input"
              @keyup.enter="handleRegister"
              :disabled="authStore.isLoading"
            />
            <button class="pixel-button primary" @click="handleRegister" :disabled="authStore.isLoading">
              {{ authStore.isLoading ? '가입 중...' : '회원가입' }}
            </button>
            <p v-if="authStore.error" class="error-text pixel-text">
              {{ typeof authStore.error === 'string' ? authStore.error : JSON.stringify(authStore.error) }}
            </p>
            <button class="pixel-button link-button" @click="switchMode(true)">
              로그인
            </button>
          </div>
        </transition>
      </div>
      
      <button class="pixel-button" @click="goHome">
        홈으로
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isLogin = ref(true)
const loginEmail = ref('')
const loginPassword = ref('')
const registerEmail = ref('')
const registerNickname = ref('')
const registerPassword = ref('')
const registerPasswordConfirm = ref('')

// Simple email validation regex
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const isEmailValid = computed(() => emailRegex.test(registerEmail.value))

function switchMode(loginMode: boolean) {
  isLogin.value = loginMode
  authStore.error = null // Clear errors on switch
}

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

  if (!isEmailValid.value) {
    authStore.error = '유효한 이메일 형식이 아닙니다.'
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
  /* background removed to use glass-panel */
  border-radius: 16px; /* Rounded corners for glass panel */
  padding: 50px 40px;
  max-width: 500px;
  width: 90%;
  box-sizing: border-box;
  min-height: auto;
  transition: height 0.3s ease;
}

.game-title {
  font-size: 56px;
  font-weight: 900;
  color: #ffd43b;
  margin-bottom: 20px;
  letter-spacing: -2px;
  text-align: center;
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
  font-size: 24px;
  font-weight: 700;
  color: #4a9eff;
  margin-bottom: 15px;
  text-align: center;
}

.glass-input.error {
  border-color: #ff6b6b;
  box-shadow: 0 0 10px rgba(255, 107, 107, 0.3);
}

.link-button {
  background: transparent;
  border: 1px dashed rgba(255, 255, 255, 0.5);
  margin-top: 10px;
  font-size: 12px;
  padding: 10px 20px;
  color: rgba(255, 255, 255, 0.8);
}

.link-button:hover {
  border-color: #fff;
  color: #fff;
}

.error-text {
  color: #ff6b6b;
  font-size: 13px;
  font-weight: 600;
  text-align: center;
  margin-top: 10px;
  word-break: keep-all;
  text-shadow: 0 1px 2px rgba(0,0,0,0.5);
}


/* Update overrides to match global premium button style logic if needed, 
   but currently global styles are better. 
   Removing specific overrides that might look "weird" compared to global
*/
.pixel-button.primary {
  margin-top: 15px;
  width: 100%;
}

.pixel-button.link-button {
  width: 100%;
  margin-top: 10px;
}
</style>

