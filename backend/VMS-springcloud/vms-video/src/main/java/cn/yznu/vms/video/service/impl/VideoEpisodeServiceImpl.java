package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.video.entity.VideoEpisode;
import cn.yznu.vms.video.feign.FileClient;
import cn.yznu.vms.video.mapper.VideoEpisodeMapper;
import cn.yznu.vms.video.service.VideoEpisodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoEpisodeServiceImpl extends ServiceImpl<VideoEpisodeMapper, VideoEpisode> implements VideoEpisodeService {

    private final FileClient fileClient;

    // 系统用户ID，用于文件删除时的身份验证
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public List<VideoEpisode> listByVideoId(Long videoId) {
        return list(new LambdaQueryWrapper<VideoEpisode>()
                .eq(VideoEpisode::getVideoId, videoId)
                .orderByAsc(VideoEpisode::getSort));
    }

    @Override
    public void deleteWithFile(Long episodeId) {
        // 1. 先获取分集信息
        VideoEpisode episode = getById(episodeId);
        if (episode == null) {
            return;
        }

        // 2. 删除数据库记录
        removeById(episodeId);
        log.info("已删除分集, episodeId={}", episodeId);

        // 3. 删除物理文件
        deleteFileByUrl(episode.getFileUrl());
        deleteFileByUrl(episode.getCoverUrl());
    }

    /**
     * 通过 URL 删除物理文件
     */
    private void deleteFileByUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }
        try {
            fileClient.deleteFile(url, SYSTEM_USER_ID);
            log.debug("已删除物理文件: {}", url);
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}, 原因: {}", url, e.getMessage());
        }
    }
}

