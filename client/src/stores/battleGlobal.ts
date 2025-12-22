import { defineStore } from 'pinia'
import { ref } from 'vue'
import { battleApi } from '../services/api'
import router from '../router'
import { useToastStore } from './toast'

interface PendingBattle {
    stageId: number
    title: string
    category: string
    timestamp: number
}

const STORAGE_KEY = 'global_pending_battles'
const POLLING_INTERVAL_MS = 3000 // 3 seconds
const EXPIRY_MS = 5 * 60 * 1000 // 5 minutes

export const useBattleGlobalStore = defineStore('battleGlobal', () => {
    const toastStore = useToastStore()
    const pendingBattles = ref<PendingBattle[]>([])
    const isMonitoring = ref(false)
    let intervalId: number | null = null

    // Initialize from LocalStorage
    function init() {
        try {
            const stored = localStorage.getItem(STORAGE_KEY)
            if (stored) {
                pendingBattles.value = JSON.parse(stored)
                cleanExpired()
            }
        } catch (e) {
            console.error('Failed to load pending battles', e)
        }

        if (pendingBattles.value.length > 0) {
            startMonitoring()
        }
    }

    function addPendingBattle(item: PendingBattle) {
        // Avoid duplicates
        if (!pendingBattles.value.some(p => p.stageId === item.stageId)) {
            pendingBattles.value.push(item)
            saveToStorage()
            startMonitoring()
        }
    }

    function removePendingBattle(stageId: number) {
        pendingBattles.value = pendingBattles.value.filter(p => p.stageId !== stageId)
        saveToStorage()

        if (pendingBattles.value.length === 0) {
            stopMonitoring()
        }
    }

    function saveToStorage() {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(pendingBattles.value))
    }

    function cleanExpired() {
        const now = Date.now()
        const initialCount = pendingBattles.value.length
        pendingBattles.value = pendingBattles.value.filter(p => (now - p.timestamp) < EXPIRY_MS)

        if (pendingBattles.value.length !== initialCount) {
            saveToStorage()
        }
    }

    function startMonitoring() {
        if (isMonitoring.value || intervalId !== null) return

        console.log('[BattleGlobal] Start Monitoring...')
        isMonitoring.value = true
        intervalId = window.setInterval(checkStatus, POLLING_INTERVAL_MS)
    }

    function stopMonitoring() {
        if (intervalId !== null) {
            clearInterval(intervalId)
            intervalId = null
        }
        isMonitoring.value = false
        console.log('[BattleGlobal] Stop Monitoring')
    }

    async function checkStatus() {
        if (pendingBattles.value.length === 0) {
            stopMonitoring()
            return
        }

        cleanExpired()

        if (pendingBattles.value.length === 0) return

        try {
            // Check latest battles to see if any pending ones are done
            // We fetch page 0 size 20 to catch recent ones
            const res = await battleApi.getMyBattles({ page: 1, size: 20, sort: 'latest' })
            const activeBattles = res.data?.content || [] // Array of Battle objects

            // Find completed battles
            const completed = pendingBattles.value.filter(p => {
                // Check if this pending battle exists in the active battles list
                // Real battles have stageId
                // IMPORTANT: We must check that the battle is NEW (created AFTER our pending request started)
                // Otherwise, we might match an old battle history for the same stage.
                return activeBattles.some((b: any) => {
                    if (b.stageId !== p.stageId) return false

                    const battleCreated = new Date(b.createdAt).getTime()
                    // Allow some buffer or purely rely on 'after'. 
                    // Server time > Client time usually.
                    // If b.createdAt > p.timestamp, it's the new one.
                    return battleCreated > p.timestamp
                })
            })

            if (completed.length > 0) {
                console.log('[BattleGlobal] Found completed battles:', completed)

                // Stop monitoring temporarily
                stopMonitoring()

                // Remove completed from pending
                completed.forEach(c => removePendingBattle(c.stageId))

                // Show non-blocking toast notification
                const currentRoute = router.currentRoute.value.path
                if (currentRoute === '/my-battles') {
                    // If on target page, refresh logic (Toast + maybe silent refresh via component watching store)
                    toastStore.showToast('AI 면접관 생성이 완료되었습니다!', 'success')
                    // Ideally we trigger a data refresh here without full reload, 
                    // but for now let's leave reload if that's what triggers the list update
                    // Or better, let MyBattles.vue watch pendingBattles? 
                    // Since we removed it from pending, MyBattles syncPendingBattles will remove the loading item.
                    // But we need to fetch the REAL item.
                    // Let's just do a reload for safety on this page as before, but maybe cleaner?
                    // User asked for "Toast without redirect". 
                    // If on the page, a reload updates the list.
                    window.location.reload()
                } else {
                    // Just show toast, no redirect
                    toastStore.showToast('AI 면접관 생성이 완료되었습니다!', 'success')
                }

                // If there are still pending battles, resume monitoring
                if (pendingBattles.value.length > 0) {
                    startMonitoring()
                }
            }

        } catch (e) {
            console.error('[BattleGlobal] Check status failed', e)
        }
    }

    return {
        pendingBattles,
        addPendingBattle,
        removePendingBattle,
        init
    }
})
