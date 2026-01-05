/**********************************
 * @Description: 轮播图管理 API
 **********************************/

import { request } from '@/utils'

export default {
  // 获取所有轮播图(管理)
  getAllBanners: () => request.get('/banner/all'),

  // 获取当前展示的轮播图
  getActiveBanners: () => request.get('/banner/list'),

  // 新增轮播图
  addBanner: data => request.post('/banner', data),

  // 更新轮播图
  updateBanner: (id, data) => request.put(`/banner/${id}`, data),

  // 删除轮播图
  deleteBanner: id => request.delete(`/banner/${id}`),
}
