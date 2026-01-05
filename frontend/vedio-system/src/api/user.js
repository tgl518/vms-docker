/**********************************
 * @Description: 用户管理 API
 **********************************/

import { request } from '@/utils'

export default {
  // 分页查询用户列表
  getUsers: params => request.get('/user/list', { params }),

  // 创建用户
  createUser: data => request.post('/user', data),

  // 更新用户
  updateUser: (id, data) => request.put(`/user/${id}`, data),

  // 删除用户
  deleteUser: id => request.delete(`/user/${id}`),

  // 重置密码
  resetPassword: (id, password) => request.put(`/user/${id}/reset-password`, { password }),

  // ==================== 个人中心 API ====================
  // 更新个人资料
  updateProfile: data => request.put('/user/info', data),

  // 修改密码
  changePassword: data => request.put('/user/password', null, {
    params: {
      oldPassword: data.oldPassword,
      newPassword: data.newPassword
    }
  }),
}

