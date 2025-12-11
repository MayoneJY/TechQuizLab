import axios from 'axios'

// 개발 환경에서는 Vite proxy를 사용하고, 프로덕션에서는 직접 API 호출
const API_BASE_URL = import.meta.env.DEV
  ? '' // 개발 환경: Vite proxy 사용 (상대 경로)
  : (import.meta.env.VITE_API_BASE_URL || 'http://localhost:9033')
// : (import.meta.env.VITE_API_BASE_URL || 'https://battle.mayonedev.com')

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json; charset=utf-8',
    'Accept': 'application/json; charset=utf-8',
    'Accept-Charset': 'utf-8'
  },
  timeout: 60000, // 60초 타임아웃
  withCredentials: false, // CORS를 위해 false로 설정
  responseType: 'json',
  responseEncoding: 'utf8'
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
  async (error) => {
    // 개발 환경에서 API 에러 로깅
    if (import.meta.env.DEV) {
      console.error(`[API Error] ${error.config?.method?.toUpperCase()} ${error.config?.url}`, {
        status: error.response?.status,
        data: error.response?.data,
        message: error.message
      })
    }

    const originalRequest = error.config

    // 401 Unauthorized Error Handling (Token Refresh)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        // Refresh Token Call (must send cookies)
        const response = await axios.post(`${API_BASE_URL}/api/token/refresh`, {}, {
          withCredentials: true
        })

        const { accessToken } = response.data

        if (accessToken) {
          // Update Token
          localStorage.setItem('authToken', accessToken)
          api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
          originalRequest.headers['Authorization'] = `Bearer ${accessToken}`

          // Retry Original Request
          return api(originalRequest)
        }
      } catch (refreshError) {
        // Refresh Failed -> valid logout
        console.error('[API] Token refresh failed:', refreshError)
        localStorage.removeItem('authToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')

        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
        return Promise.reject(refreshError)
      }
    }

    // 401 but already retried or other 401s that shouldn't initiate refresh
    if (error.response?.status === 401 && originalRequest._retry) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
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
    api.post('/api/users/register', userDto),

  getUserByEmail: (email: string) =>
    api.get(`/api/users/email/${email}`),

  getUser: (id: number) =>
    api.get(`/api/users/${id}`),

  checkEmailExists: (email: string) =>
    api.get(`/api/users/exists/email/${email}`),

  checkNicknameExists: (nickname: string) =>
    api.get(`/api/users/exists/nickname/${nickname}`),

  updateUser: (id: number, userDto: { email?: string; password?: string; nickname?: string }) =>
    api.put(`/api/users/${id}`, userDto),

  deleteUser: (id: number) =>
    api.delete(`/api/users/${id}`)
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
    userId: number
  }) =>
    api.post('/api/battles', request),

  getBattle: (id: number) =>
    api.get(`/api/battles/${id}`),

  processTurn: (id: number, userId: number, answer: string) =>
    api.post(`/api/battles/${id}/turn`, { userId, answer }),

  finishBattle: (id: number) =>
    api.post(`/api/battles/${id}/finish`, {}, { timeout: 120000 }), // 2분 타임아웃

  deleteBattle: (battleId: number) =>
    api.delete(`/api/battles/${battleId}`),

  getBattleDetails: (id: number) =>
    api.get(`/api/battles/${id}/details`),

  bookmarkBattleDetail: (battleId: number, detailId: number, memo?: string) =>
    api.post(`/api/battles/${battleId}/details/${detailId}/bookmark`, { memo }),

  getMyBattles: () =>
    api.get('/api/battles'),

  getMyBookmarks: () =>
    api.get('/api/battles/bookmarks'),

  updateBookmark: (bookmarkId: number, memo: string) =>
    api.patch(`/api/battles/bookmarks/${bookmarkId}`, { memo }),

  deleteBookmark: (bookmarkId: number) =>
    api.delete(`/api/battles/bookmarks/${bookmarkId}`),

  createPracticeBattle: () =>
    api.post('/api/battles/practice/bookmarks', {})
}

// Gamification API
export const gamificationApi = {
  getUserAchievements: (userId: number) =>
    api.get(`/api/gamification/my-achievements?userId=${userId}`),

  getDailyMissions: (userId: number) =>
    api.get(`/api/gamification/missions/daily?userId=${userId}`),

  claimMissionReward: (id: number, userId: number) =>
    api.post(`/api/gamification/missions/${id}/claim?userId=${userId}`),

  getAllAchievements: () =>
    api.get('/api/gamification/achievements'),

  getRankings: () =>
    api.get('/api/gamification/rankings'),

  getFriends: (userId: number) =>
    api.get(`/api/gamification/friends?userId=${userId}`),

  addFriend: (userId: number, nickname: string) =>
    api.post(`/api/gamification/friends?userId=${userId}`, { nickname })
}

// Board API
export const boardApi = {
  getAllPosts: (page?: number, size?: number) => {
    if (page && size) {
      return api.get('/api/boards/post', { params: { page, size } })
    }
    return api.get('/api/boards/post')
  },

  // 복합키 사용: board_id와 post_id 모두 필요
  getPostByPostId: (boardId: number, postId: number) =>
    api.get(`/api/boards/${boardId}/post/${postId}`),

  getPostbyTags: (tags: string, page?: number, size?: number) => {
    if (page && size) {
      return api.get(`/api/boards/post/tags/${tags}`, { params: { page, size } })
    }
    return api.get(`/api/boards/post/tags/${tags}`)
  },

  createBoard: (postDto: { title: string; content: string; tags: string }) =>
    api.post('/api/boards/post', postDto),

  // 복합키 사용
  updatePost: (boardId: number, postId: number, postDto: { title: string; content: string; tags: string }) =>
    api.patch(`/api/boards/${boardId}/post/${postId}`, postDto),

  // 복합키 사용
  deletePost: (boardId: number, postId: number) =>
    api.delete(`/api/boards/${boardId}/post/${postId}`)
}

// Board Comment API
export const boardCommentApi = {
  getComments: (boardId: number, postId: number) =>
    api.get(`/api/boards/${boardId}/post/${postId}/comment`),

  createComment: (boardId: number, postId: number, content: string) =>
    api.post(`/api/boards/${boardId}/post/${postId}/comment`, { content }),

  createReply: (boardId: number, postId: number, parentCommentId: number, content: string) =>
    api.post(`/api/boards/${boardId}/post/${postId}/comment/${parentCommentId}/reply`, { content }),

  deleteComment: (boardId: number, postId: number, commentId: number) =>
    api.delete(`/api/boards/${boardId}/post/${postId}/comment/${commentId}`)
}

// Board Post Like API
export const boardPostlikeApi = {
  addPostLike: (boardId: number, postId: number) =>
    api.post(`/api/boards/${boardId}/post/${postId}/like`),

  removePostLike: (boardId: number, postId: number) =>
    api.delete(`/api/boards/${boardId}/post/${postId}/like`),

  getPostLikeCount: (boardId: number, postId: number) =>
    api.get(`/api/boards/${boardId}/post/${postId}/like/count`),

  checkPostLike: (boardId: number, postId: number) =>
    api.get(`/api/boards/${boardId}/post/${postId}/like/check`),

  getPostLikeList: (boardId: number, postId: number) =>
    api.get(`/api/boards/${boardId}/post/${postId}/like`)

}

// Portfolio API
export const portfolioApi = {
  getMyPortfolios: (userId: number) =>
    api.get(`/api/portfolios/user/${userId}`),

  createPortfolio: (portfolioDto: { userId: number; title: string; content: string }) =>
    api.post('/api/portfolios', portfolioDto),

  updatePortfolio: (pfId: number, portfolioDto: { userId: number; title: string; content: string }) =>
    api.put(`/api/portfolios/${pfId}`, portfolioDto),

  deletePortfolio: (pfId: number) =>
    api.delete(`/api/portfolios/${pfId}`)
}

// Stage API
export const stageApi = {
  getAllStages: () =>
    api.get('/api/stages'),

  getStageById: (stageId: number) =>
    api.get(`/api/stages/${stageId}`)
}

export default api
