<template>
  <div class="browse-mode">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <button @click="goBack" class="back-btn">
        <span>←</span> 返回主页
      </button>
      <div class="unit-info">
        <h2>{{ unit?.name }}</h2>
        <span class="progress">{{ currentIndex + 1 }} / {{ words.length }}</span>
        <div class="keyboard-hints">
          <span>← → 翻页（循环） | ESC 返回</span>
        </div>
      </div>
             <div class="nav-controls">
         <button 
           @click="showWordList"
           class="nav-btn word-list-btn"
         >
           📋 单词列表
         </button>
         <button 
           @click="previousPage" 
           class="nav-btn prev-btn"
         >
           ← 上一页
         </button>
         <button 
           @click="nextPage" 
           class="nav-btn next-btn"
         >
           下一页 →
         </button>
       </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content" v-if="currentWord">
      <!-- 左侧：单词信息 -->
      <div class="word-section">
        <div class="word-card">
          <h1 class="word-text">{{ currentWord.word }}</h1>
          
                     <!-- 音标区域 -->
           <div class="phonetic-section">
             <div class="phonetic-item">
               <span class="phonetic-label">美音</span>
               <span class="phonetic-text">{{ currentWord.americanPhonetic || '暂无' }}</span>
               <button 
                 @click="playAudio('american')" 
                 class="audio-btn"
                 :disabled="!currentWord.americanPhonetic"
               >
                 🔊
               </button>
             </div>
             <div class="phonetic-item">
               <span class="phonetic-label">英音</span>
               <span class="phonetic-text">{{ currentWord.britishPhonetic || '暂无' }}</span>
               <button 
                 @click="playAudio('british')" 
                 class="audio-btn"
                 :disabled="!currentWord.britishPhonetic"
               >
                 🔊
               </button>
             </div>
           </div>
           
                      <!-- 词性释义区域 -->
           <div class="meanings-section" v-if="currentWord.meanings && currentWord.meanings.length > 0">
             <h4 class="meanings-title">词性释义</h4>
             <div class="meanings-list">
                                <div 
                   v-for="meaning in currentWord.meanings" 
                   :key="meaning.id" 
                   class="meaning-item"
                 >
                   <div class="meaning-left">
                     <div class="meaning-pos">{{ meaning.pos || meaning.partOfSpeech || meaning.type || '未知词性' }}</div>
                   </div>
                   <div class="meaning-right">
                     <div class="meaning-content">{{ meaning.content || meaning.meaning || '暂无释义' }}</div>
                   </div>
                 </div>
             </div>
           </div>
           <div v-else class="no-meanings">
             <p>暂无词性释义</p>
           </div>
        </div>
      </div>

      <!-- 右侧：词组和例句 -->
      <div class="content-section">
        <!-- 词组区域 -->
        <div class="phrase-section">
        <h3>词组</h3>
        <div v-if="currentWord.phrases && currentWord.phrases.length > 0" class="phrase-list">
          <div 
            v-for="phrase in currentWord.phrases" 
            :key="phrase.id" 
            class="phrase-item"
          >
            <div class="phrase-content">
              <div class="phrase-text">{{ phrase.phraseText }}</div>
              <div class="phrase-translation">{{ phrase.translation }}</div>
            </div>
            <button 
              @click="playPhraseAudio(phrase.phraseText)" 
              class="phrase-audio-btn"
              title="播放词组发音"
            >
              🔊
            </button>
          </div>
        </div>
        <div v-else class="no-content">
          <p>暂无词组</p>
        </div>
      </div>

      <!-- 例句区域 -->
      <div class="sentence-section">
        <h3>例句</h3>
        <div v-if="currentWord.sentences && currentWord.sentences.length > 0" class="sentence-list">
          <div 
            v-for="sentence in currentWord.sentences" 
            :key="sentence.id" 
            class="sentence-item"
          >
            <div class="sentence-content">
              <div class="sentence-text">{{ sentence.sentenceText }}</div>
              <div class="sentence-translation">{{ sentence.translation }}</div>
            </div>
            <button 
              @click="playSentenceAudio(sentence.sentenceText)" 
              class="sentence-audio-btn"
              title="播放例句发音"
            >
              🔊
            </button>
          </div>
        </div>
        <div v-else class="no-content">
          <p>暂无例句</p>
        </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>{{ loadingProgress || '加载中...' }}</p>
    </div>

         <!-- 错误状态 -->
     <div v-else class="error">
       <p>加载失败，请重试</p>
       <button @click="goBack" class="retry-btn">返回主页</button>
     </div>
     
     <!-- 单词列表弹窗 -->
     <div v-if="showWordListModal" class="word-list-modal" @click="closeWordList">
       <div class="word-list-content" @click.stop>
         <div class="word-list-header">
           <h3>单词列表</h3>
           <button @click="closeWordList" class="close-btn">×</button>
         </div>
         <div class="word-list-search">
           <input 
             v-model="wordListSearchTerm" 
             type="text" 
             placeholder="搜索单词..."
             class="search-input"
           >
         </div>
                   <div class="word-list-body">
            <div class="word-grid">
              <div 
                v-for="(word, index) in filteredWordList" 
                :key="word.id" 
                @click="jumpToWord(word.id)"
                class="word-grid-item"
                :class="{ 'current-word': word.id === getCurrentWordId() }"
              >
                {{ word.word }}
              </div>
            </div>
          </div>
       </div>
     </div>
   </div>
 </template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/axios'

export default {
  name: 'BrowseMode',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const unit = ref(null)
    const words = ref([])
    const currentIndex = ref(0)
         const loading = ref(true)
     const loadingProgress = ref('')
     const error = ref(null)
     
     // 单词列表弹窗相关
     const showWordListModal = ref(false)
     const wordListSearchTerm = ref('')

         // 当前单词的计算属性
     const currentWord = computed(() => {
       if (words.value.length === 0 || currentIndex.value >= words.value.length) {
         return null
       }
       return words.value[currentIndex.value]
     })
     
     // 过滤后的单词列表
     const filteredWordList = computed(() => {
       if (!wordListSearchTerm.value.trim()) {
         return words.value
       }
       const searchTerm = wordListSearchTerm.value.toLowerCase()
       return words.value.filter(word => 
         word.word.toLowerCase().includes(searchTerm) ||
         (word.americanPhonetic && word.americanPhonetic.toLowerCase().includes(searchTerm)) ||
         (word.britishPhonetic && word.britishPhonetic.toLowerCase().includes(searchTerm))
       )
     })

    // 获取单元信息
    const fetchUnit = async () => {
      try {
        const response = await api.get(`/api/units/${route.params.unitId}`)
        unit.value = response.data
      } catch (err) {
        console.error('Failed to fetch unit:', err)
        error.value = '获取单元信息失败'
        loading.value = false
      }
    }

    // 获取单元单词列表
    const fetchUnitWords = async () => {
      try {
        loading.value = true
        
        console.log(`Fetching unit words for unit ID: ${route.params.unitId}`)
        
        // 获取所有单元单词关联（处理分页）
        let allUnitWords = []
        let currentPage = 0
        let hasNext = true
        
        while (hasNext) {
          loadingProgress.value = `正在获取第 ${currentPage + 1} 页数据...`
          
          const unitWordsResponse = await api.get(`/api/unit-words/unit/${route.params.unitId}`, {
            params: {
              page: currentPage,
              size: 100 // 每页获取100条，减少请求次数
            }
          })
          
          const responseData = unitWordsResponse.data
          console.log(`Page ${currentPage} response:`, responseData)
          
          // 检查是否是分页响应，如果是则提取content数组
          let unitWords
          if (responseData.content && Array.isArray(responseData.content)) {
            unitWords = responseData.content
            hasNext = responseData.hasNext
          } else if (Array.isArray(responseData)) {
            unitWords = responseData
            hasNext = false
          } else {
            console.error('Unexpected response format:', responseData)
            throw new Error('Unexpected response format')
          }
          
          allUnitWords = allUnitWords.concat(unitWords)
          currentPage++
          
          // 防止无限循环
          if (currentPage > 10) {
            console.warn('Reached maximum page limit, stopping pagination')
            break
          }
        }
        
        console.log(`Found total ${allUnitWords.length} unit words:`, allUnitWords)
        
        // 获取每个单词的词组和例句
        loadingProgress.value = `正在获取 ${allUnitWords.length} 个单词的词组和例句...`
        
        const wordsWithDetails = await Promise.all(
          allUnitWords.map(async (unitWord, index) => {
            try {
                             // 获取词组、例句和词性释义
               console.log(`Fetching details for word ${unitWord.wordId} (${unitWord.wordText})`)
               
               const [phrasesResponse, sentencesResponse, meaningsResponse] = await Promise.all([
                 api.get(`/api/phrases/word/${unitWord.wordId}`),
                 api.get(`/api/sentences/word/${unitWord.wordId}`),
                 api.get(`/api/meanings/word/${unitWord.wordId}`)
               ])
               
               const phrases = phrasesResponse.data
               const sentences = sentencesResponse.data
               // 处理词性释义数据 - 检查是否是包装的响应对象
               let meanings = meaningsResponse.data
               if (meanings && meanings.data && Array.isArray(meanings.data)) {
                 meanings = meanings.data // 提取实际的数组数据
               }
               
               console.log(`Word ${unitWord.wordText}:`, {
                 phrases: phrases,
                 sentences: sentences,
                 meanings: meanings
               })
               
               // 调试：检查词性释义数据结构
               if (meanings && meanings.length > 0) {
                 console.log('Meanings data structure:', meanings[0])
               }
               
                               return {
                  ...unitWord,
                  word: unitWord.wordText, // 使用后端返回的wordText
                  americanPhonetic: unitWord.americanPhonetic, // 使用后端返回的美音音标
                  britishPhonetic: unitWord.britishPhonetic, // 使用后端返回的英音音标
                  phrases,
                  sentences,
                  meanings
                }
            } catch (err) {
              console.error(`Failed to get details for word ${unitWord.wordId}:`, err)
                                                               return {
                    ...unitWord,
                    word: unitWord.wordText || '未知单词',
                    americanPhonetic: unitWord.americanPhonetic || null,
                    britishPhonetic: unitWord.britishPhonetic || null,
                    phrases: [],
                    sentences: [],
                    meanings: []
                  }
            }
          })
        )
        
        words.value = wordsWithDetails
        loading.value = false
      } catch (err) {
        console.error('Failed to fetch unit words:', err)
        error.value = `获取单词列表失败: ${err.message || '未知错误'}`
        loading.value = false
      }
    }

         // 播放音频
     const playAudio = (type) => {
       const word = currentWord.value
       if (!word) return
       
       // TODO: 实现音频播放功能
       console.log(`Playing ${type} audio for word: ${word.word}`)
       alert(`播放${type === 'american' ? '美音' : '英音'}：${word.word}`)
     }
     
     // 播放词组音频
     const playPhraseAudio = (phraseText) => {
       // TODO: 实现词组音频播放功能
       console.log(`Playing phrase audio: ${phraseText}`)
       alert(`播放词组发音：${phraseText}`)
     }
     
     // 播放例句音频
     const playSentenceAudio = (sentenceText) => {
       // TODO: 实现例句音频播放功能
       console.log(`Playing sentence audio: ${sentenceText}`)
       alert(`播放例句发音：${sentenceText}`)
     }

    // 翻页功能（支持循环翻页）
    const nextPage = () => {
      if (words.value.length === 0) return
      
      if (currentIndex.value < words.value.length - 1) {
        currentIndex.value++
      } else {
        // 循环到第一页
        currentIndex.value = 0
      }
    }

    const previousPage = () => {
      if (words.value.length === 0) return
      
      if (currentIndex.value > 0) {
        currentIndex.value--
      } else {
        // 循环到最后一页
        currentIndex.value = words.value.length - 1
      }
    }

         // 返回主页
     const goBack = () => {
       router.push('/')
     }
     
     // 单词列表弹窗相关方法
     const showWordList = () => {
       showWordListModal.value = true
       wordListSearchTerm.value = ''
     }
     
     const closeWordList = () => {
       showWordListModal.value = false
       wordListSearchTerm.value = ''
     }
     
     const jumpToWord = (wordId) => {
       // 根据单词ID找到在原始数组中的索引
       const wordIndex = words.value.findIndex(word => word.id === wordId)
       if (wordIndex !== -1) {
         currentIndex.value = wordIndex
         closeWordList()
       }
     }
     
     // 获取当前单词的ID
     const getCurrentWordId = () => {
       return currentWord.value?.id || null
     }
     
     // 根据单词ID获取在原始数组中的索引
     const getWordIndex = (wordId) => {
       return words.value.findIndex(word => word.id === wordId)
     }

    onMounted(async () => {
      await fetchUnit()
      await fetchUnitWords()
      
      // 添加键盘事件监听
      window.addEventListener('keydown', handleKeydown)
    })
    
    // 清理事件监听
    onUnmounted(() => {
      window.removeEventListener('keydown', handleKeydown)
    })
    
    // 键盘快捷键处理
    const handleKeydown = (event) => {
      switch (event.key) {
        case 'ArrowLeft':
          event.preventDefault()
          previousPage()
          break
        case 'ArrowRight':
          event.preventDefault()
          nextPage()
          break
        case 'Escape':
          event.preventDefault()
          goBack()
          break
      }
    }

                         return {
           unit,
           words,
           currentIndex,
           currentWord,
           loading,
           loadingProgress,
           error,
           showWordListModal,
           wordListSearchTerm,
           filteredWordList,
           playAudio,
           playPhraseAudio,
           playSentenceAudio,
           nextPage,
           previousPage,
           goBack,
           showWordList,
           closeWordList,
           jumpToWord,
           getCurrentWordId,
           getWordIndex
         }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}
.browse-mode {
  position: fixed;
  top: 64px;
  left: 0;
  width: 100vw;
  height: calc(100vh - 64px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.unit-info {
  text-align: center;
}

.unit-info h2 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 24px;
}

.progress {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.keyboard-hints {
  font-size: 12px;
  color: #999;
}

.nav-controls {
  display: flex;
  gap: 15px;
}

.nav-btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.prev-btn {
  background: #17a2b8;
  color: white;
}

.prev-btn:hover:not(:disabled) {
  background: #138496;
  transform: translateY(-2px);
}

.next-btn {
  background: #28a745;
  color: white;
}

 .next-btn:hover:not(:disabled) {
   background: #218838;
   transform: translateY(-2px);
 }
 
 .word-list-btn {
   background: #6f42c1;
   color: white;
 }
 
 .word-list-btn:hover {
   background: #5a32a3;
   transform: translateY(-2px);
 }

.nav-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
}

.main-content {
  display: grid;
  grid-template-columns: 500px 1000px;
  gap: 30px;
  width: 1530px;
  margin: 0 auto;
  flex: 1;
  overflow: hidden;
  min-height: 0;
}

.word-section {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 100%;
  overflow: hidden;
  width: 500px;
  min-width: 500px;
  max-width: 500px;
  min-height: 100%;
  max-height: 100%;
}

.word-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
  height: 100%;
  min-height: 100%;
  max-height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.word-text {
  font-size: 48px;
  color: #333;
  margin: 0 0 30px 0;
  font-weight: 700;
}

.phonetic-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 25px;
}

.phonetic-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 15px 20px;
  background: #f8f9ff;
  border-radius: 12px;
  border: 2px solid #e1e5e9;
  min-width: 200px;
}

.meanings-section {
  margin-top: 25px;
}

.meanings-title {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  border-bottom: 2px solid #667eea;
  padding-bottom: 8px;
}

.meanings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meaning-item {
  padding: 15px;
  background: #f8f9ff;
  border-radius: 10px;
  border-left: 4px solid #667eea;
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.meaning-left {
  flex-shrink: 0;
  min-width: 80px;
}

.meaning-right {
  flex: 1;
}

.meaning-pos {
  font-size: 14px;
  color: #667eea;
  font-weight: 600;
  text-transform: uppercase;
  margin: 0;
}

.meaning-content {
  font-size: 16px;
  color: #333;
  line-height: 1.4;
  margin: 0;
}

.no-meanings {
  text-align: center;
  padding: 20px;
  color: #999;
}

.no-meanings p {
  margin: 0;
  font-size: 14px;
}

.phonetic-label {
  font-size: 14px;
  color: #666;
  min-width: 40px;
}

.phonetic-text {
  font-size: 18px;
  color: #333;
  font-family: 'Arial Unicode MS', 'Lucida Sans Unicode', sans-serif;
  min-width: 120px;
}

.audio-btn {
  background: #667eea;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-btn:hover:not(:disabled) {
  background: #5a6fd8;
  transform: scale(1.1);
}

.audio-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 30px;
  height: 100%;
  overflow: hidden;
  min-height: 100%;
  max-height: 100%;
  width: 1000px;
  min-width: 1000px;
  max-width: 1000px;
}

.phrase-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 45%;
  min-height: 45%;
  max-height: 45%;
  width: 1000px;
  min-width: 1000px;
  max-width: 1000px;
  box-sizing: border-box;
}

.sentence-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 55%;
  min-height: 55%;
  max-height: 55%;
  width: 1000px;
  min-width: 1000px;
  max-width: 1000px;
  box-sizing: border-box;
}

.phrase-section h3,
.sentence-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.phrase-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
  flex: 1;
}

.sentence-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  overflow-y: auto;
  flex: 1;
}

 .phrase-item {
   display: flex;
   align-items: flex-start;
   justify-content: space-between;
   padding: 12px;
   background: #f8f9ff;
   border-radius: 8px;
   border-left: 4px solid #667eea;
   gap: 12px;
   flex: 1;
 }

 .sentence-item {
   display: flex;
   align-items: flex-start;
   justify-content: space-between;
   padding: 15px;
   background: #f8f9ff;
   border-radius: 8px;
   border-left: 4px solid #667eea;
   gap: 15px;
 }
 
 .phrase-content,
 .sentence-content {
   flex: 1;
 }

.phrase-text,
.sentence-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
  word-wrap: break-word;
  overflow-wrap: break-word;
  hyphens: auto;
}

 .phrase-translation,
 .sentence-translation {
   font-size: 14px;
   color: #666;
   font-style: italic;
   word-wrap: break-word;
   overflow-wrap: break-word;
   hyphens: auto;
 }
 
 .phrase-audio-btn,
 .sentence-audio-btn {
   background: #667eea;
   color: white;
   border: none;
   border-radius: 50%;
   width: 36px;
   height: 36px;
   font-size: 16px;
   cursor: pointer;
   transition: all 0.3s ease;
   display: flex;
   align-items: center;
   justify-content: center;
   flex-shrink: 0;
 }
 
 .phrase-audio-btn:hover,
 .sentence-audio-btn:hover {
   background: #5a6fd8;
   transform: scale(1.1);
   box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
 }

.no-content {
  text-align: center;
  padding: 40px;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.no-content p {
  margin: 0;
  font-size: 16px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  color: white;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  text-align: center;
  color: white;
  padding: 40px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.retry-btn {
  margin-top: 20px;
  padding: 12px 24px;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

 .retry-btn:hover {
   transform: translateY(-2px);
   box-shadow: 0 4px 12px rgba(255, 255, 255, 0.3);
 }
 
 /* 单词列表弹窗样式 */
 .word-list-modal {
   position: fixed;
   top: 0;
   left: 0;
   width: 100%;
   height: 100%;
   background: rgba(0, 0, 0, 0.5);
   display: flex;
   justify-content: center;
   align-items: center;
   z-index: 1000;
 }
 
 .word-list-content {
   background: white;
   border-radius: 16px;
   width: 98%;
   max-width: 900px;
   max-height: 85vh;
   overflow: hidden;
   box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
 }
 
 .word-list-header {
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 20px 10px;
   border-bottom: 2px solid #e1e5e9;
   background: #f8f9ff;
 }
 
 .word-list-header h3 {
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
   padding: 0;
   width: 30px;
   height: 30px;
   display: flex;
   align-items: center;
   justify-content: center;
   border-radius: 50%;
   transition: all 0.3s ease;
 }
 
 .close-btn:hover {
   background: #e1e5e9;
   color: #333;
 }
 
 .word-list-search {
   padding: 20px 10px;
   border-bottom: 1px solid #e1e5e9;
 }
 
 .search-input {
   width: 100%;
   padding: 12px 16px;
   border: 2px solid #e1e5e9;
   border-radius: 8px;
   font-size: 16px;
   outline: none;
   transition: border-color 0.3s ease;
 }
 
 .search-input:focus {
   border-color: #667eea;
 }
 
 .word-list-body {
   max-height: 400px;
   overflow-y: auto;
   padding: 0;
 }
 
 .word-grid {
   display: grid;
   grid-template-columns: 1fr 1fr;
   gap: 8px;
   padding: 10px;
 }
 
 .word-grid-item {
   display: flex;
   align-items: center;
   justify-content: center;
   padding: 12px 8px;
   background: #f8f9fa;
   border: 1px solid #e1e5e9;
   border-radius: 8px;
   cursor: pointer;
   transition: all 0.3s ease;
   font-size: 16px;
   font-weight: 500;
   color: #333;
   text-align: center;
   min-height: 44px;
 }
 
 .word-grid-item:hover {
   background: #e3f2fd;
   border-color: #2196f3;
   transform: translateY(-2px);
   box-shadow: 0 4px 12px rgba(33, 150, 243, 0.2);
 }
 
 .word-grid-item.current-word {
   background: #2196f3;
   color: white;
   border-color: #1976d2;
   box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
 }

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .top-nav {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .nav-controls {
    order: -1;
  }
  
  .word-text {
    font-size: 36px;
  }
  
  .phonetic-item {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
