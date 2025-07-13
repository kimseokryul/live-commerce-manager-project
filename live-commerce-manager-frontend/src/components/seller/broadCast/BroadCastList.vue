<template>
  <div class="broadcast-list-wrapper">
    <h2 class="section-title">나의 방송 목록</h2>
    <!-- 검색 섹션 -->
    <div class="rounded-search-bar">
      <input type="text" v-model="searchParams.searchValue" placeholder="검색어 입력" />
      <button class="search-btn" @click="searchBroadcast">🔍</button>
    </div>

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

    <div class="pagination">
      <button class="btn-main"
      :disabled="currentPage === 1" 
      @click="goToPage(currentPage - 1)">이전</button>
      <button
        v-for="page in totalPages"
        :key="page"
        :class="['btn-main', { active: page === currentPage }]"
        @click="goToPage(page)"
      >
        {{ page }}
      </button>

      <button class="btn-main" 
      :disabled="currentPage === totalPages" 
      @click="goToPage(currentPage + 1)">다음</button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, reactive, ref } from 'vue'

const searchParams = reactive(
  {
    pageNo: 1,
    size: 10,
    searchValue: '',
    broadcast_id: '',
    title: '',
    broadcaster_id: '',
    created_at: '',
    total_viewers: '',
    category_id: '',
  }
)

const broadcasts = ref([])
const totalPages = ref(0)
const currentPage = ref(1)

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const broadcastList = async () => {
  try {
    const response = await axios.get('/api/broadcast/list', {
     params: searchParams
    })   
    broadcasts.value = response.data.list
    totalPages.value = response.data.totalPage

  } catch (e) {
      alert('데이터를 불러오는 중 오류 발생')
      console.error(e)
    }
}

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  searchParams.pageNo = page
  broadcastList(page)
}

const searchBroadcast = () => {
  searchParams.pageNo = 1
  currentPage.value = 1
  broadcastList()
}

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