/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2024/04/01 15:52:04
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import axios from 'axios'
import { request } from '@/utils'

export default {
  getMenuTree: () => request.get('/user/permission/menu/tree'),
  getButtons: ({ parentId }) => request.get(`/user/permission/button/${parentId}`),
  getComponents: () => axios.get(`${import.meta.env.VITE_PUBLIC_PATH}components.json`),
  addPermission: data => request.post('/user/permission', data),
  savePermission: (id, data) => request.patch(`/user/permission/${id}`, data),
  deletePermission: id => request.delete(`/user/permission/${id}`),
}
