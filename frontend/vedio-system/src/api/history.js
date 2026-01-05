/**********************************
 * @Description: 观看历史 API
 **********************************/

import { request } from '@/utils'

export default {
  // 保存观看进度
  saveProgress: params => request.post('/history/progress', params),

  // 获取观看历史列表
  getList: params => request.get('/history/list', { params }),

  // 删除单条历史
  delete: id => request.delete(`/history/${id}`),

  // 清空所有历史
  clear: () => request.delete('/history/clear'),
}
