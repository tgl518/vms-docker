package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.interaction.entity.UserFavorite;
import cn.yznu.vms.interaction.mapper.UserFavoriteMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserFavoriteServiceImpl 单元测试
 * 测试用户收藏服务的核心业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserFavoriteServiceImpl 收藏服务测试")
class UserFavoriteServiceTest {

    @Mock
    private UserFavoriteMapper userFavoriteMapper;

    @Mock
    private VideoClient videoClient;

    @InjectMocks
    private UserFavoriteServiceImpl userFavoriteService;

    @Nested
    @DisplayName("isFavorited 检查收藏状态")
    class IsFavoritedTests {

        @Test
        @DisplayName("已收藏_返回true")
        void isFavorited_shouldReturnTrue_whenFavorited() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;

            // Mock count 方法返回 1 (已收藏)
            when(userFavoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // Act
            boolean result = userFavoriteService.isFavorited(userId, videoId);

            // Assert
            assertThat(result).isTrue();
            verify(userFavoriteMapper).selectCount(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("未收藏_返回false")
        void isFavorited_shouldReturnFalse_whenNotFavorited() {
            // Arrange
            Long userId = 1L;
            Long videoId = 999L;

            when(userFavoriteMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            // Act
            boolean result = userFavoriteService.isFavorited(userId, videoId);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("toggleFavorite 切换收藏状态")
    class ToggleFavoriteTests {

        @Test
        @DisplayName("添加收藏_当用户未收藏该视频")
        void toggleFavorite_shouldAddFavorite_whenNotFavorited() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;

            // 模拟未收藏状态
            when(userFavoriteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            userFavoriteService.toggleFavorite(userId, videoId);

            // Assert
            // 验证调用了 save (insert) 而不是 remove
            verify(userFavoriteMapper).insert(any(UserFavorite.class));
            verify(userFavoriteMapper, never()).deleteById(anyLong());

            // 验证同步增加视频收藏计数
            verify(videoClient).incrementFavoriteCount(videoId);
        }

        @Test
        @DisplayName("取消收藏_当用户已收藏该视频")
        void toggleFavorite_shouldRemoveFavorite_whenAlreadyFavorited() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;

            UserFavorite existing = new UserFavorite();
            existing.setId(100L);
            existing.setUserId(userId);
            existing.setVideoId(videoId);
            existing.setCreateTime(LocalDateTime.now());

            // 模拟已收藏状态
            when(userFavoriteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
            when(userFavoriteMapper.deleteById(100L)).thenReturn(1);

            // Act
            userFavoriteService.toggleFavorite(userId, videoId);

            // Assert
            verify(userFavoriteMapper).deleteById(100L);
            verify(userFavoriteMapper, never()).insert(any(UserFavorite.class));

            // 验证同步减少视频收藏计数
            verify(videoClient).decrementFavoriteCount(videoId);
        }

        @Test
        @DisplayName("添加收藏成功_即使Feign调用失败")
        void toggleFavorite_shouldSucceed_evenIfFeignFails() {
            // Arrange
            Long userId = 1L;
            Long videoId = 1L;

            when(userFavoriteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            doThrow(new RuntimeException("网络超时")).when(videoClient).incrementFavoriteCount(videoId);

            // Act - 不应抛出异常
            userFavoriteService.toggleFavorite(userId, videoId);

            // Assert
            verify(userFavoriteMapper).insert(any(UserFavorite.class));
            // Feign 调用失败被捕获，不影响主流程
        }
    }

    @Nested
    @DisplayName("listByUserId 查询用户收藏列表")
    class ListByUserIdTests {

        @Test
        @DisplayName("分页查询成功")
        void listByUserId_shouldReturnPagedResults() {
            // Arrange
            Long userId = 1L;
            int pageNum = 1, pageSize = 10;

            UserFavorite favorite = new UserFavorite();
            favorite.setId(1L);
            favorite.setUserId(userId);
            favorite.setVideoId(1L);

            Page<UserFavorite> mockPage = new Page<>(pageNum, pageSize);
            mockPage.setRecords(Collections.singletonList(favorite));
            mockPage.setTotal(1L);

            when(userFavoriteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mockPage);

            // Act
            IPage<UserFavorite> result = userFavoriteService.listByUserId(userId, pageNum, pageSize);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getTotal()).isEqualTo(1L);
        }
    }
}
