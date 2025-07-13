<!-- src/views/admin/sales/SalesTable.vue -->
<template>
  <div class="table-container">
    <h3>기간별 매출 요약</h3>
    <table>
      <thead>
        <tr>
          <th>날짜</th>
          <th>총 주문 수</th>
          <th>총 매출액</th>
          <th>평균 주문액</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in data" :key="row.date">
          <td>{{ row.date }}</td>
          <td>{{ row.orderCount }}</td>
          <td>₩{{ row.totalSales.toLocaleString() }}</td>
          <td>₩{{ row.avgOrderAmount.toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps({
  data: Array
});
</script>

<style scoped>
.table-container {
  background: #fff;
  padding: 1rem;
  border-radius: 12px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  text-align: left;
  padding: 0.6rem;
  border-bottom: 1px solid #eee;
}
thead {
  background: #f9f9f9;
}
</style>