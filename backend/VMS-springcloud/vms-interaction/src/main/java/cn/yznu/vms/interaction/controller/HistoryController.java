package cn.yznu.vms.interaction.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 观看历史控制器
 * 提供观看进度保存和历史记录查询功能
 */
@Tag(name = "观看历史", description = "观看进度保存和查询")
@Slf4j
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final WatchHistoryService historyService;

    @Operation(summary = "保存观看进度")
    @PostMapping("/progress")
    public Result<Void> saveProgress(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 未登录用户不保存进度，静默返回成功
        if (userId == null) {
            log.debug("未登录用户尝试保存观看进度，已忽略");
            return Result.success();
        }

        // 安全地获取参数，添加空值检查
        Object videoIdObj = params.get("videoId");
        Object watchDurationObj = params.get("watchDuration");
        Object totalDurationObj = params.get("totalDuration");

        // 参数校验：缺少必要参数时静默返回
        if (videoIdObj == null || watchDurationObj == null || totalDurationObj == null) {
            log.debug("保存观看进度缺少必要参数: videoId={}, watchDuration={}, totalDuration={}",
                    videoIdObj, watchDurationObj, totalDurationObj);
            return Result.success();
        }

        try {
            Long videoId = Long.parseLong(videoIdObj.toString());
            Object episodeIdObj = params.get("episodeId");
            Long episodeId = episodeIdObj != null ? Long.parseLong(episodeIdObj.toString()) : null;
            Integer watchDuration = Integer.parseInt(watchDurationObj.toString());
            Integer totalDuration = Integer.parseInt(totalDurationObj.toString());

            // 保存进度
            historyService.saveProgress(userId, videoId, episodeId, watchDuration, totalDuration);
            log.debug("保存观看进度成功: userId={}, videoId={}, episodeId={}, progress={}/{}",
                    userId, videoId, episodeId, watchDuration, totalDuration);
            return Result.success();
        } catch (NumberFormatException e) {
            log.warn("保存观看进度参数格式错误: params={}", params);
            return Result.success(); // 参数格式错误时静默返回
        } catch (Exception e) {
            log.error("保存观看进度失败: userId={}, params={}", userId, params, e);
            return Result.success(); // 保存失败也不影响用户体验
        }
    }

    @Operation(summary = "获取观看历史")
    @GetMapping("/list")
    public Result<?> list(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        if (userId == null) {
            log.debug("未登录用户尝试获取观看历史");
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        log.debug("获取观看历史: userId={}, pageNo={}, pageSize={}", userId, pageNo, pageSize);
        return Result.success(PageResult.of(historyService.listWithVideoInfo(userId, pageNo, pageSize)));
    }

    @Operation(summary = "删除观看历史")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 权限校验：未登录用户不允许删除
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 权限校验：只能删除自己的观看历史
        WatchHistory history = historyService.getById(id);
        if (history == null) {
            log.warn("观看历史不存在: id={}", id);
            return Result.success(); // 已删除或不存在，静默返回成功
        }
        if (!userId.equals(history.getUserId())) {
            log.warn("越权删除尝试: userId={}, historyId={}, ownerId={}", userId, id, history.getUserId());
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此记录");
        }
        
        log.info("删除观看历史: id={}, userId={}", id, userId);
        historyService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "清空观看历史")
    @DeleteMapping("/clear")
    public Result<Void> clear(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) {
            log.info("用户清空观看历史: userId={}", userId);
            historyService.lambdaUpdate()
                    .eq(WatchHistory::getUserId, userId)
                    .remove();
        }
        return Result.success();
    }
}

