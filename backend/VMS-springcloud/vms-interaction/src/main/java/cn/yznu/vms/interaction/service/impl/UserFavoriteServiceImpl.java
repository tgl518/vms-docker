package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.interaction.entity.UserFavorite;
import cn.yznu.vms.interaction.mapper.UserFavoriteMapper;
import cn.yznu.vms.interaction.service.UserFavoriteService;
import cn.yznu.vms.interaction.vo.FavoriteVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    private final VideoClient videoClient;

    @Override
    public boolean isFavorited(Long userId, Long videoId) {
        return count(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getVideoId, videoId)) > 0;
    }

    @Override
    public void toggleFavorite(Long userId, Long videoId) {
        UserFavorite existing = getOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getVideoId, videoId));
        if (existing != null) {
            // 取消收藏
            removeById(existing.getId());
            log.info("用户取消收藏: userId={}, videoId={}", userId, videoId);
            // 同步减少视频收藏计数
            try {
                videoClient.decrementFavoriteCount(videoId);
            } catch (Exception e) {
                log.warn("同步减少视频收藏计数失败: videoId={}", videoId, e);
            }
        } else {
            // 添加收藏
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(userId);
            favorite.setVideoId(videoId);
            save(favorite);
            log.info("用户收藏成功: userId={}, videoId={}", userId, videoId);
            // 同步增加视频收藏计数
            try {
                videoClient.incrementFavoriteCount(videoId);
            } catch (Exception e) {
                log.warn("同步增加视频收藏计数失败: videoId={}", videoId, e);
            }
        }
    }

    @Override
    public IPage<UserFavorite> listByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreateTime));
    }
    
    @Override
    public IPage<FavoriteVO> listWithVideoInfo(Long userId, Integer pageNum, Integer pageSize) {
        return baseMapper.selectFavoriteWithVideoInfo(new Page<>(pageNum, pageSize), userId);
    }
}
