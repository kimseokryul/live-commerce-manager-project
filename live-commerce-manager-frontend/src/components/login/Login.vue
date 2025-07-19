<template>
  <div class="login-container">
    <div class="login-box">
      
      <div class="login-header">
        <!-- 로고 추가 -->
        <img src="/src/assets/TriMarketAdmin-black.png" alt="TriMarket 로고" class="logo-img" />
      </div>

      <!-- 로그인 -->
      <form @submit.prevent="handleLogin">
        <input v-model="user_id" type="text" placeholder="아이디를 입력하세요" required />
        <input v-model="password" type="password" placeholder="비밀번호를 입력하세요" required />

        <!-- 
          로그인 상태 유지 
          * 체크 시 : 로컬스토리지 (창 닫을 시 로그인 유지)에 토큰 저장
          * 체크 해제 시 : 세션스토리지 (창 닫을시 로그인 해제)에 토큰 저장 
        -->
        <div class="options">
          <label><input type="checkbox" v-model="rememberMe" /> 로그인 상태 유지</label>
        </div>
        
        <div>
          <button type="submit">로그인</button>
        </div>

        <div class="option">
          <!-- 아이디 찾기 -->
          <router-link to="/login/findId">아이디 찾기</router-link>
          <b> | </b>
          <!-- 비밀번호 찾기 -->
          <router-link to="/login/findPassword">비밀번호 찾기</router-link>
        </div>

        <p class="footer-text">
          당신도 호스트가 되고 싶나요?
          <!-- 회원가입 -->
          <router-link to="/host/register">호스트 가입</router-link>
        </p>
      </form>
      
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const user_id = ref('')
const password = ref('')
const rememberMe = ref(false)
const router = useRouter()

// 로그인 로직
const handleLogin = async () => {
  try {
    // 1. 백엔드 컨트롤러의 /login으로 로그인 정보 전송 (user_id, password)
    const response = await axios.post('/api/login', {
      user_id: user_id.value,
      password: password.value,
    }, {
      withCredentials: true, // 쿠키 전송 시 필요 (CSRF 보호나 세션 인증 시 사용 가능)
      headers: {
        'Content-Type': 'application/json'  // 이거 안 넣으면 백엔드는 @RequestBody 못 받음
      }
    })

    // 2. 백엔드에서 JWT 토큰 발급받음
    const token = response.data.token

    // 3. 로그인 상태 유지 여부에 따라 저장소 선택
    if (rememberMe.value) {
      // 체크된 경우 → localStorage에 저장 (브라우저 꺼도 유지됨)
      localStorage.setItem('jwt', token)
      localStorage.setItem('rememberMe', 'true')
    } else {
      // 체크 해제된 경우 → sessionStorage에 저장 (브라우저 종료 시 사라짐)
      sessionStorage.setItem('jwt', token)
      localStorage.removeItem('rememberMe')
    }

    // 4. 로그인한 회원의 정보 조회 (토큰 인증 필요)
    const gradeRes = await axios.get('/api/login/me', {
      headers: {
        Authorization: `Bearer ${token}`  // ✅ 이거 안 넣으면 401 뜸
      }
    })

    // 5. 등급에 따라 라우팅 분기
    if (gradeRes.data.grade_id === 'ADMIN') {
      // 관리자 등급 → 관리자 페이지로 이동
      router.push('/admin')
    } else {
      // 호스트 등급 또는 기타 → 기본 홈으로 이동
      router.push('/')
    }

  } catch (error) {
    // 에러 응답 메시지 출력 (백엔드에서 내려준 메시지 우선)
    alert(error.response?.data?.error || '로그인 실패')
    console.error(error)
  }
}

// Axios 요청마다 Authorization 헤더 자동 설정 (로그인 유지용)
onMounted(() => {
  // 1. 기존 인터셉터가 없을 때만 등록 (중복 요청 방지)
  if (!axios.interceptors.request.handlers.length) {
    axios.interceptors.request.use(config => {

      // 2. '로그인 상태 유지' 여부 확인
      const remember = localStorage.getItem('rememberMe') === 'true'

      // 3. remember 값에 따라 토큰 위치 선택
      // - true: localStorage (브라우저 종료해도 유지)
      // - false: sessionStorage (브라우저 종료 시 삭제됨)
      const token = remember
        ? localStorage.getItem('jwt')       // rememberMe면 localStorage
        : sessionStorage.getItem('jwt')     // 아니면 sessionStorage

      // 4. 토큰이 있으면 Authorization 헤더에 추가
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }

      // 5. 변경된 config 반환
      return config
    })
  }
})
</script>

<style scoped src="@/assets/login/login.css"></style>