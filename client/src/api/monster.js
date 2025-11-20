import api from './index'
import { mockMonsters, mockQuestions } from '@/mock/data'

const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

export const monsterApi = {
  getMonsters: async (params = {}) => {
    if (USE_MOCK) {
      await delay(500)
      let filtered = [...mockMonsters]
      
      if (params.jobType) {
        filtered = filtered.filter(m => m.jobTypeName === params.jobType)
      }
      if (params.status === 'ACTIVE') {
        filtered = filtered.filter(m => m.active)
      } else if (params.status === 'CLOSED') {
        filtered = filtered.filter(m => !m.active)
      }
      
      return { data: filtered }
    }
    return api.get('/monsters', { params })
  },
  getMonster: async (id) => {
    if (USE_MOCK) {
      await delay(300)
      const monster = mockMonsters.find(m => m.id === parseInt(id))
      if (!monster) {
        throw { response: { status: 404, data: '몬스터를 찾을 수 없습니다.' } }
      }
      return { data: monster }
    }
    return api.get(`/monsters/${id}`)
  },
  createMonster: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      return { data: { id: mockMonsters.length + 1, ...data } }
    }
    return api.post('/monsters', data)
  },
}
