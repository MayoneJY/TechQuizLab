import api from './index'
import { mockStats, mockHistory } from '@/mock/data'

const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

export const statsApi = {
  getMyStats: async () => {
    if (USE_MOCK) {
      await delay(400)
      return { data: mockStats }
    }
    return api.get('/me/stats')
  },
  getMyHistory: async () => {
    if (USE_MOCK) {
      await delay(400)
      return { data: mockHistory }
    }
    return api.get('/me/history')
  },
}
