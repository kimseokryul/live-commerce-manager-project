<template>
  <div class="category-chart">
    <h3>📈 카테고리별 매출 분포</h3>
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'

const chartRef = ref(null)

onMounted(() => {
  new Chart(chartRef.value, {
    type: 'pie',
    data: {
      labels: ['전자제품', '의류', '식품', '뷰티', '기타'],
      datasets: [{
        data: [4000000, 3200000, 2800000, 1500000, 900000],
        backgroundColor: ['#4dc9f6','#f67019','#f53794','#537bc4','#acc236']
      }]
    },
    options: {
      maintainAspectRatio: true, // ✅ 정비율 유지 (원형)
      responsive: true,
    }
  })
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