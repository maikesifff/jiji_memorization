<template>
  <div class="error-record-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <h2>学习记录管理</h2>
      <div class="search-box">
        <input 
          v-model="searchTerm" 
          type="text" 
          placeholder="搜索用户名或单词..."
          @input="handleSearch"
        />
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户</th>
            <th>单词</th>
            <th>所属单元</th>
            <th>所属教材</th>
            <th>错误次数</th>
            <th>正确次数</th>
            <th>正确率</th>
            <th>最后答题状态</th>
            <th>最后错误时间</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in filteredRecords" :key="record.id">
            <td>{{ record.id }}</td>
            <td>{{ getUserName(record.userId) }}</td>
            <td>{{ getWordText(record.unitWordId) }}</td>
            <td>{{ getUnitName(record.unitWordId) }}</td>
            <td>{{ getTextbookName(record.unitWordId) }}</td>
            <td>
              <span class="error-count" :class="getErrorCountClass(record.errorCount)">
                {{ record.errorCount }}
              </span>
            </td>
            <td>
              <span class="correct-count" :class="getCorrectCountClass(record.correctCount)">
                {{ record.correctCount }}
              </span>
            </td>
            <td>
              <span class="accuracy-rate" :class="getAccuracyClass(record)">
                {{ getAccuracyRate(record) }}%
              </span>
            </td>
            <td>
              <span class="last-answer-status" :class="getLastAnswerStatusClass(record.lastAnswerCorrect)">
                {{ getLastAnswerStatusText(record.lastAnswerCorrect) }}
              </span>
            </td>
            <td>{{ formatDate(record.lastErrorAt) || '-' }}</td>
            <td>{{ formatDate(record.createdAt) }}</td>
            <td>
              <button @click="viewDetails(record)" class="view-btn">查看详情</button>
              <button @click="deleteRecord(record.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredRecords.length === 0" class="no-data">
        暂无数据
      </div>
    </div>
    
    <!-- 详情模态框 -->
    <div v-if="showDetailsModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>学习记录详情</h3>
          <button @click="closeDetailsModal" class="close-btn">×</button>
        </div>
        
        <div class="modal-body">
          <div class="detail-item">
            <label>用户：</label>
            <span>{{ selectedRecord?.userName }}</span>
          </div>
          <div class="detail-item">
            <label>单词：</label>
            <span>{{ selectedRecord?.wordText }}</span>
          </div>
          <div class="detail-item">
            <label>音标：</label>
            <span>{{ selectedRecord?.phonetic || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>所属单元：</label>
            <span>{{ selectedRecord?.unitName }}</span>
          </div>
          <div class="detail-item">
            <label>所属教材：</label>
            <span>{{ selectedRecord?.textbookName }}</span>
          </div>
          <div class="detail-item">
            <label>错误次数：</label>
            <span class="error-count" :class="getErrorCountClass(selectedRecord?.errorCount)">
              {{ selectedRecord?.errorCount }}
            </span>
          </div>
          <div class="detail-item">
            <label>正确次数：</label>
            <span class="correct-count" :class="getCorrectCountClass(selectedRecord?.correctCount)">
              {{ selectedRecord?.correctCount }}
            </span>
          </div>
          <div class="detail-item">
            <label>正确率：</label>
            <span class="accuracy-rate" :class="getAccuracyClass(selectedRecord)">
              {{ getAccuracyRate(selectedRecord) }}%
            </span>
          </div>
          <div class="detail-item">
            <label>最后答题状态：</label>
            <span class="last-answer-status" :class="getLastAnswerStatusClass(selectedRecord?.lastAnswerCorrect)">
              {{ getLastAnswerStatusText(selectedRecord?.lastAnswerCorrect) }}
            </span>
          </div>
          <div class="detail-item">
            <label>最后错误时间：</label>
            <span>{{ formatDate(selectedRecord?.lastErrorAt) || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>创建时间：</label>
            <span>{{ formatDate(selectedRecord?.createdAt) }}</span>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeDetailsModal" class="close-btn">关闭</button>
        </div>
      </div>
    </div>
    
    <!-- 删除确认模态框 -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal delete-modal">
        <div class="modal-header">
          <h3>确认删除</h3>
        </div>
        
        <div class="modal-body">
          <p>确定要删除这条学习记录吗？</p>
          <p class="warning">此操作不可恢复！</p>
        </div>
        
        <div class="form-actions">
          <button @click="showDeleteModal = false" class="cancel-btn">取消</button>
          <button @click="confirmDelete" class="delete-btn">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/utils/axios'

export default {
  name: 'ErrorRecordManager',
  setup() {
    const records = ref([])
    const users = ref([])
    const words = ref([])
    const units = ref([])
    const textbooks = ref([])
    const unitWords = ref([])
    const searchTerm = ref('')
    const showDetailsModal = ref(false)
    const showDeleteModal = ref(false)
    const selectedRecord = ref(null)
    const recordToDelete = ref(null)
    const loading = ref(false)
    
    const filteredRecords = computed(() => {
      if (!searchTerm.value) return records.value
      return records.value.filter(record => {
        const userName = getUserName(record.userId) || ''
        const wordText = getWordText(record.unitWordId) || ''
        return userName.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
               wordText.toLowerCase().includes(searchTerm.value.toLowerCase())
      })
    })
    
    // 获取学习记录列表
    const fetchRecords = async () => {
      try {
        loading.value = true
        const response = await api.get('/api/error-records')
        records.value = response.data
      } catch (error) {
        console.error('Failed to fetch error records:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 获取用户列表
    const fetchUsers = async () => {
      try {
        const response = await api.get('/api/users')
        users.value = response.data
      } catch (error) {
        console.error('Failed to fetch users:', error)
      }
    }
    
    // 获取单词列表
    const fetchWords = async () => {
      try {
        const response = await api.get('/api/words')
        // 处理分页数据，提取content数组
        if (response.data && response.data.content) {
          words.value = response.data.content
        } else {
          words.value = response.data || []
        }
      } catch (error) {
        console.error('Failed to fetch words:', error)
        words.value = []
      }
    }
    
    // 获取单元列表
    const fetchUnits = async () => {
      try {
        const response = await api.get('/api/units/all')
        units.value = response.data
      } catch (error) {
        console.error('Failed to fetch units:', error)
      }
    }
    
    // 获取教材列表
    const fetchTextbooks = async () => {
      try {
        const response = await api.get('/api/textbooks/all')
        textbooks.value = response.data
      } catch (error) {
        console.error('Failed to fetch textbooks:', error)
      }
    }
    
    // 获取单元单词关联列表
    const fetchUnitWords = async () => {
      try {
        const response = await api.get('/api/unit-words')
        unitWords.value = response.data
      } catch (error) {
        console.error('Failed to fetch unit words:', error)
      }
    }
    
    // 获取用户名
    const getUserName = (userId) => {
      const user = users.value.find(u => u.id === userId)
      return user ? user.username : '-'
    }
    
    // 获取单词文本
    const getWordText = (unitWordId) => {
      const unitWord = unitWords.value.find(uw => uw.id === unitWordId)
      if (!unitWord) return '-'
      const word = words.value.find(w => w.id === unitWord.wordId)
      return word ? word.wordText : '-'
    }
    
    // 获取单元名称
    const getUnitName = (unitWordId) => {
      const unitWord = unitWords.value.find(uw => uw.id === unitWordId)
      if (!unitWord) return '-'
      const unit = units.value.find(u => u.id === unitWord.unitId)
      return unit ? unit.name : '-'
    }
    
    // 获取教材名称
    const getTextbookName = (unitWordId) => {
      const unitWord = unitWords.value.find(uw => uw.id === unitWordId)
      if (!unitWord) return '-'
      const unit = units.value.find(u => u.id === unitWord.unitId)
      if (!unit) return '-'
      const textbook = textbooks.value.find(t => t.id === unit.textbookId)
      return textbook ? `${textbook.grade} - ${textbook.name}` : '-'
    }
    
    // 获取错误次数样式类
    const getErrorCountClass = (count) => {
      if (count === 0) return 'error-none'
      if (count <= 3) return 'error-low'
      if (count <= 7) return 'error-medium'
      return 'error-high'
    }

    // 获取正确次数样式类
    const getCorrectCountClass = (count) => {
      if (count === 0) return 'correct-none'
      if (count <= 3) return 'correct-low'
      if (count <= 7) return 'correct-medium'
      return 'correct-high'
    }

    // 计算正确率
    const getAccuracyRate = (record) => {
      const total = (record.correctCount || 0) + (record.errorCount || 0)
      if (total === 0) return 0
      return Math.round(((record.correctCount || 0) / total) * 100)
    }

    // 获取正确率样式类
    const getAccuracyClass = (record) => {
      const rate = getAccuracyRate(record)
      if (rate === 0) return 'accuracy-none'
      if (rate < 50) return 'accuracy-low'
      if (rate < 80) return 'accuracy-medium'
      return 'accuracy-high'
    }

    // 获取最后答题状态文本
    const getLastAnswerStatusText = (lastAnswerCorrect) => {
      if (lastAnswerCorrect === null || lastAnswerCorrect === undefined) return '未答题'
      return lastAnswerCorrect ? '答对' : '答错'
    }

    // 获取最后答题状态样式类
    const getLastAnswerStatusClass = (lastAnswerCorrect) => {
      if (lastAnswerCorrect === null || lastAnswerCorrect === undefined) return 'status-none'
      return lastAnswerCorrect ? 'status-correct' : 'status-wrong'
    }
    
    // 查看详情
    const viewDetails = (record) => {
      selectedRecord.value = {
        ...record,
        userName: getUserName(record.userId),
        wordText: getWordText(record.unitWordId),
        phonetic: getWordPhonetic(record.unitWordId),
        unitName: getUnitName(record.unitWordId),
        textbookName: getTextbookName(record.unitWordId)
      }
      showDetailsModal.value = true
    }
    
    // 获取单词音标
    const getWordPhonetic = (unitWordId) => {
      const unitWord = unitWords.value.find(uw => uw.id === unitWordId)
      if (!unitWord) return '-'
      const word = words.value.find(w => w.id === unitWord.wordId)
      return word ? word.phonetic : '-'
    }
    
    // 关闭详情模态框
    const closeDetailsModal = () => {
      showDetailsModal.value = false
      selectedRecord.value = null
    }
    
    // 删除记录
    const deleteRecord = (recordId) => {
      recordToDelete.value = records.value.find(r => r.id === recordId)
      showDeleteModal.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      try {
        await api.delete(`/api/error-records/${recordToDelete.value.id}`)
        const index = records.value.findIndex(r => r.id === recordToDelete.value.id)
        if (index !== -1) {
          records.value.splice(index, 1)
        }
        showDeleteModal.value = false
        recordToDelete.value = null
      } catch (error) {
        console.error('Failed to delete error record:', error)
      }
    }
    
    // 搜索处理
    const handleSearch = () => {
      // 实时搜索，无需额外处理
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    onMounted(() => {
      fetchRecords()
      fetchUsers()
      fetchWords()
      fetchUnits()
      fetchTextbooks()
      fetchUnitWords()
    })
    
    return {
      records,
      searchTerm,
      showDetailsModal,
      showDeleteModal,
      selectedRecord,
      recordToDelete,
      loading,
      filteredRecords,
      viewDetails,
      closeDetailsModal,
      deleteRecord,
      confirmDelete,
      handleSearch,
      getUserName,
      getWordText,
      getUnitName,
      getTextbookName,
      getErrorCountClass,
      getCorrectCountClass,
      getAccuracyRate,
      getAccuracyClass,
      getLastAnswerStatusText,
      getLastAnswerStatusClass,
      formatDate
    }
  }
}
</script>

<style scoped>
.error-record-manager {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.action-bar h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.search-box input {
  padding: 10px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  width: 250px;
  transition: border-color 0.3s ease;
}

.search-box input:focus {
  outline: none;
  border-color: #667eea;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #e1e5e9;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.error-count {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  display: inline-block;
  min-width: 30px;
}

.error-none {
  background: #d4edda;
  color: #155724;
}

.error-low {
  background: #fff3cd;
  color: #856404;
}

.error-medium {
  background: #f8d7da;
  color: #721c24;
}

.error-high {
  background: #721c24;
  color: white;
}

.correct-count {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  display: inline-block;
  min-width: 30px;
}

.correct-none {
  background: #f8d7da;
  color: #721c24;
}

.correct-low {
  background: #fff3cd;
  color: #856404;
}

.correct-medium {
  background: #d1ecf1;
  color: #0c5460;
}

.correct-high {
  background: #d4edda;
  color: #155724;
}

.accuracy-rate {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  display: inline-block;
  min-width: 40px;
}

.accuracy-none {
  background: #f8d7da;
  color: #721c24;
}

.accuracy-low {
  background: #f8d7da;
  color: #721c24;
}

.accuracy-medium {
  background: #fff3cd;
  color: #856404;
}

.accuracy-high {
  background: #d4edda;
  color: #155724;
}

.last-answer-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  display: inline-block;
  min-width: 50px;
}

.status-none {
  background: #e2e3e5;
  color: #6c757d;
}

.status-correct {
  background: #d4edda;
  color: #155724;
}

.status-wrong {
  background: #f8d7da;
  color: #721c24;
}

.view-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 8px;
  transition: background 0.3s ease;
}

.view-btn {
  background: #17a2b8;
  color: white;
}

.view-btn:hover {
  background: #138496;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.delete-btn:hover {
  background: #c82333;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 16px;
}

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
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e5e9;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  padding: 5px;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 30px;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.detail-item label {
  font-weight: 600;
  color: #333;
  min-width: 120px;
  margin-right: 15px;
}

.detail-item span {
  color: #666;
}

.modal-footer {
  padding: 20px 30px;
  border-top: 1px solid #e1e5e9;
  text-align: right;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
}

.cancel-btn,
.delete-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.delete-btn:hover {
  background: #c82333;
}

.delete-modal .modal-body {
  padding: 20px 30px;
}

.delete-modal .modal-body p {
  margin: 10px 0;
  color: #333;
}

.delete-modal .modal-body .warning {
  color: #dc3545;
  font-weight: 600;
}
</style>
