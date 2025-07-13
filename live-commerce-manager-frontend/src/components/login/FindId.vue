<template>
  <div class="outer-wrapper">
    <div class="find-form-wrapper">
      <h2>아이디 찾기</h2>
      <form @submit.prevent="findId">
        <input type="text" v-model="name" placeholder="이름을 입력하세요" required />
        <input type="email" v-model="email" placeholder="이메일을 입력하세요" required />
        <button type="submit">아이디 찾기</button>
      </form>

      <div v-if="foundId" class="result-box">
        <p>회원님의 아이디는</p>
        <p><strong>{{ foundId }}</strong> 입니다.</p>
      </div>

      <div class="link-group">
        <router-link to="/login">로그인</router-link> |
        <router-link to="/login/findPassword"> 비밀번호 찾기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const name = ref('')
const email = ref('')
const foundId = ref('')

const findId = async () => {
  try {
    const res = await axios.get('/api/login/findId', {
      params : {
        name: name.value,
        email: email.value
      }
    })
    foundId.value = res.data.user_Id
  } catch (e) {
    alert('일치하는 정보가 없습니다.')
  }
}
</script>

<style scoped src="@/assets/loginFindForm.css"></style>