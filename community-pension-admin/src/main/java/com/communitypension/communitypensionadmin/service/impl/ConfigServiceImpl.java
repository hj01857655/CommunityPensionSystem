package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Config;
import com.communitypension.communitypensionadmin.mapper.ConfigMapper;
import com.communitypension.communitypensionadmin.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数配置 服务层实现
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {
    
    /**
     * 缓存容器
     */
    private static final Map<String, String> CONFIG_CACHE = new ConcurrentHashMap<>();

    /**
     * 查询所有参数配置信息
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<Config> selectConfigList(Config config) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        // 根据需要添加查询条件
        if (config != null) {
            if (config.getConfigName() != null) {
                queryWrapper.like(Config::getConfigName, config.getConfigName());
            }
            if (config.getConfigKey() != null) {
                queryWrapper.like(Config::getConfigKey, config.getConfigKey());
            }
            if (config.getConfigType() != null) {
                queryWrapper.eq(Config::getConfigType, config.getConfigType());
            }
        }
        return list(queryWrapper);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = CONFIG_CACHE.get(configKey);
        if (configValue != null) {
            return configValue;
        }
        
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, configKey);
        Config config = getOne(queryWrapper);
        
        if (config != null) {
            CONFIG_CACHE.put(configKey, config.getConfigValue());
            return config.getConfigValue();
        }
        
        return null;
    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(Config config) {
        boolean result = save(config);
        if (result) {
            CONFIG_CACHE.put(config.getConfigKey(), config.getConfigValue());
            return 1;
        }
        return 0;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(Config config) {
        boolean result = updateById(config);
        if (result) {
            CONFIG_CACHE.put(config.getConfigKey(), config.getConfigValue());
            return 1;
        }
        return 0;
    }

    /**
     * 根据键名更新参数配置
     *
     * @param configKey 参数键名
     * @param configValue 参数键值
     * @return 结果
     */
    @Override
    public int updateConfigByKey(String configKey, String configValue) {
        int row = getBaseMapper().updateConfigByKey(configKey, configValue);
        if (row > 0) {
            CONFIG_CACHE.put(configKey, configValue);
        }
        return row;
    }

    /**
     * 批量删除参数配置
     * 
     * @param configIds 需要删除的参数ID
     */
    @Override
    @Transactional
    public void deleteConfigByIds(Integer[] configIds) {
        for (Integer configId : configIds) {
            Config config = getById(configId);
            if (config != null && "Y".equals(config.getConfigType())) {
                continue;
            }
            removeById(configId);
            CONFIG_CACHE.remove(config.getConfigKey());
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<Config> configsList = list();
        for (Config config : configsList) {
            CONFIG_CACHE.put(config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        CONFIG_CACHE.clear();
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(Config config) {
        Integer configId = (config.getConfigId() == null) ? -1 : config.getConfigId();
        
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getConfigKey, config.getConfigKey());
        Config info = getOne(queryWrapper);
        
        if (info != null && !info.getConfigId().equals(configId)) {
            return "1"; // 不唯一
        }
        return "0"; // 唯一
    }
} 