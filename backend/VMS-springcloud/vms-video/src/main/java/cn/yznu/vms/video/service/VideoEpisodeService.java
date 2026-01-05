package cn.yznu.vms.video.service;

import cn.yznu.vms.video.entity.VideoEpisode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VideoEpisodeService extends IService<VideoEpisode> {

    /**
     * 根据视频ID获取分集列表
     */
    List<VideoEpisode> listByVideoId(Long videoId);

    /**
     * 删除分集并同步删除物理文件
     */
    void deleteWithFile(Long episodeId);
}

