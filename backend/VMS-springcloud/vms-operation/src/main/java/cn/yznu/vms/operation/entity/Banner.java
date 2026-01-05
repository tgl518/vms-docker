package cn.yznu.vms.operation.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 轮播图表
 */
@Data
@TableName("banner")
@Schema(description = "轮播图表")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    @Schema(description = "标题")
    private String title;

    @TableField("img_url")
    @Schema(description = "图片URL")
    private String imgUrl;

    @TableField("link_type")
    @Schema(description = "链接类型: 0外链 1视频详情 2分类页")
    private Integer linkType;

    @TableField("link_url")
    @Schema(description = "外部链接URL")
    private String linkUrl;

    @TableField("target_id")
    @Schema(description = "目标ID(视频ID等)")
    private Long targetId;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;

    @TableField("is_show")
    @Schema(description = "是否展示: 1是 0否")
    private Integer isShow;

    @TableField("start_time")
    @Schema(description = "开始展示时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束展示时间")
    private LocalDateTime endTime;

    @TableField("deleted")
    @TableLogic
    @Schema(description = "软删除: 0未删 1已删")
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

