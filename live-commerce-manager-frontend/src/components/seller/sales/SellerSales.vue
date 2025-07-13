<template>
  <div class="dashboard-container">
    <!-- 카드 3개 -->
    <div class="card-row">
      <!-- 카드 1: 매출 요약 -->
      <SalesSummaryCard />

      <!-- 카드 2: 상품 TOP 5 막대그래프 -->
      <TopProductsBarChart />

      <!-- 카드 3: 도넛그래프 자리 (추후 교체 예정) -->
      <PaymentMethodPieChart />
    </div>

    <!-- 테이블 -->
    <div class="table-wrapper">
      <SalesTable />
    </div>
  </div>
</template>

<script setup>
import SalesSummaryCard from './SalesSummaryCard.vue'
import TopProductsBarChart from './TopProductsBarChart.vue' // ✅ 추가: 막대 그래프 컴포넌트
import PaymentMethodPieChart from './PaymentMethodPieChart.vue'
import SalesTable from './SalesTable.vue'
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 1rem 1.5rem;
}

/* 카드 3개 가로 정렬 */
.card-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  width: 100%;
}

/* 카드 공통 스타일 */
.card-row > * {
  flex: 1;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  box-sizing: border-box;
  min-height: 260px; /* 높이 통일 (옵션) */
}

/* 테이블 스타일 */
.table-wrapper {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  min-height: 300px;
}
</style>
