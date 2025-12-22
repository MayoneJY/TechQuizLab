import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ToastType = 'success' | 'info' | 'error' | 'warning'

export const useToastStore = defineStore('toast', () => {
    const isVisible = ref(false)
    const message = ref('')
    const type = ref<ToastType>('info')
    let timeoutId: number | null = null

    function showToast(msg: string, toastType: ToastType = 'info', duration = 3000) {
        // Clear existing timeout if any
        if (timeoutId !== null) {
            clearTimeout(timeoutId)
            timeoutId = null
        }

        message.value = msg
        type.value = toastType
        isVisible.value = true

        timeoutId = window.setTimeout(() => {
            hideToast()
        }, duration)
    }

    function hideToast() {
        isVisible.value = false
        if (timeoutId !== null) {
            clearTimeout(timeoutId)
            timeoutId = null
        }
    }

    return {
        isVisible,
        message,
        type,
        showToast,
        hideToast
    }
})
