package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.pojo.dto.CreateActivityDTO;
import com.communitypension.communitypensionadmin.pojo.dto.UpdateActivityDTO;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityDetailVO;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityListVO;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动对象转换器
 * <p>
 * 负责实体类、DTO和VO之间的转换
 * </p>
 */
public class ActivityConverter {

    /**
     * 将DTO转换为实体类
     *
     * @param dto 活动DTO
     * @return 活动实体类
     */
    public static Activity toEntity(ActivityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Activity entity = new Activity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    /**
     * 将实体类转换为DTO
     *
     * @param entity 活动实体类
     * @return 活动DTO
     */
    public static ActivityDTO toDTO(Activity entity) {
        if (entity == null) {
            return null;
        }
        
        ActivityDTO dto = new ActivityDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * 将CreateActivityDTO转换为实体类
     *
     * @param dto 创建活动DTO
     * @return 活动实体类
     */
    public static Activity toEntity(CreateActivityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Activity entity = new Activity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
    
    /**
     * 将UpdateActivityDTO转换为实体类
     *
     * @param dto 更新活动DTO
     * @return 活动实体类
     */
    public static Activity toEntity(UpdateActivityDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Activity entity = new Activity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
    
    /**
     * 将实体类转换为ActivityListVO
     *
     * @param entity 活动实体类
     * @return 活动列表VO
     */
    public static ActivityListVO toListVO(Activity entity) {
        if (entity == null) {
            return null;
        }
        
        return ActivityListVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .coverImage(entity.getCoverImage())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    
    /**
     * 将实体类转换为ActivityListVO，并设置额外的显示字段
     *
     * @param entity 活动实体类
     * @param typeName 活动类型名称
     * @param statusName 状态名称
     * @return 活动列表VO
     */
    public static ActivityListVO toListVO(Activity entity, String typeName, String statusName) {
        ActivityListVO vo = toListVO(entity);
        if (vo != null) {
            vo.setTypeName(typeName);
            vo.setStatusName(statusName);
        }
        return vo;
    }
    
    /**
     * 将实体类转换为ActivityDetailVO
     *
     * @param entity 活动实体类
     * @return 活动详情VO
     */
    public static ActivityDetailVO toDetailVO(Activity entity) {
        if (entity == null) {
            return null;
        }
        
        return ActivityDetailVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .description(entity.getDescription())
                .coverImage(entity.getCoverImage())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .location(entity.getLocation())
                .maxParticipants(entity.getMaxParticipants())
                .organizerId(entity.getOrganizerId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * 将实体类转换为ActivityDetailVO，并设置额外的显示字段
     *
     * @param entity 活动实体类
     * @param typeName 活动类型名称
     * @param organizerName 组织者姓名
     * @param statusName 状态名称
     * @param currentParticipants 当前参与人数
     * @return 活动详情VO
     */
    public static ActivityDetailVO toDetailVO(Activity entity, String typeName, String organizerName, 
                                 String statusName, Integer currentParticipants) {
        ActivityDetailVO vo = toDetailVO(entity);
        if (vo != null) {
            vo.setTypeName(typeName);
            vo.setOrganizerName(organizerName);
            vo.setStatusName(statusName);
            vo.setCurrentParticipants(currentParticipants);
        }
        return vo;
    }
    
    /**
     * 将实体类转换为VO
     *
     * @param entity 活动实体类
     * @return 活动VO
     */
    public static ActivityVO toVO(Activity entity) {
        if (entity == null) {
            return null;
        }
        
        return ActivityVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .description(entity.getDescription())
                .coverImage(entity.getCoverImage())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .location(entity.getLocation())
                .maxParticipants(entity.getMaxParticipants())
                .organizerId(entity.getOrganizerId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * 将实体类转换为VO，并设置额外的显示字段
     *
     * @param entity 活动实体类
     * @param typeName 活动类型名称
     * @param organizerName 组织者姓名
     * @param statusName 状态名称
     * @param currentParticipants 当前参与人数
     * @return 活动VO
     */
    public static ActivityVO toVO(Activity entity, String typeName, String organizerName, 
                                 String statusName, Integer currentParticipants) {
        ActivityVO vo = toVO(entity);
        if (vo != null) {
            vo.setTypeName(typeName);
            vo.setOrganizerName(organizerName);
            vo.setStatusName(statusName);
            vo.setCurrentParticipants(currentParticipants);
        }
        return vo;
    }
    
    /**
     * 将活动实体类列表转换为活动列表VO列表
     *
     * @param entities 活动实体类列表
     * @return 活动列表VO列表
     */
    public static List<ActivityListVO> toListVOList(List<Activity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ActivityConverter::toListVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将活动实体类列表转换为活动VO列表
     *
     * @param entities 活动实体类列表
     * @return 活动VO列表
     */
    public static List<ActivityVO> toVOList(List<Activity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ActivityConverter::toVO)
                .collect(Collectors.toList());
    }
}
