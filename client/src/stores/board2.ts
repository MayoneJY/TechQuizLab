import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { boardApi } from '../services/api'

//보여줄 게시글 
export interface Post {
    post_id: number              // 게시글 ID (post_id)
    board_id: number             // 게시판 ID
    user_id: number              // 작성자 ID
    title: string                // 게시글 제목
    content: string              // 게시글 내용
    view_count: number           // 조회수
    created_at: string           // 작성 시간 (LocalDateTime -> string으로 변환됨)
    nickname: string             // 작성자 닉네임
    tags: string                 // 태그 (쉼표로 구분된 문자열)
    comment_count?: number       // 댓글 개수
}

//게시글 작성 및 수정 (클라->백엔)
export interface PostCreateAndUpdateDto {
    title: string           // 게시글 제목
    content: string         // 게시글 내용
    tags: string            // 태그
}

export const useBoardStore2 = defineStore('board2', () => {

    const authStore = useAuthStore() //로그인 유저 확인용 스토어 

    const posts = ref<Post[]>([]) // 게시글 목록

    const currentPost = ref<Post | null>(null) // 현재 선택된 게시글 상세

    const isLoading = ref(false) // 서버에 요청을 보내는 중이라는 표시용 (로딩바)

    const error = ref<string | null>(null) // 에러 

    //모든 게시글 전체 조회
    async function fetchAllPosts() {
        isLoading.value = true
        error.value = null

        try {
            // boardApi.getAllPosts() 호출 -> GET /api/boards/post
            const response = await boardApi.getAllPosts()

            // 서버 응답 데이터 유효성 검사
            if (Array.isArray(response.data)) {
                // 서버에서 받은 데이터를 posts에 저장
                posts.value = response.data
                console.log('게시글 목록 조회 성공:', posts.value.length, '개')
            } else {
                console.error('게시글 목록 조회 실패: 응답 데이터가 배열이 아닙니다.', response.data)
                posts.value = [] // 빈 배열로 초기화하여 화면 깨짐 방지
                // 에러 메시지 설정 (선택 사항)
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
    async function fetchPostsByTags(tags: string) {
        isLoading.value = true
        error.value = null

        try {
            // boardApi.getPostByTags(tags) -> GET /api/boards/post/tags/{tags}
            const response = await boardApi.getPostbyTags(tags)

            const list = response.data?.resvalue ?? response.data  // 유연하게 처리

            if (Array.isArray(list)) {
                posts.value = list
                console.log('게시글 태그 조회 성공:', posts.value.length, '개')
            } else {
                console.error('게시글 태그 조회 실패: 응답 데이터가 배열이 아닙니다.', response.data)
                posts.value = []
                error.value = '서버에서 올바르지 않은 데이터를 반환했습니다.'
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
            if (response.data && response.data.resvalue) {
                currentPost.value = response.data.resvalue
            } else {
                currentPost.value = response.data
            }

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
            if (currentPost.value?.post_id === postId) {
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
            if (currentPost.value?.post_id === postId) {
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

        // Actions
        fetchAllPosts,
        fetchPostById,
        fetchPostsByTags,
        createPost,
        updatePost,
        deletePost,
        clearError,
        clearCurrentPost
    }
})