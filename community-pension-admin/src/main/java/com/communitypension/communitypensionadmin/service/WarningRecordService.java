package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.WarningRecord;

import java.util.List;
import java.util.Map;

/**
 * 预警记录服务接口
 */
public interface WarningRecordService extends IService<WarningRecord> {
    
    /**
     * 查询总预警数量
     * @return 预警数量
     */
    long count();
    
    /**
     * 获取最新健康预警列表
     * @param limit 返回条数
     * @return 预警列表
     */
    List<Map<String, Object>> getRecentWarnings(int limit);
} 