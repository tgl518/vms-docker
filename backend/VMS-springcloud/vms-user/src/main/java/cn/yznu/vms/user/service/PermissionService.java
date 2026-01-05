package cn.yznu.vms.user.service;

import cn.yznu.vms.user.dto.PermissionCreateDTO;
import cn.yznu.vms.user.dto.PermissionUpdateDTO;
import cn.yznu.vms.user.vo.PermissionVO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 获取用户权限菜单树
     */
    List<PermissionVO> getUserPermissions(Long userId);

    /**
     * 获取用户权限编码列表
     */
    List<String> getUserPermissionCodes(Long userId);

    /**
     * 获取所有权限树（不过滤启用状态，用于角色编辑）
     */
    List<PermissionVO> getAllPermissionTree();

    /**
     * 获取菜单权限树（仅MENU类型）
     */
    List<PermissionVO> getMenuTree();

    /**
     * 获取某菜单下的按钮权限列表
     */
    List<PermissionVO> getButtonsByParentId(Long parentId);

    /**
     * 创建权限
     */
    Long createPermission(PermissionCreateDTO dto);

    /**
     * 更新权限
     */
    void updatePermission(Long id, PermissionUpdateDTO dto);

    /**
     * 删除权限
     */
    void deletePermission(Long id);

    /**
     * 清除权限缓存
     */
    void clearCache();
}

