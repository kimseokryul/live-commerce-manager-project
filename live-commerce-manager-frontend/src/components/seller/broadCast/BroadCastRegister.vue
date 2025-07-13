<template>
  <div class="register-wrapper">
    <h2 class="title">방송 등록</h2>
    <div class="form-grid">

      <!-- 왼쪽 영역 -->
      <div class="form-left">
        <div class="form-group">
          <label>방송자</label>
          <input type="text" v-model="broadcast.broadcaster_id" readonly />
        </div>

        <div class="form-group">
          <label>제목</label>
          <input type="text" v-model="broadcast.title" />
        </div>

        <div class="form-group">
          <label>설명</label>
          <textarea v-model="broadcast.description"></textarea>
        </div>

        <div class="form-group horizontal">
          <div class="radio-group">
            <label><b>공개 여부</b></label>
            <label><input type="radio" :value="true" v-model="broadcast.is_public" /> 공개</label>
            <label><input type="radio" :value="false" v-model="broadcast.is_public" /> 비공개</label>
          </div>
        </div>

        <div class="form-group horizontal">
          <div class="radio-group2">
            <label>방송 시작 시간<input type="datetime-local" v-model="broadcast.scheduled_start_time" /></label>
            <label>방송 종료 시간<input type="datetime-local" v-model="broadcast.scheduled_end_time" /></label>
          </div>
        </div>

        <div class="form-group">
          <label>OBS 설치된 PC의 IP</label>
          <input type="text" v-model="broadcast.obs_host" placeholder="OBS를 사용할 PC의 IP를 입력해주세요" />
        </div>

        <div class="form-group">
          <label>OBS Websocket 포트 번호</label>
          <input type="text" v-model="broadcast.obs_port" placeholder="OBS WebSocket을 연결할 포트번호를 입력해주세요" />
        </div>

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

        <div class="form-group">
          <label>서버 IP 주소</label>
          <input type="text" v-model="broadcast.nginx_host" placeholder="docker 설치된 서버 주소 (192.168.4.206)" />
        </div>
      </div>

      <!-- 오른쪽 영역 -->
      <div class="form-right">
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
                <td><img :src="item.product.mainImage" alt="상품" width="50" /></td>
                <td>{{ item.product.name }}</td>
                <td>{{ item.product.price.toLocaleString() }}원</td>
                <td><button @click="removeProduct(index)">❌</button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- <div class="form-group">
          <label>카테고리</label>
          <select v-model="broadcast.category_id">
            <option disabled value="">선택</option>
            <option value="1">신선식품</option>
            <option value="2">가공식품</option>
            <option value="3">간편식/밀키트</option>
            <option value="4">베이커리</option>
            <option value="5">유제품/음료</option>
            <option value="6">건강식품</option>
            <option value="7">주방용품</option>
            <option value="8">생활용품</option>
            <option value="9">유아동</option>
          </select>
        </div> -->

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

const broadcast = reactive({
  broadcast_id: '',
  broadcaster_id: '',
  title: '',
  description: '',
  category_id: '',
  thumbnail_url: '',
  is_public: '',
  broadcast_status: '',
  scheduled_start_time: '',
  scheduled_end_time: '',
  obs_host: '',
  obs_port: '4455',
  obs_password: '',
  nginx_host: '',
  productList: [],
})


const thumbnailPreview = ref('') // 미리보기용 로컬 URL

const searchKeyword = ref('')
const searchResults = ref([])
const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')

const showPassword = ref(false)


// broadcaster_id 에 로그인된 id 불러오기
const getUserId = await axios.get('/api/host/me', {
  headers: {
    'Authorization' : `Bearer ${token}`
  }
})
broadcast.broadcaster_id = getUserId.data.user_id

// 방송 등록
const submitForm = async () => {
  if (!confirm("방송을 등록하시겠습니까?")) return;

  try{
    const formData = new FormData()

    formData.append('broadcast', new Blob([JSON.stringify(toRaw(broadcast))], {
      type: 'application/json'
    }))
    console.log(toRaw(broadcast))
    formData.append('productList', new Blob([JSON.stringify(broadcast.productList)], {
      type: 'application/json'
    }))

    const res = await axios.post('/api/broadcast/register', formData, {
      headers : {
        'Content-Type' : 'multipart/form-data',
        'Authorization': `Bearer ${token}`,
      }
    })
    const responseData = res.data
    console.log(token)

    Object.assign(broadcast, responseData.broadcast);  // broadcast 전체 덮어쓰기 대신 병합

    if(responseData.status === "error"){
      alert(responseData.error)
    } else {
      alert('방송 등록 완료!')
      // router.push(`/broadcast/{broadcast.broadcast_id}`)
     
      const broadcastUrl = `/broadcast/${broadcast.broadcast_id}`

      console.log("broadcast_id : ", broadcast.broadcast_id)
      console.log("broadcast: ", broadcast) 
      window.open(broadcastUrl, '_blank', 'width=1500,height=900,resizable=yes')
    }

  }catch(error){
    alert('방송 등록 실패 '  + (error.response?.data?.message || error.message))
  }
}

// 상품명 검색
const searchProducts = async () => {
  const keyword = searchKeyword.value.trim()

  if(!keyword){
    searchResults.value = []
    return
  }

  try {
    const res = await axios.get('/api/broadcast/product/search', {
      params: { 
        keyword: keyword
      }
    })
    
    searchResults.value = res.data
    console.log("🔍 searchResults =", searchResults)
  } catch(error) {
    console.error("상품 검색 실패 : ", error)
    searchResults.value = []
  }
}

// 상품 추가
const addProduct = (product) => {
  const exists = broadcast.productList.some(p => p.product_id === product.product.productId)
  console.log("👉 현재 추가할 product_id:", product.product.productId);
  if(!exists){
    broadcast.productList.push({
      product_id: product.product.productId,
      product: {
        name: product.product.name,
        price: product.product.price,
        mainImage: product.product.mainImage || '',
      }
    })
    console.log("선택된 product_id:", product.product_id)
  }
}

// 상품 삭제
const removeProduct = (index) => {
  broadcast.productList.splice(index, 1)
}

// 파일 업로드 메소드
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
    thumbnailPreview.value = `http://localhost:8080${url}`
    broadcast.thumbnail_url = res.data.url // 서버가 url 반환

    console.log('preview URL:', thumbnailPreview.value)
  } catch(error){
    console.error("썸네일 업로드 실패: ", error)
    alert("썸네일 업로드 실패")
  }
}


// 스트림 키 보여주기
const togglePassword = () => {
  showPassword.value = !showPassword.value
}

// 스트림 키 복사
const copyStreamKey = async () => {
  try {
    await navigator.clipboard.writeText(broadcast.stream_key)
    alert('스트림 키 복사 완료!')
  } catch (err) {
    alert('복사 실패')
  }
}

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
</style>