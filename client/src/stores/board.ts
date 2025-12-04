import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { boardApi } from '../services/api'


export interface Post {
  id: number
  title: string
  content: string
  author: string
  authorId: number
  createdAt: string
  updatedAt: string
  views: number
  likes: number
  category: 'general' | 'question' | 'tip' | 'free'
}

export interface Comment {
  id: number
  postId: number
  content: string
  author: string
  authorId: number
  createdAt: string
}

const STORAGE_KEY = 'jobmonster_board_posts'
const STORAGE_COMMENTS_KEY = 'jobmonster_board_comments'

export const useBoardStore = defineStore('board', () => {
  const authStore = useAuthStore()
  const posts = ref<Post[]>([])
  const post = ref<Post | null>(null)
  const comments = ref<Comment[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // 로컬 스토리지에서 데이터 로드
  function loadFromStorage() {
    try {
      const storedPosts = localStorage.getItem(STORAGE_KEY)
      if (storedPosts) {
        posts.value = JSON.parse(storedPosts)
      } else {
        // 초기 목업 데이터
        initializeMockData()
      }

      const storedComments = localStorage.getItem(STORAGE_COMMENTS_KEY)
      if (storedComments) {
        comments.value = JSON.parse(storedComments)
      }
    } catch (error) {
      console.error('Failed to load board data from storage:', error)
      initializeMockData()
    }
  }

  // 초기 목업 데이터 생성
  function initializeMockData() {
    const mockPosts: Post[] = []

    posts.value = mockPosts
    saveToStorage()
  }

  // 로컬 스토리지에 저장
  function saveToStorage() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(posts.value))
      localStorage.setItem(STORAGE_COMMENTS_KEY, JSON.stringify(comments.value))
    } catch (error) {
      console.error('Failed to save board data to storage:', error)
    }
  }

  // 게시글 목록 가져오기
  function getPosts(category?: string) {
    if (category) {
      return boardApi.getAllPosts(); //카테고리가 잇을 때 api
    }
    return posts.value.sort((a, b) =>
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
  }

  // 게시글 상세 가져오기
  async function getPost(id: number) {
    isLoading.value = true;

    try {
      //제대로 받을 받아왔을 떄
      const response = await boardApi.getPostByPostId(id);
      post.value = response.data;
      return post;

    } catch (err: any) {
      const errorMessage = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        err.message ||
        '문제를 불러오는데 실패했습니다.'
      error
    } finally {
      isLoading.value = false;
    }
  }

  // 게시글 작성
  function createPost(title: string, content: string, category: Post['category']) {
    if (!authStore.user) {
      throw new Error('로그인이 필요합니다.')
    }

    const newPost: Post = {
      id: Date.now(),
      title,
      content,
      author: authStore.user.nickname || '익명',
      authorId: authStore.user.id,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      views: 0,
      likes: 0,
      category
    }

    posts.value.unshift(newPost)
    saveToStorage()
    return newPost
  }

  // 게시글 수정
  function updatePost(id: number, title: string, content: string) {
    if (!authStore.user) {
      throw new Error('로그인이 필요합니다.')
    }

    const post = posts.value.find(p => p.id === id)
    if (!post) {
      throw new Error('게시글을 찾을 수 없습니다.')
    }

    if (post.authorId !== authStore.user.id) {
      throw new Error('수정 권한이 없습니다.')
    }

    post.title = title
    post.content = content
    post.updatedAt = new Date().toISOString()
    saveToStorage()
    return post
  }

  // 게시글 삭제
  function deletePost(id: number) {
    if (!authStore.user) {
      throw new Error('로그인이 필요합니다.')
    }

    const post = posts.value.find(p => p.id === id)
    if (!post) {
      throw new Error('게시글을 찾을 수 없습니다.')
    }

    if (post.authorId !== authStore.user.id) {
      throw new Error('삭제 권한이 없습니다.')
    }

    posts.value = posts.value.filter(p => p.id !== id)
    comments.value = comments.value.filter(c => c.postId !== id)
    saveToStorage()
  }

  // 좋아요
  function toggleLike(id: number) {
    const post = posts.value.find(p => p.id === id)
    if (post) {
      post.likes++
      saveToStorage()
    }
  }

  // 댓글 가져오기
  function getComments(postId: number) {
    return comments.value
      .filter(c => c.postId === postId)
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
  }

  // 댓글 작성
  function createComment(postId: number, content: string) {
    if (!authStore.user) {
      throw new Error('로그인이 필요합니다.')
    }

    const newComment: Comment = {
      id: Date.now(),
      postId,
      content,
      author: authStore.user.nickname || '익명',
      authorId: authStore.user.id,
      createdAt: new Date().toISOString()
    }

    comments.value.push(newComment)
    saveToStorage()
    return newComment
  }

  // 댓글 삭제
  function deleteComment(id: number) {
    if (!authStore.user) {
      throw new Error('로그인이 필요합니다.')
    }

    const comment = comments.value.find(c => c.id === id)
    if (!comment) {
      throw new Error('댓글을 찾을 수 없습니다.')
    }

    if (comment.authorId !== authStore.user.id) {
      throw new Error('삭제 권한이 없습니다.')
    }

    comments.value = comments.value.filter(c => c.id !== id)
    saveToStorage()
  }

  // 초기화
  loadFromStorage()

  return {
    post,
    posts,
    comments,
    isLoading,
    error,
    getPosts,
    getPost,
    createPost,
    updatePost,
    deletePost,
    toggleLike,
    getComments,
    createComment,
    deleteComment

  }
})

