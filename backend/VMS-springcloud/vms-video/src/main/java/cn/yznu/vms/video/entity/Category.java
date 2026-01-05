package cn.yznu.vms.video.entity;

import cn.yznu.vms.common.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频分类表
 */
@Data
@TableName("category")
@Schema(description = "视频分类表")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    @Schema(description = "分类名称")
    private String name;

    @TableField("slug")
    @Schema(description = "别名(英文)")
    private String slug;

    @TableField("icon")
    @Schema(description = "分类图标URL")
    private String icon;

    @TableField("description")
    @Schema(description = "分类描述")
    private String description;

    @TableField("parent_id")
    @Schema(description = "父分类ID")
    private Long parentId;

    @TableField("sort")
    @Schema(description = "排序优先级")
    private Integer sort;

    @TableField("status")
    @Schema(description = "状态: 1启用 0禁用")
    private StatusEnum status;

    @TableField("deleted")
    @TableLogic
    @Schema(description = "软删除: 0未删 1已删")
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

