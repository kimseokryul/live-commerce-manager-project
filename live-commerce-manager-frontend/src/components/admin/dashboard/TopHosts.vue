<template>
  <div>
    <h3>🏆 매출 상위 호스트 TOP 5</h3>
    <ul>
      <li v-for="host in hosts" :key="host.name">
        {{ host.rank }}. {{ host.name }} - ₩{{ host.sales.toLocaleString() }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const hosts = ref([])

onMounted(() => {
  // 예시 데이터
  hosts.value = [
    { rank: 1, name: 'hostA', sales: 3420000 },
    { rank: 2, name: 'hostB', sales: 2800000 },
    { rank: 3, name: 'hostC', sales: 1980000 },
    { rank: 4, name: 'hostD', sales: 1700000 },
    { rank: 5, name: 'hostE', sales: 1500000 },
  ]
})
</script>
<style scoped>
.inquiry-card {
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}
</style>