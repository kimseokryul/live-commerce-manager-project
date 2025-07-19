<script setup>
// 📦 필수 라이브러리 및 컴포넌트 import
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 📊 대시보드 카드 및 그래프 컴포넌트
import KpiCard from '../admin/dashboard/KpiCard.vue'
import TopProducts from '../admin/dashboard/TopProducts.vue'
import SalesChartWhole from '../admin/dashboard/SalesChartWhole.vue'
import TopHosts from '../admin/dashboard/TopHosts.vue'
import SalesByCategory from '../admin/dashboard/SalesByCategory.vue'
import AlertBox from '../admin/dashboard/AlertBox.vue'

// ✅ 주문 상태 관련 ref 변수
const paid = ref(0)            // 결제 완료 주문 수
const preparing = ref(0)       // 상품 준비 중 주문 수
const delivering = ref(0)      // 배송 중 주문 수

// ✅ 상품 상태 관련 ref 변수
const onSale = ref(0)          // 판매중 상품 수
const offSale = ref(0)         // 판매중지 상품 수
const outOfStock = ref(0)      // 품절 상품 수

// ✅ 환불/교환 관련 ref 변수
const cancel = ref(0)          // 취소 요청 수
const returnCount = ref(0)     // 반품 요청 수
const exchange = ref(0)        // 교환 요청 수

// ✅ 로그인한 회원 등급
const grade_id = ref('')

// ✅ 관리자용 KPI 데이터
const kpi = ref({
  todaySales: '0₩',           // 오늘의 총 매출
  totalMembers: 0,            // 전체 회원 수
  newMembers: 0,              // 신규 회원 수
  totalOrders: 0,             // 전체 주문 수
  processingOrders: 0,        // 처리 중 주문 수
  totalHosts: 0,              // 전체 호스트 수
})

// ✅ 페이지 로드 시 데이터 조회
onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const headers = { authorization: `Bearer ${token}` }

  try {
    // 🔐 로그인한 유저의 등급 확인
    const gradeRes = await axios.get('/api/login/me', { headers })
    grade_id.value = gradeRes.data.grade_id
    console.log("grade_id: ", gradeRes.data.grade_id)

    // 🧑‍💼 호스트일 경우 데이터 분기 처리
    if(grade_id.value === 'HOST') {
      // 📦 주문 상태 데이터 조회
      const orderRes = await axios.get('/api/dashboard/order-status', { headers })
      const order = orderRes.data
      paid.value = order.paid
      preparing.value = order.preparing
      delivering.value = order.delivering
      cancel.value = order.cancelled
      returnCount.value = order.returnRequested
      exchange.value = order.exchangeRequested

      // 🛍️ 상품 상태 데이터 조회
      const productRes = await axios.get('/api/products/dashboard/product-status', { headers })
      const product = productRes.data
      onSale.value = product.onSale
      offSale.value = product.offSale
      outOfStock.value = product.outOfStock
    } 
    // 🧑‍💼 관리자일 경우 KPI 대시보드 정보 요청
    else {
      const dashboardRes = await axios.get('api/dashboard/admin', { headers })
      kpi.value = dashboardRes.data
    }

  } catch (e) {
    console.error('대시보드 데이터 조회 실패:', e)
  }
})
</script>

<template>
  <div class="dashboard-grid">
    <!-- 관리자 등급일 때 대시보드 렌더링 -->
    <template v-if="grade_id==='ADMIN'">

      <!-- KPI 카드 요약 정보 영역 -->
      <div class="kpi-cards">
        <KpiCard icon="💰" title="오늘 매출" :value="kpi.todaySales + '₩'" />
        <KpiCard icon="👥" title="총 회원 수" :value="kpi.totalMembers"/>
        <KpiCard icon="🆕" title="신규 회원 수" :value="kpi.newMembers" />
        <KpiCard icon="📦" title="총 주문 수" :value="kpi.totalOrders" />
        <KpiCard icon="🚚" title="처리 중 주문" :value="kpi.processingOrders" />
        <KpiCard icon="🧑‍💼" title="총 호스트 수" :value="kpi.totalHosts" />
      </div>

      <!-- 전체 매출 그래프 -->
      <div class="graph">
        <SalesChartWhole />
      </div>

      <!-- 인기 상품 Top 5 -->
      <div class="popular-section">
        <TopProducts />
      </div>

      <!-- 카테고리별 매출 통계 -->
      <div class="review-section">
        <SalesByCategory />
      </div>

      <!-- 인기 호스트 / 알림 박스 -->
      <div class="bottom-widgets">
        <div class="top-hosts-section">
          <TopHosts />
        </div>
        <div class="alerts-section">
          <AlertBox />
        </div>
      </div>
    </template>

    <!-- 권한 확인 중 로딩 메시지 -->
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
