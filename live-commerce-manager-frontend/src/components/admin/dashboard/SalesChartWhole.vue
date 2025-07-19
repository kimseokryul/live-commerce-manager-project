<script setup>
// 필요한 Vue 및 외부 라이브러리 import
import { ref, computed, watch, onMounted } from 'vue'
import { Line } from 'vue-chartjs'
import axios from 'axios'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Filler
} from 'chart.js'

// Chart.js에 필요한 구성 요소 등록
ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  CategoryScale,
  LinearScale,
  PointElement,
  Filler
)

// 일별 또는 월별 보기 설정
const viewType = ref('daily') // 'daily' 또는 'monthly' 선택

// 백엔드에서 가져온 매출 관련 데이터 변수
const salesList = ref([])            // 전체 매출 리스트
const totalSales = ref(0)            // 총 매출액
const averageSales = ref(0)          // 평균 매출액
const maxSales = ref(0)              // 최고 매출액
const maxLabel = ref('')             // 최고 매출을 기록한 날짜 또는 월

// 차트에 표시할 라벨과 값 계산 (computed)
const chartLabels = computed(() => salesList.value.map(d => d.label))
const chartValues = computed(() => salesList.value.map(d => d.totalSales))

// 최고 매출액 및 해당 라벨 추적
watch(chartValues, () => {
  const max = Math.max(...chartValues.value)
  maxSales.value = max
  const idx = chartValues.value.indexOf(max)
  maxLabel.value = chartLabels.value[idx] || ''
})

// 백엔드 API로부터 매출 데이터 요청 함수
const fetchData = async () => {
  // 엔드포인트 선택 (일별/월별)
  const endpoint = viewType.value === 'daily'
    ? '/api/dashboard/admin/sales/daily'
    : '/api/dashboard/admin/sales/monthly'

  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')

  try {
    const res = await axios.get(endpoint, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    const list = res.data
    salesList.value = list

    // 총합, 평균, 최고 매출 계산
    const total = list.reduce((sum, cur) => sum + cur.totalSales, 0)
    const avg = list.length > 0 ? Math.round(total / list.length) : 0
    const maxObj = list.reduce((max, cur) =>
      cur.totalSales > (max?.totalSales || 0) ? cur : max, null
    )

    totalSales.value = total
    averageSales.value = avg
    maxSales.value = maxObj?.totalSales || 0
    maxLabel.value = maxObj?.label || ''
  } catch (e) {
    console.error('📉 매출 데이터 로드 실패:', e)
  }
}

// 페이지 로딩 및 viewType 변경 시 데이터 재요청
onMounted(fetchData)
watch(viewType, fetchData)

// 차트에 전달할 데이터 구성
const chartData = computed(() => ({
  labels: chartLabels.value,
  datasets: [
    {
      label: '매출액 (원)',
      data: chartValues.value,
      borderColor: '#4e73df',
      backgroundColor: 'rgba(78, 115, 223, 0.1)',
      borderWidth: 2,
      pointRadius: 3,
      fill: true,
      tension: 0.3
    }
  ]
}))

// 차트 옵션 정의
const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: true },
    tooltip: {
      callbacks: {
        title: ctx => `${viewType.value === 'daily' ? '날짜' : '월'}: ${ctx[0].label}`,
        label: ctx => {
          const value = ctx.parsed.y
          const avg = averageSales.value
          const diff = value - avg
          const diffStr = diff === 0
            ? ' (평균과 동일)'
            : diff > 0
              ? ` (▲ ${Math.abs(diff).toLocaleString()}원)`
              : ` (▼ ${Math.abs(diff).toLocaleString()}원)`
          return `￦ ${value.toLocaleString()}${diffStr}`
        }
      }
    }
  },
  scales: {
    x: {
      ticks: {
        maxTicksLimit: viewType.value === 'daily' ? 10 : 12
      }
    },
    y: {
      suggestedMin: 0,
      ticks: {
        callback: val => `￦ ${val.toLocaleString()}`
      }
    }
  }
}))
</script>

<template>
  <div class="sales-chart-container">
    <!-- 상단 탭 및 제목 -->
    <div class="chart-header">
      <div class="tab-buttons">
        <button :class="{ active: viewType === 'daily' }" @click="viewType = 'daily'">📅 일별</button>
        <button :class="{ active: viewType === 'monthly' }" @click="viewType = 'monthly'">🗓 월별</button>
      </div>
      <div class="chart-title">📈 매출 그래프</div>
      <router-link to="/admin/sellerSales" class="more-link">더보기</router-link>
    </div>

    <!-- 라인 차트 영역 -->
    <div class="chart-wrapper">
      <Line :data="chartData" :options="chartOptions" />
    </div>

    <!-- 요약 정보 박스 -->
    <div class="summary-box">
      <div>총 매출액: <strong>{{ totalSales.toLocaleString() }}원</strong></div>
      <div>평균 매출액: <strong>{{ averageSales.toLocaleString() }}원</strong></div>
      <div>
        최고 매출 {{ viewType === 'daily' ? '일' : '월' }}:
        <strong>{{ maxLabel }} ({{ maxSales.toLocaleString() }}원)</strong>
      </div>
    </div>
  </div>
</template>


<style scoped>
.sales-chart-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tab-buttons {
  display: flex;
  gap: 0.5rem;
}
.tab-buttons button {
  padding: 0.4rem 1rem;
  border: none;
  border-radius: 6px;
  background-color: #ccc;
  color: #000;
  font-weight: 600;
  cursor: pointer;
}
.tab-buttons button.active {
  background-color: #2f3247;
  color: #fff;
}
.chart-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  text-align: center;
  flex-grow: 1;
  margin-right: 7.7rem;
}
.more-link {
  font-size: 0.9rem;
  color: #888;
  text-decoration: underline;
  cursor: pointer;
}
.more-link:hover {
  color: #2f3247;
}
.chart-wrapper {
  width: 100%;
  height: 300px;
}
.summary-box {
  display: flex;
  justify-content: space-between;
  padding: 0.8rem 1rem;
  background: #f9f9f9;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #333;
}
.summary-box strong {
  font-weight: 600;
  color: #2f3247;
}
</style>
