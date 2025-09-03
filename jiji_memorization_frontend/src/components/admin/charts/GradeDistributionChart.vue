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
  name: 'GradeDistributionChart',
  props: {
    data: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    const chartCanvas = ref(null)
    let chart = null

    const createChart = () => {
      if (!chartCanvas.value) return

      const ctx = chartCanvas.value.getContext('2d')
      
      // 处理数据，统计各年级的教材数量
      const gradeStats = {}
      props.data.forEach(textbook => {
        const grade = textbook.grade || '未知年级'
        gradeStats[grade] = (gradeStats[grade] || 0) + 1
      })

      const labels = Object.keys(gradeStats)
      const values = Object.values(gradeStats)

      // 生成颜色
      const colors = [
        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', 
        '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
      ]

      chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [{
            data: values,
            backgroundColor: colors.slice(0, labels.length),
            borderColor: '#fff',
            borderWidth: 2,
            hoverBorderWidth: 3
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                padding: 20,
                usePointStyle: true,
                font: {
                  size: 12
                }
              }
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const label = context.label || ''
                  const value = context.parsed
                  const total = context.dataset.data.reduce((a, b) => a + b, 0)
                  const percentage = ((value / total) * 100).toFixed(1)
                  return `${label}: ${value}本 (${percentage}%)`
                }
              }
            }
          },
          animation: {
            animateRotate: true,
            animateScale: true
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
    if (props.data && props.data.length > 0) {
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
