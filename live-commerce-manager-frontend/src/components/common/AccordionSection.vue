<template>
  <div class="accordion">
    <div class="accordion-header" @click="$emit('toggle')">
      <span>{{ title }}</span>
      <span>{{ open ? '▲' : '▼' }}</span>
    </div>
    <div class="accordion-body" v-show="open">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  open: Boolean
})
defineEmits(['toggle'])
</script>

<style scoped>
.accordion {
  border: 1px solid #e4e8f1;
  border-radius: 10px;
  margin-bottom: 24px;
  background: #f9fafc;
  box-shadow: 0 1px 3px rgba(60,80,120,0.03);
}
.accordion-header {
  padding: 14px 18px;
  font-weight: 700;
  font-size: 1.09rem;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f1f5fa;
  border-radius: 10px 10px 0 0;
  user-select: none;
}
.accordion-body {
  padding: 20px 18px 14px 18px;
  border-top: 1px solid #e4e8f1;
}
</style>
