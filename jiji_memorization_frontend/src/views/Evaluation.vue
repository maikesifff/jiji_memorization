<template>
  <div class="evaluation-mode">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="unit-info">
        <h2>{{ unit?.name }} - 评测</h2>
        <span class="progress">{{ currentIndex + 1 }} / {{ wordList.length }}</span>
        <div class="keyboard-hints">
          <span class="mode-badge" :class="currentModeClass">{{ currentModeText }}</span>
        </div>
      </div>
      <button @click="showKeyboardShortcuts = true" class="shortcuts-btn" title="键盘快捷键">
        ?
      </button>
      <button @click="goBack" class="back-btn">
        返回主页 <span>X</span>
      </button>
      <button @click="prevQuestion" style="margin-left: 10px; padding: 5px 10px; background: #f0f0f0; border: 1px solid #ccc; border-radius: 4px; cursor: pointer;">
        上一题
      </button>
      <button @click="nextQuestionTest" style="margin-left: 5px; padding: 5px 10px; background: #f0f0f0; border: 1px solid #ccc; border-radius: 4px; cursor: pointer;">
        下一题
      </button>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content" v-if="currentWord">
      <!-- 左侧：答题区域 -->
      <div class="word-section">
        <div class="word-card">
          <!-- 看释义选单词模式 -->
          <div v-if="currentMode === 'meaning-to-word'" class="question-content">
            <div class="word-header">
              <h1 class="word-text">{{ currentQuestion.meaning }}</h1>
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
                <span 
                  class="phonetic-text" 
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ currentWord.americanPhonetic || '暂无' }}
                </span>
                <button 
                  @click="playAudio('american')" 
                  class="audio-btn"
                  :class="{ 'hidden-btn': !showResult }"
                  :disabled="!showResult || !currentWord.pronUs"
                >
                  {{ showResult ? '🔊' : '' }}
                </button>
              </div>
              <div class="phonetic-item">
                <span class="phonetic-label">英音</span>
                <span 
                  class="phonetic-text" 
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ currentWord.britishPhonetic || '暂无' }}
                </span>
                <button 
                  @click="playAudio('british')" 
                  class="audio-btn"
                  :class="{ 'hidden-btn': !showResult }"
                  :disabled="!showResult || !currentWord.pronUk"
                >
                  {{ showResult ? '🔊' : '' }}
                </button>
              </div>
            </div>
            
            <!-- 词性释义区域 - 替换为单词选择题 -->
            <div class="meanings-section">
              <h4 class="meanings-title">选择单词</h4>
              <div class="meanings-list">
                <div 
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="meaning-item"
                  :class="{ 
                    selected: selectedOption === index,
                    correct: showResult && option.isCorrect,
                    wrong: showResult && (selectedOption === index && !option.isCorrect) || (selectedOption === -1 && !option.isCorrect)
                  }"
                  @click="selectOption(index)"
                  :style="{ cursor: showResult ? 'default' : 'pointer' }"
                >
                  <div class="meaning-content">{{ option.word }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 看单词选释义模式 -->
          <div v-if="currentMode === 'word-to-meaning'" class="question-content">
            <div class="word-header">
              <h1 class="word-text">{{ currentWord.wordText }}</h1>
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
            
            <!-- 词性释义区域 - 替换为选择题 -->
            <div class="meanings-section">
              <h4 class="meanings-title">选择释义</h4>
              <div class="meanings-list">
                <div 
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="meaning-item"
                  :class="{ 
                    selected: selectedOption === index,
                    correct: showResult && option.isCorrect,
                    wrong: showResult && (selectedOption === index && !option.isCorrect) || (selectedOption === -1 && !option.isCorrect)
                  }"
                  @click="selectOption(index)"
                  :style="{ cursor: showResult ? 'default' : 'pointer' }"
                >
                  <div class="meaning-content">{{ option.meaning }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 听音默写模式 -->
          <div v-if="currentMode === 'audio-to-word'" class="question-content">
            <div class="word-header">
              <h1 
                class="word-text"
                :class="{ 'hidden-text': !showResult }"
              >
                {{ currentWord.wordText }}
              </h1>
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
                <span 
                  class="phonetic-text"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ currentWord.americanPhonetic || '暂无' }}
                </span>
                <button 
                  @click="playAudio('american')" 
                  class="audio-btn"
                  title="播放美音"
                >
                  🔊
                </button>
              </div>
              <div class="phonetic-item">
                <span class="phonetic-label">英音</span>
                <span 
                  class="phonetic-text"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ currentWord.britishPhonetic || '暂无' }}
                </span>
                <button 
                  @click="playAudio('british')" 
                  class="audio-btn"
                  title="播放英音"
                >
                  🔊
                </button>
              </div>
            </div>
            
            <!-- 输入区域 - 替换词性释义区域 -->
            <div class="meanings-section">
              <h4 class="meanings-title">默写单词</h4>
              <div class="input-section">
                <input
                  v-model="typedWord"
                  @keyup.enter="submitAnswer"
                  type="text"
                  class="word-input"
                  placeholder="请输入您听到的单词"
                  :disabled="showResult"
                  ref="wordInput"
                />
                <button @click="submitAnswer" class="submit-answer-btn" :disabled="!typedWord.trim() || showResult">
                  →
                </button>
              </div>
            </div>
          </div>

          <!-- 答题结果反馈 -->
          <div v-if="showResult" class="result-feedback">
            <div class="feedback-content">
              <div class="feedback-icon" :class="isCorrect ? 'correct' : 'wrong'">
                {{ isCorrect ? '✅' : '❌' }}
              </div>
              <div class="feedback-text">
                <h3>{{ isCorrect ? '回答正确！' : '回答错误' }}</h3>
                <p v-if="!isCorrect">{{ resultMessage }}</p>
              </div>
            </div>
            <button @click="nextQuestion" class="nav-btn next-btn">
              下一题 →
            </button>
          </div>
          
          <!-- 不会按钮 -->
          <div v-else class="result-feedback">
            <button @click="skipQuestion" class="nav-btn skip-btn">
              不会
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧：词组和例句区域 -->
      <div class="content-section">
        <!-- 词组区域 -->
        <div class="phrases-section">
          <h3>词组</h3>
          <div v-if="currentWord && currentWord.phrases && currentWord.phrases.length > 0" class="phrases-list">
            <div
              v-for="phrase in currentWord.phrases"
              :key="phrase.id"
              class="phrase-item"
            >
              <div class="phrase-content">
                <div 
                  class="phrase-text"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ phrase.phraseText }}
                </div>
                <div 
                  class="phrase-translation"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ phrase.translation }}
                </div>
              </div>
              <button
                @click="playPhraseAudio(phrase.phraseText)"
                class="phrase-audio-btn"
                :class="{ 'hidden-btn': !showResult }"
                :disabled="!showResult"
                title="播放词组发音"
              >
                {{ showResult ? '🔊' : '' }}
              </button>
            </div>
          </div>
          <div v-else class="no-content">
            <p>暂无词组</p>
          </div>
        </div>

        <!-- 例句区域 -->
        <div class="sentences-section">
          <h3>例句</h3>
          <div v-if="currentWord && currentWord.sentences && currentWord.sentences.length > 0" class="sentences-list">
            <div
              v-for="sentence in currentWord.sentences"
              :key="sentence.id"
              class="sentence-item"
            >
              <div class="sentence-content">
                <div 
                  class="sentence-text"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ sentence.sentenceText }}
                </div>
                <div 
                  class="sentence-translation"
                  :class="{ 'hidden-text': !showResult }"
                >
                  {{ sentence.translation }}
                </div>
              </div>
              <button
                @click="playSentenceAudio(sentence.sentenceText)"
                class="sentence-audio-btn"
                :class="{ 'hidden-btn': !showResult }"
                :disabled="!showResult"
                title="播放例句发音"
              >
                {{ showResult ? '🔊' : '' }}
              </button>
            </div>
          </div>
          <div v-else class="no-content">
            <p>暂无例句</p>
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
            <div class="shortcut-item">
              <span class="key">空格/回车</span>
              <span class="description">下一题</span>
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
          
          <div class="shortcut-section">
            <h3>选择模式</h3>
            <div class="shortcut-item">
              <span class="key">1-4</span>
              <span class="description">选择对应选项</span>
            </div>
            <div class="shortcut-item">
              <span class="key">空格/回车</span>
              <span class="description">标记为"不会"</span>
            </div>
          </div>
          
          <div class="shortcut-section">
            <h3>听写模式</h3>
            <div class="shortcut-item">
              <span class="key">回车</span>
              <span class="description">提交答案</span>
            </div>
            <div class="shortcut-item">
              <span class="key">空格</span>
              <span class="description">标记为"不会"</span>
            </div>
          </div>
          
        </div>
        <div class="modal-actions">
          <button @click="showKeyboardShortcuts = false" class="modal-btn primary">知道了</button>
        </div>
      </div>
    </div>

    <!-- 评测结果模态框 -->
    <div v-if="showResultModal" class="result-modal-overlay" @click="closeResultModal">
      <div class="result-modal" @click.stop>
        <div class="modal-header">
          <h2>评测完成</h2>
        </div>
        <div class="modal-content">
          <div class="score-summary">
            <div class="score-item">
              <span class="score-label">总题数：</span>
              <span class="score-value">{{ totalAnswered }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">正确数：</span>
              <span class="score-value correct">{{ correctCount }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">错误数：</span>
              <span class="score-value wrong">{{ wrongCount }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">准确率：</span>
              <span class="score-value">{{ accuracy }}%</span>
            </div>
          </div>
          <div class="modal-actions">
            <button @click="restartEvaluation" class="restart-btn">重新开始</button>
            <button @click="closeResultModal" class="close-btn">关闭</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { useRouter, useRoute } from 'vue-router'
import api from '@/utils/axios'

const authStore = useAuthStore()
const settingsStore = useSettingsStore()
const router = useRouter()
const route = useRoute()

// 评测状态
const currentIndex = ref(0)
const correctCount = ref(0)
const wrongCount = ref(0)
const totalAnswered = ref(0)
const showResult = ref(false)
const showResultModal = ref(false)
const showKeyboardShortcuts = ref(false)
const isCorrect = ref(false)
const resultMessage = ref('')
const hasAnswered = ref(false) // 是否已经答题
const selectedOption = ref(null)
const typedWord = ref('')
const isPlaying = ref(false)
const isCurrentWordCorrect = ref(false)
const hasUserInteracted = ref(false) // 跟踪用户是否已经与页面交互
const canSubmitAnswer = ref(false) // 控制是否可以提交答案（听音默写模式防抖）

// 当前题目和单词信息
const currentQuestion = ref({})
const currentWord = ref({})
const currentMode = ref('meaning-to-word')
const unit = ref(null)
const wordList = ref([])

// 评测模式配置
const evaluationModes = [
  { key: 'meaning-to-word', name: '看释义选单词', weight: 1 },
  { key: 'word-to-meaning', name: '看单词选释义', weight: 1 },
  { key: 'audio-to-word', name: '听音默写', weight: 1 }
]

// 计算属性
const currentModeText = computed(() => {
  const mode = evaluationModes.find(m => m.key === currentMode.value)
  return mode ? mode.name : '未知模式'
})

const currentModeClass = computed(() => {
  return `mode-${currentMode.value}`
})

const accuracy = computed(() => {
  return totalAnswered.value > 0 ? Math.round((correctCount.value / totalAnswered.value) * 100) : 0
})

// 生成随机评测模式
const getRandomMode = () => {
  const availableModes = settingsStore.evaluationSettings.freeChoiceMode 
    ? settingsStore.evaluationSettings.selectedModes 
    : evaluationModes.map(m => m.key)
  
  const randomIndex = Math.floor(Math.random() * availableModes.length)
  return availableModes[randomIndex]
}

// 加载单元信息
const loadUnitInfo = async () => {
  try {
    const unitId = route.params.unitId
    const response = await api.get(`/api/units/${unitId}`)
    unit.value = response.data
  } catch (error) {
    console.error('加载单元信息失败:', error)
  }
}

// 加载单词列表并排序
const loadWordList = async () => {
  try {
    const unitId = route.params.unitId
    const userId = authStore.currentUser.id
    
    // 获取单元中的所有单词 - 设置大页面大小获取所有数据
    const wordsResponse = await api.get(`/api/unit-words/unit/${unitId}`, {
      params: {
        page: 0,
        size: 1000  // 设置大页面大小，确保获取所有单词
      }
    })
    const allWords = wordsResponse.data.content || wordsResponse.data
    
    // 获取用户的答题记录
    const errorRecordsResponse = await api.get(`/api/error-records/user/${userId}/unit/${unitId}`)
    const errorRecords = errorRecordsResponse.data || []
    
    // 创建unitWordId到答题记录的映射
    const errorRecordMap = new Map()
    errorRecords.forEach(record => {
      errorRecordMap.set(record.unitWordId, record)
    })
    
    // 分离答对和答错的单词
    const correctWords = []
    const incorrectWords = []
    
    // 为每个单词检查是否在生词本中
    const wordsWithNotebookStatus = await Promise.all(
      allWords.map(async (word) => {
        try {
          const notebookResponse = await api.get(`/api/vocabulary-notebook/check`, {
            params: {
              userId: authStore.currentUser.id,
              wordId: word.wordId
            }
          })
          
          const isInNotebook = notebookResponse.data?.isInNotebook || false
          
          return {
            ...word,
            isInNotebook
          }
        } catch (err) {
          console.error(`Failed to check notebook status for word ${word.wordId}:`, err)
          return {
            ...word,
            isInNotebook: false
          }
        }
      })
    )
    
    wordsWithNotebookStatus.forEach(word => {
      const errorRecord = errorRecordMap.get(word.id) // word.id 是 unitWordId
      if (errorRecord && errorRecord.errorCount === 0) {
        // 答对的单词
        correctWords.push({ ...word, isCorrect: true })
      } else {
        // 答错的单词或未答过的单词
        incorrectWords.push({ ...word, isCorrect: false })
      }
    })
    
    // 打乱答错单词的顺序
    for (let i = incorrectWords.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1))
      ;[incorrectWords[i], incorrectWords[j]] = [incorrectWords[j], incorrectWords[i]]
    }
    
    // 合并列表：答对的在前，答错的在后
    wordList.value = [...correctWords, ...incorrectWords]
    
    // 找到第一个答错的单词作为起始位置
    const firstIncorrectIndex = correctWords.length
    currentIndex.value = firstIncorrectIndex
    
    console.log(`单词列表加载完成：${correctWords.length}个答对，${incorrectWords.length}个答错`)
  } catch (error) {
    console.error('加载单词列表失败:', error)
  }
}

// 加载当前单词的详细信息
const loadCurrentWordDetails = async () => {
  try {
    if (!currentWord.value || !currentWord.value.wordId) return
    
    const wordId = currentWord.value.wordId
    
    // 并行获取单词的详细信息
    const [detailsResponse, meaningsResponse, phrasesResponse, sentencesResponse] = await Promise.all([
      api.get(`/api/words/${wordId}`),
      api.get(`/api/meanings/word/${wordId}`),
      api.get(`/api/phrases/word/${wordId}`),
      api.get(`/api/sentences/word/${wordId}`)
    ])
    
    // 合并单词信息
    const wordDetails = detailsResponse.data.data
    currentWord.value = {
      ...currentWord.value,
      ...wordDetails,
      // 确保音频字段正确设置
      pronUs: wordDetails.pronUs || wordDetails.americanAudio,
      pronUk: wordDetails.pronUk || wordDetails.britishAudio,
      meanings: meaningsResponse.data.data || [],
      phrases: phrasesResponse.data || [],
      sentences: sentencesResponse.data || []
    }
  } catch (error) {
    console.error('加载单词详情失败:', error)
  }
}

// 动态调整字体大小以防止换行
const adjustFontSize = (element) => {
  if (!element) return
  
  const originalFontSize = parseFloat(getComputedStyle(element).fontSize)
  const containerWidth = element.parentElement.offsetWidth
  const text = element.textContent
  
  // 创建临时元素来测量文本宽度
  const tempElement = document.createElement('span')
  tempElement.style.visibility = 'hidden'
  tempElement.style.position = 'absolute'
  tempElement.style.whiteSpace = 'nowrap'
  tempElement.style.fontSize = originalFontSize + 'px'
  tempElement.style.fontWeight = getComputedStyle(element).fontWeight
  tempElement.style.fontFamily = getComputedStyle(element).fontFamily
  tempElement.textContent = text
  
  document.body.appendChild(tempElement)
  const textWidth = tempElement.offsetWidth
  document.body.removeChild(tempElement)
  
  // 如果文本宽度超过容器宽度，减小字体大小
  if (textWidth > containerWidth) {
    const newFontSize = (containerWidth / textWidth) * originalFontSize
    element.style.fontSize = Math.max(newFontSize, 12) + 'px' // 最小12px
  } else {
    element.style.fontSize = originalFontSize + 'px' // 恢复原始大小
  }
}

// 生成题目
const generateQuestion = async (word, mode) => {
  try {
    if (mode === 'meaning-to-word') {
      // 看释义选单词：使用已加载的单词列表作为干扰项
      const allWords = wordList.value.filter(w => w.wordId !== word.wordId)
      
      // 随机选择3个干扰项
      const shuffled = allWords.sort(() => 0.5 - Math.random())
      const distractors = shuffled.slice(0, 3)
      
      // 组合选项
      const options = [
        { word: word.wordText, isCorrect: true },
        ...distractors.map(w => ({ word: w.wordText, isCorrect: false }))
      ]
      
      // 打乱选项顺序
      options.sort(() => 0.5 - Math.random())
      
      // 获取单词的真实释义（随机选择一个）
      const meanings = word.meanings || []
      const meaningText = meanings.length > 0 
        ? (() => {
            const randomMeaning = meanings[Math.floor(Math.random() * meanings.length)]
            return randomMeaning.content
          })()
        : '暂无释义'
      
      return {
        mode,
        meaning: meaningText,
        options
      }
    } else if (mode === 'word-to-meaning') {
      // 看单词选释义：使用已加载的单词列表作为干扰项
      const otherWords = wordList.value.filter(w => w.wordId !== word.wordId)
      
      // 获取其他单词的释义作为干扰项
      const wrongMeanings = []
      for (const otherWord of otherWords.slice(0, 3)) {
        try {
          // 为每个其他单词获取释义信息
          const meaningsResponse = await api.get(`/api/meanings/word/${otherWord.wordId}`)
          const otherMeanings = meaningsResponse.data.data || []
          if (otherMeanings.length > 0) {
            // 随机选择一个释义
            const randomMeaning = otherMeanings[Math.floor(Math.random() * otherMeanings.length)]
            const meaningText = `[${randomMeaning.pos}]${randomMeaning.content}`
            wrongMeanings.push(meaningText)
          }
        } catch (error) {
          console.warn(`Failed to load meanings for word ${otherWord.wordId}:`, error)
        }
      }
      
      // 如果干扰项不够，补充一些通用错误释义
      while (wrongMeanings.length < 3) {
        wrongMeanings.push(`错误的释义${wrongMeanings.length + 1}`)
      }
      
      const correctMeaning = word.meanings && word.meanings.length > 0
        ? (() => {
            const randomMeaning = word.meanings[Math.floor(Math.random() * word.meanings.length)]
            return `[${randomMeaning.pos}]${randomMeaning.content}`
          })()
        : '暂无释义'
      
      const options = [
        { meaning: correctMeaning, isCorrect: true },
        ...wrongMeanings.slice(0, 3).map(m => ({ meaning: m, isCorrect: false }))
      ]
      
      // 打乱选项顺序
      options.sort(() => 0.5 - Math.random())
      
      return {
        mode,
        options
      }
    } else if (mode === 'audio-to-word') {
      // 听音默写：直接返回单词
      return {
        mode,
        correctWord: word.wordText
      }
    }
  } catch (error) {
    console.error('生成题目失败:', error)
    return null
  }
}

// 加载当前题目
const loadCurrentQuestion = async () => {
  if (currentIndex.value >= wordList.value.length) {
    // 所有单词都答完了
    showResultModal.value = true
    return
  }
  
  currentWord.value = wordList.value[currentIndex.value]
  await loadCurrentWordDetails()
  
  // 生成随机模式
  currentMode.value = getRandomMode()
  
  // 生成题目
  currentQuestion.value = await generateQuestion(currentWord.value, currentMode.value)
  
  // 重置状态
  resetQuestionState()
  
  // 动态调整字体大小以防止换行
  nextTick(() => {
    const wordTextElement = document.querySelector('.question-content .word-text')
    if (wordTextElement) {
      adjustFontSize(wordTextElement)
    }
    
    // 听音默写模式下自动聚焦输入框
    if (currentMode.value === 'audio-to-word') {
      const wordInput = document.querySelector('.word-input')
      if (wordInput) {
        wordInput.focus()
      }
    }
  })
  
  // 如果是听音默写模式，设置防抖延迟
  if (currentMode.value === 'audio-to-word') {
    canSubmitAnswer.value = false
    setTimeout(() => {
      canSubmitAnswer.value = true
    }, DEBOUNCE_DELAY) // 防抖后才能提交答案
    
    // 如果用户已经交互过，立即自动播放美音
    if (settingsStore.evaluationSettings.autoPlayAudio && hasUserInteracted.value) {
      playAudio('american')
    }
  } else {
    canSubmitAnswer.value = true // 其他模式立即可以操作
  }
}

// 选择选项
const selectOption = (index) => {
  if (showResult.value) return
  
  hasUserInteracted.value = true // 标记用户已交互
  selectedOption.value = index
  showResult.value = true
  hasAnswered.value = true
  
  const option = currentQuestion.value.options[index]
  isCorrect.value = option.isCorrect
  isCurrentWordCorrect.value = isCorrect.value
  
  if (isCorrect.value) {
    correctCount.value++
    resultMessage.value = '恭喜你答对了！'
  } else {
    wrongCount.value++
    resultMessage.value = '很遗憾，答错了。'
    
    // 如果设置了答错自动加入生词本
    if (settingsStore.evaluationSettings.autoAddToNotebook) {
      addToNotebook()
    }
  }
  
  // 记录答题结果到后端
  recordAnswer(isCorrect.value)
  
  totalAnswered.value++
  
  // 自动播放发音（如果设置了）
  if (settingsStore.evaluationSettings.autoPlayAudio && 
      (currentMode.value === 'word-to-meaning' || currentMode.value === 'meaning-to-word')) {
    playAudio('american')
  }
}

// 提交听音默写答案
const submitAnswer = () => {
  if (showResult.value || !canSubmitAnswer.value) return
  
  hasUserInteracted.value = true // 标记用户已交互
  showResult.value = true
  hasAnswered.value = true
  const userAnswer = typedWord.value ? typedWord.value.trim().toLowerCase() : ''
  const correctAnswer = currentWord.value.wordText.toLowerCase()
  
  isCorrect.value = userAnswer === correctAnswer
  isCurrentWordCorrect.value = isCorrect.value
  
  if (isCorrect.value) {
    correctCount.value++
    resultMessage.value = '恭喜你答对了！'
  } else {
    wrongCount.value++
    resultMessage.value = `正确答案是：${currentWord.value.wordText}`
    
    // 如果设置了答错自动加入生词本
    if (settingsStore.evaluationSettings.autoAddToNotebook) {
      addToNotebook()
    }
  }
  
  // 记录答题结果到后端
  recordAnswer(isCorrect.value)
  
  totalAnswered.value++
  
  // 自动播放发音（如果设置了）
  if (settingsStore.evaluationSettings.autoPlayAudio && currentMode.value === 'audio-to-word') {
    playAudio('american')
  }
}

// 播放音频
const playAudio = async (type) => {
  console.log(`playAudio called with type: ${type}`)
  try {
    isPlaying.value = true
    
    const word = currentWord.value
    if (!word) {
      console.log('No current word')
      return
    }
    
    const audioData = type === 'american' ? word.pronUs : word.pronUk
    console.log(`Audio data for ${type}:`, audioData ? 'exists' : 'missing')
    if (!audioData) {
      console.warn(`No audio data for ${type} pronunciation of word: ${word.wordText}`)
      isPlaying.value = false
      return
    }
    
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
    console.log('About to play audio, src:', audio.src.substring(0, 100) + '...')
    await audio.play()
    
    console.log(`Playing ${type} audio for word: ${word.wordText}`)
    isPlaying.value = false
  } catch (error) {
    console.error('播放音频失败:', error)
    console.error('Error details:', error.name, error.message)
    
    // 如果是权限错误，提示用户需要先点击页面
    if (error.name === 'NotAllowedError') {
      console.warn('需要用户先与页面交互才能播放音频')
      isPlaying.value = false
      return
    }
    
    // 如果播放失败，尝试其他格式
    const word = currentWord.value
    if (word) {
      const audioData = type === 'american' ? word.pronUs : word.pronUk
      if (audioData && !audioData.startsWith('data:audio/')) {
        try {
          const audio = new Audio()
          audio.src = `data:audio/wav;base64,${audioData}`
          await audio.play()
          console.log(`Playing ${type} audio with wav format for word: ${word.wordText}`)
        } catch (err) {
          console.error('Audio play failed with wav format:', err)
        }
      }
    }
    isPlaying.value = false
  }
}

const playQuestionAudio = () => {
  hasUserInteracted.value = true // 标记用户已交互
  playAudio('american')
}

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
      
      // 更新wordList数组中的对应项
      const wordIndex = wordList.value.findIndex(w => w.wordId === word.wordId)
      if (wordIndex !== -1) {
        wordList.value[wordIndex].isInNotebook = false
      }
      
      console.log(`Word "${word.wordText}" removed from vocabulary notebook`)
    } else {
      // 添加到生词本
      await api.post('/api/vocabulary-notebook/add', {
        userId: authStore.currentUser.id,
        wordId: word.wordId
      })
      
      // 更新本地状态
      word.isInNotebook = true
      
      // 更新wordList数组中的对应项
      const wordIndex = wordList.value.findIndex(w => w.wordId === word.wordId)
      if (wordIndex !== -1) {
        wordList.value[wordIndex].isInNotebook = true
      }
      
      console.log(`Word "${word.wordText}" added to vocabulary notebook`)
    }
  } catch (error) {
    console.error('Failed to toggle vocabulary notebook:', error)
    
    // 检查是否是重复操作错误
    const errorMessage = error.response?.data?.message || error.message || '未知错误'
    if (errorMessage.includes('已在生词本中') || errorMessage.includes('already in notebook') || 
        errorMessage.includes('不在生词本中') || errorMessage.includes('not in notebook')) {
      // 如果是重复操作，直接更新本地状态，不显示错误提示
      word.isInNotebook = !isCurrentlyInNotebook
      
      // 更新wordList数组中的对应项
      const wordIndex = wordList.value.findIndex(w => w.wordId === word.wordId)
      if (wordIndex !== -1) {
        wordList.value[wordIndex].isInNotebook = !isCurrentlyInNotebook
      }
      
      console.log(`Word "${word.wordText}" status updated locally (duplicate operation)`)
    } else {
      // 其他错误才显示提示
      alert(`操作失败: ${errorMessage}`)
    }
  }
}

// 记录答题结果
const recordAnswer = async (isCorrect) => {
  try {
    const currentWordData = wordList.value[currentIndex.value]
    if (!currentWordData) return
    
    await api.post('/api/error-records/record-by-word', {
      userId: authStore.currentUser.id,
      wordId: currentWordData.wordId,
      unitId: unit.value.id,
      isCorrect: isCorrect
    })
    
    console.log(`答题记录已保存: wordId=${currentWordData.wordId}, isCorrect=${isCorrect}`)
  } catch (error) {
    console.error('保存答题记录失败:', error)
  }
}

// 跳过题目
const skipQuestion = () => {
  if (showResult.value) return
  
  hasUserInteracted.value = true // 标记用户已交互
  
  // 标记为答错，将当前单词移到列表最后
  const currentWordItem = wordList.value.splice(currentIndex.value, 1)[0]
  wordList.value.push(currentWordItem)
  
  // 设置跳过状态，让所有选项都显示为错误
  selectedOption.value = -1 // 表示没有选择任何选项
  showResult.value = true
  hasAnswered.value = true
  isCorrect.value = false
  resultMessage.value = '已跳过此题'
  
  // 记录答题结果到后端（跳过算答错）
  recordAnswer(false)
  
  // 如果设置了答错自动加入生词本
  if (settingsStore.evaluationSettings.autoAddToNotebook) {
    addToNotebook()
  }
  
  // 自动播放发音（如果设置了）
  if (settingsStore.evaluationSettings.autoPlayAudio && 
      (currentMode.value === 'word-to-meaning' || currentMode.value === 'meaning-to-word')) {
    playAudio('american')
  }
  
  totalAnswered.value++
}

// 下一题
const nextQuestion = () => {
  if (!showResult.value) return
  
  if (isCorrect.value) {
    // 答对了，直接下一题
    currentIndex.value++
  } else {
    // 答错了，将当前单词移到列表最后
    const currentWordItem = wordList.value.splice(currentIndex.value, 1)[0]
    wordList.value.push(currentWordItem)
  }
  
  loadCurrentQuestion()
}

// 上一题（临时测试用 - 不受答题状态控制）
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    loadCurrentQuestion()
  }
}

// 下一题（临时测试用 - 不受答题状态控制）
const nextQuestionTest = () => {
  if (currentIndex.value < wordList.value.length - 1) {
    currentIndex.value++
    loadCurrentQuestion()
  }
}

// 重置题目状态
const resetQuestionState = () => {
  showResult.value = false
  hasAnswered.value = false
  selectedOption.value = null
  typedWord.value = ''
  isCurrentWordCorrect.value = false
  resultMessage.value = ''
  canSubmitAnswer.value = true // 重置提交状态
}

// 添加到生词本
const addToNotebook = async () => {
  try {
    await api.post('/api/vocabulary-notebook/add', {
      userId: authStore.currentUser.id,
      wordId: currentWord.value.wordId
    })
    
    // 更新本地状态
    currentWord.value.isInNotebook = true
    
    // 更新wordList数组中的对应项
    const wordIndex = wordList.value.findIndex(w => w.wordId === currentWord.value.wordId)
    if (wordIndex !== -1) {
      wordList.value[wordIndex].isInNotebook = true
    }
    
    console.log('已自动添加到生词本')
  } catch (error) {
    // 忽略"该单词已在生词本中"的错误
    if (error.response?.data?.message !== '该单词已在生词本中') {
      console.error('添加到生词本失败:', error)
    }
  }
}

// 返回主页
const goBack = () => {
  router.push('/')
}

// 重新开始评测
const restartEvaluation = () => {
  currentIndex.value = 0
  correctCount.value = 0
  wrongCount.value = 0
  totalAnswered.value = 0
  showResultModal.value = false
  resetQuestionState()
  loadWordList().then(() => {
    loadCurrentQuestion()
  })
}

// 关闭结果模态框
const closeResultModal = () => {
  showResultModal.value = false
  goBack()
}

// 防抖配置
const DEBOUNCE_DELAY = 500 // 防抖延迟时间（毫秒）

// 防抖标志
let isProcessingKey = false

// 键盘事件处理
const handleKeydown = (event) => {
  
  // 防止重复触发
  if (isProcessingKey) {
    return
  }
  
  // 检查是否在输入框内
  if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
    // 听音默写模式：输入框内只允许回车键
    if (currentMode.value === 'audio-to-word' && event.key === 'Enter') {
      // 回车键继续处理
    } else {
      return
    }
  }
  
  // ESC键返回主页
  if (event.key === 'Escape') {
    event.preventDefault()
    router.push('/')
    return
  }
  
  // 如果正在显示结果，处理下一题逻辑
  if (showResult.value) {
    if (event.key === ' ' || event.key === 'Enter') {
      event.preventDefault()
      isProcessingKey = true
      setTimeout(() => {
        isProcessingKey = false
      }, DEBOUNCE_DELAY)
      
      nextQuestion()
      return
    }
    // 其他按键（包括发音快捷键）继续处理
  }
  
  // 听音默写模式：回车提交答案，空格标记为不会
  if (currentMode.value === 'audio-to-word') {
    if (event.key === 'Enter') {
      event.preventDefault()
      if (!canSubmitAnswer.value) return // 防抖期间不允许提交
      
      isProcessingKey = true
      setTimeout(() => {
        isProcessingKey = false
      }, DEBOUNCE_DELAY)
      
      // 如果输入框为空，直接标记为不会（错误）
      if (!typedWord.value || !typedWord.value.trim()) {
        typedWord.value = ''
        submitAnswer()
      } else {
        submitAnswer()
      }
      return
    } else if (event.key === ' ') {
      event.preventDefault()
      if (!canSubmitAnswer.value) return // 防抖期间不允许提交
      
      isProcessingKey = true
      setTimeout(() => {
        isProcessingKey = false
      }, DEBOUNCE_DELAY)
      
      // 空格键走回车流程：如果输入框为空，直接标记为不会（错误）
      if (!typedWord.value || !typedWord.value.trim()) {
        typedWord.value = ''
        submitAnswer()
      } else {
        submitAnswer()
      }
      return
    }
    // 其他按键（包括发音快捷键）继续处理
  }
  
  // 选择题模式：1-4选择选项，空格/回车标记为不会
  if (currentMode.value === 'word-to-meaning' || currentMode.value === 'meaning-to-word') {
    if (event.key >= '1' && event.key <= '4') {
      event.preventDefault()
      isProcessingKey = true
      setTimeout(() => {
        isProcessingKey = false
      }, DEBOUNCE_DELAY)
      
      const optionIndex = parseInt(event.key) - 1
      if (currentQuestion.value && currentQuestion.value.options && currentQuestion.value.options[optionIndex]) {
        selectOption(optionIndex)
      }
    } else if (event.key === ' ' || event.key === 'Enter') {
      event.preventDefault()
      isProcessingKey = true
      setTimeout(() => {
        isProcessingKey = false
      }, DEBOUNCE_DELAY)
      
      // 调用跳过函数，与点击"不会"按钮效果一致
      skipQuestion()
    }
  }
  
  // 发音快捷键（与按钮状态完全一致）
  if (currentWord.value) {
    switch (event.key.toLowerCase()) {
      case 'q':
        event.preventDefault()
        // 美音按钮：看单词选释义和听音默写模式答题前后都可用，看释义选单词模式只有答题后可用
        if (currentWord.value.pronUs && (currentMode.value === 'word-to-meaning' || currentMode.value === 'audio-to-word' || hasAnswered.value)) {
          // 模拟点击美音按钮
          const americanBtn = document.querySelector('.audio-btn[title="播放美音"], .audio-btn:first-of-type')
          if (americanBtn && !americanBtn.disabled) {
            americanBtn.click()
          }
        }
        break
      case 'e':
        event.preventDefault()
        // 英音按钮：看单词选释义和听音默写模式答题前后都可用，看释义选单词模式只有答题后可用
        if (currentWord.value.pronUk && (currentMode.value === 'word-to-meaning' || currentMode.value === 'audio-to-word' || hasAnswered.value)) {
          // 根据当前模式找到对应的英音按钮
          let britishBtn = null
          
          if (currentMode.value === 'word-to-meaning') {
            // 看单词选释义模式：第二个audio-btn
            const audioBtns = document.querySelectorAll('.audio-btn')
            britishBtn = audioBtns[1]
          } else if (currentMode.value === 'meaning-to-word') {
            // 看释义选单词模式：第二个audio-btn
            const audioBtns = document.querySelectorAll('.audio-btn')
            britishBtn = audioBtns[1]
          } else if (currentMode.value === 'audio-to-word') {
            // 听音默写模式：有title="播放英音"的按钮
            britishBtn = document.querySelector('.audio-btn[title="播放英音"]')
          }
          
          if (britishBtn && !britishBtn.disabled) {
            britishBtn.click()
          }
        }
        break
      case 'u':
        event.preventDefault()
        // 词组发音按钮：答题后可用
        if (hasAnswered.value && currentWord.value.phrases && currentWord.value.phrases[0]) {
          playPhraseAudio(currentWord.value.phrases[0].phraseText)
        }
        break
      case 'i':
        event.preventDefault()
        if (hasAnswered.value && currentWord.value.phrases && currentWord.value.phrases[1]) {
          playPhraseAudio(currentWord.value.phrases[1].phraseText)
        }
        break
      case 'o':
        event.preventDefault()
        if (hasAnswered.value && currentWord.value.phrases && currentWord.value.phrases[2]) {
          playPhraseAudio(currentWord.value.phrases[2].phraseText)
        }
        break
      case 'j':
        event.preventDefault()
        // 例句发音按钮：答题后可用
        if (hasAnswered.value && currentWord.value.sentences && currentWord.value.sentences[0]) {
          playSentenceAudio(currentWord.value.sentences[0].sentenceText)
        }
        break
      case 'k':
        event.preventDefault()
        if (hasAnswered.value && currentWord.value.sentences && currentWord.value.sentences[1]) {
          playSentenceAudio(currentWord.value.sentences[1].sentenceText)
        }
        break
      case 'l':
        event.preventDefault()
        if (hasAnswered.value && currentWord.value.sentences && currentWord.value.sentences[2]) {
          playSentenceAudio(currentWord.value.sentences[2].sentenceText)
        }
        break
      case 'v':
        event.preventDefault()
        // 生词本按钮：始终可用
        toggleVocabularyNotebook()
        break
    }
  }
}

// 组件挂载
onMounted(async () => {
  // 平滑滚动到页面顶部
  window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
  
  // 加载数据
  await loadUnitInfo()
  await loadWordList()
  await loadCurrentQuestion()
  
  // 添加键盘事件监听
  document.addEventListener('keydown', handleKeydown)
})

// 组件卸载
onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.evaluation-mode {
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
  flex: 1;
  text-align: center;
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

.mode-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.8rem;
}

.mode-meaning-to-word {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.mode-word-to-meaning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
}

.mode-audio-to-word {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
}

.nav-controls {
  display: flex;
  gap: 0.5rem;
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

.nav-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(116, 185, 255, 0.4);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.skip-btn {
  background: linear-gradient(45deg, #e74c3c, #c0392b);
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
}

.skip-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
}

/* 主要内容区域 - 与BrowseMode保持一致 */
.main-content {
  height: calc(100% - 10vh);
  display: flex;
  padding: 2vh 4vw;
  gap: 2vw;
  max-width: 90vw;
  margin: 0 auto;
}

/* 词性释义样式 - 与BrowseMode保持一致 */
.meaning-item {
  display: flex;
  align-items: center;
  justify-content: center;
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

.meaning-item.selected {
  border-left-color: #4ecdc4;
  background: rgba(78, 205, 196, 0.1);
}

.meaning-item.correct {
  border-left-color: #00b894;
  background: rgba(0, 184, 148, 0.1);
}

.meaning-item.wrong {
  border-left-color: #e17055;
  background: rgba(225, 112, 85, 0.1);
}

.meaning-left {
  min-width: 8vw;
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
  text-align: center;
  width: 100%;
}

.no-meanings {
  text-align: center;
  color: #7f8c8d;
  font-size: 1.2vw;
  margin-top: 5vh;
}

/* 选择题区域样式 */
.quiz-section {
  margin-top: 2vh;
}

.quiz-title {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 1vh 0;
  text-align: left;
  border-bottom: 2px solid #4ecdc4;
  padding-bottom: 0.5vh;
}

/* 左侧单词区域 - 固定宽度 */
.word-section {
  width: 30vw;
  min-width: 30vw;
  max-width: 30vw;
  height: 100%;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
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

.question-content {
  flex: 1;
  display: flex;
  flex-direction: column;
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
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  height: 1.2em;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 看单词选释义和看释义选单词模式的动态字体大小 */
.question-content .word-text {
  font-size: 2.5vw;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  min-width: 0;
  flex-shrink: 1;
  height: 1.2em;
  display: flex;
  align-items: center;
  justify-content: center;
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
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 隐藏样式 */
.hidden-text {
  color: transparent !important;
  text-shadow: none !important;
}

.hidden-btn {
  background: transparent !important;
  border: 1px solid transparent !important;
  box-shadow: none !important;
  cursor: default !important;
}

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


.question-title h2 {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.3rem;
  text-align: center;
}

.question-text {
  margin-bottom: 2rem;
  text-align: center;
}

.question-text p {
  font-size: 1.1rem;
  color: #555;
  margin: 0;
}

.word-display {
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 1rem 0;
}


.options-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.option-btn {
  padding: 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  background: white;
  color: #333;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.option-btn:hover:not(:disabled) {
  border-color: #667eea;
  background: #f8f9ff;
}

.option-btn.selected {
  border-color: #667eea;
  background: #f8f9ff;
}

.option-btn.correct {
  border-color: #4caf50;
  background: #e8f5e8;
  color: #2e7d32;
}

.option-btn.wrong {
  border-color: #f44336;
  background: #ffebee;
  color: #c62828;
}

.audio-section {
  text-align: center;
  margin-bottom: 2rem;
}

.audio-player {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 8vh;
  gap: 3vw;
}

.audio-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5vh;
}

.audio-label {
  font-size: 0.9vw;
  color: #666;
  font-weight: 500;
}

.play-audio-btn {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-audio-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 184, 148, 0.3);
}

.audio-controls {
  margin-top: 1rem;
}

.audio-control-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.input-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

.word-input {
  width: 100%;
  max-width: 400px;
  padding: 1.5rem 2rem;
  border: 2px solid #e1e5e9;
  border-radius: 16px;
  font-size: 1.8rem;
  font-weight: 500;
  outline: none;
  transition: all 0.3s ease;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.word-input:focus {
  border-color: #667eea;
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.submit-btn {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.result-display {
  text-align: center;
  padding: 1rem;
  background: #f8f9ff;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.correct-answer {
  font-size: 1.1rem;
  color: #333;
}

.result-feedback {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
}

.result-feedback:has(.skip-btn) {
  justify-content: center;
}

.feedback-content {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.feedback-icon {
  font-size: 3rem;
}

.feedback-icon.correct {
  color: #4caf50;
}

.feedback-icon.wrong {
  color: #f44336;
}

.feedback-text h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.feedback-text p {
  margin: 0;
  color: #666;
}

/* 右侧内容区域 - 与BrowseMode保持一致 */
.content-section {
  width: 56vw;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 2vh;
}

.phrases-section {
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

.sentences-section {
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

.phrases-section h3 {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 2vh 0;
  text-align: left;
  border-bottom: 2px solid #a29bfe;
  padding-bottom: 1vh;
}

.sentences-section h3 {
  font-size: 1.3vw;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 2vh 0;
  text-align: left;
  border-bottom: 2px solid #00b894;
  padding-bottom: 1vh;
}

.phrases-list {
  flex: 1;
  overflow: hidden;
  padding-right: 1vw;
  display: flex;
  flex-direction: column;
  gap: 0.8vh;
  justify-content: flex-start;
}

.sentences-list {
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.phrase-audio-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(162, 155, 254, 0.4);
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.sentence-audio-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

/* 听音默写播放按钮 - 比例句按钮大一点 */
.audio-to-word-btn {
  background: linear-gradient(45deg, #00b894, #00a085);
  color: white;
  border: none;
  width: 4vw;
  height: 4vw;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.5vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-to-word-btn:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 184, 148, 0.4);
}

.audio-to-word-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  box-shadow: none;
}

/* 听音默写提交按钮 - 圆形，只显示右箭头 */
.submit-answer-btn {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  width: 4vw;
  height: 4vw;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.8vw;
  font-weight: 900;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  text-align: center;
}

.submit-answer-btn:hover:not(:disabled) {
  transform: scale(1.15);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.submit-answer-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.phrase-meaning,
.sentence-meaning {
  color: #666;
  font-size: 0.9rem;
}

.no-content {
  text-align: center;
  color: #7f8c8d;
  font-size: 1.2vw;
  margin-top: 5vh;
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
  font-size: 24px;
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
  font-size: 18px;
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
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 4px 8px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  min-width: 80px;
  text-align: center;
}

.description {
  color: #666;
  font-size: 14px;
  flex: 1;
  margin-left: 15px;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.modal-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.modal-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.modal-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 结果模态框 */
.result-modal-overlay {
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

.result-modal {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header h2 {
  margin: 0 0 1.5rem 0;
  text-align: center;
  color: #333;
}

.score-summary {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e1e5e9;
}

.score-label {
  font-weight: 600;
  color: #666;
}

.score-value {
  font-weight: 700;
  color: #333;
}

.score-value.correct {
  color: #4caf50;
}

.score-value.wrong {
  color: #f44336;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.restart-btn,
.close-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.restart-btn {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
}

.close-btn {
  background: #e1e5e9;
  color: #666;
}

.restart-btn:hover,
.close-btn:hover {
  transform: translateY(-2px);
}

/* 响应式设计 - 与BrowseMode保持一致 */
@media (max-width: 1200px) {
  .phrase-text {
    font-size: 1.1vw;
  }
  
  .sentence-text {
    font-size: 1vw;
  }
  
  .phrase-translation {
    font-size: 0.9vw;
  }
  
  .sentence-translation {
    font-size: 0.8vw;
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
  
  .phrases-section {
    height: 18vh;
  }
  
  .sentences-section {
    height: 22vh;
  }
  
  .top-nav {
    flex-direction: column;
    gap: 1rem;
    height: auto;
    padding: 1vh 2vw;
  }
  
  .options-grid {
    grid-template-columns: 1fr;
  }
  
  .input-section {
    flex-direction: column;
  }
}
</style>
