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
        if (entity.getMonitorTime() == null) {
            entity.setMonitorTime(LocalDateTime.now());
        }
        
        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity 实体对象
     * @param elder 老人对象
     * @return VO对象
     */
    public static HealthMonitorVO toVO(HealthMonitor entity, User elder) {
        if (entity == null) {
            return null;
        }
        HealthMonitorVO vo = new HealthMonitorVO();
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
        
        // 设置监测类型名称
        vo.setMonitorTypeName(getMonitorTypeName(entity.getMonitorType()));
        
        // 设置血糖类型名称
        if (entity.getMonitorType() == 2) { // 血糖
            vo.setBloodSugarTypeName(getBloodSugarTypeName(entity.getBloodSugarType()));
        }
        
        // 设置监测结果名称
        vo.setMonitorResultName(getMonitorResultName(entity.getMonitorResult()));
        
        // 设置血压格式化值
        if (entity.getMonitorType() == 1 && entity.getSystolicPressure() != null && entity.getDiastolicPressure() != null) {
            vo.setBloodPressureFormatted(entity.getSystolicPressure() + "/" + entity.getDiastolicPressure() + " mmHg");
        }
        
        return vo;
    }

    /**
     * 实体列表转VO列表
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
            voList.add(toVO(entity, elder));
        }
        return voList;
    }

    /**
     * 获取监测类型名称
     *
     * @param monitorType 监测类型
     * @return 监测类型名称
     */
    private static String getMonitorTypeName(Integer monitorType) {
        if (monitorType == null) {
            return "未知类型";
        }
        
        return switch (monitorType) {
            case 1 -> "血压";
            case 2 -> "血糖";
            case 3 -> "体温";
            case 4 -> "心率";
            case 5 -> "血氧";
            case 6 -> "体重";
            case 7 -> "其他";
            default -> "未知类型";
        };
    }

    /**
     * 获取血糖类型名称
     *
     * @param bloodSugarType 血糖类型
     * @return 血糖类型名称
     */
    private static String getBloodSugarTypeName(Integer bloodSugarType) {
        if (bloodSugarType == null) {
            return "未知类型";
        }
        
        return switch (bloodSugarType) {
            case 1 -> "空腹";
            case 2 -> "餐后";
            case 3 -> "随机";
            default -> "未知类型";
        };
    }

    /**
     * 获取监测结果名称
     *
     * @param monitorResult 监测结果
     * @return 监测结果名称
     */
    private static String getMonitorResultName(Integer monitorResult) {
        if (monitorResult == null) {
            return "未知结果";
        }
        
        return switch (monitorResult) {
            case 1 -> "正常";
            case 2 -> "偏高";
            case 3 -> "偏低";
            case 4 -> "异常";
            default -> "未知结果";
        };
    }
}
