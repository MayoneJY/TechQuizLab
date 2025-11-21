import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useQuestionStore } from './question'
import { useAuthStore } from './auth'

export interface Quiz {
  id: number
  question: string
  options: string[]
  correctAnswer: number
  category: string
  explanation?: string
}

export const useGameStore = defineStore('game', () => {
  const questionStore = useQuestionStore()
  const authStore = useAuthStore()
  
  const currentQuestionIndex = ref(0)
  const score = ref(0)
  const lives = ref(8)
  const selectedAnswer = ref<string | null>(null)
  const gameStatus = ref<'playing' | 'gameOver' | 'victory'>('playing')
  const topicId = ref<number | null>(null)
  const isLoading = ref(false)
  
  const currentQuiz = computed(() => {
    if (!questionStore.questions.length) return null
    const question = questionStore.questions[currentQuestionIndex.value]
    if (!question) return null
    
    // 서버의 content를 질문으로, answer를 정답으로 사용
    // API 응답 형식: { id, topicId, content, answer, difficulty, createdAt }
    return {
      id: question.id,
      question: question.content || question.question || '',
      answer: question.answer || '',
      difficulty: question.difficulty || 1,
      topicId: question.topicId
    }
  })
  
  const totalQuestions = computed(() => {
    return questionStore.questions.length
  })
  
  const progress = computed(() => {
    if (totalQuestions.value === 0) return 0
    return ((currentQuestionIndex.value + 1) / totalQuestions.value) * 100
  })
  
  function selectAnswer(answer: string) {
    if (selectedAnswer.value !== null) return
    selectedAnswer.value = answer
  }
  
  async function submitAnswer(): Promise<boolean> {
    if (selectedAnswer.value === null || !currentQuiz.value) return false
    
    isLoading.value = true
    try {
      const isCorrect = await questionStore.submitAnswer(
        currentQuiz.value.id,
        selectedAnswer.value
      )
      
      if (isCorrect) {
        score.value += 100
      } else {
        lives.value--
        if (lives.value <= 0) {
          gameStatus.value = 'gameOver'
        }
      }
      
      setTimeout(() => {
        nextQuestion()
      }, 2000)
      
      return isCorrect
    } catch (error: any) {
      console.error('Failed to submit answer:', error)
      // 에러를 다시 던져서 UI에서 처리할 수 있도록
      throw error
    } finally {
      isLoading.value = false
    }
  }
  
  function nextQuestion() {
    selectedAnswer.value = null
    if (currentQuestionIndex.value < totalQuestions.value - 1) {
      currentQuestionIndex.value++
    } else {
      gameStatus.value = 'victory'
    }
  }
  
  async function resetGame(topicIdValue: number) {
    currentQuestionIndex.value = 0
    score.value = 0
    lives.value = 8
    selectedAnswer.value = null
    gameStatus.value = 'playing'
    topicId.value = topicIdValue
    
    // 서버에서 문제 가져오기
    try {
      await questionStore.fetchQuestionsByTopic(topicIdValue)
      
      // 문제가 없으면 에러
      if (questionStore.questions.length === 0) {
        throw new Error('해당 주제에 문제가 없습니다.')
      }
    } catch (error: any) {
      console.error('Failed to load questions:', error)
      throw error
    }
  }
  
  async function setTopicId(topicIdValue: number) {
    topicId.value = topicIdValue
    await resetGame(topicIdValue)
  }
  
  return {
    currentQuestionIndex,
    score,
    lives,
    selectedAnswer,
    gameStatus,
    topicId,
    isLoading,
    currentQuiz,
    totalQuestions,
    progress,
    selectAnswer,
    submitAnswer,
    resetGame,
    setTopicId
  }
})

