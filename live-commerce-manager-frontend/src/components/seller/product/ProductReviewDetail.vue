<template>
  <div class="review-detail">
    <!-- 제목 -->
    <div class="review-detail-title-above">[{{ review.productName }}] 후기 상세보기</div>

    <!-- 목록 버튼 -->
    <div class="review-detail-header">
      <button class="btn-main list-btn-top" @click="goToReviewList">
        <span class="icon-arrow">&#8592;</span> 목록
      </button>
    </div>

    <!-- 후기 기본 정보 -->
    <div class="review-info-row">
      <span class="review-id">{{ review.reviewId }}</span>
      <span class="review-author">{{ review.userId }}</span>
      <span class="review-date">{{ formatDate(review.createdDate) }}</span>

      <!-- 별점 표시 -->
      <span class="review-rating-inline">
        <span class="star" v-for="n in 5" :key="n" :class="{ filled: n <= review.rating }">&#9733;</span>
        <span class="rating-number">({{ review.rating }}/5)</span>
      </span>
    </div>

    <!-- 이미지 영역 -->
    <div class="review-image-section">
      <template v-if="review.imageUrls?.length">
        <div class="image-slider">
          <img
            v-for="(img, idx) in review.imageUrls"
            :key="idx"
            :src="getFullImageUrl(img)"
            alt="리뷰 이미지"
            class="review-image"
            @click="openModal(idx)"
          />
        </div>
      </template>
      <template v-else>
        <div class="no-image">
          <span class="no-image-icon">🖼️</span>
          <span class="no-image-text">이미지 없음</span>
        </div>
      </template>
    </div>

    <!-- 이미지 확대 모달 -->
    <div v-if="isModalOpen" class="modal-backdrop" @click="closeModal">
      <img :src="getFullImageUrl(review.imageUrls[modalIndex])" class="modal-image" />
    </div>

    <!-- 내용 카드 -->
    <div class="review-content-card">
      <div class="review-content-title">리뷰 내용</div>
      <div class="review-content-text">{{ review.content }}</div>
    </div>

    <!-- 기타 정보 -->
    <div class="review-meta-info">
      <span>공개 여부: <strong>{{ review.displayYn === 'Y' ? '공개' : '비공개' }}</strong></span>
      <span style="margin-left: 24px;">도움수: <strong>{{ review.helpfulCount ?? 0 }}</strong></span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const productId = Number(route.params.productId);
const reviewId = route.params.reviewId;

const review = ref({
  reviewId: '',
  userId: '',
  productName: '',
  content: '',
  createdDate: '',
  imageUrls: [],
  rating: 0,
  displayYn: '',
  helpfulCount: 0
});

function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toISOString().slice(0, 10);
}

// ✅ 이미지 경로 보정 함수
function getFullImageUrl(path) {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `http://localhost:8080${path}`;
}

async function fetchReview() {
  const token = sessionStorage.getItem('jwt') || localStorage.getItem('jwt');
  try {
    const { data } = await axios.get(
      `/api/products/${productId}/reviews/${reviewId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
    review.value = {
      reviewId: data.reviewId,
      userId: data.userId,
      productName: data.productName,
      content: data.content,
      createdDate: data.createdDate,
      imageUrls: data.imageUrls ?? [],
      rating: data.rating,
      displayYn: data.displayYn,
      helpfulCount: data.helpfulCount
    };

    console.log("📷 리뷰 이미지 URL 목록:", review.value.imageUrls);

  } catch {
    alert('리뷰 정보를 불러오지 못했습니다.');
  }
}

onMounted(fetchReview);

function goToReviewList() {
  router.push({ name: 'ProductReviewList', params: { productId } });
}

// 이미지 모달
const isModalOpen = ref(false);
const modalIndex = ref(0);
function openModal(idx) {
  modalIndex.value = idx;
  isModalOpen.value = true;
}
function closeModal() {
  isModalOpen.value = false;
}
</script>


<style scoped>
.review-detail {
  max-width: 100%;
  margin: 0;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  padding: 32px 16px 24px 16px;
}
.review-detail-title-above {
  text-align: center;
  font-size: 1.35rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 6px;
}
.review-detail-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 10px;
  min-height: 48px;
}
.list-btn-top {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 7px;
  font-size: 1.09rem;
  font-weight: 700;
  padding: 0.54rem 1.4rem;
  cursor: pointer;
  transition: background 0.2s;
}
.list-btn-top:hover {
  background: #1746a2;
}
.icon-arrow {
  font-size: 1.15rem;
}
.review-info-row {
  display: flex;
  gap: 48px;
  align-items: center;
  font-size: 1.2rem;
  font-weight: 600;
  color: #222;
  margin-bottom: 28px;
  padding: 18px 10px 10px 18px;
  background: #f8fafc;
  border-radius: 8px;
}
.review-id::before {
  content: "후기번호 : #";
  color: #2563eb;
  margin-right: 4px;
}
.review-author::before {
  content: "작성자 : ";
  color: #555;
  margin-right: 4px;
}
.review-date::before {
  content: "등록일자 : ";
  color: #888;
  margin-right: 4px;
}
.review-rating {
  text-align: center;
  font-size: 1.4rem;
  margin: 12px 0 20px 0;
}
.star {
  color: #ddd;
}
.star.filled {
  color: #ffd600;
}
.rating-number {
  margin-left: 6px;
  font-size: 1.1rem;
  color: #555;
}
.review-image-section {
  text-align: center;
  margin-bottom: 24px;
}
.image-slider {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}
.review-image {
  max-width: 200px;
  max-height: 180px;
  border-radius: 8px;
  object-fit: cover;
  background: #f4f4f4;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}
.review-image:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.13);
}
.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #aaa;
  font-size: 1.12rem;
  padding: 24px 0;
}
.no-image-icon {
  font-size: 2.2rem;
}
.modal-backdrop {
  position: fixed;
  z-index: 100;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-image {
  max-width: 80vw;
  max-height: 80vh;
  border-radius: 12px;
  background: #fff;
}
.review-content-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px 16px 18px 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}
.review-content-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2563eb;
  margin-bottom: 8px;
}
.review-content-text {
  font-size: 1.2rem;
  color: #222;
  line-height: 1.7;
}
.review-meta-info {
  font-size: 1.1rem;
  color: #444;
  padding: 4px 16px;
  margin-top: 6px;
}
@media (max-width: 640px) {
  .review-detail {
    padding: 10px 2vw;
  }
  .review-info-row {
    gap: 16px;
    font-size: 1rem;
  }
}
</style>
