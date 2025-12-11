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
  userId: number
  missionDate: string
  missionId: number
  currentCount: number
  isCompleted: boolean
  isRewarded: boolean
  createdAt: string
  // joined mission fields
  mission?: {
    missionId: number
    title: string
    missionType: string
    goalCount: number
    rewardExp: number
    isActive: boolean
  }
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
    if (!authStore.user) return
    try {
      await gamificationApi.claimMissionReward(missionId, authStore.user.userId)
      await fetchDailyMissions()
      await authStore.fetchUser()
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
    isLoading.value = true
    try {
      const response = await gamificationApi.getRankings()
      rankings.value = response.data
      return rankings.value
    } catch (err: any) {
      console.error('Failed to fetch rankings', err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchFriends() {
    if (!authStore.user) return

    isLoading.value = true
    try {
      const response = await gamificationApi.getFriends(authStore.user.userId)
      friends.value = response.data
      return friends.value
    } catch (err: any) {
      console.error('Failed to fetch friends', err)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function addFriend(nickname: string) {
    if (!authStore.user) return
    try {
      await gamificationApi.addFriend(authStore.user.userId, nickname)
      await fetchFriends()
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        '친구 추가에 실패했습니다.'
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
    addFriend,
    getUserAchievementIds,
    isAchievementUnlocked
  }
})

