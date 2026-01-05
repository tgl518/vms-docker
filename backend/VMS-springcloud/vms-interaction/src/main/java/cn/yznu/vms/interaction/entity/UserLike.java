package cn.yznu.vms.interaction.entity;

import cn.yznu.vms.interaction.enums.TargetTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户点赞表
 */
@Data
@TableName("user_like")
@Schema(description = "用户点赞表")
public class UserLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("target_type")
    @Schema(description = "点赞类型: 1视频 2评论")
    private TargetTypeEnum targetType;

    @TableField("target_id")
    @Schema(description = "目标ID")
    private Long targetId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
