<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const reviews = ref<any[]>([])

const goToReviewList = () => {
  router.push({ name: 'ReviewInquiriesManage', query: { tab: 'review' } })
}

// 후기 5개 가져오기
onMounted(async () => {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')
  try {
    const res = await axios.get('/api/seller/reviews', {
      params: { limit: 5 },
      headers: { Authorization: `Bearer ${token}` }
    })
    reviews.value = (res.data ?? []).slice(0, 5)
  } catch {
    reviews.value = []
  }
})

// 글자수 자르기
function shortText(text: string): string {
  return text.length > 50 ? text.slice(0, 50) + '...' : text
}
</script>

<template>
  <div class="review-card-content">
    <div class="header">
      <h3>🗨️ 최근 상품 반응</h3>
      <button class="more-btn" @click="goToReviewList">더보기</button>
    </div>
    <ul class="review-list">
      <li v-for="review in reviews" :key="review.reviewId">
        <div class="title">{{ shortText(review.content) }}</div>
        <div class="meta">
          {{ review.authorName ?? review.userId }} · {{ review.createdDate?.slice(0, 10) }}
        </div>
      </li>
      <li v-if="reviews.length === 0" class="empty">등록된 후기가 없습니다.</li>
    </ul>
  </div>
</template>

<style scoped>
.review-card-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 12px;
}

.review-card-content .header h3 {
  font-size: 1.5rem; /* 원하는 크기로 조정 (예: 1.2rem, 16px 등) */
  margin: 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.more-btn {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
}

.review-list {
  flex: 1;
  overflow-y: auto;
  list-style: none;
  padding: 0;
  margin: 0;
}

.review-list li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f1f1;
}

.title {
  font-size: 0.95rem;
  color: #222;
  margin-bottom: 4px;
  font-weight: 500;
}

.meta {
  font-size: 0.6rem;
  color: #777;
}

.empty {
  text-align: center;
  color: #888;
  padding: 1rem 0;
}
</style>
