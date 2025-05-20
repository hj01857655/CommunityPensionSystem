package com.communitypension.communitypensionadmin.pojo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 紧急呼叫查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmergencyCallQuery extends PageQuery {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 状态
     */
    private String status;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
