<template>
  <div class="register-container">
    <div class="register-box">
      <div class="icon">👤</div>
      <h2>호스트 회원가입</h2>

      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <label>아이디</label>
          <div class="input-inline">
            <input v-model="form.user_id" type="text" required />
            <button type="button" @click="valid">중복확인</button>
          </div>
        </div>

        <div class="form-item">
          <label>비밀번호</label>
          <input v-model="form.password" type="password" required />
        </div>

        <div class="form-item">
          <label>비밀번호 재확인</label>
          <input v-model="password_check" type="password" required />
        </div>

        <div class="form-item">
          <label>이름</label>
          <input v-model="form.name" type="text" required />
        </div>

        <div class="form-item">
          <label>닉네임</label>
          <input v-model="form.nickname" type="text" required />
        </div>

        <div class="form-item">
          <label>이메일</label>
          <input v-model="form.email" type="email" required />
        </div>

        <div class="form-item">
          <label>전화번호</label>
          <input v-model="form.phone" type="text" required />
        </div>

        <div class="form-item">
          <label>우편번호</label>
          <div class="zipcode-wrapper">
            <input v-model="form.zipcode" type="text" readonly />
            <button type="button" @click="searchAddress">주소 검색</button>
          </div>
        </div>

        <div class="form-item">
          <label>주소</label>
          <input v-model="form.address" type="text" readonly />
        </div>

        <div class="form-item">
          <label>상세주소</label>
          <input v-model="form.myaddress" type="text" />
        </div>

        <div class="form-item">
          <label>생년월일</label>
          <input v-model="form.birth_date" type="date" />
        </div>

        <div class="form-item">
          <label>성별</label>
          <select v-model="form.gender">
            <option value="">선택</option>
            <option value="M">남</option>
            <option value="F">여</option>
          </select>
        </div>

        <div class="form-item">
          <label>프로필 이미지 URL</label>
          <div class="input-inline">
            <input v-model="form.profile_img" type="text" />
            <button type="button">첨부하기</button>
          </div>
        </div>

        <div class="form-item">
          <label>사업자등록번호</label>
          <input v-model="form.business_no" type="text" />
        </div>

        <div class="form-item">
          <label>은행명</label>
          <input v-model="form.bank_name" type="text" />
        </div>

        <div class="form-item">
          <label>계좌번호</label>
          <input v-model="form.account_no" type="text" />
        </div>

        <div class="form-item">
          <label>방송 채널명</label>
          <input v-model="form.channel_name" type="text" />
        </div>

        <div class="form-item">
          <label>소개</label>
          <textarea v-model="form.intro" rows="3"></textarea>
        </div>

        
        <div class="form-item checkbox-item">
          <label>
            <input type="checkbox" v-model="form.marketing_agree" 
            true-value="Y" 
            false-value="N" />
            마케팅 수신에 동의합니다
          </label>
        </div>

        <button class="register-btn" type="submit">호스트 가입</button>

        <div class="link-box">
          <router-link to="/login">이미 계정이 있으신가요? 로그인</router-link>
        </div>
      </form>
    </div>
  </div>
</template>
<!-- 안녕 -->
<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  user_id: '',
  password: '',
  name: '',
  nickname: '',
  email: '',
  phone: '',
  zipcode: '',
  address: '',
  myaddress: '',
  birth_date: '',
  gender: '',
  profile_img: '',
  business_no: '',
  bank_name: '',
  account_no: '',
  channel_name: '',
  intro: '',
  marketing_agree: 'N',
})

const password_check = ref('')
const existUserId = ref(true)
const validClicked = ref(false)

const handleRegister = async () => {
  if (!confirm("회원 가입하시겠습니까?")) return;
  if (!validClicked.value) {
    alert("아이디 중복을 검사해주세요")
    return
  }
  if (form.password !== password_check.value) {
    alert("비밀번호가 일치하지 않습니다")
    form.password = ""
    password_check.value = ""
    return
  }

  try {
    const res = await axios.post('/api/host/register', form)
    const json = res.data
    if (json.status === "error") {
      alert(json.errorMessage)
    } else {
      alert('회원가입이 완료되었습니다. 관리자의 승인 후 로그인 가능합니다.')
      router.push('/login')
    }
  } catch (error) {
    alert('회원가입 실패: ' + (error.response?.data?.message || error.message))
  }
}

function valid() {
  if (form.user_id.length === 0) {
    alert("아이디를 입력해주세요")
    return
  }

  validClicked.value = true

  axios.post("/api/host/isExistUserId", { user_id: form.user_id })
    .then(response => {
      const json = response.data
      existUserId.value = json.existUserId
      if (existUserId.value) {
        alert(`[${form.user_id}] 해당 아이디는 사용 불가능합니다.`)
        validClicked.value = false
      } else {
        alert(`[${form.user_id}] 해당 아이디는 사용 가능합니다.`)
      }
    })
}

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
      form.zipcode = data.zonecode
      form.address = data.roadAddress || data.jibunAddress
    }
  }).open()
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f2f2f2;
  min-height: 100vh;
}

.register-box {
  background: white;
  padding: 30px 40px;
  width: 1000px;
  border-radius: 10px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
}

.icon {
  font-size: 40px;
  text-align: center;
  margin-bottom: 10px;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #2c2c2c;
}

.form-item {
  margin-bottom: 15px;
}

.form-item label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

input,
select,
textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.input-inline {
  display: flex;
  gap: 10px;
}

.input-inline input {
  flex: 1;
}

.input-inline button {
  white-space: nowrap;
  padding: 8px 12px;
  background-color: #e7e7e7;
  border: 1px solid #aaa;
  border-radius: 4px;
  cursor: pointer;
}

.input-inline button:hover {
  background-color: #d1d1d1;
}

.register-btn {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  background-color: #2f4fa1;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.register-btn:hover {
  background-color: #223e88;
}

.link-box {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}

.link-box a {
  color: #2f4fa1;
  text-decoration: none;
}

.link-box a:hover {
  text-decoration: underline;
}

.checkbox-item {
  display: flex;
  align-items: center;
}

.checkbox-item label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: normal;
  white-space: nowrap; /* 줄바꿈 방지 */
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
</style>