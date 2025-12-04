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
  missionName?: string
  description?: string
  progress: number
  isCompleted: boolean
  isClaimed: boolean
  assignedDate: string
  targetValue?: number
  currentValue?: number
}

export interface Ranking {
  userId: number
  nickname: string
  level: number
  exp: number
  rank: number
}

export interface FriendDisplay {
  userId: number
  nickname: string
  level: number
  exp: number
  isRival: boolean
  solvedCount: number
}

export const useGamificationStore = defineStore('gamification', () => {
  const authStore = useAuthStore()
  const achievements = ref<Achievement[]>([])
  const userAchievements = ref<UserAchievement[]>([])
  const dailyMissions = ref<UserDailyMission[]>([])
  const rankings = ref<Ranking[]>([])
  const friends = ref<FriendDisplay[]>([])
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
      const response = await gamificationApi.getUserAchievements(authStore.user.userId)
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
      const response = await gamificationApi.getDailyMissions(authStore.user.userId)
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

  async function fetchRankings() {
    // Mock data
    rankings.value = [
      { userId: 1, nickname: 'AlgorithmMaster', level: 10, exp: 5000, rank: 1 },
      { userId: 2, nickname: 'JavaKing', level: 8, exp: 3500, rank: 2 },
      { userId: 3, nickname: 'FrontendWizard', level: 7, exp: 3200, rank: 3 },
      { userId: 4, nickname: 'SSAFY_Ace', level: 6, exp: 2800, rank: 4 },
      { userId: 5, nickname: 'CodingBear', level: 5, exp: 2100, rank: 5 },
    ]
    return rankings.value
  }

  async function fetchFriends() {
    // Mock data
    friends.value = [
      { userId: 101, nickname: 'StudyMate', level: 4, exp: 1500, isRival: true, solvedCount: 45 },
      { userId: 102, nickname: 'RivalOne', level: 5, exp: 1800, isRival: true, solvedCount: 52 },
      { userId: 103, nickname: 'Newbie', level: 1, exp: 100, isRival: false, solvedCount: 5 },
    ]
    return friends.value
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
    rankings,
    friends,
    isLoading,
    error,
    fetchAllAchievements,
    fetchUserAchievements,
    fetchDailyMissions,
    fetchRankings,
    fetchFriends,
    claimMissionReward,
    getUserAchievementIds,
    isAchievementUnlocked
  }
})

