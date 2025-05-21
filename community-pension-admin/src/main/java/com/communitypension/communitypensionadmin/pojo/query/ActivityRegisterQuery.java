package com.communitypension.communitypensionadmin.pojo.query;

import lombok.Data;

/**
 * 活动报名查询参数
 */
@Data
public class ActivityRegisterQuery {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝，3-已取消，4-已签到
     */
    private Integer status;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
