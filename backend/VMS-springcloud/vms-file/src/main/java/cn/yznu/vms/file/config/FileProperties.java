package cn.yznu.vms.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * 文件上传目录（宿主机路径，Nginx 挂载读取）
     */
    private String uploadPath = "/home/sikadi/code/vedio_project/media";

    /**
     * 文件访问 URL 前缀（通过 Nginx 80 端口访问）
     */
    private String accessUrl = "http://localhost/media";

    /**
     * 最大图片大小 (MB)
     */
    private Integer maxImageSize = 10;

    /**
     * 最大视频大小 (MB)
     */
    private Integer maxVideoSize = 500;
}
