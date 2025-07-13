<template>
  <header class="seller-header">
    <h2>Seller Header</h2>
    <div class="header-right">
        <span class="welcome-text">
          <router-link :to="`/admin/user-detail/${userId}`" class="nickname-link">{{ userName }}</router-link>님
        </span>
      <button class="btn" @click="logout">로그아웃</button>
    </div>
  </header>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
// 예시: 외부에서 받은 사용자 정보
const userName = ref('')
const userId = ref('')

const accessToken = ref(
  localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
)

// JWT 유효시간 관리
const remainingTime = ref(0)
let intervalId = null

// JWT 유효시간 관리
const formattedTime = computed(() => {
  const minutes = Math.floor(remainingTime.value / 60)
  const seconds = remainingTime.value % 60
  return `${minutes}분 ${String(seconds).padStart(2, '0')}초`
})

// 만료 시간 설정 (예: 현재로부터 1시간)
const tokenExpireAt = ref(Date.now() + 60 * 60 * 1000)

const updateRemainingTime = () => {
  const now = Date.now()
  const diff = Math.max(0, Math.floor((tokenExpireAt.value - now) / 1000))
  remainingTime.value = diff
}

// 로그인 연장
const extendToken = async () => {
  console.log("🟡 연장 버튼 눌림") // 이거 찍히는지 먼저 확인
  const now = Date.now()
  if (tokenExpireAt.value - now > 60 * 1000) {
    // 만료까지 1분 이상 남아있으면 갱신 안 함
    return
  }

  try {
    // 1. 백엔드의 /api/refresh 엔드포인트에 POST 요청 보냄
    // - body는 필요 없어서 null로 전달
    // - withCredentials: true 설정 → 쿠키에 있는 refreshToken도 같이 전송됨
    const res = await axios.post('/api/refresh', null, {
      withCredentials: true
    })

    // 2. 응답에서 새로운 accessToken과 만료시간(exp) 추출
    // - accessToken: 앞으로 인증에 사용할 새 JWT
    // - expireTime: JWT의 만료 시간 (Unix Timestamp, 초 단위)
    const newAccessToken = res.data.accessToken
    const expireTime = res.data.expireTime

    // 3. 사용자가 "로그인 상태 유지"를 체크했는지 확인
    // - Login.vue에서 rememberMe 체크시 localStorage에 'true'로 저장됨
    // - 이 값을 기준으로 accessToken 저장 위치(local or session) 결정
    const rememberMe = localStorage.getItem('rememberMe') === 'true'

    if (rememberMe) {
      // ▶ 로그인 유지 선택된 경우 → localStorage에 저장
      localStorage.setItem('accessToken', newAccessToken)
      // ▶ sessionStorage에 남아있는 이전 토큰은 제거
      sessionStorage.removeItem('accessToken')
    } else {
      // ▶ 로그인 유지 미선택 시 → sessionStorage에 저장
      sessionStorage.setItem('accessToken', newAccessToken)
      // ▶ localStorage에 남아있는 이전 토큰은 제거
      localStorage.removeItem('accessToken')
    }

    // 4. 프론트엔드에서 만료시간 상태 갱신 (ms 단위로 변환)
    // - UI에서 "남은 시간: 59분 30초" 등의 표시를 위해 사용
    tokenExpireAt.value = expireTime * 1000
    updateRemainingTime()

    // 5. 사용자에게 알림 표시
    alert('로그인 시간이 1시간 연장되었습니다.')

  } catch (err) {
    // 에러 발생 시 (예: refreshToken 만료/위조 등)
    // - 로그인 상태를 초기화하고 재로그인 유도
    alert('시간 연장 실패. 다시 로그인해주세요.')
    logout()
  }
}

// 로그아웃 함수
const logout = async () => {
  try {
    // 서버에 로그아웃 요청 (리프레시 토큰 제거용)
    await axios.post('/api/logout', null, 
    {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      withCredentials: true // ✅ 서버가 쿠키에 저장한 refreshToken 제거하려면 필요
    })
    alert('로그아웃 되었습니다')
    // 로컬/세션스토리지의 액세스 토큰 제거
    localStorage.removeItem('jwt')
    sessionStorage.removeItem('jwt') // ✅ 기억하기 옵션 대비
    // localStorage.removeItem('refreshToken')  // (혹시라도 저장했다면 제거)

    // 로그인 페이지로 이동
    router.push('/login')
  } catch (err) {
    alert('로그아웃 실패')
    console.error(err)
  }
}

const getLoginInfo = async () => {
  try {
    const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
    const res = await axios.get('/api/login/me', {
      headers: { Authorization: `Bearer ${token}`}
    }) // 토큰 자동 포함됨 (인터셉터 또는 axios 설정이 되어 있다면)
    userId.value = res.data.user_id
    userName.value = res.data.nickname
  } catch (e) {
    console.warn('❌ 로그인 정보 가져오기 실패', e)
  }
}

// 타이머 설정
onMounted(() => {
  getLoginInfo()
  // updateRemainingTime()
  // intervalId = setInterval(updateRemainingTime, 1000)
})

onBeforeUnmount(() => {
  if (intervalId) clearInterval(intervalId)
})
</script>

<style scoped>
.seller-header {
  background-color: #2f3247;
  color: white;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome-text {
  font-weight: bold;
}

.token-expire {
  font-size: 0.9rem;
  color: #ddd;
}

.btn {
  background-color: #fff;
  color: #3a3f51;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn:hover {
  background-color: #e0e0e0;
}

.nickname-link {
  color: inherit;
  text-decoration: none;
}
.nickname-link:hover {
  text-decoration: underline;
  color: #007bff;
}
</style>