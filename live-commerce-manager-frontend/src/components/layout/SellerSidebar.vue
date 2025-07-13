<template>
  <aside class="seller-sidebar">
    <div class="sidebar-logo">
      <router-link to="/" class="logo-link" style="text-decoration: none; color: inherit;">
        <span style="font-size:2.5rem;font-weight:bold;">LOGO</span>
      </router-link>
    </div>

    <nav>
      <ul class="sidebar-menu">
        <!-- 🟥 관리자 전용 메뉴 -->
        <template v-if= "LoginUser.grade_id === 'ADMIN'">
          <li>
            <button class="sidebar-link sidebar-link-btn" @click="toggleMemberMenu" :aria-expanded="memberMenuOpen">
              <span class="menu-icon">👤</span>
                회원 관리
              <span class="dropdown-arrow" :class="{ open: memberMenuOpen }">▼</span>
            </button>
            <ul v-show="memberMenuOpen" class="submenu">
              <li><router-link :to="{ path: '/admin/user-list', query: { filterType: 'normal' } }" class="submenu-link">일반회원 관리</router-link></li>
              <li><router-link :to="{ path: '/admin/user-list/locked', query: { filterType: 'locked' } }" class="submenu-link">잠긴회원 관리</router-link></li>
              <li><router-link :to="{ path: '/admin/user-list/widthdrawn', query: { filterType: 'widthdrawn' } }" class="submenu-link">탈퇴회원 관리</router-link></li>
              <li><router-link :to="{ path: '/admin/user-list/host', query: { filterType: 'host' } }" class="submenu-link">호스트 관리</router-link></li>
            </ul>
          </li>
          <li><router-link to="/admin/sales" class="sidebar-link"><span class="menu-icon">📊</span>매출 현황</router-link></li>
          <li><router-link to="/admin/board" class="sidebar-link"><span class="menu-icon">📋</span>게시판 관리</router-link></li>
        </template>

        <!-- 🟦 호스트 전용 메뉴 -->
        <template v-else-if="LoginUser.grade_id === 'HOST'">
          <!-- 기존 코드 그대로 유지 -->
          <li>
            <button class="sidebar-link sidebar-link-btn" @click="toggleProductMenu" :aria-expanded="productMenuOpen">
              <span class="menu-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><rect x="3" y="7" width="18" height="13" rx="2" stroke="#fff" stroke-width="2"/><path d="M3 7L12 13L21 7" stroke="#fff" stroke-width="2"/></svg>
              </span>
              상품 관리
              <span class="dropdown-arrow" :class="{ open: productMenuOpen }">▼</span>
            </button>
            <ul v-show="productMenuOpen" class="submenu">
              <li><router-link to="/product" class="submenu-link">조회/수정</router-link></li>
              <li><router-link to="/product/register" class="submenu-link">상품 등록</router-link></li>
              <li><router-link to="/reviews-inquiries" class="submenu-link">후기/문의</router-link></li>
            </ul>
          </li>
          <li><router-link to="/order" class="sidebar-link">
            <span class="menu-icon">
              <!-- 주문 관리: 체크리스트 아이콘 -->
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><rect x="4" y="4" width="16" height="16" rx="2" stroke="#fff" stroke-width="2"/><path d="M8 9H16M8 13H12" stroke="#fff" stroke-width="2"/><circle cx="8" cy="17" r="1" fill="#fff"/></svg>
            </span>
            주문 관리</router-link></li>
          <li><a href="#" class="sidebar-link">
            <span class="menu-icon">
              <!-- 배송 관리: 트럭 아이콘 -->
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><rect x="1" y="5" width="15" height="13" rx="2" stroke="#fff" stroke-width="2"/><path d="M16 8H20L23 12V18C23 19.1046 22.1046 20 21 20H20C18.8954 20 18 19.1046 18 18V17" stroke="#fff" stroke-width="2"/><circle cx="6.5" cy="18.5" r="1.5" fill="#fff"/><circle cx="18.5" cy="18.5" r="1.5" fill="#fff"/></svg>
            </span>배송 관리</a></li>
          <li>
            <button class="sidebar-link sidebar-link-btn" @click="toggleBroadcastMenu" :aria-expanded="broadcastMenuOpen">
              <span class="menu-icon">
                <!-- 방송 관리: 방송(플레이) 아이콘 -->
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="#fff" stroke-width="2"/><polygon points="10,8 16,12 10,16" fill="#fff"/></svg>
              </span>
              방송 관리
              <span class="dropdown-arrow" :class="{ open: broadcastMenuOpen }">▼</span>
            </button>
            <ul v-show="broadcastMenuOpen" class="submenu">
              <li><router-link to="/broadcast/list" class="submenu-link">방송 조회</router-link></li>
              <li><router-link to="/broadcast/register" class="submenu-link">방송 시작</router-link></li>
            </ul>
          </li>
          <li>
            <router-link to="/sellerSales" class="sidebar-link">
              <span class="menu-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
                  <rect x="4" y="10" width="3" height="8" fill="#fff"/>
                  <rect x="10.5" y="6" width="3" height="12" fill="#fff"/>
                  <rect x="17" y="13" width="3" height="5" fill="#fff"/>
                </svg>
              </span>
              매출 현황
            </router-link>
          </li>
        </template>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
import axios from 'axios'
import { onMounted, reactive, ref, watch } from 'vue'

const LoginUser = reactive({
  user_id: '',
  grade_id: '',
})

const productMenuOpen = ref(localStorage.getItem('productMenuOpen') === 'true')
watch(productMenuOpen, v => localStorage.setItem('productMenuOpen', v))
function toggleProductMenu() {
  productMenuOpen.value = !productMenuOpen.value
}

const broadcastMenuOpen = ref(localStorage.getItem('broadcastMenuOpen') === 'true')
watch(broadcastMenuOpen, v => localStorage.setItem('broadcastMenuOpen', v))
function toggleBroadcastMenu() {
  broadcastMenuOpen.value = !broadcastMenuOpen.value
}

const memberMenuOpen = ref(localStorage.getItem('memberMenuOpen') === 'true' || sessionStorage.getItem('memberMenuOpen') === 'true') 
watch(memberMenuOpen, v => localStorage.setItem('memberMenuOpen' || sessionStorage.getItem('memberMenuOpen'), v))
function toggleMemberMenu() {
  memberMenuOpen.value = !memberMenuOpen.value
}

const getMyInfo = async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (token) {
    try {
      const res = await axios.get('/api/login/me', {
        headers: {
          Authorization: `Bearer ${token}`, // 헤더 직접 지정
        },
      });
      LoginUser.user_id = res.data.user_id
      LoginUser.grade_id = res.data.grade_id
    } catch (e) {
      console.warn('❌ 인증 실패. 토큰 만료 또는 무효:', e)
      localStorage.removeItem('jwt') || sessionStorage.removeItem('jwt')
    }
    console.log(LoginUser.grade_id);
  }
}

onMounted(() => {
  getMyInfo()
})

</script>

<style scoped>
.seller-sidebar {
  width: 350px;
  background-color: #2f3247;
  color: white;
  height: 100vh;
  padding: 0 1rem;
  display: flex;
  flex-direction: column;
}
.sidebar-logo {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30px;
  margin-bottom: 2.5rem;
}
.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  margin-top: 24px;
}
.sidebar-menu li {
  margin-bottom: 1.5rem;
  position: relative;
}
.sidebar-link,
.sidebar-link-btn {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  font-size: 1.6rem;
  font-weight: 600;
  padding: 0.5rem 0 0.5rem 2.2rem;
  border-radius: 4px;
  transition: background 0.2s;
  letter-spacing: -1px;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
}
.sidebar-link,
.sidebar-link-btn {
  width: 100%;             /* 사이드바 너비에 맞춤 */
  box-sizing: border-box;  /* 패딩 포함한 너비 계산 */
  padding: 0.5rem 1rem;     /* 좌우 패딩 줄임 (기존: 0.5rem 0 0.5rem 2.2rem) */
  border-radius: 4px;
  background: none;
}
.menu-icon {
  display: flex;
  align-items: center;
  margin-right: 1.1rem;
  min-width: 36px;
}
.dropdown-arrow {
  margin-left: auto;
  font-size: 0.6em;
  transition: transform 0.2s;
  display: flex;
  align-items: center;
}
.dropdown-arrow.open {
  transform: rotate(180deg);
}
.submenu {
  border-radius: 0 0 8px 8px;
  margin-top: 0.1rem;
  margin-left: 3.2rem;
  padding: 0.5rem 0.5rem 0.5rem 1.6rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  position: relative;
  z-index: 2;
}
.submenu-link {
  color: #fff;
  font-size: 1.15rem;
  text-decoration: none;
  display: block;
  padding: 0.38rem 0;
  border-radius: 4px;
  transition: background 0.18s;
  margin-bottom: 0.2rem;
}
.submenu-link:hover {
  background: #3a3f51;
}
.fade-enter-active, .fade-leave-active {
  transition: all 0.18s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
@media (max-width: 600px) {
  .seller-sidebar { width: 100vw; }
  .sidebar-link, .sidebar-link-btn { font-size: 1.2rem; padding-left: 1rem; }
  .menu-icon { min-width: 26px; margin-right: 0.7rem; }
  .submenu { margin-left: 2rem; }
}
</style>
