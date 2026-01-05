package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户信息更新请求
 */
@Data
@Schema(description = "用户信息更新请求")
public class UserInfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "昵称")
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别: 0女 1男 2保密")
    private Byte gender;

    @Schema(description = "个人简介")
    @Size(max = 500, message = "简介长度不能超过500")
    private String intro;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "所在地")
    @Size(max = 100, message = "地址长度不能超过100")
    private String location;
}
