/**********************************
 * @Description: 认证相关 API
 **********************************/

import { request } from '@/utils'

export default {
  // 用户登录
  login: data => request.post('/user/login', data),

  // 用户注册
  register: data => request.post('/user/register', data),

  // 获取当前用户信息
  getUserInfo: () => request.get('/user/info'),

  // 更新用户信息
  updateUserInfo: data => request.put('/user/info', data),

  // 修改密码
  changePassword: (oldPassword, newPassword) => 
    request.put('/user/password', null, { params: { oldPassword, newPassword } }),

  // 根据用户ID获取用户信息（公开）
  getUserById: userId => request.get(`/user/info/${userId}`),
}
