package cn.yznu.vms.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 观看历史VO (包含视频信息)
 */
@Data
@Schema(description = "观看历史VO")
public class HistoryVO {

    @Schema(description = "历史记录ID")
    private Long id;

    @Schema(description = "视频ID")
    private Long videoId;

    @Schema(description = "分集ID")
    private Long episodeId;

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "视频封面")
    private String coverUrl;

    @Schema(description = "观看时长(秒)")
    private Integer watchDuration;

    @Schema(description = "视频总时长(秒)")
    private Integer totalDuration;

    @Schema(description = "最后观看时间")
    private LocalDateTime lastWatchTime;
}
