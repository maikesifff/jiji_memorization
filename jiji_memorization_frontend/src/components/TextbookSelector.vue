<template>
  <div class="textbook-selector">
    <div class="selector-content">
      <div class="selector-header">
        <h2>选择教材</h2>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
      
      <!-- 年级选择 -->
      <div class="grade-section">
        <h3>选择年级</h3>
        <div class="grade-selector">
          <select v-model="selectedGradeId" @change="handleGradeChange" class="grade-select">
            <option value="">全部年级</option>
            <option v-for="grade in grades" :key="grade.id" :value="grade.id">
              {{ grade.gradeName }}
            </option>
          </select>
        </div>
      </div>
      
      <!-- 教材选择 -->
      <div class="textbook-section">
        <h3>选择教材</h3>
        <div class="textbook-list">
          <button
            v-for="textbook in textbooks"
            :key="textbook.id"
            @click="selectTextbook(textbook)"
            :class="['textbook-btn', { active: selectedTextbook?.id === textbook.id }]"
          >
            {{ textbook.name }}
          </button>
        </div>
      </div>
      
      <!-- 确认选择 -->
      <div class="confirm-section">
        <div v-if="selectedTextbook" class="selected-info">
          <p><strong>已选择：</strong></p>
          <p>{{ (selectedGrade ? selectedGrade.gradeName : '全部年级') }} - {{ selectedTextbook.name }}</p>
        </div>
        <div v-else class="no-selection-info">
          <p>请选择教材</p>
        </div>
        
        <div class="button-group">
          <button @click="$emit('close')" class="cancel-btn">
            取消
          </button>
          <button 
            @click="confirmSelection" 
            class="confirm-btn"
            :disabled="!selectedTextbook"
          >
            确认选择
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/utils/axios'

export default {
  name: 'TextbookSelector',
  emits: ['close', 'textbook-selected'],
  setup(props, { emit }) {
    // 年级列表（写死）
    const grades = ref([
      { id: '一年级', gradeName: '一年级' },
      { id: '二年级', gradeName: '二年级' },
      { id: '三年级', gradeName: '三年级' },
      { id: '四年级', gradeName: '四年级' },
      { id: '五年级', gradeName: '五年级' },
      { id: '六年级', gradeName: '六年级' },
      { id: '七年级', gradeName: '七年级' },
      { id: '八年级', gradeName: '八年级' },
      { id: '九年级', gradeName: '九年级' },
      { id: '高中', gradeName: '高中' }
    ])
    const textbooks = ref([])
    const selectedGrade = ref(null)
    const selectedGradeId = ref('')
    const selectedTextbook = ref(null)
    const loading = ref(false)
    
    // 获取教材列表
    const fetchTextbooks = async (gradeId) => {
      try {
        loading.value = true
        let response
        if (gradeId && gradeId !== '') {
          // 获取指定年级的教材
          response = await api.get(`/api/textbooks/grade/${encodeURIComponent(gradeId)}`)
        } else {
          // 获取所有教材
          response = await api.get('/api/textbooks/all')
        }
        textbooks.value = response.data
      } catch (error) {
        console.error('Failed to fetch textbooks:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 处理年级选择变化
    const handleGradeChange = () => {
      selectedTextbook.value = null
      if (selectedGradeId.value === '') {
        selectedGrade.value = null
      } else {
        const grade = grades.value.find(g => g.id === selectedGradeId.value)
        selectedGrade.value = grade
      }
      fetchTextbooks(selectedGradeId.value)
    }
    
    // 选择教材
    const selectTextbook = (textbook) => {
      selectedTextbook.value = textbook
    }
    
    // 确认选择
    const confirmSelection = () => {
      if (selectedTextbook.value) {
        // 如果没有选择年级（全部年级），创建一个默认年级对象
        const grade = selectedGrade.value || { id: null, gradeName: '全部年级' }
        emit('textbook-selected', {
          grade: grade,
          textbook: selectedTextbook.value
        })
        emit('close')
      }
    }
    
    onMounted(() => {
      // 默认选择全部年级，获取所有教材
      fetchTextbooks('')
    })
    
    return {
      grades,
      textbooks,
      selectedGrade,
      selectedGradeId,
      selectedTextbook,
      loading,
      handleGradeChange,
      selectTextbook,
      confirmSelection
    }
  }
}
</script>

<style scoped>
.textbook-selector {
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
}

.selector-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e1e5e9;
}

.selector-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.selector-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.grade-section,
.textbook-section {
  margin-bottom: 30px;
}

.grade-section h3,
.textbook-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
}

.grade-selector {
  margin-bottom: 20px;
}

.grade-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.grade-select:focus {
  outline: none;
  border-color: #667eea;
}

.textbook-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

.textbook-btn {
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.textbook-btn:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.textbook-btn.active {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.confirm-section {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e5e9;
}

.selected-info,
.no-selection-info {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 8px;
}

.selected-info {
  background: #f8f9ff;
}

.no-selection-info {
  background: #f8f9fa;
  color: #6c757d;
}

.selected-info p,
.no-selection-info p {
  margin: 5px 0;
  color: #333;
}

.no-selection-info p {
  color: #6c757d;
}

.button-group {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.cancel-btn,
.confirm-btn {
  padding: 14px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.confirm-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.confirm-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
}
</style>
