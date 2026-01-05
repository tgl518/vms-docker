package cn.yznu.vms.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求基类
 * 其他查询 DTO 可以继承此类
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码 (从 1 开始)
     */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    /**
     * 获取分页偏移量 (用于 LIMIT 语句)
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 校验并修正分页参数
     */
    public void validate() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大每页数量
        }
    }
}
