<!-- src/views/admin/sales/PaymentMethodPieChart.vue -->
<template>
  <div class="chart-box">
    <h3>결제수단 비율</h3>
    <PieChart :data="chartData" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title, Tooltip, Legend,
  ArcElement
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, ArcElement)

const chartData = {
  labels: ['신용카드', '계좌이체', '간편결제'],
  datasets: [
    {
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
      data: [60, 25, 15]
    }
  ]
}

const PieChart = defineComponent({
  components: { Pie },
  props: ['data'],
  setup(props) {
    return () => h(Pie, { data: props.data })
  }
})
</script>

<style scoped>
.chart-box {
  background: #fff;
  padding: 1rem;
  border-radius: 12px;
}
</style>