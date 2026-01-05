package cn.yznu.vms.file.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.file.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 仅支持本地文件上传
 */
@Tag(name = "文件管理", description = "图片/视频上传删除")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", file.getOriginalFilename());
        return Result.success(result);
    }

    @Operation(summary = "上传视频")
    @PostMapping("/upload/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadVideo(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", file.getOriginalFilename());
        return Result.success(result);
    }

    @Operation(summary = "通用上传(自动识别类型)")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String ext = fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
                : "";

        String url;
        if ("jpg,jpeg,png,gif,webp".contains(ext)) {
            url = fileUploadService.uploadImage(file);
        } else if ("mp4,avi,mkv,mov,wmv,flv,webm".contains(ext)) {
            url = fileUploadService.uploadVideo(file);
        } else {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }

        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", fileName);
        return Result.success(result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Void> delete(@RequestParam String url) {
        boolean deleted = fileUploadService.deleteFile(url);
        return Result.success();
    }
}
