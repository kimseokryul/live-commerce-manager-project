<template>
  <div class="rounded-order-page">
    <!-- 상단 제목 -->
    <div class="page-title">
      <h2>전체 주문</h2>
    </div>

    <!-- 기본 검색 영역 -->
    <div class="rounded-search-bar">
      <!-- 검색 조건 선택 (전체 / 주문번호 / 상품명) -->
      <select v-model="searchCondition.searchColumn" @change="handleSizeChange">
        <option value="">전체</option>
        <option value="order_id">주문번호</option>
        <option value="product_name">상품명</option>
      </select>
      <input type="text" v-model="searchCondition.searchValue" placeholder="검색어 입력" />
      <button class="search-btn" @click="searchOrders">🔍</button>

      <!-- 상세검색 토글 버튼 -->
      <button class="detail-toggle" @click="showDetail = !showDetail">
        {{ showDetail ? '▲ 상세검색 닫기' : '+ 상세검색' }}
      </button>
    </div>

    <!-- 상세 검색 영역 (애니메이션 포함) -->
    <transition name="fade">
      <div v-if="showDetail" class="detail-box">
        <h4>Search</h4>

        <!-- 상세 조건들 -->
        <table class="detail-table">
          <tbody>
            <!-- 주문일 범위 -->
            <tr>
              <th>주문일</th>
              <td colspan="5">
                <input type="date" v-model="searchCondition.startDate" />
                <span> - </span>
                <input type="date" v-model="searchCondition.endDate"/>
                <!-- 빠른 기간 선택 버튼 -->
                <button>오늘</button>
                <button>1주일</button>
                <button>1개월</button>
                <button>3개월</button>
                <button>6개월</button>
                <button>1년</button>
              </td>
            </tr>

            <!-- 진행 상태 필터 -->
            <tr>
              <th>진행상태</th>
              <td colspan="5">
                <label v-for="status in ['결제완료','결제취소','배송준비','배송중','배송완료','환불요청','교환요청','반품요청','주문취소']">
                  <input type="checkbox" :value="status" v-model="searchCondition.order_status"/> {{ status }}
                </label>
              </td>
            </tr>

            <!-- 결제 수단 필터 -->
            <tr>
              <th>결제수단</th>
              <td colspan="5">
                <label v-for="method in ['카드결제','포인트결제','카카오페이','휴대폰결제']">
                  <input type="checkbox" :value="method" v-model="searchCondition.payment_method"/> {{ method }}
                </label>
              </td>
            </tr>

            <!-- 받는 사람 필터 -->
            <tr>
              <th>받는분 이름</th>
              <td><input type="text" v-model="searchCondition.recipient_name" placeholder="받는분 이름" /></td>
              <th>받는분 휴대폰</th>
              <td><input type="text" v-model="searchCondition.recipient_phone" placeholder="휴대폰번호" /></td>
              <th>받는분 주소</th>
              <td><input type="text" v-model="searchCondition.order_address_detail" placeholder="주소" /></td>
            </tr>

            <!-- 주문자 필터 -->
            <tr>
              <th>주문자 이름</th>
              <td><input type="text" v-model="searchCondition.user_name" placeholder="사용자 이름" /></td>
              <th>주문자 휴대폰</th>
              <td><input type="text" v-model="searchCondition.user_phone" placeholder="휴대폰번호" /></td>
              <th>주문자 이메일</th>
              <td><input type="text" v-model="searchCondition.user_email" placeholder="이메일" /></td>
            </tr>
          </tbody>
        </table>

        <!-- 하단 검색 버튼 -->
        <div class="bottom-buttons">
          <button class="search-btn" @click="searchOrders">검색</button>
        </div>
      </div>
    </transition>

    <!-- 상단 컨트롤 (선택 / 정렬 / 페이지당 개수) -->
    <div class="rounded-control">
      <div class="left-buttons">
        <button @click="toggleAll">전체선택</button>
        <button @click="selectCancel">선택해제</button>
        <button @click="cancelMultiOrder">선택 주문취소</button>
      </div>
      <div class="right-selects">
        <!-- 정렬 방식 -->
        <select v-model="sortOption" @change="handleSizeChange">
          <option value="order_date_desc">최신주문일</option>
          <option value="order_date_asc">오래된주문일</option>
          <option value="total_price_desc">최대결제금액</option>
          <option value="total_price_asc">최소결제금액</option>
        </select>

        <!-- 페이지당 데이터 수 -->
        <select v-model="size" @change="handleSizeChange">
          <option :value="5">5개씩</option>
          <option :value="10">10개씩</option>
          <option :value="20">20개씩</option>
          <option :value="50">50개씩</option>
          <option :value="100">100개씩</option>
        </select>
      </div>
    </div>

    <!-- 주문 리스트 테이블 -->
    <div class="rounded-table-wrapper">
      <table class="rounded-order-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="toggleAll" /></th>
            <th>No</th>
            <th>주문번호/주문자</th>
            <th>상품</th>
            <th>결제정보</th>
            <th>주문일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(order, i) in orders" :key="order.order_id">
            <td><input type="checkbox" :value="order.order_id" v-model="selectedOrders" /></td>
            <td>{{ orders.length - i }}</td>
            <td>
              <div class="order-num">{{ order.order_id }}</div>
              <div class="order-user">{{ order.user_name }}({{ order.user_id }})</div>
            </td>
            <td>
              <!-- 주문 상품 목록 -->
              <div class="product" v-for="(p, idx) in order.orderItems" :key="idx">
                <img :src="getFullImageUrl(p.item_image_url)" />
                <div>
                  <div class="product-name">{{ p.item_name }}</div>
                  <div class="product-device">{{ p.quantity }}개</div>
                </div>
              </div>
            </td>
            <td>
              <!-- 상태 + 금액 -->
              <div class="status" :class="getStatusClass(order.order_status)">
                {{ order.order_status }}
              </div>
              <div class="price">{{ order.total_price }}원</div>
            </td>
            <td>{{ order.order_date }}</td>
            <td class="action-buttons">
              <router-link :to="`/order/detail/?order_id=${order.order_id}`" class="action-button-link">상세보기</router-link>
              <button @click="cancelOrder(order.order_id)">주문취소</button>
              <button disabled>주문서</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination-wrapper" v-if="pageResponse">
      <div class="pagination">
        <button class="btn-main" @click="goToPage(pageResponse.startPage - 1)" :disabled="!pageResponse.prev">&lt;</button>
        <button
          v-for="page in pageNumbers"
          :key="page"
          @click="goToPage(page)"
          :class="['btn-main', { active: page === pageResponse.pageNo }]">
          {{ page }}
        </button>
        <button class="btn-main" @click="goToPage(pageResponse.endPage + 1)" :disabled="!pageResponse.next">&gt;</button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue' // Vue Composition API
import axios from 'axios'  // HTTP 요청용
import qs from 'qs'        // 배열 쿼리 직렬화 도구 (?status=A&status=B 형식으로 만듦)

// 현재 페이지 번호
const currentPage = ref(1)

// 한 페이지당 표시할 주문 수
const size = ref(10)

// 전체 주문 수 (현재 사용 안하지만 추후 totalPages 계산에 사용 가능)
const totalCount = ref(0)

// 상세 검색창 토글 상태
const showDetail = ref(false)

// 정렬 옵션 선택값
const sortOption = ref('order_date_desc') // 기본값: 최신 주문일 순

// 주문 선택 상태
const selectedOrders = ref([]) // 체크된 주문 ID 배열
const allSelected = ref(false) // 전체 선택 여부

// 주문 리스트
const orders = ref([])

// 검색 조건 객체
const searchCondition = reactive({
  searchColumn: '',              // 검색 기준 컬럼 (전체 / 주문번호 / 상품명)
  searchValue: '',               // 검색어
  startDate: '',                 // 주문 시작일
  endDate: '',                   // 주문 종료일
  order_status: [],              // 주문 상태 (checkbox 다중 선택)
  payment_method: [],            // 결제 수단 (checkbox 다중 선택)
  recipient_name: '',            // 받는 사람 이름
  recipient_phone: '',           // 받는 사람 연락처
  order_address_detail: '',      // 받는 사람 주소
  user_name: '',                 // 주문자 이름
  user_phone: '',                // 주문자 연락처
  user_email: ''                 // 주문자 이메일
})

// 주문 상태별로 CSS 클래스 반환
const getStatusClass = (status) => {
  switch (status) {
    case '결제완료': return 'status-green'
    case '배송준비': return 'status-yellow'
    case '배송중': return 'status-orange'
    case '배송완료': return 'status-blue'
    case '환불요청': return 'status-purple'
    case '결제취소':
    case '주문취소': return 'status-red'
    case '교환요청': return 'status-pink'
    case '반품요청': return 'status-brown'
    default: return ''
  }
}

// 페이지네이션 정보
const pageResponse = ref(null)

// 페이지 번호 리스트 계산 (1~5, 6~10 등)
const pageNumbers = computed(() => {
  if (!pageResponse.value) return []
  const result = []
  for (let i = pageResponse.value.startPage; i <= pageResponse.value.endPage; i++) {
    result.push(i)
  }
  return result
})

// 페이지 이동 (버튼 클릭 시 실행)
const goToPage = (pageNo) => {
  currentPage.value = pageNo
  searchOrders(pageNo)
}

// 선택 해제 버튼 클릭 시
const selectCancel = () => {
  if (selectedOrders.value.length > 0) {
    selectedOrders.value = []
  }
}

// 전체 선택/해제 버튼 클릭 시
const toggleAll = () => {
  allSelected.value = !allSelected.value
  selectedOrders.value = allSelected.value
    ? orders.value.map(o => o.order_id)
    : []
}

// 선택된 주문이 전체 수와 같은지 감시 → allSelected 상태 자동 동기화
watch(selectedOrders, (newVal) => {
  allSelected.value = newVal.length === orders.value.length
})

// 정렬 방식 또는 size 변경 시 페이지 초기화 후 재조회
const handleSizeChange = () => {
  currentPage.value = 1
  searchOrders()
}

// 주문 검색 실행
const searchOrders = async () => {
  try {
    const response = await axios.get('/api/order/', {
      params: {
        ...searchCondition,              // 검색 조건 전체
        pageNo: currentPage.value,       // 현재 페이지 번호
        size: size.value,                // 페이지당 개수
        sortOption: sortOption.value     // 정렬 옵션
      },
      paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' }) // 배열 처리
    })

    // 주문 목록 및 페이지 정보 저장
    orders.value = response.data.list
    pageResponse.value = response.data

    const totalPages = Math.ceil(totalCount.value / size.value)

    // 현재 페이지가 범위 초과했을 경우 이전 페이지로
    if (currentPage.value > totalPages && totalPages > 0) {
      currentPage.value = totalPages
      await fetchOrders()
      return
    }

    // 페이지 데이터가 부족한데 다음 페이지가 존재하면 일부 끌어와서 채움
    if (orders.value.length < size.value && currentPage.value < totalPages) {
      const nextRes = await axios.get('/api/order/', {
        params: {
          pageNo: currentPage.value + 1,
          size: size.value
        }
      })
      const nextPageOrders = nextRes.data.content
      const fillCount = size.value - orders.value.length
      orders.value = [...orders.value, ...nextPageOrders.slice(0, fillCount)]
    }

    // 디버깅 로그
    console.log("현재 size:", size.value)
    console.log("현재 currentPage:", currentPage.value)
    console.log("orders length:", orders.value.length)
    console.log("orders.value:", orders.value)
    console.log("첫 번째 주문의 아이템 개수:", orders.value[0]?.orderItems?.length)

  } catch (error) {
    console.error('검색 실패:', error)
    alert('검색 중 오류 발생')
  }
}

// 단건 주문 취소
const cancelOrder = async (order_id) => {
  try {
    await axios.delete(`/api/order/${order_id}`)
    alert('주문이 취소되었습니다.')
  } catch (error) {
    console.error('주문 취소 실패', error)
    alert('주문 취소에 실패했습니다.')
  }
}

// 선택된 여러 주문을 일괄 취소
const cancelMultiOrder = async () => {
  try {
    await axios.delete("/api/order", {
      data: selectedOrders.value
    })
    alert('주문이 취소되었습니다.')
  } catch (error) {
    console.error('주문 취소 실패', error)
    alert('주문 취소에 실패했습니다.')
  }
}

// 이미지 URL 조합 함수
function getFullImageUrl(path) {
  return `http://localhost:8080${path}` // 실제 서버 주소에 맞게 수정
}

// 컴포넌트 마운트 시 최초 검색 실행
onMounted(() => {
  searchOrders()
})
</script>

<style scoped src="@/assets/order/order.css"></style>