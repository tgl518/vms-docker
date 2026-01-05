/**********************************
 * @Description: 系统配置管理API
 **********************************/

import { request } from '@/utils'

export default {
  // 分页查询配置列表
  read: (params = {}) => request.get('/user/config/page', { params }),
  // 创建配置
  create: data => request.post('/user/config', data),
  // 更新配置
  update: data => request.put(`/user/config/${data.id}`, data),
  // 删除配置
  delete: id => request.delete(`/user/config/${id}`),
  // 根据key获取配置值
  getByKey: key => request.get(`/user/config/key/${key}`),
}
