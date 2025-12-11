import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { boardApi } from '../services/api'

//보여줄 게시글 
export interface Post {
    postId: number
    boardId: number
    userId: number
    title: string
    content: string
    viewCount: number
    createdAt: string
    nickname: string
    tags: string
    commentCount?: number
    level?: number
}

//게시글 작성 및 수정 (클라->백엔)
export interface PostCreateAndUpdateDto {
    title: string
    content: string
    tags: string
}

export const useBoardStore2 = defineStore('board2', () => {

    const authStore = useAuthStore() //로그인 유저 확인용 스토어 

    const posts = ref<Post[]>([]) // 게시글 목록

    const currentPost = ref<Post | null>(null) // 현재 선택된 게시글 상세

    const isLoading = ref(false) // 서버에 요청을 보내는 중이라는 표시용 (로딩바)

    const error = ref<string | null>(null) // 에러

    // 페이징 관련 상태
    const currentPage = ref(1) // 현재 페이지
    const pageSize = ref(10) // 페이지당 게시글 수
    const totalPages = ref(1) // 전체 페이지 수
    const totalCount = ref(0) // 전체 게시글 수 

    // 검색 및 정렬 상태
    const currentSearch = ref('')
    const currentSort = ref('latest') // 'latest', 'viewCount' 

    //모든 게시글 전체 조회
    async function fetchAllPosts(page?: number, size?: number) {
        isLoading.value = true
        error.value = null

        try {
            // 페이징 파라미터가 있으면 페이징 조회, 없으면 전체 조회
            // search, sort 추가
            const response = await boardApi.getAllPosts(page, size, currentSearch.value, currentSort.value)

            // 서버 응답 데이터 유효성 검사
            if (Array.isArray(response.data)) {
                // 페이징 없이 전체 조회한 경우
                posts.value = response.data
                currentPage.value = 1
                totalPages.value = 1
                totalCount.value = response.data.length
                console.log('게시글 목록 조회 성공:', posts.value.length, '개')
            } else if (response.data && typeof response.data === 'object' && response.data.posts) {
                // 페이징 조회한 경우
                posts.value = response.data.posts
                currentPage.value = response.data.currentPage || 1
                totalPages.value = response.data.totalPages || 1
                totalCount.value = response.data.totalCount || 0
                console.log('게시글 목록 조회 성공 (페이징):', posts.value.length, '개 / 전체:', totalCount.value, '개 / 페이지:', currentPage.value, '/', totalPages.value)
            } else {
                console.error('게시글 목록 조회 실패: 응답 데이터가 올바르지 않습니다.', response.data)
                posts.value = [] // 빈 배열로 초기화하여 화면 깨짐 방지
                error.value = '서버에서 올바르지 않은 데이터를 반환했습니다.'
            }

            return posts.value

        } catch (err: any) {
            // 에러 처리: 서버 응답 메시지 또는 기본 메시지 사용
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.message ||
                '게시글 목록을 불러오는데 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 목록 조회 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    //게시글 태그 조회
    async function fetchPostsByTags(tags: string, page?: number, size?: number) {
        isLoading.value = true
        error.value = null

        try {
            // boardApi.getPostByTags(tags) -> GET /api/boards/post/tags/{tags}
            const response = await boardApi.getPostbyTags(tags, page, size, currentSearch.value, currentSort.value)

            // 페이징 응답인지 확인
            if (response.data?.resvalue && typeof response.data.resvalue === 'object' && response.data.resvalue.posts) {
                // 페이징 조회한 경우
                const result = response.data.resvalue
                posts.value = result.posts || []
                currentPage.value = result.currentPage || 1
                totalPages.value = result.totalPages || 1
                totalCount.value = result.totalCount || 0
                console.log('게시글 태그 조회 성공 (페이징):', posts.value.length, '개 / 전체:', totalCount.value, '개 / 페이지:', currentPage.value, '/', totalPages.value)
            } else {
                // 페이징 없이 전체 조회한 경우
                const list = response.data?.resvalue ?? response.data  // 유연하게 처리

                if (Array.isArray(list)) {
                    posts.value = list
                    currentPage.value = 1
                    totalPages.value = 1
                    totalCount.value = list.length
                    console.log('게시글 태그 조회 성공:', posts.value.length, '개')
                } else {
                    console.error('게시글 태그 조회 실패: 응답 데이터가 배열이 아닙니다.', response.data)
                    posts.value = []
                    error.value = '서버에서 올바르지 않은 데이터를 반환했습니다.'
                }
            }

            return posts.value

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.message ||
                '게시글 태그 조회에 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 태그 조회 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }


    //게시글 상세 조회 - 복합키 사용
    async function fetchPostById(boardId: number, postId: number) {
        isLoading.value = true
        error.value = null

        try {
            // boardApi.getPostByPostId(boardId, postId) 호출 -> GET /api/boards/{board_id}/post/{post_id}
            const response = await boardApi.getPostByPostId(boardId, postId)

            // 백엔드 응답 구조: { resmsg: string, resvalue: Board }
            let postData = null;
            if (response.data && response.data.resvalue) {
                postData = response.data.resvalue
            } else {
                postData = response.data
            }

            currentPost.value = postData

            console.log('게시글 상세 조회 성공:', currentPost.value)
            return currentPost.value

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.message ||
                '게시글을 불러오는데 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 상세 조회 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    // 게시글 작성
    async function createPost(title: string, content: string, tags?: string) {
        // 로그인 체크
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            // 서버로 보낼 데이터 구성
            const postDto: PostCreateAndUpdateDto = {
                title,
                content,
                tags: tags || ''
            }

            // boardApi.createBoard(postDto) 호출 -> POST /api/boards/post
            const response = await boardApi.createBoard(postDto)

            console.log('게시글 작성 성공:', response.data)

            // 작성 후 목록 새로고침
            await fetchAllPosts()

            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.message ||
                '게시글 작성에 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 작성 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    //게시글 수정 - 복합키 사용
    async function updatePost(boardId: number, postId: number, title: string, content: string, tags?: string) {
        // 로그인 체크
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            // 서버로 보낼 데이터 구성
            const postDto: PostCreateAndUpdateDto = {
                title,
                content,
                tags: tags || ''
            }

            // boardApi.updatePost(boardId, postId, postDto) 호출 -> PATCH /api/boards/{board_id}/post/{post_id}
            const response = await boardApi.updatePost(boardId, postId, postDto)

            console.log('게시글 수정 성공:', response.data)

            // 수정 후 목록 새로고침
            await fetchAllPosts()

            // 현재 보고 있던 게시글이면 상세도 새로고침
            if (currentPost.value?.postId === postId) {
                await fetchPostById(boardId, postId)
            }

            return response.data

        } catch (err: any) {
            const errorMessage = err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.message ||
                '게시글 수정에 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 수정 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    //게시글 삭제 - 복합키 사용
    async function deletePost(boardId: number, postId: number) {

        // 로그인 체크
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            // boardApi.deletePost(boardId, postId) 호출 -> DELETE /api/boards/{board_id}/post/{post_id}
            const response = await boardApi.deletePost(boardId, postId)

            console.log('게시글 삭제 성공:', response.data)

            // 삭제 후 목록 새로고침
            await fetchAllPosts()

            // 현재 보고 있던 게시글이면 null로 초기화
            if (currentPost.value?.postId === postId) {
                currentPost.value = null
            }

            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.message ||
                '게시글 삭제에 실패했습니다.'

            error.value = errorMessage
            console.error('게시글 삭제 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    //에러 메세지 초기화
    function clearError() {
        error.value = null
    }

    //게시글 상세 조회 초기화
    function clearCurrentPost() {
        currentPost.value = null
    }

    //이 store 에서 외부에서도 사용할 것들
    return {
        // State
        posts,
        currentPost,
        isLoading,
        error,

        // 페이징 State
        currentPage,
        pageSize,
        totalPages,
        totalCount,

        // Actions
        fetchAllPosts,
        fetchPostById,
        fetchPostsByTags,
        createPost,
        updatePost,
        deletePost,
        clearError,
        clearCurrentPost,

        // Search/Sort State
        currentSearch,
        currentSort
    }
})