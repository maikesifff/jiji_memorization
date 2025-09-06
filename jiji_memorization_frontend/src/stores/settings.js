import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  // 评测设置
  const evaluationSettings = ref({
    // 自由选择题型
    freeChoiceMode: false,
    selectedModes: ['meaning-to-word', 'word-to-meaning', 'audio-to-word'],
    
    // 自动发音
    autoPlayAudio: true,
    
    // 答错自动加入生词本
    autoAddToNotebook: true,
    
    // 时间限制（秒，0表示无限制）
    timeLimit: 0,
    
    // 是否显示提示
    showHints: true,
    
    // 是否显示音标
    showPhonetics: true,
    
    // 是否显示例句
    showExamples: true
  })

  // 界面设置
  const uiSettings = ref({
    // 主题
    theme: 'light', // light, dark
    
    // 字体大小
    fontSize: 'medium', // small, medium, large
    
    // 自动保存进度
    autoSave: true
  })

  // 学习设置（保留但不在UI中显示）
  const learningSettings = ref({
    // 每日学习目标
    dailyGoal: 20,
    
    // 复习提醒
    reviewReminder: true,
    reviewTime: '20:00',
    
    // 学习模式
    learningMode: 'balanced', // balanced, intensive, relaxed
    
    // 重复次数
    repetitionCount: 3
  })

  // 从本地存储加载设置
  const loadSettings = () => {
    try {
      const savedEvaluation = localStorage.getItem('evaluationSettings')
      if (savedEvaluation) {
        evaluationSettings.value = { ...evaluationSettings.value, ...JSON.parse(savedEvaluation) }
      }

      const savedUI = localStorage.getItem('uiSettings')
      if (savedUI) {
        uiSettings.value = { ...uiSettings.value, ...JSON.parse(savedUI) }
      }

      const savedLearning = localStorage.getItem('learningSettings')
      if (savedLearning) {
        learningSettings.value = { ...learningSettings.value, ...JSON.parse(savedLearning) }
      }
    } catch (error) {
      console.error('加载设置失败:', error)
    }
  }

  // 保存设置到本地存储
  const saveSettings = () => {
    try {
      localStorage.setItem('evaluationSettings', JSON.stringify(evaluationSettings.value))
      localStorage.setItem('uiSettings', JSON.stringify(uiSettings.value))
      localStorage.setItem('learningSettings', JSON.stringify(learningSettings.value))
    } catch (error) {
      console.error('保存设置失败:', error)
    }
  }

  // 更新评测设置
  const updateEvaluationSettings = (newSettings) => {
    evaluationSettings.value = { ...evaluationSettings.value, ...newSettings }
  }

  // 更新界面设置
  const updateUISettings = (newSettings) => {
    uiSettings.value = { ...uiSettings.value, ...newSettings }
  }

  // 更新学习设置
  const updateLearningSettings = (newSettings) => {
    learningSettings.value = { ...learningSettings.value, ...newSettings }
  }

  // 重置所有设置
  const resetAllSettings = () => {
    evaluationSettings.value = {
      freeChoiceMode: false,
      selectedModes: ['meaning-to-word', 'word-to-meaning', 'audio-to-word'],
      autoPlayAudio: true,
      autoAddToNotebook: true,
      timeLimit: 0,
      showHints: true,
      showPhonetics: true,
      showExamples: true
    }

    uiSettings.value = {
      theme: 'light',
      fontSize: 'medium',
      autoSave: true
    }

    learningSettings.value = {
      dailyGoal: 20,
      reviewReminder: true,
      reviewTime: '20:00',
      learningMode: 'balanced',
      repetitionCount: 3
    }
  }

  // 获取可用的评测模式
  const getAvailableModes = () => {
    return [
      { key: 'meaning-to-word', name: '看释义选单词', description: '根据释义选择正确的单词' },
      { key: 'word-to-meaning', name: '看单词选释义', description: '根据单词选择正确的释义' },
      { key: 'audio-to-word', name: '听音默写', description: '根据发音默写单词' }
    ]
  }


  // 监听设置变化，自动保存
  watch(evaluationSettings, saveSettings, { deep: true })
  watch(uiSettings, saveSettings, { deep: true })
  watch(learningSettings, saveSettings, { deep: true })

  // 初始化时加载设置
  loadSettings()

  return {
    // 状态
    evaluationSettings,
    uiSettings,
    learningSettings,

    // 方法
    loadSettings,
    saveSettings,
    updateEvaluationSettings,
    updateUISettings,
    updateLearningSettings,
    resetAllSettings,
    getAvailableModes
  }
})
