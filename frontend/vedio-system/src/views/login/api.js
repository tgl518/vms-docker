/**********************************
 * @Description: 登录注册 API
 **********************************/

import { request } from '@/utils'

export default {
  // 用户登录
  login: data => request.post('/user/login', data, { needToken: false }),
  // 用户注册
  register: data => request.post('/user/register', data, { needToken: false }),
  // 获取用户信息
  getUser: () => request.get('/user/info'),
}
