package cn.yznu.vms.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏表
 */
@Data
@TableName("user_favorite")
@Schema(description = "用户收藏表")
public class UserFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("video_id")
    @Schema(description = "视频ID")
    private Long videoId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

