import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Home from '@/views/Home.vue'
import About from '@/views/About.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录
    if (authStore.isAuthenticated && !authStore.isTokenExpired()) {
      next()
    } else {
      // 未登录，跳转到登录页
      next('/login')
    }
  } else {
    // 不需要认证的页面
    if ((to.path === '/login' || to.path === '/register') && authStore.isAuthenticated) {
      // 已登录用户访问登录/注册页，跳转到主页
      next('/')
    } else {
      next()
    }
  }
})

export default router

