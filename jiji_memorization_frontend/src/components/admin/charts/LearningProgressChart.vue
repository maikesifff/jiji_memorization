<template>
  <div class="chart-container">
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

export default {
  name: 'LearningProgressChart',
  props: {
    stats: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const chartCanvas = ref(null)
    let chart = null

    const createChart = () => {
      if (!chartCanvas.value) return

      const ctx = chartCanvas.value.getContext('2d')
      
      const labels = ['教材', '单元', '单词', '释义', '短语', '例句', '关联']
      const values = [
        props.stats.totalTextbooks || 0,
        props.stats.totalUnits || 0,
        props.stats.totalWords || 0,
        props.stats.totalMeanings || 0,
        props.stats.totalPhrases || 0,
        props.stats.totalSentences || 0,
        props.stats.totalUnitWords || 0
      ]

      // 生成渐变色
      const gradients = []
      const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#43e97b']
      
      colors.forEach((color, index) => {
        const gradient = ctx.createLinearGradient(0, 0, 0, 200)
        gradient.addColorStop(0, color)
        gradient.addColorStop(1, color + '80')
        gradients.push(gradient)
      })

      chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: '数量统计',
            data: values,
            backgroundColor: gradients,
            borderColor: colors,
            borderWidth: 1,
            borderRadius: 8,
            borderSkipped: false
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              backgroundColor: 'rgba(0, 0, 0, 0.8)',
              titleColor: '#fff',
              bodyColor: '#fff',
              borderColor: '#fff',
              borderWidth: 1,
              cornerRadius: 8,
              callbacks: {
                label: function(context) {
                  return `${context.label}: ${context.parsed.y}`
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.1)',
                drawBorder: false
              },
              ticks: {
                color: '#666',
                font: {
                  size: 12
                }
              }
            },
            x: {
              grid: {
                display: false
              },
              ticks: {
                color: '#666',
                font: {
                  size: 12
                }
              }
            }
          },
          animation: {
            duration: 2000,
            easing: 'easeInOutQuart'
          }
        }
      })
    }

    const updateChart = () => {
      if (chart) {
        chart.destroy()
      }
      createChart()
    }

    onMounted(() => {
      createChart()
    })

    onUnmounted(() => {
      if (chart) {
        chart.destroy()
      }
    })

    // 监听数据变化
    if (props.stats) {
      updateChart()
    }

    return {
      chartCanvas
    }
  }
}
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
}
</style>
