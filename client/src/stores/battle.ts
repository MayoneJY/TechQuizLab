import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { battleApi, questionApi } from '../services/api'
import { useAuthStore } from './auth'

export interface Battle {
  id: number
  stageId: number
  mode: string
  status: string
  difficulty: number
  createdAt: string
  closedAt?: string
}

export interface BattleTurn {
  id: number
  battleId: number
  turnNo: number
  attackerId: number
  questionText: string
  answerText: string
  multiplier: number
  damage: number
  createdAt: string
}

export const useBattleStore = defineStore('battle', () => {
  const authStore = useAuthStore()
  const currentBattle = ref<Battle | null>(null)
  const currentTurn = ref<BattleTurn | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function createBattle(topicId: number, mode: string = 'single', difficulty: number = 1) {
    if (!authStore.user) throw new Error('로그인이 필요합니다.')
    
    isLoading.value = true
    error.value = null
    try {
      const response = await battleApi.createBattle({
        stageId: topicId,
        mode,
        difficulty,
        userId: authStore.user.id
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

  async function processTurn(answer: string) {
    if (!currentBattle.value || !authStore.user) throw new Error('배틀이 시작되지 않았습니다.')
    
    isLoading.value = true
    error.value = null
    try {
      const response = await battleApi.processTurn(
        currentBattle.value.id,
        authStore.user.id,
        answer
      )
      currentTurn.value = response.data
      return currentTurn.value
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
      await battleApi.finishBattle(currentBattle.value.id)
      currentBattle.value = null
      currentTurn.value = null
    } catch (err: any) {
      error.value = err.response?.data || '배틀 종료에 실패했습니다.'
    } finally {
      isLoading.value = false
    }
  }

  function resetBattle() {
    currentBattle.value = null
    currentTurn.value = null
    error.value = null
  }

  return {
    currentBattle,
    currentTurn,
    isLoading,
    error,
    createBattle,
    processTurn,
    finishBattle,
    resetBattle
  }
})

