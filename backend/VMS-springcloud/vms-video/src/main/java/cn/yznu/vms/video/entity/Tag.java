package cn.yznu.vms.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签表
 */
@Data
@TableName("tag")
@Schema(description = "标签表")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    @Schema(description = "标签名称")
    private String name;

    @TableField("color")
    @Schema(description = "标签颜色")
    private String color;

    @TableField("use_count")
    @Schema(description = "使用次数")
    private Integer useCount;

    @TableField("deleted")
    @TableLogic
    @Schema(description = "软删除: 0未删 1已删")
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

