import { createRouter, createWebHistory } from 'vue-router'
import SellerMain from '../components/seller/SellerMain.vue'
import ProductList from '../components/seller/product/ProductList.vue'
import ProductRegister from '../components/seller/product/ProductRegister.vue'
import ProductDetail from '../components/seller/product/ProductDetail.vue'
import ProductReviewList from '../components/seller/product/ProductReviewList.vue'
import ProductInquiryList from '../components/seller/product/ProductInquiryList.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: SellerMain},
  { path: '/product', name: 'ProductList', component: ProductList},
  { path: '/product/register', name: 'ProductRegister', component: ProductRegister },
  { path: '/product/:productCode', name: 'ProductDetail', component: ProductDetail, props: true },
  { path: '/product/:productCode/reviews', name: 'ProductReviewList', component: ProductReviewList, props: true },
  { path: '/product/:productCode/inquiries', name: 'ProductInquiryList', component: ProductInquiryList, props: true },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
