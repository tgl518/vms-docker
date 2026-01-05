/**********************************
 * @Description: 互动相关 API（评论、点赞、收藏）
 **********************************/

import { request } from '@/utils'

export default {
  // ==================== 评论 API ====================
  // 获取视频评论列表
  getComments: (videoId, params) => request.get(`/comment/video/${videoId}`, { params }),

  // 发表评论
  addComment: data => request.post('/comment', data),

  // 删除评论
  deleteComment: id => request.delete(`/comment/${id}`),

  // ==================== 收藏 API ====================
  // 切换收藏状态
  toggleFavorite: videoId => request.post(`/interact/favorite/${videoId}`),

  // 检查是否已收藏
  checkFavorite: videoId => request.get(`/interact/favorite/check/${videoId}`),

  // 获取用户收藏列表
  getFavorites: params => request.get('/interact/favorites', { params }),

  // ==================== 点赞 API ====================
  // 切换点赞状态（video/comment）
  toggleLike: (targetType, targetId) => request.post(`/interact/like/${targetType}/${targetId}`),

  // 检查是否已点赞
  checkLike: (targetType, targetId) => request.get(`/interact/like/check/${targetType}/${targetId}`),

  // ==================== 综合状态 API ====================
  // 批量获取视频互动状态（点赞+收藏）
  getInteractStatus: videoId => request.get(`/interact/status/${videoId}`),

  // ==================== 用户统计 API ====================
  // 获取用户统计数据（收藏、历史、点赞、评论数）
  getUserStats: () => request.get('/interact/user/stats'),

  // ==================== 弹幕 API ====================
  // 获取视频弹幕列表
  getDanmaku: videoId => request.get(`/danmaku/video/${videoId}`),

  // 发送弹幕
  addDanmaku: data => request.post('/danmaku', data),
}

