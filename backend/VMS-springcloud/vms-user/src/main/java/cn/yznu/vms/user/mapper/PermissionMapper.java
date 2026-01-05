package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限列表（通过角色关联）
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.is_enable = 1 " +
            "ORDER BY p.sort")
    List<Permission> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.is_enable = 1 " +
            "ORDER BY p.sort")
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限编码列表
     */
    @Select("SELECT DISTINCT p.code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.is_enable = 1")
    List<String> selectCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据父ID查询子权限列表
     */
    @Select("SELECT * FROM sys_permission WHERE parent_id = #{parentId} AND is_enable = 1 ORDER BY sort")
    List<Permission> selectByParentId(@Param("parentId") Long parentId);
}
