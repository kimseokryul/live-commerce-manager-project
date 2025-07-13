<template>
  <div class="inquiry-management">
    <h2>[{{ productName }}] 상품 문의 목록</h2>
    <table class="inquiry-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>작성자</th>
          <th>문의제목</th>
          <th>문의내용</th>
          <th>등록일자</th>
          <th>공개여부</th>
          <th>답변여부</th>
          <th>답변일자</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(inquiry, index) in pagedInquiries"
          :key="inquiry.id"
        >
          <td>{{ totalElements - ((currentPage - 1) * pageSize) - index }}</td>
          <td>{{ inquiry.author }}</td>
          <td>{{ inquiry.title }}</td>
          <td>
            <!-- router-link 부분 -->
<router-link
  :to="{ name: 'ProductInquiryDetail', params: { productId, qnaId: inquiry.id } }"
  class="inquiry-link"
>
  {{ shortContent(inquiry.content) }}
</router-link>
          </td>
          <td>{{ formatDate(inquiry.date) }}</td>
          <td>{{ inquiry.isPublic ? '공개' : '비공개' }}</td>
          <td>
            <span :class="['answer-status', inquiry.answered ? 'answered' : 'not-answered']">
              {{ inquiry.answered ? '답변완료' : '미완료' }}
            </span>
          </td>
          <td>{{ inquiry.answered ? formatDate(inquiry.answeredAt) : '-' }}</td>
        </tr>
        <tr v-if="pagedInquiries.length === 0">
          <td colspan="7" style="text-align:center;">문의 데이터가 없습니다.</td>
        </tr>
      </tbody>
    </table>
    <div class="inquiry-table-bottom-bar">
      <div class="inquiry-pagination-wrapper">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const totalElements = computed(() => inquiries.value.length);

const productId = Number(route.params.productId); // 라우트 파라미터명에 맞게 수정
const inquiries = ref([]);
const productName = ref('');
const currentPage = ref(1);
const pageSize = 10;

const totalPages = computed(() => Math.ceil(inquiries.value.length / pageSize));

const pagedInquiries = computed(() => {
  const sorted = [...inquiries.value].sort((a, b) =>
    new Date(b.date) - new Date(a.date)
  );
  const start = (currentPage.value - 1) * pageSize;
  return sorted.slice(start, start + pageSize);
});

function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
  return date.toISOString().slice(0, 10);
}
function shortContent(content) {
  return content.length > 30 ? content.slice(0, 30) + '...' : content;
}
function goToPage(page) {
  currentPage.value = page;
}
function prevPage() {
  if (currentPage.value > 1) currentPage.value--;
}
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}

// 문의 데이터 불러오기 (상품코드별)
async function fetchInquiries() {
  try {
    const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');

    const res = await axios.get(`/api/products/${productId}/inquiries`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    inquiries.value = res.data?.map(item => ({
  id: item.qnaId ?? item.QNA_ID,
  author: item.userId ?? item.USER_ID,
  title: item.title ?? item.TITLE,
  content: item.content ?? item.CONTENT,
  date: item.createdDate ?? item.CREATED_DATE,
  isPublic: (item.isSecret ?? item.IS_SECRET) === 'N',
  answered: (item.qnaStatus ?? item.QNA_STATUS) === 'ANSWERED',
  answeredAt: item.answerDate ?? item.ANSWER_DATE
})) ?? [];

    if (inquiries.value.length > 0 && (res.data[0].productName || res.data[0].PRODUCT_NAME)) {
      productName.value = res.data[0].productName ?? res.data[0].PRODUCT_NAME;
    }
  } catch (err) {
    inquiries.value = [];
    productName.value = '';
    console.error('❌ 문의 데이터를 불러오지 못했습니다.', err);
  }
}

onMounted(fetchInquiries);
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
  width: 100%;
  margin: 36px 0 0 0;
}
.inquiry-pagination-wrapper {
  flex: 1 1 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 0;
  width: 100%;
}
.pagination {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  justify-content: center;
  overflow-x: auto;
  white-space: nowrap;
  width: 100%;
  scrollbar-width: thin;
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
  transition: background 0.2s;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin: 0 2px;
}
.btn-main.active {
  background: #1746a2;
  color: #fff;
}
.btn-main:disabled {
  background: #e5e7eb;
  color: #b3b9c9;
  cursor: not-allowed;
}
.btn-main:hover {
  background: #1746a2;
}
.inquiry-link {
  color: #2563eb;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.15s;
}
.inquiry-link:hover {
  color: #1746a2;
}
</style>