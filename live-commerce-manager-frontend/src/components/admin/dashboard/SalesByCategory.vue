<template>
  <div class="category-chart">
    <h3>📈 카테고리별 매출 분포</h3>
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'
import axios from 'axios'

const chartRef = ref(null)
const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')

onMounted(async () => {
  try{
    const chartRes = await axios.get('/api/dashboard/admin/sales/category', {
      headers: { Authorization: `Bearer ${token}` }
    })
  const responseData = chartRes.data
  console.log(responseData)
  // labels, data 추출
  const labels = responseData.map(item => item.name)
  const data = responseData.map(item => item.total_price)

  const backgroundColors = [
      '#4dc9f6', '#f67019', '#f53794', '#537bc4', '#acc236',
      '#166a8f', '#00a950', '#58595b', '#8549ba'
  ]

  // 파이차트 생성
  new Chart(chartRef.value, {
    type: 'pie',
    data: {
      labels: labels,
      datasets: [{
        data: data,
         backgroundColor: backgroundColors.slice(0, labels.length) // 카테고리 수에 맞춰 자름
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  })
  }catch (e) {
    console.error('카테고리별 매출 차트 로드 실패:', e)
  }
})
</script>

<style scoped>
.category-chart {
  max-width: 400px;
  margin: 0 auto;
  height: 300px; /* ✅ 높이 제한 추가 */
  position: relative;
}
canvas {
  width: 100% !important;
  height: 100% !important;
}
</style>