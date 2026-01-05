package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.video.entity.Category;
import cn.yznu.vms.video.entity.Video;
import cn.yznu.vms.video.feign.UserFeignClient;
import cn.yznu.vms.video.mapper.CategoryMapper;
import cn.yznu.vms.video.mapper.TagMapper;
import cn.yznu.vms.video.mapper.VideoMapper;
import cn.yznu.vms.video.service.StatisticsService;
import cn.yznu.vms.video.vo.StatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final VideoMapper videoMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final UserFeignClient userFeignClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATS_CACHE_KEY = "vms:stats:dashboard";

    @Override
    public StatisticsVO getDashboardStats() {
        // 尝试从缓存获取
        Object cached = redisTemplate.opsForValue().get(STATS_CACHE_KEY);
        if (cached instanceof StatisticsVO) {
            return (StatisticsVO) cached;
        }

        StatisticsVO stats = new StatisticsVO();

        // ========== 总体统计（使用 SQL 聚合，避免加载全表） ==========
        Map<String, Object> aggregateStats = videoMapper.selectAggregateStats();
        stats.setTotalVideos(((Number) aggregateStats.get("totalVideos")).longValue());
        stats.setTotalViews(((Number) aggregateStats.get("totalViews")).longValue());
        stats.setTotalLikes(((Number) aggregateStats.get("totalLikes")).longValue());

        // 分类和标签统计
        stats.setTotalCategories((long) categoryMapper.selectCount(null));
        stats.setTotalTags((long) tagMapper.selectCount(null));

        // 用户统计（通过Feign调用）
        try {
            Long userCount = userFeignClient.getUserCount();
            stats.setTotalUsers(userCount != null ? userCount : 0L);
        } catch (Exception e) {
            log.warn("获取用户统计失败", e);
            stats.setTotalUsers(0L);
        }

        // 评论统计（暂时设为0，后续可扩展）
        stats.setTotalComments(0L);

        // ========== 今日统计（使用 SQL 查询）==========
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long todayVideos = videoMapper.countVideosSince(todayStart);
        stats.setTodayNewVideos(todayVideos != null ? todayVideos : 0L);
        stats.setTodayNewUsers(0L); // 需要用户服务支持
        stats.setTodayViews(0L);    // 需要日志支持
        stats.setTodayComments(0L);

        // ========== 趋势数据（最近7天，使用 SQL 按天统计） ==========
        List<String> dates = new ArrayList<>();
        List<Long> videosTrend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);

            Long dayVideos = videoMapper.countVideosBetween(dayStart, dayEnd);
            videosTrend.add(dayVideos != null ? dayVideos : 0L);
        }

        stats.setTrendDates(dates);
        stats.setVideosTrend(videosTrend);
        stats.setViewsTrend(Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L)); // 简化处理
        stats.setUsersTrend(Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L));

        // ========== 热门数据（优化：只查询 Top5，不加载全表）==========
        // 热门视频 Top5（按播放量排序，只查前5条）
        LambdaQueryWrapper<Video> hotVideoWrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getDeleted, 0)
                .orderByDesc(Video::getViewCount)
                .last("LIMIT 5");
        List<Video> topVideos = videoMapper.selectList(hotVideoWrapper);
        
        List<StatisticsVO.HotVideoVO> hotVideos = topVideos.stream()
                .map(v -> {
                    StatisticsVO.HotVideoVO hv = new StatisticsVO.HotVideoVO();
                    hv.setId(v.getId());
                    hv.setTitle(v.getTitle());
                    hv.setCoverUrl(v.getCoverUrl());
                    hv.setViewCount(v.getViewCount() != null ? v.getViewCount().longValue() : 0L);
                    hv.setLikeCount(v.getLikeCount() != null ? v.getLikeCount().longValue() : 0L);
                    return hv;
                })
                .collect(Collectors.toList());
        stats.setHotVideos(hotVideos);

        // 热门分类（按视频数量，使用SQL分组统计）
        List<Category> categories = categoryMapper.selectList(null);
        
        // 为每个分类统计视频数量（使用单独的count查询，比加载全表更高效）
        List<StatisticsVO.HotCategoryVO> hotCategories = categories.stream()
                .map(c -> {
                    StatisticsVO.HotCategoryVO hc = new StatisticsVO.HotCategoryVO();
                    hc.setId(c.getId());
                    hc.setName(c.getName());
                    // 使用 count 查询而非加载全部视频
                    Long count = videoMapper.selectCount(
                            new LambdaQueryWrapper<Video>()
                                    .eq(Video::getCategoryId, c.getId())
                                    .eq(Video::getDeleted, 0));
                    hc.setVideoCount(count != null ? count : 0L);
                    return hc;
                })
                .sorted((a, b) -> Long.compare(b.getVideoCount(), a.getVideoCount()))
                .limit(5)
                .collect(Collectors.toList());
        stats.setHotCategories(hotCategories);

        // 缓存5分钟
        redisTemplate.opsForValue().set(STATS_CACHE_KEY, stats, 5, TimeUnit.MINUTES);

        return stats;
    }
}

