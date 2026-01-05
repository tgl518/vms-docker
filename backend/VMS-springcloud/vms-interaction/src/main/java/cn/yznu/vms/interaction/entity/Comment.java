package cn.yznu.vms.interaction.entity;

import cn.yznu.vms.common.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论表
 */
@Data
@TableName("comment")
@Schema(description = "评论表")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_id")
    @Schema(description = "视频ID")
    private Long videoId;

    @TableField("user_id")
    @Schema(description = "评论用户ID")
    private Long userId;

    @TableField("content")
    @Schema(description = "评论内容")
    private String content;

    @TableField("parent_id")
    @Schema(description = "父评论ID(回复)")
    private Long parentId;

    @TableField("reply_user_id")
    @Schema(description = "回复的用户ID")
    private Long replyUserId;

    @TableField("like_count")
    @Schema(description = "点赞数")
    private Integer likeCount;

    @TableField("status")
    @Schema(description = "状态: 1正常 0隐藏")
    private StatusEnum status;

    @TableField("deleted")
    @TableLogic
    @Schema(description = "软删除: 0未删 1已删")
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

