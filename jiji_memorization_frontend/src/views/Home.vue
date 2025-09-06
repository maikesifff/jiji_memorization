<template>
  <div class="home">
    <div class="home-content">
         <!-- 欢迎和教材选择区域 -->
     <div class="welcome-textbook-section">
       <div class="textbook-section">
         <!-- 欢迎文字区域 -->
         <div class="welcome-section">
           <h1>欢迎回来，{{ authStore.currentUser?.nickname || authStore.currentUser?.username }}！</h1>
           <p>开始你的吉吉记单词之旅吧</p>
         </div>
         
                   <div class="textbook-content">
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
         >
                       <!-- 左侧：单元信息 -->
            <div class="unit-info">
              <!-- 上方区域：状态和标题 -->
              <div class="unit-header">
                <div v-if="getUnitStatusText(unit)" class="unit-status" :class="getUnitStatusClass(unit)">
                  {{ getUnitStatusText(unit) }}
                </div>
                <h3>{{ unit.name }}</h3>
              </div>
              
              <!-- 下方区域：进度条和数据 -->
              <div class="unit-content">
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
                  <button 
                    v-if="unit.learnedWords || unit.errorCount"
                    @click="clearUnitProgress(unit)" 
                    class="clear-icon-btn"
                    title="清空该单元的学习记录"
                  >
                    🗑️
                  </button>
                </div>
                
                                 <div class="unit-stats">
                   <div class="stat-item">
                     <span class="stat-label">错误次数</span>
                     <span class="stat-value">{{ unit.errorCount || 0 }}</span>
                   </div>
                   <div class="stat-item">
                     <span class="stat-label">正确率</span>
                     <span class="stat-value">{{ getUnitAccuracy(unit) }}%</span>
                   </div>
                 </div>
              </div>
            </div>
           
           <!-- 右侧：学习模式按钮 -->
           <div class="learning-modes">
             <div class="mode-section">
               <h4>浏览模式</h4>
               <p>浏览和查看单元中的单词</p>
               <button @click="startBrowseMode(unit)" class="mode-btn browse-btn">
                 开始浏览
               </button>
             </div>
             <div class="mode-section">
               <h4>评测模式</h4>
               <p>测试单词掌握程度</p>
               <button @click="startTestMode(unit)" class="mode-btn test-btn">
                 开始评测
               </button>
             </div>
           </div>
         </div>
      </div>
    </div>
    
    <!-- 快速统计 -->
    <div class="quick-stats">
      <h2>教材概览</h2>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-number">{{ totalWords }}</div>
          <div class="stat-label">教材单词数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ learnedWords }}</div>
          <div class="stat-label">在学单词数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ totalErrors }}</div>
          <div class="stat-label">总错误次数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ errorRate }}%</div>
          <div class="stat-label">平均错误率</div>
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
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import TextbookSelector from '@/components/TextbookSelector.vue'
import api from '@/utils/axios'

export default {
  name: 'Home',
  components: {
    TextbookSelector
  },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const showTextbookSelector = ref(false)
    const currentTextbook = ref(null)
    const units = ref([])
    const loading = ref(false)
    
    // 计算属性
    const totalUnits = computed(() => units.value.length)
    const learnedWords = computed(() => 
      units.value.reduce((sum, unit) => sum + (unit.learnedWords || 0), 0)
    )
    const totalWords = computed(() => 
      units.value.reduce((sum, unit) => sum + (unit.totalWords || 0), 0)
    )
    const totalErrors = computed(() => 
      units.value.reduce((sum, unit) => sum + (unit.errorCount || 0), 0)
    )
    const errorRate = computed(() => {
      if (learnedWords.value === 0) return 0
      return Math.round((totalErrors.value / learnedWords.value) * 100)
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
            try {
              // 获取单元的总单词数（使用计数接口）
              const countResponse = await api.get(`/api/unit-words/unit/${unit.id}/count`)
              const totalWords = countResponse.data
              console.log(`Unit ${unit.id} (${unit.name}): totalWords = ${totalWords}`)
              
                             // 获取用户在该单元的学习记录
               const userProgress = await getUserUnitProgress(unit.id, totalWords)
               
               return {
                 ...unit,
                 totalWords: totalWords,
                 learnedWords: userProgress.learnedWords,
                 errorCount: userProgress.errorCount
               }
            } catch (error) {
              console.error(`Failed to get progress for unit ${unit.id}:`, error)
              return {
                ...unit,
                totalWords: 0,
                learnedWords: 0
              }
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

         // 获取用户在指定单元的学习进度
     const getUserUnitProgress = async (unitId, totalWords) => {
       try {
        // 从错误记录表查询该用户在该单元的学习数据
        const response = await api.get(`/api/error-records/user/${authStore.currentUser.id}/unit/${unitId}`)
         const records = response.data
         
         // 统计已学习的单词数（答对次数大于等于1视为已完成）
         const learnedWords = records.filter(record => record.correctCount >= 1).length
         
         // 统计总错误次数
         const errorCount = records.reduce((sum, record) => sum + (record.errorCount || 0), 0)
         
         return {
           learnedWords,
           totalWords,
           errorCount
         }
       } catch (error) {
         console.error('Failed to get user progress:', error)
         return {
           learnedWords: 0,
           totalWords,
           errorCount: 0
         }
       }
     }
    
    // 处理教材选择
    const handleTextbookSelected = (selection) => {
      currentTextbook.value = selection
      showTextbookSelector.value = false
      
      // 保存到 localStorage
      localStorage.setItem('selectedTextbook', JSON.stringify(selection))
      
      fetchUnits()
    }
    
    // 开始浏览模式
    const startBrowseMode = (unit) => {
      console.log('Starting browse mode for unit:', unit)
      // 跳转到浏览模式页面
      router.push(`/browse/${unit.id}`)
    }

    // 开始评测模式
    const startTestMode = (unit) => {
      console.log('Starting test mode for unit:', unit)
      // 跳转到评测模式页面
      router.push(`/evaluation/${unit.id}`)
    }
    
    // 清空单元学习记录
    const clearUnitProgress = async (unit) => {
      if (!confirm(`确定要清空单元"${unit.name}"的学习记录吗？此操作不可撤销。`)) {
        return
      }
      
      try {
        await api.delete(`/api/error-records/user/${authStore.currentUser.id}/unit/${unit.id}`)
        
        // 更新本地数据
        unit.learnedWords = 0
        unit.errorCount = 0
        
        // 重新获取单元数据以更新统计
        await fetchUnits()
        
      } catch (error) {
        console.error('Failed to clear unit progress:', error)
        alert('清空学习记录失败，请重试')
      }
    }
    
    // 获取单元进度
    const getUnitProgress = (unit) => {
      if (!unit.totalWords || unit.totalWords === 0) return 0
      if (!unit.learnedWords) return 0
      return Math.round((unit.learnedWords / unit.totalWords) * 100)
    }
    
         // 获取单元进度文本
     const getUnitProgressText = (unit) => {
       const total = unit.totalWords || 0
       const learned = unit.learnedWords || 0
       return `${learned}/${total}`
     }
     
     // 计算单元正确率
     const getUnitAccuracy = (unit) => {
       if (!unit.totalWords || unit.totalWords === 0) return 0
       if (!unit.learnedWords || unit.learnedWords === 0) return 0
       
       // 计算总答题次数（正确次数 + 错误次数）
       const totalAttempts = unit.learnedWords + (unit.errorCount || 0)
       if (totalAttempts === 0) return 0
       
       // 正确率 = 正确次数 / 总答题次数
       const accuracy = Math.round((unit.learnedWords / totalAttempts) * 100)
       return accuracy
     }
    
    // 获取单元状态
    const getUnitStatusClass = (unit) => {
      // 如果没有学习记录，不显示状态
      if (unit.learnedWords === 0 && unit.totalWords === 0) return ''
      
      const progress = getUnitProgress(unit)
      if (progress === 0) return 'not-started'
      if (progress === 100) return 'completed'
      return 'in-progress'
    }
    
    // 获取单元状态文本
    const getUnitStatusText = (unit) => {
      // 如果没有学习记录，不显示状态
      if (unit.learnedWords === 0 && unit.totalWords === 0) return ''
      
      const progress = getUnitProgress(unit)
      if (progress === 0) return '未开始'
      if (progress === 100) return '已完成'
      return '学习中'
    }
    
    onMounted(() => {
      // 平滑滚动到页面顶部
      window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
      
      // 从 localStorage 读取上次选择的教材
      const savedTextbook = localStorage.getItem('selectedTextbook')
      if (savedTextbook) {
        try {
          currentTextbook.value = JSON.parse(savedTextbook)
          fetchUnits()
        } catch (error) {
          console.error('Failed to parse saved textbook:', error)
          localStorage.removeItem('selectedTextbook')
        }
      }
    })
    
    return {
      authStore,
      showTextbookSelector,
      currentTextbook,
      units,
      loading,
      learnedWords,
      totalWords,
      totalErrors,
      errorRate,
      handleTextbookSelected,
      startBrowseMode,
      startTestMode,
      clearUnitProgress,
             getUnitProgress,
       getUnitProgressText,
       getUnitAccuracy,
       getUnitStatusClass,
       getUnitStatusText
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.home-content {
  max-width: 1400px; /* 增加页面最大宽度 */
  margin: 0 auto;
}

.welcome-textbook-section {
  margin-bottom: 50px;
}

.welcome-section {
  flex: 0 0 300px;
  text-align: center;
  padding-right: 20px;
  border-right: 1px solid #e1e5e9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 120px;
}

.welcome-section h1 {
  font-size: 28px; /* 稍微减小标题字体 */
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
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 40px;
  align-items: stretch;
}

.textbook-content {
  flex: 1;
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
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); /* 增加最小宽度 */
  gap: 20px;
  align-items: stretch; /* 让所有卡片拉伸到相同高度 */
}

.unit-card {
  background: #f8f9ff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  display: flex;
  flex-direction: row; /* 改为左右布局 */
  height: 100%; /* 让卡片占满整个高度 */
  gap: 20px; /* 左右两边的间距 */
}

.unit-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
}

.unit-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 垂直居中 */
  position: relative; /* 为绝对定位的垃圾桶按钮提供定位基准 */
}

.unit-header h3 {
  margin: 20px 0 0 0; /* 添加顶部偏移，让标题往下一点 */
  color: #333;
  font-size: 18px;
  text-align: center;
  line-height: 1.3; /* 控制行高 */
  word-wrap: break-word; /* 长单词换行 */
  flex: 1; /* 占据剩余空间 */
  display: flex;
  align-items: center; /* 在自己的区域内垂直居中 */
  justify-content: center; /* 在自己的区域内水平居中 */
}

.unit-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0; /* 防止状态标签被压缩 */
  white-space: nowrap; /* 状态文字不换行 */
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
  margin-bottom: 0;
  flex-shrink: 0; /* 防止进度条被压缩 */
  position: relative; /* 为绝对定位的垃圾桶按钮提供定位基准 */
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
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  flex-shrink: 0; /* 防止统计信息被压缩 */
  margin-top: 0; /* 移除上边距 */
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

.unit-info {
  flex: 1; /* 左侧占据剩余空间 */
  display: flex;
  flex-direction: column;
}

.unit-header {
  flex: 1; /* 占据上半部分 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* 垂直居中 */
}

.unit-content {
  flex: 1; /* 占据下半部分 */
  display: flex;
  flex-direction: column;
  justify-content: center; /* 内容上下居中 */
  gap: 15px; /* 进度条和数据之间的间距 */
}

.learning-modes {
  flex-shrink: 0; /* 右侧固定宽度，不压缩 */
  display: flex;
  flex-direction: column;
  min-width: 200px; /* 设置最小宽度 */
}

.mode-section {
  flex: 1; /* 平分右侧高度 */
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e1e5e9;
  text-align: center; /* 居中对齐 */
  display: flex;
  flex-direction: column;
  justify-content: center; /* 内容垂直居中 */
}

.mode-section:first-child {
  margin-bottom: 10px; /* 第一个模式区域底部添加间距 */
}

.mode-section h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 16px;
}

.mode-section p {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
}

.mode-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.browse-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.browse-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.test-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.test-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
}

/* 垃圾桶图标按钮样式 */
.clear-icon-btn {
  background: #ff4757;
  color: white;
  border: none;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 9px;
  padding: 0;
  position: absolute;
  top: -18px; /* 再往上挪一个相同的距离 */
  right: 0;
  z-index: 1;
  opacity: 0; /* 默认隐藏 */
  visibility: hidden;
}

.unit-card:hover .clear-icon-btn {
  opacity: 1; /* 悬停时显示 */
  visibility: visible;
}

.clear-icon-btn:hover {
  background: #ff3742;
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3);
}
</style>



