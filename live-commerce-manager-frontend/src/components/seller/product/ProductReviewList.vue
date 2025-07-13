<template>
  <div class="review-management">
    <h2>[{{ productName }}] 상품 후기 목록</h2>
    <table class="review-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>작성자</th>
          <th>후기내용</th>
          <th>평점</th>
          <th>포토후기</th>
          <th>도움수</th>
          <th>등록일자</th>
          <th>공개여부</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(review, index) in pagedReviews"
          :key="review.reviewId"
          :class="{ 'row-inactive': review.displayYn !== 'Y' }"
        >
          <td>{{ totalElements - ((currentPage - 1) * pageSize) - index }}</td>
          <td>{{ review.userId }}</td>
          <td>
            <router-link
              :to="{ name: 'ProductReviewDetail', params: { reviewId: review.reviewId } }"
              class="review-link"
            >
              {{ shortContent(review.content) }}
            </router-link>
          </td>
          <td>
            <span class="star-rating">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.rating }">&#9733;</span>
            </span>
          </td>
          <td>
            <span v-if="review.isPhoto === 'Y'" class="photo-badge">포토</span>
            <span v-else class="no-photo">-</span>
          </td>
          <td>
            <span class="helpful-count">{{ review.helpfulCount ?? 0 }}</span>
          </td>
          <td>{{ formatDate(review.createdDate) }}</td>
          <td>
            <label class="switch">
              <input
                type="checkbox"
                :checked="review.displayYn === 'Y'"
                @change="toggleVisibility(review)"
              />
              <span class="slider"></span>
            </label>
          </td>
        </tr>
        <tr v-if="pagedReviews.length === 0">
          <td colspan="8" style="text-align:center;">후기 데이터가 없습니다.</td>
        </tr>
      </tbody>
    </table>

    <div class="review-table-bottom-bar">
      <div class="review-pagination-wrapper">
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
      <div class="review-bottom-btns">
        <button class="btn-main" @click="goToList">목록으로</button>
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
const productId = Number(route.params.productId);

const totalElements = computed(() => reviews.value.length)

const reviews = ref([]);
const productName = ref('');
const currentPage = ref(1);
const pageSize = 10;

const totalPages = computed(() => Math.ceil(reviews.value.length / pageSize));

const pagedReviews = computed(() => {
  const sorted = [...reviews.value].sort((a, b) =>
    new Date(b.createdDate) - new Date(a.createdDate)
  );
  const start = (currentPage.value - 1) * pageSize;
  return sorted.slice(start, start + pageSize);
});

function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toISOString().slice(0, 10);
}
function shortContent(content) {
  return content.length > 30 ? content.slice(0, 30) + '...' : content;
}

// 후기 공개여부 토글
async function toggleVisibility(review) {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');
  const newYn = review.displayYn === 'Y' ? 'N' : 'Y';

  try {
    await axios.put(
      `/api/products/${productId}/reviews/${review.reviewId}/display-yn`,
      { displayYn: newYn },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    review.displayYn = newYn;
  } catch {
    alert('공개여부 변경에 실패했습니다.');
  }
}

function goToList() {
  router.push({ name: 'ProductList' });
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

async function fetchReviews() {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');
  try {
    const res = await axios.get(`/api/products/${productId}/reviews`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    reviews.value = res.data ?? [];
    if (reviews.value.length > 0 && reviews.value[0].productName) {
      productName.value = reviews.value[0].productName;
    }
  } catch {
    alert('후기 목록을 불러오는 데 실패했습니다.');
    reviews.value = [];
    productName.value = '';
  }
}

onMounted(fetchReviews);
</script>

<style scoped>
.review-management {
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
.review-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 0.5rem;
}
.review-table th,
.review-table td {
  border: 1px solid #c7d1e0;
  padding: 0.7rem 0.4rem;
  text-align: center;
  font-size: 1.09rem;
}
.review-table th {
  background: #f7faff;
  font-weight: 600;
}
.review-table td {
  background: #fff;
}
.row-inactive {
  opacity: 0.45;
}
.switch {
  position: relative;
  display: inline-block;
  width: 36px;
  height: 20px;
  vertical-align: middle;
}
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}
.slider {
  position: absolute;
  cursor: pointer;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 20px;
}
.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}
input:checked + .slider {
  background-color: #2563eb;
}
input:checked + .slider:before {
  transform: translateX(16px);
}
.star-rating {
  display: inline-block;
  vertical-align: middle;
}
.star {
  color: #e0e0e0;
  font-size: 1.1em;
}
.star.filled {
  color: #ffd600;
}
.photo-badge {
  display: inline-block;
  background: #e9f5ff;
  color: #2563eb;
  font-size: 0.98em;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 8px;
}
.no-photo {
  color: #bbb;
  font-size: 0.98em;
}
.helpful-count {
  color: #2563eb;
  font-weight: 600;
  font-size: 1.05em;
}
.review-table-bottom-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin: 36px 0 0 0;
}
.review-pagination-wrapper {
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
.review-bottom-btns {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
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
.btn-main.active,
.pagination .btn-main.active {
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
.review-link {
  color: #2563eb;
  text-decoration: underline;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.15s;
}
.review-link:hover {
  color: #1746a2;
}
</style>
