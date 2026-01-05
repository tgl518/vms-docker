package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建配置DTO
 */
@Data
@Schema(description = "创建配置请求")
public class ConfigCreateDTO {

    @NotBlank(message = "配置键不能为空")
    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "类型: STRING/NUMBER/BOOLEAN/JSON")
    private String configType = "STRING";

    @Schema(description = "描述")
    private String description;
}
