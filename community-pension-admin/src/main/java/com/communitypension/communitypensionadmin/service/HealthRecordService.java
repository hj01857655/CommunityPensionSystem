package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.entity.HealthRecord;
import com.communitypension.communitypensionadmin.vo.HealthRecordVO;

/**
 * 健康档案Service接口
 */
public interface HealthRecordService extends IService<HealthRecord> {

    /**
     * 添加健康档案
     *
     * @param recordDTO 健康档案DTO
     * @return 档案ID
     */
    Long addHealthRecord(HealthRecordDTO recordDTO);

    /**
     * 更新健康档案
     *
     * @param recordDTO 健康档案DTO
     * @return 是否成功
     */
    boolean updateHealthRecord(HealthRecordDTO recordDTO);

    /**
     * 删除健康档案
     *
     * @param id 档案ID
     * @return 是否成功
     */
    boolean deleteHealthRecord(Long id);

    /**
     * 获取健康档案详情
     *
     * @param id 档案ID
     * @return 档案详情
     */
    HealthRecordVO getHealthRecordDetail(Long id);

    /**
     * 获取老人的健康档案
     *
     * @param elderId 老人ID
     * @return 档案详情
     */
    HealthRecordVO getElderHealthRecord(Long elderId);

    /**
     * 分页查询健康档案列表
     *
     * @param elderName 老人姓名（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<HealthRecordVO> getHealthRecordList(String elderName, Integer pageNum, Integer pageSize);
}
