
export const basicRoutes = [
  // 错误页面路由（必须放在最前面，避免被通配符匹配）
  {
    name: '404',
    path: '/404',
    component: () => import('@/views/error-page/404.vue'),
    meta: {
      title: '页面飞走了',
      layout: 'empty',
    },
  },

  {
    name: '403',
    path: '/403',
    component: () => import('@/views/error-page/403.vue'),
    meta: {
      title: '没有权限',
      layout: 'empty',
    },
  },

  // 文档中提供的备用 404 访问路径
  {
    name: 'NotFoundPage',
    path: '/not-found',
    component: () => import('@/views/error-page/404.vue'),
    meta: {
      title: '页面飞走了',
      layout: 'empty',
    },
  },

  // 后台管理入口重定向
  {
    name: 'Admin',
    path: '/admin',
    redirect: '/admin/dashboard',
    meta: {
      title: '后台管理',
      layout: 'empty',
    },
  },

  // 后台个人信息页面
  {
    name: 'AdminProfile',
    path: '/admin/profile',
    component: () => import('@/views/profile/index.vue'),
    meta: {
      title: '个人信息',
      layout: 'normal',
    },
  },

  {
    name: 'Login',
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录页',
      layout: 'empty',
    },
  },

  // 后台管理 catch-all：捕获所有未注册的 admin 路由
  // 权限守卫会检查这些路由并返回 403（无权限）
  {
    name: 'AdminCatchAll',
    path: '/admin/:pathMatch(.*)*',
    component: () => import('@/views/error-page/403.vue'),
    meta: {
      title: '无权限访问',
      layout: 'empty',
      requiresAuth: true,
    },
  },

  // 后台管理路由通过权限系统动态添加，无需在此定义
  
  // 用户端路由（默认首页为视频门户）
  {
    path: '/',
    component: () => import('@/layouts/portal/index.vue'),
    meta: { layout: 'empty' },
    children: [
      {
        path: '',
        name: 'PortalHome',
        component: () => import('@/views/portal/home/index.vue'),
        meta: { title: 'VMS视频' }
      },
      {
        path: 'category/:id',
        name: 'PortalCategory',
        component: () => import('@/views/portal/category/index.vue'),
        meta: { title: '分类 - VMS视频' }
      },
      {
        path: 'video/:id',
        name: 'PortalVideo',
        component: () => import('@/views/portal/video/index.vue'),
        meta: { title: '视频播放 - VMS视频' }
      },
      {
        path: 'search',
        name: 'PortalSearch',
        component: () => import('@/views/portal/search/index.vue'),
        meta: { title: '搜索 - VMS视频' }
      },
      {
        path: 'history',
        name: 'PortalHistory',
        component: () => import('@/views/portal/history/index.vue'),
        meta: { title: '观看历史 - VMS视频' }
      },
      {
        path: 'favorites',
        name: 'PortalFavorites',
        component: () => import('@/views/portal/favorites/index.vue'),
        meta: { title: '我的收藏 - VMS视频' }
      },
      {
        path: 'profile',
        name: 'PortalProfile',
        component: () => import('@/views/portal/profile/index.vue'),
        meta: { title: '个人中心 - VMS视频' }
      }
      // 移除 Portal 子路由中的 catch-all，改由全局 catch-all 处理
    ]
  },

  // 捕获所有未匹配的路由，直接显示 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error-page/404.vue'),
    meta: {
      title: '页面飞走了',
      layout: 'empty',
    },
  },
]
