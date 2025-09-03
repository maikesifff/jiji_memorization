<template>
  <div class="word-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <button @click="showAddModal = true" class="add-btn">
        + 添加单词
      </button>
      <div class="search-box">
        <input 
          v-model="searchTerm" 
          type="text" 
          placeholder="搜索单词..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch" class="search-btn">搜索</button>
        <button v-if="isSearching" @click="clearSearch" class="clear-btn">清除搜索</button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>单词</th>
            <th>美音音标</th>
            <th>英音音标</th>
            <th>英音发音</th>
            <th>美音发音</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="word in filteredWords" :key="word.id">
            <td>{{ word.id }}</td>
            <td>{{ word.wordText }}</td>
            <td>{{ word.americanPhonetic || '-' }}</td>
            <td>{{ word.britishPhonetic || '-' }}</td>
            <td>{{ word.pronUk ? '已上传' : '未上传' }}</td>
            <td>{{ word.pronUs ? '已上传' : '未上传' }}</td>
            <td>{{ formatDate(word.createdAt) }}</td>
            <td>
              <button @click="editWord(word)" class="edit-btn">编辑</button>
              <button @click="deleteWord(word.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredWords.length === 0" class="no-data">
        暂无数据
      </div>
      
      <!-- 分页控件 -->
      <div v-if="pagination.totalPages > 1" class="pagination">
        <button 
          @click="changePage(pagination.currentPage - 1)" 
          :disabled="!pagination.hasPrevious"
          class="page-btn"
        >
          上一页
        </button>
        
        <div class="page-input-group">
          <span>第</span>
          <input 
            v-model.number="pageInput" 
            type="number" 
            min="1" 
            :max="pagination.totalPages"
            class="page-input"
            @keyup.enter="goToPage"
          />
          <span>页，共 {{ pagination.totalPages }} 页</span>
        </div>
        
        <span class="page-info">
          (共 {{ pagination.totalItems }} 条记录)
        </span>
        
        <button 
          @click="changePage(pagination.currentPage + 1)" 
          :disabled="!pagination.hasNext"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
    
    <!-- 添加/编辑模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑单词' : '添加单词' }}</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="form-group">
            <label>单词 *</label>
            <input 
              v-model="formData.wordText" 
              type="text" 
              required
              placeholder="请输入单词"
            />
          </div>
          
          <div class="form-group">
            <label>美音音标</label>
            <input 
              v-model="formData.americanPhonetic" 
              type="text" 
              placeholder="请输入美音音标"
            />
          </div>
          
          <div class="form-group">
            <label>英音音标</label>
            <input 
              v-model="formData.britishPhonetic" 
              type="text" 
              placeholder="请输入英音音标"
            />
          </div>
          
          <div class="form-group">
            <label>英音发音</label>
            <input 
              v-model="formData.pronUk" 
              type="text" 
              placeholder="英音发音文件路径或编码"
            />
          </div>
          
          <div class="form-group">
            <label>美音发音</label>
            <input 
              v-model="formData.pronUs" 
              type="text" 
              placeholder="美音发音文件路径或编码"
            />
          </div>
          
          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">
              {{ showEditModal ? '更新' : '添加' }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 删除确认模态框 -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal delete-modal">
        <div class="modal-header">
          <h3>确认删除</h3>
        </div>
        
        <div class="modal-body">
          <p>确定要删除单词 "{{ wordToDelete?.wordText }}" 吗？</p>
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import api from '@/utils/axios'

export default {
  name: 'WordManager',
  setup() {
    const words = ref([])
    const searchTerm = ref('')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const showDeleteModal = ref(false)
    const wordToDelete = ref(null)
    const loading = ref(false)
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(20)
    const pagination = ref({
      currentPage: 0,
      totalPages: 0,
      totalItems: 0,
      hasNext: false,
      hasPrevious: false
    })
    
    const formData = reactive({
      id: null,
      wordText: '',
      americanPhonetic: '',
      britishPhonetic: '',
      pronUk: '',
      pronUs: ''
    })
    
    const filteredWords = computed(() => {
      if (!searchTerm.value) return words.value
      // 如果正在搜索，返回搜索结果；否则返回当前页数据
      return isSearching.value ? searchResults.value : words.value.filter(word => 
        word.wordText.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
        (word.americanPhonetic && word.americanPhonetic.toLowerCase().includes(searchTerm.value.toLowerCase())) ||
        (word.britishPhonetic && word.britishPhonetic.toLowerCase().includes(searchTerm.value.toLowerCase()))
      )
    })
    
    // 搜索状态
    const isSearching = ref(false)
    const searchResults = ref([])
    
    // 执行搜索
    const performSearch = async () => {
      if (!searchTerm.value.trim()) {
        isSearching.value = false
        searchResults.value = []
        currentPage.value = 0
        await fetchWords()
        return
      }
      
      try {
        loading.value = true
        isSearching.value = true
        currentPage.value = 0
        
        const response = await api.get(`/api/words/search?keyword=${encodeURIComponent(searchTerm.value)}&page=0&size=${pageSize.value}`)
        
        if (response.data && response.data.content) {
          searchResults.value = response.data.content
          pagination.value = {
            currentPage: response.data.currentPage,
            totalPages: response.data.totalPages,
            totalItems: response.data.totalItems,
            hasNext: response.data.hasNext,
            hasPrevious: response.data.hasPrevious
          }
        }
      } catch (error) {
        console.error('搜索失败:', error)
        searchResults.value = []
      } finally {
        loading.value = false
      }
    }
    
    // 清除搜索
    const clearSearch = async () => {
      searchTerm.value = ''
      isSearching.value = false
      searchResults.value = []
      currentPage.value = 0
      await fetchWords()
    }
    
    // 搜索分页
    const searchPage = async (page) => {
      if (!searchTerm.value.trim()) return
      
      try {
        loading.value = true
        currentPage.value = page
        
        const response = await api.get(`/api/words/search?keyword=${encodeURIComponent(searchTerm.value)}&page=${page}&size=${pageSize.value}`)
        
        if (response.data && response.data.content) {
          searchResults.value = response.data.content
          pagination.value = {
            currentPage: response.data.currentPage,
            totalPages: response.data.totalPages,
            totalItems: response.data.totalItems,
            hasNext: response.data.hasNext,
            hasPrevious: response.data.hasPrevious
          }
        }
      } catch (error) {
        console.error('搜索分页失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 获取单词列表
    const fetchWords = async () => {
      try {
        loading.value = true
        const response = await api.get(`/api/words?page=${currentPage.value}&size=${pageSize.value}`)
        // 处理分页数据
        if (response.data && response.data.content) {
          words.value = response.data.content
          pagination.value = {
            currentPage: response.data.currentPage,
            totalPages: response.data.totalPages,
            totalItems: response.data.totalItems,
            hasNext: response.data.hasNext,
            hasPrevious: response.data.hasPrevious
          }
        } else {
          words.value = response.data || []
          pagination.value = {
            currentPage: 0,
            totalPages: 1,
            totalItems: words.value.length,
            hasNext: false,
            hasPrevious: false
          }
        }
      } catch (error) {
        console.error('Failed to fetch words:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 添加单词
    const addWord = async () => {
      try {
        const response = await api.post('/api/words', formData)
        
        // 检查响应状态
        if (response.data && response.data.status === 'success') {
          // 添加成功后，重新获取当前页数据以确保数据完整性
          await fetchWords()
          closeModal()
        } else {
          // 显示错误信息
          alert(response.data?.message || '添加单词失败')
        }
      } catch (error) {
        console.error('Failed to add word:', error)
        // 显示错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else {
          alert('添加单词失败: ' + error.message)
        }
      }
    }
    
    // 编辑单词
    const editWord = (word) => {
      formData.id = word.id
      formData.wordText = word.wordText
      formData.americanPhonetic = word.americanPhonetic || ''
      formData.britishPhonetic = word.britishPhonetic || ''
      formData.pronUk = word.pronUk || ''
      formData.pronUs = word.pronUs || ''
      showEditModal.value = true
    }
    
    // 更新单词
    const updateWord = async () => {
      try {
        const response = await api.put(`/api/words/${formData.id}`, formData)
        
        // 检查响应状态
        if (response.data && response.data.status === 'success') {
          // 更新成功后，重新获取当前页数据以确保数据完整性
          await fetchWords()
          closeModal()
        } else {
          // 显示错误信息
          alert(response.data?.message || '更新单词失败')
        }
      } catch (error) {
        console.error('Failed to update word:', error)
        // 显示错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else {
          alert('更新单词失败: ' + error.message)
        }
      }
    }
    
    // 删除单词
    const deleteWord = (wordId) => {
      wordToDelete.value = words.value.find(w => w.id === wordId)
      showDeleteModal.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      try {
        await api.delete(`/api/words/${wordToDelete.value.id}`)
        const index = words.value.findIndex(w => w.id === wordToDelete.value.id)
        if (index !== -1) {
          words.value.splice(index, 1)
        }
        showDeleteModal.value = false
        wordToDelete.value = null
      } catch (error) {
        console.error('Failed to delete word:', error)
      }
    }
    
    // 提交表单
    const handleSubmit = () => {
      if (showEditModal.value) {
        updateWord()
      } else {
        addWord()
      }
    }
    
    // 关闭模态框
    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      formData.id = null
      formData.wordText = ''
      formData.americanPhonetic = ''
      formData.britishPhonetic = ''
      formData.pronUk = ''
      formData.pronUs = ''
    }
    
    // 搜索处理
    const handleSearch = () => {
      performSearch()
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    // 切换页面
    const changePage = async (page) => {
      if (page < 0 || page >= pagination.value.totalPages) return
      
      if (isSearching.value) {
        await searchPage(page)
      } else {
        currentPage.value = page
        await fetchWords()
      }
    }

    // 跳转到指定页码
    const pageInput = ref(1)
    const goToPage = () => {
      const newPage = parseInt(pageInput.value) - 1
      if (newPage >= 0 && newPage < pagination.value.totalPages) {
        if (isSearching.value) {
          searchPage(newPage)
        } else {
          currentPage.value = newPage
          fetchWords()
        }
      }
    }
    
    // 监听分页变化，同步输入框
    watch(() => pagination.value.currentPage, (newPage) => {
      pageInput.value = newPage + 1
    })
    
    onMounted(() => {
      fetchWords()
    })
    
    return {
      words,
      searchTerm,
      showAddModal,
      showEditModal,
      showDeleteModal,
      wordToDelete,
      loading,
      formData,
      filteredWords,
      pagination,
      editWord,
      deleteWord,
      confirmDelete,
      handleSubmit,
      closeModal,
      handleSearch,
      formatDate,
      changePage,
      pageInput,
      goToPage,
      isSearching,
      searchResults,
      performSearch,
      clearSearch,
      searchPage
    }
  }
}
</script>

<style scoped>
.word-manager {
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

.add-btn {
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

.add-btn:hover {
  transform: translateY(-2px);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box input {
  padding: 10px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  width: 250px;
  transition: border-color 0.3s ease;
}

.search-btn,
.clear-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn {
  background: #667eea;
  color: white;
}

.search-btn:hover {
  background: #5a67d8;
}

.clear-btn {
  background: #6c757d;
  color: white;
}

.clear-btn:hover {
  background: #5a6268;
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

.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  margin-right: 8px;
  transition: background 0.3s ease;
}

.edit-btn {
  background: #17a2b8;
  color: white;
}

.edit-btn:hover {
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
  max-width: 500px;
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

.modal-form {
  padding: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
}

.cancel-btn,
.submit-btn {
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

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.submit-btn:hover {
  transform: translateY(-2px);
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

/* 分页控件样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-top: 1px solid #e1e5e9;
}

.page-btn {
  padding: 8px 16px;
  border: 2px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.page-btn:disabled {
  border-color: #ccc;
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

.page-input-group {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #333;
}

.page-input {
  width: 50px;
  padding: 8px;
  border: 2px solid #e1e5e9;
  border-radius: 6px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  transition: border-color 0.3s ease;
}

.page-input:focus {
  outline: none;
  border-color: #667eea;
}
</style>
