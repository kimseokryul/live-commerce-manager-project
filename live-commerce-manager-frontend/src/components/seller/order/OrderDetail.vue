<template>
  <div class="order-detail-page">
    <!-- 주문 상품 테이블 -->
    <section class="product-table-section">
      <h3>전체 주문</h3>
      <table class="product-table">
        <thead>
          <tr>
            <th>No</th>
            <th>진행상태</th>
            <th>주문상품</th>
            <th>주문금액</th>
            <th>배송비</th>
            <th>적립금</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in items" :key="index">
            <td>{{ index + 1 }}</td>
            <td>
              <!-- <div class="badge green">{{ item.item_status }}</div> -->
              <div :class="getStatusClass(item.item_status)">{{ item.item_status }}</div>
            </td>
            <td>
              <img :src="item.item_image_url" class="product-img" />
              {{ item.item_name }}
            </td>
            <td>{{ item.item_total_price }}원</td>
            <td class="green">{{ item.item_delivery_fee }}원</td>
            <td>{{ item.item_point_earned }}원</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 혜택/요약 -->
    <section class="summary-wrapper">
      <div class="invoice-box">
        <h4>주문서 ({{ getOrderDetail.order_id }})</h4>
        <ul>
          <li>상품개수 <span>{{ getOrderDetail.quantity }}개</span></li>
          <li>총 상품금액 <span class="blue">{{ getOrderDetail.total_price }}원</span></li>
          <li>총 배송비 <span class="green">+ {{ getOrderDetail.delivery_fee }}원</span></li>
          <li>할인금액 <span class="orange">- {{ getOrderDetail.discount_amount }}원</span></li>
          <li>합계금액 <span>{{ getOrderDetail.original_total_price }}원</span></li>
          <li>실 결제금액 <span class="red">{{ getOrderDetail.final_payment_amount }}원</span></li>
        </ul>
        <div class="order-date">주문일시 : {{ getOrderDetail.order_date }}</div>
      </div>

    </section>

    <!-- 결제 정보  -->
    <section class="payment-box">
      <h4>결제 정보</h4>
      <div class="button-group">
        <button class="red-btn" @click="cancelOrder(getOrderDetail.order_id)">강제취소 처리하기</button>
        <router-link to="/order"><button class="border-btn">주문목록 바로가기</button></router-link>
      </div>
      <p class="warning">
        ※ 강제취소는 PG사에서 직접 결제를 취소해야 할 경우에만 사용하세요.
      </p>
    </section>

    <!-- 취소 정보 -->
    <section class="info-section">
      <h4>취소 정보</h4>
      <table class="info-table">
        <tbody>
          <tr>
            <th>부분취소/환불액</th>
            <td>0원</td>
            <th>참고사항</th>
            <td rowspan="3">
              부분 취소 요청 및 완료된 주문상품의 환불 금액이 표시됩니다.<br />
              전체 취소시 취소 비용은 포함되지 않습니다.
            </td>
          </tr>
          <tr>
            <th>부분취소시 환불한 적립금</th>
            <td>0원</td>
            <th></th>
          </tr>
          <tr>
            <th>합계금액</th>
            <td>0원</td>
            <th></th>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 주문자 정보 -->
    <section class="info-section">
      <h4>주문자 정보</h4>
      <table class="info-table">
        <tbody>
          <tr>
            <th>아이디</th>
            <td><input type="text" v-model="getOrderDetail.user_id" readonly /></td>
            <th>이름</th>
            <td>{{ getOrderDetail.user_name }}</td>
          </tr>
          <tr>
            <th>휴대폰번호</th>
            <td><input type="text" v-model="getOrderDetail.phone" readonly /></td>
            <th>이메일</th>
            <td><input type="text" v-model="getOrderDetail.email" readonly /></td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 받는 분 정보 -->
    <section class="info-section">
      <h4>받는 분 정보</h4>
      <table class="info-table">
        <tbody>
          <tr>
            <th>이름</th>
            <td><input type="text" v-model="getOrderDetail.recipient_name" /></td>
            <th>휴대폰번호</th>
            <td><input type="text" v-model="getOrderDetail.recipient_phone" /></td>
          </tr>
          <tr>
            <th>배송지 주소</th>
            <td colspan="3">
              <input type="text" v-model="getOrderDetail.order_zipcode" style="width: 100px" />
              <button class="black-btn">주소찾기</button>
              <input type="text" v-model="getOrderDetail.order_address_detail" style="width: 80%" />
              <!-- <input type="text" value="2" style="width: 80%" />
              <input type="text" value="광주 서구 치평동 1132-34" style="width: 80%" /> -->
            </td>
          </tr>
          <tr>
            <th>배송시 유의사항</th>
            <td colspan="3">
              <textarea placeholder="배송시 유의사항" v-model="getOrderDetail.delivery_memo"></textarea>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 메모 -->
    <section class="info-section">
      <h4>관리용 메모</h4>
      <table class="info-table">
        <tbody>
          <tr>
            <th>메모 내용</th>
            <td><textarea placeholder="관리용 메모" v-model="getOrderDetail.order_memo"></textarea></td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 확인 버튼 -->
    <div class="bottom-buttons">
      <button class="red-btn" @click="putOrders">확인</button>
      <router-link to="/order"><button class="border-btn">목록</button></router-link>
    </div>
  </div>
</template>

<script setup>

import { onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import qs from 'qs'
import { useRoute } from 'vue-router'

const route = useRoute()
const items = ref([])

const getOrderDetail = reactive({
  order_id: '',
  user_id: '',
  order_date: '',
  user_name: '',
  phone: '',
  email: '',
  recipient_name: '',
  recipient_phone: '',
  order_zipcode: '',
  order_address_detail: '',
  delivery_memo: '',
  order_memo: '',
  total_price: '',
  delivery_fee: '',
  discount_amount: '',
  original_total_price: '',
  final_payment_amount: '',
  orderItems: [{ 
    quantity: '',
    item_name: '', 
    item_image_url: '',
    item_status: '',
    item_total_price: '',
    item_delivery_fee: '',
    item_point_earned: '',
    },
  ]
})

const getStatusClass = (status) => {
  switch (status) {
    case '결제완료':
      return 'badge green'
    case '배송준비':
      return 'badge yellow'
    case '배송중':
      return 'badge orange'
    case '배송완료':
      return 'badge blue'
    case '주문취소':
      return 'badge red'
    default:
      return ''
  }
}

const getOrders = async () => {
  try{
    const response = await axios.get('/api/order/detail', {
    params: {
      order_id: getOrderDetail.order_id,
      
    },
    // get방식으로 배열을 보내기 위해
    // 배열 파라미터를 ?key=val1&key=val2 형태로 직렬화함
    // paramsSerializer: params => qs.stringify(params, { arrayFormat: 'repeat' })
  })
    const data = response.data

   // 주문 기본 정보 세팅
    getOrderDetail.user_id = data.user_id
    getOrderDetail.order_date = data.order_date
    getOrderDetail.user_name = data.user_name
    getOrderDetail.phone = data.phone
    getOrderDetail.email = data.email
    getOrderDetail.recipient_name = data.recipient_name
    getOrderDetail.recipient_phone = data.recipient_phone
    getOrderDetail.order_zipcode = data.order_zipcode
    getOrderDetail.order_address_detail = data.order_address_detail
    getOrderDetail.delivery_memo = data.delivery_memo
    getOrderDetail.order_memo = data.order_memo
    getOrderDetail.total_price = data.total_price
    getOrderDetail.delivery_fee = data.delivery_fee
    getOrderDetail.discount_amount = data.discount_amount
    getOrderDetail.original_total_price = data.original_total_price
    getOrderDetail.final_payment_amount = data.final_payment_amount

    // 주문 상품 목록 세팅
    items.value = data.orderItems
  } catch (error) {
    console.error('데이터를 불러오는데 실패하였습니다.', error)
    alert('데이터를 불러오는데 실패하였습니다.')
  }
}

const putOrders = async () => {
  console.log("버튼 클릭됨")
  try{
   await axios.put('/api/order/detail', {
      order_id: getOrderDetail.order_id,
      recipient_name: getOrderDetail.recipient_name,
      recipient_phone: getOrderDetail.recipient_phone,
      order_zipcode: getOrderDetail.order_zipcode,
      order_address_detail: getOrderDetail.order_address_detail,
      delivery_memo: getOrderDetail.delivery_memo,
      order_memo: getOrderDetail.order_memo
  })
  alert('수정 완료')
  }catch (error) {
    console.error('데이터를 수정하는데 실패하였습니다.', error)
    alert('데이터를 수정하는데 실패하였습니다.')
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



onMounted(() => {
  getOrderDetail.order_id = route.query.order_id
  getOrders()
})

</script>

<style scoped src="@/assets/order/orderDetail.css"></style>