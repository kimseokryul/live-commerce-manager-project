<template>
  <div class="member-list-wrapper">
    <h2 class="section-title">호스트 목록</h2>
    <!-- 검색 섹션 -->
    <div class="rounded-search-bar">
      <select v-model="searchParams.searchColumn" @change="handleSizeChange">
        <option value="">전체</option>
        <option value="user_id">아이디</option>
        <option value="name">이름</option>
        <option value="nickname">닉네임</option>
      </select>
      <input type="text" v-model="searchParams.searchValue" placeholder="검색어 입력" />
      <button class="search-btn" @click="searchMember">🔍</button>
      <select v-model="searchParams.sortOption" @change="handleSizeChange">
          <option value="">선택</option>
          <option value="created_date_desc">최신가입일</option>
          <option value="created_date_asc">오래된가입일</option>
          <option value="name_desc">이름내림차순</option>
          <option value="name_asc">이름오름차순</option>
          <option value="nickname_desc">닉네임내림차순</option>
          <option value="nickname_asc">닉네임오름차순</option>
        </select>
    </div>

    <table class="member-table">
      <thead>
        <tr>
          <th><input type="checkbox" @change="toggleAll" /></th>
          <th>번호</th>
          <th>아이디</th>
          <th>이름</th>
          <th>닉네임</th>
          <th>이메일</th>
          <th>전화번호</th>
          <th>가입날짜</th>
          <th>주소</th>
          <th>블랙리스트 여부</th>
          <th>계정 상태</th>
          <th>권한</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(member, index) in memberlist" :key="member.user_id">
            <td><input type="checkbox" :value="member.user_id" v-model="selectedOrders" /></td>
            <td>{{ index + 1 }}</td>
            <td>
                <router-link :to="`/admin/user-detail/${member.user_id}`">
                    {{ member.user_id }}
                </router-link>
            </td>
            <td>{{ member.name }}</td>
            <td>{{ member.nickname }}</td>
            <td>{{ member.email }}</td>
            <td>{{ member.phone }}</td>
            <td>{{ formatDate(member.created_date) }}</td>
            <td>{{ member.address }}</td>
            <td>{{ member.blacklisted }}</td>
            <td>{{ member.status }}</td>
            <td>{{ member.grade_id }}</td>
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
    <div class="right-selects">
      <button class="btn-blacklist" @click="selectBlackList">
        블랙리스트 선택 설정
      </button>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { onMounted, reactive, ref, toRaw, watch } from 'vue'

const searchParams = reactive(
  {
    pageNo: 1,
    size: 10,
    searchColumn: '',
    searchValue: '',
    sortOption: '',
    user_id: '',
    name: '',
    nickname: '',
    email: '',
    phone: '',
    address: '',
    gender: '',
    created_date: '',
    blacklisted: '',
    status: '',
    grade_id: '',
    filterType: 'host',
  }
)

const memberlist = ref([])
const totalPages = ref(0)
const currentPage = ref(1)

// 선택 / 전체선택
const selectedOrders = ref([]) // 선택된 주문번호를 저장할 배열
const allSelected = ref(false) // 전체 선택 여부

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

const getMemberList = async () => {
  try {
    const response = await axios.get('/api/admin/user-list', {
      params : toRaw(searchParams)
    })  
    memberlist.value = response.data.list
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
  getMemberList(page)
}

const searchMember = () => {
  searchParams.pageNo = 1
  currentPage.value = 1
  getMemberList()
}

const handleSizeChange = () => {
  currentPage.value = 1 // 페이지는 다시 1페이지로 초기화
  getMemberList()
}

const toggleAll = () => {
  allSelected.value = !allSelected.value;
  selectedOrders.value = allSelected.value
    ? memberlist.value.map(m => m.user_id)
    : [];
}

const selectBlackList = async () => {
  if (selectedOrders.value.length === 0) {
    alert("선택된 회원이 없습니다.");
    return;
  }

  const confirmed = window.confirm("선택한 회원들을 블랙리스트로 설정하시겠습니까?");
  if (!confirmed) return;

  try {
    const response = await axios.put("/api/admin/users/blacklist", {
      userIds: selectedOrders.value,
      blacklisted: 'Y'
    });

    alert(response.data.message || "블랙리스트 설정 완료!");
    getMemberList(); // 목록 갱신
    selectedOrders.value = []; // 선택 초기화
  } catch (e) {
    console.error("❌ 블랙리스트 설정 실패:", e);
    alert("오류가 발생했습니다.");
  }
};

watch(selectedOrders, (newVal) => {
  allSelected.value = newVal.length === memberlist.value.length
})

onMounted(() => {
  getMemberList()
})

</script>

<style scoped>
.member-list-wrapper {
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

.member-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  margin-bottom: 20px;
}

.member-table thead,
.member-table th {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #e2e2e2;
  text-align: left;
}

.member-table tr,
.member-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #ffffff;
}

.member-table tbody tr:hover {
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

.right-selects {
  display: flex;
  justify-content: flex-end;
  margin: 16px 0;
}

.btn-blacklist {
  background-color: #ef4444; /* 빨간색 계열 (tailwind 기준 red-500) */
  color: white;
  font-weight: 600;
  padding: 10px 18px;
  font-size: 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-blacklist:hover {
  background-color: #dc2626; /* red-600 */
}

.btn-blacklist:disabled {
  background-color: #fca5a5; /* red-300 */
  cursor: not-allowed;
}
</style>