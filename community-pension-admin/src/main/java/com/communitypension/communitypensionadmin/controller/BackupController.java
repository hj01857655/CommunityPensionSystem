package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.service.BackupService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据库备份恢复控制器
 */
@RestController
@RequestMapping("/api/system/backup")
public class BackupController {

    private static final Logger log = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private BackupService backupService;

    /**
     * 获取备份文件列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getBackupList() {
        try {
            return Result.success(backupService.getBackupList());
        } catch (Exception e) {
            log.error("获取备份列表失败", e);
            return Result.error("获取备份列表失败：" + e.getMessage());
        }
    }

    /**
     * 创建新的数据备份
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createBackup() {
        try {
            Map<String, Object> result = backupService.createBackup();
            return Result.success("数据备份成功", result);
        } catch (Exception e) {
            log.error("创建备份失败", e);
            return Result.error("创建备份失败：" + e.getMessage());
        }
    }

    /**
     * 下载备份文件
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadBackup(@RequestParam String fileName) {
        try {
            Resource resource = backupService.loadBackupAsResource(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("下载备份文件失败", e);
            throw new RuntimeException("下载备份文件失败：" + e.getMessage());
        }
    }

    /**
     * 删除备份文件
     */
    @DeleteMapping("/delete")
    public Result<String> deleteBackup(@RequestParam String fileName) {
        try {
            backupService.deleteBackup(fileName);
            return Result.success("备份文件删除成功");
        } catch (Exception e) {
            log.error("删除备份文件失败", e);
            return Result.error("删除备份文件失败：" + e.getMessage());
        }
    }

    /**
     * 恢复数据库备份
     */
    @PostMapping("/restore")
    public Result<String> restoreBackup(@RequestBody Map<String, Object> params) {
        try {
            String backupId = params.get("backupId").toString();
            String password = params.get("password").toString();

            // 验证管理员密码
            if (!backupService.verifyAdminPassword(password)) {
                return Result.error("管理员密码验证失败");
            }

            backupService.restoreBackup(backupId);
            return Result.success("数据恢复成功");
        } catch (Exception e) {
            log.error("恢复数据库失败", e);
            return Result.error("恢复数据库失败：" + e.getMessage());
        }
    }
} 