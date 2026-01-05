package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.interaction.entity.Danmaku;
import cn.yznu.vms.interaction.mapper.DanmakuMapper;
import cn.yznu.vms.interaction.service.DanmakuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanmakuServiceImpl extends ServiceImpl<DanmakuMapper, Danmaku> implements DanmakuService {

    @Override
    public List<Danmaku> listByVideoId(Long videoId) {
        return list(new LambdaQueryWrapper<Danmaku>()
                .eq(Danmaku::getVideoId, videoId)
                .orderByAsc(Danmaku::getTime));
    }

    @Override
    public Long addDanmaku(Long videoId, Long userId, String content, Float time, String color, Integer mode) {
        Danmaku danmaku = new Danmaku();
        danmaku.setVideoId(videoId);
        danmaku.setUserId(userId);
        danmaku.setContent(content);
        danmaku.setTime(time);
        danmaku.setColor(color != null ? color : "#FFFFFF");
        danmaku.setMode(mode != null ? mode : 1);
        danmaku.setFontSize(25);
        save(danmaku);
        return danmaku.getId();
    }
}
