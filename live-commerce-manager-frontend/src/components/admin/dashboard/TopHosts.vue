<template>
  <div class="ranking-card">
    <h3 class="title">🏆 매출 상위 호스트 TOP 5</h3>
    <ul class="ranking-list">
      <li v-for="(host, index) in hosts" :key="host.nickname" class="ranking-item">
        <div class="rank-num">{{ index + 1 }}</div>
        <div class="host-info">
          <span class="nickname">{{ host.nickname }}</span>
          <span class="user-id">({{ host.user_id }})</span>
        </div>
        <div class="total-price">₩{{ host.total_price.toLocaleString() }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'

const hosts = ref([])

onMounted(async () => {
  const token = localStorage.getItem('jwt') || sessionStorage.getItem('jwt')
  if (!token) return

  const hostsRef = await axios.get('/api/dashboard/admin/sales/host', {
    headers: { Authorization: `Bearer ${token}` }
  })

  hosts.value = hostsRef.data
})
</script>

<style scoped>
.ranking-card {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  max-width: 500px;
  margin: 0 auto;
}

.title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.ranking-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.ranking-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #f9f9fb;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 0.5rem;
}

.rank-num {
  font-size: 1.25rem;
  font-weight: 700;
  color: #5c6ac4;
  margin-right: 1rem;
  width: 2rem;
  text-align: center;
}

.host-info {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.nickname {
  font-weight: 600;
  color: #333;
}

.user-id {
  font-size: 0.85rem;
  color: #777;
}

.total-price {
  font-weight: 700;
  color: #fa5252;
}
</style>