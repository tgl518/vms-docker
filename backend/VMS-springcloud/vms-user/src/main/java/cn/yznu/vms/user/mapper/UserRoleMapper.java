package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 删除用户的所有角色关联
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     */
    @Delete("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID查询用户ID列表
     */
    @Select("SELECT user_id FROM sys_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量给角色添加用户
     */
    @Insert("<script>" +
            "INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES " +
            "<foreach collection='userIds' item='uid' separator=','>" +
            "(#{uid}, #{roleId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds);

    /**
     * 批量从角色移除用户
     */
    @Delete("<script>" +
            "DELETE FROM sys_user_role WHERE role_id = #{roleId} AND user_id IN " +
            "<foreach collection='userIds' item='uid' open='(' separator=',' close=')'>" +
            "#{uid}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds);
}

