/**********************************
 * @Description: 用户管理 API (本地模块)
 **********************************/

import { request } from '@/utils'

export default {
  // 分页查询用户列表
  read: (params = {}) => request.get('/user/list', { params }),
  // 创建用户
  create: data => request.post('/user', data),
  // 更新用户
  update: data => request.put(`/user/${data.id}`, data),
  // 删除用户
  delete: id => request.delete(`/user/${id}`),
  // 重置密码
  resetPwd: (id, data) => request.put(`/user/${id}/reset-password`, data),
  // 获取角色列表 (后端暂未实现，返回模拟数据)
  getAllRoles: () => Promise.resolve({
    data: [
      { id: 'admin', name: '管理员' },
      { id: 'user', name: '普通用户' },
    ],
  }),
}
