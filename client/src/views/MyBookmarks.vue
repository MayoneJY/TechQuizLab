<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      <div class="page-header">
        <h2 class="page-title pixel-text">내 오답노트</h2>
        <button class="back-btn pixel-button" @click="router.back()">뒤로가기</button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="skeleton-list">
           <div class="skeleton-item-card" v-for="i in 5" :key="i">
              <div class="skeleton skeleton-box" style="width: 24px; height: 24px; border-radius: 4px;"></div>
              <div class="skeleton-content" style="flex: 1;">
                   <div class="skeleton skeleton-text" style="width: 90%; height: 16px; margin-bottom: 5px;"></div>
                   <div class="skeleton skeleton-text" style="width: 50%; height: 12px;"></div>
              </div>
           </div>
         </div>
      </div>

      <div v-else-if="bookmarks.length > 0" class="bookmark-list">
        <div 
          v-for="bookmark in bookmarks" 
          :key="bookmark.bookmarkId" 
          class="bookmark-item clickable-item"
          @click="router.push(`/bookmarks/${bookmark.bookmarkId}`)"
        >
          <div class="bookmark-icon-wrapper">
              <span class="bookmark-icon">📑</span>
          </div>
          <div class="bookmark-text">
              <div class="bookmark-q">{{ bookmark.questionText || '질문 내용 없음' }}</div>
              <div class="bookmark-meta-row">
                 <div class="bookmark-memo" v-if="bookmark.memo">
                     <span class="memo-label">MEMO</span> {{ bookmark.memo }}
                 </div>
                 <span class="created-at">{{ new Date(bookmark.createdAt).toLocaleDateString() }}</span>
              </div>
          </div>
          <div class="arrow-icon">›</div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>저장된 북마크가 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { battleApi } from '../services/api'

const router = useRouter()
const loading = ref(true)
const bookmarks = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await battleApi.getMyBookmarks()
    bookmarks.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 400px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  padding-bottom: 10px;
}

.page-title {
  color: #ffd43b;
  font-size: 24px;
  margin: 0;
}

.back-btn {
  font-size: 14px;
  padding: 8px 16px;
}

.bookmark-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.clickable-item {
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 8px;
  padding: 15px;
  background: rgba(0,0,0,0.2);
  border: 1px solid transparent;
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.clickable-item:hover {
  background: rgba(255,255,255,0.1);
  border-color: rgba(74, 158, 255, 0.3);
  transform: translateX(2px);
}

.bookmark-icon-wrapper {
  width: 40px;
  height: 40px;
  background: rgba(255, 212, 59, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.bookmark-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow: hidden;
}

.bookmark-q {
  font-size: 15px;
  font-weight: 600;
  color: #eee;
  /* line-clamp */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.bookmark-meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.bookmark-memo {
  font-size: 12px;
  color: #ccc;
  background: rgba(0,0,0,0.3);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  max-width: 80%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.memo-label {
  color: #ffd43b;
  font-weight: 700;
  margin-right: 4px;
}

.created-at {
    font-size: 11px;
    color: #666;
}

.arrow-icon {
  color: #555;
  font-size: 20px;
  align-self: center;
}

.clickable-item:hover .arrow-icon {
  color: #fff;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
  color: #888;
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
  padding: 8px;
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
  top: 0; right: 0; bottom: 0; left: 0;
  transform: translateX(-100%);
  background-image: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  100% { transform: translateX(100%); }
}
</style>
