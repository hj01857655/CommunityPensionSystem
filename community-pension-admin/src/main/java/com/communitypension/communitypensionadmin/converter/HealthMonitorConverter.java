package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.HealthMonitorDTO;
import com.communitypension.communitypensionadmin.entity.HealthMonitor;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.HealthMonitorVO;
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
public class HealthMonitorConverter {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public static HealthMonitor toEntity(HealthMonitorDTO dto) {
        if (dto == null) {
            return null;
        }
        HealthMonitor entity = new HealthMonitor();
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
    public static HealthMonitorVO toVO(HealthMonitor entity, User elder, User processor) {
        if (entity == null) {
            return null;
        }
        HealthMonitorVO vo = new HealthMonitorVO();
        BeanUtils.copyProperties(entity, vo);

        // 设置老人信息
        if (elder != null) {
            vo.setElderName(elder.getName());
            vo.setElderGender(elder.getGender());

            // 计算年龄
            if (elder.getBirthday() != null) {
                LocalDate birthDate = elder.getBirthday();
                LocalDate currentDate = LocalDate.now();
                vo.setElderAge(Period.between(birthDate, currentDate).getYears());
            }
        }

        // 设置处理人信息
        if (processor != null) {
            vo.setProcessedByName(processor.getName());
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
    public static List<HealthMonitorVO> toVOList(List<HealthMonitor> entities, Map<Long, User> elderMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<HealthMonitorVO> voList = new ArrayList<>(entities.size());
        for (HealthMonitor entity : entities) {
            User elder = elderMap.get(entity.getElderId());
            User processor = entity.getProcessedBy() != null ? userMap.get(entity.getProcessedBy()) : null;
            voList.add(toVO(entity, elder, processor));
        }
        return voList;
    }

    /**
     * 实体列表转VO列表（简化版，只需要老人Map）
     *
     * @param entities 实体列表
     * @param elderMap 老人Map
     * @return VO列表
     */
    public static List<HealthMonitorVO> toVOList(List<HealthMonitor> entities, Map<Long, User> elderMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<HealthMonitorVO> voList = new ArrayList<>(entities.size());
        for (HealthMonitor entity : entities) {
            User elder = elderMap.get(entity.getElderId());
            voList.add(toVO(entity, elder, null));
        }
        return voList;
    }
}
