<template>
  <div class="register-wrapper">
    <!-- 페이지 제목 -->
    <h2 class="title">방송 등록</h2>

    <!-- 양쪽 컬럼 구조 -->
    <div class="form-grid">

      <!-- 왼쪽: 방송 정보 입력 영역 -->
      <div class="form-left">
        <!-- 방송자 ID (readonly) -->
        <div class="form-group">
          <label>방송자</label>
          <input type="text" v-model="broadcast.broadcaster_id" readonly />
        </div>

        <!-- 방송 제목 -->
        <div class="form-group">
          <label>제목</label>
          <input type="text" v-model="broadcast.title" />
        </div>

        <!-- 방송 설명 -->
        <div class="form-group">
          <label>설명</label>
          <textarea v-model="broadcast.description"></textarea>
        </div>

        <!-- 공개 여부 라디오 버튼 -->
        <div class="form-group horizontal">
          <div class="radio-group">
            <label><b>공개 여부</b></label>
            <label><input type="radio" :value="true" v-model="broadcast.is_public" /> 공개</label>
            <label><input type="radio" :value="false" v-model="broadcast.is_public" /> 비공개</label>
          </div>
        </div>

        <!-- 방송 시작/종료 시간 -->
        <div class="form-group horizontal">
          <div class="radio-group2">
            <label>방송 시작 시간<input type="datetime-local" v-model="broadcast.scheduled_start_time" /></label>
            <label>방송 종료 시간<input type="datetime-local" v-model="broadcast.scheduled_end_time" /></label>
          </div>
        </div>

        <!-- OBS 관련 정보 입력 -->
        <div class="form-group">
          <label>OBS 설치된 PC의 IP</label>
          <input type="text" v-model="broadcast.obs_host" placeholder="OBS를 사용할 PC의 IP를 입력해주세요" />
        </div>

        <!-- <div class="form-group">
          <label>OBS Websocket 포트 번호</label>
          <input type="text" v-model="broadcast.obs_port" placeholder="OBS WebSocket을 연결할 포트번호를 입력해주세요" />
        </div> -->

        <div class="form-group" style="position: relative;">
          <label>
            OBS Websocket 포트 번호
            <button @click="showObsPortHelp = true" style="margin-left: 8px; font-size: 0.85em;">도움말</button>
          </label>
          <input
            type="text"
            v-model="broadcast.obs_port"
            placeholder="OBS WebSocket을 연결할 포트번호를 입력해주세요"
          />

          <!-- 모달 (도움말 창) -->
          <div v-if="showObsPortHelp" class="modal-overlay">
            <div class="modal-content">
              <h3>OBS WebSocket 방화벽 포트 설정 방법</h3>
              <p>
                이 포트를 사용자가 접근할 수 있도록 운영체제 방화벽과 클라우드 인스턴스(예: AWS)의 보안 그룹에서 <strong>TCP 4455 포트</strong>를 허용해야 합니다.
              </p>
              <ol>
                <li><strong>Windows 방화벽</strong> <br/>
                  (1) 제어판 → 시스템 및 보안 → Windows Defender 방화벽 → 왼쪽 메뉴에서 고급 설정 클릭 <br/>
                  (2) 왼쪽 탭의 인바운드 규칙 선택 → 오른쪽 탭의 새 규칙 클릭 <br/>
                  (3) 포트 선택 → TCP 선택 + 특정 로컬 포트 입력 : 4455  <br/>
                  (4) 연결 허용 선택 → 프로파일(도메인/개인/공용) 원하는 대로 선택 <br/>
                  (5) 이름 입력 (예 : OBS Websocket 4455) → 완료 </li>
                <li><strong>Linux(UFW)</strong>: <code>sudo ufw allow 4455/tcp</code></li>
                <li><strong>AWS</strong>: EC2 → 보안 그룹 → 인바운드 규칙 추가 (TCP 4455)</li>
              </ol>
              <!-- <img src="/path/to/your/help-image.png" alt="포트 설정 예시" style="max-width: 100%; margin-top: 10px;" /> -->
              <button @click="showObsPortHelp = false" style="margin-top: 10px;">닫기</button>
            </div>
          </div>
        </div>

        <!-- OBS WebSocket 비밀번호 입력 (눈 아이콘으로 가림/보임 전환) -->
        <div class="form-group">
          <label>OBS WebSocket 비밀번호</label>
          <div class="password-wrapper">
            <input
              :type="showPassword ? 'text' : 'password'"
              v-model="broadcast.obs_password"
              placeholder="비밀번호를 입력해주세요"
            />
            <button type="button" @click="togglePassword">
              {{ showPassword ? '🙈' : '👁️' }}
            </button>
          </div>
        </div>

        <!-- nginx 서버 호스트 정보 -->
        <div class="form-group">
          <label>서버 IP 주소</label>
          <input type="text" v-model="broadcast.nginx_host" placeholder="docker 설치된 서버 주소 (192.168.4.206)" />
        </div>
      </div>

      <!-- 오른쪽: 상품 등록 및 썸네일 -->
      <div class="form-right">
        <!-- 상품 검색 -->
        <div class="form-group">
          <label>상품 등록</label>
          <div class="product-register">
            <input type="text" v-model="searchKeyword" @keyup="searchProducts" placeholder="상품명 입력" />
            <button @click="searchProducts">검색</button>
          </div>
        </div>

        <!-- 검색 결과 테이블 -->
        <div v-if="searchResults.length">
          <table class="product-table">
            <thead>
              <tr>
                <th>상품명</th>
                <th>가격</th>
                <th>추가</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in searchResults" :key="product.product_id">
                <td>{{ product.product.name }}</td>
                <td>{{ product.product.price.toLocaleString() }}원</td>
                <td><button @click="addProduct(product)">추가</button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 선택된 상품 테이블 -->
        <div class="form-group">
          <label>선택된 상품</label>
          <table class="product-table">
            <thead>
              <tr>
                <th>이미지</th>
                <th>상품명</th>
                <th>가격</th>
                <th>삭제</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in broadcast.productList" :key="index">
                <td><img :src="getFullImageUrl(item.product.mainImage)" alt="상품" width="50" /></td>
                <td>{{ item.product.name }}</td>
                <td>{{ item.product.price.toLocaleString() }}원</td>
                <td><button @click="removeProduct(index)">❌</button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 썸네일 업로드 -->
        <div class="form-group">
          <label>썸네일 업로드</label>
          <div class="thumbnail-box" @click="$refs.thumbnailInput.click()">
            <img v-if="thumbnailPreview" :src="thumbnailPreview" alt="썸네일" />
            <span v-else>클릭하여 썸네일 업로드</span>
          </div>
          <input type="file" ref="thumbnailInput" @change="handleFileUpload" style="display: none;" />
        </div>
      </div>
    </div>

    <!-- 등록 버튼 -->
    <div class="btn-wrap">
      <button @click="submitForm">방송 등록</button>
    </div>
  </div>
</template>


<script setup>
import { onMounted, reactive, ref, toRaw } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 방송 등록 정보 객체
const broadcast = reactive({
  broadcast_id: '',
  broadcaster_id: '', // 로그인한 사용자 ID
  title: '',
  description: '',
  category_id: '',
  thumbnail_url: '',
  is_public: '', // 공개 여부
  broadcast_status: '',
  scheduled_start_time: '',
  scheduled_end_time: '',
  obs_host: '',
  obs_port: '4455',
  obs_password: '',
  nginx_host: '3.39.101.58',
  productList: [], // 선택된 상품 리스트
})

// 썸네일 미리보기 이미지 URL
const thumbnailPreview = ref('')

// 상품 검색 키워드 및 결과 리스트
const searchKeyword = ref('')
const searchResults = ref([])

// 도움말 팝업 보기/숨기기 토글 상태
const showObsPortHelp = ref(false)

// 로그인 토큰
const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')

// 비밀번호 보기/숨기기 토글 상태
const showPassword = ref(false)

// 공개/비공개 버튼 토글용 함수
const togglePassword = () => {
  showPassword.value = !showPassword.value
}

// 로그인한 사용자 ID 가져오기
const getUserId = await axios.get('/api/host/me', {
  headers: {
    'Authorization' : `Bearer ${token}`
  }
})
broadcast.broadcaster_id = getUserId.data.user_id

// 방송 등록 함수
const submitForm = async () => {
  if (!confirm("방송을 등록하시겠습니까?")) return;

  try {
    const formData = new FormData()

    // 방송 객체 전송 (JSON → Blob)
    formData.append('broadcast', new Blob([JSON.stringify(toRaw(broadcast))], {
      type: 'application/json'
    }))

    // 상품 리스트도 같이 전송
    formData.append('productList', new Blob([JSON.stringify(broadcast.productList)], {
      type: 'application/json'
    }))

    const res = await axios.post('/api/broadcast/register', formData, {
      headers: {
        'Content-Type' : 'multipart/form-data',
        'Authorization': `Bearer ${token}`,
      }
    })

    const responseData = res.data
    Object.assign(broadcast, responseData.broadcast) // broadcast 데이터 덮어쓰기

    if(responseData.status === "error"){
      alert(responseData.error)
    } else {
      alert('방송 등록 완료!')
      const broadcastUrl = `/broadcast/${broadcast.broadcast_id}`
      window.open(broadcastUrl, '_blank', 'width=1500,height=900,resizable=yes')
    }

  } catch (error) {
    alert('방송 등록 실패 ' + (error.response?.data?.message || error.message))
  }
}

// 상품 검색
const searchProducts = async () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) {
    searchResults.value = []
    return
  }

  try {
    const res = await axios.get('/api/broadcast/product/search', {
      params: { keyword }
    })
    searchResults.value = res.data
  } catch (error) {
    console.error("상품 검색 실패: ", error)
    searchResults.value = []
  }
}

// 상품 추가
const addProduct = (product) => {
  const exists = broadcast.productList.some(p => p.product_id === product.product.productId)
  if (!exists) {
    broadcast.productList.push({
      product_id: product.product.productId,
      product: {
        name: product.product.name,
        price: product.product.price,
        mainImage: product.product.mainImage || '',
      }
    })
  }
}

// 선택 상품 삭제
const removeProduct = (index) => {
  broadcast.productList.splice(index, 1)
}

// 썸네일 이미지 파일 업로드
const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return;

  const formData = new FormData()
  formData.append("file", file)

  try {
    const res = await axios.post("/api/broadcast/uploads/thumbnail", formData, {
      headers: {
        'Content-Type' : 'multipart/form-data'
      }
    })
    const { url } = res.data
    thumbnailPreview.value =  `http://3.39.101.58:8081${url}`
    broadcast.thumbnail_url = res.data.url
  } catch(error){
    console.error("썸네일 업로드 실패: ", error)
    alert("썸네일 업로드 실패")
  }
}

// 이미지 URL 전체 경로 생성
function getFullImageUrl(path) {
  // return `http://localhost:8080${path}`
  if (!path) return '/default-image.png';
  return path.startsWith('http') ? path : `http://3.39.101.58:8081${path}`;
}

// 컴포넌트 로드시 초기 상품 검색 실행
onMounted(() => {
  searchProducts()
})
</script>


<style scoped>
/* 전체 레이아웃 */
.register-wrapper {
  font-family: 'Noto Sans KR', sans-serif;
  width: 100%;
  flex: 1;
  padding: 0;
  margin: 0;
  min-height: 100%;
  box-sizing: border-box;
}

.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 32px;
  color: #1f2937;
}

/* 그리드 배치 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
}

/* 공통 폼 그룹 */
.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #374151;
  font-size: 15px;
}

/* 입력 요소 */
input[type="text"],
input[type="datetime-local"],
input[type="file"],
input[type="password"],
textarea,
select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background-color: #fff;
  font-size: 14px;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

input:focus,
textarea:focus,
select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.3);
  outline: none;
}

/* select 화살표 */
select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg fill='none' stroke='%23333' stroke-width='2' viewBox='0 0 24 24'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M19 9l-7 7-7-7'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 16px 16px;
  padding-right: 40px;
}

/* 수평 정렬 그룹 */
.form-group.horizontal {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 버튼 기본 스타일 */
button {
  background-color: #2563eb;
  color: white;
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #1e40af;
}

/* 상품 검색 박스 */
.product-register {
  display: flex;
  gap: 12px;
}

.product-register input {
  flex: 1;
}

/* 테이블 */
.product-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
  background-color: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;
}

.product-table th,
.product-table td {
  padding: 12px 14px;
  text-align: center;
  border-bottom: 1px solid #e5e7eb;
  font-size: 14px;
}

.product-table th {
  background-color: #f3f4f6;
  font-weight: 600;
  color: #374151;
}

.product-table td img {
  width: 50px;
  height: auto;
  object-fit: cover;
}

/* 썸네일 */
.thumbnail-box {
  width: 100%;
  height: 180px;
  border: 2px dashed #cbd5e1;
  border-radius: 8px;
  background-color: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s;
}

.thumbnail-box:hover {
  background-color: #e0f2fe;
  border-color: #3b82f6;
}

.thumbnail-box img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* 공개 여부 라디오 버튼 */
.radio-group {
  display: flex;
  gap: 20px;
  align-items: center;
}

.radio-group2 {
  display: flex;
  gap: 80px;
  align-items: center;
}

.radio-group2 label {
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
}

/* 하단 버튼 정렬 */
.btn-wrap {
  margin-top: 40px;
  text-align: right;
}

.btn-wrap button {
  font-size: 16px;
  padding: 12px 30px;
}

.password-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}
.password-wrapper input {
  flex: 1;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  max-width: 400px;
}
</style>