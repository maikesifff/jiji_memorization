<template>
  <div class="textbook-selector">
    <div class="selector-header">
      <h2>选择教材</h2>
      <button @click="$emit('close')" class="close-btn">×</button>
    </div>
    
    <div class="selector-content">
      <!-- 年级选择 -->
      <div class="grade-section">
        <h3>选择年级</h3>
        <div class="grade-list">
          <button
            v-for="grade in grades"
            :key="grade.id"
            @click="selectGrade(grade)"
            :class="['grade-btn', { active: selectedGrade?.id === grade.id }]"
          >
            {{ grade.gradeName }}
          </button>
        </div>
      </div>
      
      <!-- 教材选择 -->
      <div v-if="selectedGrade" class="textbook-section">
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
      <div v-if="selectedGrade && selectedTextbook" class="confirm-section">
        <div class="selected-info">
          <p><strong>已选择：</strong></p>
          <p>{{ selectedGrade.gradeName }} - {{ selectedTextbook.name }}</p>
        </div>
        <button @click="confirmSelection" class="confirm-btn">
          确认选择
        </button>
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
    const grades = ref([])
    const textbooks = ref([])
    const selectedGrade = ref(null)
    const selectedTextbook = ref(null)
    const loading = ref(false)
    
    // 获取年级列表
    const fetchGrades = async () => {
      try {
        loading.value = true
        const response = await api.get('/api/grades')
        grades.value = response.data
      } catch (error) {
        console.error('Failed to fetch grades:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 获取教材列表
    const fetchTextbooks = async (gradeId) => {
      try {
        loading.value = true
        const response = await api.get(`/api/textbooks/grade/${gradeId}`)
        textbooks.value = response.data
      } catch (error) {
        console.error('Failed to fetch textbooks:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 选择年级
    const selectGrade = (grade) => {
      selectedGrade.value = grade
      selectedTextbook.value = null
      fetchTextbooks(grade.id)
    }
    
    // 选择教材
    const selectTextbook = (textbook) => {
      selectedTextbook.value = textbook
    }
    
    // 确认选择
    const confirmSelection = () => {
      if (selectedGrade.value && selectedTextbook.value) {
        emit('textbook-selected', {
          grade: selectedGrade.value,
          textbook: selectedTextbook.value
        })
        emit('close')
      }
    }
    
    onMounted(() => {
      fetchGrades()
    })
    
    return {
      grades,
      textbooks,
      selectedGrade,
      selectedTextbook,
      loading,
      selectGrade,
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

.grade-list,
.textbook-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

.grade-btn,
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

.grade-btn:hover,
.textbook-btn:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.grade-btn.active,
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

.selected-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 8px;
}

.selected-info p {
  margin: 5px 0;
  color: #333;
}

.confirm-btn {
  padding: 14px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.confirm-btn:hover {
  transform: translateY(-2px);
}
</style>
