package cn.yznu.vms.video.entity;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频主表
 */
@Data
@TableName("video")
@Schema(description = "视频主表")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @TableField("title")
    @Schema(description = "视频标题")
    private String title;

    @TableField("intro")
    @Schema(description = "视频简介")
    private String intro;

    @TableField("cover_url")
    @Schema(description = "封面图片URL")
    private String coverUrl;

    @TableField("video_url")
    @Schema(description = "单集视频地址")
    private String videoUrl;

    @TableField("category_id")
    @Schema(description = "分类ID")
    private Long categoryId;

    @TableField("user_id")
    @Schema(description = "上传者ID")
    private Long userId;

    @TableField("status")
    @Schema(description = "状态: 0草稿 1审核中 2已发布 3下架")
    private VideoStatusEnum status;

    @TableField("view_count")
    @Schema(description = "播放量")
    private Integer viewCount;

    @TableField("like_count")
    @Schema(description = "点赞数")
    private Integer likeCount;

    @TableField("favorite_count")
    @Schema(description = "收藏数")
    private Integer favoriteCount;

    @TableField("comment_count")
    @Schema(description = "评论数")
    private Integer commentCount;

    @TableField("duration")
    @Schema(description = "总时长(秒)")
    private Integer duration;

    @TableField("is_vip")
    @Schema(description = "是否VIP专享: 0否 1是")
    private Byte isVip;

    @TableField("publish_time")
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("deleted")
    @TableLogic
    @Schema(description = "软删除: 0未删 1已删")
    private Integer deleted;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

