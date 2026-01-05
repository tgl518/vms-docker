package cn.yznu.vms.video.service;

import cn.yznu.vms.video.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取仪表盘统计数据
     */
    StatisticsVO getDashboardStats();
}
