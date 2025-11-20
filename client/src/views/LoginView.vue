<template>
  <AuthCard title="로그인">
    <form @submit.prevent="handleLogin">
      <FormInput
        v-model="email"
        label="이메일"
        type="email"
        placeholder="이메일을 입력하세요"
        required
      />
      <FormInput
        v-model="password"
        label="비밀번호"
        type="password"
        placeholder="비밀번호를 입력하세요"
        required
      />
      <ErrorMessage :message="error" />
      <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
        <IconZap :size="20" />
        {{ loading ? '로그인 중...' : '로그인' }}
      </button>
    </form>
    <p class="auth-link">
      계정이 없으신가요? <router-link to="/signup">회원가입</router-link>
    </p>
  </AuthCard>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthCard from '@/components/common/AuthCard.vue'
import FormInput from '@/components/common/FormInput.vue'
import ErrorMessage from '@/components/common/ErrorMessage.vue'
import IconZap from '@/components/icons/IconZap.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  const result = await authStore.login(email.value, password.value)

  if (result.success) {
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
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
