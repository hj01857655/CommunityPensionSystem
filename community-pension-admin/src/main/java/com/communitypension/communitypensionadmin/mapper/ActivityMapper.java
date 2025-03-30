package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动Mapper接口
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    
    /**
     * 获取活动当前参与人数
     *
     * @param activityId 活动ID
     * @return 参与人数
     */
    @Select("SELECT COUNT(*) FROM activityparticipate WHERE activity_id = #{activityId} AND status = 1")
    Integer getCurrentParticipants(@Param("activityId") Long activityId);
} 