package cn.yznu.vms.operation.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.operation.entity.Banner;
import cn.yznu.vms.operation.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@Tag(name = "轮播图管理", description = "首页轮播图的增删改查")
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "获取当前展示的轮播图")
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        return Result.list(bannerService.listActive());
    }

    @Operation(summary = "获取所有轮播图(管理)")
    @GetMapping("/all")
    public Result<cn.yznu.vms.common.result.PageResult<Banner>> all() {
        return Result.page(bannerService.list());
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    public Result<Banner> detail(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return Result.success(banner);
    }

    @RequireAdmin("新增轮播图")
    @Operation(summary = "新增轮播图")
    @PostMapping
    public Result<Long> add(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success(banner.getId());
    }

    @RequireAdmin("更新轮播图")
    @Operation(summary = "更新轮播图")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.updateById(banner);
        return Result.success();
    }

    @RequireAdmin("删除轮播图")
    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.deleteWithFile(id);
        return Result.success();
    }
}
