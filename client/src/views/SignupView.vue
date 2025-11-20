<template>
  <AuthCard title="회원가입">
    <form @submit.prevent="handleSignup">
      <FormInput
        v-model="email"
        label="이메일"
        type="email"
        placeholder="이메일을 입력하세요"
        required
      />
      <FormInput
        v-model="nickname"
        label="닉네임"
        type="text"
        placeholder="닉네임을 입력하세요"
        required
      />
      <FormInput
        v-model="password"
        label="비밀번호"
        type="password"
        placeholder="비밀번호를 입력하세요"
        required
      />
      <FormInput
        v-model="passwordConfirm"
        label="비밀번호 확인"
        type="password"
        placeholder="비밀번호를 다시 입력하세요"
        required
      />
      <ErrorMessage :message="error" />
      <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
        <IconZap :size="20" />
        {{ loading ? '가입 중...' : '회원가입' }}
      </button>
    </form>
    <p class="auth-link">
      이미 계정이 있으신가요? <router-link to="/login">로그인</router-link>
    </p>
  </AuthCard>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthCard from '@/components/common/AuthCard.vue'
import FormInput from '@/components/common/FormInput.vue'
import ErrorMessage from '@/components/common/ErrorMessage.vue'
import IconZap from '@/components/icons/IconZap.vue'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const nickname = ref('')
const password = ref('')
const passwordConfirm = ref('')
const error = ref('')
const loading = ref(false)

const handleSignup = async () => {
  error.value = ''

  if (password.value !== passwordConfirm.value) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  loading.value = true

  const result = await authStore.signup(email.value, password.value, nickname.value)

  if (result.success) {
    router.push('/login')
  } else {
    error.value = result.message
  }

  loading.value = false
}
</script>

<style scoped>
.auth-link {
  margin-top: 1.5rem;
  text-align: center;
  color: #666;
  font-size: 0.95rem;
}

.auth-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.auth-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}
</style>
