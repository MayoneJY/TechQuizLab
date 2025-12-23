<template>
  <div class="gamification-screen">

    
    <div class="gamification-container">
      <div class="header">
        <h1 class="pixel-text title">
          <span class="glitch" data-text="미션 및 랭킹">미션 및 랭킹</span>
        </h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div class="tabs">
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'missions' }"
          @click="activeTab = 'missions'"
        >
          미션
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'ranking' }"
          @click="activeTab = 'ranking'"
        >
          랭킹
        </button>
        <button 
          class="pixel-button tab-button" 
          :class="{ active: activeTab === 'friends' }"
          @click="activeTab = 'friends'"
        >
          친구
        </button>
      </div>

      <!-- Missions Tab -->
      <transition name="fade" mode="out-in">
        <div v-if="activeTab === 'missions'" class="tab-content" key="missions">
          
          <div v-if="isPageLoading || gamificationStore.isLoading" class="missions-list">
             <!-- Skeleton Loading -->
             <div v-for="i in 3" :key="i" class="mission-item glass-card skeleton-item">
                <div class="mission-header" style="width: 100%;">
                    <div class="skeleton skeleton-circle" style="width: 24px; height: 24px;"></div>
                    <div class="mission-info" style="width: 100%;">
                         <div class="skeleton skeleton-text" style="width: 40%; height: 24px; margin-bottom: 5px;"></div>
                         <div class="skeleton skeleton-text" style="width: 70%; height: 18px;"></div>
                    </div>
                </div>
                <div class="mission-progress-container">
                    <div class="skeleton skeleton-bar" style="width: 100%; height: 20px; border-radius: 10px;"></div>
                </div>
                <div class="skeleton skeleton-text" style="width: 80px; height: 32px; align-self: flex-end; border-radius: 4px;"></div>
             </div>
          </div>

          <div v-else-if="gamificationStore.dailyMissions.length === 0" class="empty-message pixel-text">
            오늘의 미션이 없습니다.
          </div>
          <div v-else class="missions-list">
            <div
              v-for="mission in gamificationStore.dailyMissions"
              :key="mission.missionId"
              class="mission-item glass-card"
              :class="{ completed: mission.isCompleted, claimed: mission.isRewarded }"
            >
              <div class="mission-header">
                <div class="mission-icon">📋</div>
                <div class="mission-info">
                  <p class="pixel-text mission-name">{{ mission.mission?.title || '일일 미션' }}</p>
                  <p class="mission-desc">{{ mission.mission?.missionType || '미션을 완료하세요!' }}</p>
                </div>
              </div>
              <div class="mission-progress-container">
                <div class="mission-progress">
                  <div class="progress-bar" :style="{ width: `${Math.min((mission.currentCount / (mission.mission?.goalCount || 1)) * 100, 100)}%` }"></div>
                  <p class="pixel-text progress-text">{{ mission.currentCount }} / {{ mission.mission?.goalCount }}</p>
                </div>
              </div>
              <div class="mission-actions">
                <button
                  v-if="mission.isCompleted && !mission.isRewarded"
                  class="pixel-button success small-action-btn"
                  @click="claimMission(mission.missionId)"
                >
                  보상 받기
                </button>
                <p v-else-if="mission.isRewarded" class="pixel-text claimed-text">완료됨</p>
                <p v-else class="pixel-text progress-status">진행 중</p>
              </div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>

        <!-- Ranking Tab -->
        <div v-else-if="activeTab === 'ranking'" class="tab-content" key="ranking">
          
          <div v-if="isPageLoading || gamificationStore.isLoading" class="ranking-list">
              <!-- Skeleton Loading -->
              <div v-for="i in 5" :key="i" class="ranking-item glass-card skeleton-item">
                  <div class="skeleton skeleton-text" style="width: 40px; height: 36px;"></div>
                  <div class="rank-info" style="flex: 1; margin-left: 15px;">
                      <div class="skeleton skeleton-text" style="width: 60%; height: 24px; margin-bottom: 5px;"></div>
                      <div class="skeleton skeleton-text" style="width: 30%; height: 16px;"></div>
                  </div>
                  <div class="skeleton skeleton-text" style="width: 80px; height: 22px;"></div>
              </div>
          </div>

          <div v-else-if="gamificationStore.rankings.length === 0" class="empty-message pixel-text">
            랭킹 데이터가 없습니다.
          </div>
          <div v-else class="ranking-list">
            <div 
              v-for="rank in gamificationStore.rankings" 
              :key="rank.userId"
              class="ranking-item glass-card"
              :class="{ 'my-rank': rank.userId === authStore.user?.userId }"
            >
              <div class="rank-number pixel-text">{{ rank.rank }}</div>
              <div class="rank-info">
                <p class="pixel-text rank-name">{{ rank.nickname }}</p>
                <p class="rank-level">Lv.{{ rank.level }}</p>
              </div>
              <div class="rank-exp pixel-text">{{ rank.exp }} EXP</div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>

        <!-- Friends Tab -->
        <div v-else-if="activeTab === 'friends'" class="tab-content" key="friends">
          
          <div class="tab-actions">
            <button class="pixel-button small-btn" @click="openAddFriendModal">
              + 친구 추가
            </button>
          </div>

          <div v-if="isPageLoading || gamificationStore.isLoading" class="friends-list">
              <div v-for="i in 3" :key="i" class="friend-item glass-card skeleton-item">
                  <div class="skeleton skeleton-circle" style="width: 32px; height: 32px;"></div>
                  <div class="friend-info" style="flex: 1; margin-left: 15px;">
                      <div class="skeleton skeleton-text" style="width: 50%; height: 24px; margin-bottom: 5px;"></div>
                      <div class="skeleton skeleton-text" style="width: 40%; height: 16px;"></div>
                  </div>
              </div>
          </div>

          <div v-else-if="gamificationStore.friends.length === 0" class="empty-message pixel-text">
             친구 목록이 비어있습니다.
          </div>
          <div v-else class="friends-list">
            <div 
              v-for="friend in gamificationStore.friends" 
              :key="friend.userId"
              class="friend-item glass-card"
              :class="{ 'is-rival': friend.isRival }"
            >
              <div class="friend-icon">{{ friend.isRival ? '⚔️' : '😊' }}</div>
              <div class="friend-info">
                <p class="pixel-text friend-name">{{ friend.nickname }}</p>
                <p class="friend-stats">Lv.{{ friend.level }} | Solved: {{ friend.solvedCount }}</p>
              </div>
              <div class="friend-action">
                <span v-if="friend.isRival" class="rival-badge pixel-text">라이벌</span>
              </div>
              <div class="scan-line"></div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- Add Friend Modal -->
    <Teleport to="body">
      <div v-if="isAddFriendModalOpen" class="modal-overlay" @click.self="closeAddFriendModal">
        <div class="glass-panel modal-card">
          <!-- Header -->
          <div class="modal-header">
            <span class="pixel-text modal-title">친구 요청</span>
            <div class="header-decoration">
              <div class="dot red"></div>
              <div class="dot yellow"></div>
              <div class="dot green"></div>
            </div>
          </div>

          <!-- Content -->
          <div class="modal-content">
            <div class="message-icon">
              <!-- Using an icon similar to PixelModal or a specific one -->
               <span class="pixel-emoji">🤝</span>
            </div>
            <p class="pixel-text modal-message">친구의 닉네임을 입력하세요.</p>
            
            <input 
              v-model="targetNickname" 
              type="text" 
              placeholder="닉네임 입력..." 
              class="pixel-input"
              @keyup.enter="submitAddFriend"
              ref="friendInput"
            />
          </div>

          <!-- Footer -->
          <div class="modal-buttons">
            <button class="pixel-button ghost-btn" @click="closeAddFriendModal">
              취소
            </button>
            <button 
              class="pixel-button primary" 
              @click="submitAddFriend" 
              :disabled="!targetNickname || isAddingFriend"
            >
              {{ isAddingFriend ? '추가 중...' : '추가' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
    <div class="monster monster-2">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGamificationStore } from '../stores/gamification'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()
const gamificationStore = useGamificationStore()
const authStore = useAuthStore()
const modalStore = useModalStore()

const activeTab = ref<'missions' | 'ranking' | 'friends'>('missions')
const isPageLoading = ref(true)

// Friend Modal State
const isAddFriendModalOpen = ref(false)
const targetNickname = ref('')
const isAddingFriend = ref(false)

function openAddFriendModal() {
  isAddFriendModalOpen.value = true
  targetNickname.value = ''
}

function closeAddFriendModal() {
  isAddFriendModalOpen.value = false
}

async function submitAddFriend() {
  if (!targetNickname.value) return
  isAddingFriend.value = true
  try {
    await gamificationStore.addFriend(targetNickname.value)
    await modalStore.openAlert('친구가 추가되었습니다!')
    closeAddFriendModal()
  } catch (error: any) {
    let msg = error.response?.data?.message || error.message || '실패했습니다.'
    // If msg format is "Prefix: Actual Message", show only "Actual Message"
    if (msg.includes(': ')) {
        const parts = msg.split(': ')
        if (parts.length > 1) {
            msg = parts[parts.length - 1]
        }
    }
    await modalStore.openAlert(msg)
  } finally {
    isAddingFriend.value = false
  }
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  try {
    isPageLoading.value = true
    await Promise.all([
      gamificationStore.fetchDailyMissions(),
      gamificationStore.fetchRankings(),
      gamificationStore.fetchFriends()
    ])
  } catch (error) {
    console.error('Failed to load gamification data:', error)
  } finally {
    isPageLoading.value = false
  }
})

function goHome() {
  router.push('/')
}

async function claimMission(missionId: number) {
  try {
    await gamificationStore.claimMissionReward(missionId)
  } catch (error: any) {
    const errorMessage = error.response?.data?.message || 
                        error.response?.data?.error || 
                        error.message ||
                        'Failed to claim reward.'
    await modalStore.openAlert(errorMessage)
  }
}
</script>

<style scoped>
/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0; 
  left: 0;
  width: 100%; 
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  z-index: 9990; /* Lower than global modal (9999) */
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(2px);
}
.modal-card {
  min-width: 360px;
  max-width: 90%;
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5), 0 0 20px rgba(74, 158, 255, 0.2);
  display: flex;
  flex-direction: column;
}

/* Header */
.modal-header {
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  color: #4a9eff;
  font-size: 14px;
  letter-spacing: 1px;
}

.header-decoration {
  display: flex;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  opacity: 0.7;
}

.dot.red { background: #ff6b6b; }
.dot.yellow { background: #ffd43b; }
.dot.green { background: #51cf66; }

/* Content */
.modal-content {
  padding: 30px 20px;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.message-icon {
  margin-bottom: 0;
}

.pixel-emoji {
    font-size: 40px;
}

.modal-message {
  font-size: 16px;
  color: #fff;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
}

.pixel-input {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid #555;
  color: #fff;
  padding: 12px;
  font-family: inherit;
  border-radius: 4px;
  outline: none;
  font-size: 16px;
  width: 100%;
  box-sizing: border-box;
  text-align: center;
}
.pixel-input:focus {
  border-color: #4a9eff;
}

/* Footer / Buttons */
.modal-buttons {
  padding: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(0, 0, 0, 0.2);
}

.ghost-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #aaa;
}
.ghost-btn:hover {
  border-color: #fff;
  color: #fff;
  background: rgba(255,255,255,0.1);
}

/* Button Variants */
.primary {
  color: #000;
  background: #4a9eff;
  border-color: #4a9eff;
}
.primary:hover:not(:disabled) {
  background: #3a8eef;
  box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
}
.secondary {
  background: transparent;
  border-color: #888;
  color: #888;
}
.secondary:hover {
  border-color: #ff6b6b;
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  box-shadow: 0 0 10px rgba(255, 107, 107, 0.3);
}
.tab-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
.small-btn {
  font-size: 14px;
  padding: 8px 16px;
}
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.gamification-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-x: hidden;
}

.gamification-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  justify-content: center;
}

.tab-button {
  flex: 1;
  min-width: 120px;
  font-size: 14px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: #888;
}

.tab-button:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.tab-button.active {
  background: rgba(74, 158, 255, 0.2);
  border-color: #4a9eff;
  color: #fff;
  box-shadow: 0 0 15px rgba(74, 158, 255, 0.3);
}

.tab-content {
  width: 100%;
  min-height: 400px;
}

.empty-message {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  background: rgba(0,0,0,0.3);
  border-radius: 8px;
  border: 1px dashed rgba(255,255,255,0.1);
}

.loading-text {
  text-align: center;
  color: #4a9eff;
  font-size: 16px;
  padding: 40px;
}

/* LIST ITEMS COMMON STYLES */
.achievement-item,
.mission-item,
.ranking-item,
.friend-item {
  position: relative;
  display: flex;
  gap: 15px;
  padding: 20px;
  margin-bottom: 15px;
  align-items: center;
  overflow: hidden; /* For scan-line */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.achievement-item:hover,
.mission-item:hover,
.ranking-item:hover,
.friend-item:hover {
  transform: translateY(-2px);
  border-color: rgba(74, 158, 255, 0.5);
  background: rgba(255, 255, 255, 0.1);
}

/* SCAN LINE ANIMATION */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(74, 158, 255, 0.1), transparent);
  transform: translateY(-100%);
  padding: 0;
  margin: 0;
  pointer-events: none;
}

.achievement-item:hover .scan-line,
.mission-item:hover .scan-line,
.ranking-item:hover .scan-line,
.friend-item:hover .scan-line {
  animation: scan 1.5s infinite linear;
}

@keyframes scan {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(200%); }
}

/* ACHIEVEMENTS */
.achievement-item.unlocked {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.1);
}

.achievement-icon {
  font-size: 32px;
  filter: grayscale(1);
  opacity: 0.5;
}

.achievement-item.unlocked .achievement-icon {
  filter: grayscale(0);
  opacity: 1;
  text-shadow: 0 0 10px rgba(255, 212, 59, 0.5);
}

.achievement-info {
  flex: 1;
}

.achievement-name {
  font-size: 16px;
  color: #aaa;
  margin-bottom: 4px;
}

.achievement-item.unlocked .achievement-name {
  color: #fff;
  font-weight: 700;
}

.achievement-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.achievement-item.unlocked .achievement-desc {
  color: #ccc;
}

.achievement-reward {
  font-size: 12px;
  color: #444;
}

.achievement-item.unlocked .achievement-reward {
  color: #51cf66;
}

.checkmark {
  font-size: 24px;
  color: #ffd43b;
  text-shadow: 0 0 10px #ffd43b;
}

/* MISSIONS */
.mission-item.completed {
  border-color: #51cf66;
  background: rgba(81, 207, 102, 0.1);
}

.mission-item.claimed {
  opacity: 0.6;
  filter: grayscale(0.5);
}

.mission-header {
  display: flex;
  gap: 15px;
  width: 100%;
}

.mission-item {
  flex-direction: column;
  align-items: flex-start;
}

.mission-icon {
  font-size: 24px;
}

.mission-info {
  flex: 1;
}

.mission-name {
  font-size: 16px;
  color: #fff;
  margin-bottom: 4px;
}

.mission-desc {
  font-size: 13px;
  color: #aaa;
}

.mission-progress-container {
  width: 100%;
  margin: 10px 0;
}

.mission-progress {
  width: 100%;
  height: 20px;
  background: rgba(0, 0, 0, 0.5);
  border: 2px solid rgba(255,255,255,0.2);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #4a9eff, #51cf66);
  transition: width 0.5s ease;
}

.progress-text {
  position: absolute;
  width: 100%;
  text-align: center;
  top: 0;
  line-height: 18px;
  font-size: 10px;
  color: #fff;
  text-shadow: 1px 1px 0 #000;
}

.mission-actions {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.small-action-btn {
  padding: 8px 16px;
  font-size: 12px;
}

.progress-status {
  font-size: 12px;
  color: #4a9eff;
}

.claimed-text {
  font-size: 12px;
  color: #51cf66;
}

/* RANKING */
.ranking-item {
  justify-content: space-between;
}

.ranking-item.my-rank {
  border-color: #ffd43b;
  background: rgba(255, 212, 59, 0.1);
  box-shadow: 0 0 15px rgba(255, 212, 59, 0.1);
}

.rank-number {
  font-size: 24px;
  color: #fff;
  width: 40px;
  text-align: center;
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 16px;
  color: #fff;
}

.rank-level {
  font-size: 12px;
  color: #888;
}

.rank-exp {
  font-size: 14px;
  color: #51cf66;
}

/* FRIENDS */
.friend-item.is-rival {
  border-color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.friend-icon {
  font-size: 24px;
}

.friend-info {
  flex: 1;
}

.friend-name {
  font-size: 16px;
  color: #fff;
}

.friend-stats {
  font-size: 12px;
  color: #888;
}

.rival-badge {
  font-size: 10px;
  color: #ff6b6b;
  border: 1px solid #ff6b6b;
  padding: 2px 6px;
  border-radius: 4px;
}

/* MONSTERS */
.monster {
  position: fixed;
  z-index: 1;
  pointer-events: none;
  opacity: 0.8;
}

.monster-1 {
  top: 150px;
  left: 5%;
  animation: float 4s ease-in-out infinite;
}

.monster-2 {
  bottom: 100px;
  right: 5%;
  animation: float 5s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

/* TRANSITIONS */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }
  
  .tab-button {
    min-width: auto;
    font-size: 12px;
    padding: 10px;
  }
}

/* Skeleton Effect */
.skeleton {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.skeleton::after {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  transform: translateX(-100%);
  background-image: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0,
    rgba(255, 255, 255, 0.1) 20%,
    rgba(255, 255, 255, 0.2) 60%,
    rgba(255, 255, 255, 0)
  );
  animation: shimmer 1.5s infinite;
}


.skeleton-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  min-height: 80px;
}

.mission-item.skeleton-item {
  flex-direction: column;
  align-items: flex-start;
  min-height: 150px;
}


.skeleton-circle {
  border-radius: 50%;
}

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}
</style>

