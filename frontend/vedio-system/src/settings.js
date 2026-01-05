/**********************************
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/13 20:54:36
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

export const defaultLayout = 'normal'

export const defaultPrimaryColor = '#316C72'

// 控制 LayoutSetting 组件是否可见
export const layoutSettingVisible = true

export const naiveThemeOverrides = {
  common: {
    primaryColor: '#316C72FF',
    primaryColorHover: '#316C72E3',
    primaryColorPressed: '#2B4C59FF',
    primaryColorSuppl: '#316C72E3',
  },
}

export const basePermissions = [
  {
    code: 'RoleUser',
    name: '角色用户分配',
    type: 'MENU',
    path: '/admin/pms/role/user/:roleId',
    component: '/src/views/pms/role/role-user.vue',
    icon: 'i-fe:users',
    order: 0,
    enable: true,
    show: false, // 隐藏菜单，但路由可访问
  },

]
