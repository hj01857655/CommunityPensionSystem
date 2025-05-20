package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.HealthAlert;
import com.communitypension.communitypensionadmin.pojo.vo.HealthAlertVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康预警Mapper接口
 */
@Mapper
public interface HealthAlertMapper extends BaseMapper<HealthAlert> {

    /**
     * 分页查询健康预警列表
     *
     * @param page       分页参数
     * @param elderId    老人ID
     * @param elderName  老人姓名
     * @param alertType  预警类型
     * @param alertLevel 预警级别
     * @param status     处理状态
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 健康预警列表
     */
    IPage<HealthAlertVO> selectHealthAlertPage(
            Page<HealthAlertVO> page,
            @Param("elderId") Long elderId,
            @Param("elderName") String elderName,
            @Param("alertType") Integer alertType,
            @Param("alertLevel") Integer alertLevel,
            @Param("status") Integer status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取健康预警详情
     *
     * @param id 预警ID
     * @return 健康预警详情
     */
    HealthAlertVO selectHealthAlertDetail(@Param("id") Long id);

    /**
     * 获取老人未处理的健康预警数量
     *
     * @param elderId 老人ID
     * @return 未处理的健康预警数量
     */
    int countUnhandledAlertsByElderId(@Param("elderId") Long elderId);

    /**
     * 获取老人最近的健康预警列表
     *
     * @param elderId 老人ID
     * @param limit   限制数量
     * @return 最近的健康预警列表
     */
    List<HealthAlertVO> selectRecentAlertsByElderId(@Param("elderId") Long elderId, @Param("limit") int limit);

    /**
     * 获取所有未处理的紧急预警
     *
     * @return 未处理的紧急预警列表
     */
    List<HealthAlertVO> selectUnhandledEmergencyAlerts();
} 