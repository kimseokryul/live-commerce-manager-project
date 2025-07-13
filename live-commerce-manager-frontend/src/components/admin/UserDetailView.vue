<template>
  <div class="container">
    <h2 class="page-title">회원 상세정보</h2>

    <div class="card">
      <h3 class="card-title">기본 정보</h3>
      <div class="grid">
        <div class="form-group">
          <label>아이디</label>
          <input type="text" v-model="User.user_id" readonly />
        </div>
        <div class="form-group">
          <label>이름</label>
          <input type="text" v-model="User.name"/>
        </div>
        <!-- <div class="form-group">
          <label>비밀번호(수정시에만 입력)</label>
          <input type="password" value="password" />
        </div>
        <div class="form-group">
          <label>비밀번호 재확인</label>
          <input type="password" />
        </div> -->
        <div class="form-group">
          <label>닉네임</label>
          <input type="text" v-model="User.nickname"/>
        </div>
        <div class="form-group">
          <label>휴대폰</label>
          <input type="text" v-model="User.phone" />
        </div>
        <div class="form-group">
          <label>이메일</label>
          <input type="email" v-model="User.email" />
        </div>
        <div class="form-group">
          <label>회원등급</label>
          <select v-model="User.grade_id">
            <template v-if="User.grade_id==='HOST'">
                <option value="HOST">HOST</option>
            </template>
            <template v-else-if="User.grade_id==='ADMIN'">
                <option value="ADMIN">ADMIN</option>
            </template>

            <template v-else>
                <option value="BRONZE">BRONZE</option>
                <option value="SILVER">SILVER</option>
                <option value="GOLD">GOLD</option>
                <option value="PLATINUM">PLATINUM</option>
                <option value="DIAMOND">DIAMOND</option>
                <option value="VIP">VIP</option>
            </template>
            
          </select>
        </div>
        <div class="form-group">
          <label>성별</label>
          <div class="radio-group">
            <label><input type="radio" value="M" v-model="User.gender"/> 남성</label>
            <label><input type="radio" value="F" v-model="User.gender" /> 여성</label>
            <label><input type="radio" value="N" v-model="User.gender" /> 선택안함</label>
          </div>
        </div>
        <div class="form-group">
          <label>생년월일</label>
          <input type="date" v-model="User.birth_date" />
        </div>
        <div class="form-group">
          <label>마케팅 수신 동의</label>
          <div class="radio-group">
            <label><input type="radio" value="Y" v-model="User.marketing_agree"/> 동의</label>
            <label><input type="radio" value="N" v-model="User.marketing_agree"/> 미동의</label>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <h3 class="card-title">주소 정보</h3>
      <div class="grid">
        <div class="form-group">
          <label>우편번호</label>
          <div class="zipcode-wrapper">
            <input v-model="User.zipcode" type="text" readonly />
            <button type="button" @click="searchAddress">주소 검색</button>
          </div>
        </div>
        <div class="form-group full">
          <label>주소</label>
          <input type="text" v-model="User.address"/>
        </div>
        <div class="form-group full">
          <label>상세 주소</label>
          <input type="text" v-model="User.myaddress" />
        </div>
      </div>
    </div>

    <div class="card" v-if="isAdminViewingOtherUser">
      <h3 class="card-title">계정 상태 정보</h3>
      <div class="grid">
        <!-- ✅ 계정 잠금 여부 (Toggle) -->
        <div class="form-group">
            <label>계정 잠금</label>
            <label class="switch">
                <input type="checkbox" 
                v-model="User.status"
                true-value="N"
                false-value="Y"
                />
                <span class="slider">
                <span class="slider-label on">OFF</span>
                <span class="slider-label off">ON</span>
                
                </span>
            </label>
        </div>
        <div class="form-group">
            <label>블랙리스트 여부</label>
            <label class="switch">
                <input type="checkbox" 
                v-model="User.blacklisted"
                true-value="Y"
                false-value="N"/>
                <span class="slider">
                <span class="slider-label off">ON</span>
                <span class="slider-label on">OFF</span>
                </span>
            </label>
        </div>
        <!-- <div class="form-group">
          <label>승계 여부</label>
          <div class="radio-group">
            <label><input type="radio" /> Y</label>
            <label><input type="radio" /> N</label>
          </div>
        </div> -->
        <div class="form-group">
          <label>탈퇴 여부</label>
          <div class="radio-group">
            <!-- 탈퇴 회원이면 강조된 경고 메시지 표시 -->
            <!-- <div v-if="User.secession_yn === 'Y'" class="secession-label">
                탈퇴 회원입니다.
            </div> -->
            <div v-if="User.secession_yn === 'Y'">
                <button class="force-withdraw-btn" @click="forceUnWithdraw">탈퇴 해제</button>
            </div>

            <!-- 탈퇴 회원이 아니면 강제 탈퇴 버튼 표시 -->
            <template v-else>
                <button class="force-withdraw-btn" @click="forceWithdraw">강제 탈퇴</button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="card" v-if="isMyPage">
      <h3 class="card-title">계정 상태 정보</h3>
        <div class="form-group">
          <label>탈퇴 하기</label>
          <div class="radio-group">
              <button class="force-withdraw-btn" @click="forceWithdraw">회원 탈퇴하기</button>
          </div>
        </div>
    </div>

    <div class="card">
      <h3 class="card-title">기타 정보</h3>
      <table class="info-table">
        <tbody>
          <tr><th>가입일</th><td>{{ User.created_date }}</td></tr>
          <tr><th>수정일</th><td>{{ User.updated_date }}</td></tr>
          <tr><th>최종 로그인</th><td>{{ User.last_login }}</td></tr>
          <tr><th>세션 기준 시간</th><td>{{ User.secession_date }}</td></tr>
          <tr><th>로그인 실패 횟수</th><td>{{ User.login_fail_cnt }}회</td></tr>
          <tr><th>소셜 로그인</th><td>{{ User.social_type }}</td></tr>
          <tr><th>소셜 ID</th><td>{{ User.social_id }}</td></tr>
        </tbody>
      </table>
    </div>

    <div v-if="User.grade_id==='HOST'" class="card">
        <h3 class="card-title">호스트 정보</h3>
        <div class="grid">
            <div class="form-group">
                <label>사업자 등록번호</label>
                <input type="text" v-model="User.business_no"/>
            </div>
            <div class="form-group">
                <label>방송채널명</label>
                <input type="text" v-model="User.channel_name" />
            </div>
            <div class="form-group">
                <label>은행명</label>
                <input type="text" v-model="User.bank_name" />
            </div>
            <div class="form-group">
                <label>계좌번호</label>
                <input type="text" v-model="User.account_no" />
            </div>
        </div>
        <div class="form-group">
          <label>소개</label>
          <input type="textarea" v-model="User.intro" />
        </div>
        <div v-if="isAdminViewingOtherUser" class="form-group">
            <label>관리자 승인 여부</label>
            <label class="switch">
                <input type="checkbox" 
                v-model="User.approved_yn"
                true-value="Y"
                false-value="N"/>
                <span class="slider">
                <span class="slider-label off">ON</span>
                <span class="slider-label on">OFF</span>
                </span>
                <p>현재 값: {{ User.approved_yn }}</p>
            </label>
        </div>
    </div>

    <div class="card">
        <h3 class="card-title">주문 정보</h3>

        <!-- 주문 내역이 없을 때 -->
        <div v-if="!Order || Order.length === 0" class="empty-table">
            구매내역이 없습니다.
        </div>

        <!-- 주문 내역이 있을 때 -->
        <table v-else class="info-table">
            <thead>
            <tr>
                <th>주문번호</th>
                <th>주문일자</th>
                <th>상품명</th>
                <th>결제금액</th>
                <th>결제방법</th>
                <th>주문상태</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="order in paginatedOrders" :key="order.order_id">
                <td>{{ order.order_id }}</td>
                <td>{{ formatDate(order.order_date) }}</td>
                <td>{{ order.product_name }}</td>
                <td>{{ formatPrice(order.total_price) }}</td>
                <td>{{ order.payment_method }}</td>
                <td>{{ order.status }}</td>
            </tr>
            </tbody>
        </table>

        <div class="pagination">
        <!-- 이전 그룹 -->
        <button class="btn-main" 
                :disabled="pageGroupStart <= 1"
                @click="goToPage(pageGroupStart - 1)">«</button>

        <!-- 이전 페이지 -->
        <button class="btn-main" 
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)">‹</button>

        <!-- 페이지 넘버 -->
        <button v-for="page in visiblePages"
                :key="page"
                @click="goToPage(page)"
                :class="['btn-main', { active: currentPage === page }]">
          {{ page }}
        </button>

        <!-- 다음 페이지 -->
        <button class="btn-main"
                :disabled="currentPage === totalPages"
                @click="goToPage(currentPage + 1)">›</button>

        <!-- 다음 그룹 -->
        <button class="btn-main" 
                :disabled="pageGroupEnd >= totalPages"
                @click="goToPage(pageGroupEnd + 1)">»</button>
      </div>
    </div>

    

    <div class="button-area">
      <button class="btn primary" @click="putUserDetail">저장</button>
      <button class="btn" @click="goBack">목록</button>
    </div>
  </div>
</template>

<script setup>

import axios from 'axios'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const User = reactive({
  user_id: '',          // 사용자 ID (PK)
  password: '',         // 비밀번호
  name: '',             // 이름
  email: '',            // 이메일
  phone: '',            // 전화번호
  zipcode: '',          // 우편번호
  address: '',          // 주소
  birth_date: '',       // 생년월일
  gender: '',           // 성별
  succession_yn: '',    // 승계 여부 (Y/N)
  blacklisted: '',      // 블랙리스트 여부 (Y/N)
  created_date: '',     // 생성일
  session_date: '',     // 세션 유지 시간 기준
  login_fail_cnt: 0,    // 로그인 실패 횟수
  status: '',           // 계정 상태 (예: ACTIVE)
  last_login: '',       // 마지막 로그인 시간
  marketing_agree: '',  // 마케팅 수신 동의 여부 (Y/N)
  social_id: '',        // 소셜 로그인 식별자 (카카오, 구글 등)
  marketing_agent: '',  // 마케팅 유입 채널
  grade_id: '',         // 등급 ID
  updated_date: '',     // 회원 정보 수정일
  myaddress: '',        // 상세 주소
  secession_yn: '',     // 탈퇴 여부 (Y/N)
  secession_date: '',   // 탈퇴 처리 날짜
  profile_img: '',      // 프로필 이미지 URL
  social_type: '',      // 소셜 로그인 타입 (예: KAKAO, GOOGLE)
  nickname: '',         // 닉네임
  
  // 호스트 부분
  business_no: '',     // 사업자 등록 번호
  bank_name: '',       // 은행명
  account_no: '',      // 계좌번호
  channel_name: '',    // 라이브 방송 채널명
  intro: '',           // 소개글
  approved_yn: ''      // 관리자 승인 여부 (Y/N)
})

const Order = reactive([])
  // ...추가 주문

const userId = ref('') // 로그인한 사용자 ID
const targetUserId = ref('') // 현재 상세 페이지 대상 ID

const gradeId = ref('')

const getUserDetail = async (targetUserId) => {
  try {
    const res = await axios.get(`/api/user-detail/${targetUserId}`, {
      params: { user_id: targetUserId }
    })
    Object.assign(User, res.data)

  } catch(e) {
    console.error('데이터를 불러오는데 실패하였습니다 : ', e)
    alert('데이터를 불러오는데 실패하였습니다.')
  }
}

const getOrderDetail = async(targetUserId) => {
  try {
    const res = await axios.get(`/api/order/user-detail`, {
      params: { user_id: targetUserId }
    })
    Object.assign(Order, res.data)
  } catch(e) {
    console.error('데이터를 불러오는데 실패하였습니다 : ', e)
    alert('데이터를 불러오는데 실패하였습니다.')
  } 
}

const putUserDetail = async () => {
  try {
    console.log('보내는 값:', User.approved_yn); // ✅ 콘솔로 확인
    await axios.put('/api/user-detail', User)
    alert('회원 정보를 수정하였습니다!')
    const filterType = getFilterTypeFromUser(User)

    if (filterType === 'locked') {
        router.push({ name: 'LockedUserList' })
    } else if (filterType === 'withdrawn') {
        router.push({ name: 'WidthdrawnUserList' })
    } else if (filterType === 'host') {
        router.push({ name: 'HostList' })
    } else {
        router.push({ name: 'UserList' })
    }
  } catch(e) {
    console.error('데이터를 저장하는데 실패하였습니다 : ', e)
    alert('데이터를 저장하는데 실패하였습니다.')
  }
}

function formatDate(dateStr) {
  const d = new Date(dateStr)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

function formatPrice(price) {
  return new Intl.NumberFormat('ko-KR').format(price) + '원'
}

function getFilterTypeFromUser(user) {
  if (user.secession_yn === 'Y') return 'withdrawn'
  if (user.blacklisted === 'Y' || user.status === 'N' && user.secession_yn === 'N') return 'locked'
  if (user.grade_id === 'HOST') return 'host'
  return 'normal'
}

const forceWithdraw = async () => {
  const userId = route.params.user_id
  if (!userId) {
    alert('유저 정보가 올바르지 않습니다.')
    return
  }

  const confirm1 = window.confirm('정말로 탈퇴시키겠습니까?')
  if (!confirm1) return

  const confirm2 = window.confirm('이 작업은 되돌릴 수 없습니다. 진행하시겠습니까?')
  if (!confirm2) return

  try {
    const res = await axios.put(`/api/admin/user/secession/${userId}`, null, {
      params: { secession_yn: 'Y' }
    })
    alert(res.data || '탈퇴 처리 완료되었습니다.')
     // ✅ 사용자 정보 다시 불러오기
    // await getUserDetail()
  } catch (e) {
    console.error('❌ 탈퇴 실패:', e)
    alert('탈퇴 처리 중 오류가 발생했습니다.')
  }
}

const forceUnWithdraw = async () => {
  const userId = route.params.user_id
  if (!userId) {
    alert('유저 정보가 올바르지 않습니다.')
    return
  }

  const confirm = window.confirm('해당 유저를 탈퇴 해제하시겠습니까?')
  if (!confirm) return


  try {
    const res = await axios.put(`/api/admin/user/secession/${userId}`, null, {
      params: { secession_yn: 'N' }
    })
    alert(res.data || '탈퇴 해제 처리 완료되었습니다.')
    // await getUserDetail()
  } catch (e) {
    console.error('❌ 탈퇴 해제 실패:', e)
    alert('탈퇴 처리 중 오류가 발생했습니다.')
  }
}

const getLoginInfo = async () => {
  try {
    const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
    const res = await axios.get('/api/login/me', {
      headers: { Authorization: `Bearer ${token}`}
    }) // 토큰 자동 포함됨 (인터셉터 또는 axios 설정이 되어 있다면)
    userId.value = res.data.user_id
    gradeId.value = res.data.grade_id
  } catch (e) {
    console.warn('❌ 로그인 정보 가져오기 실패', e)
  }
}

const isAdminViewingOtherUser = computed(() => {
  return gradeId.value === 'ADMIN' && targetUserId.value !== userId.value
})


const isMyPage = computed(() => {
  return targetUserId.value === userId.value
})


const loadDaumPostcodeScript = () => {
  return new Promise((resolve, reject) => {
    if (window.daum && window.daum.Postcode) {
      resolve()
      return
    }
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

const searchAddress = async () => {
  await loadDaumPostcodeScript()
  new window.daum.Postcode({
    oncomplete: function (data) {
      User.zipcode = data.zonecode
      User.address = data.roadAddress || data.jibunAddress
    }
  }).open()
}

const currentPage = ref(1)
const pageSize = 10  // 한 페이지에 보여줄 주문 수

const totalPages = computed(() => {
  return Math.ceil(Order.length / pageSize)
})

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return Order.slice(start, end)
})

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const pageGroupSize = 5  // 한 번에 보여줄 페이지 수

const pageGroupStart = computed(() => {
  return Math.floor((currentPage.value - 1) / pageGroupSize) * pageGroupSize + 1
})

const pageGroupEnd = computed(() => {
  return Math.min(pageGroupStart.value + pageGroupSize - 1, totalPages.value)
})

const visiblePages = computed(() => {
  const pages = []
  for (let i = pageGroupStart.value; i <= pageGroupEnd.value; i++) {
    pages.push(i)
  }
  return pages
})

function goBack() {
  router.back();
}

onMounted(async () => {
  await getLoginInfo() // 로그인한 사용자 ID 세팅됨 (userId.value)

  targetUserId.value = route.params.user_id || route.query.user_id

  if (targetUserId.value) {
    User.user_id = targetUserId.value
    Order.user_id = targetUserId.value
    getUserDetail(targetUserId.value)
    getOrderDetail(targetUserId.value)
  } else {
    alert('user_id가 없습니다.')
  }
})

</script>

<style scoped>
.container {
  font-family: 'Noto Sans KR', sans-serif;
  width: 100%;
  flex: 1;
  padding: 0;
  margin: 0;
  min-height: 100%;
  box-sizing: border-box;
}
.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 32px;
}

.card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  border-left: 4px solid #f7223f;
  padding-left: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px 32px;
}
.form-group {
  display: flex;
  flex-direction: column;
}
.form-group.full {
  grid-column: span 2;
}
.form-group label {
  font-weight: 500;
  margin-bottom: 6px;
}
.form-group input,
.form-group select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
}
.radio-group {
  display: flex;
  gap: 12px;
  margin-top: 6px;
}
.radio-group.small {
  font-size: 0.9rem;
}

.address-box input {
  margin-top: 6px;
}
.zip-search {
  display: flex;
  gap: 8px;
}
.btn-small {
  padding: 6px 12px;
  background-color: #f5f5f5;
  border: 1px solid #aaa;
  border-radius: 6px;
  cursor: pointer;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
}
.info-table th,
.info-table td {
  border: 1px solid #eee;
  padding: 12px;
  text-align: left;
}
.info-table th {
  background-color: #f9f9f9;
  width: 200px;
  font-weight: 600;
}

.empty-table {
  border: 1px dashed #ccc;
  padding: 40px;
  text-align: center;
  color: #aaa;
  font-size: 1rem;
  background-color: #fcfcfc;
  border-radius: 8px;
}

.button-area {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}
.btn {
  padding: 12px 28px;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  transition: 0.2s;
}
.btn:hover {
  background-color: #f2f2f2;
}
.btn.primary {
  background-color: #f7223f;
  color: white;
  border: none;
}
.btn.primary:hover {
  background-color: #d91f38;
}

.info-table {
  width: 100%;
  border-collapse: collapse;
}
.info-table th,
.info-table td {
  border: 1px solid #eee;
  padding: 12px;
  text-align: left;
}
.info-table th {
  background-color: #f9f9f9;
  width: 200px;
  font-weight: 600;
}

.empty-table {
  border: 1px dashed #ccc;
  padding: 40px;
  text-align: center;
  color: #aaa;
  font-size: 1rem;
  background-color: #fcfcfc;
  border-radius: 8px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 100px;     /* ✅ 너비를 더 키움 */
  height: 44px;
}
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}
.slider {
  position: relative;
  display: block;
  width: 100%;
  height: 100%;
  background-color: #ccc;
  border-radius: 999px;
  transition: background-color 0.3s;
}
.slider:before {
  content: "";
  position: absolute;
  width: 50%;
  height: 100%;
  left: 0;
  top: 0;
  background-color: white;
  border-radius: 999px;
  transition: transform 0.3s ease;
  z-index: 2;
}
.slider-label {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  font-size: 13px;
  font-weight: bold;
  color: white;
  z-index: 1;
  pointer-events: none;
}
.slider-label.off {
  left: 12px;
}
.slider-label.on {
  right: 12px;
}

/* ✅ 체크 시 슬라이더 핸들 오른쪽으로 이동 (전체 너비 기준) */
.switch input:checked + .slider {
  background-color: #f7223f;
}
.switch input:checked + .slider:before {
  transform: translateX(100%);
}

.secession-label {
  background-color: #ffe5e5;
  color: #d8000c;
  border: 1px solid #d8000c;
  padding: 10px 16px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 1rem;
  display: inline-block;
  margin-top: 8px;
}

.force-withdraw-btn {
  background-color: #ff3b3b;
  color: white;
  padding: 10px 18px;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  font-size: 1rem;
}
.force-withdraw-btn:hover {
  background-color: #d50000;
}

.zipcode-wrapper {
  display: flex;
  gap: 10px;
}

.zipcode-wrapper input {
  flex: 1;
}

.zipcode-wrapper button {
  padding: 0 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}

.btn-main {
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-main:hover {
  background-color: #1e40af;
}

.btn-main:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

.btn-main.active {
  background-color: #1e3a8a;
  font-weight: bold;
}
</style>