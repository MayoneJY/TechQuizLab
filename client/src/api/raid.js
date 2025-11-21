import api from './index'
import { mockRaidState, mockQuestions, mockMonsters, mockRaids } from '@/mock/data'

const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

export const raidApi = {
  getRaids: async (filters = {}) => {
    if (USE_MOCK) {
      await delay(300)
      return { data: mockRaids }
    }
    return api.get('/raids', { params: filters })
  },
  getRaid: async (id) => {
    if (USE_MOCK) {
      await delay(300)
      const raid = mockRaids.find(r => r.id === parseInt(id)) || mockRaids[0]
      return { data: raid }
    }
    return api.get(`/raids/${id}`)
  },
  createRaid: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      const question = mockQuestions.find(q => q.monsterId === data.monsterId) || mockQuestions[0]
      const monster = mockMonsters.find(m => m.id === data.monsterId) || mockMonsters[0]
      const newRaid = {
        id: mockRaids.length + 1,
        monsterId: data.monsterId,
        monsterName: monster.name,
        status: 'OPEN',
        currentHp: monster.hp,
        maxHp: monster.hp,
        participants: [],
        currentQuestion: question,
        createdAt: new Date().toISOString(),
        createdBy: { userId: 1, nickname: '현재사용자' },
      }
      mockRaids.unshift(newRaid)
      return { data: newRaid }
    }
    return api.post('/raids', data)
  },
  joinRaid: async (id) => {
    if (USE_MOCK) {
      await delay(300)
      const raid = mockRaids.find(r => r.id === parseInt(id))
      if (raid && raid.status === 'OPEN') {
        const currentUser = { userId: 1, nickname: '현재사용자' }
        if (!raid.participants.find(p => p.userId === currentUser.userId)) {
          raid.participants.push(currentUser)
          if (raid.participants.length >= 2) {
            raid.status = 'IN_PROGRESS'
          }
        }
      }
      return { data: { success: true } }
    }
    return api.post(`/raids/${id}/join`)
  },
  getRaidState: async (id) => {
    if (USE_MOCK) {
      await delay(300)
      const state = { ...mockRaidState }
      // 랜덤하게 HP 감소 시뮬레이션
      state.currentHp = Math.max(0, state.currentHp - Math.floor(Math.random() * 50))
      
      // 턴 정보 추가
      state.currentTurn = {
        turnNumber: 1,
        status: 'WAITING',
        question: state.currentQuestion,
        answers: state.answers || [],
        results: state.results || [],
      }
      
      // 턴 히스토리
      state.turnHistory = [
        { turnNumber: 1, status: 'WAITING' },
      ]
      
      return { data: state }
    }
    return api.get(`/raids/${id}/state`)
  },
  submitAnswer: async (id, data) => {
    if (USE_MOCK) {
      await delay(1500) // GPT 평가 시뮬레이션
      const qualityScore = 70 + Math.floor(Math.random() * 30)
      const xpTotal = Math.floor(qualityScore * 0.8)
      const damage = Math.floor(xpTotal * 1.2)
      
      return {
        data: {
          myDamage: damage,
          teamDamage: damage * 3, // 팀 데미지는 참여자 수에 비례
          myXp: xpTotal,
          qualityScore,
          feedback: {
            good: [
              '핵심 개념을 잘 이해하고 있습니다.',
              '구체적인 예시를 들어 설명했습니다.',
            ],
            bad: [
              '더 깊이 있는 설명이 필요합니다.',
              '실무 경험을 추가로 언급하면 좋겠습니다.',
            ],
          },
        },
      }
    }
    return api.post(`/raids/${id}/answer`, data)
  },
}
