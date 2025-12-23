<template>
  <div class="dashboard-widget glass-panel">
    <div class="widget-header-row">
      <h3 class="widget-title pixel-text">
        AI 공고 추천
        <span v-if="!loading && recommendations" class="refresh-btn" @click="fetchRecommendations">🔄</span>
      </h3>
    </div>
    
    <template v-if="loading">
      <div class="skeleton-list">
        <div class="skeleton-item-card" v-for="i in 3" :key="i">
          <div class="skeleton skeleton-circle" style="width: 40px; height: 40px; border-radius: 50%;"></div>
          <div class="skeleton-content" style="flex: 1;">
            <div class="skeleton skeleton-text" style="width: 70%; height: 18px; margin-bottom: 4px;"></div>
            <div class="skeleton skeleton-text" style="width: 50%; height: 12px;"></div>
          </div>
        </div>
      </div>
    </template>
    
    <template v-else-if="recommendations">
      <div v-if="recommendations.recommendedStages && recommendations.recommendedStages.length > 0" class="stage-list">
        <div 
          v-for="stage in recommendations.recommendedStages.slice(0, 3)" 
          :key="stage.stageId"
          class="stage-item clickable-item"
          @click="goToStage(stage)"
        >
          <div class="stage-icon-wrapper">
            <span class="stage-icon">🏢</span>
          </div>
          <div class="stage-info">
            <div class="stage-title">{{ stage.title || '공고 제목 없음' }}</div>
            <div class="stage-meta">
              <span class="stage-company">{{ stage.companyName || '회사명 없음' }}</span>
              <span class="stage-category">{{ stage.jobCategory || '' }}</span>
              <span class="stage-score">적합도: {{ (stage.score * 100).toFixed(0) }}%</span>
            </div>
          </div>
          <div class="arrow-icon">›</div>
        </div>
      </div>
      <div v-else class="empty-widget-text">
        추천할 공고가 없습니다.
      </div>
    </template>
    
    <div v-else class="empty-widget-text">
      공고 추천을 불러올 수 없습니다.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { aiRecommendationApi } from '../services/api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const recommendations = ref<any>(null)

async function fetchRecommendations() {
  if (!authStore.user?.userId) return
  
  loading.value = true
  try {
    const response = await aiRecommendationApi.getStageRecommendations(200, 300)
    recommendations.value = response.data
  } catch (e) {
    console.error('공고 추천 조회 실패:', e)
    recommendations.value = null
  } finally {
    loading.value = false
  }
}

function goToStage(stage: any) {
  router.push({
    path: '/stages',
    query: {
      keyword: stage.title || ''
    }
  })
}

onMounted(() => {
  fetchRecommendations()
})
</script>

<style scoped>
.stage-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stage-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.stage-item:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(74, 158, 255, 0.3);
  transform: translateX(2px);
}

.stage-icon-wrapper {
  width: 40px;
  height: 40px;
  background: rgba(74, 158, 255, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stage-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.stage-title {
  font-size: 14px;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stage-meta {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: #888;
  flex-wrap: wrap;
}

.stage-company {
  color: #4a9eff;
  font-weight: 600;
}

.stage-category {
  color: #888;
}

.stage-score {
  color: #51cf66;
  font-weight: 600;
}

.arrow-icon {
  font-size: 18px;
  color: #555;
}

.stage-item:hover .arrow-icon {
  color: #fff;
}

.widget-title {
  font-size: 14px;
  color: #4a9eff;
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.widget-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 5px;
}

.refresh-btn {
  font-size: 14px;
  cursor: pointer;
  transition: transform 0.2s;
}

.refresh-btn:hover {
  transform: rotate(90deg);
}

.empty-widget-text {
  color: #666;
  font-size: 12px;
  text-align: center;
  padding: 20px 0;
  background: rgba(0,0,0,0.1);
  border-radius: 8px;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 8px;
  background: rgba(255,255,255,0.05);
  border-radius: 8px;
}

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

@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}
</style>

