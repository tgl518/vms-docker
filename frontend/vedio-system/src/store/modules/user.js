/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/05 21:25:59
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import { defineStore } from 'pinia'
import { request } from '@/utils'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
  }),
  getters: {
    userId() {
      // 后端返回的字段名是 userId 而不是 id
      return this.userInfo?.userId ?? this.userInfo?.id
    },
    username() {
      return this.userInfo?.username
    },
    nickName() {
      // 后端返回 nickname (全小写)
      return this.userInfo?.nickname || this.userInfo?.nickName
    },
    avatar() {
      return this.userInfo?.avatar
    },
    currentRole() {
      return this.userInfo?.currentRole || {}
    },
    roles() {
      return this.userInfo?.roles || []
    },
    permissions() {
      return this.userInfo?.permissions || []
    },
  },
  actions: {
    setUser(user) {
      this.userInfo = user
    },
    resetUser() {
      this.$reset()
    },
    // 从后端获取最新用户信息
    async fetchUserInfo() {
      try {
        const { data } = await request.get('/user/info')
        if (data) {
          this.userInfo = data
        }
        return data
      } catch (error) {
        console.error('获取用户信息失败', error)
        throw error
      }
    },
  },
})
