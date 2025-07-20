// import './assets/main.css'
// import './assets/tailwind.css'
import '../init'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// import 'bootstrap/dist/css/bootstrap.min.css'
// import 'bootstrap-icons/font/bootstrap-icons.css'
import '@toast-ui/editor/dist/toastui-editor.css'
import '@/assets/main.css'
import axios from 'axios'

// ✅ 응답 인터셉터 추가
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      alert('⏰ 세션이 만료되었습니다. 다시 로그인해주세요.')

      // Access Token 삭제
      sessionStorage.removeItem('jwt')
      localStorage.removeItem('jwt')

      // 로그인 페이지로 이동
      router.push('/login')
    }
    return Promise.reject(error)
    // 하하
  }
)

// 모든 Axios 요청 전에 실행되는 요청 인터셉터 설정
axios.interceptors.request.use(config => {
  // 세션스토리지 또는 로컬스토리지에서 JWT 토큰 꺼내기
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')

  // 토큰이 존재하면 요청 헤더에 Authorization 필드 추가
  // 예: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  // 수정된 config 객체(헤더 포함)를 반환 → 실제 요청에 적용됨
  return config

// 요청 설정 중 오류가 발생한 경우 처리
}, error => {
  return Promise.reject(error)  // 에러를 그대로 다음 핸들러로 전달
})

const app = createApp(App)
app.use(createPinia())
app.use(router)

// 라우터 준비 완료 후에 마운트
router.isReady().then(() => {
  app.mount('#app')
})