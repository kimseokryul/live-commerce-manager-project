<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <!-- 비밀번호 변경 타이틀 -->
      <h2>비밀번호 변경</h2>

      <!-- 비밀번호 변경 입력 폼 -->
      <form @submit.prevent="changePassword">
        <!-- 새 비밀번호 입력 -->
        <input v-model="newPassword" type="password" placeholder="새 비밀번호" required />

        <!-- 새 비밀번호 확인 입력 -->
        <input v-model="confirmPassword" type="password" placeholder="새 비밀번호 확인" required />

        <!-- 제출 버튼 -->
        <button type="submit">비밀번호 변경</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

// 현재 라우트 정보 및 페이지 이동 라우터 설정
const route = useRoute()
const router = useRouter()

// 쿼리 파라미터로 전달된 user_id 추출
const user_id = route.query.user_id

// 비밀번호 입력값 상태 변수
const newPassword = ref('')
const confirmPassword = ref('')

// 비밀번호 변경 처리 함수
const changePassword = async () => {
  // 새 비밀번호와 확인값이 일치하는지 검증
  if (newPassword.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  try {
    // 서버에 비밀번호 변경 요청 (PUT 메서드 사용)
    await axios.put('/api/login/changePassword', {
      user_id,                        // 변경 대상 사용자 ID
      newPassword: newPassword.value // 새 비밀번호
    })

    // 성공 시 알림 후 로그인 페이지로 이동
    alert('비밀번호가 성공적으로 변경되었습니다.')
    router.push('/login')

  } catch (e) {
    // 실패 시 에러 메시지 출력
    alert('비밀번호 변경에 실패했습니다.')
  }
}
</script>

<!-- 외부 스타일 파일 적용 (Scoped: 이 컴포넌트에만 적용됨) -->
<style scoped src="@/assets/login/loginFindForm.css"></style>