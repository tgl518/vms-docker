package cn.yznu.vms.video.controller;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.annotation.RequireLogin;
import cn.yznu.vms.common.enums.VideoStatusEnum;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.video.entity.Video;
import cn.yznu.vms.video.entity.VideoEpisode;
import cn.yznu.vms.video.feign.UserFeignClient;
import cn.yznu.vms.video.service.TagService;
import cn.yznu.vms.video.service.VideoEpisodeService;
import cn.yznu.vms.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频控制器
 */
@Tag(name = "视频管理", description = "视频的增删改查接口")
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final VideoEpisodeService videoEpisodeService;
    private final TagService tagService;
    private final UserFeignClient userFeignClient;

    @Operation(summary = "分页查询视频列表")
    @GetMapping("/list")
    public Result<PageResult<Video>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态(0草稿1审核2发布3下架)") @RequestParam(required = false) Integer status,
            @Parameter(description = "标题关键字") @RequestParam(required = false) String title) {
        // 将 Integer 转换为枚举
        VideoStatusEnum statusEnum = status != null ? VideoStatusEnum.fromValue(status) : null;
        return Result.page(videoService.listVideos(pageNo, pageSize, categoryId, statusEnum, title));
    }

    @Operation(summary = "获取热门视频")
    @GetMapping("/hot")
    public Result<PageResult<Video>> hotVideos(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.page(videoService.getHotVideos(pageNo, pageSize));
    }

    @Operation(summary = "获取视频详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Video video = videoService.getById(id);
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        // 增加播放量
        videoService.incrementViewCount(id);

        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("episodes", videoEpisodeService.listByVideoId(id));
        result.put("tags", tagService.listByVideoId(id));
        
        // 获取投稿人信息
        if (video.getUserId() != null) {
            try {
                Map<String, Object> uploaderResp = userFeignClient.getUserInfo(video.getUserId());
                if (uploaderResp != null && uploaderResp.get("data") != null) {
                    result.put("uploader", uploaderResp.get("data"));
                }
            } catch (Exception e) {
                // 降级处理：获取用户信息失败时不影响整体返回
            }
        }
        
        return Result.success(result);
    }

    @RequireAdmin("新增视频")
    @Operation(summary = "新增视频")
    @PostMapping
    public Result<Long> add(@RequestBody cn.yznu.vms.video.dto.VideoDTO dto,
                            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // 构建Video实体
        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setIntro(dto.getIntro());
        video.setCoverUrl(dto.getCoverUrl());
        video.setVideoUrl(dto.getVideoUrl());
        video.setCategoryId(dto.getCategoryId());
        video.setStatus(dto.getStatus());
        video.setDuration(dto.getDuration());
        video.setIsVip(dto.getIsVip());


        // 设置用户ID（如果没有则使用默认管理员ID）
        video.setUserId(userId != null ? userId : 1L);
        video.setViewCount(0);
        video.setLikeCount(0);
        video.setFavoriteCount(0);
        video.setCommentCount(0);

        // 保存视频
        videoService.save(video);

        // 保存视频标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            videoService.saveVideoTags(video.getId(), dto.getTagIds());
        }

        return Result.success(video.getId());
    }

    @RequireAdmin("更新视频")
    @Operation(summary = "更新视频")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody cn.yznu.vms.video.dto.VideoDTO dto) {
        // 构建Video实体
        Video video = new Video();
        video.setId(id);
        video.setTitle(dto.getTitle());
        video.setIntro(dto.getIntro());
        video.setCoverUrl(dto.getCoverUrl());
        video.setVideoUrl(dto.getVideoUrl());
        video.setCategoryId(dto.getCategoryId());
        video.setStatus(dto.getStatus());
        video.setDuration(dto.getDuration());
        video.setIsVip(dto.getIsVip());

        // 更新视频
        videoService.updateById(video);

        // 更新视频标签关联
        videoService.saveVideoTags(id, dto.getTagIds());

        return Result.success();
    }

    @RequireAdmin("删除视频")
    @Operation(summary = "删除视频")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        videoService.deleteWithRelations(id);
        return Result.success();
    }

    @RequireLogin("获取我的视频")
    @Operation(summary = "获取我的投稿视频")
    @GetMapping("/my")
    public Result<PageResult<Video>> getMyVideos(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Result.page(videoService.listVideosByUserId(pageNo, pageSize, userId));
    }

    @Operation(summary = "获取视频分集列表")
    @GetMapping("/{videoId}/episodes")
    public Result<List<VideoEpisode>> episodes(@PathVariable Long videoId) {
        return Result.success(videoEpisodeService.listByVideoId(videoId));
    }

    @RequireAdmin("添加分集")
    @Operation(summary = "添加视频分集")
    @PostMapping("/{videoId}/episodes")
    public Result<Long> addEpisode(@PathVariable Long videoId, @RequestBody VideoEpisode episode) {
        episode.setVideoId(videoId);
        videoEpisodeService.save(episode);
        return Result.success(episode.getId());
    }

    @RequireAdmin("删除分集")
    @Operation(summary = "删除分集")
    @DeleteMapping("/episodes/{episodeId}")
    public Result<Void> deleteEpisode(@PathVariable Long episodeId) {
        videoEpisodeService.deleteWithFile(episodeId);
        return Result.success();
    }

    // ==================== 计数器接口（供其他微服务调用） ====================

    @Operation(summary = "增加评论数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/comment/incr")
    public Result<Void> incrementCommentCount(@PathVariable Long id) {
        videoService.updateCount(id, "comment_count", 1);
        return Result.success();
    }

    @Operation(summary = "减少评论数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/comment/decr")
    public Result<Void> decrementCommentCount(@PathVariable Long id) {
        videoService.updateCount(id, "comment_count", -1);
        return Result.success();
    }

    @Operation(summary = "增加点赞数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/like/incr")
    public Result<Void> incrementLikeCount(@PathVariable Long id) {
        videoService.updateCount(id, "like_count", 1);
        return Result.success();
    }

    @Operation(summary = "减少点赞数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/like/decr")
    public Result<Void> decrementLikeCount(@PathVariable Long id) {
        videoService.updateCount(id, "like_count", -1);
        return Result.success();
    }

    @Operation(summary = "增加收藏数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/favorite/incr")
    public Result<Void> incrementFavoriteCount(@PathVariable Long id) {
        videoService.updateCount(id, "favorite_count", 1);
        return Result.success();
    }

    @Operation(summary = "减少收藏数", description = "供互动服务调用")
    @PutMapping("/stats/{id}/favorite/decr")
    public Result<Void> decrementFavoriteCount(@PathVariable Long id) {
        videoService.updateCount(id, "favorite_count", -1);
        return Result.success();
    }
}
