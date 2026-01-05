package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.mapper.WatchHistoryMapper;
import cn.yznu.vms.interaction.service.WatchHistoryService;
import cn.yznu.vms.interaction.vo.HistoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WatchHistoryServiceImpl extends ServiceImpl<WatchHistoryMapper, WatchHistory> implements WatchHistoryService {

    @Override
    public void saveProgress(Long userId, Long videoId, Long episodeId, Integer watchDuration, Integer totalDuration) {
        WatchHistory existing = getOne(new LambdaQueryWrapper<WatchHistory>()
                .eq(WatchHistory::getUserId, userId)
                .eq(WatchHistory::getVideoId, videoId)
                .eq(episodeId != null, WatchHistory::getEpisodeId, episodeId));

        // 计算观看进度（秒）
        Integer watchProgress = watchDuration;

        if (existing != null) {
            existing.setWatchDuration(watchDuration);
            existing.setWatchProgress(watchProgress);
            existing.setLastWatchTime(LocalDateTime.now());
            updateById(existing);
        } else {
            WatchHistory history = new WatchHistory();
            history.setUserId(userId);
            history.setVideoId(videoId);
            history.setEpisodeId(episodeId);
            history.setWatchDuration(watchDuration);
            history.setWatchProgress(watchProgress);
            history.setLastWatchTime(LocalDateTime.now());
            save(history);
        }
    }

    @Override
    public IPage<WatchHistory> listByUserId(Long userId, Integer pageNum, Integer pageSize) {
        return page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<WatchHistory>()
                        .eq(WatchHistory::getUserId, userId)
                        .orderByDesc(WatchHistory::getLastWatchTime));
    }
    
    @Override
    public IPage<HistoryVO> listWithVideoInfo(Long userId, Integer pageNum, Integer pageSize) {
        return baseMapper.selectHistoryWithVideoInfo(new Page<>(pageNum, pageSize), userId);
    }
}
