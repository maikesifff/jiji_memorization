<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalTextbooks }}</div>
          <div class="stat-label">教材总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🎓</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalGrades }}</div>
          <div class="stat-label">年级总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📖</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUnits }}</div>
          <div class="stat-label">单元总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🔤</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalWords }}</div>
          <div class="stat-label">单词总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📝</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalMeanings }}</div>
          <div class="stat-label">释义总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📖</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalPhrases }}</div>
          <div class="stat-label">短语总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">💬</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalSentences }}</div>
          <div class="stat-label">例句总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🔗</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUnitWords }}</div>
          <div class="stat-label">单元单词关联</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUsers }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalRecords }}</div>
          <div class="stat-label">学习记录</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-container">
        <h3>年级分布</h3>
        <GradeDistributionChart :data="textbooks" />
      </div>
      
      <div class="chart-container">
        <h3>学习进度</h3>
        <LearningProgressChart :stats="stats" />
      </div>
      
      <div class="chart-container">
        <h3>趋势分析</h3>
        <TrendChart :data="trendData" />
      </div>
    </div>
    
    <!-- 最近活动 -->
    <div class="recent-activities">
      <h3>最近活动</h3>
      <div class="activity-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-icon">{{ activity.icon }}</div>
          <div class="activity-content">
            <div class="activity-text">{{ activity.text }}</div>
            <div class="activity-time">{{ formatTime(activity.time) }}</div>
          </div>
        </div>
        
        <div v-if="recentActivities.length === 0" class="no-activities">
          暂无活动记录
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '@/utils/axios'
import GradeDistributionChart from './charts/GradeDistributionChart.vue'
import LearningProgressChart from './charts/LearningProgressChart.vue'
import TrendChart from './charts/TrendChart.vue'

export default {
  name: 'Dashboard',
  components: {
    GradeDistributionChart,
    LearningProgressChart,
    TrendChart
  },
  setup() {
    const stats = ref({
      totalTextbooks: 0,
      totalGrades: 0,
      totalUnits: 0,
      totalWords: 0,
      totalMeanings: 0,
      totalPhrases: 0,
      totalSentences: 0,
      totalUnitWords: 0,
      totalUsers: 0,
      totalRecords: 0
    })
    
    const textbooks = ref([])
    const trendData = ref([])
    const recentActivities = ref([])
    const loading = ref(false)
    
    // 获取统计数据
    const fetchStats = async () => {
      try {
        loading.value = true
        
        // 获取教材数量
        const textbooksResponse = await api.get('/api/textbooks/all')
        textbooks.value = textbooksResponse.data
        stats.value.totalTextbooks = textbooksResponse.data.length
        
        // 获取年级数量（从教材中统计不同年级）
        const uniqueGrades = new Set(textbooksResponse.data.map(t => t.grade))
        stats.value.totalGrades = uniqueGrades.size
        
        // 获取单元数量
        const unitsResponse = await api.get('/api/units/all')
        stats.value.totalUnits = unitsResponse.data.length
        
        // 获取用户数量
        const usersResponse = await api.get('/api/users')
        if (usersResponse.data && usersResponse.data.totalItems !== undefined) {
          // 分页响应格式
          stats.value.totalUsers = usersResponse.data.totalItems
        } else if (usersResponse.data && Array.isArray(usersResponse.data)) {
          // 直接数组格式（兼容性）
          stats.value.totalUsers = usersResponse.data.length
        } else {
          stats.value.totalUsers = 0
        }
        
        // 获取单词数量
        const wordsResponse = await api.get('/api/words')
        if (wordsResponse.data && wordsResponse.data.content) {
          stats.value.totalWords = wordsResponse.data.totalItems || 0
        } else {
          stats.value.totalWords = wordsResponse.data.length || 0
        }
        
        // 获取释义数量
        const meaningsResponse = await api.get('/api/meanings')
        if (meaningsResponse.data && meaningsResponse.data.content) {
          stats.value.totalMeanings = meaningsResponse.data.totalItems || 0
        } else {
          stats.value.totalMeanings = meaningsResponse.data.length || 0
        }
        
        // 获取短语数量
        const phrasesResponse = await api.get('/api/phrases')
        if (phrasesResponse.data && phrasesResponse.data.content) {
          stats.value.totalPhrases = phrasesResponse.data.totalItems || 0
        } else {
          stats.value.totalPhrases = phrasesResponse.data.length || 0
        }
        
        // 获取例句数量
        const sentencesResponse = await api.get('/api/sentences')
        if (sentencesResponse.data && sentencesResponse.data.content) {
          stats.value.totalSentences = sentencesResponse.data.totalItems || 0
        } else {
          stats.value.totalSentences = sentencesResponse.data.length || 0
        }
        
        // 获取单元单词关联数量
        const unitWordsResponse = await api.get('/api/unit-words')
        if (unitWordsResponse.data && unitWordsResponse.data.content) {
          stats.value.totalUnitWords = unitWordsResponse.data.totalItems || 0
        } else {
          stats.value.totalUnitWords = unitWordsResponse.data.length || 0
        }
        
        // 获取学习记录数量
        const recordsResponse = await api.get('/api/error-records')
        stats.value.totalRecords = recordsResponse.data.length || 0
        
        // 生成趋势数据（模拟最近7天的数据变化）
        generateTrendData()
        
      } catch (error) {
        console.error('Failed to fetch stats:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 生成趋势数据
    const generateTrendData = () => {
      // 模拟最近7天的数据变化
      const today = new Date()
      trendData.value = []
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date(today)
        date.setDate(date.getDate() - i)
        
        // 基于当前统计数据生成模拟趋势
        const baseValue = Math.floor((stats.value.totalWords + stats.value.totalMeanings) / 20)
        const randomVariation = Math.floor(Math.random() * 10) - 5
        const value = Math.max(0, baseValue + randomVariation)
        
        trendData.value.push({
          date: date.toISOString().split('T')[0],
          value: value
        })
      }
    }
    
    // 获取最近活动
    const fetchRecentActivities = async () => {
      try {
        // 暂时不获取活动记录，等后端实现相关功能
        recentActivities.value = []
      } catch (error) {
        console.error('Failed to fetch recent activities:', error)
      }
    }
    
    // 格式化时间
    const formatTime = (time) => {
      const now = new Date()
      const diff = now - time
      const minutes = Math.floor(diff / (1000 * 60))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (minutes < 60) {
        return `${minutes}分钟前`
      } else if (hours < 24) {
        return `${hours}小时前`
      } else {
        return `${days}天前`
      }
    }
    
    onMounted(() => {
      fetchStats()
      fetchRecentActivities()
    })
    
    return {
      stats,
      textbooks,
      trendData,
      recentActivities,
      loading,
      formatTime
    }
  }
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 48px;
  width: 60px;
  text-align: center;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.chart-container {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chart-container h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.chart-placeholder {
  text-align: center;
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #dee2e6;
}

.chart-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.chart-placeholder p {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.chart-placeholder small {
  color: #666;
  font-size: 14px;
}

.recent-activities {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.recent-activities h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.activity-item:hover {
  background: #e9ecef;
}

.activity-icon {
  font-size: 24px;
  width: 40px;
  text-align: center;
}

.activity-content {
  flex: 1;
}

.activity-text {
  color: #333;
  font-size: 14px;
  margin-bottom: 5px;
}

.activity-time {
  color: #666;
  font-size: 12px;
}

.no-activities {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
  
  .charts-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    font-size: 36px;
    width: 50px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .chart-container {
    padding: 20px;
  }
}

@media (min-width: 1200px) {
  .charts-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1600px) {
  .charts-section {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1600px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
    max-width: 1400px;
    margin: 0 auto 30px auto;
  }
}
</style>
