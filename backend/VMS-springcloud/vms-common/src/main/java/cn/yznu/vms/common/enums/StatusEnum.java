package cn.yznu.vms.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举（启用/禁用）
 * <p>
 * 适用于：用户状态、分类状态、评论状态等
 */
public enum StatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    /**
     * 数据库存储值
     * 同时也作为 JSON 序列化值 (0/1)
     */
    @EnumValue
    @JsonValue
    private final Integer value;

    /**
     * 描述
     */
    private final String desc;

    StatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 value 获取枚举
     */
    public static StatusEnum fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (StatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return this == ENABLED;
    }
}
