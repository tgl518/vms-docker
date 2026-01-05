package cn.yznu.vms.video.dto;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 视频创建/更新 DTO
 */
@Data
@Schema(description = "视频创建/更新请求")
public class VideoDTO {

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "视频简介")
    private String intro;

    @Schema(description = "封面图片URL")
    private String coverUrl;

    @Schema(description = "单集视频地址")
    private String videoUrl;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "状态")
    private VideoStatusEnum status;

    @Schema(description = "总时长(秒)")
    private Integer duration;

    @Schema(description = "是否VIP专享: 0否 1是")
    private Byte isVip;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
