package cn.yznu.vms.interaction.mapper;

import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.vo.HistoryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WatchHistoryMapper extends BaseMapper<WatchHistory> {
    
    /**
     * 分页查询用户观看历史（包含视频信息）
     */
    @Select("SELECT h.id, h.video_id, h.episode_id, v.title, v.cover_url, h.watch_duration, v.duration as total_duration, h.last_watch_time " +
            "FROM watch_history h " +
            "LEFT JOIN video v ON h.video_id = v.id " +
            "WHERE h.user_id = #{userId} AND (v.deleted = 0 OR v.deleted IS NULL) " +
            "ORDER BY h.last_watch_time DESC")
    IPage<HistoryVO> selectHistoryWithVideoInfo(Page<HistoryVO> page, @Param("userId") Long userId);
}

