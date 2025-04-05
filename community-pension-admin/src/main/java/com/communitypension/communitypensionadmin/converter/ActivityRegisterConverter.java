package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.ActivityRegisterDTO;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.ActivityRegisterExportVO;
import com.communitypension.communitypensionadmin.vo.ActivityRegisterVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动报名数据转换器
 */
public class ActivityRegisterConverter {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    public static ActivityRegister toEntity(ActivityRegisterDTO dto) {
        if (dto == null) {
            return null;
        }
        ActivityRegister entity = new ActivityRegister();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    /**
     * 实体转VO
     *
     * @param entity 实体对象
     * @param activity 活动对象
     * @param user 用户对象
     * @return VO对象
     */
    public static ActivityRegisterVO toVO(ActivityRegister entity, Activity activity, User user) {
        if (entity == null) {
            return null;
        }
        ActivityRegisterVO vo = new ActivityRegisterVO();
        BeanUtils.copyProperties(entity, vo);
        
        // 设置活动信息
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }
        
        // 设置用户信息
        if (user != null) {
            vo.setUserName(user.getUsername());
        }
        
        // 设置状态名称
        vo.setStatusName(getStatusName(entity.getStatus()));
        
        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 实体列表
     * @param activityMap 活动Map
     * @param userMap 用户Map
     * @return VO列表
     */
    public static List<ActivityRegisterVO> toVOList(List<ActivityRegister> entities, Map<Long, Activity> activityMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(entity -> {
                    Activity activity = activityMap.get(entity.getActivityId());
                    User user = userMap.get(entity.getUserId());
                    return toVO(entity, activity, user);
                })
                .collect(Collectors.toList());
    }

    /**
     * 实体转导出VO
     *
     * @param entity 实体对象
     * @param activity 活动对象
     * @param user 用户对象
     * @return 导出VO对象
     */
    public static ActivityRegisterExportVO toExportVO(ActivityRegister entity, Activity activity, User user) {
        if (entity == null) {
            return null;
        }
        ActivityRegisterExportVO vo = new ActivityRegisterExportVO();
        vo.setId(entity.getId());
        
        // 设置活动信息
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }
        
        // 设置用户信息
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserPhone(user.getPhone());
        }
        
        vo.setRegisterTime(entity.getRegisterTime());
        vo.setCheckInTime(entity.getCheckInTime());
        vo.setStatusName(getStatusName(entity.getStatus()));
        vo.setRemark(entity.getRemark());
        
        return vo;
    }

    /**
     * 实体列表转导出VO列表
     *
     * @param entities 实体列表
     * @param activityMap 活动Map
     * @param userMap 用户Map
     * @return 导出VO列表
     */
    public static List<ActivityRegisterExportVO> toExportVOList(List<ActivityRegister> entities, Map<Long, Activity> activityMap, Map<Long, User> userMap) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(entity -> {
                    Activity activity = activityMap.get(entity.getActivityId());
                    User user = userMap.get(entity.getUserId());
                    return toExportVO(entity, activity, user);
                })
                .collect(Collectors.toList());
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
            case 3 -> "已取消";
            case 4 -> "已签到";
            default -> "未知状态";
        };
    }
}
