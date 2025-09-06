<template>
  <div class="vocabulary-container">

    <!-- 统计信息 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📚</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalWords || 0 }}</div>
            <div class="stat-label">总词汇</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🆕</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.newWords || 0 }}</div>
            <div class="stat-label">新词</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📖</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.learningWords || 0 }}</div>
            <div class="stat-label">学习中</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.masteredWords || 0 }}</div>
            <div class="stat-label">已掌握</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选和操作 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button 
          v-for="status in statusOptions" 
          :key="status.value"
          @click="selectedStatus = status.value; handleStatusChange()"
          :class="['filter-tab', { active: selectedStatus === status.value }]"
        >
          {{ status.label }}
        </button>
      </div>
      <div class="action-buttons">
        <button @click="startReview" class="review-btn" :disabled="!hasWordsForReview">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
          开始复习
        </button>
      </div>
    </div>

    <!-- 生词列表 -->
    <div class="vocabulary-list">
      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="filteredWords.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>暂无生词</h3>
        <p>去学习页面添加一些生词吧！</p>
        <router-link to="/" class="go-learn-btn">开始学习</router-link>
      </div>
      
      <div v-else class="words-grid">
        <div 
          v-for="word in filteredWords" 
          :key="word.id"
          class="word-card"
        >
          <div class="word-header">
            <div class="word-info">
              <h3 class="word-title">{{ word.word || '未知单词' }}</h3>
              <div class="phonetics-container">
                <div class="phonetic-item" v-if="word.americanPhonetic || word.phonetic">
                  <span class="phonetic-label">美</span>
                  <span class="phonetic-text">{{ word.americanPhonetic || word.phonetic || '暂无音标' }}</span>
                  <button @click="playAudio(word, 'american')" class="audio-btn" v-if="word.pronUs" title="播放美音">
                    🔊
                  </button>
                </div>
                <div class="phonetic-item" v-if="word.britishPhonetic || word.phonetic">
                  <span class="phonetic-label">英</span>
                  <span class="phonetic-text">{{ word.britishPhonetic || word.phonetic || '暂无音标' }}</span>
                  <button @click="playAudio(word, 'british')" class="audio-btn" v-if="word.pronUk" title="播放英音">
                    🔊
                  </button>
                </div>
                <!-- 调试信息 -->
                <div v-if="!word.americanPhonetic && !word.britishPhonetic && !word.phonetic" class="debug-info">
                  <small>调试: americanPhonetic={{ word.americanPhonetic }}, britishPhonetic={{ word.britishPhonetic }}, phonetic={{ word.phonetic }}</small>
                </div>
              </div>
            </div>
            <div class="word-actions">
              <button @click="removeFromNotebook(word.wordId)" class="remove-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
          </div>
          
          <div class="word-content">
            <div class="word-meanings">
              <p class="meaning">{{ word.meanings || '暂无词义信息' }}</p>
            </div>
            
            <div class="word-meta">
              <div class="status-badge" :class="getStatusClass(word.status)">
                {{ getStatusText(word.status) }}
              </div>
              <div class="review-count">
                复习 {{ word.reviewCount }} 次
              </div>
            </div>
          </div>
          
          <div class="word-notes" v-if="word.notes">
            <p class="notes-label">笔记：</p>
            <p class="notes-content">{{ word.notes }}</p>
          </div>
        </div>
      </div>
      
      <!-- 分页组件 -->
      <div v-if="totalPages > 1" class="pagination-section">
        <div class="pagination">
          <button 
            @click="previousPage" 
            :disabled="!hasPrevious"
            class="pagination-btn prev-btn"
          >
            上一页
          </button>
          
          <div class="pagination-pages">
            <button
              v-for="page in visiblePages"
              :key="page"
              @click="goToPage(page)"
              :class="['pagination-btn page-btn', { active: page === currentPage }]"
            >
              {{ page + 1 }}
            </button>
          </div>
          
          <button 
            @click="nextPage" 
            :disabled="!hasNext"
            class="pagination-btn next-btn"
          >
            下一页
          </button>
        </div>
        
        <div class="pagination-info">
          第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页，总计 {{ totalElements }} 个单词
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/utils/axios'

export default {
  name: 'Vocabulary',
  setup() {
    const authStore = useAuthStore()
    const loading = ref(false)
    const words = ref([])
    const selectedStatus = ref('ALL')
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(12)
    const totalPages = ref(0)
    const totalElements = ref(0)
    const hasNext = ref(false)
    const hasPrevious = ref(false)
    
    const stats = reactive({
      totalWords: 0,
      newWords: 0,
      learningWords: 0,
      masteredWords: 0
    })
    
    const statusOptions = [
      { value: 'ALL', label: '全部' },
      { value: 'NEW', label: '新词' },
      { value: 'LEARNING', label: '学习中' },
      { value: 'MASTERED', label: '已掌握' }
    ]
    
    // 当前页的单词（分页后直接显示）
    const filteredWords = computed(() => {
      return words.value
    })
    
    // 可见的页码
    const visiblePages = computed(() => {
      const pages = []
      const maxVisible = 5
      const start = Math.max(0, currentPage.value - Math.floor(maxVisible / 2))
      const end = Math.min(totalPages.value - 1, start + maxVisible - 1)
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    })
    
    // 是否有需要复习的单词
    const hasWordsForReview = computed(() => {
      return words.value.some(word => 
        word.status === 'LEARNING' || 
        (word.status === 'NEW' && word.reviewCount === 0)
      )
    })
    
    // 获取状态样式类
    const getStatusClass = (status) => {
      switch (status) {
        case 'NEW': return 'status-new'
        case 'LEARNING': return 'status-learning'
        case 'MASTERED': return 'status-mastered'
        default: return 'status-new'
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'NEW': return '新词'
        case 'LEARNING': return '学习中'
        case 'MASTERED': return '已掌握'
        default: return '新词'
      }
    }
    
    // 播放音频
    const playAudio = async (word, type) => {
      try {
        const audioData = type === 'american' ? word.pronUs : word.pronUk
        if (!audioData) {
          console.warn(`No audio data for ${type} pronunciation of word: ${word.word}`)
          return
        }
        
        // 创建音频元素
        const audio = new Audio()
        
        // 判断是URL还是base64数据
        if (audioData.startsWith('http://') || audioData.startsWith('https://')) {
          // URL格式
          audio.src = audioData
        } else if (audioData.startsWith('data:audio/')) {
          // 完整的data URL格式
          audio.src = audioData
        } else {
          // 假设是base64数据，添加data URL前缀
          audio.src = `data:audio/mpeg;base64,${audioData}`
        }
        
        // 播放音频
        audio.play().catch(error => {
          console.error('Audio play failed:', error)
          // 如果播放失败，尝试其他格式
          if (!audioData.startsWith('data:audio/')) {
            audio.src = `data:audio/wav;base64,${audioData}`
            audio.play().catch(err => {
              console.error('Audio play failed with wav format:', err)
            })
          }
        })
        
        console.log(`Playing ${type} audio for word: ${word.word}`)
      } catch (error) {
        console.error('Error creating audio:', error)
      }
    }
    
    // 从生词本中移除单词
    const removeFromNotebook = async (wordId) => {
      try {
        const response = await api.delete('/api/vocabulary-notebook/remove', {
          params: {
            userId: authStore.currentUser.id,
            wordId: wordId
          }
        })
        
        if (response.data.status === 'success') {
          // 重新加载数据
          await loadVocabularyData()
        }
      } catch (error) {
        console.error('移除单词失败:', error)
      }
    }
    
    // 开始复习
    const startReview = () => {
      // TODO: 实现复习功能
      alert('复习功能开发中...')
    }
    
    // 分页相关方法
    const goToPage = (page) => {
      currentPage.value = page
      loadVocabularyData()
    }
    
    const nextPage = () => {
      if (hasNext.value) {
        currentPage.value++
        loadVocabularyData()
      }
    }
    
    const previousPage = () => {
      if (hasPrevious.value) {
        currentPage.value--
        loadVocabularyData()
      }
    }
    
    // 状态切换时重置到第一页
    const handleStatusChange = () => {
      currentPage.value = 0
      loadVocabularyData()
    }
    
    // 加载生词本数据
    const loadVocabularyData = async () => {
      if (!authStore.currentUser) return
      
      loading.value = true
      try {
        // 构建API URL
        let apiUrl = `/api/vocabulary-notebook/user/${authStore.currentUser.id}/page`
        if (selectedStatus.value !== 'ALL') {
          apiUrl = `/api/vocabulary-notebook/user/${authStore.currentUser.id}/status/${selectedStatus.value}/page`
        }
        
        // 加载生词本（分页）
        const notebookResponse = await api.get(apiUrl, {
          params: {
            page: currentPage.value,
            size: pageSize.value
          }
        })
        
        if (notebookResponse.data.status === 'success') {
          words.value = notebookResponse.data.content || []
          totalPages.value = notebookResponse.data.totalPages || 0
          totalElements.value = notebookResponse.data.totalElements || 0
          hasNext.value = notebookResponse.data.hasNext || false
          hasPrevious.value = notebookResponse.data.hasPrevious || false
        }
        
        // 加载统计信息
        const statsResponse = await api.get(`/api/vocabulary-notebook/user/${authStore.currentUser.id}/stats`)
        if (statsResponse.data.status === 'success') {
          const statsData = statsResponse.data.stats
          stats.totalWords = statsData.totalWords
          stats.newWords = statsData.newWords
          stats.learningWords = statsData.learningWords
          stats.masteredWords = statsData.masteredWords
        }
      } catch (error) {
        console.error('加载生词本数据失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      // 平滑滚动到页面顶部
      window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
      loadVocabularyData()
    })
    
    return {
      loading,
      words,
      stats,
      selectedStatus,
      statusOptions,
      filteredWords,
      hasWordsForReview,
      getStatusClass,
      getStatusText,
      playAudio,
      removeFromNotebook,
      startReview,
      // 分页相关
      currentPage,
      pageSize,
      totalPages,
      totalElements,
      hasNext,
      hasPrevious,
      visiblePages,
      goToPage,
      nextPage,
      previousPage,
      handleStatusChange
    }
  }
}
</script>

<style scoped>
.vocabulary-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}


.stats-section {
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2.5rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.9rem;
  color: #718096;
  font-weight: 500;
}

.filter-section {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
}

.filter-tab {
  padding: 0.5rem 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.filter-tab:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

.review-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.review-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.review-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
  transform: none;
}

.vocabulary-list {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.empty-state p {
  margin: 0 0 1.5rem 0;
}

.go-learn-btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.go-learn-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.words-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.word-card {
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  padding: 0.75rem;
  transition: all 0.3s ease;
  background: white;
}

.word-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.word-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.word-info {
  flex: 1;
}

.word-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 0.125rem 0;
}

.phonetics-container {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  flex-wrap: wrap;
}

.phonetic-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  flex-shrink: 0;
}

.phonetic-label {
  font-size: 0.75rem;
  color: #999;
  font-weight: 600;
  min-width: 16px;
}

.phonetic-text {
  font-size: 0.9rem;
  color: #666;
  font-style: italic;
  white-space: nowrap;
}

.debug-info {
  font-size: 0.7rem;
  color: #999;
  margin-top: 0.25rem;
  padding: 0.25rem;
  background: #f5f5f5;
  border-radius: 4px;
}

.word-actions {
  display: flex;
  gap: 0.5rem;
}

.audio-btn,
.remove-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
}

.phonetic-item .audio-btn {
  width: 20px;
  height: 20px;
  font-size: 10px;
  flex-shrink: 0;
}

.audio-btn {
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
}

.audio-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

.remove-btn {
  background: #ffebee;
  color: #d32f2f;
}

.remove-btn:hover {
  background: #ffcdd2;
}

.word-content {
  margin-bottom: 0.5rem;
}

.word-meanings {
  margin-bottom: 0.5rem;
}

.meaning {
  font-size: 0.9rem;
  color: #555;
  margin: 0 0 0.25rem 0;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-height: 1.3em;
}

.word-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-new {
  background: #fff3e0;
  color: #f57c00;
}

.status-learning {
  background: #e3f2fd;
  color: #1976d2;
}

.status-mastered {
  background: #e8f5e8;
  color: #2e7d32;
}

.review-count {
  font-size: 0.8rem;
  color: #666;
}

.word-notes {
  border-top: 1px solid #f0f0f0;
  padding-top: 1rem;
}

.notes-label {
  font-size: 0.8rem;
  color: #666;
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.notes-content {
  font-size: 0.9rem;
  color: #555;
  margin: 0;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .vocabulary-container {
    padding: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .words-grid {
    grid-template-columns: 1fr;
  }
}

/* 分页组件样式 */
.pagination-section {
  margin-top: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #e1e5e9;
  background: white;
  color: #555;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8f9ff;
  color: #667eea;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-btn.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.pagination-pages {
  display: flex;
  gap: 0.25rem;
}

.page-btn {
  min-width: 40px;
  text-align: center;
}

.pagination-info {
  font-size: 0.9rem;
  color: #666;
  text-align: center;
}

@media (max-width: 768px) {
  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .pagination-pages {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 0.5rem;
  }
}
</style>
