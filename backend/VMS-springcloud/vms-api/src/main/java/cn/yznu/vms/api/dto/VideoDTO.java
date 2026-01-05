package cn.yznu.vms.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频基本信息 DTO (用于 Feign 调用返回)
 */
@Data
public class VideoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String coverUrl;
    private Long categoryId;
    private Long userId;
    private Integer viewCount;
}
