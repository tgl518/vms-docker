/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/05 21:25:07
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import api from '@/api'
import { useAuthStore, usePermissionStore, useUserStore } from '@/store'
import { getPermissions, getUserInfo } from '@/store/helper'

const WHITE_LIST = ['/login', '/404', '/403', '/not-found']

// 需要登录的路径前缀
const AUTH_REQUIRED_PREFIXES = ['/admin']

// 检查是否为白名单路径（用户端路由不需要登录）
function isWhitePath(path) {
  // 白名单直接放行
  if (WHITE_LIST.includes(path)) return true
  // 需要登录的路径
  if (AUTH_REQUIRED_PREFIXES.some(prefix => path.startsWith(prefix))) return false
  // 其他路径（用户端）放行
  return true
}

export function createPermissionGuard(router) {
  router.beforeEach(async (to) => {
    const authStore = useAuthStore()
    const token = authStore.accessToken

    /** 没有token */
    if (!token) {
      if (isWhitePath(to.path))
        return true
      return { path: 'login', query: { ...to.query, redirect: to.path } }
    }

    // 有token的情况
    if (to.path === '/login')
      return { path: '/' }
    if (isWhitePath(to.path))
      return true

    const userStore = useUserStore()
    const permissionStore = usePermissionStore()

    // 检查是否需要加载动态路由（用户已登录但权限未加载，或访问后台但路由未注册）
    const needLoadPermissions = !userStore.userInfo ||
      (to.path.startsWith('/admin') && permissionStore.accessRoutes.length === 0)

    if (needLoadPermissions) {
      const [user, permissions] = await Promise.all([getUserInfo(), getPermissions()])
      userStore.setUser(user)
      permissionStore.setPermissions(permissions)

      // 动态导入所有视图组件，key格式为 /src/views/xxx/index.vue
      const routeComponents = import.meta.glob('/src/views/**/*.vue')

      permissionStore.accessRoutes.forEach((route) => {
        if (route.component) {
          // 数据库中的路径格式与 import.meta.glob 的 key 格式一致
          if (routeComponents[route.component]) {
            route.component = routeComponents[route.component]
          } else {
            // 尝试宽松匹配 (解决可能的路径前缀差异)
            const componentKey = Object.keys(routeComponents).find(key => key.endsWith(route.component))
            if (componentKey) {
              route.component = routeComponents[componentKey]
            } else {
              console.warn(`[Permission Guard] Component not found: ${route.component}`)
              route.component = undefined
            }
          }
        }

        // 设置默认layout为normal（后台管理布局）
        if (!route.meta) route.meta = {}
        if (!route.meta.layout && route.path?.startsWith('/admin')) {
          route.meta.layout = 'normal'
        }

        !router.hasRoute(route.name) && router.addRoute(route)
      })
      return { ...to, replace: true }
    }

    // 用户端路由直接放行
    if (isWhitePath(to.path))
      return true

    // 检查是否是 AdminCatchAll 路由（用户访问了未授权的后台页面）
    if (to.name === 'AdminCatchAll') {
      // 已登录但无权限，显示 403 页面
      return true  // 让它渲染 403 组件
    }

    // 检查路由是否是动态添加的后台路由
    const routes = router.getRoutes()
    const matchedRoute = routes.find(route => route.name === to.name && route.name !== 'NotFound' && route.name !== 'AdminCatchAll')
    if (matchedRoute)
      return true

    // 路由不存在时显示 404
    return { name: '404', query: { path: to.fullPath } }
  })
}

