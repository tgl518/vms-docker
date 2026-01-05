package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.interaction.entity.UserLike;
import cn.yznu.vms.interaction.enums.TargetTypeEnum;
import cn.yznu.vms.interaction.mapper.UserLikeMapper;
import cn.yznu.vms.interaction.service.UserLikeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户点赞服务实现
 * 支持对视频和评论的点赞/取消点赞操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {

    private final VideoClient videoClient;

    @Override
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            log.debug("检查点赞状态参数为空: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            return false;
        }

        try {
            TargetTypeEnum typeEnum = TargetTypeEnum.fromType(targetType);
            long count = count(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getTargetType, typeEnum)
                    .eq(UserLike::getTargetId, targetId));
            return count > 0;
        } catch (IllegalArgumentException e) {
            log.warn("无效的点赞目标类型: {}", targetType);
            return false;
        } catch (Exception e) {
            log.error("检查点赞状态失败: userId={}, targetType={}, targetId={}", userId, targetType, targetId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Long userId, String targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) {
            log.warn("切换点赞状态参数为空: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            throw new IllegalArgumentException("参数不能为空");
        }

        TargetTypeEnum typeEnum;
        try {
            typeEnum = TargetTypeEnum.fromType(targetType);
        } catch (IllegalArgumentException e) {
            log.warn("无效的点赞目标类型: {}", targetType);
            throw new IllegalArgumentException("无效的目标类型: " + targetType);
        }

        try {
            UserLike existing = getOne(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getTargetType, typeEnum)
                    .eq(UserLike::getTargetId, targetId));

            if (existing != null) {
                // 取消点赞
                removeById(existing.getId());
                log.info("用户取消点赞: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
                
                // 如果是视频点赞，同步减少视频点赞计数
                if (typeEnum == TargetTypeEnum.VIDEO) {
                    try {
                        videoClient.decrementLikeCount(targetId);
                    } catch (Exception e) {
                        log.warn("同步减少视频点赞计数失败: videoId={}", targetId, e);
                    }
                }
            } else {
                // 添加点赞
                UserLike like = new UserLike();
                like.setUserId(userId);
                like.setTargetType(typeEnum);
                like.setTargetId(targetId);
                save(like);
                log.info("用户点赞成功: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
                
                // 如果是视频点赞，同步增加视频点赞计数
                if (typeEnum == TargetTypeEnum.VIDEO) {
                    try {
                        videoClient.incrementLikeCount(targetId);
                    } catch (Exception e) {
                        log.warn("同步增加视频点赞计数失败: videoId={}", targetId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("切换点赞状态失败: userId={}, targetType={}, targetId={}", userId, targetType, targetId, e);
            throw new RuntimeException("操作失败，请稍后重试");
        }
    }
}
