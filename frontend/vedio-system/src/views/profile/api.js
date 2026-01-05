/**********************************
 * @Description: 个人资料 API
 **********************************/

import { request } from '@/utils'

export default {
  changePassword: data => request.put('/user/password', data),
  updateProfile: data => request.put('/user/info', data),
  // 获取我的投稿视频列表
  getMyVideos: params => request.get('/video/my', { params }),
}
