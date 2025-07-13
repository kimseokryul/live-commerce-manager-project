import { createRouter, createWebHistory } from 'vue-router'
import SellerDashboard from '../components/seller/SellerDashboard.vue'
import ProductList from '../components/seller/product/ProductList.vue'
import ProductRegister from '../components/seller/product/ProductRegister.vue'
import ProductDetail from '../components/seller/product/ProductDetail.vue'
import ProductReviewList from '../components/seller/product/ProductReviewList.vue'
import ProductInquiryList from '../components/seller/product/ProductInquiryList.vue'
import ProductInquiryDetail from '../components/seller/product/ProductInquiryDetail.vue'
import ProductReviewDetail from '../components/seller/product/ProductReviewDetail.vue'
import ProductDetailMain from '../components/seller/product/ProductDetailMain.vue'
import Orderlist from '../components/seller/order/Orderlist.vue'
import OrderDetail from '../components/seller/order/OrderDetail.vue'
import ProductEdit from '../components/seller/product/ProductEdit.vue'
import ReviewInquiries from '../components/seller/ReviewInquiries/ReviewInquiries.vue'


import BroadCastTest from '../components/seller/broadCast/test.vue'
import BroadCastRegister from '../components/seller/broadCast/BroadCastRegister.vue'
import BroadCastStart from '../components/seller/broadCast/BroadCastStart.vue'

import ChatTest from '../components/chat/ChatTest.vue'
import BroadCastList from '../components/seller/broadCast/BroadCastList.vue'
import BroadCastDetail from '../components/seller/broadCast/BroadCastDetail.vue'

import UserList from '../components/admin/UserList.vue'
import UserDetail from '../components/admin/UserDetailView.vue'
import LockedUserList from '../components/admin/LockedUserList.vue'
import HostList from '../components/admin/HostList.vue'
import WidthdrawnUserList from '../components/admin/WidthdrawnUserList.vue'

import SellerSales from '../components/seller/Sales/SellerSales.vue'

const Login = () => import('@/components/login/login.vue')
const Register = () => import('@/components/host/register.vue')
const FindId = () => import('@/components/login/FindId.vue')
const FindPassword = () => import('@/components/login/FindPasswordStep1.vue')
const ChangePassword = () => import('@/components/login/ChangePassword.vue')

const routes = [
  { path: '/', name: 'Dashboard', component: SellerDashboard, meta: { requiresAuth: true }},
  { path: '/product', name: 'ProductList', component: ProductList },
  { path: '/product/register', name: 'ProductRegister', component: ProductRegister },
  { path: '/product/:productId/edit', name: 'ProductEdit', component: ProductEdit, props: true },
  { path: '/product/:productId', component: ProductDetail, props: true, children: [
  { path: '', name: 'ProductDetail', component : ProductDetailMain, props: true },
  { path: 'reviews', name: 'ProductReviewList', component: ProductReviewList, props: true },
  { path: 'reviews/:reviewId', name: 'ProductReviewDetail', component: ProductReviewDetail, props: true },
  { path: 'inquiries', name: 'ProductInquiryList', component: ProductInquiryList, props: true },
  { path: 'inquiries/:qnaId', name: 'ProductInquiryDetail', component: ProductInquiryDetail, props: true }
]},

  { path: '/reviews-inquiries', name: 'ReviewInquiriesManage', component: ReviewInquiries, props: true},
  


  { path: '/login', name: 'Login', component: Login },
  { path: '/host/register', name: 'Register', component: Register },
  { path: '/login/findId', name: 'FindId', component: FindId },
  { path: '/login/findPassword', name: 'FindPassword', component: FindPassword },
  { path: '/login/changePassword', name: 'ChangePassword', component: ChangePassword },
  { path: '/order', name: 'OrderList', component: Orderlist, props: true},
  { path: '/order/detail', name: 'OrderDetail', component: OrderDetail, props: true},

  { path: '/broadcast/test', name: 'BroadCastTest', component: BroadCastTest, props: true},
  { path: '/broadcast/register', name: 'BroadCastRegister', component: BroadCastRegister, props: true},
  { path: '/broadcast/:broadcast_id', name: 'BroadCastStart', component: BroadCastStart, props: true},
  { path: '/broadcast/list', name: 'BroadCastList', component: BroadCastList, props: true},
  { path: '/chat-test/:broadcastId/:role?', name: 'ChatTest', component: ChatTest, props: true },
  { path: '/broadcast/detail/:broadcast_id', name: 'BroadCastDetail', component: BroadCastDetail, props: true},

  { path: '/sellerSales', name: 'sellerSales', component: SellerSales, props: true},
  { path: '/admin/user-list', name: 'UserList', component: UserList, props: true},
  { path: '/admin/user-list/locked', name: 'LockedUserList', component: LockedUserList, props: true},
  { path: '/admin/user-list/widthdrawn', name: 'WidthdrawnUserList', component: WidthdrawnUserList, props: true},
  { path: '/admin/user-list/host', name: 'HostList', component: HostList, props: true},
  { path: '/admin/user-detail/:user_id', name: 'UserDetail', component: UserDetail, props: true},
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { left: 0, top: 0 }
  }
})

// ✅ Navigation Guard 설정
const publicPages = ['/login', '/login/findId', '/login/findPassword', '/login/changePassword', '/host/register','/chat-test/:broadcastId/:role?']

router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('jwt') || !!sessionStorage.getItem('jwt')
  const isPublicPage = publicPages.includes(to.path)

  if (!isPublicPage && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    next('/')
  } else {
    console.log('🚦 이동 감지:')
  console.log('➡️ 경로:', to.path)
  console.log('➡️ 쿼리:', to.query)
  console.log('➡️ name:', to.name)
    next()
  }
})

export default router