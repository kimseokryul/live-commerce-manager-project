<template>
  <div class="summary-card">
    <h3 class="summary-title">📊 이번 달 매출 현황</h3>
    <div class="summary-stats">
      <div class="summary-item">
        <span class="label">총 판매량</span>
        <span class="value">{{ totalOrders.toLocaleString() }}건</span>
      </div>
      <div class="summary-item">
        <span class="label">총 매출액</span>
        <span class="value">{{ totalAmount.toLocaleString() }}원</span>
      </div>
      <div class="summary-item">
        <span class="label">평균 결제금액</span>
        <span class="value">{{ averageAmount.toLocaleString() }}원</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const totalOrders = ref(0)
const totalAmount = ref(0)
const averageAmount = ref(0)

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const now = new Date()
  const yearMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`

  try {
    const res = await axios.get('/api/dashboard/sales/summary-card', {
      params: { month: yearMonth },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    totalOrders.value = res.data.totalOrders
    totalAmount.value = res.data.totalAmount
    averageAmount.value = res.data.averageAmount
  } catch (e) {
    console.error('카드1 매출 요약 정보 가져오기 실패:', e)
  }
})
</script>

<style scoped>
.summary-title {
  font-size: 1.6rem;
  font-weight: bold;
  margin-bottom: 4rem;
}

.summary-stats {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 1.1rem;
}

.label {
  font-weight: 400;
}

.value {
  font-weight: bold;
}
</style>
