package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.entity.HealthRecords;

import java.util.List;

public interface ElderService extends IService<Elder> {
    /**
     * 获取老人健康档案
     * @param elderId 老人ID
     * @return 健康档案信息
     */
    HealthRecords getHealthRecords(Long elderId);

    /**
     * 更新老人健康档案
     * @param elderId 老人ID
     * @param healthRecords 健康档案信息
     * @return 是否更新成功
     */
    boolean updateHealthRecords(Long elderId, HealthRecords healthRecords);

    /**
     * 批量导入老人信息
     * @param elders 老人信息列表
     * @return 是否导入成功
     */
    boolean importElders(List<Elder> elders);
}
