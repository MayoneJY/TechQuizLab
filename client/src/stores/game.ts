import { battleApi } from '../services/api'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useQuestionStore } from './question'


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
  // const authStore = useAuthStore()

  const currentQuestionIndex = ref(0)
  const score = ref(0)
  const lives = ref(8)
  const selectedAnswer = ref<string | null>(null)
  const gameStatus = ref<'playing' | 'gameOver' | 'victory' | 'grading'>('playing')
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
      question: question.content || '',
      answer: question.answer || '',
      difficulty: question.difficulty || 1,
      topicId: question.topicId,
      explanation: '' // AI Feedback or explanation can be added here if available in Question model
    }
  })

  // Computed for totalQuestions, but request was to set it.
  // Since it depends on questionStore, we can just let it be.
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

  async function resetGame(topicIdValue: number) {
    currentQuestionIndex.value = 0
    score.value = 0
    lives.value = 8
    selectedAnswer.value = null
    gameStatus.value = 'playing'
    topicId.value = topicIdValue
    battleId.value = null
    battleResult.value = null

    // 서버에서 문제 가져오기 (Battle 모드가 아닐 때 사용)
    try {
      if (topicIdValue > 0) {
        await questionStore.fetchQuestionsByTopic(topicIdValue)
      } else {
        // topicId == 0 or special case, maybe clear questions
        questionStore.questions = []
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

  function nextQuestion() {
    selectedAnswer.value = null
    if (currentQuestionIndex.value < totalQuestions.value - 1) {
      currentQuestionIndex.value++
    } else {
      // Last question submitted, now wait for external finish or explicit call
      // For now, we set status to victory so UI can respond, but we might want to wait for "Grading"
      // Actually, we should call finishGame() here or from the UI.
      // Let's rely on the UI to call finishGame when timer ends or last question is submitted.
      finishGame()
    }
  }

  async function submitAnswer(): Promise<boolean> {
    if (selectedAnswer.value === null || !currentQuiz.value) return false

    // 비동기 채점이므로 로컬 상태만 업데이트하고 API 호출
    isLoading.value = true
    try {
      // 서버에 답안 제출 (채점 X)
      await battleApi.processTurn(battleId.value!, 1, selectedAnswer.value)

      const isLastQuestion = currentQuestionIndex.value >= totalQuestions.value - 1

      if (isLastQuestion) {
        // 마지막 문제면 딜레이 없이 바로 채점/종료 프로세스로 이동
        nextQuestion()
        // isLoading은 finishGame에서 계속 true로 유지되거나 관리됨
      } else {
        // 다음 문제로 이동 (딜레이 둠)
        setTimeout(() => {
          nextQuestion()
          isLoading.value = false
        }, 500)
      }

      return true
    } catch (error: any) {
      console.error('Failed to submit answer:', error)
      isLoading.value = false
      throw error
    }
  }

  const battleId = ref<number | null>(null)

  async function loadBattleQuestions(id: number) {
    // Reset game state for new battle
    currentQuestionIndex.value = 0
    score.value = 0
    lives.value = 8
    selectedAnswer.value = null
    gameStatus.value = 'playing'
    battleResult.value = null

    battleId.value = id
    isLoading.value = true
    try {
      const response = await battleApi.getBattleDetails(id)
      const questions = response.data.map((detail: any) => ({
        id: detail.detailId,
        content: detail.questionText,
        answer: '', // AI 생성 문제라 정답이 DB에 없을 수 있음 (오픈 엔디드)
        difficulty: detail.difficulty,
        topicId: 0
      }))

      questionStore.questions = questions
      // totalQuestions is computed, so no need to set.
    } catch (e) {
      console.error(e)
    } finally {
      isLoading.value = false
    }
  }

  const battleResult = ref<any>(null)

  async function finishGame() {
    if (!battleId.value) return
    // 이미 완료되었거나 채점 중이면 중복 호출 방지
    if (gameStatus.value === 'grading' || gameStatus.value === 'victory' || gameStatus.value === 'gameOver') return

    // 채점 시작 상태로 변경
    gameStatus.value = 'grading'
    isLoading.value = true

    try {
      const response = await battleApi.finishBattle(battleId.value)
      battleResult.value = response.data
      gameStatus.value = 'victory'
    } catch (e) {
      console.error(e)
      // 에러 발생 시 처리 (일단 playing으로 되돌리거나 에러 상태로?)
      // 여기서는 UI가 멈추지 않게 일단 playing으로 리셋하거나 알림
      gameStatus.value = 'playing'
    } finally {
      isLoading.value = false
    }
  }

  async function giveUp() {
    if (!battleId.value) return
    try {
      await battleApi.deleteBattle(battleId.value)
    } catch (e) {
      console.error('Failed to delete battle on give up:', e)
    }
  }

  return {
    currentQuestionIndex,
    score,
    lives,
    selectedAnswer,
    gameStatus,
    topicId,
    battleId,
    isLoading,
    currentQuiz,
    totalQuestions,
    progress,
    battleResult,
    selectAnswer,
    submitAnswer,
    resetGame,
    setTopicId,
    loadBattleQuestions,
    finishGame,
    giveUp
  }
})

