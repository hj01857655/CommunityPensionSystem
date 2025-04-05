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
     * 获取老人对特定活动的报名状态
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 报名状态，如果未报名则返回null
     */
    @Select("SELECT status FROM activity_register WHERE activity_id = #{activityId} AND elder_id = #{elderId} LIMIT 1")
    Integer getElderActivityStatus(@Param("activityId") Long activityId, @Param("elderId") Long elderId);

    /**
     * 获取活动当前报名人数
     *
     * @param activityId 活动ID
     * @return 报名人数
     */
    @Select("SELECT COUNT(*) FROM activity_register WHERE activity_id = #{activityId} AND status = 1")
    Integer getCurrentRegisters(@Param("activityId") Long activityId);

    /**
     * 获取老人对特定活动的报名ID
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 报名ID，如果未报名则返回null
     */
    @Select("SELECT id FROM activity_register WHERE activity_id = #{activityId} AND elder_id = #{elderId} LIMIT 1")
    Long getRegisterIdByActivityAndElder(@Param("activityId") Long activityId, @Param("elderId") Long elderId);

    /**
     * 检查老人是否已签到
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 是否已签到（0-未签到，1-已签到）
     */
    @Select("SELECT COUNT(*) FROM activity_register ar JOIN activity_check_in ac ON ar.id = ac.register_id WHERE ar.activity_id = #{activityId} AND ar.elder_id = #{elderId}")
    Integer checkElderCheckedIn(@Param("activityId") Long activityId, @Param("elderId") Long elderId);
}
