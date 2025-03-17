package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.mapper.HealthRecordsMapper;
import com.communitypension.communitypensionadmin.service.HealthRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public
class HealthRecordsImpl extends ServiceImpl<HealthRecordsMapper, HealthRecords>  implements HealthRecordsService {
    @Autowired
    private HealthRecordsMapper healthRecordsMapper;
    @Override
    public Page<HealthRecords> findAll(Page<HealthRecords> page) {
        return this.page(page);
    }

    @Override
    public HealthRecords getHealthRecordsWithElderInfoByElderId(Long elderId) {
        return healthRecordsMapper.selectHealthRecordsWithElderInfoByElderId(elderId);
    }

    //更新健康档案
    public boolean updateHealthRecordByElderId(HealthRecords healthRecord, Long elderId) {
        if (healthRecord == null || elderId == null) {
            return false;
        }
        int result = healthRecordsMapper.updateHealthRecord(healthRecord, elderId);
        return result > 0;
    }

}
