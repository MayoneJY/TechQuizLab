import { defineStore } from 'pinia'
import { statsApi } from '@/api/stats'

export const useStatsStore = defineStore('stats', {
  state: () => ({
    stats: [],
    history: [],
    loading: false,
  }),

  actions: {
    async fetchMyStats() {
      this.loading = true
      try {
        const response = await statsApi.getMyStats()
        this.stats = response.data
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '능력치를 불러오는데 실패했습니다.' }
      } finally {
        this.loading = false
      }
    },

    async fetchMyHistory() {
      this.loading = true
      try {
        const response = await statsApi.getMyHistory()
        this.history = response.data
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '이력을 불러오는데 실패했습니다.' }
      } finally {
        this.loading = false
      }
    },
  },
})

