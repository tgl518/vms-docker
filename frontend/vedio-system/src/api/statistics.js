/**********************************
 * @Description: 统计API
 **********************************/

import { request } from '@/utils'

export default {
  // 获取仪表盘统计数据
  getDashboard: () => request.get('/statistics/dashboard'),
}
