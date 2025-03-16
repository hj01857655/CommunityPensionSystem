package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.HealthRecords;

public interface HealthRecordsService extends IService<HealthRecords> {
    HealthRecords getHealthRecordsWithElderInfoByElderId(Long elderId);
    
    /**
     * 根据老人ID更新健康档案
     * @param healthRecord 健康档案信息
     * @param elderId 老人ID
     * @return 更新是否成功
     */
    boolean updateHealthRecordByElderId(HealthRecords healthRecord, Long elderId);
}
