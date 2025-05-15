package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.Config;
import com.communitypension.communitypensionadmin.service.ConfigService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置控制器
 * 提供系统设置和安全设置相关的API
 */
@RestController
@RequestMapping("/api/system/config")
public class ConfigController {
    
    @Autowired
    private ConfigService configService;
    
    /**
     * 获取所有配置列表
     */
    @GetMapping("/list")
    public Result<List<Config>> getConfigList() {
        List<Config> configs = configService.selectConfigList(new Config());
        return Result.success(configs);
    }
    
    /**
     * 根据键名获取配置
     */
    @GetMapping("/configKey/{configKey}")
    public Result<String> getConfigValueByKey(@PathVariable String configKey) {
        String configValue = configService.selectConfigByKey(configKey);
        return Result.success(configValue);
    }
    
    /**
     * 获取系统设置
     * 包括系统名称、Logo、描述等
     */
    @GetMapping("/settings")
    public Result<Map<String, Object>> getSystemSettings() {
        // 查询系统相关的配置
        List<Config> configs = configService.selectConfigList(new Config());
        Map<String, String> systemSettings = configs.stream()
                .filter(config -> config.getConfigKey().startsWith("sys."))
                .collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue));
        
        // 转换为前端需要的格式
        Map<String, Object> result = new HashMap<>();
        result.put("systemName", systemSettings.getOrDefault("sys.name", "社区养老系统"));
        result.put("logo", systemSettings.getOrDefault("sys.logo", ""));
        result.put("description", systemSettings.getOrDefault("sys.description", "为社区老人提供全方位的养老服务和健康管理"));
        result.put("icp", systemSettings.getOrDefault("sys.icp", ""));
        result.put("contactPhone", systemSettings.getOrDefault("sys.contact.phone", ""));
        result.put("contactEmail", systemSettings.getOrDefault("sys.contact.email", ""));
        result.put("version", systemSettings.getOrDefault("sys.version", "v1.0.0"));
        
        return Result.success(result);
    }
    
    /**
     * 获取安全设置
     * 包括密码策略、登录限制等
     */
    @GetMapping("/security")
    public Result<Map<String, Object>> getSecuritySettings() {
        // 查询安全相关的配置
        List<Config> configs = configService.selectConfigList(new Config());
        Map<String, String> securitySettings = configs.stream()
                .filter(config -> config.getConfigKey().startsWith("security.") || config.getConfigKey().startsWith("sys.account."))
                .collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue));
        
        // 转换为前端需要的格式
        Map<String, Object> result = new HashMap<>();
        result.put("passwordMinLength", Integer.parseInt(securitySettings.getOrDefault("security.password.minLength", "8")));
        result.put("passwordComplexity", securitySettings.getOrDefault("security.password.complexity", "medium"));
        result.put("loginFailLockCount", Integer.parseInt(securitySettings.getOrDefault("security.login.failLockCount", "5")));
        result.put("accountLockTime", Integer.parseInt(securitySettings.getOrDefault("security.account.lockTime", "30")));
        result.put("sessionTimeout", Integer.parseInt(securitySettings.getOrDefault("security.session.timeout", "30")));
        result.put("enableCaptcha", Boolean.parseBoolean(securitySettings.getOrDefault("sys.account.captchaEnabled", "true")));
        result.put("enableIpRestriction", Boolean.parseBoolean(securitySettings.getOrDefault("security.ip.restriction", "false")));
        result.put("allowedIps", securitySettings.getOrDefault("security.ip.allowedList", ""));
        
        return Result.success(result);
    }
    
    /**
     * 更新系统设置
     */
    @PutMapping("/updateSystemSettings")
    public Result<String> updateSystemSettings(@RequestBody Map<String, Object> systemSettings) {
        try {
            // 更新系统相关配置
            configService.updateConfigByKey("sys.name", String.valueOf(systemSettings.get("systemName")));
            configService.updateConfigByKey("sys.logo", String.valueOf(systemSettings.get("logo")));
            configService.updateConfigByKey("sys.description", String.valueOf(systemSettings.get("description")));
            configService.updateConfigByKey("sys.icp", String.valueOf(systemSettings.get("icp")));
            configService.updateConfigByKey("sys.contact.phone", String.valueOf(systemSettings.get("contactPhone")));
            configService.updateConfigByKey("sys.contact.email", String.valueOf(systemSettings.get("contactEmail")));
            // 版本号不允许通过UI更新
            
            return Result.success("系统设置更新成功");
        } catch (Exception e) {
            return Result.error("系统设置更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新安全设置
     */
    @PutMapping("/updateSecuritySettings")
    public Result<String> updateSecuritySettings(@RequestBody Map<String, Object> securitySettings) {
        try {
            // 更新安全相关配置
            configService.updateConfigByKey("security.password.minLength", String.valueOf(securitySettings.get("passwordMinLength")));
            configService.updateConfigByKey("security.password.complexity", String.valueOf(securitySettings.get("passwordComplexity")));
            configService.updateConfigByKey("security.login.failLockCount", String.valueOf(securitySettings.get("loginFailLockCount")));
            configService.updateConfigByKey("security.account.lockTime", String.valueOf(securitySettings.get("accountLockTime")));
            configService.updateConfigByKey("security.session.timeout", String.valueOf(securitySettings.get("sessionTimeout")));
            configService.updateConfigByKey("sys.account.captchaEnabled", String.valueOf(securitySettings.get("enableCaptcha")));
            configService.updateConfigByKey("security.ip.restriction", String.valueOf(securitySettings.get("enableIpRestriction")));
            
            if (Boolean.TRUE.equals(securitySettings.get("enableIpRestriction"))) {
                configService.updateConfigByKey("security.ip.allowedList", String.valueOf(securitySettings.get("allowedIps")));
            }
            
            return Result.success("安全设置更新成功");
        } catch (Exception e) {
            return Result.error("安全设置更新失败：" + e.getMessage());
        }
    }
} 