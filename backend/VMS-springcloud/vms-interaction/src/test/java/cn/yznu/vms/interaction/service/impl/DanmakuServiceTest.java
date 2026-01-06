package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.interaction.entity.Danmaku;
import cn.yznu.vms.interaction.mapper.DanmakuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DanmakuServiceImpl 单元测试
 * 测试弹幕服务的核心业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DanmakuServiceImpl 弹幕服务测试")
class DanmakuServiceTest {

    @Mock
    private DanmakuMapper danmakuMapper;

    @InjectMocks
    private DanmakuServiceImpl danmakuService;

    @Nested
    @DisplayName("listByVideoId 查询视频弹幕")
    class ListByVideoIdTests {

        @Test
        @DisplayName("查询成功_返回弹幕列表（按时间排序）")
        void listByVideoId_shouldReturnDanmakuList_orderedByTime() {
            // Arrange
            Long videoId = 1L;

            Danmaku d1 = new Danmaku();
            d1.setId(1L);
            d1.setVideoId(videoId);
            d1.setContent("炎柱出场！");
            d1.setTime(10.5f);  // 10.5秒

            Danmaku d2 = new Danmaku();
            d2.setId(2L);
            d2.setVideoId(videoId);
            d2.setContent("燃起来了🔥");
            d2.setTime(20.2f);  // 20.2秒

            when(danmakuMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(d1, d2));

            // Act
            List<Danmaku> result = danmakuService.listByVideoId(videoId);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getContent()).isEqualTo("炎柱出场！");
            assertThat(result.get(0).getTime()).isEqualTo(10.5f);
            assertThat(result.get(1).getTime()).isEqualTo(20.2f);

            verify(danmakuMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无弹幕_返回空列表")
        void listByVideoId_shouldReturnEmptyList_whenNoDanmaku() {
            // Arrange
            when(danmakuMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            List<Danmaku> result = danmakuService.listByVideoId(999L);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("addDanmaku 发送弹幕")
    class AddDanmakuTests {

        @Test
        @DisplayName("发送弹幕成功_使用指定颜色和模式")
        void addDanmaku_shouldSucceed_withCustomColorAndMode() {
            // Arrange
            Long videoId = 1L;
            Long userId = 2L;
            String content = "太精彩了！";
            Float time = 15.5f;
            String color = "#FF4757";  // 红色
            Integer mode = 1;  // 滚动弹幕

            // Act
            Long danmakuId = danmakuService.addDanmaku(videoId, userId, content, time, color, mode);

            // Assert
            ArgumentCaptor<Danmaku> captor = ArgumentCaptor.forClass(Danmaku.class);
            verify(danmakuMapper).insert(captor.capture());

            Danmaku inserted = captor.getValue();
            assertThat(inserted.getVideoId()).isEqualTo(videoId);
            assertThat(inserted.getUserId()).isEqualTo(userId);
            assertThat(inserted.getContent()).isEqualTo(content);
            assertThat(inserted.getTime()).isEqualTo(time);
            assertThat(inserted.getColor()).isEqualTo(color);
            assertThat(inserted.getMode()).isEqualTo(mode);
            assertThat(inserted.getFontSize()).isEqualTo(25);  // 默认字号
        }

        @Test
        @DisplayName("发送弹幕成功_使用默认颜色")
        void addDanmaku_shouldUseDefaultColor_whenColorNull() {
            // Arrange
            Long videoId = 1L;
            Long userId = 2L;
            String content = "好看！";
            Float time = 5.0f;

            // Act
            danmakuService.addDanmaku(videoId, userId, content, time, null, null);

            // Assert
            ArgumentCaptor<Danmaku> captor = ArgumentCaptor.forClass(Danmaku.class);
            verify(danmakuMapper).insert(captor.capture());

            Danmaku inserted = captor.getValue();
            // 验证使用默认值
            assertThat(inserted.getColor()).isEqualTo("#FFFFFF");  // 默认白色
            assertThat(inserted.getMode()).isEqualTo(1);  // 默认滚动模式
        }

        @Test
        @DisplayName("发送弹幕成功_顶部弹幕模式")
        void addDanmaku_shouldSucceed_withTopMode() {
            // Arrange
            Integer topMode = 2;  // 假设 2 代表顶部弹幕

            // Act
            danmakuService.addDanmaku(1L, 2L, "顶部弹幕", 10.0f, "#FFFF00", topMode);

            // Assert
            ArgumentCaptor<Danmaku> captor = ArgumentCaptor.forClass(Danmaku.class);
            verify(danmakuMapper).insert(captor.capture());

            assertThat(captor.getValue().getMode()).isEqualTo(topMode);
        }
    }
}
