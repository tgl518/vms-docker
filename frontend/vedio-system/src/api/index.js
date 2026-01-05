/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/04 22:50:38
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import { request } from '@/utils'

export default {
  // 获取用户信息
  getUser: () => request.get('/user/info'),
  // 刷新token
  refreshToken: () => request.get('/auth/refresh/token'),
  // 登出 (后端JWT无状态，前端清除token即可)
  logout: () => Promise.resolve({ code: 200, message: 'success' }),
  // 切换当前角色
  switchCurrentRole: role => request.post(`/auth/current-role/switch/${role}`),
  // 获取角色权限菜单 (调用后端动态权限接口)
  getRolePermissions: () => request.get('/user/permissions'),
  // 验证菜单路径 (后端暂未实现，默认允许)
  validateMenuPath: path => Promise.resolve({ code: 200, data: true }),
}
