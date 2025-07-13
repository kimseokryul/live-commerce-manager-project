<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SellerHeader from '@/components/layout/SellerHeader.vue'
import SellerSidebar from '@/components/layout/SellerSidebar.vue'
import SellerFooter from '@/components/layout/SellerFooter.vue'

const route = useRoute()

const isEmptyLayoutPage = computed(() => {
  return [
    'Login', 'Register', 'FindId', 'FindPassword',
    'ChangePassword', 'BroadCastStart'
  ].includes(route.name)
})
</script>

<template>
  <div v-if="!isEmptyLayoutPage" class="layout-container">
    <!-- 고정 사이드바 -->
    <SellerSidebar class="sidebar" />

    <!-- 전체 오른쪽 영역 -->
    <div class="main-content">
      <!-- 고정 헤더 -->
      <SellerHeader class="header" />

      <!-- 스크롤 가능한 본문 영역 -->
      <div class="scroll-area">
        <div class="content-wrapper">
          <slot />
        </div>
        <SellerFooter class="footer" />
      </div>
    </div>
  </div>

  <!-- 빈 레이아웃 -->
  <div v-else>
    <slot />
  </div>
</template>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 사이드바 고정 */
.sidebar {
  width: 14%;
  min-width: 200px;
  height: 100vh;
  background-color: #2f3247;
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
}

/* 오른쪽 영역은 사이드바 공간만큼 밀려야 함 */
.main-content {
  margin-left: 14%;
  width: 86%;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 헤더 고정 */
.header {
  height: 80px;
  background-color: #2f3247;
  color: white;
  position: fixed;
  top: 0;
  left: 14%;
  width: 84%;
  z-index: 1000;
}

/* 본문 스크롤 영역 */
.scroll-area {
  margin-top: 80px; /* 헤더 높이만큼 내림 */
  height: calc(100vh - 80px);
  overflow-y: auto;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
}

/* 컨텐츠 내부 */
.content-wrapper {
  padding: 2rem 3rem;
  flex: 1 0 auto;
  box-sizing: border-box;
}

.footer {
  margin-top: auto;
  padding: 1rem 3rem;
  background-color: #eee;
  color: #333;
  font-size: 0.9rem;
}
</style>
