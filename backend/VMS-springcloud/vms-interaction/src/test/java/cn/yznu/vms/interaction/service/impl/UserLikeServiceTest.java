package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.interaction.entity.UserLike;
import cn.yznu.vms.interaction.enums.TargetTypeEnum;
import cn.yznu.vms.interaction.mapper.UserLikeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserLikeServiceImpl 单元测试
 * 测试用户点赞服务的核心业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserLikeServiceImpl 点赞服务测试")
class UserLikeServiceTest {

    @Mock
    private UserLikeMapper userLikeMapper;

    @Mock
    private VideoClient videoClient;

    @InjectMocks
    private UserLikeServiceImpl userLikeService;

    @Nested
    @DisplayName("isLiked 检查点赞状态")
    class IsLikedTests {

        @Test
        @DisplayName("已点赞视频_返回true")
        void isLiked_shouldReturnTrue_whenVideoLiked() {
            // Arrange
            Long userId = 1L;
            String targetType = "video";
            Long targetId = 1L;

            when(userLikeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // Act
            boolean result = userLikeService.isLiked(userId, targetType, targetId);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("未点赞视频_返回false")
        void isLiked_shouldReturnFalse_whenVideoNotLiked() {
            // Arrange
            when(userLikeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            // Act
            boolean result = userLikeService.isLiked(1L, "video", 1L);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("已点赞评论_返回true")
        void isLiked_shouldReturnTrue_whenCommentLiked() {
            // Arrange
            when(userLikeMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // Act
            boolean result = userLikeService.isLiked(1L, "comment", 100L);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("参数为空_返回false")
        void isLiked_shouldReturnFalse_whenParamsNull() {
            // Act & Assert
            assertThat(userLikeService.isLiked(null, "video", 1L)).isFalse();
            assertThat(userLikeService.isLiked(1L, null, 1L)).isFalse();
            assertThat(userLikeService.isLiked(1L, "video", null)).isFalse();
        }

        @Test
        @DisplayName("无效目标类型_返回false")
        void isLiked_shouldReturnFalse_whenInvalidTargetType() {
            // Act
            boolean result = userLikeService.isLiked(1L, "invalid_type", 1L);

            // Assert
            assertThat(result).isFalse();
            // 不应查询数据库
            verify(userLikeMapper, never()).selectCount(any());
        }
    }

    @Nested
    @DisplayName("toggleLike 切换点赞状态")
    class ToggleLikeTests {

        @Test
        @DisplayName("点赞视频_当未点赞")
        void toggleLike_shouldAddLike_whenNotLiked() {
            // Arrange
            Long userId = 1L;
            String targetType = "video";
            Long targetId = 1L;

            // 模拟未点赞
            when(userLikeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            userLikeService.toggleLike(userId, targetType, targetId);

            // Assert
            // 验证插入新记录
            ArgumentCaptor<UserLike> captor = ArgumentCaptor.forClass(UserLike.class);
            verify(userLikeMapper).insert(captor.capture());

            UserLike inserted = captor.getValue();
            assertThat(inserted.getUserId()).isEqualTo(userId);
            assertThat(inserted.getTargetType()).isEqualTo(TargetTypeEnum.VIDEO);
            assertThat(inserted.getTargetId()).isEqualTo(targetId);

            // 验证同步增加视频点赞计数
            verify(videoClient).incrementLikeCount(targetId);
        }

        @Test
        @DisplayName("取消点赞视频_当已点赞")
        void toggleLike_shouldRemoveLike_whenAlreadyLiked() {
            // Arrange
            Long userId = 1L;
            Long targetId = 1L;

            UserLike existing = new UserLike();
            existing.setId(100L);
            existing.setUserId(userId);
            existing.setTargetType(TargetTypeEnum.VIDEO);
            existing.setTargetId(targetId);

            when(userLikeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
            when(userLikeMapper.deleteById(100L)).thenReturn(1);

            // Act
            userLikeService.toggleLike(userId, "video", targetId);

            // Assert
            verify(userLikeMapper).deleteById(100L);
            verify(userLikeMapper, never()).insert(any(UserLike.class));

            // 验证同步减少视频点赞计数
            verify(videoClient).decrementLikeCount(targetId);
        }

        @Test
        @DisplayName("点赞评论_不调用VideoClient")
        void toggleLike_shouldNotCallVideoClient_whenLikingComment() {
            // Arrange
            when(userLikeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // Act
            userLikeService.toggleLike(1L, "comment", 100L);

            // Assert
            verify(userLikeMapper).insert(any(UserLike.class));
            // 评论点赞不应调用 VideoClient
            verify(videoClient, never()).incrementLikeCount(any());
            verify(videoClient, never()).decrementLikeCount(any());
        }

        @Test
        @DisplayName("参数为空_抛出异常")
        void toggleLike_shouldThrowException_whenParamsNull() {
            // Act & Assert
            assertThatThrownBy(() -> userLikeService.toggleLike(null, "video", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("参数不能为空");

            assertThatThrownBy(() -> userLikeService.toggleLike(1L, null, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("无效目标类型_抛出异常")
        void toggleLike_shouldThrowException_whenInvalidTargetType() {
            // Act & Assert
            assertThatThrownBy(() -> userLikeService.toggleLike(1L, "invalid", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("无效的目标类型");
        }

        @Test
        @DisplayName("点赞成功_即使Feign调用失败")
        void toggleLike_shouldSucceed_evenIfFeignFails() {
            // Arrange
            when(userLikeMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
            doThrow(new RuntimeException("Feign 调用失败")).when(videoClient).incrementLikeCount(1L);

            // Act - 不应抛出异常（异常被捕获）
            userLikeService.toggleLike(1L, "video", 1L);

            // Assert
            verify(userLikeMapper).insert(any(UserLike.class));
        }
    }
}
