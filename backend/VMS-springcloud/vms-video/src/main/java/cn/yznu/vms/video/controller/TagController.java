package cn.yznu.vms.video.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.video.entity.Tag;
import cn.yznu.vms.video.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "视频标签的增删改查")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取所有标签")
    @GetMapping("/list")
    public Result<cn.yznu.vms.common.result.PageResult<Tag>> list() {
        return Result.page(tagService.list());
    }

    @Operation(summary = "获取热门标签")
    @GetMapping("/hot")
    public Result<List<Tag>> hot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(tagService.listHot(limit));
    }

    @RequireAdmin("新增标签")
    @Operation(summary = "新增标签")
    @PostMapping
    public Result<Long> add(@RequestBody Tag tag) {
        tag.setUseCount(0);
        tagService.save(tag);
        return Result.success(tag.getId());
    }

    @RequireAdmin("更新标签")
    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        tagService.updateById(tag);
        return Result.success();
    }

    @RequireAdmin("删除标签")
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.deleteWithRelations(id);
        return Result.success();
    }
}
