<template>
  <div>
    <h3>📬 관리자 알림</h3>
    <ul>
      <li v-for="(alert, idx) in alerts" :key="idx">
        <span class="alert-label">{{ alert.type }}</span>
        {{ alert.message }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const alerts = ref([])

onMounted(() => {
  alerts.value = [
    { type: '후기 신고', message: '상품 A에 부적절한 리뷰가 등록되었습니다.' },
    { type: 'CS 문의', message: '회원 user123의 배송 문의가 미응답 상태입니다.' },
    { type: '품절 경고', message: '상품 B의 재고가 5개 이하입니다.' }
  ]
})
</script>

<style scoped>
.alert-label {
  font-weight: bold;
  margin-right: 6px;
  color: #ff4d4f;
}
</style>