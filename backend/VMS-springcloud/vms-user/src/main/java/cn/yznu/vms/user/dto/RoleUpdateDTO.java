package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新角色DTO
 */
@Data
@Schema(description = "更新角色请求")
public class RoleUpdateDTO {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否启用")
    private Boolean enable;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
