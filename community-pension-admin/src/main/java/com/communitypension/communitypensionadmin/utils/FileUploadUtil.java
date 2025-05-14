package com.communitypension.communitypensionadmin.utils;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class FileUploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

    // 允许的文件类型（小写）
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".pdf", ".jpg", ".jpeg", ".png")
    );

    // 允许的MIME类型
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(
            Arrays.asList(
                    "application/pdf",
                    "image/jpeg",
                    "image/jpg",
                    "image/png"
            )
    );
    // 静态实例，用于在依赖注入失败时提供备份方案
    private static FileUploadUtil instance;
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    @Value("${file.upload.url-prefix:/upload}")
    private String urlPrefix;

    /**
     * 获取实例（用于在依赖注入失败时的备用方案）
     */
    public static FileUploadUtil getInstance() {
        if (instance == null) {
            // 如果没有通过依赖注入初始化，则创建一个默认实例
            instance = new FileUploadUtil();
            instance.uploadPath = "uploads";
            instance.urlPrefix = "/upload";

            // 创建默认上传目录
            File uploadDir = new File(instance.uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            logger.info("使用默认配置创建FileUploadUtil实例");
        }
        return instance;
    }

    /**
     * 上传文件（静态方法，便于直接调用）
     */
    public static String upload(MultipartFile file, String folder) {
        return getInstance().uploadFile(file, folder);
    }

    @PostConstruct
    public void init() {
        logger.info("FileUploadUtil初始化，上传路径: {}, URL前缀: {}", uploadPath, urlPrefix);
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                logger.info("成功创建上传目录: {}", uploadPath);
            } else {
                logger.warn("无法创建上传目录: {}", uploadPath);
            }
        }

        // 设置静态实例
        instance = this;
    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @param folder 文件夹
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("无法获取文件名");
        }

        // 获取文件扩展名并转为小写
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        // 检查文件类型是否允许
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("不支持的文件类型，只允许PDF和图片格式(jpg, jpeg, png)");
        }

        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            logger.warn("文件MIME类型可能不正确: {}", contentType);
            // 根据实际情况可以决定是否严格验证MIME类型
        }

        // 生成新的文件名
        String newFilename = UUID.randomUUID() + extension;

        // 检查配置是否有效
        if (!StringUtils.hasText(uploadPath)) {
            logger.error("上传路径未配置，使用默认值'uploads'");
            uploadPath = "uploads";
        }

        if (!StringUtils.hasText(urlPrefix)) {
            logger.error("URL前缀未配置，使用默认值'/upload'");
            urlPrefix = "/upload";
        }

        // 创建目标文件夹
        File targetFolder = new File(uploadPath + "/" + folder);
        if (!targetFolder.exists()) {
            boolean created = targetFolder.mkdirs();
            if (!created) {
                logger.error("无法创建目标目录: {}", targetFolder.getAbsolutePath());
                throw new RuntimeException("无法创建目标目录: " + targetFolder.getAbsolutePath());
            }
        }

        // 创建目标文件
        File targetFile = new File(targetFolder, newFilename);
        logger.info("保存文件到: {}", targetFile.getAbsolutePath());

        try {
            // 保存文件
            file.transferTo(targetFile);

            // 返回文件访问URL
            String fileUrl = urlPrefix + "/" + folder + "/" + newFilename;
            logger.info("文件上传成功，访问URL: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
} 