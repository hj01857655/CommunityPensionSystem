package com.communitypension.communitypensionadmin.pojo.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色查询条件
 */
@Data
public class RoleQuery implements Serializable {
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色权限字符
     */
    private String roleKey;
    
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    
    /**
     * 开始创建时间
     */
    private String beginTime;
    
    /**
     * 结束创建时间
     */
    private String endTime;
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
}
