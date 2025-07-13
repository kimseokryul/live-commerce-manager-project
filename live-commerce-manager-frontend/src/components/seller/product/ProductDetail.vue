<template>
  <ProductDetailLayout
    :tabs="tabs"
    :currentTab="currentTab"
    @update:currentTab="onTabClick"
  >
    <template #default>
      <!-- 자식 라우트 컴포넌트 렌더링 -->
      <router-view />
    </template>
  </ProductDetailLayout>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ProductDetailLayout from './ProductDetailLayout.vue';

const router = useRouter();
const route = useRoute();

const tabs = ['상품 상세정보', '후기', '문의'];

const currentTab = ref('상품 상세정보');

function updateCurrentTabByRoute() {
  const path = route.path;

  if (path.includes('/reviews')) {
    currentTab.value = '후기';
  } else if (path.includes('/inquiries')) {
    currentTab.value = '문의';
  } else {
    currentTab.value = '상품 상세정보';
  }
}


updateCurrentTabByRoute();

watch(() => route.path, () => {
  updateCurrentTabByRoute();
});

function onTabClick(tab) {
  currentTab.value = tab;

  const productId = Number(route.params.productId);;
  const basePath = `/product/${productId}`;

  if (tab === '상품 상세정보') {
    router.push(basePath);
  } else if (tab === '후기') {
    router.push(`${basePath}/reviews`);
  } else if (tab === '문의') {
    router.push(`${basePath}/inquiries`);
  }
}
</script>