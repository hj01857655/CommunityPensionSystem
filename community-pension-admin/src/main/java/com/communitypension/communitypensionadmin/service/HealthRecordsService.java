package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.HealthRecords;

public interface HealthRecordsService extends IService<HealthRecords> {
    HealthRecords getHealthRecordsWithElderInfoByElderId(Long elderId);
}
