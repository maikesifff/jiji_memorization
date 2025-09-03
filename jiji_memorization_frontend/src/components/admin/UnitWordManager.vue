<template>
  <div class="unit-word-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <button @click="showAddModal = true" class="add-btn">
          + 添加单元单词关联
        </button>
        
        <!-- 单元筛选器 -->
        <div class="filter-group">
          <label>按单元筛选：</label>
          <select v-model="selectedUnitId" @change="filterByUnit" class="unit-filter">
            <option value="">全部单元</option>
            <option 
              v-for="unit in units" 
              :key="unit.id" 
              :value="unit.id"
            >
              {{ getTextbookName(unit.id) }} - {{ unit.name }}
            </option>
          </select>
        </div>
      </div>
      
      <div class="search-box">
        <input 
          v-model="searchTerm" 
          type="text" 
          placeholder="搜索单元名称或单词..."
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
            <th>单元名称</th>
            <th>单词</th>
            <th>音标</th>
            <th>所属教材</th>
            <th>创建时间</th>
                         <th>删除</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="unitWord in filteredUnitWords" :key="unitWord.id">
            <td>{{ unitWord.id }}</td>
            <td>{{ unitWord.unitName || '-' }}</td>
            <td>{{ unitWord.wordText || '-' }}</td>
            <td>{{ unitWord.phonetic || '-' }}</td>
            <td>{{ unitWord.textbookGrade && unitWord.textbookName ? `${unitWord.textbookGrade} - ${unitWord.textbookName}${unitWord.textbookPublisher ? `（${unitWord.textbookPublisher}）` : ''}` : '-' }}</td>
            <td>{{ formatDate(unitWord.createdAt) }}</td>
            <td>
              <button @click="deleteUnitWord(unitWord.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredUnitWords.length === 0" class="no-data">
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
    
         <!-- 添加模态框 -->
     <div v-if="showAddModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
                     <h3>添加单元单词关联</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        
        <form @submit.prevent="handleSubmit" class="modal-form">
          <div class="form-group">
            <label>单元 *</label>
            <select v-model="formData.unitId" required>
              <option value="">请选择单元</option>
              <option 
                v-for="unit in units" 
                :key="unit.id" 
                :value="unit.id"
              >
                {{ getTextbookName(unit.id) }} - {{ unit.name }}
              </option>
            </select>
          </div>
          
                     <div class="form-group">
             <label>单词 *</label>
             <div class="word-input-group">
               <input 
                 v-model="wordSearchTerm" 
                 type="text" 
                 placeholder="请输入要查找的单词..."
                 @input="searchWord"
                 class="word-input"
               />
               <button 
                 type="button" 
                 @click="searchWord" 
                 class="search-word-btn"
                 :disabled="!wordSearchTerm.trim()"
               >
                 查找
               </button>
             </div>
             
             <!-- 单词搜索结果 -->
             <div v-if="wordSearchResults.length > 0" class="word-results">
               <div class="word-result-item" 
                    v-for="word in wordSearchResults" 
                    :key="word.id"
                    @click="selectWord(word)"
                    :class="{ 'selected': formData.wordId === word.id }"
               >
                 <span class="word-text">{{ word.wordText }}</span>
                                 <span v-if="word.phonetic" class="word-phonetic">
              [{{ word.phonetic }}]
            </span>
               </div>
             </div>
             
             <!-- 未找到单词的提示 -->
             <div v-if="wordSearchTerm && wordSearchResults.length === 0 && !wordSearching && !formData.wordId" class="no-word-found">
               未找到单词 "{{ wordSearchTerm }}"
             </div>
             
             <!-- 已选择的单词显示 -->
             <div v-if="formData.wordId" class="selected-word-display">
               <span class="selected-word-text">
                 已选择: {{ getSelectedWordText() }}
               </span>
               <button type="button" @click="clearSelectedWord" class="clear-word-btn">
                 清除
               </button>
             </div>
           </div>
          
          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
                         <button type="submit" class="submit-btn">
               添加
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
          <p>确定要删除这个单元单词关联吗？</p>
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
  name: 'UnitWordManager',
  setup() {
    const unitWords = ref([])
    const units = ref([])
    const words = ref([])
    const textbooks = ref([])
    const searchTerm = ref('')
         const showAddModal = ref(false)
     const showDeleteModal = ref(false)
    const unitWordToDelete = ref(null)
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
    
    // 筛选相关
    const selectedUnitId = ref('')
    
         const formData = reactive({
       unitId: '',
       wordId: ''
     })
     
     // 单词搜索相关
     const wordSearchTerm = ref('')
     const wordSearchResults = ref([])
     const wordSearching = ref(false)
    
    // 搜索状态
    const isSearching = ref(false)
    const searchResults = ref([])
    
    // 执行搜索
    const performSearch = async () => {
      if (!searchTerm.value.trim()) {
        isSearching.value = false
        searchResults.value = []
        currentPage.value = 0
        await fetchUnitWords()
        return
      }
      
      try {
        loading.value = true
        isSearching.value = true
        currentPage.value = 0
        
        // 构建搜索URL，考虑单元筛选
        let searchUrl = `/api/unit-words/search?keyword=${encodeURIComponent(searchTerm.value)}&page=0&size=${pageSize.value}`
        if (selectedUnitId.value) {
          searchUrl = `/api/unit-words/unit/${selectedUnitId.value}/search?keyword=${encodeURIComponent(searchTerm.value)}&page=0&size=${pageSize.value}`
        }
        
        const response = await api.get(searchUrl)
        
        if (response.data && response.data.content) {
          searchResults.value = response.data.content
          
          pagination.value = {
            currentPage: response.data.currentPage,
            totalPages: response.data.totalPages,
            totalItems: response.data.totalItems,
            hasNext: response.data.hasNext,
            hasPrevious: response.data.hasPrevious
          }
        } else {
          console.warn('搜索响应格式不正确:', response.data)
          searchResults.value = []
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
      await fetchUnitWords()
    }
    
    // 搜索分页
    const searchPage = async (page) => {
      if (!searchTerm.value.trim()) return
      
      try {
        loading.value = true
        currentPage.value = page
        
        // 构建搜索URL，考虑单元筛选
        let searchUrl = `/api/unit-words/search?keyword=${encodeURIComponent(searchTerm.value)}&page=${page}&size=${pageSize.value}`
        if (selectedUnitId.value) {
          searchUrl = `/api/unit-words/unit/${selectedUnitId.value}/search?keyword=${encodeURIComponent(searchTerm.value)}&page=${page}&size=${pageSize.value}`
        }
        
        const response = await api.get(searchUrl)
        
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
    
         const filteredUnitWords = computed(() => {
      // 如果正在搜索，返回搜索结果
      if (isSearching.value) {
        return searchResults.value
      }
      
      // 如果没有搜索词，返回当前页数据
      if (!searchTerm.value) {
        return unitWords.value
      }
      
      // 如果不在搜索状态，但在当前页数据中找到了匹配项，则返回过滤结果
      const filtered = unitWords.value.filter(unitWord => {
        const unitName = unitWord.unitName || ''
        const wordText = unitWord.wordText || ''
        return unitName.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
               wordText.toLowerCase().includes(searchTerm.value.toLowerCase())
      })
      
      // 如果当前页没有找到匹配项，自动触发全局搜索
      if (filtered.length === 0 && searchTerm.value.trim()) {
        performSearch()
      }
      return filtered
    })
    
         // 获取单元单词关联列表
     const fetchUnitWords = async () => {
       try {
         loading.value = true
         let url = `/api/unit-words?page=${currentPage.value}&size=${pageSize.value}`
         
         // 如果选择了特定单元，添加筛选参数
         if (selectedUnitId.value) {
           url = `/api/unit-words/unit/${selectedUnitId.value}?page=${currentPage.value}&size=${pageSize.value}`
         }
         
         const response = await api.get(url)
 
         
         // 处理分页数据
         if (response.data && response.data.content) {
           unitWords.value = response.data.content
           
           pagination.value = {
             currentPage: response.data.currentPage,
             totalPages: response.data.totalPages,
             totalItems: response.data.totalItems,
             hasNext: response.data.hasNext,
             hasPrevious: response.data.hasPrevious
           }
           
           // 现在后端直接返回完整的DTO数据，不需要再获取单词信息
         } else {
           console.warn('响应格式不正确:', response.data)
           unitWords.value = response.data || []
           pagination.value = {
             currentPage: 0,
             totalPages: 1,
             totalItems: unitWords.value.length,
             hasNext: false,
             hasPrevious: false
           }
         }
       } catch (error) {
         console.error('Failed to fetch unit words:', error)
         unitWords.value = []
         pagination.value = {
           currentPage: 0,
           totalPages: 1,
           totalItems: 0,
           hasNext: false,
           hasPrevious: false
         }
       } finally {
         loading.value = false
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
    
             // 单词搜索功能
    const searchWord = async () => {
      if (!wordSearchTerm.value.trim()) {
        wordSearchResults.value = []
        return
      }
      
      // 如果搜索词与当前选中的单词不同，清空选中的单词信息
      if (selectedWordInfo.value && wordSearchTerm.value.trim() !== selectedWordInfo.value.wordText) {
        formData.wordId = ''
        selectedWordInfo.value = null
      }
      
      try {
        wordSearching.value = true
        const response = await api.get(`/api/words/search?keyword=${encodeURIComponent(wordSearchTerm.value.trim())}`)
        
        if (response.data && response.data.content) {
          wordSearchResults.value = response.data.content
        } else {
          wordSearchResults.value = response.data || []
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
        wordText: word.wordText,
        phonetic: word.phonetic
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
      return `${selectedWordInfo.value.wordText}${selectedWordInfo.value.phonetic ? ` [${selectedWordInfo.value.phonetic}]` : ''}`
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
    
    // 添加单元单词关联
    const addUnitWord = async () => {
      try {
        const response = await api.post('/api/unit-words', formData)
        
        // 检查响应状态
        if (response.data && response.data.status === 'success') {
          // 添加成功后，重新获取当前页数据以确保数据完整性
          await fetchUnitWords()
          closeModal()
        } else {
          // 显示错误信息
          alert(response.data?.message || '添加单元单词关联失败')
        }
      } catch (error) {
        console.error('Failed to add unit word:', error)
        // 显示错误信息
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else {
          alert('添加单元单词关联失败: ' + error.message)
        }
      }
    }
    
    
    
    // 删除单元单词关联
    const deleteUnitWord = (unitWordId) => {
      unitWordToDelete.value = unitWords.value.find(uw => uw.id === unitWordId)
      showDeleteModal.value = true
    }
    
    // 确认删除
    const confirmDelete = async () => {
      try {
        await api.delete(`/api/unit-words/${unitWordToDelete.value.id}`)
        const index = unitWords.value.findIndex(uw => uw.id === unitWordToDelete.value.id)
        if (index !== -1) {
          unitWords.value.splice(index, 1)
        }
        showDeleteModal.value = false
        unitWordToDelete.value = null
      } catch (error) {
        console.error('Failed to delete unit word:', error)
      }
    }
    
         // 提交表单
     const handleSubmit = () => {
       addUnitWord()
     }
    
             // 关闭模态框
    const closeModal = () => {
      showAddModal.value = false
      formData.unitId = ''
      formData.wordId = ''
      wordSearchTerm.value = ''
      wordSearchResults.value = []
      selectedWordInfo.value = null
    }
    
    // 搜索处理
    const handleSearch = () => {
      performSearch()
    }
    
    // 获取单元名称
    const getUnitName = (unitId) => {
      const unit = units.value.find(u => u.id === unitId)
      return unit ? unit.name : '-'
    }
    
         // 获取单词文本
     const getWordText = (wordId) => {
       const word = words.value.find(w => w.id === wordId)
       return word ? word.wordText : '-'
     }
     
     // 获取单词音标
         const getWordPhonetic = (wordId) => {
      const word = words.value.find(w => w.id === wordId)
      return word ? (word.phonetic || '-') : '-'
    }
    
    // 获取教材名称
    const getTextbookName = (unitId) => {
      const unit = units.value.find(u => u.id === unitId)
      if (!unit) return '-'
      const textbook = textbooks.value.find(t => t.id === unit.textbookId)
      return textbook ? `${textbook.grade} - ${textbook.name}` : '-'
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
        await fetchUnitWords()
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
          fetchUnitWords()
        }
      }
    }
    
    // 监听分页变化，同步输入框
    watch(() => pagination.value.currentPage, (newPage) => {
      pageInput.value = newPage + 1
    })
    
    // 监听搜索词变化，如果与选中的单词不同则清空选中状态
    watch(wordSearchTerm, (newValue) => {
      if (selectedWordInfo.value && newValue.trim() !== selectedWordInfo.value.wordText) {
        formData.wordId = ''
        selectedWordInfo.value = null
      }
    })
    
    // 按单元筛选
    const filterByUnit = () => {
      currentPage.value = 0 // 重置到第一页
      fetchUnitWords()
    }
    
         onMounted(async () => {
       await fetchUnitWords()
       await fetchUnits()
       await fetchTextbooks()
     })
    
         return {
       unitWords,
       units,
       words,
       textbooks,
       searchTerm,
       selectedUnitId,
       showAddModal,
       showDeleteModal,
       unitWordToDelete,
       loading,
       formData,
       filteredUnitWords,
       pagination,
       deleteUnitWord,
       confirmDelete,
       handleSubmit,
       closeModal,
       handleSearch,
       getUnitName,
       getWordText,
       getWordPhonetic,
       getTextbookName,
       formatDate,
       changePage,
       filterByUnit,
       // 单词搜索相关
       wordSearchTerm,
       wordSearchResults,
       wordSearching,
       searchWord,
       selectWord,
       clearSelectedWord,
       getSelectedWordText,
       pageInput,
       goToPage,
       performSearch,
       clearSearch,
       searchPage,
       isSearching,
       searchResults
     }
  }
}
</script>

<style scoped>
.unit-word-manager {
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

.left-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  white-space: nowrap;
}

.unit-filter {
  padding: 8px 12px;
  border: 2px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  min-width: 200px;
  transition: border-color 0.3s ease;
}

.unit-filter:focus {
  outline: none;
  border-color: #667eea;
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

.form-group select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  background: white;
}

.form-group select:focus {
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
   padding: 8px 10px;
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
 .word-input-group {
   display: flex;
   gap: 10px;
   align-items: center;
 }
 
 .word-input {
   flex: 1;
   padding: 12px;
   border: 2px solid #e1e5e9;
   border-radius: 8px;
   font-size: 14px;
   transition: border-color 0.3s ease;
 }
 
 .word-input:focus {
   outline: none;
   border-color: #667eea;
 }
 
 .search-word-btn {
   padding: 12px 20px;
   background: #667eea;
   color: white;
   border: none;
   border-radius: 8px;
   font-size: 14px;
   font-weight: 500;
   cursor: pointer;
   transition: background 0.3s ease;
   white-space: nowrap;
 }
 
 .search-word-btn:hover:not(:disabled) {
   background: #5a6fd8;
 }
 
 .search-word-btn:disabled {
   background: #ccc;
   cursor: not-allowed;
 }
 
 .word-results {
   margin-top: 10px;
   max-height: 200px;
   overflow-y: auto;
   border: 1px solid #e1e5e9;
   border-radius: 6px;
   background: white;
 }
 
 .word-result-item {
   padding: 10px 12px;
   cursor: pointer;
   border-bottom: 1px solid #f0f0f0;
   transition: background 0.2s ease;
   display: flex;
   align-items: center;
   gap: 10px;
 }
 
 .word-result-item:hover {
   background: #f8f9fa;
 }
 
 .word-result-item.selected {
   background: #e3f2fd;
   border-left: 3px solid #2196f3;
 }
 
 .word-result-item:last-child {
   border-bottom: none;
 }
 
 .word-text {
   font-weight: 500;
   color: #333;
 }
 
 .word-phonetic {
   color: #666;
   font-size: 12px;
   font-style: italic;
 }
 
 .no-word-found {
   margin-top: 10px;
   padding: 10px;
   background: #fff3cd;
   border: 1px solid #ffeaa7;
   border-radius: 6px;
   color: #856404;
   font-size: 14px;
   text-align: center;
 }
 
 .selected-word-display {
   margin-top: 10px;
   padding: 10px;
   background: #d4edda;
   border: 1px solid #c3e6cb;
   border-radius: 6px;
   display: flex;
   justify-content: space-between;
   align-items: center;
 }
 
 .selected-word-text {
   color: #155724;
   font-weight: 500;
 }
 
 .clear-word-btn {
   padding: 6px 12px;
   background: #dc3545;
   color: white;
   border: none;
   border-radius: 4px;
   font-size: 12px;
   cursor: pointer;
   transition: background 0.3s ease;
 }
 
 .clear-word-btn:hover {
   background: #c82333;
 }
</style>
