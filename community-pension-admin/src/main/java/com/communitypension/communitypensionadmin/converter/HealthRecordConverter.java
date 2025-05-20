package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.pojo.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.entity.HealthRecord;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.pojo.vo.HealthRecordVO;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 健康档案数据转换器
 */
public class HealthRecordConverter {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public static HealthRecord toEntity(HealthRecordDTO dto) {
        if (dto == null) {
            return null;
        }
        HealthRecord entity = new HealthRecord();
        BeanUtils.copyProperties(dto, entity);

        // 如果没有设置记录时间，则使用当前时间
        if (entity.getRecordTime() == null) {
            entity.setRecordTime(LocalDateTime.now());
        }

        // 计算BMI
        if (entity.getHeight() != null && entity.getWeight() != null && entity.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = entity.getHeight().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = entity.getWeight().divide(heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
            entity.setBmi(bmi);
        }

        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity   实体对象
     * @param elder    老人对象
     * @param recorder 记录人对象
     * @return VO对象
     */
    public static HealthRecordVO toVO(HealthRecord entity, User elder, User recorder) {
        if (entity == null) {
            return null;
        }
        HealthRecordVO vo = new HealthRecordVO();
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

        // 设置记录人信息
        if (recorder != null) {
            vo.setRecorderName(recorder.getName());
        }

        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 实体列表
     * @param elderMap 老人Map
     * @param userMap  用户Map
     * @return VO列表
     */
    public static List<HealthRecordVO> toVOList(List<HealthRecord> entities, Map<Long, User> elderMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<HealthRecordVO> voList = new ArrayList<>(entities.size());
        for (HealthRecord entity : entities) {
            User elder = elderMap.get(entity.getElderId());
            User recorder = userMap.get(entity.getRecorderId());
            voList.add(toVO(entity, elder, recorder));
        }
        return voList;
    }
}
