package cn.yznu.vms.interaction.service.impl;

import cn.yznu.vms.api.client.VideoClient;
import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.mapper.CommentMapper;
import cn.yznu.vms.interaction.service.CommentService;
import cn.yznu.vms.interaction.vo.CommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final VideoClient videoClient;

    @Override
    public IPage<CommentVO> listByVideoId(Long videoId, Integer pageNum, Integer pageSize) {
        // 1. 查询顶级评论（带用户信息）
        IPage<CommentVO> page = commentMapper.selectCommentsWithUser(
                new Page<>(pageNum, pageSize), videoId);
        
        List<CommentVO> topComments = page.getRecords();
        if (topComments.isEmpty()) {
            return page;
        }
        
        // 2. 批量查询所有顶级评论的子评论 (优化 N+1 问题)
        List<Long> parentIds = topComments.stream()
                .map(CommentVO::getId)
                .collect(Collectors.toList());
        
        List<CommentVO> allReplies = commentMapper.selectRepliesByParentIds(parentIds);
        
        // 3. 按父评论ID分组
        Map<Long, List<CommentVO>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(CommentVO::getParentId));
        
        // 4. 为每个顶级评论设置子评论列表
        for (CommentVO comment : topComments) {
            comment.setReplies(repliesMap.getOrDefault(comment.getId(), Collections.emptyList()));
        }
        
        return page;
    }

    @Override
    public Long addComment(Long videoId, Long userId, String content, Long parentId, Long replyUserId) {
        Comment comment = new Comment();
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setReplyUserId(replyUserId);
        comment.setLikeCount(0);
        comment.setStatus(StatusEnum.ENABLED);
        save(comment);
        
        // 同步增加视频评论计数
        try {
            videoClient.incrementCommentCount(videoId);
            log.info("评论发表成功，已同步增加视频评论计数: videoId={}", videoId);
        } catch (Exception e) {
            log.warn("同步增加视频评论计数失败: videoId={}", videoId, e);
        }
        
        return comment.getId();
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        // 获取评论信息以获取 videoId
        Comment comment = getById(id);
        boolean result = super.removeById(id);
        
        // 删除成功后同步减少视频评论计数
        if (result && comment != null) {
            try {
                videoClient.decrementCommentCount(comment.getVideoId());
                log.info("评论删除成功，已同步减少视频评论计数: videoId={}", comment.getVideoId());
            } catch (Exception e) {
                log.warn("同步减少视频评论计数失败: videoId={}", comment.getVideoId(), e);
            }
        }
        
        return result;
    }
}
