package com.communitypension.communitypensionadmin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    /**
     * 上传文件
     *
     * @param file     文件
     * @param folder   文件夹
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 生成新的文件名
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 创建目标文件夹
        File targetFolder = new File(uploadPath + "/" + folder);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        // 创建目标文件
        File targetFile = new File(targetFolder, newFilename);
        
        try {
            // 保存文件
            file.transferTo(targetFile);
            
            // 返回文件访问URL
            return urlPrefix + "/" + folder + "/" + newFilename;
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
} 