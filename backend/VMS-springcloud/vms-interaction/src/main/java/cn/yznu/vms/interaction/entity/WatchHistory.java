package cn.yznu.vms.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 观看历史表
 */
@Data
@TableName("watch_history")
@Schema(description = "观看历史表")
public class WatchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("video_id")
    @Schema(description = "视频ID")
    private Long videoId;

    @TableField("episode_id")
    @Schema(description = "分集ID(可选)")
    private Long episodeId;

    @TableField("watch_duration")
    @Schema(description = "观看时长(秒)")
    private Integer watchDuration;

    @TableField("watch_progress")
    @Schema(description = "观看进度(秒)")
    private Integer watchProgress;

    @TableField("last_watch_time")
    @Schema(description = "最后观看时间")
    private LocalDateTime lastWatchTime;
}


