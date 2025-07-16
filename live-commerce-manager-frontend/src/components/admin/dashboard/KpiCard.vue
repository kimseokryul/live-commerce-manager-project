<template>
  <div class="kpi-card">
    <div class="kpi-icon">{{ icon }}</div>
    <div class="kpi-info">
      <div class="kpi-title">{{ title }}</div>
      <div class="kpi-value">{{ value }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: String,
  title: String,
  value: [String, Number]
})
</script>

<style scoped>
.kpi-card {
  display: flex;
  align-items: center;
  background-color: #f6f8fa;
  border-radius: 12px;
  padding: 1.2rem 1.6rem;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
  transition: transform 0.2s ease;
}
.kpi-card:hover {
  transform: translateY(-3px);
}
.kpi-icon {
  font-size: 2.2rem;
  margin-right: 1.2rem;
}
.kpi-info {
  display: flex;
  flex-direction: column;
}
.kpi-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: #555;
}
.kpi-value {
  font-size: 1.4rem;
  font-weight: bold;
  color: #111;
}
</style>
