package cn.yznu.vms.interaction.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 弹幕表
 */
@Data
@TableName("danmaku")
@Schema(description = "弹幕表")
public class Danmaku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("video_id")
    @Schema(description = "视频ID")
    private Long videoId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("content")
    @Schema(description = "弹幕内容")
    private String content;

    @TableField("time")
    @Schema(description = "弹幕出现时间(秒)")
    private Float time;

    @TableField("color")
    @Schema(description = "弹幕颜色(HEX)")
    private String color;

    @TableField("mode")
    @Schema(description = "弹幕模式: 1滚动 2顶部 3底部")
    private Integer mode;

    @TableField("font_size")
    @Schema(description = "字体大小")
    private Integer fontSize;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
