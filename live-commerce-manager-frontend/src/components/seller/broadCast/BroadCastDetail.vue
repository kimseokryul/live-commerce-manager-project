<template>
  <div class="broadcast-detail-wrapper">
    <!-- 영상 & 상품 목록 & 통계 -->
    <div class="top-section">
      <!-- 영상 -->
      <div class="video-box">
        <video controls>
          <source src="http://localhost:8090/live/abc123_720p/index.m3u8" type="application/x-mpegURL" />
          브라우저가 video 태그를 지원하지 않습니다.
        </video>
        <!-- <video
          v-if="broadcast.video_url"
          :src="broadcast.video_url"
          controls
        />
        <p v-else>다시보기 영상이 아직 없습니다.</p> -->

      </div>

      <!-- 우측: 상품 + 통계 -->
      <div class="side-box">
        <!-- 연동 상품 목록 -->
        <div class="product-box">
          <h3>연동 상품 목록</h3>
          <table>
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>가격</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, index) in broadcast.productList" :key="index">
                <td>{{ index + 1 }}</td>
                <td>{{ product.product.name }}</td>
                <td>{{ product.product.price }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 방송 통계 -->
        <div class="stats-box">
          <h3>방송 통계</h3>
          <div class="stat-row">
            <div class="stat-item">
              <div class="stat-label">
                <span class="stat-icon">👥</span>
                동시 시청자 최고치
              </div>
              <div class="stat-value">{{ broadcast.peak_viewers }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">
                <span class="stat-icon">📊</span>
                총 시청자 수
              </div>
              <div class="stat-value">{{ broadcast.total_viewers }}</div>
            </div>
          </div>
        </div>

      </div>
    </div>

    <!-- 방송 기본 정보 -->
    <div class="info-section">
      <div class="info-top-row">
        <div class="info-card">
          <div class="label">제목</div>
          <div class="value">{{ broadcast.title }}</div>
        </div>
        <div class="info-card">
          <div class="label">방송자</div>
          <div class="value">{{ broadcast.broadcaster_id }}</div>
        </div>
        <div class="info-card">
          <div class="label">카테고리</div>
          <div class="value">{{ broadcast.category_name }}</div>
        </div>
      </div>

      <div class="info-bottom-row">
        <div class="description-box">
          <div class="label">설명</div>
          <div class="value">{{ broadcast.description }}</div>
        </div>

        <div class="time-box">
          <div class="info-card">
            <div class="label">방송 시작시간</div>
            <div class="value">{{ broadcast.actual_start_time }}</div>
          </div>
          <div class="info-card">
            <div class="label">방송 종료시간</div>
            <div class="value">{{ broadcast.actual_end_time }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 버튼 -->
    <div class="btn-wrapper">
      <router-link to="/broadcast/list">
        <button class="go-back-btn">목록으로 돌아가기</button>
      </router-link>
      
    </div>
  </div>
</template>

<script setup>

import axios from 'axios'
import { onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const broadcast = reactive({
  broadcast_id: '',
  broadcaster_id: '',
  title: '',
  description: '',
  total_viewers: '',
  peak_viewers: '',
  actual_start_time: '',
  actual_end_time: '',
  category_id: '',
  category_name: '',
  video_url: '',
  productList: [],
})

const getBroadCasts = async () => {
  if(!broadcast.broadcast_id) {
    return
  }
  try {
    const response = await axios.get(`/api/broadcast/detail/${broadcast.broadcast_id}`)
    Object.assign(broadcast, response.data)
    console.log(broadcast)
  } catch (e) {
    alert('데이터를 불러오는데 실패했습니다.')
  }
}

onMounted(() => {
  broadcast.broadcast_id = parseInt(route.params.broadcast_id)
  getBroadCasts()
})

</script>

<style scoped>
.broadcast-detail-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px;
  font-family: 'Segoe UI', sans-serif;
  color: #333;
  font-size: 1.05rem; /* 폰트 전체 약간 키움 */
}

/* 상단: 영상 + 우측 박스 */
.top-section {
  display: flex;
  gap: 36px; /* 간격 더 넓힘 */
}

/* 영상 */
.video-box {
  flex: 2;
}
.video-box video {
  width: 100%;
  height: 500px; /* 영상 높이 증가 */
  border-radius: 16px;
  border: 1px solid #ccc;
  background: #000;
}

/* 우측 영역 */
.side-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

/* 상품 박스 */
.product-box {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 16px;
  padding: 20px;
}
.product-box table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1rem;
}
.product-box th,
.product-box td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: center;
}
.product-box th {
  background-color: #f1f3f5;
}

/* 통계 박스 */
.stats-box {
  background: #eef3fc;
  border-radius: 16px;
  padding: 20px;
  font-size: 1.05rem;
}
.stats-box strong {
  color: #1c4fd7;
  font-size: 1.2rem;
}

/* 방송 정보 */
.info-section {
  margin-top: 36px;
  padding: 28px;
  background: #f9f9f9;
  border-radius: 16px;
  line-height: 2;
  font-size: 1.05rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

/* 버튼 */
.btn-wrapper {
  margin-top: 36px;
  text-align: center;
}
.go-back-btn {
  background: #3c82f4;
  color: white;
  padding: 14px 30px;
  border: none;
  border-radius: 10px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.2s ease;
}
.go-back-btn:hover {
  background: #295ecb;
}

/* 방송 정보 카드형 레이아웃 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.info-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid #e0e0e0;
}

.info-card.wide {
  grid-column: span 2; /* 긴 항목용 */
}

.label {
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.value {
  font-size: 1.05rem;
  color: #222;
  word-break: break-word;
}

.stat-row {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 12px;
}

.stat-item {
  background: white;
  padding: 18px 20px;
  border-radius: 12px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #dde3ec;
}

.stat-label {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 1.05rem;
  color: #444;
}

.stat-icon {
  font-size: 1.5rem;
  margin-right: 10px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #1c4fd7;
}

/* 상단 3개 항목 (제목, 방송자, 카테고리) */
.info-top-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.info-top-row .info-card {
  flex: 1;
  background: #fff;
  padding: 18px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* 하단 영역: 설명 왼쪽, 시간 오른쪽 */
.info-bottom-row {
  display: flex;
  gap: 20px;
}

/* 설명 박스 (왼쪽 넓게) */
.description-box {
  flex: 2;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* 방송 시간 박스 (오른쪽) */
.time-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.time-box .info-card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* 라벨과 값 공통 */
.label {
  font-weight: 600;
  color: #555;
  margin-bottom: 6px;
  font-size: 0.95rem;
}

.value {
  font-size: 1.1rem;
  color: #222;
  word-break: break-word;
}
</style>