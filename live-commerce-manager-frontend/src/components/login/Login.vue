<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="avatar">
          <svg xmlns="http://www.w3.org/2000/svg" class="avatar-icon" viewBox="0 0 16 16" fill="currentColor">
            <path d="M8 9a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>
            <path fill-rule="evenodd" d="M14 13s-1-1.5-6-1.5S2 13 2 13v1h12v-1z"/>
          </svg>
        </div>
        <h2>LOGIN</h2>
      </div>

      <form @submit.prevent="handleLogin">
        <input v-model="user_id" type="text" placeholder="아이디를 입력하세요" required />
        <input v-model="password" type="password" placeholder="비밀번호를 입력하세요" required />

        <div class="options">
          <label><input type="checkbox" v-model="rememberMe" /> 로그인 상태 유지</label>
        </div>
        
        <div>
          <button type="submit">로그인</button>
        </div>

        <div class="option">
          <router-link to="/login/findId">아이디 찾기</router-link>
          <b> | </b>
          <router-link to="/login/findPassword">비밀번호 찾기</router-link>
        </div>

        <p class="footer-text">
          당신도 호스트가 되고 싶나요?
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

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/login', {
      user_id: user_id.value,
      password: password.value,
    }, {
    withCredentials: true,
    headers: {
      'Content-Type': 'application/json'   // 이거 안 넣으면 백엔드는 @RequestBody 못 받음
    }
  })

    const token = response.data.token
    if (rememberMe.value) {
      localStorage.setItem('jwt', token)
      localStorage.setItem('rememberMe', 'true')
    } else {
      sessionStorage.setItem('jwt', token)
      localStorage.removeItem('rememberMe')
    }

    router.push('/')
  } catch (error) {
    alert(error.response?.data?.error || '로그인 실패')
    console.error(error)
  }
}

// Axios 요청마다 Authorization 자동 설정
onMounted(() => {
  // 중복 등록 방지
  if (!axios.interceptors.request.handlers.length) {
    axios.interceptors.request.use(config => {
      // 1. rememberMe 체크
      const remember = localStorage.getItem('rememberMe') === 'true'

      // 2. remember 값에 따라 token 위치 선택
      const token = remember
        ? localStorage.getItem('jwt')       // rememberMe면 localStorage
        : sessionStorage.getItem('jwt')     // 아니면 sessionStorage

      // 3. 토큰이 존재하면 요청 헤더에 Authorization 추가
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }

      return config
    })
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f4f6f9;
}

.login-box {
  width: 450px;
  background: white;
  border: 1px solid #ddd;
  padding: 2rem;
  border-radius: 6px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.05);
  text-align: center;
}

.login-header .avatar {
  font-size: 48px;
  color: #3b5998;
  margin-bottom: 0.5rem;
}

.login-header .avatar-icon {
  width: 48px;
  height: 48px;
}

.login-header h2 {
  margin-bottom: 1.5rem;
  color: #333;
  font-weight: 500;
}

form input[type="text"],
form input[type="password"] {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box; 
  background-color: #fff;
}

/* form input[type="text"],
form input[type="password"],
form button{
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
} */

/*form input[type="text"],
form input[type="password"],
form button {
  width: 100%;
  box-sizing: border-box;
} */

form .options {
  display: flex;
  justify-content: space-between;
  /* justify-content: center; */
  align-items: center;
  font-size: 13px;
  margin-bottom: 1rem;
}

form .options b {
  justify-content: center;
  text-decoration: none;
  color: #3b5998;
}

form button {
  /* all: unset; ❌ 빼버리고 아래처럼 직접 초기화 */
  appearance: none;
  border: none;
  outline: none;
  background: none;

  display: block;
  width: 100%;
  box-sizing: border-box; /* ✅ 이거 꼭 넣어 */
  background-color: #3b5998;
  color: white;
  padding: 10px;
  border-radius: 4px;
  font-size: 15px;
  text-align: center;
  cursor: pointer;
  margin-bottom: 1rem;
}

form button:hover {
  background-color: #2d4373;
}

.footer-text {
  margin-top: 1rem;
  font-size: 13px;
}

.footer-text a {
  color: #3b5998;
  text-decoration: none;
  font-weight: 500;
}
</style>
<!-- 안녕 -->