import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    async login(email, password) {
      try {
        const response = await authApi.login({ email, password })
        const { token, ...user } = response.data
        
        this.token = token || 'temp-token' // 백엔드에서 토큰을 반환하지 않을 수 있으므로 임시 처리
        this.user = user
        
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
        
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '로그인에 실패했습니다.' }
      }
    },

    async signup(email, password, nickname) {
      try {
        await authApi.signup({ email, password, nickname })
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '회원가입에 실패했습니다.' }
      }
    },

    async getMe() {
      try {
        const response = await authApi.getMe()
        this.user = response.data
        localStorage.setItem('user', JSON.stringify(this.user))
        return { success: true }
      } catch (error) {
        return { success: false }
      }
    },

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})

