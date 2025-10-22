package com.star.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 * 将文件上传到服务器本地目录
 * 
 * @author star
 */
@Slf4j
public class FileUploadUtil {

    /**
     * 允许上传的图片格式
     */
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

    /**
     * 默认最大文件大小：2MB
     */
    private static final long DEFAULT_MAX_SIZE = 2 * 1024 * 1024;

    /**
     * 上传文件到服务器本地目录
     *
     * @param file 上传的文件
     * @param uploadPath 上传目录（如：/opt/xuetu/uploads/avatars/）
     * @param baseUrl 基础访问URL（如：http://your-server-ip/uploads/avatars/）
     * @return 文件访问URL
     * @throws IOException 文件上传失败
     */
    public static String uploadFile(MultipartFile file, String uploadPath, String baseUrl) throws IOException {
        return uploadFile(file, uploadPath, baseUrl, DEFAULT_MAX_SIZE);
    }

    /**
     * 上传文件到服务器本地目录
     *
     * @param file 上传的文件
     * @param uploadPath 上传目录（如：/opt/xuetu/uploads/avatars/）
     * @param baseUrl 基础访问URL（如：http://your-server-ip/uploads/avatars/）
     * @param maxSize 最大文件大小（字节）
     * @return 文件访问URL
     * @throws IOException 文件上传失败
     */
    public static String uploadFile(MultipartFile file, String uploadPath, String baseUrl, long maxSize) throws IOException {
        // 1. 校验文件
        validateFile(file, maxSize);

        // 2. 创建上传目录（按日期分类）
        String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fullUploadPath = uploadPath + File.separator + dateFolder;
        File uploadDir = new File(fullUploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                log.error("创建上传目录失败: {}", fullUploadPath);
                throw new IOException("创建上传目录失败");
            }
        }

        // 3. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 4. 保存文件
        Path filePath = Paths.get(fullUploadPath, newFileName);
        file.transferTo(filePath.toFile());
        log.info("文件上传成功: {}", filePath);

        // 5. 返回访问URL
        String fileUrl = baseUrl + "/" + dateFolder + "/" + newFileName;
        // 统一使用 / 分隔符
        fileUrl = fileUrl.replace("\\", "/");
        return fileUrl;
    }

    /**
     * 上传图片文件
     *
     * @param file 上传的图片文件
     * @param uploadPath 上传目录
     * @param baseUrl 基础访问URL
     * @return 图片访问URL
     * @throws IOException 文件上传失败
     */
    public static String uploadImage(MultipartFile file, String uploadPath, String baseUrl) throws IOException {
        // 校验图片格式
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedImageExtension(extension)) {
            throw new IllegalArgumentException("不支持的图片格式，只允许上传: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }

        return uploadFile(file, uploadPath, baseUrl, DEFAULT_MAX_SIZE);
    }

    /**
     * 校验文件
     *
     * @param file 文件
     * @param maxSize 最大文件大小
     */
    private static void validateFile(MultipartFile file, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        if (file.getSize() > maxSize) {
            String maxSizeMB = String.format("%.2f", maxSize / 1024.0 / 1024.0);
            throw new IllegalArgumentException("文件大小不能超过 " + maxSizeMB + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（包含点，如 .jpg）
     */
    private static String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex).toLowerCase();
        }
        return "";
    }

    /**
     * 判断是否是允许的图片格式
     *
     * @param extension 文件扩展名
     * @return true-允许，false-不允许
     */
    private static boolean isAllowedImageExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            return false;
        }
        for (String allowed : ALLOWED_IMAGE_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return true-删除成功，false-删除失败
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功: {}", filePath);
                } else {
                    log.warn("文件删除失败: {}", filePath);
                }
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("删除文件异常: {}", filePath, e);
            return false;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / 1024.0 / 1024.0);
        } else {
            return String.format("%.2f GB", size / 1024.0 / 1024.0 / 1024.0);
        }
    }
}
