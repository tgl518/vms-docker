package cn.yznu.vms.interaction.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 点赞/收藏目标类型枚举
 */
@Getter
public enum TargetTypeEnum {
    VIDEO(1, "video"),
    COMMENT(2, "comment");

    @EnumValue  // MyBatis-Plus 使用此值存入数据库
    private final Integer code;

    @JsonValue  // JSON序列化时使用此值
    private final String type;

    TargetTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    /**
     * 根据字符串类型获取枚举
     */
    public static TargetTypeEnum fromType(String type) {
        for (TargetTypeEnum e : values()) {
            if (e.type.equalsIgnoreCase(type)) {
                return e;
            }
        }
        throw new IllegalArgumentException("未知的目标类型: " + type);
    }
}
