// import './assets/main.css'
// import './assets/tailwind.css'
import '../init'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

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
  }
)

const app = createApp(App)
app.use(router)

// 라우터 준비 완료 후에 마운트
router.isReady().then(() => {
  app.mount('#app')
})