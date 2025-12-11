import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { boardCommentApi } from '../services/api'

export interface Comment {
    commentId: number
    boardId: number
    postId: number
    userId: number
    parentCommentId: number | null
    content: string
    createdAt: string
    isDeleted: number
    nickname: string
}

export const useBoardCommentStore = defineStore('boardComment', () => {
    const authStore = useAuthStore()

    const comments = ref<Comment[]>([])
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    async function fetchComments(boardId: number, postId: number) {
        isLoading.value = true
        error.value = null

        try {
            const response = await boardCommentApi.getComments(boardId, postId)

            const commentList = response.data?.resvalue ?? response.data

            if (Array.isArray(commentList)) {
                comments.value = commentList
                console.log('댓글 목록 조회 성공:', comments.value.length, '개')
            } else {
                console.error('댓글 목록 조회 실패', response.data)
                comments.value = []
                error.value = '서버에서 올바르지 않은 데이터를 반환했습니다.'
            }

            return comments.value

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.message ||
                '댓글 목록을 불러오는데 실패했습니다.'

            error.value = errorMessage
            console.error('댓글 목록 조회 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function createComment(boardId: number, postId: number, content: string) {
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            const response = await boardCommentApi.createComment(boardId, postId, content)

            console.log('댓글 작성 성공:', response.data)

            await fetchComments(boardId, postId)

            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '댓글 작성에 실패했습니다.'

            error.value = errorMessage
            console.error('댓글 작성 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function createReply(boardId: number, postId: number, parentCommentId: number, content: string) {
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            const response = await boardCommentApi.createReply(boardId, postId, parentCommentId, content)

            console.log('대댓글 작성 성공:', response.data)

            await fetchComments(boardId, postId)

            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '대댓글 작성에 실패했습니다.'

            error.value = errorMessage
            console.error('대댓글 작성 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function deleteComment(boardId: number, postId: number, commentId: number) {
        if (!authStore.user) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            const response = await boardCommentApi.deleteComment(boardId, postId, commentId)

            console.log('댓글 삭제 성공:', response.data)

            await fetchComments(boardId, postId)

            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '댓글 삭제에 실패했습니다.'

            error.value = errorMessage
            console.error('댓글 삭제 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    function clearError() {
        error.value = null
    }

    function clearComments() {
        comments.value = []
    }

    return {
        comments,
        isLoading,
        error,
        fetchComments,
        createComment,
        createReply,
        deleteComment,
        clearError,
        clearComments
    }
})
