import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login,
      alias: '/login'
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/home',
      name: 'Home',
      component: () => import('../views/Home.vue')
    },
    { 
      path: '/:pathMatch(.*)*', 
      name: 'NotFound', 
      component: () => import('../views/error404.vue')
    },
    {
      path: '/admin',
      name: 'Admin',
      component: () => import('../views/AdminHome.vue'),
    }
  ]
})

router.beforeEach((to, from, next) => {
  const loggedIn = isLoggedIn();
  const userRole = getUserRole();

  if (to.name === 'Register') {
    // 如果是注册页面，则直接放行
    next();
  } else if (!loggedIn && to.name !== 'Login') {
    // 如果未登录且目标页面不是 Login，则重定向到 Login
    next({ name: 'Login' });
  } else if (loggedIn && to.name === 'Login') {
    // 如果已登录且目标页面是 Login，则重定向到 Home
    next({ name: 'Home' });
  } else if (userRole === '1' && to.name === 'Admin') {
    // 如果用户角色为 1 且试图访问 Admin 页面，则重定向到 Home 或错误页面
    next({ name: 'Home' });
    // 或者 next({ name: 'ErrorPage' }); // 如果你有一个错误页面
  } else {
    // 其他情况，正常导航
    next();
  }
});

function isLoggedIn() {
  return localStorage.getItem('userLoggedIn') === 'true';
}

function getUserRole() {
  return localStorage.getItem('role');
}



export default router
