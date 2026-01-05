package cn.yznu.vms.interaction.mapper;

import cn.yznu.vms.interaction.entity.UserFavorite;
import cn.yznu.vms.interaction.vo.FavoriteVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    
    /**
     * 分页查询用户收藏列表（包含视频信息）
     */
    @Select("SELECT f.id, f.video_id, v.title, v.cover_url, v.duration, v.view_count, v.like_count, f.create_time " +
            "FROM user_favorite f " +
            "LEFT JOIN video v ON f.video_id = v.id " +
            "WHERE f.user_id = #{userId} AND (v.deleted = 0 OR v.deleted IS NULL) " +
            "ORDER BY f.create_time DESC")
    IPage<FavoriteVO> selectFavoriteWithVideoInfo(Page<FavoriteVO> page, @Param("userId") Long userId);
}

