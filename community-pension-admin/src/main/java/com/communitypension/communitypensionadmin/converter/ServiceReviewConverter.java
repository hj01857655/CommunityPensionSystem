package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.ServiceReviewDTO;
import com.communitypension.communitypensionadmin.entity.ServiceReview;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.ServiceReviewVO;
import org.springframework.beans.BeanUtils;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务评价数据转换器
 */
public class ServiceReviewConverter {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public static ServiceReview toEntity(ServiceReviewDTO dto) {
        if (dto == null) {
            return null;
        }
        ServiceReview entity = new ServiceReview();
        BeanUtils.copyProperties(dto, entity);
        entity.setReviewTime(LocalDateTime.now());
        entity.setStatus(0); // 默认待审核
        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity 实体对象
     * @param serviceName 服务名称
     * @param elder 老人对象
     * @param reviewUser 评价人对象
     * @param replyAdmin 回复管理员对象
     * @return VO对象
     */
    public static ServiceReviewVO toVO(ServiceReview entity, String serviceName, User elder, User reviewUser, User replyAdmin) {
        if (entity == null) {
            return null;
        }
        ServiceReviewVO vo = new ServiceReviewVO();
        BeanUtils.copyProperties(entity, vo);
        
        // 设置服务名称
        vo.setServiceName(serviceName);
        
        // 设置老人姓名
        if (elder != null) {
            vo.setElderName(elder.getName());
        }
        
        // 设置评价人姓名（如果是匿名评价，则不显示评价人姓名）
        if (entity.getIsAnonymous() == 0 && reviewUser != null) {
            vo.setReviewUserName(reviewUser.getName());
        } else {
            vo.setReviewUserName("匿名用户");
        }
        
        // 设置评价类型名称
        vo.setReviewTypeName(getReviewTypeName(entity.getReviewType()));
        
        // 设置回复管理员姓名
        if (replyAdmin != null) {
            vo.setReplyAdminName(replyAdmin.getName());
        }
        
        // 设置状态名称
        vo.setStatusName(
            DictUtils.getDictLabel(
                DictTypeConstants.SERVICE_REVIEW_STATUS,
                entity.getStatus() == null ? null : String.valueOf(entity.getStatus())
            )
        );
        
        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 实体列表
     * @param serviceNameMap 服务名称Map
     * @param userMap 用户Map
     * @return VO列表
     */
    public static List<ServiceReviewVO> toVOList(List<ServiceReview> entities, Map<Long, String> serviceNameMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        List<ServiceReviewVO> voList = new ArrayList<>(entities.size());
        for (ServiceReview entity : entities) {
            String serviceName = serviceNameMap.get(entity.getServiceId());
            User elder = userMap.get(entity.getElderId());
            User reviewUser = userMap.get(entity.getReviewUserId());
            User replyAdmin = userMap.get(entity.getReplyAdminId());
            voList.add(toVO(entity, serviceName, elder, reviewUser, replyAdmin));
        }
        return voList;
    }

    /**
     * 获取评价类型名称
     *
     * @param reviewType 评价类型
     * @return 评价类型名称
     */
    private static String getReviewTypeName(Integer reviewType) {
        if (reviewType == null) {
            return "未知类型";
        }
        
        return switch (reviewType) {
            case 0 -> "老人自己评价";
            case 1 -> "家属代评价";
            default -> "未知类型";
        };
    }

    /**
     * 获取状态名称
     *
     * @param status 状态码
     * @return 状态名称
     */
    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "已通过";
            case 2 -> "已拒绝";
            default -> "未知状态";
        };
    }
}
