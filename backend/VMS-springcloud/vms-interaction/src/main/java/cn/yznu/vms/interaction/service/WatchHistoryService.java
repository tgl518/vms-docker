package cn.yznu.vms.interaction.service;

import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.vo.HistoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WatchHistoryService extends IService<WatchHistory> {

    void saveProgress(Long userId, Long videoId, Long episodeId, Integer watchDuration, Integer totalDuration);

    IPage<WatchHistory> listByUserId(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 分页查询用户观看历史（包含视频信息）
     */
    IPage<HistoryVO> listWithVideoInfo(Long userId, Integer pageNum, Integer pageSize);
}
