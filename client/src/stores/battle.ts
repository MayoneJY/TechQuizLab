import { defineStore } from 'pinia'
import { ref } from 'vue'
import { battleApi } from '../services/api'
import { useAuthStore } from './auth'

export interface Battle {
  userId: number
  battleId: number
  stageId: number
  pfId: number
  totalDamage: number
  status: string
  createdAt: string
}

export interface BattleDetail {
  userId: number
  battleId: number
  detailId: number
  questionText: string
  keywordTags: string
  difficulty: number
  userAnswer: string
  aiFeedback: string
  damage: number
  createdAt: string
}

export const useBattleStore = defineStore('battle', () => {
  const authStore = useAuthStore()
  const currentBattle = ref<Battle | null>(null)
  const currentDetail = ref<BattleDetail | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function createBattle(stageId: number) {
    if (!authStore.user) throw new Error('로그인이 필요합니다.')

    isLoading.value = true
    error.value = null
    try {
      const response = await battleApi.createBattle({
        stageId,
        userId: authStore.user.userId
      })
      currentBattle.value = response.data
      return currentBattle.value
    } catch (err: any) {
      error.value = err.response?.data || '배틀 생성에 실패했습니다.'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function getBattle(battleId: number) {
    if (!authStore.user) throw new Error('로그인이 필요합니다.')

    isLoading.value = true
    error.value = null
    try {
      const response = await battleApi.getBattle(battleId)
      currentBattle.value = response.data
      return currentBattle.value
    } catch (err: any) {
      error.value = err.response?.data || '배틀 조회에 실패했습니다.'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function processTurn(answer: string) {
    if (!currentBattle.value || !authStore.user) throw new Error('배틀이 시작되지 않았습니다.')

    isLoading.value = true
    error.value = null
    try {
      await battleApi.processTurn(
        currentBattle.value.battleId,
        authStore.user.userId,
        answer
      )
      // Since API returns void, we might need to fetch the detail or just acknowledge success.
      // For now, just return.
    } catch (err: any) {
      error.value = err.response?.data || '턴 진행에 실패했습니다.'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function finishBattle() {
    if (!currentBattle.value) return

    isLoading.value = true
    try {
      await battleApi.finishBattle(currentBattle.value.battleId)
      currentBattle.value = null
      currentDetail.value = null
    } catch (err: any) {
      error.value = err.response?.data || '배틀 종료에 실패했습니다.'
    } finally {
      isLoading.value = false
    }
  }

  function resetBattle() {
    currentBattle.value = null
    currentDetail.value = null
    error.value = null
  }

  return {
    currentBattle,
    currentDetail,
    isLoading,
    error,
    createBattle,
    getBattle,
    processTurn,
    finishBattle,
    resetBattle
  }
})


