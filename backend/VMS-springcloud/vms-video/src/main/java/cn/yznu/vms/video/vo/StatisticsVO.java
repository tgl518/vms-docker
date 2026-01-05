package cn.yznu.vms.video.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 总体统计 ==========
    /**
     * 用户总数
     */
    private Long totalUsers;
    /**
     * 视频总数
     */
    private Long totalVideos;
    /**
     * 分类总数
     */
    private Long totalCategories;
    /**
     * 标签总数
     */
    private Long totalTags;
    /**
     * 评论总数
     */
    private Long totalComments;
    /**
     * 总播放量
     */
    private Long totalViews;
    /**
     * 总点赞数
     */
    private Long totalLikes;

    // ========== 今日统计 ==========
    /**
     * 今日新增用户
     */
    private Long todayNewUsers;
    /**
     * 今日新增视频
     */
    private Long todayNewVideos;
    /**
     * 今日播放量
     */
    private Long todayViews;
    /**
     * 今日评论数
     */
    private Long todayComments;

    // ========== 趋势数据 ==========
    /**
     * 最近7天日期标签
     */
    private List<String> trendDates;
    /**
     * 最近7天播放量趋势
     */
    private List<Long> viewsTrend;
    /**
     * 最近7天新增用户趋势
     */
    private List<Long> usersTrend;
    /**
     * 最近7天新增视频趋势
     */
    private List<Long> videosTrend;

    // ========== 热门数据 ==========
    /**
     * 热门视频Top5
     */
    private List<HotVideoVO> hotVideos;
    /**
     * 热门分类
     */
    private List<HotCategoryVO> hotCategories;

    @Data
    public static class HotVideoVO implements Serializable {
        private Long id;
        private String title;
        private String coverUrl;
        private Long viewCount;
        private Long likeCount;
    }

    @Data
    public static class HotCategoryVO implements Serializable {
        private Long id;
        private String name;
        private Long videoCount;
    }
}
