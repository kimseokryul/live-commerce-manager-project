<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <!-- 타이틀 -->
      <h2>아이디 찾기</h2>

      <!-- 아이디 찾기 폼 (submit 시 findId 함수 호출) -->
      <form @submit.prevent="findId">
        <!-- 이름 입력 필드 -->
        <input type="text" v-model="name" placeholder="이름을 입력하세요" required />

        <!-- 이메일 입력 필드 -->
        <input type="email" v-model="email" placeholder="이메일을 입력하세요" required />

        <!-- 제출 버튼 -->
        <button type="submit">아이디 찾기</button>
      </form>

      <!-- 아이디 찾기 결과가 있을 경우 결과 표시 -->
      <div v-if="foundId" class="result-box">
        <p>회원님의 아이디는</p>
        <p><strong>{{ foundId }}</strong> 입니다.</p>
      </div>

      <!-- 로그인/비밀번호 찾기 링크 -->
      <div class="link-group">
        <router-link to="/login">로그인</router-link> |
        <router-link to="/login/findPassword"> 비밀번호 찾기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'           // ✅ 반응형 변수 선언용
import axios from 'axios'          // ✅ HTTP 요청 라이브러리

// 입력 필드와 결과를 위한 상태 변수 선언
const name = ref('')               // 사용자 입력 이름
const email = ref('')              // 사용자 입력 이메일
const foundId = ref('')            // 조회된 아이디 결과 저장 변수

// 아이디 찾기 함수
const findId = async () => {
  try {
    // 서버에 GET 요청으로 name, email 전달
    const res = await axios.get('/api/login/findId', {
      params : {
        name: name.value,
        email: email.value
      }
    })

    // 응답 결과에서 아이디 추출하여 화면에 표시
    foundId.value = res.data.userIds

  } catch (e) {
    // 실패 시 사용자에게 알림 표시
    alert('일치하는 정보가 없습니다.')
  }
}
</script>

<!-- 외부 CSS 파일 연결 (Scoped: 해당 컴포넌트에만 적용) -->
<style scoped src="@/assets/login/loginFindForm.css"></style>