<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <!-- 타이틀 -->
      <h2>비밀번호 변경 위한 정보 확인</h2>

      <!-- 아이디 + 이메일 입력 폼 -->
      <form @submit.prevent="findUser">
        <!-- 아이디 입력 -->
        <input v-model="user_id" type="text" placeholder="아이디" required />

        <!-- 이메일 입력 -->
        <input v-model="email" type="email" placeholder="이메일" required />

        <!-- 제출 버튼 -->
        <button type="submit">아이디 및 이메일 확인</button>
      </form>

      <!-- 로그인 및 아이디 찾기 페이지로 이동할 수 있는 링크 -->
      <div class="link-group">
        <router-link to="/login">로그인</router-link> |
        <router-link to="/login/findId"> 아이디 찾기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'              // ✅ 반응형 변수 선언용
import { useRouter } from 'vue-router' // ✅ 페이지 이동을 위한 라우터 훅
import axios from 'axios'              // ✅ HTTP 요청용 라이브러리

// 사용자 입력값 상태 변수
const user_id = ref('')               // 아이디 입력값
const email = ref('')                 // 이메일 입력값

// 라우터 객체 (페이지 이동에 사용)
const router = useRouter()

// 아이디 + 이메일 확인 요청 함수
const findUser = async () => {
  try {
    // 백엔드에 GET 요청 전송 (아이디 + 이메일 확인)
    await axios.get('/api/login/findPassword', {
      params: {
        user_id: user_id.value,
        email: email.value
      }
    })

    // 성공 시 비밀번호 변경 페이지로 이동 (쿼리 파라미터에 user_id 포함)
    router.push({ name: 'ChangePassword', query: { user_id: user_id.value } })

  } catch (e) {
    // 일치하는 정보가 없을 경우 사용자에게 알림 표시
    alert('입력하신 정보가 일치하지 않습니다.')
  }
}
</script>

<!-- 외부 CSS 연결 (Scoped 적용: 해당 컴포넌트에만 영향) -->
<style scoped src="@/assets/login/loginFindForm.css"></style>