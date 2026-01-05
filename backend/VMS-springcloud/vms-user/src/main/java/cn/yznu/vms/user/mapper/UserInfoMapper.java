package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户档案 Mapper
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 批量查询用户档案（优化 N+1 问题）
     */
    @Select("<script>" +
            "SELECT * FROM sys_user_info WHERE user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<UserInfo> selectByUserIds(@Param("userIds") List<Long> userIds);
}
