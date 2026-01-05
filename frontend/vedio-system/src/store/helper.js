import { cloneDeep } from 'lodash-es'
import api from '@/api'
import { basePermissions } from '@/settings'

export async function getUserInfo() {
  const res = await api.getUser()
  // 后端 UserVO 字段: userId, username, email, phone, roles, status, nickname, avatar, gender, intro, birthday, location, createTime
  const data = res.data || {}
  return {
    id: data.userId,
    username: data.username,
    avatar: data.avatar,
    nickName: data.nickname,
    gender: data.gender,
    address: data.location,
    email: data.email,
    roles: data.roles || [],  // 后端返回 roles 数组
    permissions: data.permissions || [], // 后端返回权限编码数组
    currentRole: data.roles?.[0] ? { code: data.roles[0] } : null,
  }
}

export async function getPermissions() {
  let asyncPermissions = []
  try {
    const res = await api.getRolePermissions()
    asyncPermissions = res?.data || []
  }
  catch (error) {
    console.error(error)
  }
  return cloneDeep(basePermissions).concat(asyncPermissions)
}
