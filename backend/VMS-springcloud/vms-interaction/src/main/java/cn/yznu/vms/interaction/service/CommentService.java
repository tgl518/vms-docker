package cn.yznu.vms.interaction.service;

import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.vo.CommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {

    /**
     * 分页查询视频评论（带用户信息和子评论）
     */
    IPage<CommentVO> listByVideoId(Long videoId, Integer pageNum, Integer pageSize);

    /**
     * 添加评论
     */
    Long addComment(Long videoId, Long userId, String content, Long parentId, Long replyUserId);
}

