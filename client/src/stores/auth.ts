import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../services/api'

export interface User {
  id: number
  email: string
  nickname: string
  password?: string
  level?: number
  exp?: number
  createdAt?: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => user.value !== null)

  // Load user from localStorage on init
  function init() {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('authToken')
    if (savedUser && savedToken) {
      user.value = JSON.parse(savedUser)
      token.value = savedToken
    }
  }

  async function login(email: string, password: string) {
    isLoading.value = true
    error.value = null
    try {
      const response = await userApi.login(email, password)
      // 응답 데이터 처리 (서버 응답 형식에 맞게 조정)
      const responseData = response.data
      
      // 응답이 객체이고 user 정보가 있는 경우
      if (responseData && typeof responseData === 'object') {
        user.value = responseData.user || responseData
        // 토큰이 서버에서 오는 경우 사용, 없으면 임시 토큰 생성
        if (user.value && user.value.id) {
          token.value = responseData.token || responseData.accessToken || `token_${user.value.id}`
        } else {
          token.value = responseData.token || responseData.accessToken || 'token_temp'
        }
      } else {
        user.value = responseData
        if (user.value && user.value.id) {
          token.value = `token_${user.value.id}`
        } else {
          token.value = 'token_temp'
        }
      }
      
      if (user.value) {
      localStorage.setItem('user', JSON.stringify(user.value))
      }
      if (token.value) {
      localStorage.setItem('authToken', token.value)
      }
      return user.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '로그인에 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function register(email: string, password: string, nickname: string) {
    isLoading.value = true
    error.value = null
    try {
      const response = await userApi.register({ email, password, nickname })
      const responseData = response.data
      
      // 응답이 객체이고 user 정보가 있는 경우
      if (responseData && typeof responseData === 'object') {
        user.value = responseData.user || responseData
        if (user.value && user.value.id) {
          token.value = responseData.token || responseData.accessToken || `token_${user.value.id}`
        } else {
          token.value = responseData.token || responseData.accessToken || 'token_temp'
        }
      } else {
        user.value = responseData
        if (user.value && user.value.id) {
      token.value = `token_${user.value.id}`
        } else {
          token.value = 'token_temp'
        }
      }
      
      if (user.value) {
      localStorage.setItem('user', JSON.stringify(user.value))
      }
      if (token.value) {
      localStorage.setItem('authToken', token.value)
      }
      return user.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '회원가입에 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('authToken')
  }

  async function checkEmailExists(email: string): Promise<boolean> {
    try {
      const response = await userApi.checkEmailExists(email)
      return response.data
    } catch {
      return false
    }
  }

  async function checkNicknameExists(nickname: string): Promise<boolean> {
    try {
      const response = await userApi.checkNicknameExists(nickname)
      return response.data
    } catch {
      return false
    }
  }

  // Initialize on store creation
  init()

  return {
    user,
    token,
    isLoading,
    error,
    isAuthenticated,
    login,
    register,
    logout,
    checkEmailExists,
    checkNicknameExists
  }
})

