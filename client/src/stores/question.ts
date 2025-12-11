import { defineStore } from 'pinia'
import { ref } from 'vue'
import { questionApi } from '../services/api'
import { useAuthStore } from './auth'

export interface Question {
  id: number
  topicId: number
  content: string
  answer: string
  difficulty: number
  createdAt?: string
}

export interface QuestionBookmark {
  id: number
  userId: number
  questionId: number
  memo?: string
  createdAt: string
}

export const useQuestionStore = defineStore('question', () => {
  const authStore = useAuthStore()
  const questions = ref<Question[]>([])
  const currentQuestion = ref<Question | null>(null)
  const bookmarks = ref<QuestionBookmark[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function fetchQuestionsByTopic(topicId: number) {
    isLoading.value = true
    error.value = null
    try {
      const response = await questionApi.getQuestionsByTopic(topicId)
      questions.value = response.data
      return questions.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '문제를 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchQuestionById(id: number) {
    isLoading.value = true
    try {
      const response = await questionApi.getQuestionById(id)
      currentQuestion.value = response.data
      return currentQuestion.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '문제를 불러오는데 실패했습니다.'
      error.value = errorMessage
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function submitAnswer(questionId: number, answer: string): Promise<boolean> {
    if (!authStore.user) throw new Error('로그인이 필요합니다.')

    try {
      const response = await questionApi.submitAnswer(questionId, authStore.user.userId, answer)
      return response.data
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '답안 제출에 실패했습니다.'
      error.value = errorMessage
      throw err
    }
  }

  async function bookmarkQuestion(questionId: number, memo?: string) {
    if (!authStore.user) throw new Error('로그인이 필요합니다.')

    try {
      await questionApi.bookmarkQuestion(questionId, authStore.user.userId, memo)
      await fetchBookmarks()
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '북마크 추가에 실패했습니다.'
      error.value = errorMessage
      throw err
    }
  }

  async function fetchBookmarks() {
    if (!authStore.user) return

    try {
      const response = await questionApi.getBookmarks(authStore.user.userId)
      bookmarks.value = response.data
      return bookmarks.value
    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '북마크를 불러오는데 실패했습니다.'
      error.value = errorMessage
    }
  }

  function reset() {
    questions.value = []
    currentQuestion.value = null
    error.value = null
  }

  return {
    questions,
    currentQuestion,
    bookmarks,
    isLoading,
    error,
    fetchQuestionsByTopic,
    fetchQuestionById,
    submitAnswer,
    bookmarkQuestion,
    fetchBookmarks,
    reset
  }
})

