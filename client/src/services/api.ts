import axios from 'axios'

// 개발 환경에서는 Vite proxy를 사용하고, 프로덕션에서는 직접 API 호출
const API_BASE_URL = import.meta.env.DEV 
  ? '' // 개발 환경: Vite proxy 사용 (상대 경로)
  : (import.meta.env.VITE_API_BASE_URL || 'https://battle.mayonedev.com')

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000, // 10초 타임아웃
  withCredentials: false // CORS를 위해 false로 설정
})

// Request interceptor for auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  // 개발 환경에서 API 요청 로깅
  if (import.meta.env.DEV) {
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
      data: config.data,
      params: config.params,
      headers: config.headers
    })
  }
  
  return config
})

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    // 개발 환경에서 API 응답 로깅
    if (import.meta.env.DEV) {
      console.log(`[API Response] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        data: response.data
      })
    }
    return response
  },
  (error) => {
    // 개발 환경에서 API 에러 로깅
    if (import.meta.env.DEV) {
      console.error(`[API Error] ${error.config?.method?.toUpperCase()} ${error.config?.url}`, {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message
      })
    }
    
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('user')
      // 로그인 페이지로 리다이렉트
      if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
    }
    
    // 에러 메시지 개선
    if (error.response) {
      // 서버에서 에러 메시지가 오는 경우
      const errorData = error.response.data
      let errorMessage = '요청 처리 중 오류가 발생했습니다.'
      
      if (typeof errorData === 'string') {
        errorMessage = errorData
      } else if (errorData && typeof errorData === 'object') {
        errorMessage = errorData.message || 
                      errorData.error || 
                      errorData.msg ||
                      JSON.stringify(errorData)
      }
      
      error.message = errorMessage
      error.serverMessage = errorMessage
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      error.message = '서버에 연결할 수 없습니다. 네트워크를 확인해주세요.'
    } else {
      // 요청 설정 중 에러가 발생한 경우
      error.message = error.message || '요청을 보내는 중 오류가 발생했습니다.'
    }
    
    return Promise.reject(error)
  }
)

// User API
export const userApi = {
  login: (email: string, password: string) =>
    api.post('/api/users/login', { email, password }),
  
  register: (userDto: { email: string; password: string; nickname: string }) =>
    api.post('/api/users', userDto),
  
  getUser: (id: number) =>
    api.get(`/api/users/${id}`),
  
  checkEmailExists: (email: string) =>
    api.get(`/api/users/exists/email/${email}`),
  
  checkNicknameExists: (nickname: string) =>
    api.get(`/api/users/exists/nickname/${nickname}`)
}

// Topic API
export const topicApi = {
  getAllTopics: () =>
    api.get('/api/topics'),
  
  getTopicById: (id: number) =>
    api.get(`/api/topics/${id}`),
  
  getUserTopicLevel: (topicId: number, userId: number) =>
    api.get(`/api/topics/${topicId}/level?userId=${userId}`)
}

// Question API
export const questionApi = {
  getQuestionsByTopic: (topicId: number) =>
    api.get(`/api/questions?topicId=${topicId}`),
  
  getQuestionById: (id: number) =>
    api.get(`/api/questions/${id}`),
  
  submitAnswer: (id: number, userId: number, answer: string) =>
    api.post(`/api/questions/${id}/submit`, { userId, answer }),
  
  bookmarkQuestion: (id: number, userId: number, memo?: string) =>
    api.post(`/api/questions/${id}/bookmark`, { userId, memo }),
  
  getHistory: (userId: number) =>
    api.get(`/api/questions/history?userId=${userId}`),
  
  getBookmarks: (userId: number) =>
    api.get(`/api/questions/bookmarks?userId=${userId}`)
}

// Battle API
export const battleApi = {
  createBattle: (request: {
    stageId: number
    mode: string
    difficulty: number
    userId: number
  }) =>
    api.post('/api/battles', request),
  
  getBattle: (id: number) =>
    api.get(`/api/battles/${id}`),
  
  processTurn: (id: number, userId: number, answer: string) =>
    api.post(`/api/battles/${id}/turn`, { userId, answer }),
  
  finishBattle: (id: number) =>
    api.post(`/api/battles/${id}/finish`)
}

// Gamification API
export const gamificationApi = {
  getUserAchievements: (userId: number) =>
    api.get(`/api/gamification/my-achievements?userId=${userId}`),
  
  getDailyMissions: (userId: number) =>
    api.get(`/api/gamification/missions/daily?userId=${userId}`),
  
  claimMissionReward: (id: number) =>
    api.post(`/api/gamification/missions/${id}/claim`),
  
  getAllAchievements: () =>
    api.get('/api/gamification/achievements')
}

export default api

