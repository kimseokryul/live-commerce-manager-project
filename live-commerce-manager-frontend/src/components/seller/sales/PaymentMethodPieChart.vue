<template>
  <div class="chart-card">
    <h3 class="chart-title">💳 결제 수단 비율</h3>
    <div class="chart-area">
      <Pie v-if="chartData" :data="chartData" :options="chartOptions" :plugins="[centerTextPlugin]" />
      <p v-else class="loading-text">데이터 불러오는 중...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, ArcElement)

const chartData = ref(null)
const chartOptions = ref(null)

// ✅ 플러그인을 전역 등록하지 않고 옵션에서만 사용함
const centerTextPlugin = {
  id: 'centerText',
  beforeDraw(chart) {
    if (chart.config.type !== 'pie' && chart.config.type !== 'doughnut') return

    const { ctx } = chart
    const centerX = chart.getDatasetMeta(0).data[0].x
    const centerY = chart.getDatasetMeta(0).data[0].y
    const total = chart._metasets[0].total

    ctx.save()
    ctx.font = 'bold 16px sans-serif'
    ctx.fillStyle = '#333'
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.fillText('총합', centerX, centerY - 12)
    ctx.fillText(`${total}건`, centerX, centerY + 10)
    ctx.restore()
  }
}

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  try {
    const res = await axios.get('/api/dashboard/payment-method-chart', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const { phone, pay, deposit, card } = res.data

    chartData.value = {
      labels: ['휴대폰', '페이', '입금', '카드'],
      datasets: [
        {
          data: [phone, pay, deposit, card],
          backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'],
          borderColor: '#fff',
          borderWidth: 2
        }
      ]
    }

    chartOptions.value = {
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            font: {
              size: 14
            }
          }
        }
      },
      cutout: '75%', // ✅ 도넛 중앙 더 넓게
      responsive: true,
      maintainAspectRatio: false
    }
  } catch (e) {
    console.error('도넛차트 데이터 조회 실패:', e)
  }
})
</script>

<style scoped>
.chart-card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  height: 100%;
  box-sizing: border-box;
}

.chart-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 0;
  text-align: center;
}

.chart-area {
  position: relative;
  width: 100%;
  height: 227px; /* ✅ 도넛 차트 크기 줄임 */
}

.loading-text {
  color: #777;
  font-size: 0.95rem;
}
</style>
