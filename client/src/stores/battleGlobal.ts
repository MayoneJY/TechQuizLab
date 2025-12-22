import { defineStore } from 'pinia'
import { ref } from 'vue'
import { battleApi } from '../services/api'
import router from '../router'

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
                // Real battles have stageId (or we can match by title if stageId missing in list, but stageId is safer)
                // The API response for 'MyBattles' returns items with 'stageId'.
                // Let's verify schema.ts: Battle has 'stageId'.
                return activeBattles.some((b: any) => b.stageId === p.stageId)
            })

            if (completed.length > 0) {
                console.log('[BattleGlobal] Found completed battles:', completed)

                // Remove completed from pending
                completed.forEach(c => removePendingBattle(c.stageId))

                // Redirect to MyBattles if not already there
                // But only if we are currently on a different page that allows interruption
                const currentRoute = router.currentRoute.value.path
                if (currentRoute !== '/my-battles' && !currentRoute.startsWith('/game')) {
                    console.log('[BattleGlobal] Redirecting to my-battles')
                    router.push('/my-battles')
                } else if (currentRoute === '/my-battles') {
                    // If already on page, we might want to trigger a refresh
                    // But MyBattles.vue will likely poll or manually refresh.
                    // Instead, we can let MyBattles.vue watch the global store or just rely on the user refreshing?
                    // The requirement says: "If on another page, force redirect". 
                    // If on same page, it just updates. MyBattles.vue should use this store's state to hide the "loading" item.
                    window.location.reload()
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
