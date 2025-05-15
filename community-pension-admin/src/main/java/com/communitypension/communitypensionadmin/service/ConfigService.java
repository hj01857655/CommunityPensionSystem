package com.communitypension.communitypensionadmin.service;

import com.communitypension.communitypensionadmin.entity.Config;

import java.util.List;

/**
 * 参数配置 服务层
 */
public interface ConfigService {
    /**
     * 查询所有参数配置信息
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    List<Config> selectConfigList(Config config);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    int insertConfig(Config config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    int updateConfig(Config config);

    /**
     * 根据键名更新参数配置
     *
     * @param configKey 参数键名
     * @param configValue 参数键值
     * @return 结果
     */
    int updateConfigByKey(String configKey, String configValue);
    
    /**
     * 批量删除参数配置信息
     * 
     * @param configIds 需要删除的参数ID
     */
    void deleteConfigByIds(Integer[] configIds);

    /**
     * 加载参数缓存数据
     */
    void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数信息
     * @return 结果
     */
    String checkConfigKeyUnique(Config config);
} 