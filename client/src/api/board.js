import api from './index'
import { mockBoardPosts } from '@/mock/data'

const USE_MOCK = true

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

let posts = [...mockBoardPosts]
let nextPostId = posts.length + 1
let nextCommentId = 8

export const boardApi = {
  getPosts: async (params = {}) => {
    if (USE_MOCK) {
      await delay(400)
      let filtered = [...posts]
      
      if (params.category) {
        filtered = filtered.filter(p => p.category === params.category)
      }
      
      // 최신순 정렬
      filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      
      return { data: filtered }
    }
    return api.get('/board', { params })
  },
  getPost: async (id) => {
    if (USE_MOCK) {
      await delay(300)
      const post = posts.find(p => p.id === parseInt(id))
      if (!post) {
        throw { response: { status: 404, data: '게시글을 찾을 수 없습니다.' } }
      }
      return { data: post }
    }
    return api.get(`/board/${id}`)
  },
  createPost: async (data) => {
    if (USE_MOCK) {
      await delay(500)
      const newPost = {
        id: nextPostId++,
        ...data,
        authorNickname: '나',
        createdAt: new Date().toISOString(),
        comments: [],
      }
      posts.unshift(newPost)
      return { data: newPost }
    }
    return api.post('/board', data)
  },
  createComment: async (id, data) => {
    if (USE_MOCK) {
      await delay(400)
      const post = posts.find(p => p.id === parseInt(id))
      if (!post) {
        throw { response: { status: 404, data: '게시글을 찾을 수 없습니다.' } }
      }
      const newComment = {
        id: nextCommentId++,
        ...data,
        authorNickname: '나',
        createdAt: new Date().toISOString(),
      }
      post.comments = post.comments || []
      post.comments.push(newComment)
      return { data: newComment }
    }
    return api.post(`/board/${id}/comments`, data)
  },
}
