package cn.yznu.vms.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送弹幕请求DTO
 */
@Data
@Schema(description = "发送弹幕请求")
public class DanmakuAddDTO {

    @NotNull(message = "视频ID不能为空")
    @Schema(description = "视频ID")
    private Long videoId;

    @NotBlank(message = "弹幕内容不能为空")
    @Size(max = 100, message = "弹幕内容不能超过100个字符")
    @Schema(description = "弹幕内容")
    private String content;

    @NotNull(message = "弹幕时间不能为空")
    @Schema(description = "弹幕出现时间(秒)")
    private Float time;

    @Schema(description = "弹幕颜色(HEX)", example = "#FFFFFF")
    private String color = "#FFFFFF";

    @Schema(description = "弹幕模式: 1滚动 2顶部 3底部", example = "1")
    private Integer mode = 1;
}
