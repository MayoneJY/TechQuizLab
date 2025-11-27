import { defineStore } from 'pinia'
import { ref } from 'vue'
import { topicApi } from '../services/api'

export interface Topic {
  id: number
  name: string
}

export interface UserTopicLevel {
  id: number
  userId: number
  topicId: number
  level: number
  exp: number
}

export const useTopicStore = defineStore('topic', () => {
  const topics = ref<Topic[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function fetchTopics() {
    isLoading.value = true
    error.value = null
    try {
      const response = await topicApi.getAllTopics()
      topics.value = response.data
      return topics.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.response?.data ||
                          err.message ||
                          '주제 목록을 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function getUserTopicLevel(topicId: number, userId: number): Promise<UserTopicLevel | null> {
    try {
      const response = await topicApi.getUserTopicLevel(topicId, userId)
      return response.data
    } catch {
      return null
    }
  }

  return {
    topics,
    isLoading,
    error,
    fetchTopics,
    getUserTopicLevel
  }
})

