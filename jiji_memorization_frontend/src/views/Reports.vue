<template>
  <div class="reports-container">

    <!-- 统计信息 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <div class="stat-number">{{ scoreReports.length }}</div>
            <div class="stat-label">测试次数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ totalCorrectAnswers }}</div>
            <div class="stat-label">总正确数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">❌</div>
          <div class="stat-content">
            <div class="stat-number">{{ totalErrorCount }}</div>
            <div class="stat-label">总错误数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-content">
            <div class="stat-number">{{ averageAccuracy }}%</div>
            <div class="stat-label">平均准确率</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 成绩报告列表 -->
    <div class="reports-section">
      <div class="section-header">
        <h2>测试记录</h2>
        <div class="filter-options">
          <select v-model="selectedTimeRange" @change="filterReports" class="time-filter">
            <option value="all">全部时间</option>
            <option value="week">最近一周</option>
            <option value="month">最近一月</option>
          </select>
        </div>
      </div>

      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="filteredReports.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>暂无测试记录</h3>
        <p>去评测模式完成一些测试吧！</p>
        <router-link to="/" class="go-test-btn">开始测试</router-link>
      </div>

      <div v-else class="reports-list">
        <div 
          v-for="report in filteredReports" 
          :key="report.id"
          class="report-card"
          @click="showReportDetail(report)"
        >
          <div class="report-header">
            <div class="report-info">
              <h3 class="report-name">{{ report.reportName }}</h3>
              <p class="report-desc" v-if="report.reportDesc">{{ report.reportDesc }}</p>
            </div>
            <div class="report-date">
              {{ formatDate(report.createdAt) }}
            </div>
          </div>
          
          <div class="report-stats">
            <div class="stat-item">
              <span class="stat-label">测试单词</span>
              <span class="stat-value">{{ getReportWordCount(report.id) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">错误次数</span>
              <span class="stat-value error">{{ getReportErrorCount(report.id) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">准确率</span>
              <span class="stat-value" :class="getAccuracyClass(getReportAccuracy(report.id))">
                {{ getReportAccuracy(report.id) }}%
              </span>
            </div>
          </div>
          
          <div class="report-actions">
            <button @click.stop="viewReportDetail(report)" class="view-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              查看详情
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 报告详情模态框 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedReport?.reportName }}</h3>
          <button @click="closeDetailModal" class="close-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div v-if="reportDetails.length === 0" class="no-details">
            <p>暂无详细数据</p>
          </div>
          <div v-else class="details-list">
            <div 
              v-for="item in reportDetails" 
              :key="item.id"
              class="detail-item"
            >
              <div class="word-info">
                <h4>{{ item.word?.word || '未知单词' }}</h4>
                <p class="word-phonetic">{{ item.word?.phonetic || '' }}</p>
              </div>
              <div class="error-info">
                <span class="error-count" :class="{ 'has-error': item.errorCount > 0 }">
                  {{ item.errorCount }} 次错误
                </span>
              </div>
            </div>
          </div>
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
  name: 'Reports',
  setup() {
    const authStore = useAuthStore()
    const loading = ref(false)
    const scoreReports = ref([])
    const scoreItems = ref([])
    const selectedTimeRange = ref('all')
    const showDetailModal = ref(false)
    const selectedReport = ref(null)
    const reportDetails = ref([])
    
    // 计算总正确数
    const totalCorrectAnswers = computed(() => {
      return scoreItems.value.reduce((total, item) => {
        // 假设每个单词最多答错3次，超过3次算完全错误
        const maxErrors = 3
        const correctCount = Math.max(0, maxErrors - item.errorCount)
        return total + correctCount
      }, 0)
    })
    
    // 计算总错误数
    const totalErrorCount = computed(() => {
      return scoreItems.value.reduce((total, item) => total + item.errorCount, 0)
    })
    
    // 计算平均准确率
    const averageAccuracy = computed(() => {
      if (scoreItems.value.length === 0) return 0
      const totalWords = scoreItems.value.length
      const totalCorrect = totalCorrectAnswers.value
      return Math.round((totalCorrect / (totalWords * 3)) * 100) // 假设每个单词最多3次机会
    })
    
    // 筛选报告
    const filteredReports = computed(() => {
      if (selectedTimeRange.value === 'all') {
        return scoreReports.value
      }
      
      const now = new Date()
      let filterDate
      
      if (selectedTimeRange.value === 'week') {
        filterDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
      } else if (selectedTimeRange.value === 'month') {
        filterDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
      }
      
      return scoreReports.value.filter(report => {
        const reportDate = new Date(report.createdAt)
        return reportDate >= filterDate
      })
    })
    
    // 获取报告单词数量
    const getReportWordCount = (reportId) => {
      return scoreItems.value.filter(item => item.reportId === reportId).length
    }
    
    // 获取报告错误次数
    const getReportErrorCount = (reportId) => {
      return scoreItems.value
        .filter(item => item.reportId === reportId)
        .reduce((total, item) => total + item.errorCount, 0)
    }
    
    // 获取报告准确率
    const getReportAccuracy = (reportId) => {
      const reportItems = scoreItems.value.filter(item => item.reportId === reportId)
      if (reportItems.length === 0) return 0
      
      const totalErrors = reportItems.reduce((total, item) => total + item.errorCount, 0)
      const maxErrors = reportItems.length * 3 // 假设每个单词最多3次机会
      const correctCount = Math.max(0, maxErrors - totalErrors)
      
      return Math.round((correctCount / maxErrors) * 100)
    }
    
    // 获取准确率样式类
    const getAccuracyClass = (accuracy) => {
      if (accuracy >= 80) return 'excellent'
      if (accuracy >= 60) return 'good'
      if (accuracy >= 40) return 'fair'
      return 'poor'
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    
    // 显示报告详情
    const showReportDetail = (report) => {
      selectedReport.value = report
      reportDetails.value = scoreItems.value.filter(item => item.reportId === report.id)
      showDetailModal.value = true
    }
    
    // 查看报告详情
    const viewReportDetail = (report) => {
      showReportDetail(report)
    }
    
    // 关闭详情模态框
    const closeDetailModal = () => {
      showDetailModal.value = false
      selectedReport.value = null
      reportDetails.value = []
    }
    
    // 筛选报告
    const filterReports = () => {
      // 筛选逻辑在computed中处理
    }
    
    // 加载成绩报告数据
    const loadScoreReports = async () => {
      if (!authStore.currentUser) return
      
      loading.value = true
      try {
        // 加载成绩报告
        const reportsResponse = await api.get(`/api/score-reports/user/${authStore.currentUser.id}`)
        scoreReports.value = reportsResponse.data || []
        
        // 加载成绩项
        const itemsResponse = await api.get('/api/score-items')
        scoreItems.value = itemsResponse.data || []
        
      } catch (error) {
        console.error('加载成绩报告失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      // 平滑滚动到页面顶部
      window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
      loadScoreReports()
    })
    
    return {
      loading,
      scoreReports,
      scoreItems,
      selectedTimeRange,
      showDetailModal,
      selectedReport,
      reportDetails,
      totalCorrectAnswers,
      totalErrorCount,
      averageAccuracy,
      filteredReports,
      getReportWordCount,
      getReportErrorCount,
      getReportAccuracy,
      getAccuracyClass,
      formatDate,
      showReportDetail,
      viewReportDetail,
      closeDetailModal,
      filterReports
    }
  }
}
</script>

<style scoped>
.reports-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}


/* 统计信息区域 */
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

/* 报告列表区域 */
.reports-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  color: white;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.filter-options {
  display: flex;
  gap: 1rem;
}

.time-filter {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  color: #2d3748;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.time-filter:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: white;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: white;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.empty-state h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.empty-state p {
  font-size: 1rem;
  opacity: 0.8;
  margin-bottom: 2rem;
}

.go-test-btn {
  display: inline-block;
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.go-test-btn:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

/* 报告列表 */
.reports-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.report-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
}

.report-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.report-info {
  flex: 1;
}

.report-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.25rem 0;
}

.report-desc {
  font-size: 0.9rem;
  color: #718096;
  margin: 0;
}

.report-date {
  font-size: 0.8rem;
  color: #a0aec0;
  white-space: nowrap;
  margin-left: 1rem;
}

.report-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.stat-item {
  text-align: center;
}

.stat-item .stat-label {
  display: block;
  font-size: 0.8rem;
  color: #718096;
  margin-bottom: 0.25rem;
}

.stat-item .stat-value {
  display: block;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
}

.stat-item .stat-value.error {
  color: #e53e3e;
}

.stat-item .stat-value.excellent {
  color: #38a169;
}

.stat-item .stat-value.good {
  color: #3182ce;
}

.stat-item .stat-value.fair {
  color: #d69e2e;
}

.stat-item .stat-value.poor {
  color: #e53e3e;
}

.report-actions {
  display: flex;
  justify-content: flex-end;
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: #a0aec0;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #f7fafc;
  color: #2d3748;
}

.modal-body {
  padding: 1.5rem;
  max-height: 60vh;
  overflow-y: auto;
}

.no-details {
  text-align: center;
  color: #a0aec0;
  padding: 2rem;
}

.details-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.word-info h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.25rem 0;
}

.word-phonetic {
  font-size: 0.9rem;
  color: #718096;
  margin: 0;
}

.error-info {
  text-align: right;
}

.error-count {
  font-size: 0.9rem;
  font-weight: 500;
  color: #38a169;
  padding: 0.25rem 0.75rem;
  background: #f0fff4;
  border-radius: 12px;
  border: 1px solid #c6f6d5;
}

.error-count.has-error {
  color: #e53e3e;
  background: #fed7d7;
  border-color: #feb2b2;
}

@media (max-width: 768px) {
  .reports-container {
    padding: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .report-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .report-date {
    margin-left: 0;
  }
  
  .report-stats {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .modal-content {
    margin: 1rem;
    max-height: 90vh;
  }
  
  .detail-item {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
  
  .error-info {
    text-align: left;
  }
}
</style>
