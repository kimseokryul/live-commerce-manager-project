<template>
  <div class="product-detail-layout">
    <div class="tabs">
      <div
        v-for="tab in tabs"
        :key="tab"
        :class="['tab', { active: currentTab === tab }]"
        @click="$emit('update:currentTab', tab)"
      >
        {{ tab }}
      </div>
    </div>
    <div class="tab-content">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps(['tabs', 'currentTab']);
defineEmits(['update:currentTab']);
</script>

<style scoped>
.product-detail-layout {
  width: 100%;
  height: 100%;
  max-width: none;
  min-height: 0;
  margin: 0;
  padding: 40px 48px 48px 48px; /* 위 40px, 좌우/아래 48px */
  background: #fff;
  border-radius: 10px;     /* 더 각지게, 완전 각지게 하려면 0 */
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}
.tabs {
  display: flex;
  border-bottom: 2px solid #eee;
  margin-bottom: 32px;
}
.tab {
  padding: 16px 36px;
  font-size: 1.1rem;
  cursor: pointer;
  color: #888;
  border-bottom: 3px solid transparent;
  transition: color 0.2s, border-bottom 0.2s;
}
.tab.active {
  color: #222;
  font-weight: bold;
  border-bottom: 3px solid #3e7eff;
}
.tab-content {
  min-height: 300px;
}
</style>
