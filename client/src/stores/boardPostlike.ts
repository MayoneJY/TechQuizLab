import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAuthStore } from './auth'
import { boardPostlikeApi } from '../services/api'

export interface PostLike {
    board_id: number
    post_id: number
    user_id: number
    created_at: string
}

export interface PostLikeStatus {
    board_id: number
    post_id: number
    is_liked: boolean
    like_count: number
}

export const useBoardPostlikeStore = defineStore('boardPostlike', () => {
    const authStore = useAuthStore()

    const likeStatusMap = ref<Map<string, PostLikeStatus>>(new Map())
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    function getKey(boardId: number, postId: number): string {
        return `${boardId}_${postId}`
    }

    async function fetchLikeStatus(boardId: number, postId: number) {
        isLoading.value = true
        error.value = null

        try {
            const [likeStatusRes, likeCountRes] = await Promise.all([
                authStore.isAuthenticated
                    ? boardPostlikeApi.checkPostLike(boardId, postId)
                    : Promise.resolve({ data: { resvalue: { is_liked: false } } }),
                boardPostlikeApi.getPostLikeCount(boardId, postId)
            ])

            const isLiked = likeStatusRes.data?.resvalue?.is_liked || false
            const likeCount = likeCountRes.data?.resvalue?.like_count || 0

            const key = getKey(boardId, postId)
            likeStatusMap.value.set(key, {
                board_id: boardId,
                post_id: postId,
                is_liked: isLiked,
                like_count: likeCount
            })

            return likeStatusMap.value.get(key)!

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '좋아요 상태 조회에 실패했습니다.'

            error.value = errorMessage
            console.error('좋아요 상태 조회 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function addPostLike(boardId: number, postId: number) {
        if (!authStore.isAuthenticated) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            const response = await boardPostlikeApi.addPostLike(boardId, postId)

            const likeCount = response.data?.resvalue?.like_count || 0

            const key = getKey(boardId, postId)
            const currentStatus = likeStatusMap.value.get(key)

            likeStatusMap.value.set(key, {
                board_id: boardId,
                post_id: postId,
                is_liked: true,
                like_count: likeCount
            })

            console.log('좋아요 추가 성공:', response.data)
            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '좋아요 추가에 실패했습니다.'

            error.value = errorMessage
            console.error('좋아요 추가 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function removePostLike(boardId: number, postId: number) {
        if (!authStore.isAuthenticated) {
            error.value = '로그인이 필요합니다.'
            throw new Error('로그인이 필요합니다.')
        }

        isLoading.value = true
        error.value = null

        try {
            const response = await boardPostlikeApi.removePostLike(boardId, postId)

            const likeCount = response.data?.resvalue?.like_count || 0

            const key = getKey(boardId, postId)
            likeStatusMap.value.set(key, {
                board_id: boardId,
                post_id: postId,
                is_liked: false,
                like_count: likeCount
            })

            console.log('좋아요 취소 성공:', response.data)
            return response.data

        } catch (err: any) {
            const errorMessage =
                err.response?.data?.resmsg ||
                err.response?.data?.resvalue ||
                err.response?.data?.message ||
                err.message ||
                '좋아요 취소에 실패했습니다.'

            error.value = errorMessage
            console.error('좋아요 취소 실패:', errorMessage)
            throw err

        } finally {
            isLoading.value = false
        }
    }

    async function togglePostLike(boardId: number, postId: number) {
        const key = getKey(boardId, postId)
        const currentStatus = likeStatusMap.value.get(key)

        if (currentStatus?.is_liked) {
            await removePostLike(boardId, postId)
        } else {
            await addPostLike(boardId, postId)
        }
    }

    async function getLikeCount(boardId: number, postId: number) {
        try {
            const response = await boardPostlikeApi.getPostLikeCount(boardId, postId)
            const likeCount = response.data?.resvalue?.like_count || 0

            const key = getKey(boardId, postId)
            const currentStatus = likeStatusMap.value.get(key)

            if (currentStatus) {
                currentStatus.like_count = likeCount
            } else {
                likeStatusMap.value.set(key, {
                    board_id: boardId,
                    post_id: postId,
                    is_liked: false,
                    like_count: likeCount
                })
            }

            return likeCount

        } catch (err: any) {
            console.error('좋아요 개수 조회 실패:', err)
            return 0
        }
    }

    function getLikeStatus(boardId: number, postId: number): PostLikeStatus | null {
        const key = getKey(boardId, postId)
        return likeStatusMap.value.get(key) || null
    }

    function clearError() {
        error.value = null
    }

    function clearLikeStatus(boardId: number, postId: number) {
        const key = getKey(boardId, postId)
        likeStatusMap.value.delete(key)
    }

    function clearAllLikeStatus() {
        likeStatusMap.value.clear()
    }

    return {
        likeStatusMap,
        isLoading,
        error,
        fetchLikeStatus,
        addPostLike,
        removePostLike,
        togglePostLike,
        getLikeCount,
        getLikeStatus,
        clearError,
        clearLikeStatus,
        clearAllLikeStatus
    }
})

