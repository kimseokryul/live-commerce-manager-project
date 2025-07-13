<!-- src/views/admin/sales/SalesSummaryCard.vue -->
<template>
  <div class="summary-cards">
    <div class="summary-card">
      <span>💰 총 매출</span>
      <strong>₩{{ totalSales.toLocaleString() }}</strong>
    </div>
    <div class="summary-card">
      <span>📦 총 주문 수</span>
      <strong>{{ totalOrders }}</strong>
    </div>
    <div class="summary-card">
      <span>📈 평균 주문액</span>
      <strong>₩{{ avgOrderAmount.toLocaleString() }}</strong>
    </div>
  </div>
</template>

<script setup>
defineProps({
  totalSales: Number,
  totalOrders: Number,
  avgOrderAmount: Number
});
</script>

<style scoped>
.summary-cards {
  display: flex;
  gap: 1rem;
}
.summary-card {
  flex: 1;
  background: #fff;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  text-align: center;
}
.summary-card span {
  display: block;
  color: #555;
  margin-bottom: 0.5rem;
}
.summary-card strong {
  font-size: 1.2rem;
}
</style>
