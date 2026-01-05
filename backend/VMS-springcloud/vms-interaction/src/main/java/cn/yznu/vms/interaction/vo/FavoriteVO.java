package cn.yznu.vms.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏列表VO (包含视频信息)
 */
@Data
@Schema(description = "收藏列表VO")
public class FavoriteVO {

    @Schema(description = "收藏记录ID")
    private Long id;

    @Schema(description = "视频ID")
    private Long videoId;

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "视频封面")
    private String coverUrl;

    @Schema(description = "视频时长(秒)")
    private Integer duration;

    @Schema(description = "播放量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
