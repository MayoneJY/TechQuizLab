import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Game from '../views/Game.vue'
import Login from '../views/Login.vue'
import Gamification from '../views/Gamification.vue'
import TopicSelection from '../views/TopicSelection.vue'
import About from '../views/About.vue'
import Board from '../views/Board.vue'
import BoardDetail from '../views/BoardDetail.vue'
import BoardWrite from '../views/BoardWrite.vue'
import Portfolio from '../views/Portfolio.vue'
import StageList from '../views/StageList.vue'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/topics',
      name: 'topics',
      component: TopicSelection
    },
    {
      path: '/about',
      name: 'about',
      component: About
    },
    {
      path: '/game',
      name: 'game',
      component: Game,
      meta: { requiresAuth: true }
    },
    {
      path: '/gamification',
      name: 'gamification',
      component: Gamification,
      meta: { requiresAuth: true }
    },
    {
      path: '/portfolio',
      name: 'portfolio',
      component: Portfolio,
      meta: { requiresAuth: true }
    },
    {
      path: '/stages',
      name: 'stages',
      component: StageList
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('../views/MyPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/board',
      name: 'board',
      component: Board
    },
    {
      path: '/board/write',
      name: 'board-write',
      component: BoardWrite,
      meta: { requiresAuth: true }
    },
    {
      path: '/board/:id',
      name: 'board-detail',
      component: BoardDetail
    },
    {
      path: '/board/:id/edit',
      name: 'board-edit',
      component: BoardWrite,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router

