package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.pojo.dto.HealthAlertDTO;
import com.communitypension.communitypensionadmin.entity.HealthAlert;
import com.communitypension.communitypensionadmin.pojo.vo.HealthAlertVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康预警服务接口
 */
public interface HealthAlertService extends IService<HealthAlert> {

    /**
     * a增加健康预警
     *
     * @param alertDTO 健康预警数据
     * @return 健康预警ID
     */
    Long addHealthAlert(HealthAlertDTO alertDTO);

    /**
     * 更新健康预警
     *
     * @param alertDTO 健康预警数据
     * @return 是否成功
     */
    boolean updateHealthAlert(HealthAlertDTO alertDTO);

    /**
     * 删除健康预警
     *
     * @param id 健康预警ID
     * @return 是否成功
     */
    boolean deleteHealthAlert(Long id);

    /**
     * 处理健康预警
     *
     * @param id        健康预警ID
     * @param handlerId 处理人ID
     * @param handleNote 处理备注
     * @param status    状态 1:已处理 2:已忽略
     * @return 是否成功
     */
    boolean handleHealthAlert(Long id, Long handlerId, String handleNote, Integer status);

    /**
     * 获取健康预警详情
     *
     * @param id 健康预警ID
     * @return 健康预警详情
     */
    HealthAlertVO getHealthAlertDetail(Long id);

    /**
     * 分页查询健康预警列表
     *
     * @param elderId    老人ID
     * @param elderName  老人姓名
     * @param alertType  预警类型
     * @param alertLevel 预警级别
     * @param status     处理状态
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页记录数
     * @return 健康预警列表
     */
    Page<HealthAlertVO> getHealthAlertList(
            Long elderId,
            String elderName,
            Integer alertType,
            Integer alertLevel,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer pageNum,
            Integer pageSize);

    /**
     * 获取老人未处理的健康预警数量
     *
     * @param elderId 老人ID
     * @return 未处理的健康预警数量
     */
    int getUnhandledAlertsCount(Long elderId);

    /**
     * 获取老人最近的健康预警列表
     *
     * @param elderId 老人ID
     * @param limit   限制数量
     * @return 最近的健康预警列表
     */
    List<HealthAlertVO> getRecentAlerts(Long elderId, int limit);

    /**
     * 获取所有未处理的紧急预警
     *
     * @return 未处理的紧急预警列表
     */
    List<HealthAlertVO> getUnhandledEmergencyAlerts();

    /**
     * 基于健康监测数据生成预警
     *
     * @param monitorId 健康监测ID
     * @return 生成的预警ID，如果没有生成则返回null
     */
    Long generateAlertFromMonitor(Long monitorId);

    /**
     * 批量添加健康预警
     *
     * @param alertDTOs 健康预警数据集合
     * @return 是否成功
     */
    boolean batchAddHealthAlerts(List<HealthAlertDTO> alertDTOs);

    /**
     * 发送健康预警通知
     *
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean sendAlertNotification(Long alertId);
} 