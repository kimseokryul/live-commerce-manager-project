<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

import KpiCard from '../admin/dashboard/KpiCard.vue'
import TopProducts from '../admin/dashboard/TopProducts.vue'
import SalesChartWhole from '../admin/dashboard/SalesChartWhole.vue'
import TopHosts from '../admin/dashboard/TopHosts.vue'
import SalesByCategory from '../admin/dashboard/SalesByCategory.vue'
import AlertBox from '../admin/dashboard/AlertBox.vue'

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

// // 회원 등급
const grade_id = ref('')

const kpi = ref({
  todaySales: '₩0',
  totalUsers: 0,
  newUsers: 0,
  totalOrders: 0,
  processingOrders: 0,
  totalHosts: 0,
})


onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const headers = { authorization: `Bearer ${token}` }

  try {
    // ✅ 로그인한 회원 등급
    const gradeRes = await axios.get('/api/login/me', { headers })
    grade_id.value = gradeRes.data.grade_id
    console.log("grade_id: ", gradeRes.data.grade_id)

    if(grade_id.value === 'HOST')
    {
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

    }
    
    
    
  } catch (e) {
    console.error('대시보드 데이터 조회 실패:', e)
  }
})
</script>

<template>
  <div class="dashboard-grid">
    <template v-if="grade_id==='ADMIN'">
      <!-- ✅ KPI 요약 카드 영역 -->
      <div class="kpi-cards">
        <KpiCard icon="💰" title="오늘 매출" :value="kpi.todaySales" />
        <KpiCard icon="👥" title="총 회원 수" :value="kpi.totalUsers" />
        <KpiCard icon="🆕" title="신규 회원 수" :value="kpi.newUsers" />
        <KpiCard icon="📦" title="총 주문 수" :value="kpi.totalOrders" />
        <KpiCard icon="🚚" title="처리 중 주문" :value="kpi.processingOrders" />
        <KpiCard icon="🧑‍💼" title="총 호스트 수" :value="kpi.totalHosts" />
      </div>

      <div class="graph">
        <SalesChartWhole />
      </div>
      <div class="popular-section">
        <TopProducts />
      </div>
      <div class="review-section">
        <SalesByCategory />
      </div>
      <div class="bottom-widgets">
        <div class="top-hosts-section">
          <TopHosts />
        </div>
        <div class="alerts-section">
          <AlertBox />
        </div>
      </div>
    </template>
    <template v-else>
      로딩 중...
    </template>
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

.admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
}

.sales-chart-section,
.top-products-section,
.top-hosts-section,
.alerts-section {
  background: white;
  border-radius: 10px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.kpi-cards {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}
.kpi-cards > * {
  flex: 1;
  min-width: 180px;
}

/* 전체 콘텐츠 그리드 */
.admin-content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: auto auto;
  gap: 1rem;
  grid-template-areas:
    "graph popular"
    "pie   bottom";
}

/* 각 영역 설정 */
.admin-graph {
  grid-area: graph;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  min-height: 300px;
}

.admin-popular-section {
  grid-area: popular;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  min-height: 300px;
}

.sales-pie-section {
  grid-area: pie;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  min-height: 300px;
}

.admin-bottom-widgets {
  grid-area: bottom;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.admin-bottom-widgets > * {
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}
</style>
