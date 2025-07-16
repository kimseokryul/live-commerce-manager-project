<!-- src/views/admin/sales/TopProductsBarChart.vue -->
<template>
  <div class="chart-box">
    <h3>상품별 매출 TOP5</h3>
    <Bar :data="chartData" />
  </div>
</template>

<script setup>
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title, Tooltip, Legend,
  CategoryScale, LinearScale, BarElement
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, CategoryScale, LinearScale, BarElement)

const chartData = {
  labels: ['상품A', '상품B', '상품C', '상품D', '상품E'],
  datasets: [
    {
      label: '매출액',
      data: [300000, 250000, 180000, 120000, 90000],
      backgroundColor: '#69b3f9'
    }
  ]
}
</script>

<style scoped>
.chart-box {
  background: #fff;
  padding: 1rem;
  border-radius: 12px;
}
</style>