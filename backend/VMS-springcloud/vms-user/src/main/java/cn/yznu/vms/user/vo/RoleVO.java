package cn.yznu.vms.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色响应VO
 */
@Data
@Schema(description = "角色响应VO")
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色编码")
    private String code;

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
