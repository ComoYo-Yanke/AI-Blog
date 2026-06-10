import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/articles/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue') },
  { path: '/users/:id', name: 'UserProfile', component: () => import('../views/UserProfile.vue') },
  { path: '/messages', name: 'Messages', component: () => import('../views/Messages.vue'), meta: { requiresAuth: true } },
  {
    path: '/my',
    meta: { requiresAuth: true },
    children: [
      { path: 'articles', name: 'MyArticles', component: () => import('../views/MyArticles.vue') },
      { path: 'comments', name: 'MyComments', component: () => import('../views/MyComments.vue') },
    ]
  },
  { path: '/articles/create', name: 'CreateArticle', component: () => import('../views/CreateArticle.vue'), meta: { requiresAuth: true } },
  { path: '/articles/:id/edit', name: 'EditArticle', component: () => import('../views/EditArticle.vue'), meta: { requiresAuth: true } },
  {
    path: '/admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', component: () => import('../views/admin/UserManage.vue') },
      { path: 'articles', component: () => import('../views/admin/ArticleManage.vue') },
      { path: 'comments', component: () => import('../views/admin/CommentManage.vue') },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !auth.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
