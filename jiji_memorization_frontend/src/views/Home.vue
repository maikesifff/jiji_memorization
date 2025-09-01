<template>
  <div class="home">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h1>欢迎回来，{{ authStore.currentUser?.username }}！</h1>
      <p>开始你的吉吉记单词之旅吧</p>
    </div>
    
    <!-- 教材选择区域 -->
    <div class="textbook-section">
      <div class="section-header">
        <h2>当前教材</h2>
        <button @click="showTextbookSelector = true" class="select-btn">
          {{ currentTextbook ? '更换教材' : '选择教材' }}
        </button>
      </div>
      
      <div v-if="currentTextbook" class="current-textbook">
        <div class="textbook-info">
          <h3>{{ currentTextbook.grade.gradeName }} - {{ currentTextbook.textbook.name }}</h3>
          <p>已选择教材，可以开始学习</p>
        </div>
      </div>
      
      <div v-else class="no-textbook">
        <p>请先选择教材开始学习</p>
      </div>
    </div>
    
    <!-- 单元进度区域 -->
    <div v-if="currentTextbook" class="units-section">
      <h2>单元进度</h2>
      <div class="units-grid">
        <div 
          v-for="unit in units" 
          :key="unit.id" 
          class="unit-card"
          @click="selectUnit(unit)"
        >
          <div class="unit-header">
            <h3>{{ unit.name }}</h3>
            <div class="unit-status" :class="getUnitStatusClass(unit)">
              {{ getUnitStatusText(unit) }}
            </div>
          </div>
          
          <div class="unit-progress">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: getUnitProgress(unit) + '%' }"
              ></div>
            </div>
            <div class="progress-text">
              {{ getUnitProgressText(unit) }}
            </div>
          </div>
          
          <div class="unit-stats">
            <div class="stat-item">
              <span class="stat-label">总单词</span>
              <span class="stat-value">{{ unit.totalWords }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已学习</span>
              <span class="stat-value">{{ unit.learnedWords }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">进度</span>
              <span class="stat-value">{{ getUnitProgress(unit) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 快速统计 -->
    <div class="quick-stats">
      <h2>学习概览</h2>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-number">{{ totalUnits }}</div>
          <div class="stat-label">总单元数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ completedUnits }}</div>
          <div class="stat-label">已完成单元</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ totalWords }}</div>
          <div class="stat-label">总单词数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ overallProgress }}%</div>
          <div class="stat-label">总体进度</div>
        </div>
      </div>
    </div>
    
    <!-- 教材选择器 -->
    <TextbookSelector
      v-if="showTextbookSelector"
      @close="showTextbookSelector = false"
      @textbook-selected="handleTextbookSelected"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import TextbookSelector from '@/components/TextbookSelector.vue'
import api from '@/utils/axios'

export default {
  name: 'Home',
  components: {
    TextbookSelector
  },
  setup() {
    const authStore = useAuthStore()
    const showTextbookSelector = ref(false)
    const currentTextbook = ref(null)
    const units = ref([])
    const loading = ref(false)
    
    // 计算属性
    const totalUnits = computed(() => units.value.length)
    const completedUnits = computed(() => 
      units.value.filter(unit => getUnitProgress(unit) === 100).length
    )
    const totalWords = computed(() => 
      units.value.reduce((sum, unit) => sum + unit.totalWords, 0)
    )
    const overallProgress = computed(() => {
      if (totalWords.value === 0) return 0
      const learnedWords = units.value.reduce((sum, unit) => sum + unit.learnedWords, 0)
      return Math.round((learnedWords / totalWords.value) * 100)
    })
    
    // 获取单元列表
    const fetchUnits = async () => {
      if (!currentTextbook.value) return
      
      try {
        loading.value = true
        const response = await api.get(`/api/units/textbook/${currentTextbook.value.textbook.id}`)
        const unitsData = response.data
        
        // 获取每个单元的进度信息
        const unitsWithProgress = await Promise.all(
          unitsData.map(async (unit) => {
            const progressResponse = await api.get(`/api/units/${unit.id}/progress/${authStore.currentUser.id}`)
            return {
              ...unit,
              ...progressResponse.data
            }
          })
        )
        
        units.value = unitsWithProgress
      } catch (error) {
        console.error('Failed to fetch units:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 处理教材选择
    const handleTextbookSelected = (selection) => {
      currentTextbook.value = selection
      showTextbookSelector.value = false
      fetchUnits()
    }
    
    // 选择单元
    const selectUnit = (unit) => {
      console.log('Selected unit:', unit)
      // TODO: 跳转到单元学习页面
    }
    
    // 获取单元进度
    const getUnitProgress = (unit) => {
      if (unit.totalWords === 0) return 0
      return Math.round((unit.learnedWords / unit.totalWords) * 100)
    }
    
    // 获取单元进度文本
    const getUnitProgressText = (unit) => {
      return `${unit.learnedWords}/${unit.totalWords}`
    }
    
    // 获取单元状态
    const getUnitStatusClass = (unit) => {
      const progress = getUnitProgress(unit)
      if (progress === 0) return 'not-started'
      if (progress === 100) return 'completed'
      return 'in-progress'
    }
    
    // 获取单元状态文本
    const getUnitStatusText = (unit) => {
      const progress = getUnitProgress(unit)
      if (progress === 0) return '未开始'
      if (progress === 100) return '已完成'
      return '学习中'
    }
    
    onMounted(() => {
      // TODO: 从localStorage或用户设置中恢复上次选择的教材
    })
    
    return {
      authStore,
      showTextbookSelector,
      currentTextbook,
      units,
      loading,
      totalUnits,
      completedUnits,
      totalWords,
      overallProgress,
      handleTextbookSelected,
      selectUnit,
      getUnitProgress,
      getUnitProgressText,
      getUnitStatusClass,
      getUnitStatusText
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 50px;
}

.welcome-section h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.welcome-section p {
  font-size: 18px;
  color: #666;
}

.textbook-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.select-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.select-btn:hover {
  transform: translateY(-2px);
}

.current-textbook {
  background: #f8f9ff;
  border-radius: 8px;
  padding: 20px;
}

.textbook-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 20px;
}

.textbook-info p {
  margin: 0;
  color: #666;
}

.no-textbook {
  text-align: center;
  padding: 40px;
  color: #666;
}

.units-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.units-section h2 {
  margin: 0 0 30px 0;
  color: #333;
  font-size: 24px;
}

.units-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.unit-card {
  background: #f8f9ff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.unit-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
}

.unit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.unit-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.unit-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.unit-status.not-started {
  background: #e9ecef;
  color: #6c757d;
}

.unit-status.in-progress {
  background: #fff3cd;
  color: #856404;
}

.unit-status.completed {
  background: #d4edda;
  color: #155724;
}

.unit-progress {
  margin-bottom: 20px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.progress-text {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.unit-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.quick-stats {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.quick-stats h2 {
  margin: 0 0 30px 0;
  color: #333;
  font-size: 24px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stats-grid .stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9ff;
  border-radius: 8px;
}

.stats-grid .stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 10px;
}

.stats-grid .stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}
</style>

