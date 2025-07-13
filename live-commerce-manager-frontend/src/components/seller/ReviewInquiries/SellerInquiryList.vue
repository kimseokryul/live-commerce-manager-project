<template>
  <div class="inquiry-management">
    <h2>상품 문의 목록</h2>

    <!-- 검색창 -->
    <div class="inquiry-search-bar">
      <input
        type="text"
        v-model="searchKeyword"
        placeholder="상품명, 제목 또는 내용 검색"
        class="search-input"
        @keyup.enter="fetchInquiries"
      />
      <button class="btn-main" @click="fetchInquiries">검색</button>
    </div>

    <!-- 문의 테이블 -->
    <table class="inquiry-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>상품명</th>
          <th>작성자</th>
          <th>문의제목</th>
          <th>문의내용</th>
          <th>공개여부</th>
          <th>답변여부</th>
          <th>등록일자</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(inquiry,index) in pagedInquiries"
          :key="inquiry.qnaId"
        >
          <td>
            {{ totalInquiries - ((currentPage - 1) * pageSize) - index }}
          </td>
          <td>{{ inquiry.productName }}</td>
          <td>{{ inquiry.userId }}</td>
          <td>{{ inquiry.title }}</td>
          <td>
            <RouterLink
              :to="{ name: 'ProductInquiryDetail', params: { productId: inquiry.productId, qnaId: inquiry.qnaId } }"
              class="inquiry-link"
            >
              {{ shortContent(inquiry.content) }}
            </RouterLink>
          </td>
          <td>{{ inquiry.isSecret === 'N' ? '공개' : '비공개' }}</td>
          <td>
            <span :class="['answer-status', inquiry.qnaStatus === 'ANSWERED' ? 'answered' : 'not-answered']">
              {{ inquiry.qnaStatus === 'ANSWERED' ? '답변완료' : '미완료' }}
            </span>
          </td>
          <td>{{ formatDate(inquiry.createdDate) }}</td>
        </tr>
        <tr v-if="pagedInquiries.length === 0">
          <td colspan="7" style="text-align:center;">문의 데이터가 없습니다.</td>
        </tr>
      </tbody>
    </table>

    <!-- 페이지네이션 -->
    <div class="inquiry-table-bottom-bar">
      <div class="pagination">
        <button class="btn-main" @click="prevPage" :disabled="currentPage === 1">이전</button>
        <button
          v-for="page in totalPages"
          :key="page"
          @click="goToPage(page)"
          :class="['btn-main', { active: page === currentPage }]"
        >
          {{ page }}
        </button>
        <button class="btn-main" @click="nextPage" :disabled="currentPage === totalPages">다음</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const inquiries = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalInquiries = computed(() => inquiries.value.length)

const totalPages = computed(() => Math.ceil(inquiries.value.length / pageSize))
const pagedInquiries = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return inquiries.value.slice(start, start + pageSize)
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toISOString().slice(0, 10)
}
function shortContent(content) {
  return content.length > 30 ? content.slice(0, 30) + '...' : content
}

// 문의 목록 조회
async function fetchInquiries() {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt')
  try {
    const res = await axios.get('/api/seller/inquiries', {
      params: { keyword: searchKeyword.value },
      headers: { Authorization: `Bearer ${token}` }
    })
    inquiries.value = res.data ?? []
    currentPage.value = 1
  } catch (e) {
    inquiries.value = []
    console.error('문의 목록 로딩 실패', e)
  }
}

// 페이지 조작
function goToPage(page) {
  currentPage.value = page
}
function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

onMounted(fetchInquiries)
</script>

<style scoped>
.inquiry-management {
  width: 100%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 40px 48px 48px 48px;
  box-sizing: border-box;
}
h2 {
  font-size: 1.5rem;
  margin-bottom: 32px;
  font-weight: 700;
  color: #222;
}
.inquiry-search-bar {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}
.search-input {
  width: 320px;
  height: 40px;
  font-size: 1rem;
  padding: 0 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}
.inquiry-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 0.5rem;
}
.inquiry-table th,
.inquiry-table td {
  border: 1px solid #c7d1e0;
  padding: 0.7rem 0.4rem;
  text-align: center;
  font-size: 1.09rem;
}
.inquiry-table th {
  background: #f7faff;
  font-weight: 600;
}
.inquiry-table td {
  background: #fff;
}
.answer-status {
  display: inline-block;
  padding: 0.22em 1.1em;
  border-radius: 16px;
  font-size: 1.02rem;
  font-weight: 600;
}
.answer-status.answered {
  background: #2563eb;
  color: #fff;
}
.answer-status.not-answered {
  background: #e5e7eb;
  color: #888;
}
.inquiry-table-bottom-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 32px;
}
.pagination {
  display: flex;
  gap: 0.5rem;
}
.btn-main {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 7px;
  font-size: 1.13rem;
  font-weight: 700;
  padding: 0.54rem 1.6rem;
  cursor: pointer;
  height: 40px;
}
.btn-main.active {
  background: #1746a2;
}
.btn-main:disabled {
  background: #e5e7eb;
  color: #b3b9c9;
  cursor: not-allowed;
}
.inquiry-link {
  color: #2563eb;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 500;
}
.inquiry-link:hover {
  color: #1746a2;
}
</style>
