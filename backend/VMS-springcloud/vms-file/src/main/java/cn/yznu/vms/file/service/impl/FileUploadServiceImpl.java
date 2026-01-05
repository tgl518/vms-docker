package cn.yznu.vms.file.service.impl;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.utils.SnowflakeIdGenerator;
import cn.yznu.vms.file.config.FileProperties;
import cn.yznu.vms.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传服务实现
 * 仅支持本地文件上传
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileProperties fileProperties;

    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> VIDEO_TYPES = Arrays.asList("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm");

    @Override
    public String uploadImage(MultipartFile file) {
        String ext = getFileExtension(file.getOriginalFilename());
        if (!IMAGE_TYPES.contains(ext.toLowerCase())) {
            throw new BusinessException("不支持的图片格式，仅支持: " + IMAGE_TYPES);
        }
        return upload(file, "images");
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        String ext = getFileExtension(file.getOriginalFilename());
        if (!VIDEO_TYPES.contains(ext.toLowerCase())) {
            throw new BusinessException("不支持的视频格式，仅支持: " + VIDEO_TYPES);
        }
        return upload(file, "videos");
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return false;
        }
        // 从 URL 提取相对路径
        String relativePath = filePath.replace(fileProperties.getAccessUrl(), "");
        Path path = Paths.get(fileProperties.getUploadPath(), relativePath);
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("文件删除成功: {}", path);
            }
            return deleted;
        } catch (IOException e) {
            log.error("删除文件失败: {}", path, e);
            return false;
        }
    }

    /**
     * 上传本地文件（使用雪花算法命名）
     */
    private String upload(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 按日期分目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = getFileExtension(file.getOriginalFilename());

        // 使用雪花算法生成唯一文件名
        String fileName = SnowflakeIdGenerator.generateIdStr() + "." + ext;
        String relativePath = subDir + "/" + dateDir + "/" + fileName;

        Path targetPath = Paths.get(fileProperties.getUploadPath(), relativePath);
        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
            log.info("文件上传成功: {}", targetPath);
            return fileProperties.getAccessUrl() + "/" + relativePath;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
