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
          <span class="mode-title">浏览学习</span>
        </div>
      </div>
             <div class="nav-controls">
         <button @click="showKeyboardShortcuts = true" class="shortcuts-btn" title="键盘快捷键">
           ?
         </button>
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
          <div class="word-header">
          <h1 class="word-text">{{ currentWord.word }}</h1>
            <div class="word-actions">
              <button 
                @click="toggleVocabularyNotebook" 
                class="vocab-btn"
                :class="{ 'in-notebook': currentWord.isInNotebook }"
                :title="currentWord.isInNotebook ? '从生词本中移除' : '添加到生词本'"
              >
                <span v-if="currentWord.isInNotebook">📚</span>
                <span v-else>📖</span>
                {{ currentWord.isInNotebook ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
          
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

   <!-- 键盘快捷键弹窗 -->
   <div v-if="showKeyboardShortcuts" class="modal-overlay" @click="showKeyboardShortcuts = false">
     <div class="modal-content shortcuts-modal" @click.stop>
       <h2>键盘快捷键</h2>
       <div class="shortcuts-content">
         <div class="shortcut-section">
           <h3>通用快捷键</h3>
           <div class="shortcut-item">
             <span class="key">ESC</span>
             <span class="description">返回主页</span>
           </div>
         </div>
         
         <div class="shortcut-section">
           <h3>浏览学习</h3>
           <div class="shortcut-item">
             <span class="key">A / ←</span>
             <span class="description">上一页</span>
           </div>
           <div class="shortcut-item">
             <span class="key">D / →</span>
             <span class="description">下一页</span>
           </div>
           <div class="shortcut-item">
             <span class="key">Q</span>
             <span class="description">美式发音</span>
           </div>
           <div class="shortcut-item">
             <span class="key">E</span>
             <span class="description">英式发音</span>
           </div>
           <div class="shortcut-item">
             <span class="key">U I O</span>
             <span class="description">词组发音（1-3）</span>
           </div>
           <div class="shortcut-item">
             <span class="key">J K L</span>
             <span class="description">例句发音（1-3）</span>
           </div>
           <div class="shortcut-item">
             <span class="key">V</span>
             <span class="description">添加/移除生词本</span>
           </div>
         </div>
       </div>
       <div class="modal-actions">
         <button @click="showKeyboardShortcuts = false" class="modal-btn primary">知道了</button>
       </div>
     </div>
   </div>
 </template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/utils/axios'

export default {
  name: 'BrowseMode',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const authStore = useAuthStore()
    
    const unit = ref(null)
    const words = ref([])
    const currentIndex = ref(0)
         const loading = ref(true)
     const loadingProgress = ref('')
     const error = ref(null)
     
     // 单词列表弹窗相关
     const showWordListModal = ref(false)
     const wordListSearchTerm = ref('')
     
     // 键盘快捷键弹窗
     const showKeyboardShortcuts = ref(false)

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
                             // 获取词组、例句、词性释义和生词本状态
               console.log(`Fetching details for word ${unitWord.wordId} (${unitWord.wordText})`)
               
               const [phrasesResponse, sentencesResponse, meaningsResponse, notebookResponse] = await Promise.all([
                 api.get(`/api/phrases/word/${unitWord.wordId}`),
                 api.get(`/api/sentences/word/${unitWord.wordId}`),
                 api.get(`/api/meanings/word/${unitWord.wordId}`),
                 api.get(`/api/vocabulary-notebook/check`, {
                   params: {
                     userId: authStore.currentUser.id,
                     wordId: unitWord.wordId
                   }
                 })
               ])
               
               const phrases = phrasesResponse.data
               const sentences = sentencesResponse.data
               // 处理词性释义数据 - 检查是否是包装的响应对象
               let meanings = meaningsResponse.data
               if (meanings && meanings.data && Array.isArray(meanings.data)) {
                 meanings = meanings.data // 提取实际的数组数据
               }
               
               // 处理生词本状态
               const isInNotebook = notebookResponse.data?.isInNotebook || false
               
               console.log(`Word ${unitWord.wordText}:`, {
                 phrases: phrases,
                 sentences: sentences,
                 meanings: meanings,
                 isInNotebook: isInNotebook
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
                  pronUs: unitWord.pronUs, // 美音发音
                  pronUk: unitWord.pronUk, // 英音发音
                  phrases,
                  sentences,
                  meanings,
                  isInNotebook // 生词本状态
                }
            } catch (err) {
              console.error(`Failed to get details for word ${unitWord.wordId}:`, err)
                                                               return {
                    ...unitWord,
                    word: unitWord.wordText || '未知单词',
                    americanPhonetic: unitWord.americanPhonetic || null,
                    britishPhonetic: unitWord.britishPhonetic || null,
                    pronUs: unitWord.pronUs || null,
                    pronUk: unitWord.pronUk || null,
                    phrases: [],
                    sentences: [],
                    meanings: [],
                    isInNotebook: false // 默认不在生词本中
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

    // 切换生词本状态
    const toggleVocabularyNotebook = async () => {
      if (!currentWord.value || !authStore.currentUser) return
      
      const word = currentWord.value
      const isCurrentlyInNotebook = word.isInNotebook
      
      try {
        if (isCurrentlyInNotebook) {
          // 从生词本中移除
          await api.delete('/api/vocabulary-notebook/remove', {
            params: {
              userId: authStore.currentUser.id,
              wordId: word.wordId
            }
          })
          
          // 更新本地状态
          word.isInNotebook = false
          
          // 更新words数组中的对应项
          const wordIndex = words.value.findIndex(w => w.wordId === word.wordId)
          if (wordIndex !== -1) {
            words.value[wordIndex].isInNotebook = false
          }
          
          console.log(`Word "${word.word}" removed from vocabulary notebook`)
        } else {
          // 添加到生词本
          await api.post('/api/vocabulary-notebook/add', {
            userId: authStore.currentUser.id,
            wordId: word.wordId
          })
          
          // 更新本地状态
          word.isInNotebook = true
          
          // 更新words数组中的对应项
          const wordIndex = words.value.findIndex(w => w.wordId === word.wordId)
          if (wordIndex !== -1) {
            words.value[wordIndex].isInNotebook = true
          }
          
          console.log(`Word "${word.word}" added to vocabulary notebook`)
        }
      } catch (error) {
        console.error('Failed to toggle vocabulary notebook:', error)
        alert(`操作失败: ${error.response?.data?.message || error.message || '未知错误'}`)
      }
    }

         // 播放音频
     const playAudio = (type) => {
       const word = currentWord.value
       if (!word) return
       
       const audioData = type === 'american' ? word.pronUs : word.pronUk
       if (!audioData) {
         console.warn(`No audio data for ${type} pronunciation of word: ${word.word}`)
         return
       }
       
       try {
         // 创建音频元素
         const audio = new Audio()
         
         // 判断是URL还是base64数据
         if (audioData.startsWith('http://') || audioData.startsWith('https://')) {
           // URL格式
           audio.src = audioData
         } else if (audioData.startsWith('data:audio/')) {
           // 完整的data URL格式
           audio.src = audioData
         } else {
           // 假设是base64数据，添加data URL前缀
           audio.src = `data:audio/mpeg;base64,${audioData}`
         }
         
         // 播放音频
         audio.play().catch(error => {
           console.error('Audio play failed:', error)
           // 如果播放失败，尝试其他格式
           if (!audioData.startsWith('data:audio/')) {
             audio.src = `data:audio/wav;base64,${audioData}`
             audio.play().catch(err => {
               console.error('Audio play failed with wav format:', err)
             })
           }
         })
         
       console.log(`Playing ${type} audio for word: ${word.word}`)
       } catch (error) {
         console.error('Error creating audio:', error)
       }
     }
     
     // 播放词组音频
     const playPhraseAudio = (phraseText) => {
       const word = currentWord.value
       if (!word || !word.phrases) return
       
       // 找到对应的词组
       const phrase = word.phrases.find(p => p.phraseText === phraseText)
       if (!phrase || !phrase.pron) {
         console.warn(`No audio data for phrase: ${phraseText}`)
         return
       }
       
       try {
         const audio = new Audio()
         
         // 判断是URL还是base64数据
         if (phrase.pron.startsWith('http://') || phrase.pron.startsWith('https://')) {
           audio.src = phrase.pron
         } else if (phrase.pron.startsWith('data:audio/')) {
           audio.src = phrase.pron
         } else {
           audio.src = `data:audio/mpeg;base64,${phrase.pron}`
         }
         
         audio.play().catch(error => {
           console.error('Phrase audio play failed:', error)
           if (!phrase.pron.startsWith('data:audio/')) {
             audio.src = `data:audio/wav;base64,${phrase.pron}`
             audio.play().catch(err => {
               console.error('Phrase audio play failed with wav format:', err)
             })
           }
         })
         
       console.log(`Playing phrase audio: ${phraseText}`)
       } catch (error) {
         console.error('Error creating phrase audio:', error)
       }
     }
     
     // 播放例句音频
     const playSentenceAudio = (sentenceText) => {
       const word = currentWord.value
       if (!word || !word.sentences) return
       
       // 找到对应的例句
       const sentence = word.sentences.find(s => s.sentenceText === sentenceText)
       if (!sentence || !sentence.pron) {
         console.warn(`No audio data for sentence: ${sentenceText}`)
         return
       }
       
       try {
         const audio = new Audio()
         
         // 判断是URL还是base64数据
         if (sentence.pron.startsWith('http://') || sentence.pron.startsWith('https://')) {
           audio.src = sentence.pron
         } else if (sentence.pron.startsWith('data:audio/')) {
           audio.src = sentence.pron
         } else {
           audio.src = `data:audio/mpeg;base64,${sentence.pron}`
         }
         
         audio.play().catch(error => {
           console.error('Sentence audio play failed:', error)
           if (!sentence.pron.startsWith('data:audio/')) {
             audio.src = `data:audio/wav;base64,${sentence.pron}`
             audio.play().catch(err => {
               console.error('Sentence audio play failed with wav format:', err)
             })
           }
         })
         
       console.log(`Playing sentence audio: ${sentenceText}`)
       } catch (error) {
         console.error('Error creating sentence audio:', error)
       }
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
      switch (event.key.toLowerCase()) {
        case 'arrowleft':
        case 'a':
          event.preventDefault()
          previousPage()
          break
        case 'arrowright':
        case 'd':
          event.preventDefault()
          nextPage()
          break
        case 'escape':
          event.preventDefault()
          goBack()
          break
        case 'q':
          event.preventDefault()
          if (currentWord.value && currentWord.value.pronUs) {
            playAudio('american')
          }
          break
        case 'e':
          event.preventDefault()
          if (currentWord.value && currentWord.value.pronUk) {
            playAudio('british')
          }
          break
        case 'u':
          event.preventDefault()
          if (currentWord.value && currentWord.value.phrases && currentWord.value.phrases[0]) {
            playPhraseAudio(currentWord.value.phrases[0].phraseText)
          }
          break
        case 'i':
          event.preventDefault()
          if (currentWord.value && currentWord.value.phrases && currentWord.value.phrases[1]) {
            playPhraseAudio(currentWord.value.phrases[1].phraseText)
          }
          break
        case 'o':
          event.preventDefault()
          if (currentWord.value && currentWord.value.phrases && currentWord.value.phrases[2]) {
            playPhraseAudio(currentWord.value.phrases[2].phraseText)
          }
          break
        case 'j':
          event.preventDefault()
          if (currentWord.value && currentWord.value.sentences && currentWord.value.sentences[0]) {
            playSentenceAudio(currentWord.value.sentences[0].sentenceText)
          }
          break
        case 'k':
          event.preventDefault()
          if (currentWord.value && currentWord.value.sentences && currentWord.value.sentences[1]) {
            playSentenceAudio(currentWord.value.sentences[1].sentenceText)
          }
          break
        case 'l':
          event.preventDefault()
          if (currentWord.value && currentWord.value.sentences && currentWord.value.sentences[2]) {
            playSentenceAudio(currentWord.value.sentences[2].sentenceText)
          }
          break
        case 'v':
          event.preventDefault()
          toggleVocabularyNotebook()
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
           showKeyboardShortcuts,
           filteredWordList,
           playAudio,
           playPhraseAudio,
           playSentenceAudio,
           toggleVocabularyNotebook,
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
/* 整体布局 - 适应组件容器，无滚动条 */
.browse-mode {
  width: 100%;
  height: calc(100vh - 64px); /* 减去导航栏高度 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 顶部导航栏 - 独立岛状设计 */
.top-nav {
  height: 8vh;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2vw;
  margin: 2vh 2vw 0 2vw;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.back-btn {
  background: rgba(255, 255, 255, 0.8);
  color: #666;
  border: 1px solid rgba(0, 0, 0, 0.1);
  padding: 1vh 1.5vw;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1vw;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.unit-info {
  text-align: center;
  flex: 1;
}

.unit-info h2 {
  margin: 0;
  font-size: 1.3vw;
  color: #2c3e50;
  font-weight: 700;
  line-height: 1.2;
}

.progress {
  display: block;
  font-size: 0.9vw;
  color: #7f8c8d;
  margin-top: 0.3vh;
  line-height: 1.2;
}

.keyboard-hints {
  font-size: 0.7vw;
  color: #95a5a6;
  margin-top: 0.2vh;
  line-height: 1.2;
}

.mode-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.8rem;
  display: inline-block;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.nav-controls {
  display: flex;
  align-items: center;
  gap: 0.5vw;
}

.nav-btn {
  background: linear-gradient(45deg, #74b9ff, #0984e3);
  color: white;
  border: none;
  padding: 1vh 1.5vw;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9vw;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(116, 185, 255, 0.3);
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(116, 185, 255, 0.4);
}

.word-list-btn {
  background: linear-gradient(45deg, #a29bfe, #6c5ce7);
  box-shadow: 0 4px 15px rgba(162, 155, 254, 0.3);
}

.word-list-btn:hover {
  box-shadow: 0 6px 20px rgba(162, 155, 254, 0.4);
}

/* 主要内容区域 - 占据剩余高度 */
.main-content {
  height: calc(100% - 10vh);
  display: flex;
  padding: 2vh 4vw;
  gap: 2vw;
  max-width: 90vw;
  margin: 0 auto;
}

/* 左侧单词区域 - 固定宽度 */
.word-section {
  width: 30vw;
  min-width: 30vw;
  max-width: 30vw;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.word-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 3vh 2.5vw;
  height: 100%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.word-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1vh;
  margin-bottom: 2vh;
}

.word-text {
  font-size: 2.5vw;
  font-weight: 800;
  color: #2c3e50;
  text-align: center;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  line-height: 1.2;
}

.word-actions {
  display: flex;
  justify-content: center;
}

.vocab-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.vocab-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.vocab-btn.in-notebook {
  background: linear-gradient(135deg, #38a169, #2f855a);
  box-shadow: 0 2px 8px rgba(56, 161, 105, 0.3);
}

.vocab-btn.in-notebook:hover {
  box-shadow: 0 4px 12px rgba(56, 161, 105, 0.4);
}

/* 音标区域 */
.phonetic-section {
  margin: 1vh 0;
}

.phonetic-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1vw;
  margin: 1vh 0;
  padding: 1vh 1vw;
  background: rgba(116, 185, 255, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(116, 185, 255, 0.2);
}

.phonetic-label {
  font-size: 1vw;
  font-weight: 600;
  color: #0984e3;
  min-width: 3vw;
}

.phonetic-text {
  font-size: 1.2vw;
  color: #2c3e50;
  font-family: 'Times New Roman', serif;
  flex: 1;
  text-align: center;
}

.audio-btn {
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
  border: none;
  width: 2.5vw;
  height: 2.5vw;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
}

.audio-btn:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

.audio-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  box-shadow: none;
}

/* 词性释义区域 */
.meanings-section {
  flex: 1;
  overflow: hidden;
}

.meanings-title {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 1vh 0;
  text-align: left;
  border-bottom: 2px solid #74b9ff;
  padding-bottom: 0.5vh;
}

.meanings-list {
  height: calc(100% - 2.5vh);
  overflow-y: auto;
  padding-right: 1vw;
  padding-bottom: 1.5vh;
}

.meanings-list::-webkit-scrollbar {
  width: 0.5vw;
}

.meanings-list::-webkit-scrollbar-track {
  background: rgba(116, 185, 255, 0.1);
  border-radius: 10px;
}

.meanings-list::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, #74b9ff, #0984e3);
  border-radius: 10px;
}

.meaning-item {
  display: flex;
  margin: 1.5vh 0;
  padding: 1.5vh 1.5vw;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border-left: 4px solid #74b9ff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.meaning-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.meaning-left {
  min-width: 4.8vw;
}

.meaning-pos {
  background: linear-gradient(45deg, #74b9ff, #0984e3);
  color: white;
  padding: 0.5vh 1vw;
  border-radius: 6px;
  font-size: 0.9vw;
  font-weight: 600;
  text-align: center;
  box-shadow: 0 2px 10px rgba(116, 185, 255, 0.3);
}

.meaning-right {
  flex: 1;
  margin-left: 1vw;
}

.meaning-content {
  font-size: 1.1vw;
  color: #2c3e50;
  line-height: 1.6;
  font-weight: 500;
}

.no-meanings {
  text-align: center;
  color: #7f8c8d;
  font-size: 1.2vw;
  margin-top: 5vh;
}

/* 右侧内容区域 - 固定宽度 */
.content-section {
  width: 56vw;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 2vh;
}

/* 词组区域 - 占据上半部分，比例句区域矮 */
.phrase-section {
  height: 40vh;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 2vh 2vw;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
}

.phrase-section h3 {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 2vh 0;
  text-align: left;
  border-bottom: 2px solid #a29bfe;
  padding-bottom: 1vh;
}

.phrase-list {
  flex: 1;
  overflow: hidden;
  padding-right: 1vw;
  display: flex;
  flex-direction: column;
  gap: 0.8vh;
  justify-content: flex-start;
}

.phrase-item {
  display: flex;
  align-items: center;
  margin: 0;
  padding: 1.5vh 1.5vw;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border-left: 4px solid #a29bfe;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  flex-shrink: 0;
  height: 7vh;
  min-height: 7vh;
  max-height: 7vh;
}

.phrase-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.phrase-content {
  flex: 1;
}

.phrase-text {
  font-size: 1.2vw;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.2vh;
  line-height: 1.2;
}

.phrase-translation {
  font-size: 1vw;
  color: #7f8c8d;
  line-height: 1.2;
}

.phrase-audio-btn {
  background: linear-gradient(45deg, #a29bfe, #6c5ce7);
  color: white;
  border: none;
  width: 2.5vw;
  height: 2.5vw;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(162, 155, 254, 0.3);
  margin-left: 1vw;
}

.phrase-audio-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(162, 155, 254, 0.4);
}

/* 例句区域 - 占据下半部分，比词组区域高 */
.sentence-section {
  height: 50vh;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 12px;
  padding: 2vh 2vw;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
}

.sentence-section h3 {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 2vh 0;
  text-align: left;
  border-bottom: 2px solid #00b894;
  padding-bottom: 1vh;
}

.sentence-list {
  flex: 1;
  overflow: hidden;
  padding-right: 1vw;
  display: flex;
  flex-direction: column;
  gap: 0.8vh;
  justify-content: flex-start;
}

.sentence-item {
  display: flex;
  align-items: center;
  margin: 0;
  padding: 2vh 1.5vw;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border-left: 4px solid #00b894;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  flex-shrink: 0;
  height: 10vh;
  min-height: 10vh;
  max-height: 10vh;
}

.sentence-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.sentence-content {
  flex: 1;
}

.sentence-text {
  font-size: 1.1vw;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.2vh;
  line-height: 1.2;
}

.sentence-translation {
  font-size: 0.9vw;
  color: #7f8c8d;
  line-height: 1.2;
}

.sentence-audio-btn {
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
  border: none;
  width: 2.5vw;
  height: 2.5vw;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
  margin-left: 1vw;
}

.sentence-audio-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

/* 无内容状态 */
.no-content {
  text-align: center;
  color: #7f8c8d;
  font-size: 1.2vw;
  margin-top: 5vh;
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 92vh;
  color: white;
}

.loading-spinner {
  width: 4vw;
  height: 4vw;
  border: 0.5vw solid rgba(255, 255, 255, 0.3);
  border-top: 0.5vw solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 2vh;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  font-size: 1.2vw;
  margin: 0;
}

/* 错误状态 */
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 92vh;
  color: white;
  text-align: center;
}

.error p {
  font-size: 1.5vw;
  margin-bottom: 2vh;
}

.retry-btn {
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
  color: white;
  border: none;
  padding: 1.5vh 3vw;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1vw;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

/* 单词列表弹窗 */
.word-list-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.word-list-content {
  width: 60vw;
  height: 70vh;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.word-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2vh 2vw;
  background: linear-gradient(45deg, #74b9ff, #0984e3);
  color: white;
}

.word-list-header h3 {
  margin: 0;
  font-size: 1.5vw;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 2vw;
  cursor: pointer;
  width: 2.5vw;
  height: 2.5vw;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.word-list-search {
  padding: 2vh 2vw;
  border-bottom: 1px solid #e0e0e0;
}

.search-input {
  width: 100%;
  padding: 1.5vh 1.5vw;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1vw;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: #74b9ff;
  box-shadow: 0 0 0 3px rgba(116, 185, 255, 0.1);
}

.word-list-body {
  flex: 1;
  overflow-y: auto;
  padding: 2vh 2vw;
}

.word-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1vw;
}

.word-grid-item {
  padding: 1.5vh 1vw;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  font-size: 1vw;
  font-weight: 600;
  color: #495057;
  transition: all 0.3s ease;
}

.word-grid-item:hover {
  background: #74b9ff;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(116, 185, 255, 0.3);
}

.word-grid-item.current-word {
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
  border-color: #00a085;
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .word-text {
    font-size: 2.2vw;
  }
  
  .vocab-btn {
    font-size: 0.8rem;
    padding: 0.4rem 0.8rem;
  }
  
  .phonetic-text {
    font-size: 1.1vw;
  }
  
  .meaning-content {
    font-size: 1vw;
  }
  
  .phrase-text {
    font-size: 1.1vw;
  }
  
  .sentence-text {
    font-size: 1vw;
  }
}

@media (max-width: 900px) {
  .main-content {
    flex-direction: column;
    height: auto;
    min-height: calc(100% - 10vh);
    padding: 2vh 2vw;
    max-width: 100vw;
  }
  
  .word-section {
    width: 100%;
    height: 50vh;
  }
  
  .content-section {
    width: 100%;
    height: 42vh;
  }
  
  .phrase-section {
    height: 18vh;
  }
  
  .sentence-section {
    height: 22vh;
  }
}

/* 键盘快捷键按钮 */
.shortcuts-btn {
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  border: 2px solid #667eea;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shortcuts-btn:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 键盘快捷键弹窗 */
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
  z-index: 2000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.shortcuts-modal h2 {
  margin: 0 0 20px 0;
  color: #333;
  text-align: center;
  font-size: 1.5rem;
}

.shortcuts-content {
  margin-bottom: 20px;
}

.shortcut-section {
  margin-bottom: 20px;
}

.shortcut-section h3 {
  margin: 0 0 10px 0;
  color: #667eea;
  font-size: 1.1rem;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 5px;
}

.shortcut-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.shortcut-item:last-child {
  border-bottom: none;
}

.key {
  background: #f8f9fa;
  color: #495057;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  font-weight: bold;
  border: 1px solid #dee2e6;
  min-width: 60px;
  text-align: center;
}

.description {
  color: #666;
  font-size: 0.95rem;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.modal-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modal-btn.primary {
  background: #667eea;
  color: white;
}

.modal-btn.primary:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}
</style>
