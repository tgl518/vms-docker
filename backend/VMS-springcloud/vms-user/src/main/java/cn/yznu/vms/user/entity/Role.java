package cn.yznu.vms.user.entity;

import cn.yznu.vms.common.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色表 (RBAC)
 */
@Data
@TableName("sys_role")
@Schema(name = "Role", description = "角色表")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "角色编码(唯一)")
    @TableField("code")
    private String code;

    @Schema(description = "角色名称")
    @TableField("name")
    private String name;

    @Schema(description = "角色描述")
    @TableField("description")
    private String description;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "状态: 1启用 0禁用")
    @TableField("status")
    private StatusEnum status;

    @Schema(description = "软删除: 0未删 1已删")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
