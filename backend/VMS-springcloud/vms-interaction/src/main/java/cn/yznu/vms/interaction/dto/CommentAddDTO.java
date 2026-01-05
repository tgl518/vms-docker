package cn.yznu.vms.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论添加请求 DTO
 */
@Data
@Schema(description = "添加评论请求")
public class CommentAddDTO {

    @NotNull(message = "视频ID不能为空")
    @Schema(description = "视频ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long videoId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容最多500字")
    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "父评论ID（回复时填写）")
    private Long parentId;

    @Schema(description = "被回复用户ID")
    private Long replyUserId;
}
