package cn.yznu.vms.video.mapper;

import cn.yznu.vms.video.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    
    /**
     * 聚合统计：视频总数、播放量总和、点赞量总和
     * 直接在数据库层面统计，避免加载全表到内存
     */
    @Select("""
        SELECT 
            COUNT(*) as totalVideos,
            COALESCE(SUM(view_count), 0) as totalViews,
            COALESCE(SUM(like_count), 0) as totalLikes
        FROM video
        WHERE deleted = 0
    """)
    Map<String, Object> selectAggregateStats();
    
    /**
     * 统计指定日期范围内新增的视频数量
     */
    @Select("""
        SELECT COUNT(*) 
        FROM video 
        WHERE deleted = 0 
          AND create_time >= #{startTime}
    """)
    Long countVideosSince(@Param("startTime") LocalDateTime startTime);
    
    /**
     * 统计指定日期范围内新增的视频数量（按天）
     */
    @Select("""
        SELECT COUNT(*) 
        FROM video 
        WHERE deleted = 0 
          AND create_time >= #{startTime}
          AND create_time < #{endTime}
    """)
    Long countVideosBetween(@Param("startTime") LocalDateTime startTime, 
                           @Param("endTime") LocalDateTime endTime);
}

