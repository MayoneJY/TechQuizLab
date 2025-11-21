import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { quizData } from '../data/quizData'

export interface Quiz {
  id: number
  question: string
  options: string[]
  correctAnswer: number
  category: string
  explanation?: string
}

export const useGameStore = defineStore('game', () => {
  const currentQuestionIndex = ref(0)
  const score = ref(0)
  const lives = ref(8)
  const selectedAnswer = ref<number | null>(null)
  const gameStatus = ref<'playing' | 'gameOver' | 'victory'>('playing')
  const currentCategory = ref<string>('전체')
  
  const currentQuiz = computed(() => {
    const filtered = currentCategory.value === '전체' 
      ? quizData 
      : quizData.filter(q => q.category === currentCategory.value)
    return filtered[currentQuestionIndex.value]
  })
  
  const totalQuestions = computed(() => {
    return currentCategory.value === '전체' 
      ? quizData.length 
      : quizData.filter(q => q.category === currentCategory.value).length
  })
  
  const progress = computed(() => {
    return ((currentQuestionIndex.value + 1) / totalQuestions.value) * 100
  })
  
  function selectAnswer(answerIndex: number) {
    if (selectedAnswer.value !== null) return
    selectedAnswer.value = answerIndex
  }
  
  function submitAnswer() {
    if (selectedAnswer.value === null) return
    
    if (selectedAnswer.value === currentQuiz.value.correctAnswer) {
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
  }
  
  function nextQuestion() {
    selectedAnswer.value = null
    if (currentQuestionIndex.value < totalQuestions.value - 1) {
      currentQuestionIndex.value++
    } else {
      gameStatus.value = 'victory'
    }
  }
  
  function resetGame(category: string = '전체') {
    currentQuestionIndex.value = 0
    score.value = 0
    lives.value = 8
    selectedAnswer.value = null
    gameStatus.value = 'playing'
    currentCategory.value = category
  }
  
  function setCategory(category: string) {
    currentCategory.value = category
    resetGame(category)
  }
  
  return {
    currentQuestionIndex,
    score,
    lives,
    selectedAnswer,
    gameStatus,
    currentCategory,
    currentQuiz,
    totalQuestions,
    progress,
    selectAnswer,
    submitAnswer,
    resetGame,
    setCategory
  }
})

