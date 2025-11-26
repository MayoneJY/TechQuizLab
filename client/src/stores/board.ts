import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'

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
  const comments = ref<Comment[]>([])
  const isLoading = ref(false)

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
    const mockPosts: Post[] = [
      {
        id: 1,
        title: '취업 준비생들을 위한 조언',
        content: '안녕하세요! 취업 준비를 하면서 느낀 점들을 공유하고 싶어서 글을 씁니다.\n\n1. 이력서 작성 시 구체적인 성과를 명시하세요.\n2. 기술 스택은 실제로 사용해본 것만 적으세요.\n3. 면접 준비는 충분히 하되, 긴장하지 마세요.\n\n모두 화이팅입니다! 💪',
        author: '취준생A',
        authorId: 1,
        createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
        views: 156,
        likes: 23,
        category: 'tip'
      },
      {
        id: 2,
        title: '면접 질문 중 가장 어려웠던 질문은?',
        content: '면접을 보면서 가장 어려웠던 질문이 뭐였나요?\n\n저는 "왜 우리 회사를 선택했나요?"라는 질문이 항상 어려웠습니다.\n\n여러분은 어떤 질문이 어려웠나요?',
        author: '면접러',
        authorId: 2,
        createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
        views: 89,
        likes: 12,
        category: 'question'
      },
      {
        id: 3,
        title: '잡스페이스 게임 재밌네요!',
        content: '이 게임 정말 재밌습니다! 퀴즈 풀면서 공부도 되고 게임도 되고 일석이조네요.\n\n특히 업적 시스템이 마음에 듭니다. 더 많은 업적을 달성하고 싶어요!',
        author: '게이머123',
        authorId: 3,
        createdAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
        views: 45,
        likes: 8,
        category: 'free'
      },
      {
        id: 4,
        title: '프론트엔드 개발자 취업 후기',
        content: '안녕하세요! 프론트엔드 개발자로 취업한 후기를 공유합니다.\n\n주요 포인트:\n- 포트폴리오가 가장 중요했습니다\n- 기술 면접에서는 실제 프로젝트 경험이 도움이 되었습니다\n- 협업 경험을 강조하는 것이 좋았습니다\n\n질문 있으면 댓글 달아주세요!',
        author: '프론트개발자',
        authorId: 4,
        createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
        views: 234,
        likes: 45,
        category: 'general'
      }
    ]

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
      return posts.value.filter(post => post.category === category)
    }
    return posts.value.sort((a, b) => 
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
  }

  // 게시글 상세 가져오기
  function getPost(id: number): Post | undefined {
    const post = posts.value.find(p => p.id === id)
    if (post) {
      post.views++
      saveToStorage()
    }
    return post
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
    posts,
    comments,
    isLoading,
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

