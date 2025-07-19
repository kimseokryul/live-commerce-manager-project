<template>
  <div class="broadcast-detail-wrapper">
    <!-- 영상 & 상품 목록 & 통계 포함 상단 섹션 -->
    <div class="top-section">

      <!-- 방송 영상 영역 -->
      <div class="video-box">
        <video controls>
          <source src="http://localhost:8090/live/abc123_720p/index.m3u8" type="application/x-mpegURL" />
          브라우저가 video 태그를 지원하지 않습니다.
        </video>
        <!-- 추후 다시보기 영상 출력 가능
        <video
          v-if="broadcast.video_url"
          :src="broadcast.video_url"
          controls
        />
        <p v-else>다시보기 영상이 아직 없습니다.</p> -->
      </div>

      <!-- 연동 상품 목록 + 방송 통계 영역 -->
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

        <!-- 방송 통계 정보 -->
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
        <!-- 방송 설명 -->
        <div class="description-box">
          <div class="label">설명</div>
          <div class="value">{{ broadcast.description }}</div>
        </div>

        <!-- 시작/종료 시간 -->
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

    <!-- 목록으로 돌아가기 버튼 -->
    <div class="btn-wrapper">
      <router-link to="/broadcast/list">
        <button class="go-back-btn">목록으로 돌아가기</button>
      </router-link>
    </div>
  </div>
</template>

<script setup>
// 라이브러리 import
import axios from 'axios'
import { onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'

// 현재 라우트에서 방송 ID 추출
const route = useRoute()

// 방송 상세 정보 객체
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

// 방송 상세 정보 조회 함수
const getBroadCasts = async () => {
  // 방송 ID가 없으면 요청하지 않음
  if (!broadcast.broadcast_id) return

  try {
    const response = await axios.get(`/api/broadcast/detail/${broadcast.broadcast_id}`)
    Object.assign(broadcast, response.data) // 응답 데이터 병합
    console.log(broadcast) // 디버깅용 콘솔 출력
  } catch (e) {
    alert('데이터를 불러오는데 실패했습니다.')
  }
}

// 페이지 진입 시 방송 ID 설정 및 데이터 로드
onMounted(() => {
  broadcast.broadcast_id = parseInt(route.params.broadcast_id)
  getBroadCasts()
})
</script>

<style scoped src="@/assets/broadcast/broadcastDetail.css"></style>
