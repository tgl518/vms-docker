package cn.yznu.vms.user.service;

import cn.yznu.vms.user.dto.RoleCreateDTO;
import cn.yznu.vms.user.dto.RoleUpdateDTO;
import cn.yznu.vms.user.vo.RoleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色
     */
    IPage<RoleVO> listRoles(Integer pageNum, Integer pageSize, String name, Boolean enable);

    /**
     * 根据ID获取角色
     */
    RoleVO getRoleById(Long id);

    /**
     * 创建角色
     */
    Long createRole(RoleCreateDTO dto);

    /**
     * 更新角色
     */
    void updateRole(Long id, RoleUpdateDTO dto);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 获取角色的用户列表
     */
    List<Long> getRoleUserIds(Long roleId);

    /**
     * 给角色添加用户
     */
    void addRoleUsers(Long roleId, List<Long> userIds);

    /**
     * 从角色移除用户
     */
    void removeRoleUsers(Long roleId, List<Long> userIds);

    /**
     * 同步角色权限缓存到 Redis
     */
    void syncRolePermissionCache(String roleCode);

    /**
     * 初始化所有角色权限缓存
     */
    void initRolePermissionCache();
}
