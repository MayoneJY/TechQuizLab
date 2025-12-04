import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../services/api'
import type { User } from '../types/schema'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => user.value !== null && token.value !== null)

  // Load user from localStorage on init
  function init() {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('authToken')
    const savedRefreshToken = localStorage.getItem('refreshToken')
    if (savedUser && savedToken) {
      user.value = JSON.parse(savedUser)
      token.value = savedToken
      if (savedRefreshToken) {
        refreshToken.value = savedRefreshToken
      }
    }
  }

  async function login(email: string, password: string) {
    isLoading.value = true
    error.value = null
    try {
      const response = await userApi.login(email, password)
      // 서버 응답: LoginResponseDTO { accessToken, refreshToken }
      const responseData = response.data

      if (responseData && responseData.accessToken) {
        // 토큰 저장
        token.value = responseData.accessToken
        refreshToken.value = responseData.refreshToken || null

        localStorage.setItem('authToken', token.value!)
        if (refreshToken.value) {
          localStorage.setItem('refreshToken', refreshToken.value)
        }

        // 토큰을 사용하여 사용자 정보 가져오기
        // 이메일로 사용자 정보 조회
        try {
          const userResponse = await userApi.getUserByEmail(email)
          user.value = userResponse.data
          if (user.value) {
            localStorage.setItem('user', JSON.stringify(user.value))
          }
        } catch (userErr: any) {
          console.error('Failed to fetch user info:', userErr)
          // 사용자 정보를 가져오지 못해도 토큰은 저장되어 있으므로 계속 진행
        }

        return user.value
      } else {
        throw new Error('로그인 응답 형식이 올바르지 않습니다.')
      }
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        (typeof err.response?.data === 'string' ? err.response.data : null) ||
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

      // 회원가입 성공 시 사용자 정보 저장
      if (responseData && typeof responseData === 'object') {
        // 회원가입 후 자동 로그인
        try {
          await login(email, password)
        } catch (loginErr) {
          // 로그인 실패해도 회원가입은 성공했으므로 사용자 정보만 저장
          user.value = responseData
          if (user.value) {
            localStorage.setItem('user', JSON.stringify(user.value))
          }
          console.warn('회원가입 후 자동 로그인 실패:', loginErr)
        }
      } else {
        throw new Error('회원가입 응답 형식이 올바르지 않습니다.')
      }

      return user.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        (typeof err.response?.data === 'string' ? err.response.data : null) ||
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
    refreshToken.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('authToken')
    localStorage.removeItem('refreshToken')
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
    refreshToken,
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

