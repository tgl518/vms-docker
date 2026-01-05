package cn.yznu.vms.api.client;

import cn.yznu.vms.api.dto.VideoDTO;
import cn.yznu.vms.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 视频服务 Feign Client
 * <p>
 * 其他服务通过此接口调用 vms-video 服务
 */
@FeignClient(name = "vms-video", path = "/video")
public interface VideoClient {

    /**
     * 根据视频ID获取视频信息
     */
    @GetMapping("/detail/{id}")
    Result<VideoDTO> getVideoById(@PathVariable("id") Long id);

    // ==================== 计数器操作 ====================

    /**
     * 增加视频评论数
     */
    @PutMapping("/stats/{id}/comment/incr")
    Result<Void> incrementCommentCount(@PathVariable("id") Long id);

    /**
     * 减少视频评论数
     */
    @PutMapping("/stats/{id}/comment/decr")
    Result<Void> decrementCommentCount(@PathVariable("id") Long id);

    /**
     * 增加视频点赞数
     */
    @PutMapping("/stats/{id}/like/incr")
    Result<Void> incrementLikeCount(@PathVariable("id") Long id);

    /**
     * 减少视频点赞数
     */
    @PutMapping("/stats/{id}/like/decr")
    Result<Void> decrementLikeCount(@PathVariable("id") Long id);

    /**
     * 增加视频收藏数
     */
    @PutMapping("/stats/{id}/favorite/incr")
    Result<Void> incrementFavoriteCount(@PathVariable("id") Long id);

    /**
     * 减少视频收藏数
     */
    @PutMapping("/stats/{id}/favorite/decr")
    Result<Void> decrementFavoriteCount(@PathVariable("id") Long id);
}
