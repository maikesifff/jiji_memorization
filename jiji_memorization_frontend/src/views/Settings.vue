<template>
  <div class="settings-container">
    <!-- 设置内容 -->
    <div class="settings-content">
      <!-- 评测设置 -->
      <div class="settings-section">
        <h2>评测设置</h2>
        
        <!-- 自由选择题型 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>自由选择题型</h3>
            <p>允许用户自定义评测题型</p>
          </div>
          <div class="setting-control">
            <label class="switch">
              <input 
                type="checkbox" 
                v-model="evaluationSettings.freeChoiceMode"
                @change="updateSettings"
              >
              <span class="slider"></span>
            </label>
          </div>
        </div>

        <!-- 题型选择 -->
        <div v-if="evaluationSettings.freeChoiceMode" class="setting-item">
          <div class="setting-info">
            <h3>选择题型</h3>
            <p>选择您想要练习的题型</p>
          </div>
          <div class="setting-control">
            <div class="mode-selection">
              <label 
                v-for="mode in availableModes" 
                :key="mode.key"
                class="mode-option"
              >
                <input 
                  type="checkbox" 
                  :value="mode.key"
                  v-model="evaluationSettings.selectedModes"
                  @change="updateSettings"
                >
                <div class="mode-info">
                  <span class="mode-name">{{ mode.name }}</span>
                  <span class="mode-desc">{{ mode.description }}</span>
                </div>
              </label>
            </div>
          </div>
        </div>


        <!-- 自动发音 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>自动发音</h3>
            <p>答题后自动播放单词发音</p>
          </div>
          <div class="setting-control">
            <label class="switch">
              <input 
                type="checkbox" 
                v-model="evaluationSettings.autoPlayAudio"
                @change="updateSettings"
              >
              <span class="slider"></span>
            </label>
          </div>
        </div>

        <!-- 答错自动加入生词本 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>答错自动加入生词本</h3>
            <p>答错的单词自动添加到生词本</p>
          </div>
          <div class="setting-control">
            <label class="switch">
              <input 
                type="checkbox" 
                v-model="evaluationSettings.autoAddToNotebook"
                @change="updateSettings"
              >
              <span class="slider"></span>
            </label>
          </div>
        </div>

        <!-- 时间限制 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>时间限制</h3>
            <p>每道题的答题时间限制（0表示无限制）</p>
          </div>
          <div class="setting-control">
            <select v-model="evaluationSettings.timeLimit" @change="updateSettings">
              <option value="0">无限制</option>
              <option value="10">10秒</option>
              <option value="15">15秒</option>
              <option value="20">20秒</option>
              <option value="30">30秒</option>
              <option value="60">60秒</option>
            </select>
          </div>
        </div>

        <!-- 显示提示 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>显示提示</h3>
            <p>在答题时显示相关提示</p>
          </div>
          <div class="setting-control">
            <label class="switch">
              <input 
                type="checkbox" 
                v-model="evaluationSettings.showHints"
                @change="updateSettings"
              >
              <span class="slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 界面设置 -->
      <div class="settings-section">
        <h2>界面设置</h2>
        
        <!-- 主题 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>主题</h3>
            <p>选择应用的主题样式</p>
          </div>
          <div class="setting-control">
            <div class="theme-selection">
              <label class="theme-option">
                <input 
                  type="radio" 
                  value="light"
                  v-model="uiSettings.theme"
                  @change="updateSettings"
                >
                <div class="theme-preview light">
                  <span>浅色主题</span>
                </div>
              </label>
              <label class="theme-option">
                <input 
                  type="radio" 
                  value="dark"
                  v-model="uiSettings.theme"
                  @change="updateSettings"
                >
                <div class="theme-preview dark">
                  <span>深色主题</span>
                </div>
              </label>
            </div>
          </div>
        </div>

        <!-- 字体大小 -->
        <div class="setting-item">
          <div class="setting-info">
            <h3>字体大小</h3>
            <p>调整界面字体大小</p>
          </div>
          <div class="setting-control">
            <select v-model="uiSettings.fontSize" @change="updateSettings">
              <option value="small">小</option>
              <option value="medium">中</option>
              <option value="large">大</option>
            </select>
          </div>
        </div>

      </div>


      <!-- 操作按钮 -->
      <div class="settings-actions">
        <button @click="resetSettings" class="reset-btn">重置设置</button>
        <button @click="exportSettings" class="export-btn">导出设置</button>
        <button @click="importSettings" class="import-btn">导入设置</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useSettingsStore } from '@/stores/settings'

const settingsStore = useSettingsStore()

// 响应式数据
const evaluationSettings = ref({})
const uiSettings = ref({})
const learningSettings = ref({})

// 可用选项
const availableModes = ref([])

// 更新设置
const updateSettings = () => {
  settingsStore.updateEvaluationSettings(evaluationSettings.value)
  settingsStore.updateUISettings(uiSettings.value)
  settingsStore.updateLearningSettings(learningSettings.value)
}

// 重置设置
const resetSettings = () => {
  if (confirm('确定要重置所有设置吗？此操作不可撤销。')) {
    settingsStore.resetAllSettings()
    loadSettings()
  }
}

// 导出设置
const exportSettings = () => {
  const settings = {
    evaluation: evaluationSettings.value,
    ui: uiSettings.value,
    learning: learningSettings.value
  }
  
  const dataStr = JSON.stringify(settings, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  
  const link = document.createElement('a')
  link.href = URL.createObjectURL(dataBlob)
  link.download = 'settings.json'
  link.click()
}

// 导入设置
const importSettings = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  
  input.onchange = (event) => {
    const file = event.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const settings = JSON.parse(e.target.result)
          
          if (settings.evaluation) {
            settingsStore.updateEvaluationSettings(settings.evaluation)
          }
          if (settings.ui) {
            settingsStore.updateUISettings(settings.ui)
          }
          if (settings.learning) {
            settingsStore.updateLearningSettings(settings.learning)
          }
          
          loadSettings()
          alert('设置导入成功！')
        } catch (error) {
          alert('导入失败：文件格式不正确')
        }
      }
      reader.readAsText(file)
    }
  }
  
  input.click()
}

// 加载设置
const loadSettings = () => {
  evaluationSettings.value = { ...settingsStore.evaluationSettings }
  uiSettings.value = { ...settingsStore.uiSettings }
  learningSettings.value = { ...settingsStore.learningSettings }
}

// 组件挂载
onMounted(() => {
  // 平滑滚动到页面顶部
  window.scrollTo({ top: 0, left: 0, behavior: 'smooth' })
  
  // 加载设置
  loadSettings()
  
  // 加载选项
  availableModes.value = settingsStore.getAvailableModes()
})
</script>

<style scoped>
.settings-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.settings-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.settings-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.settings-section h2 {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.5rem;
  border-bottom: 2px solid #e1e5e9;
  padding-bottom: 0.5rem;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
  margin-right: 2rem;
}

.setting-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.setting-info p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.setting-control {
  flex-shrink: 0;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #667eea;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

/* 选择框样式 */
select {
  padding: 0.5rem 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 0.9rem;
  cursor: pointer;
}

select:focus {
  outline: none;
  border-color: #667eea;
}

/* 数字输入框 */
.number-input {
  width: 80px;
  padding: 0.5rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  text-align: center;
  font-size: 0.9rem;
}

.input-unit {
  margin-left: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

/* 时间输入框 */
.time-input {
  padding: 0.5rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 0.9rem;
}

/* 题型选择 */
.mode-selection {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.mode-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mode-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.mode-option input[type="checkbox"]:checked + .mode-info {
  color: #667eea;
}

.mode-info {
  display: flex;
  flex-direction: column;
}

.mode-name {
  font-weight: 600;
  color: #333;
}

.mode-desc {
  font-size: 0.8rem;
  color: #666;
}

/* 难度选择 */
.difficulty-selection {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.difficulty-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.difficulty-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.difficulty-option input[type="radio"]:checked + .difficulty-info {
  color: #667eea;
}

.difficulty-info {
  display: flex;
  flex-direction: column;
}

.difficulty-name {
  font-weight: 600;
  color: #333;
}

.difficulty-desc {
  font-size: 0.8rem;
  color: #666;
}

/* 主题选择 */
.theme-selection {
  display: flex;
  gap: 1rem;
}

.theme-option {
  cursor: pointer;
}

.theme-preview {
  padding: 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s ease;
}

.theme-preview.light {
  background: #f8f9fa;
  color: #333;
}

.theme-preview.dark {
  background: #2d3748;
  color: white;
}

.theme-option input[type="radio"]:checked + .theme-preview {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 学习模式选择 */
.learning-mode-selection {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.learning-mode-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.learning-mode-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.learning-mode-option input[type="radio"]:checked + .learning-mode-info {
  color: #667eea;
}

.learning-mode-info {
  display: flex;
  flex-direction: column;
}

.learning-mode-name {
  font-weight: 600;
  color: #333;
}

.learning-mode-desc {
  font-size: 0.8rem;
  color: #666;
}

/* 操作按钮 */
.settings-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  padding: 2rem 0;
}

.reset-btn,
.export-btn,
.import-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reset-btn {
  background: #f44336;
  color: white;
}

.export-btn {
  background: #4caf50;
  color: white;
}

.import-btn {
  background: #2196f3;
  color: white;
}

.reset-btn:hover,
.export-btn:hover,
.import-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-container {
    padding: 1rem;
  }
  
  .setting-item {
    flex-direction: column;
    gap: 1rem;
  }
  
  .setting-info {
    margin-right: 0;
  }
  
  .theme-selection {
    flex-direction: column;
  }
  
  .settings-actions {
    flex-direction: column;
  }
}
</style>
