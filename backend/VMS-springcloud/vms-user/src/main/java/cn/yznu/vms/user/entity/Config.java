package cn.yznu.vms.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置表
 */
@Data
@TableName("sys_config")
@Schema(name = "Config", description = "系统配置表")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "配置键(唯一)")
    @TableField("config_key")
    private String configKey;

    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;

    @Schema(description = "类型: STRING/NUMBER/BOOLEAN/JSON")
    @TableField("config_type")
    private String configType;

    @Schema(description = "配置描述")
    @TableField("description")
    private String description;

    @Schema(description = "系统内置: 1是 0否")
    @TableField("is_system")
    private Integer isSystem;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
