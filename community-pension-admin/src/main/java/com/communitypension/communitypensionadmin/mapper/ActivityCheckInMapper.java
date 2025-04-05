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
    @Select("SELECT COUNT(*) FROM activity_check_in ac JOIN activity_register ar ON ac.register_id = ar.id WHERE ar.activity_id = #{activityId}")
    int getCheckInCount(@Param("activityId") Long activityId);

    /**
     * 检查老人是否已签到
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 是否已签到（0-未签到，1-已签到）
     */
    @Select("SELECT COUNT(*) FROM activity_check_in ac JOIN activity_register ar ON ac.register_id = ar.id WHERE ar.activity_id = #{activityId} AND ar.elder_id = #{elderId}")
    Integer checkElderCheckedIn(@Param("activityId") Long activityId, @Param("elderId") Long elderId);

    /**
     * 根据报名ID获取签到记录
     *
     * @param registerId 报名ID
     * @return 签到记录
     */
    @Select("SELECT * FROM activity_check_in WHERE register_id = #{registerId} LIMIT 1")
    ActivityCheckIn getCheckInByRegisterId(@Param("registerId") Long registerId);
}
