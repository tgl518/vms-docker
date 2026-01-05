/**********************************
 * @Description: 视频相关 API
 **********************************/

import { request } from '@/utils'

export default {
  // ==================== 视频 API ====================
  // 分页获取视频列表
  getVideos: params => request.get('/video/list', { params }),
  // 获取视频详情
  getVideoDetail: id => request.get(`/video/detail/${id}`),
  // 新增视频
  addVideo: data => request.post('/video', data),
  // 更新视频
  updateVideo: (id, data) => request.put(`/video/${id}`, data),
  // 删除视频
  deleteVideo: id => request.delete(`/video/${id}`),

  // ==================== 分类 API ====================
  // 获取所有分类(含禁用)
  getAllCategories: () => request.get('/category/all'),
  // 获取启用分类
  getEnabledCategories: () => request.get('/category/list'),
  // 新增分类
  addCategory: data => request.post('/category', data),
  // 更新分类
  updateCategory: (id, data) => request.put(`/category/${id}`, data),
  // 删除分类
  deleteCategory: id => request.delete(`/category/${id}`),

  // ==================== 标签 API ====================
  // 获取所有标签
  getAllTags: () => request.get('/tag/list'),
  // 新增标签
  addTag: data => request.post('/tag', data),
  // 更新标签
  updateTag: (id, data) => request.put(`/tag/${id}`, data),
  // 删除标签
  deleteTag: id => request.delete(`/tag/${id}`),
}
