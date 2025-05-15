package com.communitypension.communitypensionadmin.service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据库备份恢复服务
 */
public interface BackupService {
    
    /**
     * 获取备份文件列表
     * 
     * @return 备份文件列表
     * @throws IOException IO异常
     */
    List<Map<String, Object>> getBackupList() throws IOException;
    
    /**
     * 创建数据库备份
     * 
     * @return 备份信息
     * @throws Exception 备份异常
     */
    Map<String, Object> createBackup() throws Exception;
    
    /**
     * 删除备份文件
     * 
     * @param fileName 文件名
     * @throws IOException IO异常
     */
    void deleteBackup(String fileName) throws IOException;
    
    /**
     * 将备份加载为资源
     * 
     * @param fileName 文件名
     * @return 资源对象
     * @throws IOException IO异常
     */
    Resource loadBackupAsResource(String fileName) throws IOException;
    
    /**
     * 恢复数据库
     * 
     * @param backupId 备份ID
     * @throws Exception 恢复异常
     */
    void restoreBackup(String backupId) throws Exception;
    
    /**
     * 验证管理员密码
     * 
     * @param password 密码
     * @return 验证结果
     */
    boolean verifyAdminPassword(String password);
} 