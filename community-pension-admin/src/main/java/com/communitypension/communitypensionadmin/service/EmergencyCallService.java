package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.EmergencyCall;
import com.communitypension.communitypensionadmin.query.EmergencyCallQuery;

/**
 * 紧急呼叫服务接口
 */
public interface EmergencyCallService extends IService<EmergencyCall> {
    
    /**
     * 获取紧急呼叫列表
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<EmergencyCall> getEmergencyCallList(EmergencyCallQuery query);
    
    /**
     * 获取紧急呼叫详情
     * @param id 紧急呼叫ID
     * @return 紧急呼叫详情
     */
    EmergencyCall getEmergencyCallById(Long id);
    
    /**
     * 保存紧急呼叫
     * @param emergencyCall 紧急呼叫信息
     * @return 保存结果
     */
    boolean saveEmergencyCall(EmergencyCall emergencyCall);
    
    /**
     * 更新紧急呼叫
     * @param emergencyCall 紧急呼叫信息
     * @return 更新结果
     */
    boolean updateEmergencyCall(EmergencyCall emergencyCall);
    
    /**
     * 取消紧急呼叫
     * @param id 紧急呼叫ID
     * @return 取消结果
     */
    boolean cancelEmergencyCall(Long id);
    
    /**
     * 处理紧急呼叫
     * @param id 紧急呼叫ID
     * @param processedBy 处理人
     * @return 处理结果
     */
    boolean processEmergencyCall(Long id, String processedBy);
    
    /**
     * 完成紧急呼叫
     * @param id 紧急呼叫ID
     * @return 完成结果
     */
    boolean completeEmergencyCall(Long id);
    
    /**
     * 获取用户的紧急呼叫历史
     * @param userId 用户ID
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<EmergencyCall> getUserEmergencyCallHistory(Long userId, EmergencyCallQuery query);
}
