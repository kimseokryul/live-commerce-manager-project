<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

import SummaryCardOrder from './dashboard/SummaryCardOrder.vue'
import SummaryCardProduct from './dashboard/SummaryCardProduct.vue'
import SummaryCardRefund from './dashboard/SummaryCardRefund.vue'
import ReviewCard from './dashboard/ReviewCard.vue'
import PopularProductCard from './dashboard/PopularProductCard.vue'
import RecentInquiry from './dashboard/RecentInquiry.vue'
import StockWarningCard from './dashboard/StockWarningCard.vue'
import SalesChart from './dashboard/SalesChart.vue'

// 주문 상태
const paid = ref(0)
const preparing = ref(0)
const delivering = ref(0)

// 상품 상태
const onSale = ref(0)
const offSale = ref(0)
const outOfStock = ref(0)

// 환불 상태
const cancel = ref(0)
const returnCount = ref(0)
const exchange = ref(0)

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const headers = { authorization: `Bearer ${token}` }

  try {
    // ✅ 주문 상태
    const orderRes = await axios.get('/api/dashboard/order-status', { headers })
    const order = orderRes.data
    paid.value = order.paid
    preparing.value = order.preparing
    delivering.value = order.delivering
    cancel.value = order.cancelled
    returnCount.value = order.returnRequested
    exchange.value = order.exchangeRequested

    // ✅ 상품 상태
    const productRes = await axios.get('/api/products/dashboard/product-status', { headers })
    const product = productRes.data
    onSale.value = product.onSale
    offSale.value = product.offSale
    outOfStock.value = product.outOfStock
  } catch (e) {
    console.error('대시보드 데이터 조회 실패:', e)
  }
})
</script>

<template>
  <div class="dashboard-grid">
    <div class="card-group" style="grid-area: cards">
      <SummaryCardOrder :paid="paid" :preparing="preparing" :delivering="delivering" />
      <SummaryCardProduct :onSale="onSale" :offSale="offSale" :outOfStock="outOfStock" />
      <SummaryCardRefund :cancel="cancel" :returnCount="returnCount" :exchange="exchange" />
    </div>

    <div class="graph">
      <SalesChart />
    </div>

    <div class="review-section">
      <ReviewCard />
    </div>

    <div class="popular-section">
      <PopularProductCard />
    </div>

    <div class="bottom-widgets">
      <RecentInquiry />
      <StockWarningCard />
    </div>
  </div>
</template>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: 3fr 1.2fr;
  grid-template-rows: auto auto auto auto;
  gap: 1rem;
  padding: 0.5rem 1rem 1rem 1rem; /* 상단 여백 더 줄임 */
  grid-template-areas:
    "cards           review-section"
    "graph           review-section"
    "graph           popular-section"
    "bottom-widgets  popular-section";
  box-sizing: border-box;
}

.card-group {
  display: flex;
  gap: 1rem;
  grid-area: cards;
}
.card-group > * {
  flex: 1;
}

.graph {
  grid-area: graph;
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  min-height: 300px;
}

.review-section {
  grid-area: review-section;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  min-height: 400px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.popular-section {
  grid-area: popular-section;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  min-height: 250px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.bottom-widgets {
  grid-area: bottom-widgets;
  display: flex;
  gap: 1rem;
}
.bottom-widgets > * {
  flex: 1;
}
</style>
