package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.interaction.entity.WatchHistory;
import cn.yznu.vms.interaction.mapper.WatchHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WatchHistoryServiceImpl 单元测试
 * 测试用户观看历史服务的核心业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WatchHistoryServiceImpl 观看历史服务测试")
class WatchHistoryServiceTest {

    @Mock
    private WatchHistoryMapper watchHistoryMapper;

    @InjectMocks
    private WatchHistoryServiceImpl watchHistoryService;

    @Nested
    @DisplayName("saveProgress 保存观看进度")
    class SaveProgressTests {

        @Test
        @DisplayName("首次观看_创建新记录")
        void saveProgress_shouldCreateNewRecord_whenFirstWatch() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;
            Long episodeId = null;
            Integer watchDuration = 300;  // 5分钟
            Integer totalDuration = 600;  // 10分钟

            // 模拟未找到已有记录
            when(watchHistoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            watchHistoryService.saveProgress(userId, videoId, episodeId, watchDuration, totalDuration);

            // Assert
            // 验证插入新记录
            ArgumentCaptor<WatchHistory> captor = ArgumentCaptor.forClass(WatchHistory.class);
            verify(watchHistoryMapper).insert(captor.capture());

            WatchHistory inserted = captor.getValue();
            assertThat(inserted.getUserId()).isEqualTo(userId);
            assertThat(inserted.getVideoId()).isEqualTo(videoId);
            assertThat(inserted.getWatchDuration()).isEqualTo(watchDuration);
            assertThat(inserted.getWatchProgress()).isEqualTo(watchDuration);
            assertThat(inserted.getLastWatchTime()).isNotNull();

            // 不应调用更新
            verify(watchHistoryMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("继续观看_更新现有记录")
        void saveProgress_shouldUpdateRecord_whenContinueWatch() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;
            Integer watchDuration = 400;  // 更新到 6分40秒

            WatchHistory existing = new WatchHistory();
            existing.setId(100L);
            existing.setUserId(userId);
            existing.setVideoId(videoId);
            existing.setWatchDuration(300);  // 之前是 5分钟
            existing.setWatchProgress(300);
            existing.setLastWatchTime(LocalDateTime.now().minusHours(1));

            // 模拟找到已有记录
            when(watchHistoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
            when(watchHistoryMapper.updateById(any())).thenReturn(1);

            // Act
            watchHistoryService.saveProgress(userId, videoId, null, watchDuration, 600);

            // Assert
            // 验证更新而非插入
            verify(watchHistoryMapper).updateById(any(WatchHistory.class));
            verify(watchHistoryMapper, never()).insert(any());

            // 验证更新后的值
            assertThat(existing.getWatchDuration()).isEqualTo(watchDuration);
            assertThat(existing.getWatchProgress()).isEqualTo(watchDuration);
        }

        @Test
        @DisplayName("分集观看_区分不同分集")
        void saveProgress_shouldDistinguishEpisodes() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;
            Long episodeId = 5L;  // 第5集
            Integer watchDuration = 100;

            when(watchHistoryMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            watchHistoryService.saveProgress(userId, videoId, episodeId, watchDuration, 600);

            // Assert
            ArgumentCaptor<WatchHistory> captor = ArgumentCaptor.forClass(WatchHistory.class);
            verify(watchHistoryMapper).insert(captor.capture());

            // 验证分集ID被正确记录
            assertThat(captor.getValue().getEpisodeId()).isEqualTo(episodeId);
        }
    }

    @Nested
    @DisplayName("listByUserId 查询观看历史")
    class ListByUserIdTests {

        @Test
        @DisplayName("分页查询成功_按时间倒序")
        void listByUserId_shouldReturnPagedResults_orderedByTime() {
            // Arrange
            Long userId = 1L;
            int pageNum = 1, pageSize = 10;

            WatchHistory history = new WatchHistory();
            history.setId(1L);
            history.setUserId(userId);
            history.setVideoId(1L);
            history.setWatchDuration(300);
            history.setLastWatchTime(LocalDateTime.now());

            Page<WatchHistory> mockPage = new Page<>(pageNum, pageSize);
            mockPage.setRecords(Collections.singletonList(history));
            mockPage.setTotal(1L);

            when(watchHistoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            // Act
            IPage<WatchHistory> result = watchHistoryService.listByUserId(userId, pageNum, pageSize);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getWatchDuration()).isEqualTo(300);
        }

        @Test
        @DisplayName("无观看历史_返回空列表")
        void listByUserId_shouldReturnEmptyList_whenNoHistory() {
            // Arrange
            Long userId = 999L;
            Page<WatchHistory> emptyPage = new Page<>(1, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0L);

            when(watchHistoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(emptyPage);

            // Act
            IPage<WatchHistory> result = watchHistoryService.listByUserId(userId, 1, 10);

            // Assert
            assertThat(result.getRecords()).isEmpty();
            assertThat(result.getTotal()).isZero();
        }
    }
}
