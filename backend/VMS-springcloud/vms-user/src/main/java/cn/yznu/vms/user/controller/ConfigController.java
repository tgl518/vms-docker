package cn.yznu.vms.user.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.user.dto.ConfigCreateDTO;
import cn.yznu.vms.user.dto.ConfigUpdateDTO;
import cn.yznu.vms.user.service.ConfigService;
import cn.yznu.vms.user.vo.ConfigVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器
 * 需要管理员权限
 */
@Tag(name = "系统配置", description = "系统配置CRUD")
@RestController
@RequestMapping("/user/config")
@RequiredArgsConstructor
@RequireAdmin("系统配置")
public class ConfigController {

    private final ConfigService configService;

    /**
     * 分页查询配置列表
     */
    @Operation(summary = "分页查询配置列表")
    @GetMapping("/page")
    public Result<PageResult<ConfigVO>> listConfigs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "配置键") @RequestParam(required = false) String configKey,
            @Parameter(description = "类型") @RequestParam(required = false) String configType) {
        
        IPage<ConfigVO> page = configService.listConfigs(pageNo, pageSize, configKey, configType);
        return Result.page(page);
    }

    /**
     * 获取配置详情
     */
    @Operation(summary = "获取配置详情")
    @GetMapping("/{id}")
    public Result<ConfigVO> getConfigById(@PathVariable Long id) {
        ConfigVO vo = configService.getConfigById(id);
        return Result.success(vo);
    }

    /**
     * 根据Key获取配置值
     */
    @Operation(summary = "根据Key获取配置值")
    @GetMapping("/key/{key}")
    public Result<String> getConfigByKey(@PathVariable String key) {
        String value = configService.getConfigValueByKey(key);
        return Result.success(value);
    }

    /**
     * 创建配置
     */
    @Operation(summary = "创建配置")
    @PostMapping
    public Result<Long> createConfig(@Valid @RequestBody ConfigCreateDTO dto) {
        Long id = configService.createConfig(dto);
        return Result.success("创建成功", id);
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置")
    @PutMapping("/{id}")
    public Result<Void> updateConfig(@PathVariable Long id, @RequestBody ConfigUpdateDTO dto) {
        configService.updateConfig(id, dto);
        return Result.success("更新成功", null);
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success("删除成功", null);
    }
}
