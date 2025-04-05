package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.HealthMonitoringDTO;
import com.communitypension.communitypensionadmin.entity.HealthMonitoring;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.HealthMonitoringVO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 健康监测数据转换器
 */
public class HealthMonitoringConverter {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public static HealthMonitoring toEntity(HealthMonitoringDTO dto) {
        if (dto == null) {
            return null;
        }
        HealthMonitoring entity = new HealthMonitoring();
        BeanUtils.copyProperties(dto, entity);

        // 如果没有设置监测时间，则使用当前时间
        if (entity.getMonitoringTime() == null) {
            entity.setMonitoringTime(LocalDateTime.now());
        }

        // 如果没有设置监测状态，则默认为normal
        if (entity.getMonitoringStatus() == null || entity.getMonitoringStatus().isEmpty()) {
            entity.setMonitoringStatus("normal");
        }

        // 如果没有设置是否处理，则默认为未处理
        if (entity.getIsProcessed() == null) {
            entity.setIsProcessed(false);
        }

        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity 实体对象
     * @param elder 老人对象
     * @param processor 处理人对象
     * @return VO对象
     */
    public static HealthMonitoringVO toVO(HealthMonitoring entity, User elder, User processor) {
        if (entity == null) {
            return null;
        }
        HealthMonitoringVO vo = new HealthMonitoringVO();
        BeanUtils.copyProperties(entity, vo);

        // 设置老人信息
        if (elder != null) {
            vo.setElderName(elder.getUsername());
            vo.setElderGender(elder.getGender());

            // 计算年龄
            if (elder.getBirthday() != null) {
                LocalDate birthDate = elder.getBirthday().toLocalDate();
                LocalDate currentDate = LocalDate.now();
                vo.setElderAge(Period.between(birthDate, currentDate).getYears());
            }
        }

        // 设置处理人信息
        if (processor != null) {
            vo.setProcessedByName(processor.getUsername());
        }

        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 实体列表
     * @param elderMap 老人Map
     * @param userMap 用户Map
     * @return VO列表
     */
    public static List<HealthMonitoringVO> toVOList(List<HealthMonitoring> entities, Map<Long, User> elderMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<HealthMonitoringVO> voList = new ArrayList<>(entities.size());
        for (HealthMonitoring entity : entities) {
            User elder = elderMap.get(entity.getElderId());
            User processor = entity.getProcessedBy() != null ? userMap.get(entity.getProcessedBy()) : null;
            voList.add(toVO(entity, elder, processor));
        }
        return voList;
    }
}
