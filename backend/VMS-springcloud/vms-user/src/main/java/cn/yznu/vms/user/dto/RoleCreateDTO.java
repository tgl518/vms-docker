package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建角色DTO
 */
@Data
@Schema(description = "创建角色请求")
public class RoleCreateDTO {

    @NotBlank(message = "角色编码不能为空")
    @Size(min = 1, max = 50, message = "角色编码长度必须在1-50字符之间")
    @Schema(description = "角色编码")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 100, message = "角色名称长度必须在1-100字符之间")
    @Schema(description = "角色名称")
    private String name;

    @Size(max = 255, message = "角色描述不能超过255字符")
    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否启用")
    private Boolean enable;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}

