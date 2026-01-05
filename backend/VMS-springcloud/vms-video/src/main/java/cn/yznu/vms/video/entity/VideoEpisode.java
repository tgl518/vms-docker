package cn.yznu.vms.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频分集表
 */
@Data
@TableName("video_episode")
@Schema(description = "视频分集表")
public class VideoEpisode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_id")
    @Schema(description = "关联视频ID")
    private Long videoId;

    @TableField("title")
    @Schema(description = "分集标题")
    private String title;

    @TableField("file_url")
    @Schema(description = "视频文件地址")
    private String fileUrl;

    @TableField("cover_url")
    @Schema(description = "分集封面")
    private String coverUrl;

    @TableField("duration")
    @Schema(description = "时长(秒)")
    private Integer duration;

    @TableField("sort")
    @Schema(description = "集数排序")
    private Integer sort;

    @TableField("is_free")
    @Schema(description = "是否免费: 1免费 0收费")
    private Byte isFree;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

