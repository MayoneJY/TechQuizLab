import { defineStore } from 'pinia'
import { ref } from 'vue'
import { gamificationApi } from '../services/api'
import { useAuthStore } from './auth'

export interface Achievement {
  id: number
  name: string
  description: string
  conditionType: string
  conditionValue: number
  rewardExp: number
  imgUrl?: string
}

export interface UserAchievement {
  id: number
  userId: number
  achievementId: number
  achievedAt: string
}

export interface UserDailyMission {
  id: number
  userId: number
  dailyMissionId: number
  progress: number
  isCompleted: boolean
  isClaimed: boolean
  assignedDate: string
}

export const useGamificationStore = defineStore('gamification', () => {
  const authStore = useAuthStore()
  const achievements = ref<Achievement[]>([])
  const userAchievements = ref<UserAchievement[]>([])
  const dailyMissions = ref<UserDailyMission[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAllAchievements() {
    isLoading.value = true
    try {
      const response = await gamificationApi.getAllAchievements()
      achievements.value = response.data
      return achievements.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '업적 목록을 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchUserAchievements() {
    if (!authStore.user) return
    
    isLoading.value = true
    try {
      const response = await gamificationApi.getUserAchievements(authStore.user.id)
      userAchievements.value = response.data
      return userAchievements.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '업적을 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchDailyMissions() {
    if (!authStore.user) return
    
    isLoading.value = true
    try {
      const response = await gamificationApi.getDailyMissions(authStore.user.id)
      dailyMissions.value = response.data
      return dailyMissions.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '일일 미션을 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function claimMissionReward(missionId: number) {
    try {
      await gamificationApi.claimMissionReward(missionId)
      await fetchDailyMissions()
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '보상 수령에 실패했습니다.'
      error.value = errorMessage
      throw err
    }
  }

  function getUserAchievementIds(): number[] {
    return userAchievements.value.map(ua => ua.achievementId)
  }

  function isAchievementUnlocked(achievementId: number): boolean {
    return getUserAchievementIds().includes(achievementId)
  }

  return {
    achievements,
    userAchievements,
    dailyMissions,
    isLoading,
    error,
    fetchAllAchievements,
    fetchUserAchievements,
    fetchDailyMissions,
    claimMissionReward,
    getUserAchievementIds,
    isAchievementUnlocked
  }
})

