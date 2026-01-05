package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色编码列表
     */
    @Select("SELECT r.code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<String> selectCodesByUserId(@Param("userId") Long userId);

    /**
     * 批量查询用户角色编码（优化 N+1 问题）
     * 返回包含 userId 和 roleCode 的列表，便于后续按用户分组
     */
    @Select("<script>" +
            "SELECT ur.user_id as userId, r.code as roleCode FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "AND r.deleted = 0" +
            "</script>")
    List<java.util.Map<String, Object>> selectCodesByUserIds(@Param("userIds") List<Long> userIds);
}
