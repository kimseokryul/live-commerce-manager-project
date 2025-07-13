<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

interface PopularProduct {
  productId: number
  name: string
  thumbnailUrl: string
  salesCount: number
}

const popularProducts = ref<PopularProduct[]>([])

const goToProductDetail = (productId: number) => {
  router.push({ name: 'ProductDetail', params: { productId } })
}

const getImageUrl = (src: string) => {
  if (!src) return '/default-image.png'
  return src.startsWith('http') ? src : `http://localhost:8080${src}`
}

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  try {
    const res = await axios.get('/api/products/dashboard/popular', {
      headers: { Authorization: `Bearer ${token}` }
    })

    popularProducts.value = (res.data ?? []).map((item: any) => ({
      productId: item.productId,
      name: item.name,
      thumbnailUrl: item.mainImage,
      salesCount: item.productSalesCount
    }))
  } catch (err) {
    console.error('❌ 인기 상품 불러오기 실패', err)
  }
})
</script>

<template>
  <div>
    <h3><span class="emoji">🔥</span> 인기 상품 TOP5</h3>

    <ul class="product-list">
      <li
        v-for="item in popularProducts"
        :key="item.productId"
        class="product-item"
        @click="goToProductDetail(item.productId)"
      >
        <img :src="getImageUrl(item.thumbnailUrl)" alt="썸네일" class="thumb" />
        <div class="info">
          <div class="name">{{ item.name }}</div>
          <div class="sales">{{ item.salesCount }}개 판매</div>
        </div>
      </li>
    </ul>

    <div v-if="popularProducts.length === 0" class="no-data">
      인기 상품이 없습니다.
    </div>
  </div>
</template>

<style scoped>
h3 {
  font-size: 1.5rem;
  font-weight: bold;
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  margin-left: 10px;
}

.emoji {
  font-size: 2rem;
  margin-right: 8px;
  color: #ff5722;
}

.product-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 0.8rem 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}
.product-item:hover {
  background-color: #f9f9f9;
}

.thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  margin-right: 30px;
  flex-shrink: 0;
  margin-left: 50px;
}

.info {
  display: flex;
  flex-direction: column;
}

.name {
  font-weight: 600;
  color: #1f2937;
  font-size: 1.2rem;
}

.sales {
  color: #666;
  font-size: 0.85rem;
  margin-top: 2px;
}

.no-data {
  text-align: center;
  font-size: 0.95rem;
  color: #999;
  margin-top: 0.5rem;
}
</style>
