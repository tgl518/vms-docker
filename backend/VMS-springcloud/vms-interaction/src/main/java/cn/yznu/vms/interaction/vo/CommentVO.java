package cn.yznu.vms.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象（包含用户信息）
 */
@Data
@Schema(description = "评论VO(包含用户信息)")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "视频ID")
    private Long videoId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "父评论ID(0=顶级)")
    private Long parentId;

    @Schema(description = "回复目标用户ID")
    private Long replyUserId;

    @Schema(description = "回复目标用户昵称")
    private String replyNickname;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子评论列表(回复)")
    private List<CommentVO> replies;
}
