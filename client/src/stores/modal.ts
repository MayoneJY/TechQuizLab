import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ModalType = 'alert' | 'confirm'

export const useModalStore = defineStore('modal', () => {
    const isVisible = ref(false)
    const message = ref('')
    const type = ref<ModalType>('alert')

    // Resolve function to return promise structure
    let resolvePromise: (value: boolean) => void = () => { }

    function openAlert(msg: string): Promise<boolean> {
        message.value = msg
        type.value = 'alert'
        isVisible.value = true

        return new Promise((resolve) => {
            resolvePromise = resolve
        })
    }

    function openConfirm(msg: string): Promise<boolean> {
        message.value = msg
        type.value = 'confirm'
        isVisible.value = true

        return new Promise((resolve) => {
            resolvePromise = resolve
        })
    }

    function confirmAction() {
        isVisible.value = false
        resolvePromise(true)
    }

    function cancelAction() {
        isVisible.value = false
        resolvePromise(false)
    }

    return {
        isVisible,
        message,
        type,
        openAlert,
        openConfirm,
        confirmAction,
        cancelAction
    }
})
