package cn.yznu.vms.video.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.video.entity.Category;
import cn.yznu.vms.video.service.CategoryService;
import cn.yznu.vms.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@Tag(name = "分类管理", description = "视频分类的增删改查")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final VideoService videoService;

    @Operation(summary = "获取所有启用的分类")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.list(categoryService.listEnabled());
    }

    @Operation(summary = "获取所有分类(含禁用)")
    @GetMapping("/all")
    public Result<cn.yznu.vms.common.result.PageResult<Category>> all() {
        return Result.page(categoryService.list());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @RequireAdmin("新增分类")
    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Long> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(category.getId());
    }

    @RequireAdmin("更新分类")
    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryService.updateById(category);
        return Result.success();
    }

    @RequireAdmin("删除分类")
    @Operation(summary = "删除分类", description = "会级联删除该分类下的所有视频及其关联数据")
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        // 1. 先删除该分类下的所有视频及关联数据
        videoService.deleteByCategoryId(id);
        // 2. 删除分类本身（包括图标文件）
        categoryService.deleteWithFile(id);
        log.info("已删除分类及其关联视频, categoryId={}", id);
        return Result.success();
    }
}
