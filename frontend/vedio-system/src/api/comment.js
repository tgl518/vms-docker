/**********************************
 * @Description: 评论管理 API
 **********************************/

import { request } from '@/utils'

export default {
  // 根据视频ID获取评论列表
  getCommentsByVideo: (videoId, params) => request.get(`/comment/video/${videoId}`, { params }),

  // 删除评论
  deleteComment: id => request.delete(`/comment/${id}`),
}
