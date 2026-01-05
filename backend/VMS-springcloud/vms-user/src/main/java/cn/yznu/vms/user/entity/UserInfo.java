package cn.yznu.vms.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户档案表
 */
@Data
@TableName("sys_user_info")
@Schema(name = "UserInfo", description = "用户档案表")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID(关联sys_user)")
    @TableId("user_id")
    private Long userId;

    @Schema(description = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "性别: 0女 1男 2保密")
    @TableField("gender")
    private Byte gender;

    @Schema(description = "个人简介")
    @TableField("intro")
    private String intro;

    @Schema(description = "生日")
    @TableField("birthday")
    private LocalDate birthday;

    @Schema(description = "所在地")
    @TableField("location")
    private String location;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

