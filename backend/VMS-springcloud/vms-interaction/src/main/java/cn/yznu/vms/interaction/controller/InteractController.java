package cn.yznu.vms.interaction.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.entity.UserFavorite;
import cn.yznu.vms.interaction.entity.UserLike;
import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.service.CommentService;
import cn.yznu.vms.interaction.service.UserFavoriteService;
import cn.yznu.vms.interaction.service.UserLikeService;
import cn.yznu.vms.interaction.service.WatchHistoryService;
import cn.yznu.vms.interaction.vo.UserStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 互动控制器 (收藏、点赞)
 */
@Tag(name = "用户互动", description = "收藏、点赞接口")
@RestController
@RequestMapping("/interact")
@RequiredArgsConstructor
public class InteractController {

    private final UserFavoriteService favoriteService;
    private final UserLikeService likeService;
    private final CommentService commentService;
    private final WatchHistoryService watchHistoryService;

    @Operation(summary = "获取用户统计数据")
    @GetMapping("/user/stats")
    public Result<UserStatsVO> getUserStats(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        // 统计收藏数
        long favorites = favoriteService.count(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId));
        
        // 统计历史记录数
        long history = watchHistoryService.count(
                new LambdaQueryWrapper<WatchHistory>()
                        .eq(WatchHistory::getUserId, userId));
        
        // 统计点赞数
        long likes = likeService.count(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId));
        
        // 统计评论数
        long comments = commentService.count(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getUserId, userId));
        
        UserStatsVO stats = UserStatsVO.builder()
                .favorites(favorites)
                .history(history)
                .likes(likes)
                .comments(comments)
                .build();
        
        return Result.success(stats);
    }

    @Operation(summary = "切换收藏状态")
    @PostMapping("/favorite/{videoId}")
    public Result<Void> toggleFavorite(
            @PathVariable Long videoId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        favoriteService.toggleFavorite(userId, videoId);
        return Result.success();
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/favorite/check/{videoId}")
    public Result<Boolean> checkFavorite(
            @PathVariable Long videoId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.success(false);
        }
        return Result.success(favoriteService.isFavorited(userId, videoId));
    }

    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/favorites")
    public Result<?> myFavorites(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Result.success(PageResult.of(favoriteService.listWithVideoInfo(userId, pageNo, pageSize)));
    }

    @Operation(summary = "切换点赞状态")
    @PostMapping("/like/{targetType}/{targetId}")
    public Result<Void> toggleLike(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        likeService.toggleLike(userId, targetType, targetId);
        return Result.success();
    }

    @Operation(summary = "检查是否已点赞")
    @GetMapping("/like/check/{targetType}/{targetId}")
    public Result<Boolean> checkLike(
            @PathVariable String targetType,
            @PathVariable Long targetId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.success(false);
        }
        return Result.success(likeService.isLiked(userId, targetType, targetId));
    }

    @Operation(summary = "批量检查互动状态")
    @GetMapping("/status/{videoId}")
    public Result<Map<String, Boolean>> getInteractStatus(
            @PathVariable Long videoId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Map<String, Boolean> status = new HashMap<>();
        if (userId == null) {
            status.put("liked", false);
            status.put("favorited", false);
        } else {
            status.put("liked", likeService.isLiked(userId, "video", videoId));
            status.put("favorited", favoriteService.isFavorited(userId, videoId));
        }
        return Result.success(status);
    }
}
