package cn.yznu.vms.interaction.service;

import cn.yznu.vms.interaction.entity.Danmaku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DanmakuService extends IService<Danmaku> {

    /**
     * 获取视频的所有弹幕
     */
    List<Danmaku> listByVideoId(Long videoId);

    /**
     * 发送弹幕
     */
    Long addDanmaku(Long videoId, Long userId, String content, Float time, String color, Integer mode);
}
