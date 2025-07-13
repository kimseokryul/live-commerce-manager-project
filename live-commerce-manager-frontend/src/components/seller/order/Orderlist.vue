<template>
  <div class="rounded-order-page">
    <!-- 상단 -->
    <div class="page-title">
      <h2>전체 주문</h2>
    </div>

    <!-- 검색 섹션 -->
    <div class="rounded-search-bar">
      <select v-model="searchCondition.searchColumn" @change="handleSizeChange">
        <option value="">전체</option>
        <option value="order_id">주문번호</option>
        <option value="product_name">상품명</option>
      </select>
      <input type="text" v-model="searchCondition.searchValue" placeholder="검색어 입력" />
      <button class="search-btn" @click="searchOrders">🔍</button>
      <button class="detail-toggle" @click="showDetail = !showDetail">
        {{ showDetail ? '▲ 상세검색 닫기' : '+ 상세검색' }}
      </button>
    </div>

    <!-- 상세 검색 -->
    <transition name="fade">
      <div v-if="showDetail" class="detail-box">
        <h4>Search</h4>

        <table class="detail-table">
          <tbody>
            <tr>
              <th>주문일</th>
              <td colspan="5">
                <input type="date" v-model="searchCondition.startDate" />
                <span> - </span>
                <input type="date" v-model="searchCondition.endDate"/>
                <!-- <div class="quick-range"> -->
                  <button>오늘</button>
                  <button>1주일</button>
                  <button>1개월</button>
                  <button>3개월</button>
                  <button>6개월</button>
                  <button>1년</button>
                <!-- </div> -->
              </td>
            </tr>
            <tr>
              <th>진행상태</th>
              <td colspan="5">
                <label><input type="checkbox" value="결제완료" v-model="searchCondition.order_status"/> 결제완료</label>
                <label><input type="checkbox" value="결제취소" v-model="searchCondition.order_status"/> 결제취소</label>
                <label><input type="checkbox" value="배송준비" v-model="searchCondition.order_status"/> 배송준비</label>
                <label><input type="checkbox" value="배송중" v-model="searchCondition.order_status"/> 배송중</label>
                <label><input type="checkbox" value="배송완료" v-model="searchCondition.order_status"/> 배송완료</label>
                <label><input type="checkbox" value="환불요청" v-model="searchCondition.order_status"/> 환불요청</label>
                <label><input type="checkbox" value="교환요청" v-model="searchCondition.order_status"/> 교환요청</label>
                <label><input type="checkbox" value="반품요청" v-model="searchCondition.order_status"/> 반품요청</label>
                <label><input type="checkbox" value="주문취소" v-model="searchCondition.order_status"/> 주문취소</label>
              </td>
            </tr>
            <tr>
              <th>결제수단</th>
              <td colspan="5">
                <label><input type="checkbox" value="카드결제" v-model="searchCondition.payment_method"/> 카드결제</label>
                <label><input type="checkbox" value="포인트결제" v-model="searchCondition.payment_method"/> 포인트결제</label>
                <label><input type="checkbox" value="카카오페이" v-model="searchCondition.payment_method"/> 카카오페이</label>
                <label><input type="checkbox" value="휴대폰결제" v-model="searchCondition.payment_method"/> 휴대폰결제</label>
              </td>
            </tr>
            <tr>
              <th>받는분 이름</th>
              <td><input type="text" placeholder="받는분 이름" v-model="searchCondition.recipient_name"/></td>
              <th>받는분 휴대폰</th>
              <td><input type="text" placeholder="휴대폰번호" v-model="searchCondition.recipient_phone"/></td>
              <th>받는분 주소</th>
              <td><input type="text" placeholder="주소" v-model="searchCondition.order_address_detail"/></td>
            </tr>
            <tr>
              <th>주문자 이름</th>
              <td><input type="text" placeholder="사용자 이름" v-model="searchCondition.user_name"/></td>
              <th>주문자 휴대폰</th>
              <td><input type="text" placeholder="휴대폰번호" v-model="searchCondition.user_phone"/></td>
              <th>주문자 이메일</th>
              <td><input type="text" placeholder="이메일" v-model="searchCondition.user_email"/></td>
            </tr>
          </tbody>
        </table>

        <!-- 하단 버튼 -->
        <div class="bottom-buttons">
          <button class="search-btn" @click="searchOrders">검색</button>
        </div>
      </div>
    </transition>

    <!-- 컨트롤 영역 -->
    <div class="rounded-control">
      <!-- 왼쪽: 버튼 그룹 -->
      <div class="left-buttons">
        <button @click="toggleAll">전체선택</button>
        <button @click="selectCancel">선택해제</button>
        <button @click="cancelMultiOrder">선택 주문취소</button>
        <!-- <button>선택엑셀다운</button>
        <button>검색엑셀다운</button> -->
      </div>

      <!-- 오른쪽: select 박스 그룹 -->
      <div class="right-selects">
        <select v-model="sortOption" @change="handleSizeChange">
          <option value="order_date_desc">최신주문일</option>
          <option value="order_date_asc">오래된주문일</option>
          <option value="total_price_desc">최대결제금액</option>
          <option value="total_price_asc">최소결제금액</option>
        </select>
        <select v-model="size" @change="handleSizeChange">
          <option :value="5">5개씩</option>
          <option :value="10">10개씩</option>
          <option :value="20">20개씩</option>
          <option :value="50">50개씩</option>
          <option :value="100">100개씩</option>
        </select>
      </div>
    </div>

    <!-- 테이블 -->
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
        <tbody >
          
          <tr v-for="(order, i) in orders" :key="order.order_id">
            <td><input type="checkbox" :value="order.order_id" v-model="selectedOrders" /></td>
            <td>{{ orders.length - i }}</td>
            <td>
              <div class="order-num">{{ order.order_id }}</div>
              <div class="order-user">{{ order.user_name }}({{ order.user_id }})</div>
            </td>
            <td>
              <div class="product" v-for="(p, idx) in order.orderItems" :key="idx">
                <img :src="p.item_image_url" />
                <div>
                  <div class="product-name">{{ p.item_name }}</div>
                  <div class="product-device">{{ p.quantity }}개</div>
                </div>
              </div>
            </td>
            
            <td>
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
        <!-- 이전 페이지 -->
        <button class="btn-main" 
        @click="goToPage(pageResponse.startPage - 1)" 
        :disabled="!pageResponse.prev">
         &lt; 
        </button>

        <!-- 페이지 숫자 버튼 -->
        <button
          v-for="page in pageNumbers"
          :key="page"
          @click="goToPage(page)"
          :class="['btn-main', { active: page === pageResponse.pageNo }]"
          >
        {{ page }}
        </button>
        <button class="btn-main" 
        @click="goToPage(pageResponse.endPage + 1)" 
        :disabled="!pageResponse.next">
        &gt;
      </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import axios from 'axios'
import qs from 'qs'

const currentPage = ref(1)
const size = ref(10)
const totalCount = ref(0)
const showDetail = ref(false)

const sortOption = ref('order_date_desc') // 기본값: 최신 주문부터

// 선택 / 전체선택
const selectedOrders = ref([]) // 선택된 주문번호를 저장할 배열
const allSelected = ref(false) // 전체 선택 여부

const orders = ref([])

const searchCondition = reactive({
  searchColumn: '',
  searchValue: '',
  startDate: '',
  endDate: '',
  order_status: [],
  payment_method: [],
  recipient_name: '',
  recipient_phone: '',
  order_address_detail: '',
  user_name: '',
  user_phone: '',
  user_email: '',
})

// <script setup> 안에 추가
const getStatusClass = (status) => {
  switch (status) {
    case '결제완료':
      return 'status-green'
    case '배송준비':
      return 'status-yellow'
    case '배송중':
      return 'status-orange'
    case '배송완료':
      return 'status-blue'
    case '환불요청':
      return 'status-purple'
    case '결제취소':
      return 'status-red'
    case '주문취소':
      return 'status-red'
    case '교환요청':
      return 'status-pink'
    case '반품요청':
      return 'status-brown'
    default:
      return ''
  }
}

const pageResponse = ref(null)

const pageNumbers = computed(() => {
  if (!pageResponse.value) return [] // ✅ null일 때 바로 리턴
  const result = []
  for (let i = pageResponse.value.startPage; i <= pageResponse.value.endPage; i++) {
    result.push(i)
  }
  return result
})

const goToPage = (pageNo) => {
  currentPage.value = pageNo
  searchOrders(pageNo);
}

const selectCancel = () => {
  if(selectedOrders.value.length > 0){
    selectedOrders.value = []
  }
}

const toggleAll = () => {
  allSelected.value = !allSelected.value;
  selectedOrders.value = allSelected.value
    ? orders.value.map(o => o.order_id)
    : [];
}

watch(selectedOrders, (newVal) => {
  allSelected.value = newVal.length === orders.value.length
})

const handleSizeChange = () => {
  currentPage.value = 1 // 페이지는 다시 1페이지로 초기화
  searchOrders()
}

// 검색 실행
const searchOrders = async () => {

  try {
    const response = await axios.get('/api/order/', {
      params: {
        ...searchCondition,
        pageNo: currentPage.value,
        size: size.value,
        sortOption: sortOption.value
      },
      paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' })
      })
    
    
    orders.value = response.data.list
    pageResponse.value = response.data

    const totalPages = Math.ceil(totalCount.value/size.value)

    // 현재 페이지가 전체 페이지 수를 초과하면 이전 페이지로
    if (currentPage.value > totalPages && totalPages > 0) {
      currentPage.value = totalPages;
      await fetchOrders();
      return;
    }
    // ✅ 페이지가 안 꽉 찼는데 다음 페이지 데이터가 남아 있다면 → 다음 페이지 일부 끌어오기
    if (
      orders.value.length < size.value &&
      currentPage.value < totalPages
    ) {
      // 다음 페이지 일부 끌어오자
      const nextRes = await axios.get('/api/order/', {
        params: {
          pageNo: currentPage.value + 1,
          size: size.value
        }
      })
      const nextPageOrders = nextRes.data.content;

      // 현재 페이지에 부족한 수 만큼 끌어와서 채움
      const fillCount = size.value - orders.value.length;
      orders.value = [...orders.value, ...nextPageOrders.slice(0, fillCount)];
      }

      console.log("현재 size:", size.value)
      console.log("현재 currentPage:", currentPage.value)
      console.log("orders length:", orders.value.length)

      console.log("orders.value:", orders.value)
      console.log("첫 번째 주문의 아이템 개수:", orders.value[0]?.orderItems?.length)
    } catch (error) {
    console.error('검색 실패:', error)
    alert('검색 중 오류 발생')
    isAdjusting = false;
  }
}

const cancelOrder = async (order_id) => {
  try {
    await axios.delete(`/api/order/${order_id}`)
    alert('주문이 취소되었습니다.')
  } catch (error) {
    console.error('주문 취소 실패', error)
    alert('주문 취소에 실패했습니다.')
  }
}

const cancelMultiOrder = async () => {
  try {
    await axios.delete("/api/order" , {
      data : selectedOrders.value
    });
    alert('주문이 취소되었습니다.')
  } catch (error) {
    console.error('주문 취소 실패', error)
    alert('주문 취소에 실패했습니다.')
  }
}

onMounted(() => {
  searchOrders()
})

</script>

<style scoped src="@/assets/order/order.css"></style>