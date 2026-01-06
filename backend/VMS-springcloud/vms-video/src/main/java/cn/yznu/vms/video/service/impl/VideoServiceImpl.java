package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import cn.yznu.vms.video.entity.Video;
import cn.yznu.vms.video.entity.VideoEpisode;
import cn.yznu.vms.video.entity.VideoTagRel;
import cn.yznu.vms.video.feign.FileClient;
import cn.yznu.vms.video.mapper.VideoEpisodeMapper;
import cn.yznu.vms.video.mapper.VideoMapper;
import cn.yznu.vms.video.mapper.VideoTagRelMapper;
import cn.yznu.vms.video.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoEpisodeMapper videoEpisodeMapper;
    private final VideoTagRelMapper videoTagRelMapper;
    private final FileClient fileClient;

    // 系统用户ID，用于文件删除时的身份验证
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public IPage<Video> listVideos(Integer pageNum, Integer pageSize, Long categoryId, VideoStatusEnum status, String title) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Video::getStatus, status);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(Video::getTitle, title);
        }
        wrapper.orderByDesc(Video::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Cacheable(value = "hotVideos", key = "#pageNum + '-' + #pageSize", unless = "#result.records.isEmpty()")
    public IPage<Video> getHotVideos(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, VideoStatusEnum.PUBLISHED)
                .orderByDesc(Video::getViewCount);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void incrementViewCount(Long videoId) {
        update(new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, videoId)
                .setSql("view_count = view_count + 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithRelations(Long videoId) {
        // 0. 先获取视频信息，用于后续删除物理文件
        Video video = getById(videoId);

        // 1. 查询所有分集，用于删除分集的物理文件
        List<VideoEpisode> episodes = videoEpisodeMapper.selectList(
                new LambdaQueryWrapper<VideoEpisode>().eq(VideoEpisode::getVideoId, videoId)
        );

        // 2. 删除视频分集（数据库记录）
        videoEpisodeMapper.delete(new LambdaQueryWrapper<VideoEpisode>()
                .eq(VideoEpisode::getVideoId, videoId));
        log.debug("已删除视频分集, videoId={}", videoId);

        // 3. 删除视频-标签关联
        videoTagRelMapper.delete(new LambdaQueryWrapper<VideoTagRel>()
                .eq(VideoTagRel::getVideoId, videoId));
        log.debug("已删除视频标签关联, videoId={}", videoId);

        // 4. 删除视频本身（数据库记录）
        removeById(videoId);
        log.info("已级联删除视频及关联数据, videoId={}", videoId);

        // 5. 异步删除物理文件（不影响主事务）
        deleteVideoFiles(video, episodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCategoryId(Long categoryId) {
        // 1. 查询该分类下的所有视频（包含文件URL）
        List<Video> videos = list(new LambdaQueryWrapper<Video>()
                .eq(Video::getCategoryId, categoryId));

        if (videos.isEmpty()) {
            log.info("分类下无视频, categoryId={}", categoryId);
            return;
        }

        List<Long> videoIds = videos.stream()
                .map(Video::getId)
                .collect(Collectors.toList());

        // 2. 查询所有分集
        List<VideoEpisode> allEpisodes = videoEpisodeMapper.selectList(
                new LambdaQueryWrapper<VideoEpisode>().in(VideoEpisode::getVideoId, videoIds)
        );

        // 3. 删除所有视频的分集
        videoEpisodeMapper.delete(new LambdaQueryWrapper<VideoEpisode>()
                .in(VideoEpisode::getVideoId, videoIds));

        // 4. 删除所有视频的标签关联
        videoTagRelMapper.delete(new LambdaQueryWrapper<VideoTagRel>()
                .in(VideoTagRel::getVideoId, videoIds));

        // 5. 删除所有视频
        removeBatchByIds(videoIds);

        log.info("已删除分类下所有视频及关联数据, categoryId={}, 视频数={}", categoryId, videoIds.size());

        // 6. 异步删除物理文件
        for (Video video : videos) {
            List<VideoEpisode> videoEpisodes = allEpisodes.stream()
                    .filter(e -> e.getVideoId().equals(video.getId()))
                    .collect(Collectors.toList());
            deleteVideoFiles(video, videoEpisodes);
        }
    }

    /**
     * 删除视频相关的物理文件
     * 包括：视频封面、视频URL、所有分集的视频文件和封面
     */
    private void deleteVideoFiles(Video video, List<VideoEpisode> episodes) {
        if (video == null) {
            return;
        }

        // 使用 CompletableFuture 异步执行文件删除，避免阻塞主线程
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                // 删除视频封面
                deleteFileByUrl(video.getCoverUrl());

                // 删除视频主文件（如果有）
                deleteFileByUrl(video.getVideoUrl());

                // 删除所有分集的文件
                if (episodes != null) {
                    for (VideoEpisode episode : episodes) {
                        deleteFileByUrl(episode.getFileUrl());
                        deleteFileByUrl(episode.getCoverUrl());
                    }
                }
            } catch (Exception e) {
                log.error("异步删除视频文件失败: videoId={}, error={}", video.getId(), e.getMessage());
            }
        });
    }

    /**
     * 通过 URL 删除物理文件
     * 调用 vms-file 服务的删除接口
     */
    private void deleteFileByUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }
        try {
            fileClient.deleteFile(url, SYSTEM_USER_ID);
            log.debug("已删除物理文件: {}", url);
        } catch (Exception e) {
            // 文件删除失败不影响主业务，只记录日志
            log.warn("删除物理文件失败: {}, 原因: {}", url, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoTags(Long videoId, List<Long> tagIds) {
        // 1. 先删除原有的标签关联
        videoTagRelMapper.delete(new LambdaQueryWrapper<VideoTagRel>()
                .eq(VideoTagRel::getVideoId, videoId));

        // 2. 批量插入新的标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<VideoTagRel> relations = tagIds.stream()
                    .map(tagId -> {
                        VideoTagRel rel = new VideoTagRel();
                        rel.setVideoId(videoId);
                        rel.setTagId(tagId);
                        return rel;
                    })
                    .collect(Collectors.toList());
            
            // 使用 MyBatis-Plus Db 工具类进行真正的批量插入（一次 SQL）
            Db.saveBatch(relations);
            log.info("已保存视频标签关联, videoId={}, tagIds={}", videoId, tagIds);
        }
    }

    @Override
    public IPage<Video> listVideosByUserId(Integer pageNum, Integer pageSize, Long userId) {
        Page<Video> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getUserId, userId)
                .orderByDesc(Video::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void updateCount(Long videoId, String field, int delta) {
        // 安全校验：只允许更新指定的计数字段，防止 SQL 注入
        java.util.Set<String> allowedFields = java.util.Set.of("like_count", "favorite_count", "comment_count");
        if (!allowedFields.contains(field)) {
            log.warn("非法的计数字段: {}", field);
            return;
        }

        // 使用原子性 SQL 更新，确保并发安全
        // 同时防止计数变为负数
        String sql = delta >= 0
                ? field + " = " + field + " + " + delta
                : field + " = GREATEST(" + field + " + " + delta + ", 0)";

        update(new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, videoId)
                .setSql(sql));

        log.debug("更新视频计数: videoId={}, field={}, delta={}", videoId, field, delta);
    }
}
