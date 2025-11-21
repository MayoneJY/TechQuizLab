import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'onboarding',
      component: () => import('@/views/OnboardingView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignupView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/monsters',
      name: 'monsters',
      component: () => import('@/views/MonstersView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/monsters/:id',
      name: 'monster-detail',
      component: () => import('@/views/MonsterDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/solo/:monsterId',
      name: 'solo-battle',
      component: () => import('@/views/SoloBattleView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/raids',
      name: 'raids',
      component: () => import('@/views/RaidsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/raids/:id/waiting',
      name: 'raid-waiting',
      component: () => import('@/views/RaidWaitingRoomView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/raids/:id/battle',
      name: 'raid-battle',
      component: () => import('@/views/RaidBattleView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/raids/:id',
      name: 'raid-detail',
      redirect: (to) => {
        // 레이드 상태에 따라 대기방 또는 전투로 리다이렉트
        return { name: 'raid-waiting', params: { id: to.params.id } }
      },
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/board',
      name: 'board',
      component: () => import('@/views/BoardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/board/:id',
      name: 'board-detail',
      component: () => import('@/views/BoardDetailView.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
