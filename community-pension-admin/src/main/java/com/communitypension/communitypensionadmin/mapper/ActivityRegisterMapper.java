package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动报名Mapper接口
 */
@Mapper
public interface ActivityRegisterMapper extends BaseMapper<ActivityRegister> {
    
    /**
     * 获取用户对特定活动的报名状态
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名状态，如果未报名则返回null
     */
    @Select("SELECT status FROM activity_register WHERE activity_id = #{activityId} AND user_id = #{userId} LIMIT 1")
    Integer getUserActivityStatus(@Param("activityId") Long activityId, @Param("userId") Long userId);
    
    /**
     * 获取活动当前报名人数
     *
     * @param activityId 活动ID
     * @return 报名人数
     */
    @Select("SELECT COUNT(*) FROM activity_register WHERE activity_id = #{activityId} AND status = 1")
    Integer getCurrentRegisters(@Param("activityId") Long activityId);
}
