<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <h2>아이디 및 이메일 확인</h2>
      <form @submit.prevent="findUser">
        <input v-model="user_id" type="text" placeholder="아이디" required />
        <input v-model="email" type="email" placeholder="이메일" required />
        <button type="submit">아이디 확인</button>
      </form>
      <div class="link-group">
        <router-link to="/login">로그인</router-link> |
        <router-link to="/login/findId"> 아이디 찾기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const user_id = ref('')
const email = ref('')
const router = useRouter()

const findUser = async () => {
  try {
    await axios.get('/api/login/findPassword', {
      params: {
        user_id: user_id.value,
        email: email.value
      }
    })
    router.push({ name: 'ChangePassword', query: { user_id: user_id.value } })
  } catch (e) {
    alert('입력하신 정보가 일치하지 않습니다.')
  }
}
</script>

<style scoped src="@/assets/loginFindForm.css"></style>