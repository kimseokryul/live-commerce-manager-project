<template>
  <div class="chart-card">
    <div class="chart-header">
  <h3 class="chart-title">📦 상품 TOP 5</h3>

  <div class="tab-buttons">
    <button
      :class="['tab-btn', { active: activeTab === 'quantity' }]"
      @click="() => { activeTab = 'quantity'; fetchData() }"
    >
      수량별
    </button>
    <button
      :class="['tab-btn', { active: activeTab === 'sales' }]"
      @click="() => { activeTab = 'sales'; fetchData() }"
    >
      매출별
    </button>
  </div>
</div>

    <div class="chart-area">
      <Bar v-if="chartData" :data="chartData" />
      <p v-else class="loading-text">데이터 불러오는 중...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

const activeTab = ref('quantity')
const chartData = ref(null)

const fetchData = async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const url =
    activeTab.value === 'quantity'
      ? '/api/dashboard/sales/top-products/quantity'
      : '/api/dashboard/sales/top-products/sales'

  try {
    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const labels = res.data.map(item => item.productName)
    const data =
      activeTab.value === 'quantity'
        ? res.data.map(item => item.totalQuantity)
        : res.data.map(item => item.totalSales)

    chartData.value = {
      labels,
      datasets: [
        {
          label: activeTab.value === 'quantity' ? '판매 수량' : '매출액',
          data,
          backgroundColor: 'rgba(54, 162, 235, 0.7)'
        }
      ]
    }
  } catch (e) {
    console.error('막대그래프 데이터 조회 실패:', e)
  }
}

onMounted(fetchData)
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
  height: 100%; /* 카드 높이 균일하게 유지 */
  box-sizing: border-box;
}

/* 제목 + 버튼을 한 줄로 정렬 */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

/* 탭 버튼 영역 */
.tab-buttons {
  display: flex;
  gap: 0.5rem;
}

.tab-btn {
  background-color: #f0f2f5;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.tab-btn:hover {
  background-color: #e0e2e6;
}
.tab-btn.active {
  background-color: #4a90e2;
  color: white;
}

/* 그래프 영역 */
.chart-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 220px;
}

.loading-text {
  color: #777;
  font-size: 0.95rem;
}
</style>

