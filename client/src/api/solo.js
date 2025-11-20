import api from './index'
import { mockQuestions } from '@/mock/data'

const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

let battleState = null
let questionIndex = 0

export const soloApi = {
  startSolo: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      const questions = mockQuestions.filter(q => q.monsterId === data.monsterId)
      const question = questions[0] || mockQuestions[0]
      
      battleState = {
        battleId: 'solo-' + Date.now(),
        monsterId: data.monsterId,
        questions: questions.length > 0 ? questions : mockQuestions.slice(0, 5),
        currentQuestionIndex: 0,
        totalXp: 0,
        totalDamage: 0,
      }
      
      questionIndex = 0
      
      return {
        data: {
          battleId: battleState.battleId,
          question: battleState.questions[0],
        },
      }
    }
    return api.post('/solo/start', data)
  },
  submitAnswer: async (data) => {
    if (USE_MOCK) {
      await delay(1500) // GPT 평가 시뮬레이션
      
      const qualityScore = 65 + Math.floor(Math.random() * 35)
      const xpTotal = Math.floor(qualityScore * 0.9)
      const damage = Math.floor(xpTotal * 1.1)
      
      battleState.totalXp += xpTotal
      battleState.totalDamage += damage
      questionIndex++
      
      const isFinished = questionIndex >= battleState.questions.length
      const nextQuestion = !isFinished ? battleState.questions[questionIndex] : null
      
      return {
        data: {
          qualityScore,
          xpTotal,
          damage,
          totalXp: battleState.totalXp,
          totalDamage: battleState.totalDamage,
          isFinished,
          nextQuestion,
          feedback: {
            good: [
              '핵심 내용을 잘 파악하고 있습니다.',
              '구체적인 예시를 들어 설명했습니다.',
            ],
            bad: [
              '더 깊이 있는 설명이 필요합니다.',
              '실무 경험을 추가하면 더 좋겠습니다.',
            ],
          },
        },
      }
    }
    return api.post('/solo/answer', data)
  },
}
