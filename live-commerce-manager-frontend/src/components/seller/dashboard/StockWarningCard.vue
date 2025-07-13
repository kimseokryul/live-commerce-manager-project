<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

interface Product {
  productId: number
  name: string
  stock: number
}

const products = ref<Product[]>([])
const totalCount = ref(0)

const goToProductList = () => {
  router.push({ name: 'ProductList' })
}

const goToProductDetail = (productId: number) => {
  router.push({ name: 'ProductDetail', params: { productId } })
}

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  try {
    const res = await axios.get('/api/products/dashboard/sold-out', {
      headers: { Authorization: `Bearer ${token}` }
    })

    products.value = res.data.products ?? []
    totalCount.value = res.data.totalCount ?? 0
  } catch (e) {
    console.error('❌ 품절 임박 상품 불러오기 실패', e)
  }
})
</script>

<template>
  <div class="card">
    <div class="header">
      <h3>
        <span class="emoji">⚠</span> 품절 임박 상품
        <strong class="count">{{ totalCount }}</strong>건
      </h3>
      <button class="more-btn" @click="goToProductList()">더보기</button>
      <div class="sub-text">(재고 5개 이하)</div>
    </div>

    <ul class="list">
      <li v-for="item in products" :key="item.productId" class="list-item">
        <span class="name" @click="goToProductDetail(item.productId)">{{ item.name }}</span>
        <span class="meta">재고 {{ item.stock }}개</span>
      </li>
    </ul>

    <div v-if="products.length === 0" class="no-data">
      품절 임박 상품이 없습니다.
    </div>
  </div>
</template>

<style scoped>
.card {
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  position: relative;
  flex-wrap: wrap;
}

h3 {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

.emoji {
  font-size: 2rem;
  margin-top: 15px;
  margin-right: 8px;
  color: #e74c3c;
  position: relative;
  top: -6px;
}

.count {
  margin-left: 0.4rem;
  font-size: 1.5rem;
  color: #e74c3c;
}

.more-btn {
  background: none;
  border: none;
  color: #3b82f6;
  cursor: pointer;
  font-size: 0.9rem;
  margin-left: auto;
}

.sub-text {
  width: 100%;
  font-size: 0.9rem;
  color: #999;
  margin-top: 0.2rem;
  margin-left: 2.5rem;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-item {
  display: flex;
  justify-content: space-between;
  font-size: 1rem;
  padding: 0.7rem 0.6rem;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
}

.list-item + .list-item {
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.list-item:hover {
  background-color: #f9f9f9;
}

.name {
  font-weight: 500;
  cursor: pointer;
  color: #1f2937;
  transition: color 0.2s;
}
.name:hover {
  color: #3b82f6;
  text-decoration: underline;
}

.meta {
  color: #e74c3c;
  font-size: 0.85rem;
}

.no-data {
  text-align: center;
  font-size: 0.9rem;
  color: #999;
  margin-top: 0.5rem;
}
</style>
