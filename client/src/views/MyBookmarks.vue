<template>
  <div class="page-container">
    <div class="content-wrapper glass-panel">
      <div class="page-header">
        <h2 class="page-title pixel-text">내 오답노트</h2>
        <button class="back-btn pixel-button" @click="router.back()">뒤로가기</button>
      </div>


      <!-- Toolbar: Search, Sort -->
      <div class="toolbar-section">
          <div class="search-container glass-input-wrapper">
              <input 
                  v-model="searchKeyword" 
                  @keyup.enter="handleSearch"
                  type="text" 
                  class="glass-input search-input" 
                  placeholder="질문 또는 메모 검색..."
              >
              <button class="search-btn" @click="handleSearch">🔍</button>
          </div>

          <div class="sort-dropdown-wrapper">
            <select v-model="sortOption" @change="handleSort" class="glass-input sort-select">
                <option value="latest">최신순</option>
                <option value="oldest">오래된순</option>
            </select>
          </div>
      </div>
      
      <!-- Categories -->
      <div class="category-tabs centered-tabs">
        <button 
          v-for="cat in categories" 
          :key="cat.value"
          class="tab-button" 
          :class="{ active: selectedCategory === cat.value }"
          @click="handleCategoryChange(cat.value)"
        >
          {{ cat.label }}
        </button>
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
              <div class="bookmark-header">
                  <span class="bookmark-badge">{{ bookmark.jobCategory || 'General' }}</span>
                  <span class="bookmark-stage">{{ bookmark.stageTitle || 'Unknown Stage' }}</span>
                  <span class="created-at">{{ formatDateTime(bookmark.createdAt) }}</span>
              </div>
              <div class="bookmark-q">{{ bookmark.questionText || '질문 내용 없음' }}</div>
              <div class="bookmark-meta-row" v-if="bookmark.memo">
                 <div class="bookmark-memo">
                     <span class="memo-label">MEMO</span> {{ bookmark.memo }}
                 </div>
              </div>
          </div>
          <div class="arrow-icon">›</div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>저장된 오답노트가 없습니다.</p>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-container">
          <button 
            class="pagination-nav-btn prev"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            &lt;
          </button>
          
          <div class="page-numbers">
            <button
              v-for="page in getPageNumbers()"
              :key="page"
              class="page-number-btn pixel-text"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            class="pagination-nav-btn next"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            &gt;
          </button>
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

// Filters & Pagination
const searchKeyword = ref('')
const sortOption = ref('latest')
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = 10

// Category Filter
const selectedCategory = ref('all')
const categories = ref([
    { label: '전체', value: 'all' }
])

async function fetchCategories() {
    try {
        const res = await battleApi.getBookmarkCategories()
        const myCats = res.data
        if (myCats && myCats.length > 0) {
            const dynamicCats = myCats.filter((c: string) => c).map((cat: string) => ({
                label: cat,
                value: cat
            }))
            categories.value = [
                { label: '전체', value: 'all' },
                ...dynamicCats
            ]
        } else {
             categories.value = [{ label: '전체', value: 'all' }]
        }
    } catch (e) {
        console.error('Failed to categories', e)
        categories.value = [{ label: '전체', value: 'all' }]
    }
}

async function fetchBookmarks() {
    loading.value = true;
    try {
        const res = await battleApi.getMyBookmarks({
            category: selectedCategory.value === 'all' ? undefined : selectedCategory.value,
            search: searchKeyword.value,
            sort: sortOption.value,
            page: currentPage.value,
            size: pageSize
        })
        
        if (res.data && res.data.content) {
            bookmarks.value = res.data.content;
            totalPages.value = res.data.totalPages;
            currentPage.value = res.data.currentPage;
        } else {
           bookmarks.value = Array.isArray(res.data) ? res.data : [];
        }
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

function handleSearch() {
    currentPage.value = 1;
    fetchBookmarks();
}

function handleSort() {
    currentPage.value = 1;
    fetchBookmarks();
}

function handleCategoryChange(category: string) {
    selectedCategory.value = category
    currentPage.value = 1
    fetchBookmarks()
}

function goToPage(page: number) {
    if (page < 1 || page > totalPages.value) return;
    currentPage.value = page;
    fetchBookmarks();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function getPageNumbers() {
  const total = totalPages.value
  const current = currentPage.value
  const pages: number[] = []
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    let start = Math.max(1, current - 2)
    let end = Math.min(total, current + 2)
    if (end - start < 4) {
      if (start === 1) end = Math.min(total, start + 4)
      else if (end === total) start = Math.max(1, end - 4)
    }
    for (let i = start; i <= end; i++) pages.push(i)
  }
  return pages
}

// Utils
function formatDateTime(dateStr: string) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}.${month}.${day} ${hour}:${minute}`
}

onMounted(async () => {
    await fetchCategories()
    fetchBookmarks()
})
</script>

<style scoped>
.page-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 80px;
}

.content-wrapper {
  padding: 20px;
  border-radius: 12px;
  min-height: 500px;
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

/* Toolbar */
.toolbar-section {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    align-items: center;
}

.search-container {
    flex: 1;
    display: flex;
    gap: 0;
    min-width: 250px;
    box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
    border-radius: 4px;
    overflow: hidden;
    border: 2px solid #555;
    transition: border-color 0.2s;
    background: rgba(0,0,0,0.4);
}

.search-container:focus-within {
    border-color: #ffd43b; /* Active border color */
    background: rgba(0,0,0,0.6);
}

.search-input {
    flex: 1;
    padding: 12px;
    border: none; /* Removed individual border */
    background: transparent; /* Transparent to show container bg */
    color: #fff;
    font-family: 'DungGeunMo', sans-serif;
    font-size: 14px;
    outline: none;
}



.search-btn {
    padding: 0 20px;
    border: none; /* Removed individual border */
    background: #4a9eff;
    color: #fff;
    cursor: pointer;
    font-family: 'DungGeunMo', sans-serif;
    transition: all 0.1s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.search-btn:hover {
    background: #5bb0ff;
    transform: none;
}

.search-btn:active {
    background: #3a8eef;
    box-shadow: inset 2px 2px 0 rgba(0,0,0,0.2);
}

.sort-select {
    padding: 10px 16px;
    border-radius: 4px;
    background: #222;
    border: 2px solid #555;
    color: #fff;
    cursor: pointer;
    font-family: 'DungGeunMo', sans-serif;
    box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
    height: 44px;
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

.bookmark-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 4px;
}

.bookmark-badge {
    font-size: 11px;
    padding: 2px 6px;
    background: rgba(255,255,255,0.1);
    border-radius: 4px;
    color: #aaa;
}

.bookmark-stage {
    font-size: 13px;
    color: #aaa;
    font-weight: bold;
}


.bookmark-q {
  font-size: 15px;
  font-weight: 600;
  color: #eee;
  /* line-clamp */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
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

/* Pagination */
.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    padding: 20px 0;
    margin-top: 10px;
}

.pagination-nav-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    font-weight: bold;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pagination-nav-btn:disabled {
    opacity: 0.3;
    cursor: not-allowed;
}

.page-numbers {
    display: flex;
    gap: 6px;
}

.page-number-btn {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    border: 1px solid transparent; /* invisible border to prevent layout shift */
    background: transparent;
    color: #aaa;
    cursor: pointer;
    font-size: 14px;
}

.page-number-btn.active {
    background: #4a9eff;
    color: #fff;
    font-weight: bold;
    border: 1px solid #7cbcf0;
    box-shadow: 0 0 10px rgba(74, 158, 255, 0.5);
}

.page-number-btn:hover:not(.active) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
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
/* Categories - Matching Board.vue / MyBattles.vue Style */
.category-tabs {
    padding: 10px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    border-radius: 12px;
    background: rgba(0,0,0,0.2);
    justify-content: center;
    margin-bottom: 24px;
}

.tab-button {
  font-size: 13px;
  padding: 8px 16px;
  min-width: 60px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-color: transparent;
  border: 1px solid rgba(255,255,255,0.2);
  color: #ccc;
  box-shadow: none;
  font-family: 'DungGeunMo', sans-serif;
  transition: all 0.2s;
}

.tab-button:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    color: #fff;
}

.tab-button.active {
  background: linear-gradient(135deg, #4a9eff 0%, #357abd 100%);
  border-color: rgba(255,255,255,0.5);
  box-shadow: 0 0 15px rgba(74, 158, 255, 0.5);
  transform: translateY(-2px);
  color: #fff;
  font-weight: bold;
}

.tab-button.active:hover {
    cursor: default;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .page-container {
      padding: 15px;
      padding-bottom: 80px;
  }
}

@media (max-width: 480px) {
  /* Toolbar Stacking */
  .toolbar-section {
      flex-direction: column;
      align-items: stretch;
      gap: 10px;
  }
  
  .search-container {
      flex: none;
      width: 100%;
  }

  .sort-dropdown-wrapper {
      width: 100%;
  }
  
  .sort-select {
      width: 100%;
      box-sizing: border-box;
  }

  /* Category Tabs Scrolling */
  .category-tabs {
      flex-wrap: nowrap;
      overflow-x: auto;
      justify-content: flex-start;
      padding-bottom: 10px; 
      margin-bottom: 15px;
      -webkit-overflow-scrolling: touch;
      white-space: nowrap;
  }
  
  .tab-button {
      flex-shrink: 0;
  }

  /* Bookmark List Items */
  .bookmark-item {
      align-items: flex-start;
      gap: 12px;
      position: relative;
  }

  .bookmark-icon-wrapper {
      width: 32px;
      height: 32px;
      font-size: 16px;
      flex-shrink: 0;
  }
  
  .bookmark-text {
      min-width: 0; /* Enable text truncation */
  }

  .bookmark-header {
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 6px;
  }
  
  .bookmark-badge {
      margin-bottom: 0;
  }

  .bookmark-q {
      font-size: 14px;
      -webkit-line-clamp: 3; /* Show slightly more text on mobile before truncation */
      margin-bottom: 6px;
  }
  
  /* Memo adjustments */
  .bookmark-memo {
      max-width: 100%;
      white-space: normal; /* Allow memo to wrap */
      height: auto;
      overflow: visible;
  }

  /* Arrow icon positioning */
  .arrow-icon {
      position: absolute;
      top: 15px;
      right: 15px;
      font-size: 18px;
  }
}
</style>
