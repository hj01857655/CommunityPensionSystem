package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.ActivityCheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动签到Mapper接口
 */
@Mapper
public interface ActivityCheckInMapper extends BaseMapper<ActivityCheckIn> {
    
    /**
     * 获取活动签到统计
     * 
     * @param activityId 活动ID
     * @return 签到人数
     */
    @Select("SELECT COUNT(*) FROM activity_check_in WHERE activity_id = #{activityId} AND status = 1")
    int getCheckInCount(@Param("activityId") Long activityId);
    
    /**
     * 获取用户签到状态
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 签到状态
     */
    @Select("SELECT status FROM activity_check_in WHERE activity_id = #{activityId} AND user_id = #{userId} LIMIT 1")
    Integer getUserCheckInStatus(@Param("activityId") Long activityId, @Param("userId") Long userId);
    
    /**
     * 根据报名ID获取签到记录
     * 
     * @param participateId 报名ID
     * @return 签到记录
     */
    @Select("SELECT * FROM activity_check_in WHERE participate_id = #{participateId} LIMIT 1")
    ActivityCheckIn getCheckInByParticipateId(@Param("participateId") Long participateId);
}
