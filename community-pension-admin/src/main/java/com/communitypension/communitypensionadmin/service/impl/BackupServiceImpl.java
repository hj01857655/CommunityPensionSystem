package com.communitypension.communitypensionadmin.service.impl;

import com.communitypension.communitypensionadmin.service.BackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库备份恢复服务实现类
 */
@Service
public class BackupServiceImpl implements BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupServiceImpl.class);

    @Value("${backup.path:/backup}")
    private String backupPath;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public List<Map<String, Object>> getBackupList() throws IOException {
        Path backupDir = Paths.get(backupPath);
        
        // 确保备份目录存在
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
            return new ArrayList<>();
        }
        
        // 获取所有备份文件
        return Files.list(backupDir)
                .filter(path -> path.toString().endsWith(".sql"))
                .map(path -> {
                    Map<String, Object> backup = new HashMap<>();
                    try {
                        File file = path.toFile();
                        backup.put("id", path.getFileName().toString());
                        backup.put("fileName", path.getFileName().toString());
                        backup.put("size", formatFileSize(file.length()));
                        backup.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(new Date(file.lastModified())));
                    } catch (Exception e) {
                        log.error("获取备份文件信息失败", e);
                    }
                    return backup;
                })
                .sorted((o1, o2) -> {
                    String createTime1 = (String) o1.get("createTime");
                    String createTime2 = (String) o2.get("createTime");
                    return createTime2.compareTo(createTime1); // 按时间降序排序
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> createBackup() throws Exception {
        // 创建备份目录
        Path backupDir = Paths.get(backupPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }
        
        // 生成备份文件名
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + timestamp + ".sql";
        Path backupFile = backupDir.resolve(fileName);
        
        // 提取数据库名
        String dbName = extractDatabaseName(datasourceUrl);
        
        // 执行备份命令
        // 注意：这里使用了简化的实现，实际生产环境可能需要完整的数据库备份解决方案
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            processBuilder.command("cmd.exe", "/c", 
                String.format("mysqldump -u%s -p%s %s > %s", 
                        datasourceUsername, datasourcePassword, dbName, backupFile.toString()));
        } else {
            processBuilder.command("bash", "-c", 
                String.format("mysqldump -u%s -p%s %s > %s", 
                        datasourceUsername, datasourcePassword, dbName, backupFile.toString()));
        }
        
        // 执行备份过程
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new Exception("数据库备份失败，返回代码: " + exitCode);
        }
        
        // 返回备份文件信息
        Map<String, Object> backupInfo = new HashMap<>();
        File file = backupFile.toFile();
        backupInfo.put("id", fileName);
        backupInfo.put("fileName", fileName);
        backupInfo.put("size", formatFileSize(file.length()));
        backupInfo.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(file.lastModified())));
        
        return backupInfo;
    }

    @Override
    public void deleteBackup(String fileName) throws IOException {
        Path backupPath = Paths.get(this.backupPath, fileName);
        
        if (!Files.exists(backupPath)) {
            throw new IOException("备份文件不存在");
        }
        
        Files.delete(backupPath);
    }

    @Override
    public Resource loadBackupAsResource(String fileName) throws IOException {
        Path file = Paths.get(backupPath).resolve(fileName).normalize();
        Resource resource = new UrlResource(file.toUri());
        
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("无法读取备份文件: " + fileName);
        }
    }

    @Override
    public void restoreBackup(String backupId) throws Exception {
        Path backupFile = Paths.get(backupPath, backupId);
        
        if (!Files.exists(backupFile)) {
            throw new IOException("备份文件不存在");
        }
        
        // 提取数据库名
        String dbName = extractDatabaseName(datasourceUrl);
        
        // 执行恢复命令
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            processBuilder.command("cmd.exe", "/c", 
                String.format("mysql -u%s -p%s %s < %s", 
                        datasourceUsername, datasourcePassword, dbName, backupFile.toString()));
        } else {
            processBuilder.command("bash", "-c", 
                String.format("mysql -u%s -p%s %s < %s", 
                        datasourceUsername, datasourcePassword, dbName, backupFile.toString()));
        }
        
        // 执行恢复过程
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new Exception("数据库恢复失败，返回代码: " + exitCode);
        }
    }

    @Override
    public boolean verifyAdminPassword(String password) {
        // 这里应该实现对管理员密码的验证
        // 简化实现，你可能需要更复杂的验证逻辑
        return "admin123".equals(password);
    }
    
    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize > 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f%s", fileSize, units[unitIndex]);
    }
    
    /**
     * 从JDBC URL中提取数据库名
     */
    private String extractDatabaseName(String jdbcUrl) {
        // 简单实现，假设URL格式为jdbc:mysql://host:port/database
        int lastSlashIndex = jdbcUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            String dbName = jdbcUrl.substring(lastSlashIndex + 1);
            // 去掉可能的URL参数
            int paramIndex = dbName.indexOf('?');
            if (paramIndex > 0) {
                dbName = dbName.substring(0, paramIndex);
            }
            return dbName;
        }
        return "community_pension"; // 默认数据库名
    }
} 