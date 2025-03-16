package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.mapper.ElderMapper;
import com.communitypension.communitypensionadmin.service.ElderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements ElderService {

    @Override
    public HealthRecords getHealthRecords(Long elderId) {
        Elder elder = this.getById(elderId);
        if (elder != null) {
            return elder.getHealthRecords();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHealthRecords(Long elderId, HealthRecords healthRecords) {
        Elder elder = this.getById(elderId);
        if (elder != null) {
            elder.setHealthRecords(healthRecords);
            return this.updateById(elder);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importElders(List<Elder> elders) {
        if (elders == null || elders.isEmpty()) {
            return false;
        }
        return this.saveBatch(elders);
    }
}
