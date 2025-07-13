<script setup lang="ts">
import { useRouter } from 'vue-router';

defineProps<{
  onSale: number;
  offSale: number;
  outOfStock: number;
}>()

const router = useRouter();

const goToProductPage = () => {
  router.push({ name: 'ProductList' });
}
</script>

<template>
    <div class="card">
        <div class="card-header">
            <h3>판매 상품</h3>
            <button class="more-btn" @click="goToProductPage">더보기</button>
        </div>
        <div class="card-body">
            <div class="item">
                <span>판매중</span>
                <span>{{ onSale }}건</span>       
            </div>
            <div class="item">
                <span>판매중지</span>
                <span>{{ offSale }}건</span>
            </div>
            <div class="item">
                <span>품절</span>
                <span>{{ outOfStock }}건</span>
            </div>
        </div>
    </div>
</template>

<style scoped>
.card {
  background: white;
  padding: 1.2rem;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3.5rem; /* ✅ 제목과 내용 사이 여백 최소화 */
}

.card-header h3 {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

.more-btn {
  background: none;
  border: none;
  color: #3b82f6;
  cursor: pointer;
  font-size: 0.9rem;
  padding: 0;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.item {
  display: flex;
  justify-content: space-between;
  font-size: 0.95rem;
  color: #333;
}
</style>
