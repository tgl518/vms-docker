package cn.yznu.vms.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 * 仅支持本地文件上传
 */
public interface FileUploadService {

    /**
     * 上传图片
     */
    String uploadImage(MultipartFile file);

    /**
     * 上传视频
     */
    String uploadVideo(MultipartFile file);

    /**
     * 删除文件
     */
    boolean deleteFile(String filePath);
}
