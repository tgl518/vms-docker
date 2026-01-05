package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新配置DTO
 */
@Data
@Schema(description = "更新配置请求")
public class ConfigUpdateDTO {

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "类型: STRING/NUMBER/BOOLEAN/JSON")
    private String configType;

    @Schema(description = "描述")
    private String description;
}
