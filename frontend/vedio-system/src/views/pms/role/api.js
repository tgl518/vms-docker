/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/05 21:29:27
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import { request } from '@/utils'

export default {
  create: data => request.post('/user/role', data),
  read: (params = {}) => request.get('/user/role/page', { params }),
  update: data => request.patch(`/user/role/${data.id}`, data),
  delete: id => request.delete(`/user/role/${id}`),

  getAllPermissionTree: () => request.get('/user/permission/tree'),
  getAllUsers: (params = {}) => request.get('/user/list', { params }),
  addRoleUsers: (roleId, data) => request.patch(`/user/role/users/add/${roleId}`, data),
  removeRoleUsers: (roleId, data) => request.patch(`/user/role/users/remove/${roleId}`, data),
}
