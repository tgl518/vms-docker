package cn.yznu.vms.user.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.user.dto.RoleCreateDTO;
import cn.yznu.vms.user.dto.RoleUpdateDTO;
import cn.yznu.vms.user.service.RoleService;
import cn.yznu.vms.user.vo.RoleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 * 需要管理员权限
 */
@Tag(name = "角色管理", description = "角色CRUD及权限分配")
@RestController
@RequestMapping("/user/role")
@RequiredArgsConstructor
@RequireAdmin("角色管理")
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public Result<PageResult<RoleVO>> listRoles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "角色名") @RequestParam(required = false) String name,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enable) {
        
        IPage<RoleVO> page = roleService.listRoles(pageNo, pageSize, name, enable);
        return Result.page(page);
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<RoleVO> getRoleById(@PathVariable Long id) {
        RoleVO vo = roleService.getRoleById(id);
        return Result.success(vo);
    }

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody RoleCreateDTO dto) {
        Long id = roleService.createRole(dto);
        return Result.success("创建成功", id);
    }

    /**
     * 更新角色 (PATCH)
     */
    @Operation(summary = "更新角色")
    @PatchMapping("/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody RoleUpdateDTO dto) {
        roleService.updateRole(id, dto);
        return Result.success("更新成功", null);
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取角色下的用户ID列表
     */
    @Operation(summary = "获取角色下的用户")
    @GetMapping("/{id}/users")
    public Result<List<Long>> getRoleUsers(@PathVariable Long id) {
        List<Long> userIds = roleService.getRoleUserIds(id);
        return Result.success(userIds);
    }

    /**
     * 给角色添加用户
     */
    @Operation(summary = "给角色添加用户")
    @PatchMapping("/users/add/{roleId}")
    public Result<Void> addRoleUsers(
            @PathVariable Long roleId,
            @RequestBody Map<String, List<Long>> body) {
        List<Long> userIds = body.get("userIds");
        roleService.addRoleUsers(roleId, userIds);
        return Result.success("添加成功", null);
    }

    /**
     * 从角色移除用户
     */
    @Operation(summary = "从角色移除用户")
    @PatchMapping("/users/remove/{roleId}")
    public Result<Void> removeRoleUsers(
            @PathVariable Long roleId,
            @RequestBody Map<String, List<Long>> body) {
        List<Long> userIds = body.get("userIds");
        roleService.removeRoleUsers(roleId, userIds);
        return Result.success("移除成功", null);
    }
}
