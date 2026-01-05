package cn.yznu.vms.interaction.mapper;

import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.vo.CommentVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询顶级评论（关联用户信息）
     */
    @Select("""
        SELECT c.id, c.video_id, c.user_id, c.content, c.parent_id,
               c.reply_user_id, c.like_count, c.create_time,
               ui.nickname, ui.avatar
        FROM comment c
        LEFT JOIN sys_user_info ui ON c.user_id = ui.user_id
        WHERE c.video_id = #{videoId}
          AND c.status = 1
          AND c.deleted = 0
          AND (c.parent_id IS NULL OR c.parent_id = 0)
        ORDER BY c.create_time DESC
    """)
    IPage<CommentVO> selectCommentsWithUser(IPage<?> page, @Param("videoId") Long videoId);

    /**
     * 查询子评论（回复）- 单个父评论
     */
    @Select("""
        SELECT c.id, c.video_id, c.user_id, c.content, c.parent_id,
               c.reply_user_id, c.like_count, c.create_time,
               ui.nickname, ui.avatar,
               rui.nickname as reply_nickname
        FROM comment c
        LEFT JOIN sys_user_info ui ON c.user_id = ui.user_id
        LEFT JOIN sys_user_info rui ON c.reply_user_id = rui.user_id
        WHERE c.parent_id = #{parentId}
          AND c.status = 1
          AND c.deleted = 0
        ORDER BY c.create_time ASC
    """)
    List<CommentVO> selectReplies(@Param("parentId") Long parentId);

    /**
     * 批量查询子评论（回复）- 优化 N+1 问题
     * 一次查询多个父评论的所有子评论
     */
    @Select("""
        <script>
        SELECT c.id, c.video_id, c.user_id, c.content, c.parent_id,
               c.reply_user_id, c.like_count, c.create_time,
               ui.nickname, ui.avatar,
               rui.nickname as reply_nickname
        FROM comment c
        LEFT JOIN sys_user_info ui ON c.user_id = ui.user_id
        LEFT JOIN sys_user_info rui ON c.reply_user_id = rui.user_id
        WHERE c.parent_id IN
            <foreach collection="parentIds" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
          AND c.status = 1
          AND c.deleted = 0
        ORDER BY c.parent_id, c.create_time ASC
        </script>
    """)
    List<CommentVO> selectRepliesByParentIds(@Param("parentIds") List<Long> parentIds);
}

