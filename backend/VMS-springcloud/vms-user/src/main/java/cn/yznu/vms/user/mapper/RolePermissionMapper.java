package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID查询权限ID列表
     */
    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色的所有权限
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限
     */
    @Insert("<script>" +
            "INSERT INTO sys_role_permission (role_id, permission_id) VALUES " +
            "<foreach collection='permissionIds' item='pid' separator=','>" +
            "(#{roleId}, #{pid})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 根据角色ID列表批量查询权限关联
     */
    @Select("<script>" +
            "SELECT role_id, permission_id FROM sys_role_permission WHERE role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<RolePermission> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
