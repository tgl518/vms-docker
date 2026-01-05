package cn.yznu.vms.video.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频标签关联表
 */
@Data
@TableName("video_tag_rel")
@Schema(description = "视频标签关联表")
public class VideoTagRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("video_id")
    @Schema(description = "视频ID")
    private Long videoId;

    @TableField("tag_id")
    @Schema(description = "标签ID")
    private Long tagId;
}
