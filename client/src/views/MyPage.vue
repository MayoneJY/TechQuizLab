<template>
  <div class="mypage-screen">
    <div class="mypage-container glass-panel">
      <div class="header">
        <h1 class="pixel-text title">마이페이지</h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div v-if="isLoading" class="loading-state">
        <p class="pixel-text">로딩 중...</p>
      </div>

      <div v-else class="content">
        <!-- Profile View Mode -->
        <div v-if="!isEditing" class="profile-view">
          <div class="profile-avatar">
            <span class="avatar-icon">👾</span>
          </div>
          
          <div class="info-group">
            <label class="pixel-text label">닉네임</label>
            <div class="info-value pixel-text">{{ user?.nickname }}</div>
          </div>
          
          <div class="info-group">
            <label class="pixel-text label">이메일</label>
            <div class="info-value pixel-text">{{ user?.email }}</div>
          </div>

          <div class="stats-group">
            <div class="stat-item">
              <span class="stat-label">레벨</span>
              <span class="stat-value pixel-text">Lv.{{ user?.level }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">경험치</span>
              <span class="stat-value pixel-text">{{ user?.exp }}</span>
            </div>
          </div>

          <div class="actions">
            <button class="pixel-button primary" @click="startEditing">
              프로필 수정
            </button>
            <button class="pixel-button danger small" @click="handleDeleteAccount">
              회원 탈퇴
            </button>
          </div>
        </div>

        <!-- Edit Mode -->
        <div v-else class="edit-form">
          <h2 class="pixel-text section-title">프로필 수정</h2>
          
          <div class="form-group">
            <label class="pixel-text label">닉네임</label>
            <input 
              v-model="editForm.nickname" 
              type="text" 
              class="pixel-input glass-input"
              placeholder="새로운 닉네임 입력"
            />
          </div>

          <div class="form-group">
            <label class="pixel-text label">새 비밀번호 (선택사항)</label>
            <input 
              v-model="editForm.password" 
              type="password" 
              class="pixel-input glass-input"
              placeholder="변경하지 않으려면 비워두세요"
            />
          </div>

          <div class="form-group">
            <label class="pixel-text label">비밀번호 확인</label>
            <input 
              v-model="editForm.confirmPassword" 
              type="password" 
              class="pixel-input glass-input"
              placeholder="새 비밀번호 확인"
            />
          </div>

          <div class="edit-actions">
            <button class="pixel-button secondary" @click="cancelEditing">
              취소
            </button>
            <button class="pixel-button success" @click="saveProfile">
              저장하기
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Decor -->
    <div class="scan-lines"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useModalStore } from '../stores/modal'
import { userApi } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()
const modalStore = useModalStore()

const user = computed(() => authStore.user)
const isLoading = ref(false)
const isEditing = ref(false)

const editForm = ref({
  nickname: '',
  password: '',
  confirmPassword: ''
})

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  // Optional: Fetch latest user data if needed
})

function goHome() {
  router.push('/')
}



function startEditing() {
  if (user.value) {
    editForm.value.nickname = user.value.nickname
    editForm.value.password = ''
    editForm.value.confirmPassword = ''
    isEditing.value = true
  }
}

function cancelEditing() {
  isEditing.value = false
}

async function saveProfile() {
  if (!user.value) return

  if (editForm.value.password && editForm.value.password !== editForm.value.confirmPassword) {
    await modalStore.openAlert('비밀번호가 일치하지 않습니다!')
    return
  }

  isLoading.value = true
  try {
    const updateData: any = {
      nickname: editForm.value.nickname
    }
    if (editForm.value.password) {
      updateData.password = editForm.value.password
    }

    const response = await userApi.updateUser(user.value.userId, updateData)
    
    // Update store with new data
    authStore.user = { ...authStore.user, ...response.data }
    
    await modalStore.openSuccess('프로필이 성공적으로 수정되었습니다!')
    isEditing.value = false
  } catch (error: any) {
    console.error('Failed to update profile:', error)
    await modalStore.openAlert(error.response?.data?.message || '프로필 수정에 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

async function handleDeleteAccount() {
  if (!await modalStore.openConfirm('정말 회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return
  if (!user.value) return

  isLoading.value = true
  try {
    await userApi.deleteUser(user.value.userId)
    authStore.logout()
    router.push('/login')
    await modalStore.openAlert('회원 탈퇴가 완료되었습니다.')
  } catch (error: any) {
    console.error('Failed to delete account:', error)
    await modalStore.openAlert('회원 탈퇴에 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.mypage-screen {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
}

.mypage-container {
  width: 100%;
  max-width: 600px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  z-index: 10;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 20px;
}

.title {
  font-size: 28px;
  color: #ffd43b;
  margin: 0;
}

.profile-view {
  display: flex;
  flex-direction: column;
  gap: 25px;
  align-items: center;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  background: rgba(74, 158, 255, 0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 4px solid #4a9eff;
  box-shadow: 0 0 20px rgba(74, 158, 255, 0.4);
}

.avatar-icon {
  font-size: 50px;
}

.info-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  color: #888;
  font-size: 12px;
}

.info-value {
  font-size: 18px;
  color: #fff;
  padding: 10px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.stats-group {
  width: 100%;
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.stat-item {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  padding: 15px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  border: 1px solid rgba(255,255,255,0.1);
}

.stat-label {
  font-size: 12px;
  color: #888;
}

.stat-value {
  font-size: 20px;
  color: #4a9eff;
}

.actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 20px;
  color: #4a9eff;
  margin-bottom: 10px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pixel-input.glass-input {
  background: rgba(0, 0, 0, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.2);
  color: #fff;
}

.pixel-input:focus {
  border-color: #4a9eff;
}

.edit-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.edit-actions button {
  flex: 1;
}

.danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #c92a2a 100%);
  border-color: #c92a2a;
}

.danger:hover {
  background: linear-gradient(135deg, #ff8787 0%, #ff6b6b 100%);
}

.scan-lines {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0) 0px,
    rgba(0, 0, 0, 0) 1px,
    rgba(255, 255, 255, 0.02) 2px,
    rgba(255, 255, 255, 0.02) 3px
  );
  pointer-events: none;
  z-index: 1;
}

.small {
  font-size: 12px;
  padding: 10px;
  margin-top: 10px;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .mypage-container {
      padding: 30px;
  }
}

@media (max-width: 480px) {
  .mypage-screen {
      padding: 10px;
  }

  .mypage-container {
      padding: 20px;
      gap: 20px;
  }
  
  .title {
      font-size: 24px;
  }
  
  .header {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;
  }
  
  .back-button {
      align-self: flex-start;
  }

  .profile-avatar {
      width: 80px;
      height: 80px;
  }
  
  .avatar-icon {
      font-size: 40px;
  }
  
  .stats-group {
      flex-direction: column;
      gap: 10px;
  }
  
  .actions {
      margin-top: 10px;
  }
  
  .edit-actions {
      flex-direction: column;
  }
}
</style>
