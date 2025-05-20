package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.pojo.dto.HealthMonitorDTO;
import com.communitypension.communitypensionadmin.entity.HealthMonitor;
import com.communitypension.communitypensionadmin.pojo.vo.HealthMonitorVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 健康监测Service接口
 */
public interface HealthMonitorService extends IService<HealthMonitor> {

    /**
     * 添加健康监测记录
     *
     * @param monitorDTO 健康监测DTO
     * @return 监测ID
     */
    Long addHealthMonitor(HealthMonitorDTO monitorDTO);

    /**
     * 更新健康监测记录
     *
     * @param monitorDTO 健康监测DTO
     * @return 是否成功
     */
    boolean updateHealthMonitor(HealthMonitorDTO monitorDTO);

    /**
     * 删除健康监测记录
     *
     * @param id 监测ID
     * @return 是否成功
     */
    boolean deleteHealthMonitor(Long id);

    /**
     * 获取健康监测详情
     *
     * @param id 监测ID
     * @return 监测详情
     */
    HealthMonitorVO getHealthMonitorDetail(Long id);

    /**
     * 分页查询健康监测列表
     *
     * @param elderId 老人ID（可选）
     * @param elderName 老人姓名（可选）
     * @param monitorType 监测类型（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<HealthMonitorVO> getHealthMonitorList(Long elderId, String elderName, Integer monitorType, 
                                              LocalDate startDate, LocalDate endDate, 
                                              Integer pageNum, Integer pageSize);

    /**
     * 获取老人的最新健康监测记录
     *
     * @param elderId 老人ID
     * @return 监测记录列表（按类型分组）
     */
    List<HealthMonitorVO> getElderLatestMonitors(Long elderId);

    /**
     * 获取老人的健康监测统计
     *
     * @param elderId 老人ID
     * @param monitorType 监测类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    Map<String, Object> getElderMonitorStats(Long elderId, Integer monitorType, LocalDate startDate, LocalDate endDate);

    /**
     * 批量添加健康监测记录
     *
     * @param monitorDTOs 健康监测DTO列表
     * @return 是否成功
     */
    boolean batchAddHealthMonitors(List<HealthMonitorDTO> monitorDTOs);
}
