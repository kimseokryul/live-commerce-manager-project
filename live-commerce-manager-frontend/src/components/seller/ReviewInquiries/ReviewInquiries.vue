<template>
  <div class="review-inquiry-page">
    <!-- 탭 영역 -->
    <div class="tab-menu">
      <div
        :class="['tab-item', { active: currentTab === 'review' }]"
        @click="currentTab = 'review'"
      >
        후기 관리
      </div>
      <div
        :class="['tab-item', { active: currentTab === 'inquiry' }]"
        @click="currentTab = 'inquiry'"
      >
        문의 관리
      </div>
    </div>

    <!-- 탭에 따른 컴포넌트 전환 -->
    <div class="tab-content">
      <SellerReviewList v-if="currentTab === 'review'" />
      <SellerInquiryList v-else />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'  // ✅ 추가
import SellerReviewList from './SellerReviewList.vue'
import SellerInquiryList from './SellerInquiryList.vue'

const currentTab = ref('review')
const route = useRoute()

onMounted(() => {
  const tab = route.query.tab
  if (tab === 'review' || tab === 'inquiry') {
    currentTab.value = tab
  }
})
</script>

<style scoped>
.tab-menu {
  display: flex;
  gap: 24px;
  padding: 20px 0;
  border-bottom: 1px solid #ddd;
}
.tab-item {
  font-size: 1.3rem;
  font-weight: bold;
  color: #888;
  padding-bottom: 12px;
  cursor: pointer;
  border-bottom: 3px solid transparent;
}
.tab-item.active {
  color: #2563eb;
  border-bottom: 3px solid #2563eb;
}
.tab-content {
  padding-top: 2rem;
}
</style>
