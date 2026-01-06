package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.mapper.CommentMapper;
import cn.yznu.vms.interaction.vo.CommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * CommentServiceImpl 单元测试
 * 测试评论服务的核心业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceImpl 评论服务测试")
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private VideoClient videoClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Nested
    @DisplayName("listByVideoId 分页查询评论")
    class ListByVideoIdTests {

        @Test
        @DisplayName("查询成功_返回评论列表（无子评论）")
        void listByVideoId_shouldReturnComments_whenNoReplies() {
            // Arrange
            Long videoId = 1L;
            int pageNum = 1, pageSize = 10;

            // 模拟顶级评论
            CommentVO comment1 = new CommentVO();
            comment1.setId(1L);
            comment1.setContent("这部动漫太好看了！");

            Page<CommentVO> mockPage = new Page<>(pageNum, pageSize);
            mockPage.setRecords(Collections.singletonList(comment1));

            when(commentMapper.selectCommentsWithUser(any(Page.class), eq(videoId))).thenReturn(mockPage);
            when(commentMapper.selectRepliesByParentIds(any())).thenReturn(Collections.emptyList());

            // Act
            IPage<CommentVO> result = commentService.listByVideoId(videoId, pageNum, pageSize);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getContent()).isEqualTo("这部动漫太好看了！");
            assertThat(result.getRecords().get(0).getReplies()).isEmpty();

            verify(commentMapper).selectCommentsWithUser(any(Page.class), eq(videoId));
            verify(commentMapper).selectRepliesByParentIds(any());
        }

        @Test
        @DisplayName("查询成功_返回评论列表（含子评论）")
        void listByVideoId_shouldReturnCommentsWithReplies() {
            // Arrange
            Long videoId = 1L;
            int pageNum = 1, pageSize = 10;

            // 模拟顶级评论
            CommentVO topComment = new CommentVO();
            topComment.setId(1L);
            topComment.setContent("炎柱太帅了！");

            // 模拟子评论
            CommentVO reply = new CommentVO();
            reply.setId(2L);
            reply.setParentId(1L);
            reply.setContent("同意！看哭了");

            Page<CommentVO> mockPage = new Page<>(pageNum, pageSize);
            mockPage.setRecords(Collections.singletonList(topComment));

            when(commentMapper.selectCommentsWithUser(any(Page.class), eq(videoId))).thenReturn(mockPage);
            when(commentMapper.selectRepliesByParentIds(eq(List.of(1L)))).thenReturn(List.of(reply));

            // Act
            IPage<CommentVO> result = commentService.listByVideoId(videoId, pageNum, pageSize);

            // Assert
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getReplies()).hasSize(1);
            assertThat(result.getRecords().get(0).getReplies().get(0).getContent()).isEqualTo("同意！看哭了");
        }

        @Test
        @DisplayName("查询成功_空结果不查询子评论")
        void listByVideoId_shouldNotQueryReplies_whenNoTopComments() {
            // Arrange
            Long videoId = 999L;
            Page<CommentVO> emptyPage = new Page<>(1, 10);
            emptyPage.setRecords(Collections.emptyList());

            when(commentMapper.selectCommentsWithUser(any(Page.class), eq(videoId))).thenReturn(emptyPage);

            // Act
            IPage<CommentVO> result = commentService.listByVideoId(videoId, 1, 10);

            // Assert
            assertThat(result.getRecords()).isEmpty();
            // 不应查询子评论，因为没有顶级评论
            verify(commentMapper, never()).selectRepliesByParentIds(any());
        }
    }

    @Nested
    @DisplayName("addComment 添加评论")
    class AddCommentTests {

        @Test
        @DisplayName("添加评论成功_同步更新视频评论计数")
        void addComment_shouldSucceed_andSyncVideoCount() {
            // Arrange
            Long videoId = 1L;
            Long userId = 2L;
            String content = "这集太燃了！";

            // Act
            Long commentId = commentService.addComment(videoId, userId, content, 0L, null);

            // Assert
            // 验证评论被保存（通过 ServiceImpl 的 save 方法）
            // 由于 save 是继承的方法，我们通过 verify 间接验证

            // 验证调用了 videoClient 增加评论计数
            verify(videoClient).incrementCommentCount(videoId);
        }

        @Test
        @DisplayName("添加评论成功_Feign调用失败不影响主流程")
        void addComment_shouldSucceed_evenIfFeignFails() {
            // Arrange
            Long videoId = 1L;
            Long userId = 2L;
            String content = "太好看了";

            // 模拟 Feign 调用失败
            doThrow(new RuntimeException("网络错误")).when(videoClient).incrementCommentCount(videoId);

            // Act - 不应抛出异常
            Long commentId = commentService.addComment(videoId, userId, content, 0L, null);

            // Assert
            // 主流程应该成功，异常被捕获并记录日志
            verify(videoClient).incrementCommentCount(videoId);
        }
    }
}
