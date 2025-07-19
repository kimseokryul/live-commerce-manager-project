<template>
  <div class="broadcast-list-wrapper">
    <!-- 방송 목록 타이틀 -->
    <h2 class="section-title">나의 방송 목록</h2>

    <!-- 검색 영역: 검색어 입력 및 검색 버튼 -->
    <div class="rounded-search-bar">
      <input type="text" v-model="searchParams.searchValue" placeholder="검색어 입력" />
      <button class="search-btn" @click="searchBroadcast">🔍</button>
    </div>

    <!-- 방송 목록 테이블 -->
    <table class="broadcast-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>카테고리</th>
          <th>제목</th>
          <th>방송자</th>
          <th>방송일자</th>
          <th>시청자수</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(broadcast, index) in broadcasts" :key="broadcast.broadcast_id">
          <td>{{ index + 1 }}</td>
          <td>{{ broadcast.category_name }}</td>
          <td>
            <!-- 방송 상세 페이지로 이동 -->
            <router-link :to="`/broadcast/detail/${broadcast.broadcast_id}`">
              {{ broadcast.title }}
            </router-link>
          </td>
          <td>{{ broadcast.broadcaster_id }}</td>
          <td>{{ formatDate(broadcast.created_at) }}</td>
          <td>{{ broadcast.total_viewers }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 페이지네이션 -->
    <div class="pagination">
      <button class="btn-main"
              :disabled="currentPage === 1" 
              @click="goToPage(currentPage - 1)">이전</button>

      <button v-for="page in totalPages"
              :key="page"
              :class="['btn-main', { active: page === currentPage }]"
              @click="goToPage(page)">
        {{ page }}
      </button>

      <button class="btn-main" 
              :disabled="currentPage === totalPages" 
              @click="goToPage(currentPage + 1)">다음</button>
    </div>
  </div>
</template>


<script setup>
// 외부 라이브러리 및 Composition API 함수 import
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'

// 검색 조건 및 페이징 관련 상태를 reactive 객체로 관리
const searchParams = reactive({
  pageNo: 1,              // 현재 페이지 번호
  size: 10,               // 페이지 당 항목 수
  searchValue: '',        // 검색어
  broadcast_id: '',       // 방송 ID (사용 안함)
  title: '',              // 방송 제목 (사용 안함)
  broadcaster_id: '',     // 방송자 ID (사용 안함)
  created_at: '',         // 생성일 (사용 안함)
  total_viewers: '',      // 시청자 수 (사용 안함)
  category_id: '',        // 카테고리 ID (사용 안함)
})

// 방송 목록과 페이지네이션 상태 변수
const broadcasts = ref([])        // 방송 목록 배열
const totalPages = ref(0)         // 전체 페이지 수
const currentPage = ref(1)        // 현재 선택된 페이지 번호

// 날짜 포맷 변경 함수: yyyy.mm.dd 형식으로 변경
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 방송 목록 요청 API (검색어, 페이지 포함)
const broadcastList = async () => {
  try {
    const response = await axios.get('/api/broadcast/list', {
      params: searchParams
    })
    // 응답 데이터 바인딩
    broadcasts.value = response.data.list
    totalPages.value = response.data.totalPage
  } catch (e) {
    alert('데이터를 불러오는 중 오류 발생')
    console.error(e)
  }
}

// 페이지 전환 처리
const goToPage = (page) => {
  // 유효 범위 내에서만 페이지 이동 허용
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  searchParams.pageNo = page
  broadcastList()
}

// 검색 버튼 클릭 시 동작 (1페이지부터 새로 조회)
const searchBroadcast = () => {
  searchParams.pageNo = 1
  currentPage.value = 1
  broadcastList()
}

// 페이지 진입 시 방송 목록 조회
onMounted(() => {
  broadcastList()
})
</script>


<style scoped>
.broadcast-list-wrapper {
  font-family: 'Noto Sans KR', sans-serif;
  width: 100%;
  flex: 1;
  padding: 0;
  margin: 0;
  min-height: 100%;
  box-sizing: border-box;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.broadcast-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  margin-bottom: 20px;
}

.broadcast-table thead,
.broadcast-table th {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #e2e2e2;
  text-align: left;
}

.broadcast-table tr,
.broadcast-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #ffffff;
}

.broadcast-table tbody tr:hover {
  background-color: #f9f9f9;
}

.rounded-search-bar {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 16px;
}

.rounded-search-bar input{
  width: 500px;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}


.rounded-search-bar select{
  width: 150px;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
  white-space: nowrap;
}

.rounded-search-bar
.search-btn,
.detail-btn {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
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
</style>