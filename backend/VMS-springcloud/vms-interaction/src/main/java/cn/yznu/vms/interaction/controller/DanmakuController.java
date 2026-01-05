package cn.yznu.vms.interaction.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.interaction.dto.DanmakuAddDTO;
import cn.yznu.vms.interaction.entity.Danmaku;
import cn.yznu.vms.interaction.service.DanmakuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 弹幕控制器
 */
@Tag(name = "弹幕管理", description = "弹幕的增删查")
@RestController
@RequestMapping("/danmaku")
@RequiredArgsConstructor
public class DanmakuController {

    private final DanmakuService danmakuService;

    @Operation(summary = "获取视频弹幕列表")
    @GetMapping("/video/{videoId}")
    public Result<List<Danmaku>> listByVideo(@PathVariable Long videoId) {
        return Result.success(danmakuService.listByVideoId(videoId));
    }

    @Operation(summary = "发送弹幕")
    @PostMapping
    public Result<Long> add(
            @Valid @RequestBody DanmakuAddDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long id = danmakuService.addDanmaku(
                dto.getVideoId(), userId, dto.getContent(),
                dto.getTime(), dto.getColor(), dto.getMode());
        return Result.success(id);
    }
}
