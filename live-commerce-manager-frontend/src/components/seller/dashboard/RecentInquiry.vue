<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

interface Inquiry {
  qnaId: number
  productId: number
  title: string
  userId?: string
  createdDate: string
}

const unansweredCount = ref(0)
const inquiries = ref<Inquiry[]>([])

const goToInquiryList = () => {
  router.push({ name: 'ReviewInquiriesManage', query: { tab: 'inquiry' } })
}

const goToDetail = (qnaId: number, productId: number) => {
  if (!qnaId || !productId) {
    console.warn('❌ 잘못된 값으로 인해 이동하지 않음', { qnaId, productId })
    return
  }

  router.push({
    name: 'ProductInquiryDetail',
    params: { productId, qnaId }
  })
}

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  try {
    const res = await axios.get('/api/seller/recentInquiries', {
      headers: { Authorization: `Bearer ${token}` }
    })

    const fullData: Inquiry[] = res.data ?? []

    // 전체 미답변 문의 수
    unansweredCount.value = fullData.length

    // 최신순 5건만 목록에 표시
    inquiries.value = [...fullData]
      .sort((a, b) =>
        new Date(b.createdDate ?? '').getTime() - new Date(a.createdDate ?? '').getTime()
      )
      .slice(0, 5)
  } catch (err) {
    console.error('❌ 미답변 문의 불러오기 실패', err)
    unansweredCount.value = 0
    inquiries.value = []
  }
})
</script>

<template>
  <div class="inquiry-card">
    <div class="header">
      <h3>
  <span class="emoji">📨</span> 미답변 문의
  <strong class="count">{{ unansweredCount }}</strong>건
</h3>
      <button class="more-btn" @click="goToInquiryList()">더보기</button>
    </div>

    <ul class="list">
      <li
        v-for="inq in inquiries"
        :key="inq.qnaId"
        @click="goToDetail(inq.qnaId, inq.productId)"
        class="list-item"
      >
        <div class="title">{{ inq.title }}</div>
        <div class="meta">
          <span class="user">작성자: {{ inq.userId }}</span>
          <span class="date">
            {{ new Date(inq.createdDate).toLocaleDateString('ko-KR', {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit'
            }).replace(/\. /g, '.').replace('.', '').replace(/\.$/, '') }}
          </span>
        </div>
      </li>
    </ul>

    <div v-if="inquiries.length === 0" class="no-data">
      최근 미답변 문의가 없습니다.
    </div>
  </div>
</template>

<style scoped>
.inquiry-card {
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
}

h3 {
  display: flex;
  align-items: center;
  font-size: 1.5rem; /* 크기 증가 */
  font-weight: bold;
  margin: 0;
}

.emoji {
  display: inline-block;
  font-size: 2.5rem; /* 이모지 크기 */
  margin-right: 6px;
  position: relative;
  top: -8px; /* ← 이 값을 조정해서 위로 이동 */
}

.more-btn {
  background: none;
  border: none;
  color: #3b82f6;
  cursor: pointer;
  font-size: 0.9rem;
}

.count {
  margin-left: 0.4rem;
  font-size: 1.5rem;
  color: #3b82f6;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-item {
  padding: 0.7rem 0.6rem;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s ease;
}

.list-item:hover {
  background-color: #f3f4f6;
}

.title {
  font-size: 1.05rem;
  font-weight: 500; /* 이전보다 살짝 줄임 */
  color: #1f2937; /* 진한 회색 (눈에 띄지만 과하지 않게) */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  cursor: pointer;           /* ✅ 손가락 커서로 변경 */
  transition: color 0.2s;     /* ✅ hover 시 자연스럽게 전환 */
}

.title:hover {
  color: #3b82f6;             /* ✅ hover 시 파란색으로 강조 */
  text-decoration: underline; /* ✅ 밑줄로 링크 느낌 강화 */
}

.meta {
  display: flex;
  justify-content: flex-end;
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.2rem;
}

.meta .user {
  margin-right: 0.8rem;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}
</style>
