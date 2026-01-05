package cn.yznu.vms.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频状态枚举
 * <p>
 * 0-草稿, 1-审核中, 2-已发布, 3-已下架
 */
@Getter
@AllArgsConstructor
public enum VideoStatusEnum {

    DRAFT(0, "草稿"),
    PENDING(1, "审核中"),
    PUBLISHED(2, "已发布"),
    OFFLINE(3, "已下架");

    /**
     * 数据库存储值
     */
    @EnumValue
    private final Integer value;

    /**
     * JSON 序列化值
     */
    @JsonValue
    private final String desc;

    /**
     * 根据 value 获取枚举
     */
    public static VideoStatusEnum fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (VideoStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 判断是否可见（已发布状态）
     */
    public boolean isVisible() {
        return this == PUBLISHED;
    }
}
