package cn.yznu.vms.user.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.annotation.RequireLogin;
import cn.yznu.vms.common.annotation.RequirePermission;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.dto.*;
import cn.yznu.vms.user.service.PermissionService;
import cn.yznu.vms.user.service.UserService;
import cn.yznu.vms.user.vo.LoginVO;
import cn.yznu.vms.user.vo.PermissionVO;
import cn.yznu.vms.user.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@Tag(name = "用户认证", description = "登录、注册、获取用户信息等接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "用户名密码登录，返回Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterDTO dto) {
        Long userId = userService.register(dto);
        return Result.success("注册成功", userId);
    }

    /**
     * 获取当前登录用户信息
     * 用户ID从请求头 X-User-Id 获取 (由网关注入)
     */
    @RequireLogin("获取个人信息")
    @Operation(summary = "获取当前用户信息", description = "需要登录，从Token中解析用户ID")
    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo(
            @Parameter(description = "用户ID (网关注入)", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    /**
     * 获取当前用户的权限菜单
     */
    @RequireLogin("获取用户权限")
    @Operation(summary = "获取当前用户权限", description = "返回当前用户的权限菜单树")
    @GetMapping("/permissions")
    public Result<java.util.List<PermissionVO>> getUserPermissions(
            @Parameter(description = "用户ID (网关注入)", hidden = true)
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        java.util.List<PermissionVO> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    /**
     * 根据用户ID获取用户信息 (公开接口)
     */
    @Operation(summary = "根据ID获取用户信息", description = "公开接口，可查看其他用户的基本信息")
    @GetMapping("/info/{userId}")
    public Result<UserVO> getUserInfoById(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        UserVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    /**
     * 更新当前用户信息
     */
    @RequireLogin("更新个人信息")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody UserInfoUpdateDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @RequireLogin("修改密码")
    @Operation(summary = "修改密码", description = "需要提供原密码")
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Parameter(description = "原密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }

    /**
     * 分页查询用户列表 (管理员功能)
     */
    @RequireAdmin("用户管理")
    @Operation(summary = "分页查询用户列表", description = "管理员功能,支持多条件筛选")
    @GetMapping("/list")
    public Result<PageResult<UserVO>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "邮箱") @RequestParam(required = false) String email,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "性别") @RequestParam(required = false) Byte gender) {

        UserQueryDTO queryDTO = new UserQueryDTO();
        queryDTO.setUsername(username);
        queryDTO.setEmail(email);
        queryDTO.setRole(role);
        queryDTO.setStatus(status);
        queryDTO.setGender(gender);

        IPage<UserVO> page = userService.listUsers(pageNo, pageSize, queryDTO);
        return Result.page(page);
    }

    /**
     * 创建用户 (管理员功能)
     */
    @RequireAdmin("创建用户")
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    @PostMapping
    public Result<Long> createUser(@Valid @RequestBody UserCreateDTO dto) {
        Long userId = userService.createUser(dto);
        return Result.success("创建成功", userId);
    }

    /**
     * 更新用户 (管理员功能)
     */
    @RequirePermission("user:edit")
    @Operation(summary = "更新用户", description = "管理员更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @RequestHeader(value = "X-User-Id") Long operatorId,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        userService.updateUser(dto, operatorId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户 (管理员功能)
     */
    @RequirePermission("user:delete")
    @Operation(summary = "删除用户", description = "管理员删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @RequestHeader(value = "X-User-Id") Long operatorId,
            @PathVariable Long id) {
        userService.deleteUser(id, operatorId);
        return Result.success("删除成功", null);
    }

    /**
     * 重置密码 (管理员功能)
     */
    @RequirePermission("user:reset_pwd")
    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @RequestHeader(value = "X-User-Id") Long operatorId,
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(id, dto.getPassword(), operatorId);
        return Result.success("密码重置成功", null);
    }

    // ==================== 统计接口 ====================

    /**
     * 获取用户总数 (供其他服务调用)
     */
    @Operation(summary = "获取用户总数", description = "供其他微服务调用")
    @GetMapping("/count")
    public Long getUserCount() {
        return userService.count();
    }

    /**
     * 获取今日新增用户数
     */
    @Operation(summary = "获取今日新增用户数", description = "供其他微服务调用")
    @GetMapping("/count/today")
    public Long getTodayNewUsers() {
        return userService.getTodayNewUsers();
    }
}
