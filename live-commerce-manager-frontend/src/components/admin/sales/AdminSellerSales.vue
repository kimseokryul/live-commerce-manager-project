<template>
  <div class="sales-dashboard">
    <!-- ✅ KPI 카드 영역 -->
    <div class="kpi-cards">
      <SalesSummaryCard icon="💰" label="총 매출" :value="'\u20A9' + totalSales.toLocaleString()" />
      <SalesSummaryCard icon="📦" label="총 주문 수" :value="totalOrders" />
      <SalesSummaryCard icon="📊" label="평균 주문액" :value="'\u20A9' + averageOrderAmount.toLocaleString()" />
      <SalesSummaryCard icon="💳" label="신용카드 매출" :value="'\u20A9' + paymentSummary.card.toLocaleString()" />
      <SalesSummaryCard icon="🏦" label="계좌이체 매출" :value="'\u20A9' + paymentSummary.account.toLocaleString()" />
      <SalesSummaryCard icon="📱" label="간편결제 매출" :value="'\u20A9' + paymentSummary.easy.toLocaleString()" />
    </div>

    <!-- 📅 기간 선택 필터 (추후 연동) -->
    <div class="date-filter">
      <label>조회 기간:</label>
      <input type="date" v-model="startDate" />
      ~
      <input type="date" v-model="endDate" />
      <button @click="filterByDate">조회</button>
    </div>

    <div class="sales-overview-grid">
      <!-- 📋 기간별 매출 테이블 -->
      <div class="sales-table-section">
        <SalesTable :data="salesTableData" />
      </div>

      <!-- 🍰 결제수단 비율 파이 차트 + 상품별 TOP5 -->
      <div class="chart-section">
        <PaymentMethodPieChart :data="paymentChartData" />
        <TopProductsBarChart :data="topProducts" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SalesSummaryCard from './AdminSalesSummaryCard.vue'
import SalesTable from './AdminSalesTable.vue'
import PaymentMethodPieChart from './AdminPaymentMethodPieChart.vue'
import TopProductsBarChart from './AdminTopProductsBarChart.vue'

const totalSales = 3680000
const totalOrders = 105
const averageOrderAmount = Math.floor(totalSales / totalOrders)
const paymentSummary = {
  card: 2500000,
  account: 850000,
  easy: 330000
}

const startDate = ref('2025-07-01')
const endDate = ref('2025-07-03')

const filterByDate = () => {
  // 추후 날짜 필터링 로직 연동
  alert(`조회: ${startDate.value} ~ ${endDate.value}`)
}

const salesTableData = [
  { date: '2025-07-01', orders: 35, amount: 1250000, avg: 35714 },
  { date: '2025-07-02', orders: 42, amount: 1490000, avg: 35476 },
  { date: '2025-07-03', orders: 28, amount: 940000, avg: 33571 },
]

const paymentChartData = [
  { name: '신용카드', value: 2500000 },
  { name: '계좌이체', value: 850000 },
  { name: '간편결제', value: 330000 }
]

const topProducts = [
  { name: '상품 A', sales: 300000 },
  { name: '상품 B', sales: 250000 },
  { name: '상품 C', sales: 180000 },
  { name: '상품 D', sales: 120000 },
  { name: '상품 E', sales: 90000 }
]
</script>

<style scoped>
.sales-dashboard {
  padding: 1rem;
}

.kpi-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.date-filter {
  margin-bottom: 1.5rem;
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.sales-overview-grid {
  display: grid;
  grid-template-columns: 2fr 1.2fr;
  gap: 1.5rem;
  align-items: flex-start;
}

.chart-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
</style>