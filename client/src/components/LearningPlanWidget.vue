<template>
  <div class="dashboard-widget glass-panel">
    <div class="widget-header-row">
      <h3 class="widget-title pixel-text">
        학습 추천 키워드
        <span v-if="!loading && learningPlan" class="refresh-btn" @click="fetchLearningPlan">🔄</span>
      </h3>
    </div>
    
    <template v-if="loading">
      <div class="skeleton-list">
        <div class="skeleton-item-card" v-for="i in 3" :key="i">
          <div class="skeleton skeleton-box" style="width: 24px; height: 24px; border-radius: 4px;"></div>
          <div class="skeleton-content" style="flex: 1;">
            <div class="skeleton skeleton-text" style="width: 80%; height: 16px; margin-bottom: 6px;"></div>
            <div class="skeleton skeleton-text" style="width: 60%; height: 12px;"></div>
          </div>
        </div>
      </div>
    </template>
    
    <template v-else-if="learningPlan">
      <div v-if="learningPlan.learningPlan && learningPlan.learningPlan.length > 0" class="learning-plan-list">
        <div 
          v-for="(plan, idx) in learningPlan.learningPlan.slice(0, 3)" 
          :key="idx"
          class="learning-plan-item clickable-item"
        >
          <div class="plan-icon-wrapper">
            <span class="plan-icon">📚</span>
          </div>
          <div class="plan-content">
            <div class="plan-axis">{{ plan.axis }} 분야</div>
            <div class="plan-keywords">
              <span v-for="keyword in plan.keywords" :key="keyword" class="keyword-tag">
                {{ keyword }}
              </span>
            </div>
          </div>
          <div class="arrow-icon">›</div>
        </div>
      </div>
      <div v-else-if="learningPlan.weakKeywords && learningPlan.weakKeywords.length > 0" class="weak-keywords-list">
        <div 
          v-for="(keyword, idx) in learningPlan.weakKeywords.slice(0, 3)" 
          :key="idx"
          class="weak-keyword-item clickable-item"
        >
          <div class="keyword-icon-wrapper">
            <span class="keyword-icon">⚠️</span>
          </div>
          <div class="keyword-content">
            <div class="keyword-name">{{ keyword.keyword }}</div>
            <div class="keyword-axis">{{ keyword.axis }} · 점수: {{ keyword.score?.toFixed(1) }}</div>
          </div>
          <div class="arrow-icon">›</div>
        </div>
      </div>
      <div v-else class="empty-widget-text">
        추천할 학습 계획이 없습니다.
      </div>
    </template>
    
    <div v-else class="empty-widget-text">
      학습 계획을 불러올 수 없습니다.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { aiRecommendationApi } from '../services/api'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const learningPlan = ref<any>(null)

async function fetchLearningPlan() {
  if (!authStore.user?.userId) return
  
  loading.value = true
  try {
    const response = await aiRecommendationApi.getLearningPlan(200)
    learningPlan.value = response.data
  } catch (e) {
    console.error('학습 계획 조회 실패:', e)
    learningPlan.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLearningPlan()
})
</script>

<style scoped>
.learning-plan-list,
.weak-keywords-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.learning-plan-item,
.weak-keyword-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: rgba(0,0,0,0.2);
  border-radius: 8px;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.learning-plan-item:hover,
.weak-keyword-item:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(74, 158, 255, 0.3);
  transform: translateX(2px);
}

.plan-icon-wrapper,
.keyword-icon-wrapper {
  width: 32px;
  height: 32px;
  background: rgba(81, 207, 102, 0.1);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.keyword-icon-wrapper {
  background: rgba(255, 212, 59, 0.1);
}

.plan-content,
.keyword-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.plan-keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.keyword-tag {
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(81, 207, 102, 0.2);
  color: #51cf66;
  border-radius: 4px;
  border: 1px solid rgba(81, 207, 102, 0.3);
  font-weight: 600;
}

.plan-axis {
  font-size: 12px;
  color: #4a9eff;
  font-weight: 600;
}

.keyword-name {
  font-size: 13px;
  color: #fff;
  font-weight: 600;
}

.keyword-axis {
  font-size: 11px;
  color: #888;
}

.arrow-icon {
  font-size: 18px;
  color: #555;
}

.clickable-item:hover .arrow-icon {
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

