import { defineStore } from 'pinia'
import { monsterApi } from '@/api/monster'

export const useMonsterStore = defineStore('monster', {
  state: () => ({
    monsters: [],
    currentMonster: null,
    loading: false,
  }),

  actions: {
    async fetchMonsters(params = {}) {
      this.loading = true
      try {
        const response = await monsterApi.getMonsters(params)
        this.monsters = response.data
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '몬스터 목록을 불러오는데 실패했습니다.' }
      } finally {
        this.loading = false
      }
    },

    async fetchMonster(id) {
      this.loading = true
      try {
        const response = await monsterApi.getMonster(id)
        this.currentMonster = response.data
        return { success: true }
      } catch (error) {
        return { success: false, message: error.response?.data || '몬스터 정보를 불러오는데 실패했습니다.' }
      } finally {
        this.loading = false
      }
    },
  },
})

