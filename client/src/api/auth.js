import api from './index'
import { mockStats, mockHistory } from '@/mock/data'

// 목업 모드 활성화
const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

export const authApi = {
  signup: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      const userData = {
        id: 1,
        email: data.email,
        nickname: data.nickname,
      }
      localStorage.setItem('mock_user', JSON.stringify(userData))
      return { data: userData }
    }
    return api.post('/auth/signup', data)
  },
  login: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      // 저장된 사용자 정보가 있으면 사용, 없으면 새로 생성
      const savedUser = localStorage.getItem('mock_user')
      let userData
      if (savedUser) {
        userData = JSON.parse(savedUser)
      } else {
        userData = {
          id: 1,
          email: data.email,
          nickname: data.email.split('@')[0] || '사용자',
          token: 'mock-token-' + Date.now(),
        }
        localStorage.setItem('mock_user', JSON.stringify(userData))
      }
      return {
        data: {
          ...userData,
          token: 'mock-token-' + Date.now(),
        },
      }
    }
    return api.post('/auth/login', data)
  },
  getMe: async () => {
    if (USE_MOCK) {
      await delay(300)
      const savedUser = localStorage.getItem('mock_user')
      if (savedUser) {
        const userData = JSON.parse(savedUser)
        return { data: { ...userData, token: undefined } }
      }
      return {
        data: {
          id: 1,
          email: 'user@example.com',
          nickname: '개발자123',
        },
      }
    }
    return api.get('/auth/me')
  },
}
