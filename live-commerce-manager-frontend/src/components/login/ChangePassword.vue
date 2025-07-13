<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <h2>비밀번호 변경</h2>
      <form @submit.prevent="changePassword">
        <input v-model="newPassword" type="password" placeholder="새 비밀번호" required />
        <input v-model="confirmPassword" type="password" placeholder="새 비밀번호 확인" required />
        <button type="submit">비밀번호 변경</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const user_id = route.query.user_id
const newPassword = ref('')
const confirmPassword = ref('')

const changePassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  try {
    await axios.put('/api/login/changePassword', {
      user_id,
      newPassword: newPassword.value
    })
    alert('비밀번호가 성공적으로 변경되었습니다.')
    router.push('/login')
  } catch (e) {
    alert('비밀번호 변경에 실패했습니다.')
  }
}
</script>

<style scoped src="@/assets/loginFindForm.css"></style>