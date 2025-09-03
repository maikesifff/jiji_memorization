<template>
  <div class="sentence-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <button @click="showAddModal = true" class="add-btn">
        + 添加例句
      </button>
      <div class="search-box">
        <input 
          v-model="searchTerm" 
          type="text" 
          placeholder="搜索单词或例句..."
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
            <th>例句内容</th>
            <th>翻译</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sentence in filteredSentences" :key="sentence.id">
            <td>{{ sentence.id }}</td>
            <td>{{ sentence.wordText || '-' }}</td>
            <td>{{ sentence.sentenceText || '-' }}</td>
            <td>{{ sentence.translation || '-' }}</td>
            <td>{{ formatDate(sentence.createdAt) }}</td>
            <td>
              <button @click="editSentence(sentence)" class="edit-btn">编辑</button>
              <button @click="deleteSentence(sentence.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredSentences.length === 0" class="no-data">
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
          <h3>{{ showEditModal ? '编辑例句' : '添加例句' }}</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="form-group">
            <label>单词 *</label>
            
            <!-- 添加模式：显示单词搜索 -->
            <div v-if="!showEditModal" class="word-search-container">
              <input
                v-model="wordSearchTerm"
                @input="searchWords"
                @keyup.enter="searchWords"
                placeholder="输入单词名称搜索..."
                class="word-search-input"
                required
              />
              <button 
                type="button" 
                @click="searchWords" 
                class="search-btn"
                :disabled="wordSearching"
              >
                {{ wordSearching ? '搜索中...' : '搜索' }}
              </button>
            </div>
            
            <!-- 编辑模式：显示固定的单词信息 -->
            <div v-if="showEditModal" class="fixed-word-display">
              <div class="fixed-word-info">
                <span class="word-text">{{ editingWordInfo.wordText }}</span>
              </div>
            </div>
            
            <!-- 单词搜索结果（仅在添加模式显示） -->
            <div v-if="!showEditModal && wordSearchResults.length > 0" class="word-search-results">
              <div 
                v-for="word in wordSearchResults" 
                :key="word.id"
                @click="selectWord(word)"
                class="word-result-item"
                :class="{ 'selected': formData.wordId === word.id }"
              >
                <span class="word-text">{{ word.wordText }}</span>
              </div>
            </div>
            
            <!-- 已选择的单词显示（仅在添加模式显示） -->
            <div v-if="!showEditModal && formData.wordId && getSelectedWordText()" class="selected-word-display">
              <span class="selected-word-label">已选择：</span>
              <span class="selected-word-text">{{ getSelectedWordText() }}</span>
              <button 
                type="button" 
                @click="clearSelectedWord" 
                class="clear-selection-btn"
              >
                清除
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>例句内容 *</label>
            <textarea 
              v-model="formData.sentenceText" 
              required
              placeholder="请输入例句内容"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label>翻译 *</label>
            <textarea 
              v-model="formData.translation" 
              required
              placeholder="请输入例句翻译"
              rows="3"
            ></textarea>
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
          <p>确定要删除这条例句吗？</p>
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
  name: 'SentenceManager',
  setup() {
    const sentences = ref([])
    const words = ref([])
    const searchTerm = ref('')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const showDeleteModal = ref(false)
    const sentenceToDelete = ref(null)
    const loading = ref(false)
    
    // 选中的单词信息
    const selectedWordInfo = ref(null)
    
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
      wordId: '',
      sentenceText: '',
      translation: ''
    })
    
    const filteredSentences = computed(() => {
      if (!searchTerm.value) return sentences.value
      // 如果正在搜索，返回搜索结果；否则返回当前页数据
      if (isSearching.value) {
        return searchResults.value
      }
      // 如果不在搜索状态，但在当前页数据中找到了匹配项，则返回过滤结果
      const filtered = sentences.value.filter(sentence => {
        const wordText = sentence.wordText || ''
        const content = sentence.sentenceText || ''
        const translation = sentence.translation || ''
        return wordText.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
               content.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
               translation.toLowerCase().includes(searchTerm.value.toLowerCase())
      })
      // 如果当前页没有找到匹配项，自动触发全局搜索
      if (filtered.length === 0 && searchTerm.value.trim()) {
        performSearch()
      }
      return filtered
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
        await fetchSentences()
        return
      }
      
      try {
        loading.value = true
        isSearching.value = true
        currentPage.value = 0
        
        const response = await api.get(`/api/sentences/search?keyword=${encodeURIComponent(searchTerm.value)}&page=0&size=${pageSize.value}`)
        
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
      await fetchSentences()
      // 重置分页输入框
      pageInput.value = 1
    }
    
    // 搜索分页
    const searchPage = async (page) => {
      if (!searchTerm.value.trim()) return
      
      try {
        loading.value = true
        currentPage.value = page
        
        const response = await api.get(`/api/sentences/search?keyword=${encodeURIComponent(searchTerm.value)}&page=${page}&size=${pageSize.value}`)
        
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
    
    // 获取例句列表
    const fetchSentences = async () => {
      try {
        loading.value = true
        const response = await api.get(`/api/sentences?page=${currentPage.value}&size=${pageSize.value}`)
        // 处理分页数据，提取content数组
        if (response.data && response.data.content) {
          sentences.value = response.data.content
          // 设置分页信息
          pagination.value = {
            currentPage: response.data.currentPage,
            totalPages: response.data.totalPages,
            totalItems: response.data.totalItems,
            hasNext: response.data.hasNext,
            hasPrevious: response.data.hasPrevious
          }
        } else {
          sentences.value = response.data || []
          pagination.value = {
            currentPage: 0,
            totalPages: 1,
            totalItems: sentences.value.length,
            hasNext: false,
            hasPrevious: false
          }
        }
      } catch (error) {
        console.error('Failed to fetch sentences:', error)
      } finally {
        loading.value = false
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
    
    // 添加例句
    const addSentence = async () => {
      try {
        const response = await api.post('/api/sentences', formData)
        
        // 检查响应状态
        if (response.data && response.data.status === 'success') {
          // 添加成功后，重新获取当前页数据以确保数据完整性
          await fetchSentences()
          closeModal()
        } else {
          // 显示错误信息
          alert(response.data?.message || '添加例句失败')
        }
      } catch (error) {
        console.error('Failed to add sentence:', error)
        // 显示错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else {
          alert('添加例句失败: ' + error.message)
        }
      }
    }
    
    // 编辑例句
    const editSentence = async (sentence) => {
      formData.id = sentence.id
      formData.wordId = sentence.wordId
      formData.sentenceText = sentence.sentenceText || ''
      formData.translation = sentence.translation || ''
      
      // 直接从当前数据中获取单词信息，避免额外的API调用
      if (sentence.wordText) {
                  editingWordInfo.value = {
            wordText: sentence.wordText
          }
      } else {
        // 如果当前数据中没有单词信息，尝试从API获取
        try {
          const response = await api.get(`/api/words/${sentence.wordId}`)
          if (response.data && response.data.wordText) {
            editingWordInfo.value = {
              wordText: response.data.wordText
            }
          } else {
            editingWordInfo.value = {
              wordText: '未知单词',
              phonetic: ''
            }
          }
        } catch (error) {
          console.error('Failed to fetch word info:', error)
          editingWordInfo.value = {
            wordText: '未知单词'
          }
        }
      }
      
      showEditModal.value = true
    }
    
    // 更新例句
    const updateSentence = async () => {
      try {
        const response = await api.put(`/api/sentences/${formData.id}`, formData)
        
        // 检查响应状态
        if (response.data && response.data.status === 'success') {
          // 更新成功后，重新获取当前页数据以确保数据完整性
          await fetchSentences()
          closeModal()
        } else {
          // 显示错误信息
          alert(response.data?.message || '更新例句失败')
        }
      } catch (error) {
        console.error('Failed to update sentence:', error)
        // 显示错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else {
          alert('更新例句失败: ' + error.message)
        }
      }
    }
    
    // 删除例句
    const deleteSentence = (sentenceId) => {
      sentenceToDelete.value = sentences.value.find(s => s.id === sentenceId)
      showDeleteModal.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      try {
        await api.delete(`/api/sentences/${sentenceToDelete.value.id}`)
        const index = sentences.value.findIndex(s => s.id === sentenceToDelete.value.id)
        if (index !== -1) {
          sentences.value.splice(index, 1)
        }
        showDeleteModal.value = false
        sentenceToDelete.value = null
      } catch (error) {
        console.error('Failed to delete sentence:', error)
      }
    }
    
    // 提交表单
    const handleSubmit = () => {
      if (showEditModal.value) {
        updateSentence()
      } else {
        addSentence()
      }
    }
    
    // 关闭模态框
    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      formData.id = null
      formData.wordId = ''
      formData.sentenceText = ''
      formData.translation = ''
      // 清除单词搜索相关状态
      wordSearchTerm.value = ''
      wordSearchResults.value = []
      selectedWordInfo.value = null
      // 清除编辑时的单词信息
      editingWordInfo.value = {
        wordText: ''
      }
    }
    
    // 单词搜索相关
    const wordSearchTerm = ref('')
    const wordSearchResults = ref([])
    const wordSearching = ref(false)
    
    // 编辑时的单词信息
    const editingWordInfo = ref({
      wordText: '',
      phonetic: ''
    })
    
    // 搜索单词
    const searchWords = async () => {
      if (!wordSearchTerm.value.trim()) {
        wordSearchResults.value = []
        return
      }
      
      try {
        wordSearching.value = true
        const response = await api.get(`/api/words/search?keyword=${encodeURIComponent(wordSearchTerm.value)}&page=0&size=10`)
        
        if (response.data && response.data.content) {
          wordSearchResults.value = response.data.content
        } else {
          wordSearchResults.value = []
        }
      } catch (error) {
        console.error('Failed to search words:', error)
        wordSearchResults.value = []
      } finally {
        wordSearching.value = false
      }
    }
    
    // 选择单词
    const selectWord = (word) => {
      formData.wordId = word.id
      // 保存选中的单词信息，不清空搜索结果
      selectedWordInfo.value = {
        id: word.id,
        wordText: word.wordText
      }
      wordSearchTerm.value = word.wordText
    }
    
    // 清除已选择的单词
    const clearSelectedWord = () => {
      formData.wordId = ''
      wordSearchTerm.value = ''
      wordSearchResults.value = []
      selectedWordInfo.value = null
    }
    
    // 获取已选择单词的显示文本
    const getSelectedWordText = () => {
      if (!selectedWordInfo.value) return ''
      return selectedWordInfo.value.wordText
    }
    
    // 搜索处理
    const handleSearch = () => {
      performSearch()
    }
    
    // 获取单词文本
    const getWordText = (wordId) => {
      const word = words.value.find(w => w.id === wordId)
      return word ? word.wordText : '-'
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    // 换页
    const changePage = async (page) => {
      if (page < 0 || page >= pagination.value.totalPages) return
      
      if (isSearching.value) {
        await searchPage(page)
      } else {
        currentPage.value = page
        await fetchSentences()
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
          fetchSentences()
        }
      }
    }
    
    // 监听分页变化，同步输入框
    watch(() => pagination.value.currentPage, (newPage) => {
      pageInput.value = newPage + 1
    })
    
    onMounted(() => {
      fetchSentences()
      fetchWords()
    })
    
    return {
      sentences,
      words,
      searchTerm,
      showAddModal,
      showEditModal,
      showDeleteModal,
      sentenceToDelete,
      loading,
      formData,
      filteredSentences,
      pagination,
      editSentence,
      deleteSentence,
      confirmDelete,
      handleSubmit,
      closeModal,
      handleSearch,
      getWordText,
      formatDate,
      changePage,
      pageInput,
      goToPage,
      performSearch,
      clearSearch,
      searchPage,
      isSearching,
      searchResults,
      // 单词搜索相关变量
      wordSearchTerm,
      wordSearchResults,
      wordSearching,
      searchWords,
      selectWord,
      clearSelectedWord,
      getSelectedWordText,
      editingWordInfo,
      selectedWordInfo
    }
  }
}
</script>

<style scoped>
/* 使用与其他管理组件相同的样式 */
.sentence-manager {
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

.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group select {
  background: white;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
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
  font-size: 14px;
  text-align: center;
  transition: border-color 0.3s ease;
}

.page-input:focus {
  outline: none;
  border-color: #667eea;
}

/* 单词搜索相关样式 */
.word-search-container {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.word-search-input {
  flex: 1;
  padding: 12px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.word-search-input:focus {
  outline: none;
  border-color: #667eea;
}

.word-search-results {
  max-height: 200px;
  overflow-y: auto;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  margin-bottom: 10px;
}

.word-result-item {
  padding: 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
}

.word-result-item:hover {
  background: #f8f9fa;
}

.word-result-item.selected {
  background: #e3f2fd;
  border-left: 4px solid #2196f3;
}

.word-result-item:last-child {
  border-bottom: none;
}

.word-text {
  font-weight: 600;
  color: #333;
  margin-right: 8px;
}

.word-phonetic {
  color: #666;
  font-size: 12px;
}

.selected-word-display {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #e8f5e8;
  border: 2px solid #4caf50;
  border-radius: 8px;
  margin-bottom: 10px;
}

.selected-word-label {
  color: #2e7d32;
  font-weight: 600;
  font-size: 14px;
}

.selected-word-text {
  color: #2e7d32;
  font-weight: 600;
}

.clear-selection-btn {
  margin-left: auto;
  padding: 6px 12px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clear-selection-btn:hover {
  background: #d32f2f;
}

/* 固定单词显示样式 */
.fixed-word-display {
  padding: 12px;
  background: #f8f9fa;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.fixed-word-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fixed-word-info .word-text {
  font-weight: 600;
  color: #333;
  font-size: 16px;
  margin-right: 0;
}

.fixed-word-info .word-phonetic {
  color: #666;
  font-size: 14px;
}
</style>

