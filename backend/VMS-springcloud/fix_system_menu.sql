-- 修复系统管理相关菜单的路由路径和组件路径
-- 执行后需要清除浏览器缓存并重新登录

-- 修复角色管理
UPDATE sys_permission 
SET path = '/admin/pms/role',
    component = '/src/views/pms/role/index.vue'
WHERE id = 41;

-- 修复权限管理
UPDATE sys_permission 
SET path = '/admin/pms/resource',
    component = '/src/views/pms/resource/index.vue'
WHERE id = 42;

-- 修复配置管理
UPDATE sys_permission 
SET path = '/admin/pms/config',
    component = '/src/views/pms/config/index.vue'
WHERE id = 43;

-- 验证修改结果
SELECT id, code, name, path, component 
FROM sys_permission 
WHERE id IN (41, 42, 43);
