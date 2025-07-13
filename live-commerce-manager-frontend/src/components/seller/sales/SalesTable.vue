<template>
  <div>
    <h3 class="table-title">📑 정산용 주문 내역 및 매출 현황</h3>

    <div class="filter-row">
      <input type="date" v-model="startDate" class="filter-input" />
      <span class="date-separator">~</span>
      <input type="date" v-model="endDate" class="filter-input" />

      <input
        type="text"
        v-model="productKeyword"
        placeholder="상품명 검색"
        class="filter-input keyword-input"
      />

      <select v-model="paymentMethodKeyword" class="filter-select">
        <option value="">전체 결제수단</option>
        <option value="카드">카드</option>
        <option value="페이">페이</option>
        <option value="휴대폰">휴대폰</option>
        <option value="입금">입금</option>
      </select>

      <button @click="fetchData" class="btn primary">조회</button>
      <button @click="downloadCSV" class="btn secondary">CSV 다운로드</button>
    </div>

    <table class="sales-table">
      <thead>
        <tr>
          <th>주문일시</th>
          <th>주문번호</th>
          <th>상품명</th>
          <th>수량</th>
          <th>결제수단</th>
          <th>결제금액</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in dataList" :key="index">
          <td>{{ formatDateTime(item.orderDate) }}</td>
          <td>{{ item.orderId }}</td>
          <td>{{ item.productName }}</td>
          <td>{{ item.quantity.toLocaleString() }}</td>
          <td>{{ item.paymentMethodName }}</td>
          <td>{{ item.totalPrice.toLocaleString() }}원</td>
        </tr>
      </tbody>
      <tfoot v-if="dataList.length > 0">
        <tr>
          <td colspan="5" class="total-label">합계</td>
          <td class="total-amount">{{ totalAmount.toLocaleString() }}원</td>
        </tr>
      </tfoot>
    </table>

    <p v-if="dataList.length === 0" class="no-data">조회된 주문 내역이 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import dayjs from 'dayjs'

// 날짜 기본값: 지난달 1일 ~ 말일
const today = dayjs()
const firstDayOfLastMonth = today.subtract(1, 'month').startOf('month').format('YYYY-MM-DD')
const lastDayOfLastMonth = today.subtract(1, 'month').endOf('month').format('YYYY-MM-DD')

const startDate = ref(firstDayOfLastMonth)
const endDate = ref(lastDayOfLastMonth)
const productKeyword = ref('')
const paymentMethodKeyword = ref('')
const dataList = ref([])
const totalAmount = ref(0)

const fetchData = async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  try {
    const res = await axios.get('/api/dashboard/sales/order-items', {
      params: {
        startDate: startDate.value,
        endDate: endDate.value,
        productKeyword: productKeyword.value,
        paymentMethodKeyword: paymentMethodKeyword.value
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    dataList.value = res.data
    totalAmount.value = res.data.reduce((sum, item) => sum + item.totalPrice, 0)
  } catch (e) {
    console.error('주문 목록 조회 실패:', e)
  }
}

onMounted(() => {
  fetchData()
})

const formatDateTime = (dateTimeStr) => {
  const d = new Date(dateTimeStr)
  return d.toLocaleString()
}

const downloadCSV = () => {
  const rows = [
    ['주문일시', '주문번호', '상품명', '수량', '결제수단', '결제금액'],
    ...dataList.value.map(item => [
      formatDateTime(item.orderDate),
      item.orderId,
      item.productName,
      item.quantity,
      item.paymentMethodName,
      item.totalPrice
    ])
  ]
  const csvContent = rows.map(r => r.join(',')).join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.setAttribute('href', url)
  link.setAttribute('download', '매출_주문목록.csv')
  link.click()
}
</script>

<style scoped>
.filter-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 3rem;
  flex-wrap: nowrap; /* ✅ 반드시 가로 일자 유지 */
  overflow-x: auto;
  justify-content: center;
}

.filter-input {
  padding: 0.4rem 0.6rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 130px;
  
}

.keyword-input {
  flex: 1; /* ✅ 남는 공간 최대한 차지 */
  min-width: 180px; /* 기본 너비 확보 */
  max-width: 500px; /* 너무 넓어지지 않게 제한 가능 */
}

.table-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 2rem;
}

.filter-select {
  padding: 0.4rem 0.6rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 130px; /* ✅ 셀렉트 박스 너비 강제 고정 */
  text-align: center;
  text-align-last: center; /* ✅ 가운데 정렬 */
}

.btn {
  padding: 0.45rem 0.8rem;
  border: none;
  border-radius: 5px;
  font-size: 0.9rem;
  cursor: pointer;
  white-space: nowrap;
}

.btn.primary {
  background-color: #4a90e2;
  color: white;
}
.btn.primary:hover {
  background-color: #357ac8;
}
.btn.secondary {
  background-color: #1d6f42; /* Excel 느낌의 초록색 */
  color: white;
}
.btn.secondary:hover {
  background-color: #145a32;
}

.sales-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}
.sales-table th,
.sales-table td {
  border: 1px solid #ddd;
  padding: 0.6rem;
  text-align: center;
}
.total-label {
  font-weight: bold;
  text-align: right;
}
.total-amount {
  font-weight: bold;
  color: #2c3e50;
}
.no-data {
  text-align: center;
  color: #888;
  margin-top: 1rem;
}
</style>
