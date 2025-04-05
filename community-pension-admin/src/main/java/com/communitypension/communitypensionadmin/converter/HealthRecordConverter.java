package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.HealthRecordDTO;
import com.communitypension.communitypensionadmin.entity.HealthRecord;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.HealthRecordVO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity 实体对象
     * @param elder 老人对象
     * @return VO对象
     */
    public static HealthRecordVO toVO(HealthRecord entity, User elder) {
        if (entity == null) {
            return null;
        }
        HealthRecordVO vo = new HealthRecordVO();
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
        
        // 计算BMI
        if (entity.getHeight() != null && entity.getWeight() != null && entity.getHeight() > 0) {
            double heightInMeters = entity.getHeight() / 100.0;
            double bmi = entity.getWeight() / (heightInMeters * heightInMeters);
            vo.setBmi(Math.round(bmi * 10) / 10.0); // 保留一位小数
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
    public static List<HealthRecordVO> toVOList(List<HealthRecord> entities, java.util.Map<Long, User> elderMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<HealthRecordVO> voList = new ArrayList<>(entities.size());
        for (HealthRecord entity : entities) {
            User elder = elderMap.get(entity.getElderId());
            voList.add(toVO(entity, elder));
        }
        return voList;
    }
}
