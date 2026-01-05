package cn.yznu.vms.user.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.user.dto.PermissionCreateDTO;
import cn.yznu.vms.user.dto.PermissionUpdateDTO;
import cn.yznu.vms.user.service.PermissionService;
import cn.yznu.vms.user.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 * 需要管理员权限
 */
@Tag(name = "权限管理", description = "权限CRUD")
@RestController
@RequestMapping("/user/permission")
@RequiredArgsConstructor
@RequireAdmin("权限管理")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限树（用于角色编辑时选择权限）
     */
    @Operation(summary = "获取所有权限树")
    @GetMapping("/tree")
    public Result<List<PermissionVO>> getAllPermissionTree() {
        List<PermissionVO> tree = permissionService.getAllPermissionTree();
        return Result.success(tree);
    }

    /**
     * 获取菜单权限树（仅MENU类型）
     */
    @Operation(summary = "获取菜单权限树")
    @GetMapping("/menu/tree")
    public Result<List<PermissionVO>> getMenuTree() {
        List<PermissionVO> tree = permissionService.getMenuTree();
        return Result.success(tree);
    }

    /**
     * 获取某菜单下的按钮权限列表
     */
    @Operation(summary = "获取按钮权限列表")
    @GetMapping("/button/{parentId}")
    public Result<List<PermissionVO>> getButtons(
            @Parameter(description = "父菜单ID") @PathVariable Long parentId) {
        List<PermissionVO> buttons = permissionService.getButtonsByParentId(parentId);
        return Result.success(buttons);
    }

    /**
     * 创建权限
     */
    @Operation(summary = "创建权限")
    @PostMapping
    public Result<Long> createPermission(@Valid @RequestBody PermissionCreateDTO dto) {
        Long id = permissionService.createPermission(dto);
        return Result.success("创建成功", id);
    }

    /**
     * 更新权限 (PATCH)
     */
    @Operation(summary = "更新权限")
    @PatchMapping("/{id}")
    public Result<Void> updatePermission(
            @PathVariable Long id,
            @RequestBody PermissionUpdateDTO dto) {
        permissionService.updatePermission(id, dto);
        return Result.success("更新成功", null);
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success("删除成功", null);
    }

    /**
     * 清除权限缓存（调试用）
     */
    @Operation(summary = "清除权限缓存")
    @PostMapping("/cache/clear")
    public Result<Void> clearCache() {
        permissionService.clearCache();
        return Result.success("缓存已清除", null);
    }
}
